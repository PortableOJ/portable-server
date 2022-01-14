//
// Created by 胡柯青 on 2022-01-11.
//

#ifndef PORTABLE_TEST_LIB_H
#define PORTABLE_TEST_LIB_H

#include <cmath>
#include <cstdio>
#include <cstdlib>
#include <cstring>

#include <map>
#include <set>
#include <queue>
#include <string>
#include <vector>
#include <sstream>
#include <filesystem>
#include <functional>
#include <unordered_map>

#include <fcntl.h>
#include <unistd.h>

using namespace std;

/// region 可以供 check 使用的函数

int curTestId = 0;

enum class JudgeResult : int {
    AC = 0,
    WA = 1,
    FAIL = 2
};

class InStream {
private:

    int sid, cur, len;

    char buffer[1024];

protected:

    JudgeResult waResult;

private:

    int readBuffer();

    char next(const string &desc, bool disableEof = false);

    char peek();

    char peekNext();

public:

    /**
     * 构造函数
     * @param sid 读取的文件流地址
     */
    explicit InStream(JudgeResult wa);

    /**
     * 设置 id
     * @param id id 的值
     */
    void setSid(int id);

    /**
     * 获取一个字节
     * @param desc 获取的字节的含义
     * @return 被获取的字节
     */
    char getByte(const string &desc);

    /**
     * 获取一个空格，强调下一个必须是空格，若获取到的不是空格则会结束判题
     *
     * <p color="red">
     *   不推荐使用
     * </p>
     *
     * 推荐使用 StreamReader#readDelimiter 来实现跳过分隔符
     *
     * @param desc 空格的含义
     */
    void getSpace(const string &desc);

    /**
     * 获取一个回车，强调下一个必须是回车，若获取到的不是回车则会结束判题
     *
     * <p color="red">
     *   不推荐使用
     * </p>
     *
     * 推荐使用 StreamReader#readDelimiter 来实现跳过分隔符
     *
     * @param desc 回车的含义
     */
    void getReturn(const string &desc);

    /**
     * 获取一个制表符，强调下一个必须是一个制表符，若获取到的不是制表符则会结束判题
     *
     * <p color="red">
     *   不推荐使用
     * </p>
     *
     * 推荐使用 StreamReader#readDelimiter 来实现跳过分隔符
     *
     * @param desc 制表符的含义
     */
    void getTab(const string &desc);

    /**
     * 读取分隔符，跳过接下来所有的分隔符号，若接下来第一个符号不是分隔符号或者遇到文件结束，也不会抛出错误，而是什么也不做。通常不需要使用，除非需要高度的自定义
     */
    void readDelimiter();

    /**
     * 获取一个字符
     * @param desc 获取的字符的含义
     * @return 被获取的字符
     */
    char readChar(const string &desc);

    /**
     * 获取一个字符，并判断是否在范围内
     * @param lower 最小值（包含）
     * @param upper 最大值（不包含）
     * @param desc 获取的字符的含义
     * @return 被获取的字符
     */
    char readChar(char lower, char upper, const string &desc);

    /**
     * 读取一个字符串，直到遇到一个分隔符，至少包含一个字符，和 StreamReader#readWord 完全等价，更推荐使用 readWord，在语意上更加合理
     * @param maxLen 最长长度
     * @param desc 字符串的含义
     * @return 字符串的含义
     */
    string readString(int maxLen, const string &desc);

    /**
     * 读取一个字符串，直到遇到一个分隔符，至少包含一个字符，和 StreamReader#readString 完全等价
     * @param maxLen 最长长度
     * @param desc 字符串的含义
     * @return 字符串的含义
     */
    string readWord(int maxLen, const string &desc);

    /**
     * 获取一个 32 位的整数，若值超出了 32 位整数范围，则按照自动溢出计算
     *
     * @param desc 获取的整数的含义
     * @return 获取到的整数
     */
    int readInt(const string &desc);

    /**
     * 获取一个 32 位的整数，若值超出了 32 位整数范围，则按照自动溢出计算，并判断是否在一定范围（溢出后判断）
     *
     * @param lower 最小值（包含）
     * @param upper 最大值（不包含）
     * @param desc 获取的整数的含义
     * @return 获取到的整数
     */
    int readInt(int lower, int upper, const string &desc);

    /**
     * 获取一个 64 位的整数，若值超出了 64 位整数范围，则按照自动溢出计算
     *
     * @param desc 获取的整数的含义
     * @return 获取到的整数
     */
    long long readLong(const string &desc);

