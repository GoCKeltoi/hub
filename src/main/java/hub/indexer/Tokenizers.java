package hub.indexer;

import java.util.Collection;
import java.util.List;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;


/**
 * @deprecated Use elastic analyzers and tokenizers.
 */
@Deprecated
@SuppressFBWarnings
@SuppressWarnings("PMD")
class Tokenizers {

    /**
     * Prepare text values using this tokenizer when you want to index
     */
    public static final Tokenizer indexTokenizer =
            newBuilder() //
                    .with(new LowerCaseFilter(), //
                            new CompressFilter(), //
                            new TrimFilter(), new ReplaceAccentsFilter(), //
                            new InsertSpaceBetweenLetterAndDigitFilter()) //
                    .build();

    /**
     * Prepare text values using this tokenizer when you want to search
     */
    public static final Tokenizer searchTokenizer =
            newBuilder() //
                    .with(new EsWildCardTokenFilter()) //
                    .with(new LowerCaseFilter(), //
                            new CompressFilter(), //
                            new TrimFilter(), new ReplaceAccentsFilter(), //
                            new InsertSpaceBetweenLetterAndDigitFilter()) //
                    .build();

    public interface Writeable {
        void write(char c);
    }

    public interface TokenFilter {
        String filter(CharSequence in);
    }

    public interface Tokenizer {
        List<String> tokenize(CharSequence input);
    }

    public interface CharacterFilter {
        void write(char c, Writeable writeable);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {
        final List<CharacterFilter> characterFilters = Lists.newArrayList();

        final List<TokenFilter> tokenFilters = Lists.newArrayList();

        private Builder() {}

        public Builder with(CharacterFilter filter) {
            characterFilters.add(filter);
            return this;
        }

        public Builder with(CharacterFilter filter, CharacterFilter... more) {
            characterFilters.add(filter);
            for (CharacterFilter f : more) {
                characterFilters.add(f);
            }
            return this;
        }

        public Builder with(TokenFilter filter) {
            tokenFilters.add(filter);
            return this;
        }

        public Builder with(TokenFilter filter, TokenFilter... more) {
            tokenFilters.add(filter);
            for (TokenFilter f : more) {
                tokenFilters.add(f);
            }
            return this;
        }

        public Tokenizer build() {

            return new Tokenizer() {
                @Override
                public List<String> tokenize(CharSequence input) {
                    TokenSplitter tokener = new TokenSplitter(tokenFilters);
                    Writeable inputPipeline = tokener;
                    for (CharacterFilter f : characterFilters) {
                        inputPipeline = new FilterWriter(inputPipeline, f);
                    }
                    for (int i = 0, j = input.length(); i < j; i++) {
                        inputPipeline.write(input.charAt(i));
                    }
                    return tokener.tokens();
                }
            };
        }
    }


    @VisibleForTesting
    static String apply(CharSequence seq, final CharacterFilter filter) {
        final StringBuilder b = new StringBuilder();
        FilterWriter w = new FilterWriter(new Writeable() {
            @Override
            public void write(char c) {
                b.append(c);
            }
        }, filter);
        for (int i = 0, j = seq.length(); i < j; i++) {
            w.write(seq.charAt(i));
        }
        return b.toString();
    }

    public static class FilterWriter implements Writeable {

        private final Writeable out;

        private final CharacterFilter filter;

        public FilterWriter(Writeable out, CharacterFilter filter) {
            this.out = out;
            this.filter = filter;
        }

        @Override
        public void write(char c) {
            filter.write(c, out);
        }

    }

    public static class LowerCaseFilter implements CharacterFilter {

        @Override
        public void write(char c, Writeable out) {
            out.write(Character.toLowerCase(c));
        }
    }

    public static class ReplaceAccentsFilter implements CharacterFilter {

