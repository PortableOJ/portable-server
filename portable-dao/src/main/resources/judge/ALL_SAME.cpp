/**
 * Created by 胡柯青 on 2022-01-13.
 *
 * The judge comparison output and the answer are strictly consistent
 */

#include "testlib.h"

int main(int argc, char *argv[]) {
    registerTestlibCmd(argc, argv);

    char out, ans;
    int totalByte = 0;
    while (anf.notGetEof()) {
        totalByte++;
        ans = anf.getByte("A byte");
        out = ouf.getByte("A byte");
        if (ans != out) {
            ouf.wrongAnswer("The %dth byte is different, expect: %c, find: %c", totalByte, ans, out);
        }
    }
    ouf.getEof();

    accept("%d byte(s) is/are same", totalByte);
}