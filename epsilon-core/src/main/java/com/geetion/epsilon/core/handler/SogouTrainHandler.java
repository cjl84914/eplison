package com.geetion.epsilon.core.handler;

import com.geetion.epsilon.core.entity.EpsilonModel;
import com.geetion.epsilon.core.service.impl.ModelServiceImpl;
import com.geetion.epsilon.core.service.impl.NlpTopicServiceImpl;
import com.geetion.epsilon.core.utils.NPLUtil;
import com.geetion.epsilon.core.utils.SerializeUtil;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import org.ansj.domain.Result;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.NlpAnalysis;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.ml.Model;
import org.apache.spark.ml.attribute.Attribute;
import org.apache.spark.ml.feature.StringIndexer;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by alexcai on 2018/5/11.
 */
@JobHandler(value = "trainHandler")
@Component
public class SogouTrainHandler extends IJobHandler {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private SparkSession spark;

    @Autowired
    private NlpTopicServiceImpl nlpTopicService;

    @Autowired
    private ModelServiceImpl modelService;

    @Override
    public ReturnT<String> execute(String param) {
        logger.info("----------TRAIN");
        JavaRDD<Row> rawRDD = spark.read().textFile("/Users/alexcai/workspace/git/text-classification-cnn-rnn/data/cnews/cnews.train.txt").javaRDD().map(row -> {
            String[] s = row.split("\\t");
            String category = s[0];
            String text = s[1];
            Result result = NlpAnalysis.parse(text).recognition(NPLUtil.stopWords());
            List terms = result.getTerms();
            String[] words = new String[terms.size()];
            for (int i = 0; i < terms.size(); i++) {
                Term term = (Term) terms.get(i);
                words[i] = term.getName();
            }
            return RowFactory.create(category, words);
        });
        List<StructField> fields = new ArrayList<>();
        fields.add(DataTypes.createStructField("category", DataTypes.StringType, true));
        fields.add(DataTypes.createStructField("content", DataTypes.createArrayType(DataTypes.StringType), true));
        StructType schema = DataTypes.createStructType(fields);
        //中文分词
        Dataset<Row> rawDF = spark.createDataFrame(rawRDD, schema);
        StringIndexer indexer = new StringIndexer()
                .setInputCol("category")
                .setOutputCol("label");
        Dataset<Row> df = indexer.fit(rawDF).transform(rawDF);
        logger.info("-----------category:" + Attribute.fromStructField(df.schema().apply(indexer.getOutputCol())).toString());
        Model model = nlpTopicService.train(df);
        modelService.saveByRedis(new EpsilonModel("nlp_model", SerializeUtil.serialize(model)));
//        logger.warn("-----------accuracy:" + nlpTopicService.test(model, df));
        spark.close();
        return SUCCESS;
    }
}
