# The script must work every time
cd $(dirname ${BASH_SOURCE[0]})

# Configuration
JAVA_VERSION=17
SRC_DIR=../src
BIN_DIR=../bin
MAIN_CLASS=Main

# Clear bin directory
rm -rf $BIN_DIR

# Compile
javac \
    --release $JAVA_VERSION \
    --source-path $SRC_DIR \
    -d $BIN_DIR \
    ${SRC_DIR}/${MAIN_CLASS}.java

# Run
java -cp $BIN_DIR $MAIN_CLASS