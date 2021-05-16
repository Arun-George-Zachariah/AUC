# Imports
import sys
import csv
import matplotlib.pyplot as plt


# Defining the usage function
def usage(prog_name):
    print("python {} <FILE_NAME>".format(prog_name))
    print("")
    print(" FILE_NAME - Input file used with auc.jar")
    print("")


def main():
	# Validating the input
	if (len(sys.argv) < 2):
		usage(sys.argv[0])
		sys.exit(-1)
     
    # Obtaining the inputs   
	input_roc = sys.argv[1] + ".roc"
	input_pr = sys.argv[1] + ".pr"


	# Reading the files
	file_roc = csv.reader(open(input_roc), delimiter="\t")
	file_pr = csv.reader(open(input_pr), delimiter="\t")
	
	# Initializing x and y values.
	x_roc = []
	y_roc =[]
	x_pr = []
	y_pr = []
	
	# Iterating over the entries in the files.
	for row_roc, row_pr in zip(file_roc, file_pr):
	    x_roc.append(float(row_roc[0]))
	    y_roc.append(float(row_roc[1]))
	    x_pr.append(float(row_pr[0]))
	    y_pr.append(float(row_pr[1]))
	
	# Plot ROC
	plt.plot(x_roc, y_roc)
	plt.ylabel('True Positive Rate')
	plt.xlabel('False Positive Rate')
	plt.savefig('ROC.png')
	plt.clf()
	
	# Plot PR
	plt.plot(x_pr, y_pr)
	plt.ylabel('Precision')
	plt.xlabel('Recall')
	plt.savefig('PR.png')


if __name__ == "__main__":
    main()