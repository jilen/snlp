package edu.fudan.example.ml;


import java.io.File;

import edu.fudan.ml.classifier.hier.Linear;
import edu.fudan.ml.classifier.hier.PATrainer;
import edu.fudan.ml.classifier.hier.inf.MultiLinearMax;
import edu.fudan.ml.classifier.linear.inf.Inferencer;
import edu.fudan.ml.feature.BaseGenerator;
import edu.fudan.ml.loss.ZeroOneLoss;
import edu.fudan.ml.types.Instance;
import edu.fudan.ml.types.InstanceSet;
import edu.fudan.ml.types.alphabet.AlphabetFactory;
import edu.fudan.ml.types.alphabet.LabelAlphabet;
import edu.fudan.ml.types.sv.HashSparseVector;
import edu.fudan.ml.types.sv.ISparseVector;
/**
 * 层次分类器使用示例
 * 
 * @author xpqiu
 * 
 */
public class HierClassifierUsage1 {
	static InstanceSet trainset;
	static InstanceSet test;
	static AlphabetFactory factory = AlphabetFactory.buildFactory();
	//将类别标签转换为数字
	static LabelAlphabet lf = factory.DefaultLabelAlphabet();
	static String path = null;

	public static void main(String[] args) throws Exception {

		
		long start = System.currentTimeMillis();
		
		float data[][] = {{1,1,-1},{0,0,0},{1,1,1},{1,1,1},{1,-1,1}};
		String target[] = {"Y","Y","N","N","N"};
		
		
		//构建训练集
		trainset = new InstanceSet(factory);
		
		for(int i=0;i<data.length;i++){
			ISparseVector sv = new HashSparseVector(data[i],true);
			int l = lf.lookupIndex(target[i]);
			Instance inst = new Instance(sv,l);
			trainset.add(inst);
		}
		lf.setStopIncrement(true);
		
		//构建测试集

		System.out.println("Train Number: " + trainset.size());
		System.out.println("Class Number: " + lf.size());

		float c = 1.0f;
		int round = 10;
		
		BaseGenerator featureGen = new BaseGenerator();
		ZeroOneLoss loss = new ZeroOneLoss();
		Inferencer msolver = new MultiLinearMax(featureGen, lf, null,2);

		PATrainer trainer = new PATrainer(msolver, featureGen, loss, round,c, null);
		Linear pclassifier = trainer.train(trainset, null);
		String modelFile = "./tmp/m.gz";
		pclassifier.saveTo(modelFile);
		pclassifier = null;
		
		System.out.println("分类器测试");
		pclassifier = Linear.loadFrom(modelFile);
		float[] tdata = new float[]{1,0,1};
		ISparseVector sv = new HashSparseVector(tdata,true);
		Instance inst = new Instance(sv);
		String lab = pclassifier.getStringLabel(inst);
		System.out.println("分类结果：\t"+lab);
		

		long end = System.currentTimeMillis();
		System.out.println("Total Time: " + (end - start));
		System.out.println("End!");
		(new File(modelFile)).deleteOnExit();
		System.exit(0);
	}
}
