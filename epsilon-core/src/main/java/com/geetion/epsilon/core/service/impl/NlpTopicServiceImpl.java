package com.geetion.epsilon.core.service.impl;

import com.geetion.epsilon.core.entity.Article;
import com.geetion.epsilon.core.entity.JDBCTable;
import com.geetion.epsilon.core.service.NlpTopicService;
import com.geetion.epsilon.core.utils.NPLUtil;
import org.ansj.domain.Result;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.NlpAnalysis;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.ml.Model;
import org.apache.spark.ml.Pipeline;
import org.apache.spark.ml.PipelineModel;
import org.apache.spark.ml.PipelineStage;
import org.apache.spark.ml.classification.LogisticRegression;
import org.apache.spark.ml.feature.HashingTF;
import org.apache.spark.ml.feature.IDF;
import org.apache.spark.mllib.evaluation.MulticlassMetrics;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import scala.Tuple2;

import java.io.IOException;
import java.util.*;

@Component
public class NlpTopicServiceImpl implements NlpTopicService {
    @Autowired
    private SparkSession spark;
    public static final int NUM_FEATURES = 500;
    public static final String INPUT_COL = "content";

    @Override
    public PipelineModel train(Dataset<Row> trainDF) {
        HashingTF hashingTF = new HashingTF()
                .setNumFeatures(NUM_FEATURES)
                .setInputCol(INPUT_COL)
                .setOutputCol("rawFeatures");
        //计算TF-IDF值
        IDF idf = new IDF()
                .setInputCol(hashingTF.getOutputCol())
                .setOutputCol("features");
        //逻辑回归算法
        LogisticRegression lr = new LogisticRegression();
        PipelineStage[] pipelineStage = new PipelineStage[]{hashingTF, idf, lr};
        Pipeline pipeline = new Pipeline()
                .setStages(pipelineStage);
        return pipeline.fit(trainDF);
    }

    @Override
    public double test(Model model, Dataset<Row> testDF) {
        Dataset<Row> predictions = model.transform(testDF);
        JavaPairRDD<Object, Object> result = predictions.select("label", "prediction").javaRDD().mapToPair(v1 -> {
            Double label = v1.getAs("label");
            Double prediction = v1.getAs("prediction");
            return new Tuple2<>(Double.valueOf(label), prediction);
        });
        MulticlassMetrics metrics = new MulticlassMetrics(result.rdd());
        return metrics.accuracy();
    }

    @Override
    public Object predictByTable(Model model, JDBCTable jdbcTable) {
        Properties properties = new Properties();
        properties.put("user", jdbcTable.getUsername());
        properties.put("password", jdbcTable.getPassword());
        properties.put("driver", jdbcTable.getDriver());
        Dataset<Row> sqlDF = spark.read()
                .jdbc(jdbcTable.getUrl(), jdbcTable.getSql(), properties);
        JavaRDD<Article> rawRDD = sqlDF.select("id", jdbcTable.getColumnName()).javaRDD().map(row -> {
            Long id = row.getLong(0);
            String text = row.getString(1);
            Result result = NlpAnalysis.parse(text).recognition(NPLUtil.stopWords());
            List terms = result.getTerms();
            String[] words = new String[terms.size()];
            for (int i = 0; i < terms.size(); i++) {
                Term term = (Term) terms.get(i);
                words[i] = term.getName();
            }
            Article article = new Article();
            article.setId(id);
            article.setContent(words);
            return article;
        });
        Dataset<Row> df = spark.createDataFrame(rawRDD, Article.class);
        df.show();
        Dataset<Row> predictions = model.transform(df);
        JavaRDD<Article> resultRDD = predictions.select("id", "prediction").javaRDD().map(col -> {
            long label = col.getAs("id");
            Double prediction = col.getAs("prediction");
            Article article = new Article();
            article.setId(label);
            article.setLabel(prediction);
            return article;
        });
        return resultRDD.collect();
    }

    @Override
    public Object predictOne(Model model, String content) {
        List<Article> list = new ArrayList<>();
        Result result = NlpAnalysis.parse(content).recognition(NPLUtil.stopWords());
        List terms = result.getTerms();
        String[] words = new String[terms.size()];
        for (int i = 0; i < terms.size(); i++) {
            Term term = (Term) terms.get(i);
            words[i] = term.getName();
        }
        Article a = new Article();
        a.setId(-1);
        a.setContent(words);
        list.add(a);
        Dataset<Row> df = spark.createDataFrame(list, Article.class);
        df.show();
        Dataset<Row> predictions = model.transform(df);
        predictions.show();
        JavaRDD<Article> resultRDD = predictions.select("id", "prediction").javaRDD().map(col -> {
            long label = col.getAs("id");
            Double prediction = col.getAs("prediction");
            Article article = new Article();
            article.setId(label);
            article.setLabel(prediction);
            return article;
        });
        return resultRDD.collect();
    }
}