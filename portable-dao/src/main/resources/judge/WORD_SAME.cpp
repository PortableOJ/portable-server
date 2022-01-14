/**
 * Created by 胡柯青 on 2022-01-13.
 *
 * Judging that the comparison output is the same as the answer in valid strings
 *
 * valid strings: strings that do not contain separators such as spaces, carriage returns, and tabs
 */

#include "testlib.h"

int main(int argc, char *argv[]) {
    registerTestlibCmd(argc, argv);

    const int maxLen = 10000000;

    string ans, out;
    int totalWord = 0;
    while (anf.notEof()) {
        totalWord++;
        ans = anf.readWord(maxLen, "Word in answer");
        out = ouf.readWord(maxLen, "Word in output");
        if (ans != out) {
            if (ans.size() + out.size() <= 30) {
                ouf.wrongAnswer("%dth word is different, expect: %s, find: %s", totalWord, ans.c_str(), out.c_str());
            } else {
                ouf.wrongAnswer("%dth word is different", totalWord);
            }
        }
    }

    ouf.getEof();

    accept("%d valid strings is same", totalWord);
}
