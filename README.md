# BigData_CaseStudyIV_SparkStreaming
Demo of Spark application that reads the data from local file system &amp; does some transformation &amp; action on the fly. As &amp; when files keep coming within the directory the application fetches the file automatically &amp; performs successive transformation &amp; action there by providing the desired output simultaneously. Application also moves the files onto HDFS perform some transformations &amp; actions on HDFS as well &amp; then compares both the results to produce a final message of success/failure in case the output file matches/does_not_match.