/**
 * Created by 胡柯青 on 2022-01-13.
 *
 * Judging that the comparison output is the same as the answer in integer
 */

#include "testlib.h"

int main(int argc, char *argv[]) {
    registerTestlibCmd(argc, argv);

    long long ans, out;
    int totalNumber = 0;
    while (!anf.readDelimiter()) {
        totalNumber++;
        ans = anf.readLong("Integer in answer");
        out = ouf.readLong("Integer in output");
        if (ans != out) {
            ouf.wrongAnswer("%dth integer is different, expect: %lld, find: %lld", totalNumber, ans, out);
        }
    }

    ouf.readEof();

    accept("%d integers is same", totalNumber);
}