    /**
     * 获取一个 64 位的整数，若值超出了 64 位整数范围，则按照自动溢出计算，并判断是否在一定范围（溢出后判断）
     *
     * @param lower 最小值（包含）
     * @param upper 最大值（不包含）
     * @param desc 获取的整数的含义
     * @return 获取到的整数
     */
    long long readLong(long long lower, long long upper, const string &desc);

    /**
     * 获取一个浮点数
     *
     * @param desc 获取的浮点数的含义
     * @return 获取到的浮点数
     */
    double readReal(const string &desc);

    /**
     * 获取一个浮点数，并判断是否在一定范围（溢出后判断）
     *
     * @param lower 最小值
     * @param upper 最大值
     * @param desc 获取的浮点数的含义
     * @return 获取到的浮点数
     */
    double readReal(double lower, double upper, const string &desc);

    /**
     * 读取一个字符串，直到遇到一个换行符，并读走此换行符，可能是空行
     * <p color="red">
     *   不推荐使用
     * </p>
     *
     * 题目要求的输出格式应当尽可能通过分隔符进行分割，而不是强制要求相同格式，这不符合出题价值观
     *
     * @param maxLen 最长长度
     * @param desc 字符串的含义
     * @return 字符串的含义
     */
    string readLine(int maxLen, const string &desc);

    /**
     * 检查当前是不是遇到 EOF 了，强调必须是当前是否是 eof，如果不是，则直接结束评测
     * <p color="red">
     * 不推荐使用
     * </p>
     * 判题系统通常应该无视程序额外输出的换行符等结束符号，这些符号通常是被习惯性添加的或者作为一种默认规范存在的
     */
    void getEof();

    /**
     * 检查是否已经没有可读内容了，若剩余的字节都是分隔符，则 OK，否则，则直接结束评测
     */
    void readEof();

    /**
     * 检查是否已经到达文件结束了，若为 true 则文件未结束
     * @return 文件是否还有字节
     */
    bool notEof();
};

class Result : public InStream {
public:

    explicit Result(JudgeResult judgeResult);

    /**
     * 判定此结果有问题
     *
     * @param desc 描述有问题的地方
     */
    void wrongAnswer(const string &desc) const;

    /**
     * 判定此结果有问题
     *
     * @tparam Args 格式化参数内容
     * @param format 格式化模版
     * @param args 格式化内容
     */
    template<class ...Args>
#ifdef __linux__
    __attribute__((__format__ (__printf__, 2, 0)))
#endif
    __attribute__((format (__printf__, 2, 0)))
    void wrongAnswer(const char *format, const Args &...args) const;

    /**
     * 判断是否正确，若满足则继续，若不正确则停止判题
     *
     * @param flag 需要判定的逻辑表达式
     * @param desc 描述内容
     */
    void ensure(bool flag, const string &desc) const;

    /**
     * 判断两个值是否相同，若满足则继续，若不正确则停止判题
     * @tparam T 值类型
     * @param lhs 需要判定的左值
     * @param rhs 需要判定的右值
     * @param lDesc 左值的含义
     * @param rDesc 右值的含义
     */
    template<typename T>
    void equal(const T &lhs, const T &rhs, const string &lDesc, const string &rDesc) const;

    /**
     * 判断两个值是否不相同，若满足则继续，若不正确则停止判题
     * @tparam T 值类型
     * @param lhs 需要判定的左值
     * @param rhs 需要判定的右值
     * @param lDesc 左值的含义
     * @param rDesc 右值的含义
     */
    template<typename T>
    void notEqual(const T &lhs, const T &rhs, const string &lDesc, const string &rDesc) const;

    /**
     * 判断左值是否严格大于右值，若满足则继续，若不正确则停止判题
     * @tparam T 值类型
     * @param lhs 需要判定的左值
     * @param rhs 需要判定的右值
     * @param lDesc 左值的含义
     * @param rDesc 右值的含义
     */
    template<typename T>
    void ge(const T &lhs, const T &rhs, const string &lDesc, const string &rDesc) const;

    /**
     * 判断左值是否大于等于右值，若满足则继续，若不正确则停止判题
     * @tparam T 值类型
     * @param lhs 需要判定的左值
     * @param rhs 需要判定的右值
     * @param lDesc 左值的含义
     * @param rDesc 右值的含义
     */
    template<typename T>
    void geq(const T &lhs, const T &rhs, const string &lDesc, const string &rDesc) const;

