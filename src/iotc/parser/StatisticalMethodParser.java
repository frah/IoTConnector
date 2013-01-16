package iotc.parser;

import org.codehaus.jparsec.*;
import org.codehaus.jparsec.functors.*;
import org.codehaus.jparsec.pattern.*;
import static org.codehaus.jparsec.Parsers.sequence;
import static org.codehaus.jparsec.Parsers.tuple;
import static org.codehaus.jparsec.Scanners.string;

/**
 *
 * @author atsushi-o
 */
public class StatisticalMethodParser {
    enum FunctionMapper implements Map<String, Double> {
        SUM {
            @Override public Double map(String s) {
                return 0.0;
            }
        };
        public static String[] getFunctions() {
            FunctionMapper[] fo = values();
            String[] ret = new String[fo.length];
            for (int i = 0; i < fo.length; i++) {
                ret[i] = fo[i].name();
            }
            return ret;
        }
    }

    // LETTER :: [^()]+
    public static final Parser<String> LETTER =
            Scanners.pattern(Patterns.regex("[^()]"), "any char except parenthesis").many1().source();
    // BIG_LETTER :: <big letter>+
    private static final Parser<String> BIG_LETTER =
            Scanners.pattern(Patterns.regex("[A-Z]+"), "big letters").source();
    // FUNCTION ::= <BIG_LETTER> "(" .+ ")"
    private static final Parser<String> FUNCTION =
            tuple(BIG_LETTER, string("("), LETTER, string(")"))
            .map(new Map<Tuple4<String, Void, String, Void>, String>(){
                @Override
                public String map(Tuple4<String, Void, String, Void> from) {
                    System.out.println("Func: "+from.a);
                    System.out.println("Arg: "+from.c);
                    return "test";
                }
            });
    private static final Parser<String> NUMERIC_FUNC =
            Scanners.pattern(Patterns.regex("[0-9+-/() ]"), "numeric func").many1().source();
    private static final Parser<String> METHOD =
            Parsers.or(NUMERIC_FUNC, FUNCTION).many().source();
    /*
    static final Parser<String> FUNCTION = Scanners.many(P_FUNCTION, "function expected").source();

    static Parser<String> funcSolver(Parser<String> atom) {

    }*/

    private static final Parser<String> PARSER = METHOD;
    /**
     * 統計関数文字列をパースして計算結果を返す
     * @param source 統計関数文字列
     * @return 計算結果
     */
    public static Double parse(CharSequence source) {
        return NumericCalcParser.parse(PARSER.parse(source));
    }
    /* インスタンス化抑制 */
    private StatisticalMethodParser() {}

    public static void main(String[] args) {
        System.out.println(PARSER.parse("SUM(Living::SunSPOT::Illum) + 20"));
    }
}
