/**
 * Created by 胡柯青 on 2022-01-13.
 *
 * Judging that the comparison output is the same as the answer
 * except for the last return at the end of the file
 */

#include "testlib.h"

int main(int argc, char *argv[]) {
    registerTestlibCmd(argc, argv);

    const int maxLen = 10000000;

    string ans, out;
    int totalLine = 0;
    while (anf.notGetEof()) {
        totalLine++;
        ans = anf.readLine(maxLen, "Line in answer");
        out = ouf.readLine(maxLen, "Line in output");
        if (ans != out) {
            if (ans.size() + out.size() <= 30) {
                ouf.wrongAnswer("%dth line is different, expect: %s, find: %s", totalLine, ans.c_str(), out.c_str());
            } else {
                ouf.wrongAnswer("%dth line is different", totalLine);
            }
        }
    }
    ouf.getEof();

    accept("%d line(s) is/are same", totalLine);
}