    /**
     * 判断左值是否小于右值，若满足则继续，若不正确则停止判题
     * @tparam T 值类型
     * @param lhs 需要判定的左值
     * @param rhs 需要判定的右值
     * @param lDesc 左值的含义
     * @param rDesc 右值的含义
     */
    template<typename T>
    void le(const T &lhs, const T &rhs, const string &lDesc, const string &rDesc) const;

    /**
     * 判断左值是否小于等于右值，若满足则继续，若不正确则停止判题
     * @tparam T 值类型
     * @param lhs 需要判定的左值
     * @param rhs 需要判定的右值
     * @param lDesc 左值的含义
     * @param rDesc 右值的含义
     */
    template<typename T>
    void leq(const T &lhs, const T &rhs, const string &lDesc, const string &rDesc) const;
};

/**
 * 注册成为 check
 * @param argc main 函数的参数 0
 * @param argv main 函数的参数 1
 */
void registerTestlibCmd(int argc, char *argv[]);

/**
 * 设置当前的判题 ID
 * @param id 新的判题 ID
 */
void setTestId(int id);

/**
 * 判题结束，并输出 ac
 * @param desc 描述一些成功信息
 */
void accept(const string &desc);

template<class ...Args>
#ifdef __linux__
__attribute__((__format__ (__printf__, 1, 0)))
#endif
__attribute__((format (__printf__, 1, 0)))
void accept(const char *desc, const Args &...args);

/// endregion

/// region 不应该使用的函数等

template<class ...Args>
#ifdef __linux__
__attribute__((__format__ (__printf__, 2, 0)))
#endif
__attribute__((format (__printf__, 2, 0)))
void endJudge(JudgeResult judgeResult, const char *desc, const Args &...args);

void endJudge(JudgeResult judgeResult, const string &desc);

/// endregion

/// region 函数的实现

int InStream::readBuffer() {
    return (int) read(sid, buffer, 1024);
}

char InStream::next(const string &desc, bool disableEof) {
    if (cur >= len) {
        len = readBuffer();
        cur = 0;
    }
    if (cur >= len) {
        if (disableEof) {
            return -1;
        } else {
            endJudge(waResult, "%s is reach the end of file", desc.c_str());
        }
    }
    return buffer[cur++];
}

char InStream::peek() {
    if (cur >= len) {
        len = readBuffer();
        cur = 0;
    }
    return cur >= len ? (char) -1 : buffer[cur];
}

char InStream::peekNext() {
    cur++;
    return peek();
}

InStream::InStream(JudgeResult wa) : sid(0), cur(0), len(-1), buffer(), waResult(wa) {
}

void InStream::setSid(int id) {
    this->sid = id;
}

char InStream::getByte(const string &desc) {
    return next(desc);
}

void InStream::getSpace(const string &desc) {
    char res = next(desc);
    if (res != ' ') {
        endJudge(waResult, "try to read %s, expect 'space', but get '%c'", desc.c_str(), res);
    }
}

void InStream::getReturn(const string &desc) {
    char res = next(desc);
    if (res != '\n') {
        endJudge(waResult, "try to read %s, expect 'return', but get '%c'", desc.c_str(), res);
    }
}

void InStream::getTab(const string &desc) {
    char res = next(desc);
    if (res != '\t') {
        endJudge(waResult, "try to read %s, expect 'tab', but get '%c'", desc.c_str(), res);
    }
}

void InStream::readDelimiter() {
    char tmp = peek();
    while (isspace(tmp)) {
        tmp = peekNext();
    }
}

char InStream::readChar(const string &desc) {
    readDelimiter();
    return next(desc);
}

char InStream::readChar(char lower, char upper, const string &desc) {
    char res = readChar(desc);
    if (lower > res || res >= upper) {
        endJudge(waResult, "%s is out of bounds, lower: %d, upper: %d, get: %d", desc.c_str(), (int) lower,
                 (int) upper,
                 (int) res);
    }
    return res;
}

string InStream::readString(int maxLen, const string &desc) {
    readDelimiter();
    string res;
    res.push_back(next(desc));
    for (int i = 1; i < maxLen; ++i) {
        char tmp = next(desc, true);
        if (isspace(tmp) || tmp == -1) break;
        res.push_back(tmp);
    }
    if (res.size() == maxLen && !isspace(peek())) {
        endJudge(waResult, "%s, is out of bounds, the string should shorter than %d", desc.c_str(), maxLen);
    }
    return res;
}

