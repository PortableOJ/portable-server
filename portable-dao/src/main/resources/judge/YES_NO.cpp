/**
 * Created by 胡柯青 on 2022-01-13.
 *
 * Judging that the comparison output is the same as the answer in the range YES and NO,
 * including all forms of YES and NO
 *
 * for example, "YES" can be "Yes", "YEs", "TrUe", "T", "Y"
 */

#include "testlib.h"

void toLower(string &value) {
    for (auto &item: value) {
        if (isupper(item)) {
            item += 'a' - 'A';
        }
    }
}

bool yesOrNo(const Result &result, const string &value) {
    const static vector<string> yesList = {"yes", "y", "true", "t"};
    const static vector<string> noList = {"no", "n", "false", "f"};
    for (auto &item: yesList) {
        if (value == item) {
            return true;
        }
    }
    for (auto &item: noList) {
        if (value == item) {
            return false;
        }
    }
    result.wrongAnswer(value + " is not yes or no");
    return false;
}

int main(int argc, char *argv[]) {
    registerTestlibCmd(argc, argv);

    const int maxLen = 5;

    string ans, out;
    bool ansFlag, outFlag;
    int totalYesOrNo = 0;
    while (!anf.readDelimiter()) {
        totalYesOrNo++;
        ans = anf.readWord(maxLen, "Yes or No in answer");
        out = ouf.readWord(maxLen, "Yes or No in output");

        toLower(ans);
        toLower(out);

        ansFlag = yesOrNo(anf, ans);
        outFlag = yesOrNo(ouf, out);

        if (ansFlag != outFlag) {
            ouf.wrongAnswer("%dth flag is different, expect: %s, find: %s",
                            totalYesOrNo, (ansFlag ? "Yes" : "No"), (outFlag ? "Yes" : "No"));
        }
    }

    ouf.readEof();

    accept("%d 'yes and no' word is same", totalYesOrNo);
}
