# AUC
Area Under the Receiver Operating Characteristic (ROC) Curve (AUC) is one of the most important metric for evaluating a classification model, providing an aggregate measure of performance across all possible classification threshold.

## Environment Setup
Requirements:
   * [Java](https://www.java.com/en/download/) (version = 1.8)
   * [Python](https://www.python.org/) (version = 3.6)
   * [Ant](https://ant.apache.org)
   * [Matplotlib](https://matplotlib.org)

## Build
`ant build`

## Usage
`java -jar auc.jar <FILE_NAME> <FILE_TYPE> <POS_COUNT> <NEG_COUNT> <MIN_RECALL>`

* FILE_NAME: Input data.
* FILE_TYPE: 
    - roc: `false_positive_rate true_positive_rate`
    - pr: ```recall precision```
* POS_COUNT: Count of the positive examples.
* NEG_COUNT: Count of the negative examples.
* MIN_RECALL: Minimum value to start calculating the area under the PR curve.

When FILE_TYPE is list i.e ```prob(example == true)  true_classification(1 == positive, 0 = negative)```                        
```java -jar auc.jar <FILE_NAME> list <MIN_RECALL>```

## Plot ROC and PR Curves
`cd scripts && python plot.py <FILE_NAME>`


## References
* [Original AUCCalculator Jar](http://mark.goadrich.com/programs/AUC/)
* [Machine Learning Crash Course](https://developers.google.com/machine-learning/crash-course/classification/roc-and-auc)

## To Cite
Davis, Jesse, and Mark Goadrich. "The relationship between Precision-Recall and ROC curves." In Proceedings of the 23rd international conference on Machine learning, pp. 233-240. 2006. [[PDF](http://mark.goadrich.com/articles/davisgoadrichcamera2.pdf)]
