/**
 * Created by 胡柯青 on 2022-01-13.
 *
 * Judging that the comparison output is the same as the answer in double
 * max absolute or relative error is 0.0001
 */

#include "testlib.h"

int main(int argc, char *argv[]) {
    registerTestlibCmd(argc, argv);

    const double eps = 1e-6;
    double ans, out;
    int totalFloat = 0;
    while (!anf.readDelimiter()) {
        totalFloat++;
        ans = anf.readReal("Float in answer");
        out = ouf.readReal("Float in output");
        double diff = abs(ans - out);
        if (diff >= eps) {
            ouf.wrongAnswer(
                    "Relative error(%lf) for %dth floating point number exceeds upper limit(%lf), expect: %lf, find %lf",
                    diff, totalFloat, eps, ans, out);
        }
    }

    ouf.readEof();

    accept("%d floating point number(s) is/are all within the error range(%lf)",
           totalFloat, eps);
}