string InStream::readWord(int maxLen, const string &desc) {
    return readString(maxLen, desc);
}

int InStream::readInt(const string &desc) {
    long long res = readLong(desc);
    return (int) res;
}

int InStream::readInt(int lower, int upper, const string &desc) {
    int res = readInt(desc);
    if (lower > res && res >= upper) {
        endJudge(waResult, "%s is out of bounds, lower: %d, upper: %d, get: %d", desc.c_str(), lower, upper, res);
    }
    return res;
}

long long InStream::readLong(const string &desc) {
    readDelimiter();
    long long res = 0;
    bool flag = false;
    char tmp = peek();
    if (!isdigit(tmp)) {
        if (tmp == '-') {
            flag = !flag;
        } else if (tmp != '+') {
            endJudge(waResult, "get %s fail, expect number or sign, but get %c", desc.c_str(), tmp);
        }
        tmp = peekNext();
    }
    while (isdigit(tmp)) {
        res = res * 10 + tmp - '0';
        tmp = peekNext();
    }
    res *= flag ? -1 : 1;
    return res;
}

long long InStream::readLong(long long int lower, long long int upper, const string &desc) {
    long long res = readLong(desc);
    if (lower > res && res >= upper) {
        endJudge(waResult, "%s is out of bounds, lower: %lld, upper: %lld, get: %lld", desc.c_str(), lower, upper,
                 res);
    }
    return res;
}

double InStream::readReal(const string &desc) {
    readDelimiter();
    double res = 0, d = 0.1;
    bool flag = false;
    char tmp = peek();
    if (!isdigit(tmp)) {
        if (tmp == '-') {
            flag = !flag;
        } else if (tmp != '+') {
            endJudge(waResult, "get %s fail, expect number or sign, but get %c", desc.c_str(), tmp);
        }
        tmp = peekNext();
    }
    while (isdigit(tmp)) {
        res = res * 10 + tmp - '0';
        tmp = peekNext();
    }
    if (tmp == '.') {
        tmp = peekNext();
        while (isdigit(tmp)) {
            res += d * (tmp - '0');
            d *= 0.1;
            tmp = peekNext();
        }
    }
    res *= flag ? -1 : 1;
    return res;
}

double InStream::readReal(double lower, double upper, const string &desc) {
    double res = readReal(desc);
    if (lower > res && res > upper) {
        endJudge(waResult, "%s is out of bounds, lower: %lf, upper: %lf, get: %lf", desc.c_str(), lower, upper, res);
    }
    return res;
}

string InStream::readLine(int maxLen, const string &desc) {
    string res;
    char tmp = next(desc);
    if (tmp == '\n') {
        return res;
    }
    res.push_back(tmp);
    for (int i = 1; i < maxLen; ++i) {
        tmp = next(desc, true);
        if (tmp == '\n' || tmp == -1) break;
        res.push_back(tmp);
    }
    if (res.size() == maxLen && peek() != '\n') {
        endJudge(waResult, "%s, is out of bounds, the string should shorter than %d", desc.c_str(), maxLen);
    }
    return res;
}

void InStream::getEof() {
    char res = peek();
    if (res != -1) {
        endJudge(waResult, string("The judge is completely over, but there are still bytes in the output"));
    }
}

void InStream::readEof() {
    readDelimiter();
    char res = peek();
    if (res != -1) {
        endJudge(waResult, string("The judge is completely over, but there are still bytes in the output"));
    }
}

bool InStream::notEof() {
    return peek() != -1;
}

Result::Result(JudgeResult judgeResult) : InStream(judgeResult) {}

void Result::wrongAnswer(const string &desc) const {
    endJudge(waResult, desc);
}

template<class ...Args>
#ifdef __linux__
__attribute__((__format__ (__printf__, 2, 0)))
#endif
__attribute__((format (__printf__, 2, 0)))
void Result::wrongAnswer(const char *format, const Args &...args) const {
    endJudge(waResult, format, args...);
}

void Result::ensure(bool flag, const string &desc) const {
    if (!flag) {
        endJudge(waResult, desc);
    }
}

template<typename T>
void Result::equal(const T &lhs, const T &rhs, const string &lDesc, const string &rDesc) const {
    if (lhs != rhs) {
        endJudge(waResult, "%s and %s is not equal", lDesc.c_str(), rDesc.c_str());
    }
}