        @Override
        public void write(char c, Writeable out) {
            if (c < '\u00C0') {
                out.write(c);
                return;
            }
            switch (c) {
                case '\u00C0': // À
                case '\u00C1': // Á
                case '\u00C2': // Â
                case '\u00C3': // Ã
                case '\u00C5': // Å
                case '\u0102': // Ă
                    out.write('A');
                    break;
                case '\u00C4': // Ä
                case '\u00C6': // Æ
                    out.write('A');
                    out.write('E');
                    break;
                case '\u00C7': // Ç
                    out.write('C');
                    break;
                case '\u00C8': // È
                case '\u00C9': // É
                case '\u00CA': // Ê
                case '\u00CB': // Ë
                    out.write('E');
                    break;
                case '\u00CC': // Ì
                case '\u00CD': // Í
                case '\u00CE': // Î
                case '\u00CF': // Ï
                    out.write('I');
                    break;
                case '\u00D0': // Ð
                    out.write('D');
                    break;
                case '\u00D1': // Ñ
                    out.write('N');
                    break;
                case '\u00D2': // Ò
                case '\u00D3': // Ó
                case '\u00D4': // Ô
                case '\u00D5': // Õ
                case '\u00D8': // Ø
                    out.write('O');
                    break;
                case '\u0152': // Œ
                case '\u00D6': // Ö
                    out.write('O');
                    out.write('E');
                    break;
                case '\u0218': // Ș
                case '\u015E': // Ş
                    out.write('S');
                    break;
                case '\u021A': // Ț
                case '\u0162': // Ţ
                    out.write('T');
                    break;
                case '\u00DE': // Þ
                    out.write('T');
                    out.write('H');
                    break;
                case '\u00D9': // Ù
                case '\u00DA': // Ú
                case '\u00DB': // Û
                    out.write('U');
                    break;
                case '\u00DC': // Ü
                    out.write('U');
                    out.write('E');
                    break;
                case '\u00DD': // Ý
                case '\u0178': // Ÿ
                    out.write('Y');
                    break;
                case '\u00E0': // à
                case '\u00E1': // á
                case '\u00E2': // â
                case '\u00E3': // ã
                case '\u00E5': // å
                case '\u0103': // ă
                    out.write('a');
                    break;
                case '\u00E4': // ä
                case '\u00E6': // æ
                    out.write('a');
                    out.write('e');
                    break;
                case '\u00E7': // ç
                    out.write('c');
                    break;
                case '\u00E8': // è
                case '\u00E9': // é
                case '\u00EA': // ê
                case '\u00EB': // ë
                    out.write('e');
                    break;
                case '\u00EC': // ì
                case '\u00ED': // í
                case '\u00EE': // î
                case '\u00EF': // ï
                    out.write('i');
                    break;
                case '\u00F0': // ð
                    out.write('d');
                    break;
                case '\u00F1': // ñ
                    out.write('n');
                    break;
                case '\u00F2': // ò
                case '\u00F3': // ó
                case '\u00F4': // ô
                case '\u00F5': // õ
                case '\u00F8': // ø
                    out.write('o');
                    break;
                case '\u00F6': // ö
                case '\u0153': // œ
                    out.write('o');
                    out.write('e');
                    break;
                case '\u0219': // ș
                case '\u015F': // ş
                    out.write('s');
                    break;
                case '\u00DF': // ß
                    out.write('s');
                    out.write('s');
                    break;
                case '\u021B': // ț
                case '\u0163': // ţ
                    out.write('t');
                    break;
                case '\u00FE': // þ
                    out.write('t');
                    out.write('h');
                    break;
                case '\u00F9': // ù
                case '\u00FA': // ú
                case '\u00FB': // û
                    out.write('u');
                    break;
                case '\u00FC': // ü
                    out.write('u');
                    out.write('e');
                    break;
                case '\u00FD': // ý
                case '\u00FF': // ÿ
                    out.write('y');
                    break;
                default:
                    out.write(c);
            }
        }
    }

    public static class CompressFilter implements CharacterFilter {

        int last = -1;

        static boolean isWord(int c) {
            return c >= 'A' && c <= 'z' || c >= '0' && c <= '9' || c == '_' || c >= 0x00c4 && c <= 0x00ff;
        }

        static boolean isNonWord(int c) {
            return c > -1 && !isWord(c);
        }

        @Override
        public void write(char in, Writeable out) {
            if (isWord(in)) {
                if (isNonWord(last)) {
                    out.write(' ');
                }
                out.write(in);
            }
            last = in;
        }
    }

    public static class TrimFilter implements CharacterFilter {

        boolean firstSeen = false;

        @Override
        public void write(char in, Writeable out) {
            if (firstSeen) {
                out.write(in);
            } else if (!firstSeen && !Character.isWhitespace(in)) {
                firstSeen = true;
                out.write(in);
            }
        }
    }

    public static class InsertSpaceBetweenLetterAndDigitFilter implements CharacterFilter {

        static boolean isDigit(int c) {
            return c >= '0' && c <= '9';
        }

        static boolean isLetter(int c) {
            return c >= 'A' && c <= 'z';
        }

        int lastChar = -1;

        @Override
        public void write(char c, Writeable out) {
            if (isDigit(lastChar) && isLetter(c)) {
                out.write(' ');
                out.write(c);
            } else if (isLetter(lastChar) && isDigit(c)) {
                out.write(' ');
                out.write(c);
            } else {
                out.write(c);
            }
            lastChar = c;
        }

    }

    public static class WildCardTokenFilter implements TokenFilter {
        @Override
        public String filter(CharSequence in) {
            return "x" + in + "*";
        }
    }

    public static class EsWildCardTokenFilter implements TokenFilter {
        @Override
        public String filter(CharSequence in) {
            return in + "*";
        }
    }

    static class TokenSplitter implements Writeable {
        final List<String> tokens = Lists.newArrayList();

        final StringBuilder builder = new StringBuilder();

        final List<TokenFilter> filter = Lists.newArrayList();

        TokenSplitter(TokenFilter filter) {
            this.filter.add(filter);
        }

        TokenSplitter(Collection<TokenFilter> filter) {
            this.filter.addAll(filter);
        }

        @Override
        public void write(char c) {
            if (Character.isWhitespace(c)) {
                flush();
            } else {
                builder.append(c);
            }
        }

        void flush() {
            if (builder.length() > 0) {
                CharSequence token = builder;
                for (TokenFilter f : filter) {
                    token = f.filter(token);
                }
                tokens.add(token.toString());
                builder.setLength(0);
            }
        }

        List<String> tokens() {
            flush();
            return tokens;
        }

        @Override
        public String toString() {
            return tokens.toString();
        }
    }

}