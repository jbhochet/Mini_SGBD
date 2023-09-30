DB_PATH=$(realpath $1)

# The script must work every time
cd $(dirname ${BASH_SOURCE[0]})

# Configuration
JAVA_VERSION=17
SRC_DIR=../src
BIN_DIR=../bin
LIB_DIR=../lib
MAIN_CLASS=main.Main
SOURCES_FILES=$(find $SRC_DIR -name "*.java")

# Clear bin directory
rm -rf $BIN_DIR

# Compile
javac \
    --release $JAVA_VERSION \
    --source-path $SRC_DIR \
    -cp ${LIB_DIR}/*.jar \
    -d $BIN_DIR \
    $SOURCES_FILES

# Run tests
java -jar $LIB_DIR/junit-platform-console-standalone-1.9.3.jar -cp $BIN_DIR --scan-classpath

if [ $? != 0 ]; then
    echo "Unit test failure!"
    exit 1
fi

# Run
java -cp $BIN_DIR $MAIN_CLASS $DB_PATH