template<typename T>
void Result::notEqual(const T &lhs, const T &rhs, const string &lDesc, const string &rDesc) const {
    if (lhs == rhs) {
        endJudge(waResult, "%s and %s is equal", lDesc.c_str(), rDesc.c_str());
    }
}

template<typename T>
void Result::ge(const T &lhs, const T &rhs, const string &lDesc, const string &rDesc) const {
    if (lhs <= rhs) {
        endJudge(waResult, "%s is not strictly greater than %s", lDesc.c_str(), rDesc.c_str());
    }
}

template<typename T>
void Result::geq(const T &lhs, const T &rhs, const string &lDesc, const string &rDesc) const {
    if (lhs < rhs) {
        endJudge(waResult, "%s is not greater than %s", lDesc.c_str(), rDesc.c_str());
    }
}

template<typename T>
void Result::le(const T &lhs, const T &rhs, const string &lDesc, const string &rDesc) const {
    if (lhs >= rhs) {
        endJudge(waResult, "%s is not strictly less than %s", lDesc.c_str(), rDesc.c_str());
    }
}

template<typename T>
void Result::leq(const T &lhs, const T &rhs, const string &lDesc, const string &rDesc) const {
    if (lhs > rhs) {
        endJudge(waResult, "%s is not less than %s", lDesc.c_str(), rDesc.c_str());
    }
}

InStream inf(JudgeResult::FAIL);                                               // NOLINT(cert-err58-cpp)
Result ouf(JudgeResult::WA), anf(JudgeResult::FAIL);      // NOLINT(cert-err58-cpp)

void registerTestlibCmd(int argc, char **argv) {
#ifdef ONLINE_JUDGE
    if (argc <= 1) {
        endJudge(JudgeResult::FAIL, "Can not get the input/output file, judge fail");
    }
    filesystem::path inputPath = argv[1];
    filesystem::path ansPath = inputPath;
    inputPath.replace_extension(".in");
    ansPath.replace_extension(".out");

    int inSid = open(inputPath.relative_path().c_str(), O_RDONLY);
    int ansSid = open(ansPath.relative_path().c_str(), O_RDONLY);
    if (inSid == -1 || ansSid == -1) {
        endJudge(JudgeResult::FAIL, "Open file Fail");
    }
    inf.setSid(inSid);
    ouf.setSid(0);
    anf.setSid(ansSid);
#else
    if (argc < 4) {
        endJudge(JudgeResult::FAIL, string("testlib need 3 args: <input-file> <output-file> <answer-file>"));
    }
    int inSid = open(argv[1], O_RDONLY);
    int outSid = open(argv[2], O_RDONLY);
    int ansSid = open(argv[3], O_RDONLY);
    if (inSid == -1 || outSid == -1 || ansSid == -1) {
        endJudge(JudgeResult::FAIL, string("Open file Fail"));
    }
    inf.setSid(inSid);
    ouf.setSid(outSid);
    anf.setSid(ansSid);
#endif
}

template<class... Args>
#ifdef __linux__
__attribute__((__format__ (__printf__, 2, 0)))
#endif
__attribute__((format (__printf__, 2, 0)))
void endJudge(JudgeResult judgeResult, const char *desc, const Args &... args) {
    string judgeCode = to_string((int) judgeResult);
    write(2, judgeCode.c_str(), judgeCode.length());
    write(2, "\n", 1);
    char msgBuffer[128];
    int len = 0;
    if (curTestId > 0) {
        len = snprintf(msgBuffer, 128, "[test %d]: ", curTestId);
    }
    len += snprintf(msgBuffer + len, 128 - len, desc, args...);
    string msgLen = to_string(len);
    write(2, msgLen.c_str(), msgLen.length());
    write(2, "\n", 1);
    write(2, msgBuffer, len);
    exit(0);
}

void endJudge(JudgeResult judgeResult, const string &desc) {
    endJudge(judgeResult, "%s", desc.c_str());
}

void setTestId(int id) {
    curTestId = id;
}

void accept(const string &desc) {
    endJudge(JudgeResult::AC, "%s", desc.c_str());
}

template<class ...Args>
#ifdef __linux__
__attribute__((__format__ (__printf__, 1, 0)))
#endif
__attribute__((format (__printf__, 1, 0)))
void accept(const char *desc, const Args &...args) {
    endJudge(JudgeResult::AC, desc, args...);
}

/// endregion

#endif //PORTABLE_TEST_LIB_H
