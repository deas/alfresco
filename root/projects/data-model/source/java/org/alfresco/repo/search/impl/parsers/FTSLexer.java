// $ANTLR 3.3 Nov 30, 2010 12:50:56 W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g 2011-05-12 10:23:39

package org.alfresco.repo.search.impl.parsers;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
public class FTSLexer extends Lexer {
    public static final int EOF=-1;
    public static final int FTS=4;
    public static final int DISJUNCTION=5;
    public static final int CONJUNCTION=6;
    public static final int NEGATION=7;
    public static final int TERM=8;
    public static final int EXACT_TERM=9;
    public static final int PHRASE=10;
    public static final int EXACT_PHRASE=11;
    public static final int SYNONYM=12;
    public static final int RANGE=13;
    public static final int PROXIMITY=14;
    public static final int DEFAULT=15;
    public static final int MANDATORY=16;
    public static final int OPTIONAL=17;
    public static final int EXCLUDE=18;
    public static final int FIELD_DISJUNCTION=19;
    public static final int FIELD_CONJUNCTION=20;
    public static final int FIELD_NEGATION=21;
    public static final int FIELD_GROUP=22;
    public static final int FIELD_DEFAULT=23;
    public static final int FIELD_MANDATORY=24;
    public static final int FIELD_OPTIONAL=25;
    public static final int FIELD_EXCLUDE=26;
    public static final int FG_TERM=27;
    public static final int FG_EXACT_TERM=28;
    public static final int FG_PHRASE=29;
    public static final int FG_EXACT_PHRASE=30;
    public static final int FG_SYNONYM=31;
    public static final int FG_PROXIMITY=32;
    public static final int FG_RANGE=33;
    public static final int FIELD_REF=34;
    public static final int INCLUSIVE=35;
    public static final int EXCLUSIVE=36;
    public static final int QUALIFIER=37;
    public static final int PREFIX=38;
    public static final int NAME_SPACE=39;
    public static final int BOOST=40;
    public static final int FUZZY=41;
    public static final int TEMPLATE=42;
    public static final int PLUS=43;
    public static final int BAR=44;
    public static final int MINUS=45;
    public static final int LPAREN=46;
    public static final int RPAREN=47;
    public static final int PERCENT=48;
    public static final int COMMA=49;
    public static final int TILDA=50;
    public static final int DECIMAL_INTEGER_LITERAL=51;
    public static final int CARAT=52;
    public static final int COLON=53;
    public static final int EQUALS=54;
    public static final int FTSPHRASE=55;
    public static final int ID=56;
    public static final int FTSWORD=57;
    public static final int FTSPRE=58;
    public static final int FTSWILD=59;
    public static final int NOT=60;
    public static final int TO=61;
    public static final int FLOATING_POINT_LITERAL=62;
    public static final int STAR=63;
    public static final int DOTDOT=64;
    public static final int LSQUARE=65;
    public static final int LT=66;
    public static final int RSQUARE=67;
    public static final int GT=68;
    public static final int AT=69;
    public static final int URI=70;
    public static final int DOT=71;
    public static final int QUESTION_MARK=72;
    public static final int OR=73;
    public static final int AND=74;
    public static final int AMP=75;
    public static final int EXCLAMATION=76;
    public static final int F_ESC=77;
    public static final int F_URI_ALPHA=78;
    public static final int F_URI_DIGIT=79;
    public static final int F_URI_OTHER=80;
    public static final int F_HEX=81;
    public static final int F_URI_ESC=82;
    public static final int LCURL=83;
    public static final int RCURL=84;
    public static final int DOLLAR=85;
    public static final int DECIMAL_NUMERAL=86;
    public static final int INWORD=87;
    public static final int START_RANGE_I=88;
    public static final int START_RANGE_F=89;
    public static final int DIGIT=90;
    public static final int EXPONENT=91;
    public static final int ZERO_DIGIT=92;
    public static final int NON_ZERO_DIGIT=93;
    public static final int E=94;
    public static final int SIGNED_INTEGER=95;
    public static final int WS=96;

    List tokens = new ArrayList();
    public void emit(Token token) {
            state.token = token;
            tokens.add(token);
    }
    public Token nextToken() {
            nextTokenImpl();
            if ( tokens.size()==0 ) {
                Token eof = new CommonToken((CharStream)input,Token.EOF,
                                                Token.DEFAULT_CHANNEL,
                                                input.index(),input.index());
                eof.setLine(getLine());
                eof.setCharPositionInLine(getCharPositionInLine());
                return eof;
            }
            return (Token)tokens.remove(0);
    }

    public Token nextTokenImpl() {
            while (true) 
            {
                state.token = null;
                state.channel = Token.DEFAULT_CHANNEL;
                state.tokenStartCharIndex = input.index();
                state.tokenStartCharPositionInLine = input.getCharPositionInLine();
                state.tokenStartLine = input.getLine();
                state.text = null;
                if ( input.LA(1)==CharStream.EOF ) 
                {
                    Token eof = new CommonToken((CharStream)input,Token.EOF,
                                                Token.DEFAULT_CHANNEL,
                                                input.index(),input.index());
                    eof.setLine(getLine());
                    eof.setCharPositionInLine(getCharPositionInLine());
                    return eof;
                }
                try 
                {
                    mTokens();
                    if ( state.token==null ) 
                    {
                        emit();
                    }
                    else if ( state.token==Token.SKIP_TOKEN ) 
                    {
                        continue;
                    }
                    return state.token;
                }
                catch (RecognitionException re) 
                {
                    throw new FTSQueryException(getErrorString(re), re);
                }
            }
        }
        
        public String getErrorString(RecognitionException e)
        {
            String hdr = getErrorHeader(e);
            String msg = getErrorMessage(e, this.getTokenNames());
            return hdr+" "+msg;
        }


    // delegates
    // delegators

    public FTSLexer() {;} 
    public FTSLexer(CharStream input) {
        this(input, new RecognizerSharedState());
    }
    public FTSLexer(CharStream input, RecognizerSharedState state) {
        super(input,state);

    }
    public String getGrammarFileName() { return "W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g"; }

    // $ANTLR start "FTSPHRASE"
    public final void mFTSPHRASE() throws RecognitionException {
        try {
            int _type = FTSPHRASE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:867:9: ( '\"' ( F_ESC | ~ ( '\\\\' | '\"' ) )* '\"' | '\\'' ( F_ESC | ~ ( '\\\\' | '\\'' ) )* '\\'' )
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( (LA3_0=='\"') ) {
                alt3=1;
            }
            else if ( (LA3_0=='\'') ) {
                alt3=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 3, 0, input);

                throw nvae;
            }
            switch (alt3) {
                case 1 :
                    // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:868:9: '\"' ( F_ESC | ~ ( '\\\\' | '\"' ) )* '\"'
                    {
                    match('\"'); if (state.failed) return ;
                    // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:869:9: ( F_ESC | ~ ( '\\\\' | '\"' ) )*
                    loop1:
                    do {
                        int alt1=3;
                        int LA1_0 = input.LA(1);

                        if ( (LA1_0=='\\') ) {
                            alt1=1;
                        }
                        else if ( ((LA1_0>='\u0000' && LA1_0<='!')||(LA1_0>='#' && LA1_0<='[')||(LA1_0>=']' && LA1_0<='\uFFFF')) ) {
                            alt1=2;
                        }


                        switch (alt1) {
                    	case 1 :
                    	    // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:870:17: F_ESC
                    	    {
                    	    mF_ESC(); if (state.failed) return ;

                    	    }
                    	    break;
                    	case 2 :
                    	    // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:872:17: ~ ( '\\\\' | '\"' )
                    	    {
                    	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='!')||(input.LA(1)>='#' && input.LA(1)<='[')||(input.LA(1)>=']' && input.LA(1)<='\uFFFF') ) {
                    	        input.consume();
                    	    state.failed=false;
                    	    }
                    	    else {
                    	        if (state.backtracking>0) {state.failed=true; return ;}
                    	        MismatchedSetException mse = new MismatchedSetException(null,input);
                    	        recover(mse);
                    	        throw mse;}


                    	    }
                    	    break;

                    	default :
                    	    break loop1;
                        }
                    } while (true);

                    match('\"'); if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:878:11: '\\'' ( F_ESC | ~ ( '\\\\' | '\\'' ) )* '\\''
                    {
                    match('\''); if (state.failed) return ;
                    // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:879:9: ( F_ESC | ~ ( '\\\\' | '\\'' ) )*
                    loop2:
                    do {
                        int alt2=3;
                        int LA2_0 = input.LA(1);

                        if ( (LA2_0=='\\') ) {
                            alt2=1;
                        }
                        else if ( ((LA2_0>='\u0000' && LA2_0<='&')||(LA2_0>='(' && LA2_0<='[')||(LA2_0>=']' && LA2_0<='\uFFFF')) ) {
                            alt2=2;
                        }


                        switch (alt2) {
                    	case 1 :
                    	    // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:880:17: F_ESC
                    	    {
                    	    mF_ESC(); if (state.failed) return ;

                    	    }
                    	    break;
                    	case 2 :
                    	    // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:882:17: ~ ( '\\\\' | '\\'' )
                    	    {
                    	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='&')||(input.LA(1)>='(' && input.LA(1)<='[')||(input.LA(1)>=']' && input.LA(1)<='\uFFFF') ) {
                    	        input.consume();
                    	    state.failed=false;
                    	    }
                    	    else {
                    	        if (state.backtracking>0) {state.failed=true; return ;}
                    	        MismatchedSetException mse = new MismatchedSetException(null,input);
                    	        recover(mse);
                    	        throw mse;}


                    	    }
                    	    break;

                    	default :
                    	    break loop2;
                        }
                    } while (true);

                    match('\''); if (state.failed) return ;

                    }
                    break;

            }
            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "FTSPHRASE"

    // $ANTLR start "URI"
    public final void mURI() throws RecognitionException {
        try {
            int _type = URI;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:896:9: ( '{' ( ( F_URI_ALPHA | F_URI_DIGIT | F_URI_OTHER )=> ( F_URI_ALPHA | F_URI_DIGIT | F_URI_OTHER )+ COLON )? ( ( ( '//' )=> '//' ) ( ( F_URI_ALPHA | F_URI_DIGIT | F_URI_OTHER | COLON )=> ( F_URI_ALPHA | F_URI_DIGIT | F_URI_OTHER | COLON ) )* )? ( F_URI_ALPHA | F_URI_DIGIT | F_URI_OTHER | COLON | '/' )* ( '?' ( F_URI_ALPHA | F_URI_DIGIT | F_URI_OTHER | COLON | '/' | '?' )* )? ( '#' ( F_URI_ALPHA | F_URI_DIGIT | F_URI_OTHER | COLON | '/' | '?' | '#' )* )? '}' )
            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:897:9: '{' ( ( F_URI_ALPHA | F_URI_DIGIT | F_URI_OTHER )=> ( F_URI_ALPHA | F_URI_DIGIT | F_URI_OTHER )+ COLON )? ( ( ( '//' )=> '//' ) ( ( F_URI_ALPHA | F_URI_DIGIT | F_URI_OTHER | COLON )=> ( F_URI_ALPHA | F_URI_DIGIT | F_URI_OTHER | COLON ) )* )? ( F_URI_ALPHA | F_URI_DIGIT | F_URI_OTHER | COLON | '/' )* ( '?' ( F_URI_ALPHA | F_URI_DIGIT | F_URI_OTHER | COLON | '/' | '?' )* )? ( '#' ( F_URI_ALPHA | F_URI_DIGIT | F_URI_OTHER | COLON | '/' | '?' | '#' )* )? '}'
            {
            match('{'); if (state.failed) return ;
            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:898:9: ( ( F_URI_ALPHA | F_URI_DIGIT | F_URI_OTHER )=> ( F_URI_ALPHA | F_URI_DIGIT | F_URI_OTHER )+ COLON )?
            int alt5=2;
            alt5 = dfa5.predict(input);
            switch (alt5) {
                case 1 :
                    // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:899:17: ( F_URI_ALPHA | F_URI_DIGIT | F_URI_OTHER )=> ( F_URI_ALPHA | F_URI_DIGIT | F_URI_OTHER )+ COLON
                    {
                    // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:905:17: ( F_URI_ALPHA | F_URI_DIGIT | F_URI_OTHER )+
                    int cnt4=0;
                    loop4:
                    do {
                        int alt4=2;
                        int LA4_0 = input.LA(1);

                        if ( (LA4_0=='!'||LA4_0=='$'||(LA4_0>='&' && LA4_0<='.')||(LA4_0>='0' && LA4_0<='9')||LA4_0==';'||LA4_0=='='||(LA4_0>='@' && LA4_0<='[')||LA4_0==']'||LA4_0=='_'||(LA4_0>='a' && LA4_0<='z')||LA4_0=='~') ) {
                            alt4=1;
                        }


                        switch (alt4) {
                    	case 1 :
                    	    // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:
                    	    {
                    	    if ( input.LA(1)=='!'||input.LA(1)=='$'||(input.LA(1)>='&' && input.LA(1)<='.')||(input.LA(1)>='0' && input.LA(1)<='9')||input.LA(1)==';'||input.LA(1)=='='||(input.LA(1)>='@' && input.LA(1)<='[')||input.LA(1)==']'||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z')||input.LA(1)=='~' ) {
                    	        input.consume();
                    	    state.failed=false;
                    	    }
                    	    else {
                    	        if (state.backtracking>0) {state.failed=true; return ;}
                    	        MismatchedSetException mse = new MismatchedSetException(null,input);
                    	        recover(mse);
                    	        throw mse;}


                    	    }
                    	    break;

                    	default :
                    	    if ( cnt4 >= 1 ) break loop4;
                    	    if (state.backtracking>0) {state.failed=true; return ;}
                                EarlyExitException eee =
                                    new EarlyExitException(4, input);
                                throw eee;
                        }
                        cnt4++;
                    } while (true);

                    mCOLON(); if (state.failed) return ;

                    }
                    break;

            }

            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:912:9: ( ( ( '//' )=> '//' ) ( ( F_URI_ALPHA | F_URI_DIGIT | F_URI_OTHER | COLON )=> ( F_URI_ALPHA | F_URI_DIGIT | F_URI_OTHER | COLON ) )* )?
            int alt7=2;
            int LA7_0 = input.LA(1);

            if ( (LA7_0=='/') ) {
                int LA7_1 = input.LA(2);

                if ( (LA7_1=='/') ) {
                    int LA7_6 = input.LA(3);

                    if ( (synpred2_FTS()) ) {
                        alt7=1;
                    }
                }
            }
            switch (alt7) {
                case 1 :
                    // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:913:17: ( ( '//' )=> '//' ) ( ( F_URI_ALPHA | F_URI_DIGIT | F_URI_OTHER | COLON )=> ( F_URI_ALPHA | F_URI_DIGIT | F_URI_OTHER | COLON ) )*
                    {
                    // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:913:17: ( ( '//' )=> '//' )
                    // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:913:19: ( '//' )=> '//'
                    {
                    match("//"); if (state.failed) return ;


                    }

                    // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:914:17: ( ( F_URI_ALPHA | F_URI_DIGIT | F_URI_OTHER | COLON )=> ( F_URI_ALPHA | F_URI_DIGIT | F_URI_OTHER | COLON ) )*
                    loop6:
                    do {
                        int alt6=2;
                        int LA6_0 = input.LA(1);

                        if ( (LA6_0=='!'||LA6_0=='$'||(LA6_0>='&' && LA6_0<='.')||(LA6_0>='0' && LA6_0<=';')||LA6_0=='='||(LA6_0>='@' && LA6_0<='[')||LA6_0==']'||LA6_0=='_'||(LA6_0>='a' && LA6_0<='z')||LA6_0=='~') ) {
                            int LA6_1 = input.LA(2);

                            if ( (synpred3_FTS()) ) {
                                alt6=1;
                            }


                        }


                        switch (alt6) {
                    	case 1 :
                    	    // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:915:25: ( F_URI_ALPHA | F_URI_DIGIT | F_URI_OTHER | COLON )=> ( F_URI_ALPHA | F_URI_DIGIT | F_URI_OTHER | COLON )
                    	    {
                    	    if ( input.LA(1)=='!'||input.LA(1)=='$'||(input.LA(1)>='&' && input.LA(1)<='.')||(input.LA(1)>='0' && input.LA(1)<=';')||input.LA(1)=='='||(input.LA(1)>='@' && input.LA(1)<='[')||input.LA(1)==']'||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z')||input.LA(1)=='~' ) {
                    	        input.consume();
                    	    state.failed=false;
                    	    }
                    	    else {
                    	        if (state.backtracking>0) {state.failed=true; return ;}
                    	        MismatchedSetException mse = new MismatchedSetException(null,input);
                    	        recover(mse);
                    	        throw mse;}


                    	    }
                    	    break;

                    	default :
                    	    break loop6;
                        }
                    } while (true);


                    }
                    break;

            }

            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:930:9: ( F_URI_ALPHA | F_URI_DIGIT | F_URI_OTHER | COLON | '/' )*
            loop8:
            do {
                int alt8=2;
                int LA8_0 = input.LA(1);

                if ( (LA8_0=='!'||LA8_0=='$'||(LA8_0>='&' && LA8_0<=';')||LA8_0=='='||(LA8_0>='@' && LA8_0<='[')||LA8_0==']'||LA8_0=='_'||(LA8_0>='a' && LA8_0<='z')||LA8_0=='~') ) {
                    alt8=1;
                }


                switch (alt8) {
            	case 1 :
            	    // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:
            	    {
            	    if ( input.LA(1)=='!'||input.LA(1)=='$'||(input.LA(1)>='&' && input.LA(1)<=';')||input.LA(1)=='='||(input.LA(1)>='@' && input.LA(1)<='[')||input.LA(1)==']'||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z')||input.LA(1)=='~' ) {
            	        input.consume();
            	    state.failed=false;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return ;}
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


            	    }
            	    break;

            	default :
            	    break loop8;
                }
            } while (true);

            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:937:9: ( '?' ( F_URI_ALPHA | F_URI_DIGIT | F_URI_OTHER | COLON | '/' | '?' )* )?
            int alt10=2;
            int LA10_0 = input.LA(1);

            if ( (LA10_0=='?') ) {
                alt10=1;
            }
            switch (alt10) {
                case 1 :
                    // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:938:17: '?' ( F_URI_ALPHA | F_URI_DIGIT | F_URI_OTHER | COLON | '/' | '?' )*
                    {
                    match('?'); if (state.failed) return ;
                    // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:939:17: ( F_URI_ALPHA | F_URI_DIGIT | F_URI_OTHER | COLON | '/' | '?' )*
                    loop9:
                    do {
                        int alt9=2;
                        int LA9_0 = input.LA(1);

                        if ( (LA9_0=='!'||LA9_0=='$'||(LA9_0>='&' && LA9_0<=';')||LA9_0=='='||(LA9_0>='?' && LA9_0<='[')||LA9_0==']'||LA9_0=='_'||(LA9_0>='a' && LA9_0<='z')||LA9_0=='~') ) {
                            alt9=1;
                        }


                        switch (alt9) {
                    	case 1 :
                    	    // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:
                    	    {
                    	    if ( input.LA(1)=='!'||input.LA(1)=='$'||(input.LA(1)>='&' && input.LA(1)<=';')||input.LA(1)=='='||(input.LA(1)>='?' && input.LA(1)<='[')||input.LA(1)==']'||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z')||input.LA(1)=='~' ) {
                    	        input.consume();
                    	    state.failed=false;
                    	    }
                    	    else {
                    	        if (state.backtracking>0) {state.failed=true; return ;}
                    	        MismatchedSetException mse = new MismatchedSetException(null,input);
                    	        recover(mse);
                    	        throw mse;}


                    	    }
                    	    break;

                    	default :
                    	    break loop9;
                        }
                    } while (true);


                    }
                    break;

            }

            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:948:9: ( '#' ( F_URI_ALPHA | F_URI_DIGIT | F_URI_OTHER | COLON | '/' | '?' | '#' )* )?
            int alt12=2;
            int LA12_0 = input.LA(1);

            if ( (LA12_0=='#') ) {
                alt12=1;
            }
            switch (alt12) {
                case 1 :
                    // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:949:17: '#' ( F_URI_ALPHA | F_URI_DIGIT | F_URI_OTHER | COLON | '/' | '?' | '#' )*
                    {
                    match('#'); if (state.failed) return ;
                    // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:950:17: ( F_URI_ALPHA | F_URI_DIGIT | F_URI_OTHER | COLON | '/' | '?' | '#' )*
                    loop11:
                    do {
                        int alt11=2;
                        int LA11_0 = input.LA(1);

                        if ( (LA11_0=='!'||(LA11_0>='#' && LA11_0<='$')||(LA11_0>='&' && LA11_0<=';')||LA11_0=='='||(LA11_0>='?' && LA11_0<='[')||LA11_0==']'||LA11_0=='_'||(LA11_0>='a' && LA11_0<='z')||LA11_0=='~') ) {
                            alt11=1;
                        }


                        switch (alt11) {
                    	case 1 :
                    	    // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:
                    	    {
                    	    if ( input.LA(1)=='!'||(input.LA(1)>='#' && input.LA(1)<='$')||(input.LA(1)>='&' && input.LA(1)<=';')||input.LA(1)=='='||(input.LA(1)>='?' && input.LA(1)<='[')||input.LA(1)==']'||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z')||input.LA(1)=='~' ) {
                    	        input.consume();
                    	    state.failed=false;
                    	    }
                    	    else {
                    	        if (state.backtracking>0) {state.failed=true; return ;}
                    	        MismatchedSetException mse = new MismatchedSetException(null,input);
                    	        recover(mse);
                    	        throw mse;}


                    	    }
                    	    break;

                    	default :
                    	    break loop11;
                        }
                    } while (true);


                    }
                    break;

            }

            match('}'); if (state.failed) return ;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "URI"

    // $ANTLR start "F_URI_ALPHA"
    public final void mF_URI_ALPHA() throws RecognitionException {
        try {
            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:965:9: ( 'A' .. 'Z' | 'a' .. 'z' )
            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:
            {
            if ( (input.LA(1)>='A' && input.LA(1)<='Z')||(input.LA(1)>='a' && input.LA(1)<='z') ) {
                input.consume();
            state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

        }
        finally {
        }
    }
    // $ANTLR end "F_URI_ALPHA"

    // $ANTLR start "F_URI_DIGIT"
    public final void mF_URI_DIGIT() throws RecognitionException {
        try {
            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:972:9: ( '0' .. '9' )
            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:973:9: '0' .. '9'
            {
            matchRange('0','9'); if (state.failed) return ;

            }

        }
        finally {
        }
    }
    // $ANTLR end "F_URI_DIGIT"

    // $ANTLR start "F_URI_ESC"
    public final void mF_URI_ESC() throws RecognitionException {
        try {
            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:978:9: ( '%' F_HEX F_HEX )
            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:979:9: '%' F_HEX F_HEX
            {
            match('%'); if (state.failed) return ;
            mF_HEX(); if (state.failed) return ;
            mF_HEX(); if (state.failed) return ;

            }

        }
        finally {
        }
    }
    // $ANTLR end "F_URI_ESC"

    // $ANTLR start "F_URI_OTHER"
    public final void mF_URI_OTHER() throws RecognitionException {
        try {
            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:984:9: ( '-' | '.' | '_' | '~' | '[' | ']' | '@' | '!' | '$' | '&' | '\\'' | '(' | ')' | '*' | '+' | ',' | ';' | '=' )
            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:
            {
            if ( input.LA(1)=='!'||input.LA(1)=='$'||(input.LA(1)>='&' && input.LA(1)<='.')||input.LA(1)==';'||input.LA(1)=='='||input.LA(1)=='@'||input.LA(1)=='['||input.LA(1)==']'||input.LA(1)=='_'||input.LA(1)=='~' ) {
                input.consume();
            state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

        }
        finally {
        }
    }
    // $ANTLR end "F_URI_OTHER"

    // $ANTLR start "OR"
    public final void mOR() throws RecognitionException {
        try {
            int _type = OR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1010:9: ( ( 'O' | 'o' ) ( 'R' | 'r' ) )
            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1011:9: ( 'O' | 'o' ) ( 'R' | 'r' )
            {
            if ( input.LA(1)=='O'||input.LA(1)=='o' ) {
                input.consume();
            state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='R'||input.LA(1)=='r' ) {
                input.consume();
            state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "OR"

    // $ANTLR start "AND"
    public final void mAND() throws RecognitionException {
        try {
            int _type = AND;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1022:9: ( ( 'A' | 'a' ) ( 'N' | 'n' ) ( 'D' | 'd' ) )
            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1023:9: ( 'A' | 'a' ) ( 'N' | 'n' ) ( 'D' | 'd' )
            {
            if ( input.LA(1)=='A'||input.LA(1)=='a' ) {
                input.consume();
            state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='N'||input.LA(1)=='n' ) {
                input.consume();
            state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='D'||input.LA(1)=='d' ) {
                input.consume();
            state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "AND"

    // $ANTLR start "NOT"
    public final void mNOT() throws RecognitionException {
        try {
            int _type = NOT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1038:9: ( ( 'N' | 'n' ) ( 'O' | 'o' ) ( 'T' | 't' ) )
            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1039:9: ( 'N' | 'n' ) ( 'O' | 'o' ) ( 'T' | 't' )
            {
            if ( input.LA(1)=='N'||input.LA(1)=='n' ) {
                input.consume();
            state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='O'||input.LA(1)=='o' ) {
                input.consume();
            state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='T'||input.LA(1)=='t' ) {
                input.consume();
            state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "NOT"

    // $ANTLR start "TILDA"
    public final void mTILDA() throws RecognitionException {
        try {
            int _type = TILDA;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1054:9: ( '~' )
            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1055:9: '~'
            {
            match('~'); if (state.failed) return ;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "TILDA"

    // $ANTLR start "LPAREN"
    public final void mLPAREN() throws RecognitionException {
        try {
            int _type = LPAREN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1059:9: ( '(' )
            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1060:9: '('
            {
            match('('); if (state.failed) return ;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LPAREN"

    // $ANTLR start "RPAREN"
    public final void mRPAREN() throws RecognitionException {
        try {
            int _type = RPAREN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1064:9: ( ')' )
            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1065:9: ')'
            {
            match(')'); if (state.failed) return ;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RPAREN"

    // $ANTLR start "PLUS"
    public final void mPLUS() throws RecognitionException {
        try {
            int _type = PLUS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1069:9: ( '+' )
            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1070:9: '+'
            {
            match('+'); if (state.failed) return ;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "PLUS"

    // $ANTLR start "MINUS"
    public final void mMINUS() throws RecognitionException {
        try {
            int _type = MINUS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1074:9: ( '-' )
            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1075:9: '-'
            {
            match('-'); if (state.failed) return ;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "MINUS"

    // $ANTLR start "COLON"
    public final void mCOLON() throws RecognitionException {
        try {
            int _type = COLON;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1079:9: ( ':' )
            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1080:9: ':'
            {
            match(':'); if (state.failed) return ;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "COLON"

    // $ANTLR start "STAR"
    public final void mSTAR() throws RecognitionException {
        try {
            int _type = STAR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1084:9: ( '*' )
            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1085:9: '*'
            {
            match('*'); if (state.failed) return ;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "STAR"

    // $ANTLR start "DOTDOT"
    public final void mDOTDOT() throws RecognitionException {
        try {
            int _type = DOTDOT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1089:9: ( '..' )
            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1090:9: '..'
            {
            match(".."); if (state.failed) return ;


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "DOTDOT"

    // $ANTLR start "DOT"
    public final void mDOT() throws RecognitionException {
        try {
            int _type = DOT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1094:9: ( '.' )
            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1095:9: '.'
            {
            match('.'); if (state.failed) return ;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "DOT"

    // $ANTLR start "AMP"
    public final void mAMP() throws RecognitionException {
        try {
            int _type = AMP;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1099:9: ( '&' )
            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1100:9: '&'
            {
            match('&'); if (state.failed) return ;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "AMP"

    // $ANTLR start "EXCLAMATION"
    public final void mEXCLAMATION() throws RecognitionException {
        try {
            int _type = EXCLAMATION;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1104:9: ( '!' )
            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1105:9: '!'
            {
            match('!'); if (state.failed) return ;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "EXCLAMATION"

    // $ANTLR start "BAR"
    public final void mBAR() throws RecognitionException {
        try {
            int _type = BAR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1109:9: ( '|' )
            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1110:9: '|'
            {
            match('|'); if (state.failed) return ;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "BAR"

    // $ANTLR start "EQUALS"
    public final void mEQUALS() throws RecognitionException {
        try {
            int _type = EQUALS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1114:9: ( '=' )
            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1115:9: '='
            {
            match('='); if (state.failed) return ;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "EQUALS"

    // $ANTLR start "QUESTION_MARK"
    public final void mQUESTION_MARK() throws RecognitionException {
        try {
            int _type = QUESTION_MARK;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1119:9: ( '?' )
            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1120:9: '?'
            {
            match('?'); if (state.failed) return ;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "QUESTION_MARK"

    // $ANTLR start "LCURL"
    public final void mLCURL() throws RecognitionException {
        try {
            int _type = LCURL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1124:9: ( '{' )
            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1125:9: '{'
            {
            match('{'); if (state.failed) return ;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LCURL"

    // $ANTLR start "RCURL"
    public final void mRCURL() throws RecognitionException {
        try {
            int _type = RCURL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1129:9: ( '}' )
            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1130:9: '}'
            {
            match('}'); if (state.failed) return ;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RCURL"

    // $ANTLR start "LSQUARE"
    public final void mLSQUARE() throws RecognitionException {
        try {
            int _type = LSQUARE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1134:9: ( '[' )
            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1135:9: '['
            {
            match('['); if (state.failed) return ;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LSQUARE"

    // $ANTLR start "RSQUARE"
    public final void mRSQUARE() throws RecognitionException {
        try {
            int _type = RSQUARE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1139:9: ( ']' )
            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1140:9: ']'
            {
            match(']'); if (state.failed) return ;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RSQUARE"

    // $ANTLR start "TO"
    public final void mTO() throws RecognitionException {
        try {
            int _type = TO;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1144:9: ( ( 'T' | 't' ) ( 'O' | 'o' ) )
            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1145:9: ( 'T' | 't' ) ( 'O' | 'o' )
            {
            if ( input.LA(1)=='T'||input.LA(1)=='t' ) {
                input.consume();
            state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='O'||input.LA(1)=='o' ) {
                input.consume();
            state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "TO"

    // $ANTLR start "COMMA"
    public final void mCOMMA() throws RecognitionException {
        try {
            int _type = COMMA;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1156:9: ( ',' )
            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1157:9: ','
            {
            match(','); if (state.failed) return ;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "COMMA"

    // $ANTLR start "CARAT"
    public final void mCARAT() throws RecognitionException {
        try {
            int _type = CARAT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1161:9: ( '^' )
            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1162:9: '^'
            {
            match('^'); if (state.failed) return ;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "CARAT"

    // $ANTLR start "DOLLAR"
    public final void mDOLLAR() throws RecognitionException {
        try {
            int _type = DOLLAR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1166:9: ( '$' )
            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1167:9: '$'
            {
            match('$'); if (state.failed) return ;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "DOLLAR"

    // $ANTLR start "GT"
    public final void mGT() throws RecognitionException {
        try {
            int _type = GT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1171:9: ( '>' )
            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1172:9: '>'
            {
            match('>'); if (state.failed) return ;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "GT"

    // $ANTLR start "LT"
    public final void mLT() throws RecognitionException {
        try {
            int _type = LT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1176:9: ( '<' )
            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1177:9: '<'
            {
            match('<'); if (state.failed) return ;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LT"

    // $ANTLR start "AT"
    public final void mAT() throws RecognitionException {
        try {
            int _type = AT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1181:9: ( '@' )
            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1182:9: '@'
            {
            match('@'); if (state.failed) return ;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "AT"

    // $ANTLR start "PERCENT"
    public final void mPERCENT() throws RecognitionException {
        try {
            int _type = PERCENT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1186:9: ( '%' )
            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1187:9: '%'
            {
            match('%'); if (state.failed) return ;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "PERCENT"

    // $ANTLR start "ID"
    public final void mID() throws RecognitionException {
        try {
            int _type = ID;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1196:9: ( ( 'a' .. 'z' | 'A' .. 'Z' | '_' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' | '$' | '#' | F_ESC )* )
            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1197:9: ( 'a' .. 'z' | 'A' .. 'Z' | '_' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' | '$' | '#' | F_ESC )*
            {
            if ( (input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z') ) {
                input.consume();
            state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1202:9: ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' | '$' | '#' | F_ESC )*
            loop13:
            do {
                int alt13=8;
                switch ( input.LA(1) ) {
                case 'a':
                case 'b':
                case 'c':
                case 'd':
                case 'e':
                case 'f':
                case 'g':
                case 'h':
                case 'i':
                case 'j':
                case 'k':
                case 'l':
                case 'm':
                case 'n':
                case 'o':
                case 'p':
                case 'q':
                case 'r':
                case 's':
                case 't':
                case 'u':
                case 'v':
                case 'w':
                case 'x':
                case 'y':
                case 'z':
                    {
                    alt13=1;
                    }
                    break;
                case 'A':
                case 'B':
                case 'C':
                case 'D':
                case 'E':
                case 'F':
                case 'G':
                case 'H':
                case 'I':
                case 'J':
                case 'K':
                case 'L':
                case 'M':
                case 'N':
                case 'O':
                case 'P':
                case 'Q':
                case 'R':
                case 'S':
                case 'T':
                case 'U':
                case 'V':
                case 'W':
                case 'X':
                case 'Y':
                case 'Z':
                    {
                    alt13=2;
                    }
                    break;
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    {
                    alt13=3;
                    }
                    break;
                case '_':
                    {
                    alt13=4;
                    }
                    break;
                case '$':
                    {
                    alt13=5;
                    }
                    break;
                case '#':
                    {
                    alt13=6;
                    }
                    break;
                case '\\':
                    {
                    alt13=7;
                    }
                    break;

                }

                switch (alt13) {
            	case 1 :
            	    // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1203:17: 'a' .. 'z'
            	    {
            	    matchRange('a','z'); if (state.failed) return ;

            	    }
            	    break;
            	case 2 :
            	    // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1204:19: 'A' .. 'Z'
            	    {
            	    matchRange('A','Z'); if (state.failed) return ;

            	    }
            	    break;
            	case 3 :
            	    // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1205:19: '0' .. '9'
            	    {
            	    matchRange('0','9'); if (state.failed) return ;

            	    }
            	    break;
            	case 4 :
            	    // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1206:19: '_'
            	    {
            	    match('_'); if (state.failed) return ;

            	    }
            	    break;
            	case 5 :
            	    // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1207:19: '$'
            	    {
            	    match('$'); if (state.failed) return ;

            	    }
            	    break;
            	case 6 :
            	    // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1208:19: '#'
            	    {
            	    match('#'); if (state.failed) return ;

            	    }
            	    break;
            	case 7 :
            	    // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1209:19: F_ESC
            	    {
            	    mF_ESC(); if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop13;
                }
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "ID"

    // $ANTLR start "DECIMAL_INTEGER_LITERAL"
    public final void mDECIMAL_INTEGER_LITERAL() throws RecognitionException {
        try {
            int _type = DECIMAL_INTEGER_LITERAL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1214:9: ( ( PLUS | MINUS )? DECIMAL_NUMERAL )
            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1215:9: ( PLUS | MINUS )? DECIMAL_NUMERAL
            {
            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1215:9: ( PLUS | MINUS )?
            int alt14=2;
            int LA14_0 = input.LA(1);

            if ( (LA14_0=='+'||LA14_0=='-') ) {
                alt14=1;
            }
            switch (alt14) {
                case 1 :
                    // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:
                    {
                    if ( input.LA(1)=='+'||input.LA(1)=='-' ) {
                        input.consume();
                    state.failed=false;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return ;}
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        recover(mse);
                        throw mse;}


                    }
                    break;

            }

            mDECIMAL_NUMERAL(); if (state.failed) return ;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "DECIMAL_INTEGER_LITERAL"

    // $ANTLR start "FTSWORD"
    public final void mFTSWORD() throws RecognitionException {
        try {
            int _type = FTSWORD;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1223:9: ( ( F_ESC | INWORD )+ )
            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1224:9: ( F_ESC | INWORD )+
            {
            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1224:9: ( F_ESC | INWORD )+
            int cnt15=0;
            loop15:
            do {
                int alt15=3;
                int LA15_0 = input.LA(1);

                if ( (LA15_0=='\\') ) {
                    alt15=1;
                }
                else if ( ((LA15_0>='0' && LA15_0<='9')||(LA15_0>='A' && LA15_0<='Z')||(LA15_0>='a' && LA15_0<='z')||LA15_0=='\u00AA'||LA15_0=='\u00B5'||LA15_0=='\u00BA'||(LA15_0>='\u00C0' && LA15_0<='\u00D6')||(LA15_0>='\u00D8' && LA15_0<='\u00F6')||(LA15_0>='\u00F8' && LA15_0<='\u0236')||(LA15_0>='\u0250' && LA15_0<='\u02C1')||(LA15_0>='\u02C6' && LA15_0<='\u02D1')||(LA15_0>='\u02E0' && LA15_0<='\u02E4')||LA15_0=='\u02EE'||LA15_0=='\u037A'||LA15_0=='\u0386'||(LA15_0>='\u0388' && LA15_0<='\u038A')||LA15_0=='\u038C'||(LA15_0>='\u038E' && LA15_0<='\u03A1')||(LA15_0>='\u03A3' && LA15_0<='\u03CE')||(LA15_0>='\u03D0' && LA15_0<='\u03F5')||(LA15_0>='\u03F7' && LA15_0<='\u03FB')||(LA15_0>='\u0400' && LA15_0<='\u0481')||(LA15_0>='\u048A' && LA15_0<='\u04CE')||(LA15_0>='\u04D0' && LA15_0<='\u04F5')||(LA15_0>='\u04F8' && LA15_0<='\u04F9')||(LA15_0>='\u0500' && LA15_0<='\u050F')||(LA15_0>='\u0531' && LA15_0<='\u0556')||LA15_0=='\u0559'||(LA15_0>='\u0561' && LA15_0<='\u0587')||(LA15_0>='\u05D0' && LA15_0<='\u05EA')||(LA15_0>='\u05F0' && LA15_0<='\u05F2')||(LA15_0>='\u0621' && LA15_0<='\u063A')||(LA15_0>='\u0640' && LA15_0<='\u064A')||(LA15_0>='\u0660' && LA15_0<='\u0669')||(LA15_0>='\u066E' && LA15_0<='\u066F')||(LA15_0>='\u0671' && LA15_0<='\u06D3')||LA15_0=='\u06D5'||(LA15_0>='\u06E5' && LA15_0<='\u06E6')||(LA15_0>='\u06EE' && LA15_0<='\u06FC')||LA15_0=='\u06FF'||LA15_0=='\u0710'||(LA15_0>='\u0712' && LA15_0<='\u072F')||(LA15_0>='\u074D' && LA15_0<='\u074F')||(LA15_0>='\u0780' && LA15_0<='\u07A5')||LA15_0=='\u07B1'||(LA15_0>='\u0904' && LA15_0<='\u0939')||LA15_0=='\u093D'||LA15_0=='\u0950'||(LA15_0>='\u0958' && LA15_0<='\u0961')||(LA15_0>='\u0966' && LA15_0<='\u096F')||(LA15_0>='\u0985' && LA15_0<='\u098C')||(LA15_0>='\u098F' && LA15_0<='\u0990')||(LA15_0>='\u0993' && LA15_0<='\u09A8')||(LA15_0>='\u09AA' && LA15_0<='\u09B0')||LA15_0=='\u09B2'||(LA15_0>='\u09B6' && LA15_0<='\u09B9')||LA15_0=='\u09BD'||(LA15_0>='\u09DC' && LA15_0<='\u09DD')||(LA15_0>='\u09DF' && LA15_0<='\u09E1')||(LA15_0>='\u09E6' && LA15_0<='\u09F1')||(LA15_0>='\u0A05' && LA15_0<='\u0A0A')||(LA15_0>='\u0A0F' && LA15_0<='\u0A10')||(LA15_0>='\u0A13' && LA15_0<='\u0A28')||(LA15_0>='\u0A2A' && LA15_0<='\u0A30')||(LA15_0>='\u0A32' && LA15_0<='\u0A33')||(LA15_0>='\u0A35' && LA15_0<='\u0A36')||(LA15_0>='\u0A38' && LA15_0<='\u0A39')||(LA15_0>='\u0A59' && LA15_0<='\u0A5C')||LA15_0=='\u0A5E'||(LA15_0>='\u0A66' && LA15_0<='\u0A6F')||(LA15_0>='\u0A72' && LA15_0<='\u0A74')||(LA15_0>='\u0A85' && LA15_0<='\u0A8D')||(LA15_0>='\u0A8F' && LA15_0<='\u0A91')||(LA15_0>='\u0A93' && LA15_0<='\u0AA8')||(LA15_0>='\u0AAA' && LA15_0<='\u0AB0')||(LA15_0>='\u0AB2' && LA15_0<='\u0AB3')||(LA15_0>='\u0AB5' && LA15_0<='\u0AB9')||LA15_0=='\u0ABD'||LA15_0=='\u0AD0'||(LA15_0>='\u0AE0' && LA15_0<='\u0AE1')||(LA15_0>='\u0AE6' && LA15_0<='\u0AEF')||(LA15_0>='\u0B05' && LA15_0<='\u0B0C')||(LA15_0>='\u0B0F' && LA15_0<='\u0B10')||(LA15_0>='\u0B13' && LA15_0<='\u0B28')||(LA15_0>='\u0B2A' && LA15_0<='\u0B30')||(LA15_0>='\u0B32' && LA15_0<='\u0B33')||(LA15_0>='\u0B35' && LA15_0<='\u0B39')||LA15_0=='\u0B3D'||(LA15_0>='\u0B5C' && LA15_0<='\u0B5D')||(LA15_0>='\u0B5F' && LA15_0<='\u0B61')||(LA15_0>='\u0B66' && LA15_0<='\u0B6F')||LA15_0=='\u0B71'||LA15_0=='\u0B83'||(LA15_0>='\u0B85' && LA15_0<='\u0B8A')||(LA15_0>='\u0B8E' && LA15_0<='\u0B90')||(LA15_0>='\u0B92' && LA15_0<='\u0B95')||(LA15_0>='\u0B99' && LA15_0<='\u0B9A')||LA15_0=='\u0B9C'||(LA15_0>='\u0B9E' && LA15_0<='\u0B9F')||(LA15_0>='\u0BA3' && LA15_0<='\u0BA4')||(LA15_0>='\u0BA8' && LA15_0<='\u0BAA')||(LA15_0>='\u0BAE' && LA15_0<='\u0BB5')||(LA15_0>='\u0BB7' && LA15_0<='\u0BB9')||(LA15_0>='\u0BE7' && LA15_0<='\u0BEF')||(LA15_0>='\u0C05' && LA15_0<='\u0C0C')||(LA15_0>='\u0C0E' && LA15_0<='\u0C10')||(LA15_0>='\u0C12' && LA15_0<='\u0C28')||(LA15_0>='\u0C2A' && LA15_0<='\u0C33')||(LA15_0>='\u0C35' && LA15_0<='\u0C39')||(LA15_0>='\u0C60' && LA15_0<='\u0C61')||(LA15_0>='\u0C66' && LA15_0<='\u0C6F')||(LA15_0>='\u0C85' && LA15_0<='\u0C8C')||(LA15_0>='\u0C8E' && LA15_0<='\u0C90')||(LA15_0>='\u0C92' && LA15_0<='\u0CA8')||(LA15_0>='\u0CAA' && LA15_0<='\u0CB3')||(LA15_0>='\u0CB5' && LA15_0<='\u0CB9')||LA15_0=='\u0CBD'||LA15_0=='\u0CDE'||(LA15_0>='\u0CE0' && LA15_0<='\u0CE1')||(LA15_0>='\u0CE6' && LA15_0<='\u0CEF')||(LA15_0>='\u0D05' && LA15_0<='\u0D0C')||(LA15_0>='\u0D0E' && LA15_0<='\u0D10')||(LA15_0>='\u0D12' && LA15_0<='\u0D28')||(LA15_0>='\u0D2A' && LA15_0<='\u0D39')||(LA15_0>='\u0D60' && LA15_0<='\u0D61')||(LA15_0>='\u0D66' && LA15_0<='\u0D6F')||(LA15_0>='\u0D85' && LA15_0<='\u0D96')||(LA15_0>='\u0D9A' && LA15_0<='\u0DB1')||(LA15_0>='\u0DB3' && LA15_0<='\u0DBB')||LA15_0=='\u0DBD'||(LA15_0>='\u0DC0' && LA15_0<='\u0DC6')||(LA15_0>='\u0E01' && LA15_0<='\u0E30')||(LA15_0>='\u0E32' && LA15_0<='\u0E33')||(LA15_0>='\u0E40' && LA15_0<='\u0E46')||(LA15_0>='\u0E50' && LA15_0<='\u0E59')||(LA15_0>='\u0E81' && LA15_0<='\u0E82')||LA15_0=='\u0E84'||(LA15_0>='\u0E87' && LA15_0<='\u0E88')||LA15_0=='\u0E8A'||LA15_0=='\u0E8D'||(LA15_0>='\u0E94' && LA15_0<='\u0E97')||(LA15_0>='\u0E99' && LA15_0<='\u0E9F')||(LA15_0>='\u0EA1' && LA15_0<='\u0EA3')||LA15_0=='\u0EA5'||LA15_0=='\u0EA7'||(LA15_0>='\u0EAA' && LA15_0<='\u0EAB')||(LA15_0>='\u0EAD' && LA15_0<='\u0EB0')||(LA15_0>='\u0EB2' && LA15_0<='\u0EB3')||LA15_0=='\u0EBD'||(LA15_0>='\u0EC0' && LA15_0<='\u0EC4')||LA15_0=='\u0EC6'||(LA15_0>='\u0ED0' && LA15_0<='\u0ED9')||(LA15_0>='\u0EDC' && LA15_0<='\u0EDD')||LA15_0=='\u0F00'||(LA15_0>='\u0F20' && LA15_0<='\u0F29')||(LA15_0>='\u0F40' && LA15_0<='\u0F47')||(LA15_0>='\u0F49' && LA15_0<='\u0F6A')||(LA15_0>='\u0F88' && LA15_0<='\u0F8B')||(LA15_0>='\u1000' && LA15_0<='\u1021')||(LA15_0>='\u1023' && LA15_0<='\u1027')||(LA15_0>='\u1029' && LA15_0<='\u102A')||(LA15_0>='\u1040' && LA15_0<='\u1049')||(LA15_0>='\u1050' && LA15_0<='\u1055')||(LA15_0>='\u10A0' && LA15_0<='\u10C5')||(LA15_0>='\u10D0' && LA15_0<='\u10F8')||(LA15_0>='\u1100' && LA15_0<='\u1159')||(LA15_0>='\u115F' && LA15_0<='\u11A2')||(LA15_0>='\u11A8' && LA15_0<='\u11F9')||(LA15_0>='\u1200' && LA15_0<='\u1206')||(LA15_0>='\u1208' && LA15_0<='\u1246')||LA15_0=='\u1248'||(LA15_0>='\u124A' && LA15_0<='\u124D')||(LA15_0>='\u1250' && LA15_0<='\u1256')||LA15_0=='\u1258'||(LA15_0>='\u125A' && LA15_0<='\u125D')||(LA15_0>='\u1260' && LA15_0<='\u1286')||LA15_0=='\u1288'||(LA15_0>='\u128A' && LA15_0<='\u128D')||(LA15_0>='\u1290' && LA15_0<='\u12AE')||LA15_0=='\u12B0'||(LA15_0>='\u12B2' && LA15_0<='\u12B5')||(LA15_0>='\u12B8' && LA15_0<='\u12BE')||LA15_0=='\u12C0'||(LA15_0>='\u12C2' && LA15_0<='\u12C5')||(LA15_0>='\u12C8' && LA15_0<='\u12CE')||(LA15_0>='\u12D0' && LA15_0<='\u12D6')||(LA15_0>='\u12D8' && LA15_0<='\u12EE')||(LA15_0>='\u12F0' && LA15_0<='\u130E')||LA15_0=='\u1310'||(LA15_0>='\u1312' && LA15_0<='\u1315')||(LA15_0>='\u1318' && LA15_0<='\u131E')||(LA15_0>='\u1320' && LA15_0<='\u1346')||(LA15_0>='\u1348' && LA15_0<='\u135A')||(LA15_0>='\u1369' && LA15_0<='\u1371')||(LA15_0>='\u13A0' && LA15_0<='\u13F4')||(LA15_0>='\u1401' && LA15_0<='\u166C')||(LA15_0>='\u166F' && LA15_0<='\u1676')||(LA15_0>='\u1681' && LA15_0<='\u169A')||(LA15_0>='\u16A0' && LA15_0<='\u16EA')||(LA15_0>='\u1700' && LA15_0<='\u170C')||(LA15_0>='\u170E' && LA15_0<='\u1711')||(LA15_0>='\u1720' && LA15_0<='\u1731')||(LA15_0>='\u1740' && LA15_0<='\u1751')||(LA15_0>='\u1760' && LA15_0<='\u176C')||(LA15_0>='\u176E' && LA15_0<='\u1770')||(LA15_0>='\u1780' && LA15_0<='\u17B3')||LA15_0=='\u17D7'||LA15_0=='\u17DC'||(LA15_0>='\u17E0' && LA15_0<='\u17E9')||(LA15_0>='\u1810' && LA15_0<='\u1819')||(LA15_0>='\u1820' && LA15_0<='\u1877')||(LA15_0>='\u1880' && LA15_0<='\u18A8')||(LA15_0>='\u1900' && LA15_0<='\u191C')||(LA15_0>='\u1946' && LA15_0<='\u196D')||(LA15_0>='\u1970' && LA15_0<='\u1974')||(LA15_0>='\u1D00' && LA15_0<='\u1D6B')||(LA15_0>='\u1E00' && LA15_0<='\u1E9B')||(LA15_0>='\u1EA0' && LA15_0<='\u1EF9')||(LA15_0>='\u1F00' && LA15_0<='\u1F15')||(LA15_0>='\u1F18' && LA15_0<='\u1F1D')||(LA15_0>='\u1F20' && LA15_0<='\u1F45')||(LA15_0>='\u1F48' && LA15_0<='\u1F4D')||(LA15_0>='\u1F50' && LA15_0<='\u1F57')||LA15_0=='\u1F59'||LA15_0=='\u1F5B'||LA15_0=='\u1F5D'||(LA15_0>='\u1F5F' && LA15_0<='\u1F7D')||(LA15_0>='\u1F80' && LA15_0<='\u1FB4')||(LA15_0>='\u1FB6' && LA15_0<='\u1FBC')||LA15_0=='\u1FBE'||(LA15_0>='\u1FC2' && LA15_0<='\u1FC4')||(LA15_0>='\u1FC6' && LA15_0<='\u1FCC')||(LA15_0>='\u1FD0' && LA15_0<='\u1FD3')||(LA15_0>='\u1FD6' && LA15_0<='\u1FDB')||(LA15_0>='\u1FE0' && LA15_0<='\u1FEC')||(LA15_0>='\u1FF2' && LA15_0<='\u1FF4')||(LA15_0>='\u1FF6' && LA15_0<='\u1FFC')||LA15_0=='\u2071'||LA15_0=='\u207F'||LA15_0=='\u2102'||LA15_0=='\u2107'||(LA15_0>='\u210A' && LA15_0<='\u2113')||LA15_0=='\u2115'||(LA15_0>='\u2119' && LA15_0<='\u211D')||LA15_0=='\u2124'||LA15_0=='\u2126'||LA15_0=='\u2128'||(LA15_0>='\u212A' && LA15_0<='\u212D')||(LA15_0>='\u212F' && LA15_0<='\u2131')||(LA15_0>='\u2133' && LA15_0<='\u2139')||(LA15_0>='\u213D' && LA15_0<='\u213F')||(LA15_0>='\u2145' && LA15_0<='\u2149')||(LA15_0>='\u3005' && LA15_0<='\u3006')||(LA15_0>='\u3031' && LA15_0<='\u3035')||(LA15_0>='\u303B' && LA15_0<='\u303C')||(LA15_0>='\u3041' && LA15_0<='\u3096')||(LA15_0>='\u309D' && LA15_0<='\u309F')||(LA15_0>='\u30A1' && LA15_0<='\u30FA')||(LA15_0>='\u30FC' && LA15_0<='\u30FF')||(LA15_0>='\u3105' && LA15_0<='\u312C')||(LA15_0>='\u3131' && LA15_0<='\u318E')||(LA15_0>='\u31A0' && LA15_0<='\u31B7')||(LA15_0>='\u31F0' && LA15_0<='\u31FF')||(LA15_0>='\u3400' && LA15_0<='\u4DB5')||(LA15_0>='\u4E00' && LA15_0<='\u9FA5')||(LA15_0>='\uA000' && LA15_0<='\uA48C')||(LA15_0>='\uAC00' && LA15_0<='\uD7A3')||(LA15_0>='\uF900' && LA15_0<='\uFA2D')||(LA15_0>='\uFA30' && LA15_0<='\uFA6A')||(LA15_0>='\uFB00' && LA15_0<='\uFB06')||(LA15_0>='\uFB13' && LA15_0<='\uFB17')||LA15_0=='\uFB1D'||(LA15_0>='\uFB1F' && LA15_0<='\uFB28')||(LA15_0>='\uFB2A' && LA15_0<='\uFB36')||(LA15_0>='\uFB38' && LA15_0<='\uFB3C')||LA15_0=='\uFB3E'||(LA15_0>='\uFB40' && LA15_0<='\uFB41')||(LA15_0>='\uFB43' && LA15_0<='\uFB44')||(LA15_0>='\uFB46' && LA15_0<='\uFBB1')||(LA15_0>='\uFBD3' && LA15_0<='\uFD3D')||(LA15_0>='\uFD50' && LA15_0<='\uFD8F')||(LA15_0>='\uFD92' && LA15_0<='\uFDC7')||(LA15_0>='\uFDF0' && LA15_0<='\uFDFB')||(LA15_0>='\uFE70' && LA15_0<='\uFE74')||(LA15_0>='\uFE76' && LA15_0<='\uFEFC')||(LA15_0>='\uFF10' && LA15_0<='\uFF19')||(LA15_0>='\uFF21' && LA15_0<='\uFF3A')||(LA15_0>='\uFF41' && LA15_0<='\uFF5A')||(LA15_0>='\uFF66' && LA15_0<='\uFFBE')||(LA15_0>='\uFFC2' && LA15_0<='\uFFC7')||(LA15_0>='\uFFCA' && LA15_0<='\uFFCF')||(LA15_0>='\uFFD2' && LA15_0<='\uFFD7')||(LA15_0>='\uFFDA' && LA15_0<='\uFFDC')) ) {
                    alt15=2;
                }


                switch (alt15) {
            	case 1 :
            	    // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1225:17: F_ESC
            	    {
            	    mF_ESC(); if (state.failed) return ;

            	    }
            	    break;
            	case 2 :
            	    // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1226:19: INWORD
            	    {
            	    mINWORD(); if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    if ( cnt15 >= 1 ) break loop15;
            	    if (state.backtracking>0) {state.failed=true; return ;}
                        EarlyExitException eee =
                            new EarlyExitException(15, input);
                        throw eee;
                }
                cnt15++;
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "FTSWORD"

    // $ANTLR start "FTSPRE"
    public final void mFTSPRE() throws RecognitionException {
        try {
            int _type = FTSPRE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1231:9: ( ( F_ESC | INWORD )+ STAR )
            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1232:9: ( F_ESC | INWORD )+ STAR
            {
            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1232:9: ( F_ESC | INWORD )+
            int cnt16=0;
            loop16:
            do {
                int alt16=3;
                int LA16_0 = input.LA(1);

                if ( (LA16_0=='\\') ) {
                    alt16=1;
                }
                else if ( ((LA16_0>='0' && LA16_0<='9')||(LA16_0>='A' && LA16_0<='Z')||(LA16_0>='a' && LA16_0<='z')||LA16_0=='\u00AA'||LA16_0=='\u00B5'||LA16_0=='\u00BA'||(LA16_0>='\u00C0' && LA16_0<='\u00D6')||(LA16_0>='\u00D8' && LA16_0<='\u00F6')||(LA16_0>='\u00F8' && LA16_0<='\u0236')||(LA16_0>='\u0250' && LA16_0<='\u02C1')||(LA16_0>='\u02C6' && LA16_0<='\u02D1')||(LA16_0>='\u02E0' && LA16_0<='\u02E4')||LA16_0=='\u02EE'||LA16_0=='\u037A'||LA16_0=='\u0386'||(LA16_0>='\u0388' && LA16_0<='\u038A')||LA16_0=='\u038C'||(LA16_0>='\u038E' && LA16_0<='\u03A1')||(LA16_0>='\u03A3' && LA16_0<='\u03CE')||(LA16_0>='\u03D0' && LA16_0<='\u03F5')||(LA16_0>='\u03F7' && LA16_0<='\u03FB')||(LA16_0>='\u0400' && LA16_0<='\u0481')||(LA16_0>='\u048A' && LA16_0<='\u04CE')||(LA16_0>='\u04D0' && LA16_0<='\u04F5')||(LA16_0>='\u04F8' && LA16_0<='\u04F9')||(LA16_0>='\u0500' && LA16_0<='\u050F')||(LA16_0>='\u0531' && LA16_0<='\u0556')||LA16_0=='\u0559'||(LA16_0>='\u0561' && LA16_0<='\u0587')||(LA16_0>='\u05D0' && LA16_0<='\u05EA')||(LA16_0>='\u05F0' && LA16_0<='\u05F2')||(LA16_0>='\u0621' && LA16_0<='\u063A')||(LA16_0>='\u0640' && LA16_0<='\u064A')||(LA16_0>='\u0660' && LA16_0<='\u0669')||(LA16_0>='\u066E' && LA16_0<='\u066F')||(LA16_0>='\u0671' && LA16_0<='\u06D3')||LA16_0=='\u06D5'||(LA16_0>='\u06E5' && LA16_0<='\u06E6')||(LA16_0>='\u06EE' && LA16_0<='\u06FC')||LA16_0=='\u06FF'||LA16_0=='\u0710'||(LA16_0>='\u0712' && LA16_0<='\u072F')||(LA16_0>='\u074D' && LA16_0<='\u074F')||(LA16_0>='\u0780' && LA16_0<='\u07A5')||LA16_0=='\u07B1'||(LA16_0>='\u0904' && LA16_0<='\u0939')||LA16_0=='\u093D'||LA16_0=='\u0950'||(LA16_0>='\u0958' && LA16_0<='\u0961')||(LA16_0>='\u0966' && LA16_0<='\u096F')||(LA16_0>='\u0985' && LA16_0<='\u098C')||(LA16_0>='\u098F' && LA16_0<='\u0990')||(LA16_0>='\u0993' && LA16_0<='\u09A8')||(LA16_0>='\u09AA' && LA16_0<='\u09B0')||LA16_0=='\u09B2'||(LA16_0>='\u09B6' && LA16_0<='\u09B9')||LA16_0=='\u09BD'||(LA16_0>='\u09DC' && LA16_0<='\u09DD')||(LA16_0>='\u09DF' && LA16_0<='\u09E1')||(LA16_0>='\u09E6' && LA16_0<='\u09F1')||(LA16_0>='\u0A05' && LA16_0<='\u0A0A')||(LA16_0>='\u0A0F' && LA16_0<='\u0A10')||(LA16_0>='\u0A13' && LA16_0<='\u0A28')||(LA16_0>='\u0A2A' && LA16_0<='\u0A30')||(LA16_0>='\u0A32' && LA16_0<='\u0A33')||(LA16_0>='\u0A35' && LA16_0<='\u0A36')||(LA16_0>='\u0A38' && LA16_0<='\u0A39')||(LA16_0>='\u0A59' && LA16_0<='\u0A5C')||LA16_0=='\u0A5E'||(LA16_0>='\u0A66' && LA16_0<='\u0A6F')||(LA16_0>='\u0A72' && LA16_0<='\u0A74')||(LA16_0>='\u0A85' && LA16_0<='\u0A8D')||(LA16_0>='\u0A8F' && LA16_0<='\u0A91')||(LA16_0>='\u0A93' && LA16_0<='\u0AA8')||(LA16_0>='\u0AAA' && LA16_0<='\u0AB0')||(LA16_0>='\u0AB2' && LA16_0<='\u0AB3')||(LA16_0>='\u0AB5' && LA16_0<='\u0AB9')||LA16_0=='\u0ABD'||LA16_0=='\u0AD0'||(LA16_0>='\u0AE0' && LA16_0<='\u0AE1')||(LA16_0>='\u0AE6' && LA16_0<='\u0AEF')||(LA16_0>='\u0B05' && LA16_0<='\u0B0C')||(LA16_0>='\u0B0F' && LA16_0<='\u0B10')||(LA16_0>='\u0B13' && LA16_0<='\u0B28')||(LA16_0>='\u0B2A' && LA16_0<='\u0B30')||(LA16_0>='\u0B32' && LA16_0<='\u0B33')||(LA16_0>='\u0B35' && LA16_0<='\u0B39')||LA16_0=='\u0B3D'||(LA16_0>='\u0B5C' && LA16_0<='\u0B5D')||(LA16_0>='\u0B5F' && LA16_0<='\u0B61')||(LA16_0>='\u0B66' && LA16_0<='\u0B6F')||LA16_0=='\u0B71'||LA16_0=='\u0B83'||(LA16_0>='\u0B85' && LA16_0<='\u0B8A')||(LA16_0>='\u0B8E' && LA16_0<='\u0B90')||(LA16_0>='\u0B92' && LA16_0<='\u0B95')||(LA16_0>='\u0B99' && LA16_0<='\u0B9A')||LA16_0=='\u0B9C'||(LA16_0>='\u0B9E' && LA16_0<='\u0B9F')||(LA16_0>='\u0BA3' && LA16_0<='\u0BA4')||(LA16_0>='\u0BA8' && LA16_0<='\u0BAA')||(LA16_0>='\u0BAE' && LA16_0<='\u0BB5')||(LA16_0>='\u0BB7' && LA16_0<='\u0BB9')||(LA16_0>='\u0BE7' && LA16_0<='\u0BEF')||(LA16_0>='\u0C05' && LA16_0<='\u0C0C')||(LA16_0>='\u0C0E' && LA16_0<='\u0C10')||(LA16_0>='\u0C12' && LA16_0<='\u0C28')||(LA16_0>='\u0C2A' && LA16_0<='\u0C33')||(LA16_0>='\u0C35' && LA16_0<='\u0C39')||(LA16_0>='\u0C60' && LA16_0<='\u0C61')||(LA16_0>='\u0C66' && LA16_0<='\u0C6F')||(LA16_0>='\u0C85' && LA16_0<='\u0C8C')||(LA16_0>='\u0C8E' && LA16_0<='\u0C90')||(LA16_0>='\u0C92' && LA16_0<='\u0CA8')||(LA16_0>='\u0CAA' && LA16_0<='\u0CB3')||(LA16_0>='\u0CB5' && LA16_0<='\u0CB9')||LA16_0=='\u0CBD'||LA16_0=='\u0CDE'||(LA16_0>='\u0CE0' && LA16_0<='\u0CE1')||(LA16_0>='\u0CE6' && LA16_0<='\u0CEF')||(LA16_0>='\u0D05' && LA16_0<='\u0D0C')||(LA16_0>='\u0D0E' && LA16_0<='\u0D10')||(LA16_0>='\u0D12' && LA16_0<='\u0D28')||(LA16_0>='\u0D2A' && LA16_0<='\u0D39')||(LA16_0>='\u0D60' && LA16_0<='\u0D61')||(LA16_0>='\u0D66' && LA16_0<='\u0D6F')||(LA16_0>='\u0D85' && LA16_0<='\u0D96')||(LA16_0>='\u0D9A' && LA16_0<='\u0DB1')||(LA16_0>='\u0DB3' && LA16_0<='\u0DBB')||LA16_0=='\u0DBD'||(LA16_0>='\u0DC0' && LA16_0<='\u0DC6')||(LA16_0>='\u0E01' && LA16_0<='\u0E30')||(LA16_0>='\u0E32' && LA16_0<='\u0E33')||(LA16_0>='\u0E40' && LA16_0<='\u0E46')||(LA16_0>='\u0E50' && LA16_0<='\u0E59')||(LA16_0>='\u0E81' && LA16_0<='\u0E82')||LA16_0=='\u0E84'||(LA16_0>='\u0E87' && LA16_0<='\u0E88')||LA16_0=='\u0E8A'||LA16_0=='\u0E8D'||(LA16_0>='\u0E94' && LA16_0<='\u0E97')||(LA16_0>='\u0E99' && LA16_0<='\u0E9F')||(LA16_0>='\u0EA1' && LA16_0<='\u0EA3')||LA16_0=='\u0EA5'||LA16_0=='\u0EA7'||(LA16_0>='\u0EAA' && LA16_0<='\u0EAB')||(LA16_0>='\u0EAD' && LA16_0<='\u0EB0')||(LA16_0>='\u0EB2' && LA16_0<='\u0EB3')||LA16_0=='\u0EBD'||(LA16_0>='\u0EC0' && LA16_0<='\u0EC4')||LA16_0=='\u0EC6'||(LA16_0>='\u0ED0' && LA16_0<='\u0ED9')||(LA16_0>='\u0EDC' && LA16_0<='\u0EDD')||LA16_0=='\u0F00'||(LA16_0>='\u0F20' && LA16_0<='\u0F29')||(LA16_0>='\u0F40' && LA16_0<='\u0F47')||(LA16_0>='\u0F49' && LA16_0<='\u0F6A')||(LA16_0>='\u0F88' && LA16_0<='\u0F8B')||(LA16_0>='\u1000' && LA16_0<='\u1021')||(LA16_0>='\u1023' && LA16_0<='\u1027')||(LA16_0>='\u1029' && LA16_0<='\u102A')||(LA16_0>='\u1040' && LA16_0<='\u1049')||(LA16_0>='\u1050' && LA16_0<='\u1055')||(LA16_0>='\u10A0' && LA16_0<='\u10C5')||(LA16_0>='\u10D0' && LA16_0<='\u10F8')||(LA16_0>='\u1100' && LA16_0<='\u1159')||(LA16_0>='\u115F' && LA16_0<='\u11A2')||(LA16_0>='\u11A8' && LA16_0<='\u11F9')||(LA16_0>='\u1200' && LA16_0<='\u1206')||(LA16_0>='\u1208' && LA16_0<='\u1246')||LA16_0=='\u1248'||(LA16_0>='\u124A' && LA16_0<='\u124D')||(LA16_0>='\u1250' && LA16_0<='\u1256')||LA16_0=='\u1258'||(LA16_0>='\u125A' && LA16_0<='\u125D')||(LA16_0>='\u1260' && LA16_0<='\u1286')||LA16_0=='\u1288'||(LA16_0>='\u128A' && LA16_0<='\u128D')||(LA16_0>='\u1290' && LA16_0<='\u12AE')||LA16_0=='\u12B0'||(LA16_0>='\u12B2' && LA16_0<='\u12B5')||(LA16_0>='\u12B8' && LA16_0<='\u12BE')||LA16_0=='\u12C0'||(LA16_0>='\u12C2' && LA16_0<='\u12C5')||(LA16_0>='\u12C8' && LA16_0<='\u12CE')||(LA16_0>='\u12D0' && LA16_0<='\u12D6')||(LA16_0>='\u12D8' && LA16_0<='\u12EE')||(LA16_0>='\u12F0' && LA16_0<='\u130E')||LA16_0=='\u1310'||(LA16_0>='\u1312' && LA16_0<='\u1315')||(LA16_0>='\u1318' && LA16_0<='\u131E')||(LA16_0>='\u1320' && LA16_0<='\u1346')||(LA16_0>='\u1348' && LA16_0<='\u135A')||(LA16_0>='\u1369' && LA16_0<='\u1371')||(LA16_0>='\u13A0' && LA16_0<='\u13F4')||(LA16_0>='\u1401' && LA16_0<='\u166C')||(LA16_0>='\u166F' && LA16_0<='\u1676')||(LA16_0>='\u1681' && LA16_0<='\u169A')||(LA16_0>='\u16A0' && LA16_0<='\u16EA')||(LA16_0>='\u1700' && LA16_0<='\u170C')||(LA16_0>='\u170E' && LA16_0<='\u1711')||(LA16_0>='\u1720' && LA16_0<='\u1731')||(LA16_0>='\u1740' && LA16_0<='\u1751')||(LA16_0>='\u1760' && LA16_0<='\u176C')||(LA16_0>='\u176E' && LA16_0<='\u1770')||(LA16_0>='\u1780' && LA16_0<='\u17B3')||LA16_0=='\u17D7'||LA16_0=='\u17DC'||(LA16_0>='\u17E0' && LA16_0<='\u17E9')||(LA16_0>='\u1810' && LA16_0<='\u1819')||(LA16_0>='\u1820' && LA16_0<='\u1877')||(LA16_0>='\u1880' && LA16_0<='\u18A8')||(LA16_0>='\u1900' && LA16_0<='\u191C')||(LA16_0>='\u1946' && LA16_0<='\u196D')||(LA16_0>='\u1970' && LA16_0<='\u1974')||(LA16_0>='\u1D00' && LA16_0<='\u1D6B')||(LA16_0>='\u1E00' && LA16_0<='\u1E9B')||(LA16_0>='\u1EA0' && LA16_0<='\u1EF9')||(LA16_0>='\u1F00' && LA16_0<='\u1F15')||(LA16_0>='\u1F18' && LA16_0<='\u1F1D')||(LA16_0>='\u1F20' && LA16_0<='\u1F45')||(LA16_0>='\u1F48' && LA16_0<='\u1F4D')||(LA16_0>='\u1F50' && LA16_0<='\u1F57')||LA16_0=='\u1F59'||LA16_0=='\u1F5B'||LA16_0=='\u1F5D'||(LA16_0>='\u1F5F' && LA16_0<='\u1F7D')||(LA16_0>='\u1F80' && LA16_0<='\u1FB4')||(LA16_0>='\u1FB6' && LA16_0<='\u1FBC')||LA16_0=='\u1FBE'||(LA16_0>='\u1FC2' && LA16_0<='\u1FC4')||(LA16_0>='\u1FC6' && LA16_0<='\u1FCC')||(LA16_0>='\u1FD0' && LA16_0<='\u1FD3')||(LA16_0>='\u1FD6' && LA16_0<='\u1FDB')||(LA16_0>='\u1FE0' && LA16_0<='\u1FEC')||(LA16_0>='\u1FF2' && LA16_0<='\u1FF4')||(LA16_0>='\u1FF6' && LA16_0<='\u1FFC')||LA16_0=='\u2071'||LA16_0=='\u207F'||LA16_0=='\u2102'||LA16_0=='\u2107'||(LA16_0>='\u210A' && LA16_0<='\u2113')||LA16_0=='\u2115'||(LA16_0>='\u2119' && LA16_0<='\u211D')||LA16_0=='\u2124'||LA16_0=='\u2126'||LA16_0=='\u2128'||(LA16_0>='\u212A' && LA16_0<='\u212D')||(LA16_0>='\u212F' && LA16_0<='\u2131')||(LA16_0>='\u2133' && LA16_0<='\u2139')||(LA16_0>='\u213D' && LA16_0<='\u213F')||(LA16_0>='\u2145' && LA16_0<='\u2149')||(LA16_0>='\u3005' && LA16_0<='\u3006')||(LA16_0>='\u3031' && LA16_0<='\u3035')||(LA16_0>='\u303B' && LA16_0<='\u303C')||(LA16_0>='\u3041' && LA16_0<='\u3096')||(LA16_0>='\u309D' && LA16_0<='\u309F')||(LA16_0>='\u30A1' && LA16_0<='\u30FA')||(LA16_0>='\u30FC' && LA16_0<='\u30FF')||(LA16_0>='\u3105' && LA16_0<='\u312C')||(LA16_0>='\u3131' && LA16_0<='\u318E')||(LA16_0>='\u31A0' && LA16_0<='\u31B7')||(LA16_0>='\u31F0' && LA16_0<='\u31FF')||(LA16_0>='\u3400' && LA16_0<='\u4DB5')||(LA16_0>='\u4E00' && LA16_0<='\u9FA5')||(LA16_0>='\uA000' && LA16_0<='\uA48C')||(LA16_0>='\uAC00' && LA16_0<='\uD7A3')||(LA16_0>='\uF900' && LA16_0<='\uFA2D')||(LA16_0>='\uFA30' && LA16_0<='\uFA6A')||(LA16_0>='\uFB00' && LA16_0<='\uFB06')||(LA16_0>='\uFB13' && LA16_0<='\uFB17')||LA16_0=='\uFB1D'||(LA16_0>='\uFB1F' && LA16_0<='\uFB28')||(LA16_0>='\uFB2A' && LA16_0<='\uFB36')||(LA16_0>='\uFB38' && LA16_0<='\uFB3C')||LA16_0=='\uFB3E'||(LA16_0>='\uFB40' && LA16_0<='\uFB41')||(LA16_0>='\uFB43' && LA16_0<='\uFB44')||(LA16_0>='\uFB46' && LA16_0<='\uFBB1')||(LA16_0>='\uFBD3' && LA16_0<='\uFD3D')||(LA16_0>='\uFD50' && LA16_0<='\uFD8F')||(LA16_0>='\uFD92' && LA16_0<='\uFDC7')||(LA16_0>='\uFDF0' && LA16_0<='\uFDFB')||(LA16_0>='\uFE70' && LA16_0<='\uFE74')||(LA16_0>='\uFE76' && LA16_0<='\uFEFC')||(LA16_0>='\uFF10' && LA16_0<='\uFF19')||(LA16_0>='\uFF21' && LA16_0<='\uFF3A')||(LA16_0>='\uFF41' && LA16_0<='\uFF5A')||(LA16_0>='\uFF66' && LA16_0<='\uFFBE')||(LA16_0>='\uFFC2' && LA16_0<='\uFFC7')||(LA16_0>='\uFFCA' && LA16_0<='\uFFCF')||(LA16_0>='\uFFD2' && LA16_0<='\uFFD7')||(LA16_0>='\uFFDA' && LA16_0<='\uFFDC')) ) {
                    alt16=2;
                }


                switch (alt16) {
            	case 1 :
            	    // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1233:17: F_ESC
            	    {
            	    mF_ESC(); if (state.failed) return ;

            	    }
            	    break;
            	case 2 :
            	    // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1234:19: INWORD
            	    {
            	    mINWORD(); if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    if ( cnt16 >= 1 ) break loop16;
            	    if (state.backtracking>0) {state.failed=true; return ;}
                        EarlyExitException eee =
                            new EarlyExitException(16, input);
                        throw eee;
                }
                cnt16++;
            } while (true);

            mSTAR(); if (state.failed) return ;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "FTSPRE"

    // $ANTLR start "FTSWILD"
    public final void mFTSWILD() throws RecognitionException {
        try {
            int _type = FTSWILD;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1240:9: ( ( F_ESC | INWORD | STAR | QUESTION_MARK )+ )
            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1241:9: ( F_ESC | INWORD | STAR | QUESTION_MARK )+
            {
            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1241:9: ( F_ESC | INWORD | STAR | QUESTION_MARK )+
            int cnt17=0;
            loop17:
            do {
                int alt17=5;
                int LA17_0 = input.LA(1);

                if ( (LA17_0=='\\') ) {
                    alt17=1;
                }
                else if ( ((LA17_0>='0' && LA17_0<='9')||(LA17_0>='A' && LA17_0<='Z')||(LA17_0>='a' && LA17_0<='z')||LA17_0=='\u00AA'||LA17_0=='\u00B5'||LA17_0=='\u00BA'||(LA17_0>='\u00C0' && LA17_0<='\u00D6')||(LA17_0>='\u00D8' && LA17_0<='\u00F6')||(LA17_0>='\u00F8' && LA17_0<='\u0236')||(LA17_0>='\u0250' && LA17_0<='\u02C1')||(LA17_0>='\u02C6' && LA17_0<='\u02D1')||(LA17_0>='\u02E0' && LA17_0<='\u02E4')||LA17_0=='\u02EE'||LA17_0=='\u037A'||LA17_0=='\u0386'||(LA17_0>='\u0388' && LA17_0<='\u038A')||LA17_0=='\u038C'||(LA17_0>='\u038E' && LA17_0<='\u03A1')||(LA17_0>='\u03A3' && LA17_0<='\u03CE')||(LA17_0>='\u03D0' && LA17_0<='\u03F5')||(LA17_0>='\u03F7' && LA17_0<='\u03FB')||(LA17_0>='\u0400' && LA17_0<='\u0481')||(LA17_0>='\u048A' && LA17_0<='\u04CE')||(LA17_0>='\u04D0' && LA17_0<='\u04F5')||(LA17_0>='\u04F8' && LA17_0<='\u04F9')||(LA17_0>='\u0500' && LA17_0<='\u050F')||(LA17_0>='\u0531' && LA17_0<='\u0556')||LA17_0=='\u0559'||(LA17_0>='\u0561' && LA17_0<='\u0587')||(LA17_0>='\u05D0' && LA17_0<='\u05EA')||(LA17_0>='\u05F0' && LA17_0<='\u05F2')||(LA17_0>='\u0621' && LA17_0<='\u063A')||(LA17_0>='\u0640' && LA17_0<='\u064A')||(LA17_0>='\u0660' && LA17_0<='\u0669')||(LA17_0>='\u066E' && LA17_0<='\u066F')||(LA17_0>='\u0671' && LA17_0<='\u06D3')||LA17_0=='\u06D5'||(LA17_0>='\u06E5' && LA17_0<='\u06E6')||(LA17_0>='\u06EE' && LA17_0<='\u06FC')||LA17_0=='\u06FF'||LA17_0=='\u0710'||(LA17_0>='\u0712' && LA17_0<='\u072F')||(LA17_0>='\u074D' && LA17_0<='\u074F')||(LA17_0>='\u0780' && LA17_0<='\u07A5')||LA17_0=='\u07B1'||(LA17_0>='\u0904' && LA17_0<='\u0939')||LA17_0=='\u093D'||LA17_0=='\u0950'||(LA17_0>='\u0958' && LA17_0<='\u0961')||(LA17_0>='\u0966' && LA17_0<='\u096F')||(LA17_0>='\u0985' && LA17_0<='\u098C')||(LA17_0>='\u098F' && LA17_0<='\u0990')||(LA17_0>='\u0993' && LA17_0<='\u09A8')||(LA17_0>='\u09AA' && LA17_0<='\u09B0')||LA17_0=='\u09B2'||(LA17_0>='\u09B6' && LA17_0<='\u09B9')||LA17_0=='\u09BD'||(LA17_0>='\u09DC' && LA17_0<='\u09DD')||(LA17_0>='\u09DF' && LA17_0<='\u09E1')||(LA17_0>='\u09E6' && LA17_0<='\u09F1')||(LA17_0>='\u0A05' && LA17_0<='\u0A0A')||(LA17_0>='\u0A0F' && LA17_0<='\u0A10')||(LA17_0>='\u0A13' && LA17_0<='\u0A28')||(LA17_0>='\u0A2A' && LA17_0<='\u0A30')||(LA17_0>='\u0A32' && LA17_0<='\u0A33')||(LA17_0>='\u0A35' && LA17_0<='\u0A36')||(LA17_0>='\u0A38' && LA17_0<='\u0A39')||(LA17_0>='\u0A59' && LA17_0<='\u0A5C')||LA17_0=='\u0A5E'||(LA17_0>='\u0A66' && LA17_0<='\u0A6F')||(LA17_0>='\u0A72' && LA17_0<='\u0A74')||(LA17_0>='\u0A85' && LA17_0<='\u0A8D')||(LA17_0>='\u0A8F' && LA17_0<='\u0A91')||(LA17_0>='\u0A93' && LA17_0<='\u0AA8')||(LA17_0>='\u0AAA' && LA17_0<='\u0AB0')||(LA17_0>='\u0AB2' && LA17_0<='\u0AB3')||(LA17_0>='\u0AB5' && LA17_0<='\u0AB9')||LA17_0=='\u0ABD'||LA17_0=='\u0AD0'||(LA17_0>='\u0AE0' && LA17_0<='\u0AE1')||(LA17_0>='\u0AE6' && LA17_0<='\u0AEF')||(LA17_0>='\u0B05' && LA17_0<='\u0B0C')||(LA17_0>='\u0B0F' && LA17_0<='\u0B10')||(LA17_0>='\u0B13' && LA17_0<='\u0B28')||(LA17_0>='\u0B2A' && LA17_0<='\u0B30')||(LA17_0>='\u0B32' && LA17_0<='\u0B33')||(LA17_0>='\u0B35' && LA17_0<='\u0B39')||LA17_0=='\u0B3D'||(LA17_0>='\u0B5C' && LA17_0<='\u0B5D')||(LA17_0>='\u0B5F' && LA17_0<='\u0B61')||(LA17_0>='\u0B66' && LA17_0<='\u0B6F')||LA17_0=='\u0B71'||LA17_0=='\u0B83'||(LA17_0>='\u0B85' && LA17_0<='\u0B8A')||(LA17_0>='\u0B8E' && LA17_0<='\u0B90')||(LA17_0>='\u0B92' && LA17_0<='\u0B95')||(LA17_0>='\u0B99' && LA17_0<='\u0B9A')||LA17_0=='\u0B9C'||(LA17_0>='\u0B9E' && LA17_0<='\u0B9F')||(LA17_0>='\u0BA3' && LA17_0<='\u0BA4')||(LA17_0>='\u0BA8' && LA17_0<='\u0BAA')||(LA17_0>='\u0BAE' && LA17_0<='\u0BB5')||(LA17_0>='\u0BB7' && LA17_0<='\u0BB9')||(LA17_0>='\u0BE7' && LA17_0<='\u0BEF')||(LA17_0>='\u0C05' && LA17_0<='\u0C0C')||(LA17_0>='\u0C0E' && LA17_0<='\u0C10')||(LA17_0>='\u0C12' && LA17_0<='\u0C28')||(LA17_0>='\u0C2A' && LA17_0<='\u0C33')||(LA17_0>='\u0C35' && LA17_0<='\u0C39')||(LA17_0>='\u0C60' && LA17_0<='\u0C61')||(LA17_0>='\u0C66' && LA17_0<='\u0C6F')||(LA17_0>='\u0C85' && LA17_0<='\u0C8C')||(LA17_0>='\u0C8E' && LA17_0<='\u0C90')||(LA17_0>='\u0C92' && LA17_0<='\u0CA8')||(LA17_0>='\u0CAA' && LA17_0<='\u0CB3')||(LA17_0>='\u0CB5' && LA17_0<='\u0CB9')||LA17_0=='\u0CBD'||LA17_0=='\u0CDE'||(LA17_0>='\u0CE0' && LA17_0<='\u0CE1')||(LA17_0>='\u0CE6' && LA17_0<='\u0CEF')||(LA17_0>='\u0D05' && LA17_0<='\u0D0C')||(LA17_0>='\u0D0E' && LA17_0<='\u0D10')||(LA17_0>='\u0D12' && LA17_0<='\u0D28')||(LA17_0>='\u0D2A' && LA17_0<='\u0D39')||(LA17_0>='\u0D60' && LA17_0<='\u0D61')||(LA17_0>='\u0D66' && LA17_0<='\u0D6F')||(LA17_0>='\u0D85' && LA17_0<='\u0D96')||(LA17_0>='\u0D9A' && LA17_0<='\u0DB1')||(LA17_0>='\u0DB3' && LA17_0<='\u0DBB')||LA17_0=='\u0DBD'||(LA17_0>='\u0DC0' && LA17_0<='\u0DC6')||(LA17_0>='\u0E01' && LA17_0<='\u0E30')||(LA17_0>='\u0E32' && LA17_0<='\u0E33')||(LA17_0>='\u0E40' && LA17_0<='\u0E46')||(LA17_0>='\u0E50' && LA17_0<='\u0E59')||(LA17_0>='\u0E81' && LA17_0<='\u0E82')||LA17_0=='\u0E84'||(LA17_0>='\u0E87' && LA17_0<='\u0E88')||LA17_0=='\u0E8A'||LA17_0=='\u0E8D'||(LA17_0>='\u0E94' && LA17_0<='\u0E97')||(LA17_0>='\u0E99' && LA17_0<='\u0E9F')||(LA17_0>='\u0EA1' && LA17_0<='\u0EA3')||LA17_0=='\u0EA5'||LA17_0=='\u0EA7'||(LA17_0>='\u0EAA' && LA17_0<='\u0EAB')||(LA17_0>='\u0EAD' && LA17_0<='\u0EB0')||(LA17_0>='\u0EB2' && LA17_0<='\u0EB3')||LA17_0=='\u0EBD'||(LA17_0>='\u0EC0' && LA17_0<='\u0EC4')||LA17_0=='\u0EC6'||(LA17_0>='\u0ED0' && LA17_0<='\u0ED9')||(LA17_0>='\u0EDC' && LA17_0<='\u0EDD')||LA17_0=='\u0F00'||(LA17_0>='\u0F20' && LA17_0<='\u0F29')||(LA17_0>='\u0F40' && LA17_0<='\u0F47')||(LA17_0>='\u0F49' && LA17_0<='\u0F6A')||(LA17_0>='\u0F88' && LA17_0<='\u0F8B')||(LA17_0>='\u1000' && LA17_0<='\u1021')||(LA17_0>='\u1023' && LA17_0<='\u1027')||(LA17_0>='\u1029' && LA17_0<='\u102A')||(LA17_0>='\u1040' && LA17_0<='\u1049')||(LA17_0>='\u1050' && LA17_0<='\u1055')||(LA17_0>='\u10A0' && LA17_0<='\u10C5')||(LA17_0>='\u10D0' && LA17_0<='\u10F8')||(LA17_0>='\u1100' && LA17_0<='\u1159')||(LA17_0>='\u115F' && LA17_0<='\u11A2')||(LA17_0>='\u11A8' && LA17_0<='\u11F9')||(LA17_0>='\u1200' && LA17_0<='\u1206')||(LA17_0>='\u1208' && LA17_0<='\u1246')||LA17_0=='\u1248'||(LA17_0>='\u124A' && LA17_0<='\u124D')||(LA17_0>='\u1250' && LA17_0<='\u1256')||LA17_0=='\u1258'||(LA17_0>='\u125A' && LA17_0<='\u125D')||(LA17_0>='\u1260' && LA17_0<='\u1286')||LA17_0=='\u1288'||(LA17_0>='\u128A' && LA17_0<='\u128D')||(LA17_0>='\u1290' && LA17_0<='\u12AE')||LA17_0=='\u12B0'||(LA17_0>='\u12B2' && LA17_0<='\u12B5')||(LA17_0>='\u12B8' && LA17_0<='\u12BE')||LA17_0=='\u12C0'||(LA17_0>='\u12C2' && LA17_0<='\u12C5')||(LA17_0>='\u12C8' && LA17_0<='\u12CE')||(LA17_0>='\u12D0' && LA17_0<='\u12D6')||(LA17_0>='\u12D8' && LA17_0<='\u12EE')||(LA17_0>='\u12F0' && LA17_0<='\u130E')||LA17_0=='\u1310'||(LA17_0>='\u1312' && LA17_0<='\u1315')||(LA17_0>='\u1318' && LA17_0<='\u131E')||(LA17_0>='\u1320' && LA17_0<='\u1346')||(LA17_0>='\u1348' && LA17_0<='\u135A')||(LA17_0>='\u1369' && LA17_0<='\u1371')||(LA17_0>='\u13A0' && LA17_0<='\u13F4')||(LA17_0>='\u1401' && LA17_0<='\u166C')||(LA17_0>='\u166F' && LA17_0<='\u1676')||(LA17_0>='\u1681' && LA17_0<='\u169A')||(LA17_0>='\u16A0' && LA17_0<='\u16EA')||(LA17_0>='\u1700' && LA17_0<='\u170C')||(LA17_0>='\u170E' && LA17_0<='\u1711')||(LA17_0>='\u1720' && LA17_0<='\u1731')||(LA17_0>='\u1740' && LA17_0<='\u1751')||(LA17_0>='\u1760' && LA17_0<='\u176C')||(LA17_0>='\u176E' && LA17_0<='\u1770')||(LA17_0>='\u1780' && LA17_0<='\u17B3')||LA17_0=='\u17D7'||LA17_0=='\u17DC'||(LA17_0>='\u17E0' && LA17_0<='\u17E9')||(LA17_0>='\u1810' && LA17_0<='\u1819')||(LA17_0>='\u1820' && LA17_0<='\u1877')||(LA17_0>='\u1880' && LA17_0<='\u18A8')||(LA17_0>='\u1900' && LA17_0<='\u191C')||(LA17_0>='\u1946' && LA17_0<='\u196D')||(LA17_0>='\u1970' && LA17_0<='\u1974')||(LA17_0>='\u1D00' && LA17_0<='\u1D6B')||(LA17_0>='\u1E00' && LA17_0<='\u1E9B')||(LA17_0>='\u1EA0' && LA17_0<='\u1EF9')||(LA17_0>='\u1F00' && LA17_0<='\u1F15')||(LA17_0>='\u1F18' && LA17_0<='\u1F1D')||(LA17_0>='\u1F20' && LA17_0<='\u1F45')||(LA17_0>='\u1F48' && LA17_0<='\u1F4D')||(LA17_0>='\u1F50' && LA17_0<='\u1F57')||LA17_0=='\u1F59'||LA17_0=='\u1F5B'||LA17_0=='\u1F5D'||(LA17_0>='\u1F5F' && LA17_0<='\u1F7D')||(LA17_0>='\u1F80' && LA17_0<='\u1FB4')||(LA17_0>='\u1FB6' && LA17_0<='\u1FBC')||LA17_0=='\u1FBE'||(LA17_0>='\u1FC2' && LA17_0<='\u1FC4')||(LA17_0>='\u1FC6' && LA17_0<='\u1FCC')||(LA17_0>='\u1FD0' && LA17_0<='\u1FD3')||(LA17_0>='\u1FD6' && LA17_0<='\u1FDB')||(LA17_0>='\u1FE0' && LA17_0<='\u1FEC')||(LA17_0>='\u1FF2' && LA17_0<='\u1FF4')||(LA17_0>='\u1FF6' && LA17_0<='\u1FFC')||LA17_0=='\u2071'||LA17_0=='\u207F'||LA17_0=='\u2102'||LA17_0=='\u2107'||(LA17_0>='\u210A' && LA17_0<='\u2113')||LA17_0=='\u2115'||(LA17_0>='\u2119' && LA17_0<='\u211D')||LA17_0=='\u2124'||LA17_0=='\u2126'||LA17_0=='\u2128'||(LA17_0>='\u212A' && LA17_0<='\u212D')||(LA17_0>='\u212F' && LA17_0<='\u2131')||(LA17_0>='\u2133' && LA17_0<='\u2139')||(LA17_0>='\u213D' && LA17_0<='\u213F')||(LA17_0>='\u2145' && LA17_0<='\u2149')||(LA17_0>='\u3005' && LA17_0<='\u3006')||(LA17_0>='\u3031' && LA17_0<='\u3035')||(LA17_0>='\u303B' && LA17_0<='\u303C')||(LA17_0>='\u3041' && LA17_0<='\u3096')||(LA17_0>='\u309D' && LA17_0<='\u309F')||(LA17_0>='\u30A1' && LA17_0<='\u30FA')||(LA17_0>='\u30FC' && LA17_0<='\u30FF')||(LA17_0>='\u3105' && LA17_0<='\u312C')||(LA17_0>='\u3131' && LA17_0<='\u318E')||(LA17_0>='\u31A0' && LA17_0<='\u31B7')||(LA17_0>='\u31F0' && LA17_0<='\u31FF')||(LA17_0>='\u3400' && LA17_0<='\u4DB5')||(LA17_0>='\u4E00' && LA17_0<='\u9FA5')||(LA17_0>='\uA000' && LA17_0<='\uA48C')||(LA17_0>='\uAC00' && LA17_0<='\uD7A3')||(LA17_0>='\uF900' && LA17_0<='\uFA2D')||(LA17_0>='\uFA30' && LA17_0<='\uFA6A')||(LA17_0>='\uFB00' && LA17_0<='\uFB06')||(LA17_0>='\uFB13' && LA17_0<='\uFB17')||LA17_0=='\uFB1D'||(LA17_0>='\uFB1F' && LA17_0<='\uFB28')||(LA17_0>='\uFB2A' && LA17_0<='\uFB36')||(LA17_0>='\uFB38' && LA17_0<='\uFB3C')||LA17_0=='\uFB3E'||(LA17_0>='\uFB40' && LA17_0<='\uFB41')||(LA17_0>='\uFB43' && LA17_0<='\uFB44')||(LA17_0>='\uFB46' && LA17_0<='\uFBB1')||(LA17_0>='\uFBD3' && LA17_0<='\uFD3D')||(LA17_0>='\uFD50' && LA17_0<='\uFD8F')||(LA17_0>='\uFD92' && LA17_0<='\uFDC7')||(LA17_0>='\uFDF0' && LA17_0<='\uFDFB')||(LA17_0>='\uFE70' && LA17_0<='\uFE74')||(LA17_0>='\uFE76' && LA17_0<='\uFEFC')||(LA17_0>='\uFF10' && LA17_0<='\uFF19')||(LA17_0>='\uFF21' && LA17_0<='\uFF3A')||(LA17_0>='\uFF41' && LA17_0<='\uFF5A')||(LA17_0>='\uFF66' && LA17_0<='\uFFBE')||(LA17_0>='\uFFC2' && LA17_0<='\uFFC7')||(LA17_0>='\uFFCA' && LA17_0<='\uFFCF')||(LA17_0>='\uFFD2' && LA17_0<='\uFFD7')||(LA17_0>='\uFFDA' && LA17_0<='\uFFDC')) ) {
                    alt17=2;
                }
                else if ( (LA17_0=='*') ) {
                    alt17=3;
                }
                else if ( (LA17_0=='?') ) {
                    alt17=4;
                }


                switch (alt17) {
            	case 1 :
            	    // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1242:17: F_ESC
            	    {
            	    mF_ESC(); if (state.failed) return ;

            	    }
            	    break;
            	case 2 :
            	    // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1243:19: INWORD
            	    {
            	    mINWORD(); if (state.failed) return ;

            	    }
            	    break;
            	case 3 :
            	    // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1244:19: STAR
            	    {
            	    mSTAR(); if (state.failed) return ;

            	    }
            	    break;
            	case 4 :
            	    // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1245:19: QUESTION_MARK
            	    {
            	    mQUESTION_MARK(); if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    if ( cnt17 >= 1 ) break loop17;
            	    if (state.backtracking>0) {state.failed=true; return ;}
                        EarlyExitException eee =
                            new EarlyExitException(17, input);
                        throw eee;
                }
                cnt17++;
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "FTSWILD"

    // $ANTLR start "F_ESC"
    public final void mF_ESC() throws RecognitionException {
        try {
            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1251:9: ( '\\\\' ( 'u' F_HEX F_HEX F_HEX F_HEX | . ) )
            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1252:9: '\\\\' ( 'u' F_HEX F_HEX F_HEX F_HEX | . )
            {
            match('\\'); if (state.failed) return ;
            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1253:9: ( 'u' F_HEX F_HEX F_HEX F_HEX | . )
            int alt18=2;
            int LA18_0 = input.LA(1);

            if ( (LA18_0=='u') ) {
                int LA18_1 = input.LA(2);

                if ( ((LA18_1>='0' && LA18_1<='9')||(LA18_1>='A' && LA18_1<='F')||(LA18_1>='a' && LA18_1<='f')) ) {
                    alt18=1;
                }
                else {
                    alt18=2;}
            }
            else if ( ((LA18_0>='\u0000' && LA18_0<='t')||(LA18_0>='v' && LA18_0<='\uFFFF')) ) {
                alt18=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 18, 0, input);

                throw nvae;
            }
            switch (alt18) {
                case 1 :
                    // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1255:17: 'u' F_HEX F_HEX F_HEX F_HEX
                    {
                    match('u'); if (state.failed) return ;
                    mF_HEX(); if (state.failed) return ;
                    mF_HEX(); if (state.failed) return ;
                    mF_HEX(); if (state.failed) return ;
                    mF_HEX(); if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1257:19: .
                    {
                    matchAny(); if (state.failed) return ;

                    }
                    break;

            }


            }

        }
        finally {
        }
    }
    // $ANTLR end "F_ESC"

    // $ANTLR start "F_HEX"
    public final void mF_HEX() throws RecognitionException {
        try {
            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1263:9: ( '0' .. '9' | 'a' .. 'f' | 'A' .. 'F' )
            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:
            {
            if ( (input.LA(1)>='0' && input.LA(1)<='9')||(input.LA(1)>='A' && input.LA(1)<='F')||(input.LA(1)>='a' && input.LA(1)<='f') ) {
                input.consume();
            state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

        }
        finally {
        }
    }
    // $ANTLR end "F_HEX"

    // $ANTLR start "INWORD"
    public final void mINWORD() throws RecognitionException {
        try {
            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1271:9: ( '\\u0030' .. '\\u0039' | '\\u0041' .. '\\u005a' | '\\u0061' .. '\\u007a' | '\\u00aa' | '\\u00b5' | '\\u00ba' | '\\u00c0' .. '\\u00d6' | '\\u00d8' .. '\\u00f6' | '\\u00f8' .. '\\u0236' | '\\u0250' .. '\\u02c1' | '\\u02c6' .. '\\u02d1' | '\\u02e0' .. '\\u02e4' | '\\u02ee' | '\\u037a' | '\\u0386' | '\\u0388' .. '\\u038a' | '\\u038c' | '\\u038e' .. '\\u03a1' | '\\u03a3' .. '\\u03ce' | '\\u03d0' .. '\\u03f5' | '\\u03f7' .. '\\u03fb' | '\\u0400' .. '\\u0481' | '\\u048a' .. '\\u04ce' | '\\u04d0' .. '\\u04f5' | '\\u04f8' .. '\\u04f9' | '\\u0500' .. '\\u050f' | '\\u0531' .. '\\u0556' | '\\u0559' | '\\u0561' .. '\\u0587' | '\\u05d0' .. '\\u05ea' | '\\u05f0' .. '\\u05f2' | '\\u0621' .. '\\u063a' | '\\u0640' .. '\\u064a' | '\\u0660' .. '\\u0669' | '\\u066e' .. '\\u066f' | '\\u0671' .. '\\u06d3' | '\\u06d5' | '\\u06e5' .. '\\u06e6' | '\\u06ee' .. '\\u06fc' | '\\u06ff' | '\\u0710' | '\\u0712' .. '\\u072f' | '\\u074d' .. '\\u074f' | '\\u0780' .. '\\u07a5' | '\\u07b1' | '\\u0904' .. '\\u0939' | '\\u093d' | '\\u0950' | '\\u0958' .. '\\u0961' | '\\u0966' .. '\\u096f' | '\\u0985' .. '\\u098c' | '\\u098f' .. '\\u0990' | '\\u0993' .. '\\u09a8' | '\\u09aa' .. '\\u09b0' | '\\u09b2' | '\\u09b6' .. '\\u09b9' | '\\u09bd' | '\\u09dc' .. '\\u09dd' | '\\u09df' .. '\\u09e1' | '\\u09e6' .. '\\u09f1' | '\\u0a05' .. '\\u0a0a' | '\\u0a0f' .. '\\u0a10' | '\\u0a13' .. '\\u0a28' | '\\u0a2a' .. '\\u0a30' | '\\u0a32' .. '\\u0a33' | '\\u0a35' .. '\\u0a36' | '\\u0a38' .. '\\u0a39' | '\\u0a59' .. '\\u0a5c' | '\\u0a5e' | '\\u0a66' .. '\\u0a6f' | '\\u0a72' .. '\\u0a74' | '\\u0a85' .. '\\u0a8d' | '\\u0a8f' .. '\\u0a91' | '\\u0a93' .. '\\u0aa8' | '\\u0aaa' .. '\\u0ab0' | '\\u0ab2' .. '\\u0ab3' | '\\u0ab5' .. '\\u0ab9' | '\\u0abd' | '\\u0ad0' | '\\u0ae0' .. '\\u0ae1' | '\\u0ae6' .. '\\u0aef' | '\\u0b05' .. '\\u0b0c' | '\\u0b0f' .. '\\u0b10' | '\\u0b13' .. '\\u0b28' | '\\u0b2a' .. '\\u0b30' | '\\u0b32' .. '\\u0b33' | '\\u0b35' .. '\\u0b39' | '\\u0b3d' | '\\u0b5c' .. '\\u0b5d' | '\\u0b5f' .. '\\u0b61' | '\\u0b66' .. '\\u0b6f' | '\\u0b71' | '\\u0b83' | '\\u0b85' .. '\\u0b8a' | '\\u0b8e' .. '\\u0b90' | '\\u0b92' .. '\\u0b95' | '\\u0b99' .. '\\u0b9a' | '\\u0b9c' | '\\u0b9e' .. '\\u0b9f' | '\\u0ba3' .. '\\u0ba4' | '\\u0ba8' .. '\\u0baa' | '\\u0bae' .. '\\u0bb5' | '\\u0bb7' .. '\\u0bb9' | '\\u0be7' .. '\\u0bef' | '\\u0c05' .. '\\u0c0c' | '\\u0c0e' .. '\\u0c10' | '\\u0c12' .. '\\u0c28' | '\\u0c2a' .. '\\u0c33' | '\\u0c35' .. '\\u0c39' | '\\u0c60' .. '\\u0c61' | '\\u0c66' .. '\\u0c6f' | '\\u0c85' .. '\\u0c8c' | '\\u0c8e' .. '\\u0c90' | '\\u0c92' .. '\\u0ca8' | '\\u0caa' .. '\\u0cb3' | '\\u0cb5' .. '\\u0cb9' | '\\u0cbd' | '\\u0cde' | '\\u0ce0' .. '\\u0ce1' | '\\u0ce6' .. '\\u0cef' | '\\u0d05' .. '\\u0d0c' | '\\u0d0e' .. '\\u0d10' | '\\u0d12' .. '\\u0d28' | '\\u0d2a' .. '\\u0d39' | '\\u0d60' .. '\\u0d61' | '\\u0d66' .. '\\u0d6f' | '\\u0d85' .. '\\u0d96' | '\\u0d9a' .. '\\u0db1' | '\\u0db3' .. '\\u0dbb' | '\\u0dbd' | '\\u0dc0' .. '\\u0dc6' | '\\u0e01' .. '\\u0e30' | '\\u0e32' .. '\\u0e33' | '\\u0e40' .. '\\u0e46' | '\\u0e50' .. '\\u0e59' | '\\u0e81' .. '\\u0e82' | '\\u0e84' | '\\u0e87' .. '\\u0e88' | '\\u0e8a' | '\\u0e8d' | '\\u0e94' .. '\\u0e97' | '\\u0e99' .. '\\u0e9f' | '\\u0ea1' .. '\\u0ea3' | '\\u0ea5' | '\\u0ea7' | '\\u0eaa' .. '\\u0eab' | '\\u0ead' .. '\\u0eb0' | '\\u0eb2' .. '\\u0eb3' | '\\u0ebd' | '\\u0ec0' .. '\\u0ec4' | '\\u0ec6' | '\\u0ed0' .. '\\u0ed9' | '\\u0edc' .. '\\u0edd' | '\\u0f00' | '\\u0f20' .. '\\u0f29' | '\\u0f40' .. '\\u0f47' | '\\u0f49' .. '\\u0f6a' | '\\u0f88' .. '\\u0f8b' | '\\u1000' .. '\\u1021' | '\\u1023' .. '\\u1027' | '\\u1029' .. '\\u102a' | '\\u1040' .. '\\u1049' | '\\u1050' .. '\\u1055' | '\\u10a0' .. '\\u10c5' | '\\u10d0' .. '\\u10f8' | '\\u1100' .. '\\u1159' | '\\u115f' .. '\\u11a2' | '\\u11a8' .. '\\u11f9' | '\\u1200' .. '\\u1206' | '\\u1208' .. '\\u1246' | '\\u1248' | '\\u124a' .. '\\u124d' | '\\u1250' .. '\\u1256' | '\\u1258' | '\\u125a' .. '\\u125d' | '\\u1260' .. '\\u1286' | '\\u1288' | '\\u128a' .. '\\u128d' | '\\u1290' .. '\\u12ae' | '\\u12b0' | '\\u12b2' .. '\\u12b5' | '\\u12b8' .. '\\u12be' | '\\u12c0' | '\\u12c2' .. '\\u12c5' | '\\u12c8' .. '\\u12ce' | '\\u12d0' .. '\\u12d6' | '\\u12d8' .. '\\u12ee' | '\\u12f0' .. '\\u130e' | '\\u1310' | '\\u1312' .. '\\u1315' | '\\u1318' .. '\\u131e' | '\\u1320' .. '\\u1346' | '\\u1348' .. '\\u135a' | '\\u1369' .. '\\u1371' | '\\u13a0' .. '\\u13f4' | '\\u1401' .. '\\u166c' | '\\u166f' .. '\\u1676' | '\\u1681' .. '\\u169a' | '\\u16a0' .. '\\u16ea' | '\\u1700' .. '\\u170c' | '\\u170e' .. '\\u1711' | '\\u1720' .. '\\u1731' | '\\u1740' .. '\\u1751' | '\\u1760' .. '\\u176c' | '\\u176e' .. '\\u1770' | '\\u1780' .. '\\u17b3' | '\\u17d7' | '\\u17dc' | '\\u17e0' .. '\\u17e9' | '\\u1810' .. '\\u1819' | '\\u1820' .. '\\u1877' | '\\u1880' .. '\\u18a8' | '\\u1900' .. '\\u191c' | '\\u1946' .. '\\u196d' | '\\u1970' .. '\\u1974' | '\\u1d00' .. '\\u1d6b' | '\\u1e00' .. '\\u1e9b' | '\\u1ea0' .. '\\u1ef9' | '\\u1f00' .. '\\u1f15' | '\\u1f18' .. '\\u1f1d' | '\\u1f20' .. '\\u1f45' | '\\u1f48' .. '\\u1f4d' | '\\u1f50' .. '\\u1f57' | '\\u1f59' | '\\u1f5b' | '\\u1f5d' | '\\u1f5f' .. '\\u1f7d' | '\\u1f80' .. '\\u1fb4' | '\\u1fb6' .. '\\u1fbc' | '\\u1fbe' | '\\u1fc2' .. '\\u1fc4' | '\\u1fc6' .. '\\u1fcc' | '\\u1fd0' .. '\\u1fd3' | '\\u1fd6' .. '\\u1fdb' | '\\u1fe0' .. '\\u1fec' | '\\u1ff2' .. '\\u1ff4' | '\\u1ff6' .. '\\u1ffc' | '\\u2071' | '\\u207f' | '\\u2102' | '\\u2107' | '\\u210a' .. '\\u2113' | '\\u2115' | '\\u2119' .. '\\u211d' | '\\u2124' | '\\u2126' | '\\u2128' | '\\u212a' .. '\\u212d' | '\\u212f' .. '\\u2131' | '\\u2133' .. '\\u2139' | '\\u213d' .. '\\u213f' | '\\u2145' .. '\\u2149' | '\\u3005' .. '\\u3006' | '\\u3031' .. '\\u3035' | '\\u303b' .. '\\u303c' | '\\u3041' .. '\\u3096' | '\\u309d' .. '\\u309f' | '\\u30a1' .. '\\u30fa' | '\\u30fc' .. '\\u30ff' | '\\u3105' .. '\\u312c' | '\\u3131' .. '\\u318e' | '\\u31a0' .. '\\u31b7' | '\\u31f0' .. '\\u31ff' | '\\u3400' .. '\\u4db5' | '\\u4e00' .. '\\u9fa5' | '\\ua000' .. '\\ua48c' | '\\uac00' .. '\\ud7a3' | '\\uf900' .. '\\ufa2d' | '\\ufa30' .. '\\ufa6a' | '\\ufb00' .. '\\ufb06' | '\\ufb13' .. '\\ufb17' | '\\ufb1d' | '\\ufb1f' .. '\\ufb28' | '\\ufb2a' .. '\\ufb36' | '\\ufb38' .. '\\ufb3c' | '\\ufb3e' | '\\ufb40' .. '\\ufb41' | '\\ufb43' .. '\\ufb44' | '\\ufb46' .. '\\ufbb1' | '\\ufbd3' .. '\\ufd3d' | '\\ufd50' .. '\\ufd8f' | '\\ufd92' .. '\\ufdc7' | '\\ufdf0' .. '\\ufdfb' | '\\ufe70' .. '\\ufe74' | '\\ufe76' .. '\\ufefc' | '\\uff10' .. '\\uff19' | '\\uff21' .. '\\uff3a' | '\\uff41' .. '\\uff5a' | '\\uff66' .. '\\uffbe' | '\\uffc2' .. '\\uffc7' | '\\uffca' .. '\\uffcf' | '\\uffd2' .. '\\uffd7' | '\\uffda' .. '\\uffdc' )
            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:
            {
            if ( (input.LA(1)>='0' && input.LA(1)<='9')||(input.LA(1)>='A' && input.LA(1)<='Z')||(input.LA(1)>='a' && input.LA(1)<='z')||input.LA(1)=='\u00AA'||input.LA(1)=='\u00B5'||input.LA(1)=='\u00BA'||(input.LA(1)>='\u00C0' && input.LA(1)<='\u00D6')||(input.LA(1)>='\u00D8' && input.LA(1)<='\u00F6')||(input.LA(1)>='\u00F8' && input.LA(1)<='\u0236')||(input.LA(1)>='\u0250' && input.LA(1)<='\u02C1')||(input.LA(1)>='\u02C6' && input.LA(1)<='\u02D1')||(input.LA(1)>='\u02E0' && input.LA(1)<='\u02E4')||input.LA(1)=='\u02EE'||input.LA(1)=='\u037A'||input.LA(1)=='\u0386'||(input.LA(1)>='\u0388' && input.LA(1)<='\u038A')||input.LA(1)=='\u038C'||(input.LA(1)>='\u038E' && input.LA(1)<='\u03A1')||(input.LA(1)>='\u03A3' && input.LA(1)<='\u03CE')||(input.LA(1)>='\u03D0' && input.LA(1)<='\u03F5')||(input.LA(1)>='\u03F7' && input.LA(1)<='\u03FB')||(input.LA(1)>='\u0400' && input.LA(1)<='\u0481')||(input.LA(1)>='\u048A' && input.LA(1)<='\u04CE')||(input.LA(1)>='\u04D0' && input.LA(1)<='\u04F5')||(input.LA(1)>='\u04F8' && input.LA(1)<='\u04F9')||(input.LA(1)>='\u0500' && input.LA(1)<='\u050F')||(input.LA(1)>='\u0531' && input.LA(1)<='\u0556')||input.LA(1)=='\u0559'||(input.LA(1)>='\u0561' && input.LA(1)<='\u0587')||(input.LA(1)>='\u05D0' && input.LA(1)<='\u05EA')||(input.LA(1)>='\u05F0' && input.LA(1)<='\u05F2')||(input.LA(1)>='\u0621' && input.LA(1)<='\u063A')||(input.LA(1)>='\u0640' && input.LA(1)<='\u064A')||(input.LA(1)>='\u0660' && input.LA(1)<='\u0669')||(input.LA(1)>='\u066E' && input.LA(1)<='\u066F')||(input.LA(1)>='\u0671' && input.LA(1)<='\u06D3')||input.LA(1)=='\u06D5'||(input.LA(1)>='\u06E5' && input.LA(1)<='\u06E6')||(input.LA(1)>='\u06EE' && input.LA(1)<='\u06FC')||input.LA(1)=='\u06FF'||input.LA(1)=='\u0710'||(input.LA(1)>='\u0712' && input.LA(1)<='\u072F')||(input.LA(1)>='\u074D' && input.LA(1)<='\u074F')||(input.LA(1)>='\u0780' && input.LA(1)<='\u07A5')||input.LA(1)=='\u07B1'||(input.LA(1)>='\u0904' && input.LA(1)<='\u0939')||input.LA(1)=='\u093D'||input.LA(1)=='\u0950'||(input.LA(1)>='\u0958' && input.LA(1)<='\u0961')||(input.LA(1)>='\u0966' && input.LA(1)<='\u096F')||(input.LA(1)>='\u0985' && input.LA(1)<='\u098C')||(input.LA(1)>='\u098F' && input.LA(1)<='\u0990')||(input.LA(1)>='\u0993' && input.LA(1)<='\u09A8')||(input.LA(1)>='\u09AA' && input.LA(1)<='\u09B0')||input.LA(1)=='\u09B2'||(input.LA(1)>='\u09B6' && input.LA(1)<='\u09B9')||input.LA(1)=='\u09BD'||(input.LA(1)>='\u09DC' && input.LA(1)<='\u09DD')||(input.LA(1)>='\u09DF' && input.LA(1)<='\u09E1')||(input.LA(1)>='\u09E6' && input.LA(1)<='\u09F1')||(input.LA(1)>='\u0A05' && input.LA(1)<='\u0A0A')||(input.LA(1)>='\u0A0F' && input.LA(1)<='\u0A10')||(input.LA(1)>='\u0A13' && input.LA(1)<='\u0A28')||(input.LA(1)>='\u0A2A' && input.LA(1)<='\u0A30')||(input.LA(1)>='\u0A32' && input.LA(1)<='\u0A33')||(input.LA(1)>='\u0A35' && input.LA(1)<='\u0A36')||(input.LA(1)>='\u0A38' && input.LA(1)<='\u0A39')||(input.LA(1)>='\u0A59' && input.LA(1)<='\u0A5C')||input.LA(1)=='\u0A5E'||(input.LA(1)>='\u0A66' && input.LA(1)<='\u0A6F')||(input.LA(1)>='\u0A72' && input.LA(1)<='\u0A74')||(input.LA(1)>='\u0A85' && input.LA(1)<='\u0A8D')||(input.LA(1)>='\u0A8F' && input.LA(1)<='\u0A91')||(input.LA(1)>='\u0A93' && input.LA(1)<='\u0AA8')||(input.LA(1)>='\u0AAA' && input.LA(1)<='\u0AB0')||(input.LA(1)>='\u0AB2' && input.LA(1)<='\u0AB3')||(input.LA(1)>='\u0AB5' && input.LA(1)<='\u0AB9')||input.LA(1)=='\u0ABD'||input.LA(1)=='\u0AD0'||(input.LA(1)>='\u0AE0' && input.LA(1)<='\u0AE1')||(input.LA(1)>='\u0AE6' && input.LA(1)<='\u0AEF')||(input.LA(1)>='\u0B05' && input.LA(1)<='\u0B0C')||(input.LA(1)>='\u0B0F' && input.LA(1)<='\u0B10')||(input.LA(1)>='\u0B13' && input.LA(1)<='\u0B28')||(input.LA(1)>='\u0B2A' && input.LA(1)<='\u0B30')||(input.LA(1)>='\u0B32' && input.LA(1)<='\u0B33')||(input.LA(1)>='\u0B35' && input.LA(1)<='\u0B39')||input.LA(1)=='\u0B3D'||(input.LA(1)>='\u0B5C' && input.LA(1)<='\u0B5D')||(input.LA(1)>='\u0B5F' && input.LA(1)<='\u0B61')||(input.LA(1)>='\u0B66' && input.LA(1)<='\u0B6F')||input.LA(1)=='\u0B71'||input.LA(1)=='\u0B83'||(input.LA(1)>='\u0B85' && input.LA(1)<='\u0B8A')||(input.LA(1)>='\u0B8E' && input.LA(1)<='\u0B90')||(input.LA(1)>='\u0B92' && input.LA(1)<='\u0B95')||(input.LA(1)>='\u0B99' && input.LA(1)<='\u0B9A')||input.LA(1)=='\u0B9C'||(input.LA(1)>='\u0B9E' && input.LA(1)<='\u0B9F')||(input.LA(1)>='\u0BA3' && input.LA(1)<='\u0BA4')||(input.LA(1)>='\u0BA8' && input.LA(1)<='\u0BAA')||(input.LA(1)>='\u0BAE' && input.LA(1)<='\u0BB5')||(input.LA(1)>='\u0BB7' && input.LA(1)<='\u0BB9')||(input.LA(1)>='\u0BE7' && input.LA(1)<='\u0BEF')||(input.LA(1)>='\u0C05' && input.LA(1)<='\u0C0C')||(input.LA(1)>='\u0C0E' && input.LA(1)<='\u0C10')||(input.LA(1)>='\u0C12' && input.LA(1)<='\u0C28')||(input.LA(1)>='\u0C2A' && input.LA(1)<='\u0C33')||(input.LA(1)>='\u0C35' && input.LA(1)<='\u0C39')||(input.LA(1)>='\u0C60' && input.LA(1)<='\u0C61')||(input.LA(1)>='\u0C66' && input.LA(1)<='\u0C6F')||(input.LA(1)>='\u0C85' && input.LA(1)<='\u0C8C')||(input.LA(1)>='\u0C8E' && input.LA(1)<='\u0C90')||(input.LA(1)>='\u0C92' && input.LA(1)<='\u0CA8')||(input.LA(1)>='\u0CAA' && input.LA(1)<='\u0CB3')||(input.LA(1)>='\u0CB5' && input.LA(1)<='\u0CB9')||input.LA(1)=='\u0CBD'||input.LA(1)=='\u0CDE'||(input.LA(1)>='\u0CE0' && input.LA(1)<='\u0CE1')||(input.LA(1)>='\u0CE6' && input.LA(1)<='\u0CEF')||(input.LA(1)>='\u0D05' && input.LA(1)<='\u0D0C')||(input.LA(1)>='\u0D0E' && input.LA(1)<='\u0D10')||(input.LA(1)>='\u0D12' && input.LA(1)<='\u0D28')||(input.LA(1)>='\u0D2A' && input.LA(1)<='\u0D39')||(input.LA(1)>='\u0D60' && input.LA(1)<='\u0D61')||(input.LA(1)>='\u0D66' && input.LA(1)<='\u0D6F')||(input.LA(1)>='\u0D85' && input.LA(1)<='\u0D96')||(input.LA(1)>='\u0D9A' && input.LA(1)<='\u0DB1')||(input.LA(1)>='\u0DB3' && input.LA(1)<='\u0DBB')||input.LA(1)=='\u0DBD'||(input.LA(1)>='\u0DC0' && input.LA(1)<='\u0DC6')||(input.LA(1)>='\u0E01' && input.LA(1)<='\u0E30')||(input.LA(1)>='\u0E32' && input.LA(1)<='\u0E33')||(input.LA(1)>='\u0E40' && input.LA(1)<='\u0E46')||(input.LA(1)>='\u0E50' && input.LA(1)<='\u0E59')||(input.LA(1)>='\u0E81' && input.LA(1)<='\u0E82')||input.LA(1)=='\u0E84'||(input.LA(1)>='\u0E87' && input.LA(1)<='\u0E88')||input.LA(1)=='\u0E8A'||input.LA(1)=='\u0E8D'||(input.LA(1)>='\u0E94' && input.LA(1)<='\u0E97')||(input.LA(1)>='\u0E99' && input.LA(1)<='\u0E9F')||(input.LA(1)>='\u0EA1' && input.LA(1)<='\u0EA3')||input.LA(1)=='\u0EA5'||input.LA(1)=='\u0EA7'||(input.LA(1)>='\u0EAA' && input.LA(1)<='\u0EAB')||(input.LA(1)>='\u0EAD' && input.LA(1)<='\u0EB0')||(input.LA(1)>='\u0EB2' && input.LA(1)<='\u0EB3')||input.LA(1)=='\u0EBD'||(input.LA(1)>='\u0EC0' && input.LA(1)<='\u0EC4')||input.LA(1)=='\u0EC6'||(input.LA(1)>='\u0ED0' && input.LA(1)<='\u0ED9')||(input.LA(1)>='\u0EDC' && input.LA(1)<='\u0EDD')||input.LA(1)=='\u0F00'||(input.LA(1)>='\u0F20' && input.LA(1)<='\u0F29')||(input.LA(1)>='\u0F40' && input.LA(1)<='\u0F47')||(input.LA(1)>='\u0F49' && input.LA(1)<='\u0F6A')||(input.LA(1)>='\u0F88' && input.LA(1)<='\u0F8B')||(input.LA(1)>='\u1000' && input.LA(1)<='\u1021')||(input.LA(1)>='\u1023' && input.LA(1)<='\u1027')||(input.LA(1)>='\u1029' && input.LA(1)<='\u102A')||(input.LA(1)>='\u1040' && input.LA(1)<='\u1049')||(input.LA(1)>='\u1050' && input.LA(1)<='\u1055')||(input.LA(1)>='\u10A0' && input.LA(1)<='\u10C5')||(input.LA(1)>='\u10D0' && input.LA(1)<='\u10F8')||(input.LA(1)>='\u1100' && input.LA(1)<='\u1159')||(input.LA(1)>='\u115F' && input.LA(1)<='\u11A2')||(input.LA(1)>='\u11A8' && input.LA(1)<='\u11F9')||(input.LA(1)>='\u1200' && input.LA(1)<='\u1206')||(input.LA(1)>='\u1208' && input.LA(1)<='\u1246')||input.LA(1)=='\u1248'||(input.LA(1)>='\u124A' && input.LA(1)<='\u124D')||(input.LA(1)>='\u1250' && input.LA(1)<='\u1256')||input.LA(1)=='\u1258'||(input.LA(1)>='\u125A' && input.LA(1)<='\u125D')||(input.LA(1)>='\u1260' && input.LA(1)<='\u1286')||input.LA(1)=='\u1288'||(input.LA(1)>='\u128A' && input.LA(1)<='\u128D')||(input.LA(1)>='\u1290' && input.LA(1)<='\u12AE')||input.LA(1)=='\u12B0'||(input.LA(1)>='\u12B2' && input.LA(1)<='\u12B5')||(input.LA(1)>='\u12B8' && input.LA(1)<='\u12BE')||input.LA(1)=='\u12C0'||(input.LA(1)>='\u12C2' && input.LA(1)<='\u12C5')||(input.LA(1)>='\u12C8' && input.LA(1)<='\u12CE')||(input.LA(1)>='\u12D0' && input.LA(1)<='\u12D6')||(input.LA(1)>='\u12D8' && input.LA(1)<='\u12EE')||(input.LA(1)>='\u12F0' && input.LA(1)<='\u130E')||input.LA(1)=='\u1310'||(input.LA(1)>='\u1312' && input.LA(1)<='\u1315')||(input.LA(1)>='\u1318' && input.LA(1)<='\u131E')||(input.LA(1)>='\u1320' && input.LA(1)<='\u1346')||(input.LA(1)>='\u1348' && input.LA(1)<='\u135A')||(input.LA(1)>='\u1369' && input.LA(1)<='\u1371')||(input.LA(1)>='\u13A0' && input.LA(1)<='\u13F4')||(input.LA(1)>='\u1401' && input.LA(1)<='\u166C')||(input.LA(1)>='\u166F' && input.LA(1)<='\u1676')||(input.LA(1)>='\u1681' && input.LA(1)<='\u169A')||(input.LA(1)>='\u16A0' && input.LA(1)<='\u16EA')||(input.LA(1)>='\u1700' && input.LA(1)<='\u170C')||(input.LA(1)>='\u170E' && input.LA(1)<='\u1711')||(input.LA(1)>='\u1720' && input.LA(1)<='\u1731')||(input.LA(1)>='\u1740' && input.LA(1)<='\u1751')||(input.LA(1)>='\u1760' && input.LA(1)<='\u176C')||(input.LA(1)>='\u176E' && input.LA(1)<='\u1770')||(input.LA(1)>='\u1780' && input.LA(1)<='\u17B3')||input.LA(1)=='\u17D7'||input.LA(1)=='\u17DC'||(input.LA(1)>='\u17E0' && input.LA(1)<='\u17E9')||(input.LA(1)>='\u1810' && input.LA(1)<='\u1819')||(input.LA(1)>='\u1820' && input.LA(1)<='\u1877')||(input.LA(1)>='\u1880' && input.LA(1)<='\u18A8')||(input.LA(1)>='\u1900' && input.LA(1)<='\u191C')||(input.LA(1)>='\u1946' && input.LA(1)<='\u196D')||(input.LA(1)>='\u1970' && input.LA(1)<='\u1974')||(input.LA(1)>='\u1D00' && input.LA(1)<='\u1D6B')||(input.LA(1)>='\u1E00' && input.LA(1)<='\u1E9B')||(input.LA(1)>='\u1EA0' && input.LA(1)<='\u1EF9')||(input.LA(1)>='\u1F00' && input.LA(1)<='\u1F15')||(input.LA(1)>='\u1F18' && input.LA(1)<='\u1F1D')||(input.LA(1)>='\u1F20' && input.LA(1)<='\u1F45')||(input.LA(1)>='\u1F48' && input.LA(1)<='\u1F4D')||(input.LA(1)>='\u1F50' && input.LA(1)<='\u1F57')||input.LA(1)=='\u1F59'||input.LA(1)=='\u1F5B'||input.LA(1)=='\u1F5D'||(input.LA(1)>='\u1F5F' && input.LA(1)<='\u1F7D')||(input.LA(1)>='\u1F80' && input.LA(1)<='\u1FB4')||(input.LA(1)>='\u1FB6' && input.LA(1)<='\u1FBC')||input.LA(1)=='\u1FBE'||(input.LA(1)>='\u1FC2' && input.LA(1)<='\u1FC4')||(input.LA(1)>='\u1FC6' && input.LA(1)<='\u1FCC')||(input.LA(1)>='\u1FD0' && input.LA(1)<='\u1FD3')||(input.LA(1)>='\u1FD6' && input.LA(1)<='\u1FDB')||(input.LA(1)>='\u1FE0' && input.LA(1)<='\u1FEC')||(input.LA(1)>='\u1FF2' && input.LA(1)<='\u1FF4')||(input.LA(1)>='\u1FF6' && input.LA(1)<='\u1FFC')||input.LA(1)=='\u2071'||input.LA(1)=='\u207F'||input.LA(1)=='\u2102'||input.LA(1)=='\u2107'||(input.LA(1)>='\u210A' && input.LA(1)<='\u2113')||input.LA(1)=='\u2115'||(input.LA(1)>='\u2119' && input.LA(1)<='\u211D')||input.LA(1)=='\u2124'||input.LA(1)=='\u2126'||input.LA(1)=='\u2128'||(input.LA(1)>='\u212A' && input.LA(1)<='\u212D')||(input.LA(1)>='\u212F' && input.LA(1)<='\u2131')||(input.LA(1)>='\u2133' && input.LA(1)<='\u2139')||(input.LA(1)>='\u213D' && input.LA(1)<='\u213F')||(input.LA(1)>='\u2145' && input.LA(1)<='\u2149')||(input.LA(1)>='\u3005' && input.LA(1)<='\u3006')||(input.LA(1)>='\u3031' && input.LA(1)<='\u3035')||(input.LA(1)>='\u303B' && input.LA(1)<='\u303C')||(input.LA(1)>='\u3041' && input.LA(1)<='\u3096')||(input.LA(1)>='\u309D' && input.LA(1)<='\u309F')||(input.LA(1)>='\u30A1' && input.LA(1)<='\u30FA')||(input.LA(1)>='\u30FC' && input.LA(1)<='\u30FF')||(input.LA(1)>='\u3105' && input.LA(1)<='\u312C')||(input.LA(1)>='\u3131' && input.LA(1)<='\u318E')||(input.LA(1)>='\u31A0' && input.LA(1)<='\u31B7')||(input.LA(1)>='\u31F0' && input.LA(1)<='\u31FF')||(input.LA(1)>='\u3400' && input.LA(1)<='\u4DB5')||(input.LA(1)>='\u4E00' && input.LA(1)<='\u9FA5')||(input.LA(1)>='\uA000' && input.LA(1)<='\uA48C')||(input.LA(1)>='\uAC00' && input.LA(1)<='\uD7A3')||(input.LA(1)>='\uF900' && input.LA(1)<='\uFA2D')||(input.LA(1)>='\uFA30' && input.LA(1)<='\uFA6A')||(input.LA(1)>='\uFB00' && input.LA(1)<='\uFB06')||(input.LA(1)>='\uFB13' && input.LA(1)<='\uFB17')||input.LA(1)=='\uFB1D'||(input.LA(1)>='\uFB1F' && input.LA(1)<='\uFB28')||(input.LA(1)>='\uFB2A' && input.LA(1)<='\uFB36')||(input.LA(1)>='\uFB38' && input.LA(1)<='\uFB3C')||input.LA(1)=='\uFB3E'||(input.LA(1)>='\uFB40' && input.LA(1)<='\uFB41')||(input.LA(1)>='\uFB43' && input.LA(1)<='\uFB44')||(input.LA(1)>='\uFB46' && input.LA(1)<='\uFBB1')||(input.LA(1)>='\uFBD3' && input.LA(1)<='\uFD3D')||(input.LA(1)>='\uFD50' && input.LA(1)<='\uFD8F')||(input.LA(1)>='\uFD92' && input.LA(1)<='\uFDC7')||(input.LA(1)>='\uFDF0' && input.LA(1)<='\uFDFB')||(input.LA(1)>='\uFE70' && input.LA(1)<='\uFE74')||(input.LA(1)>='\uFE76' && input.LA(1)<='\uFEFC')||(input.LA(1)>='\uFF10' && input.LA(1)<='\uFF19')||(input.LA(1)>='\uFF21' && input.LA(1)<='\uFF3A')||(input.LA(1)>='\uFF41' && input.LA(1)<='\uFF5A')||(input.LA(1)>='\uFF66' && input.LA(1)<='\uFFBE')||(input.LA(1)>='\uFFC2' && input.LA(1)<='\uFFC7')||(input.LA(1)>='\uFFCA' && input.LA(1)<='\uFFCF')||(input.LA(1)>='\uFFD2' && input.LA(1)<='\uFFD7')||(input.LA(1)>='\uFFDA' && input.LA(1)<='\uFFDC') ) {
                input.consume();
            state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

        }
        finally {
        }
    }
    // $ANTLR end "INWORD"

    // $ANTLR start "FLOATING_POINT_LITERAL"
    public final void mFLOATING_POINT_LITERAL() throws RecognitionException {
        try {
            int _type = FLOATING_POINT_LITERAL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            CommonToken d=null;
            CommonToken r=null;

            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1582:9: (d= START_RANGE_I r= DOTDOT | d= START_RANGE_F r= DOTDOT | ( PLUS | MINUS )? ( DIGIT )+ DOT ( DIGIT )* ( EXPONENT )? | ( PLUS | MINUS )? DOT ( DIGIT )+ ( EXPONENT )? | ( PLUS | MINUS )? ( DIGIT )+ EXPONENT )
            int alt28=5;
            alt28 = dfa28.predict(input);
            switch (alt28) {
                case 1 :
                    // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1583:9: d= START_RANGE_I r= DOTDOT
                    {
                    int dStart8689 = getCharIndex();
                    int dStartLine8689 = getLine();
                    int dStartCharPos8689 = getCharPositionInLine();
                    mSTART_RANGE_I(); if (state.failed) return ;
                    d = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, dStart8689, getCharIndex()-1);
                    d.setLine(dStartLine8689);
                    d.setCharPositionInLine(dStartCharPos8689);
                    int rStart8693 = getCharIndex();
                    int rStartLine8693 = getLine();
                    int rStartCharPos8693 = getCharPositionInLine();
                    mDOTDOT(); if (state.failed) return ;
                    r = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, rStart8693, getCharIndex()-1);
                    r.setLine(rStartLine8693);
                    r.setCharPositionInLine(rStartCharPos8693);
                    if ( state.backtracking==0 ) {

                            			d.setType(DECIMAL_INTEGER_LITERAL);
                            			emit(d);
                            			r.setType(DOTDOT);
                            			emit(r);
                          		
                    }

                    }
                    break;
                case 2 :
                    // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1590:11: d= START_RANGE_F r= DOTDOT
                    {
                    int dStart8718 = getCharIndex();
                    int dStartLine8718 = getLine();
                    int dStartCharPos8718 = getCharPositionInLine();
                    mSTART_RANGE_F(); if (state.failed) return ;
                    d = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, dStart8718, getCharIndex()-1);
                    d.setLine(dStartLine8718);
                    d.setCharPositionInLine(dStartCharPos8718);
                    int rStart8722 = getCharIndex();
                    int rStartLine8722 = getLine();
                    int rStartCharPos8722 = getCharPositionInLine();
                    mDOTDOT(); if (state.failed) return ;
                    r = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, rStart8722, getCharIndex()-1);
                    r.setLine(rStartLine8722);
                    r.setCharPositionInLine(rStartCharPos8722);
                    if ( state.backtracking==0 ) {

                            			d.setType(FLOATING_POINT_LITERAL);
                            			emit(d);
                            			r.setType(DOTDOT);
                            			emit(r);
                          		
                    }

                    }
                    break;
                case 3 :
                    // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1598:9: ( PLUS | MINUS )? ( DIGIT )+ DOT ( DIGIT )* ( EXPONENT )?
                    {
                    // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1598:9: ( PLUS | MINUS )?
                    int alt19=2;
                    int LA19_0 = input.LA(1);

                    if ( (LA19_0=='+'||LA19_0=='-') ) {
                        alt19=1;
                    }
                    switch (alt19) {
                        case 1 :
                            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:
                            {
                            if ( input.LA(1)=='+'||input.LA(1)=='-' ) {
                                input.consume();
                            state.failed=false;
                            }
                            else {
                                if (state.backtracking>0) {state.failed=true; return ;}
                                MismatchedSetException mse = new MismatchedSetException(null,input);
                                recover(mse);
                                throw mse;}


                            }
                            break;

                    }

                    // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1602:9: ( DIGIT )+
                    int cnt20=0;
                    loop20:
                    do {
                        int alt20=2;
                        int LA20_0 = input.LA(1);

                        if ( ((LA20_0>='0' && LA20_0<='9')) ) {
                            alt20=1;
                        }


                        switch (alt20) {
                    	case 1 :
                    	    // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1602:9: DIGIT
                    	    {
                    	    mDIGIT(); if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt20 >= 1 ) break loop20;
                    	    if (state.backtracking>0) {state.failed=true; return ;}
                                EarlyExitException eee =
                                    new EarlyExitException(20, input);
                                throw eee;
                        }
                        cnt20++;
                    } while (true);

                    mDOT(); if (state.failed) return ;
                    // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1602:20: ( DIGIT )*
                    loop21:
                    do {
                        int alt21=2;
                        int LA21_0 = input.LA(1);

                        if ( ((LA21_0>='0' && LA21_0<='9')) ) {
                            alt21=1;
                        }


                        switch (alt21) {
                    	case 1 :
                    	    // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1602:20: DIGIT
                    	    {
                    	    mDIGIT(); if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    break loop21;
                        }
                    } while (true);

                    // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1602:27: ( EXPONENT )?
                    int alt22=2;
                    int LA22_0 = input.LA(1);

                    if ( (LA22_0=='E'||LA22_0=='e') ) {
                        alt22=1;
                    }
                    switch (alt22) {
                        case 1 :
                            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1602:27: EXPONENT
                            {
                            mEXPONENT(); if (state.failed) return ;

                            }
                            break;

                    }


                    }
                    break;
                case 4 :
                    // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1604:9: ( PLUS | MINUS )? DOT ( DIGIT )+ ( EXPONENT )?
                    {
                    // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1604:9: ( PLUS | MINUS )?
                    int alt23=2;
                    int LA23_0 = input.LA(1);

                    if ( (LA23_0=='+'||LA23_0=='-') ) {
                        alt23=1;
                    }
                    switch (alt23) {
                        case 1 :
                            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:
                            {
                            if ( input.LA(1)=='+'||input.LA(1)=='-' ) {
                                input.consume();
                            state.failed=false;
                            }
                            else {
                                if (state.backtracking>0) {state.failed=true; return ;}
                                MismatchedSetException mse = new MismatchedSetException(null,input);
                                recover(mse);
                                throw mse;}


                            }
                            break;

                    }

                    mDOT(); if (state.failed) return ;
                    // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1608:13: ( DIGIT )+
                    int cnt24=0;
                    loop24:
                    do {
                        int alt24=2;
                        int LA24_0 = input.LA(1);

                        if ( ((LA24_0>='0' && LA24_0<='9')) ) {
                            alt24=1;
                        }


                        switch (alt24) {
                    	case 1 :
                    	    // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1608:13: DIGIT
                    	    {
                    	    mDIGIT(); if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt24 >= 1 ) break loop24;
                    	    if (state.backtracking>0) {state.failed=true; return ;}
                                EarlyExitException eee =
                                    new EarlyExitException(24, input);
                                throw eee;
                        }
                        cnt24++;
                    } while (true);

                    // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1608:20: ( EXPONENT )?
                    int alt25=2;
                    int LA25_0 = input.LA(1);

                    if ( (LA25_0=='E'||LA25_0=='e') ) {
                        alt25=1;
                    }
                    switch (alt25) {
                        case 1 :
                            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1608:20: EXPONENT
                            {
                            mEXPONENT(); if (state.failed) return ;

                            }
                            break;

                    }


                    }
                    break;
                case 5 :
                    // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1610:9: ( PLUS | MINUS )? ( DIGIT )+ EXPONENT
                    {
                    // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1610:9: ( PLUS | MINUS )?
                    int alt26=2;
                    int LA26_0 = input.LA(1);

                    if ( (LA26_0=='+'||LA26_0=='-') ) {
                        alt26=1;
                    }
                    switch (alt26) {
                        case 1 :
                            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:
                            {
                            if ( input.LA(1)=='+'||input.LA(1)=='-' ) {
                                input.consume();
                            state.failed=false;
                            }
                            else {
                                if (state.backtracking>0) {state.failed=true; return ;}
                                MismatchedSetException mse = new MismatchedSetException(null,input);
                                recover(mse);
                                throw mse;}


                            }
                            break;

                    }

                    // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1614:9: ( DIGIT )+
                    int cnt27=0;
                    loop27:
                    do {
                        int alt27=2;
                        int LA27_0 = input.LA(1);

                        if ( ((LA27_0>='0' && LA27_0<='9')) ) {
                            alt27=1;
                        }


                        switch (alt27) {
                    	case 1 :
                    	    // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1614:9: DIGIT
                    	    {
                    	    mDIGIT(); if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt27 >= 1 ) break loop27;
                    	    if (state.backtracking>0) {state.failed=true; return ;}
                                EarlyExitException eee =
                                    new EarlyExitException(27, input);
                                throw eee;
                        }
                        cnt27++;
                    } while (true);

                    mEXPONENT(); if (state.failed) return ;

                    }
                    break;

            }
            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "FLOATING_POINT_LITERAL"

    // $ANTLR start "START_RANGE_I"
    public final void mSTART_RANGE_I() throws RecognitionException {
        try {
            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1619:9: ( ( PLUS | MINUS )? ( DIGIT )+ )
            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1620:9: ( PLUS | MINUS )? ( DIGIT )+
            {
            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1620:9: ( PLUS | MINUS )?
            int alt29=2;
            int LA29_0 = input.LA(1);

            if ( (LA29_0=='+'||LA29_0=='-') ) {
                alt29=1;
            }
            switch (alt29) {
                case 1 :
                    // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:
                    {
                    if ( input.LA(1)=='+'||input.LA(1)=='-' ) {
                        input.consume();
                    state.failed=false;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return ;}
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        recover(mse);
                        throw mse;}


                    }
                    break;

            }

            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1624:9: ( DIGIT )+
            int cnt30=0;
            loop30:
            do {
                int alt30=2;
                int LA30_0 = input.LA(1);

                if ( ((LA30_0>='0' && LA30_0<='9')) ) {
                    alt30=1;
                }


                switch (alt30) {
            	case 1 :
            	    // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1624:9: DIGIT
            	    {
            	    mDIGIT(); if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    if ( cnt30 >= 1 ) break loop30;
            	    if (state.backtracking>0) {state.failed=true; return ;}
                        EarlyExitException eee =
                            new EarlyExitException(30, input);
                        throw eee;
                }
                cnt30++;
            } while (true);


            }

        }
        finally {
        }
    }
    // $ANTLR end "START_RANGE_I"

    // $ANTLR start "START_RANGE_F"
    public final void mSTART_RANGE_F() throws RecognitionException {
        try {
            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1629:9: ( ( PLUS | MINUS )? ( DIGIT )+ DOT )
            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1630:9: ( PLUS | MINUS )? ( DIGIT )+ DOT
            {
            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1630:9: ( PLUS | MINUS )?
            int alt31=2;
            int LA31_0 = input.LA(1);

            if ( (LA31_0=='+'||LA31_0=='-') ) {
                alt31=1;
            }
            switch (alt31) {
                case 1 :
                    // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:
                    {
                    if ( input.LA(1)=='+'||input.LA(1)=='-' ) {
                        input.consume();
                    state.failed=false;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return ;}
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        recover(mse);
                        throw mse;}


                    }
                    break;

            }

            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1634:9: ( DIGIT )+
            int cnt32=0;
            loop32:
            do {
                int alt32=2;
                int LA32_0 = input.LA(1);

                if ( ((LA32_0>='0' && LA32_0<='9')) ) {
                    alt32=1;
                }


                switch (alt32) {
            	case 1 :
            	    // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1634:9: DIGIT
            	    {
            	    mDIGIT(); if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    if ( cnt32 >= 1 ) break loop32;
            	    if (state.backtracking>0) {state.failed=true; return ;}
                        EarlyExitException eee =
                            new EarlyExitException(32, input);
                        throw eee;
                }
                cnt32++;
            } while (true);

            mDOT(); if (state.failed) return ;

            }

        }
        finally {
        }
    }
    // $ANTLR end "START_RANGE_F"

    // $ANTLR start "DECIMAL_NUMERAL"
    public final void mDECIMAL_NUMERAL() throws RecognitionException {
        try {
            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1642:9: ( ZERO_DIGIT | NON_ZERO_DIGIT ( DIGIT )* )
            int alt34=2;
            int LA34_0 = input.LA(1);

            if ( (LA34_0=='0') ) {
                alt34=1;
            }
            else if ( ((LA34_0>='1' && LA34_0<='9')) ) {
                alt34=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 34, 0, input);

                throw nvae;
            }
            switch (alt34) {
                case 1 :
                    // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1643:9: ZERO_DIGIT
                    {
                    mZERO_DIGIT(); if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1644:11: NON_ZERO_DIGIT ( DIGIT )*
                    {
                    mNON_ZERO_DIGIT(); if (state.failed) return ;
                    // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1644:26: ( DIGIT )*
                    loop33:
                    do {
                        int alt33=2;
                        int LA33_0 = input.LA(1);

                        if ( ((LA33_0>='0' && LA33_0<='9')) ) {
                            alt33=1;
                        }


                        switch (alt33) {
                    	case 1 :
                    	    // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1644:26: DIGIT
                    	    {
                    	    mDIGIT(); if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    break loop33;
                        }
                    } while (true);


                    }
                    break;

            }
        }
        finally {
        }
    }
    // $ANTLR end "DECIMAL_NUMERAL"

    // $ANTLR start "DIGIT"
    public final void mDIGIT() throws RecognitionException {
        try {
            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1649:9: ( ZERO_DIGIT | NON_ZERO_DIGIT )
            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:
            {
            if ( (input.LA(1)>='0' && input.LA(1)<='9') ) {
                input.consume();
            state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

        }
        finally {
        }
    }
    // $ANTLR end "DIGIT"

    // $ANTLR start "ZERO_DIGIT"
    public final void mZERO_DIGIT() throws RecognitionException {
        try {
            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1656:9: ( '0' )
            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1657:9: '0'
            {
            match('0'); if (state.failed) return ;

            }

        }
        finally {
        }
    }
    // $ANTLR end "ZERO_DIGIT"

    // $ANTLR start "NON_ZERO_DIGIT"
    public final void mNON_ZERO_DIGIT() throws RecognitionException {
        try {
            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1662:9: ( '1' .. '9' )
            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1663:9: '1' .. '9'
            {
            matchRange('1','9'); if (state.failed) return ;

            }

        }
        finally {
        }
    }
    // $ANTLR end "NON_ZERO_DIGIT"

    // $ANTLR start "E"
    public final void mE() throws RecognitionException {
        try {
            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1668:9: ( ( 'e' | 'E' ) )
            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1669:9: ( 'e' | 'E' )
            {
            if ( input.LA(1)=='E'||input.LA(1)=='e' ) {
                input.consume();
            state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

        }
        finally {
        }
    }
    // $ANTLR end "E"

    // $ANTLR start "EXPONENT"
    public final void mEXPONENT() throws RecognitionException {
        try {
            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1677:9: ( E SIGNED_INTEGER )
            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1678:9: E SIGNED_INTEGER
            {
            mE(); if (state.failed) return ;
            mSIGNED_INTEGER(); if (state.failed) return ;

            }

        }
        finally {
        }
    }
    // $ANTLR end "EXPONENT"

    // $ANTLR start "SIGNED_INTEGER"
    public final void mSIGNED_INTEGER() throws RecognitionException {
        try {
            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1683:9: ( ( PLUS | MINUS )? ( DIGIT )+ )
            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1684:9: ( PLUS | MINUS )? ( DIGIT )+
            {
            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1684:9: ( PLUS | MINUS )?
            int alt35=2;
            int LA35_0 = input.LA(1);

            if ( (LA35_0=='+'||LA35_0=='-') ) {
                alt35=1;
            }
            switch (alt35) {
                case 1 :
                    // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:
                    {
                    if ( input.LA(1)=='+'||input.LA(1)=='-' ) {
                        input.consume();
                    state.failed=false;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return ;}
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        recover(mse);
                        throw mse;}


                    }
                    break;

            }

            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1688:9: ( DIGIT )+
            int cnt36=0;
            loop36:
            do {
                int alt36=2;
                int LA36_0 = input.LA(1);

                if ( ((LA36_0>='0' && LA36_0<='9')) ) {
                    alt36=1;
                }


                switch (alt36) {
            	case 1 :
            	    // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1688:9: DIGIT
            	    {
            	    mDIGIT(); if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    if ( cnt36 >= 1 ) break loop36;
            	    if (state.backtracking>0) {state.failed=true; return ;}
                        EarlyExitException eee =
                            new EarlyExitException(36, input);
                        throw eee;
                }
                cnt36++;
            } while (true);


            }

        }
        finally {
        }
    }
    // $ANTLR end "SIGNED_INTEGER"

    // $ANTLR start "WS"
    public final void mWS() throws RecognitionException {
        try {
            int _type = WS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1697:9: ( ( ' ' | '\\t' | '\\r' | '\\n' | '\\u000C' | '\\u00a0' | '\\u1680' | '\\u180e' | '\\u2000' .. '\\u200b' | '\\u2028' .. '\\u2029' | '\\u202f' | '\\u205f' | '\\u3000' )+ )
            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1698:9: ( ' ' | '\\t' | '\\r' | '\\n' | '\\u000C' | '\\u00a0' | '\\u1680' | '\\u180e' | '\\u2000' .. '\\u200b' | '\\u2028' .. '\\u2029' | '\\u202f' | '\\u205f' | '\\u3000' )+
            {
            // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1698:9: ( ' ' | '\\t' | '\\r' | '\\n' | '\\u000C' | '\\u00a0' | '\\u1680' | '\\u180e' | '\\u2000' .. '\\u200b' | '\\u2028' .. '\\u2029' | '\\u202f' | '\\u205f' | '\\u3000' )+
            int cnt37=0;
            loop37:
            do {
                int alt37=2;
                int LA37_0 = input.LA(1);

                if ( ((LA37_0>='\t' && LA37_0<='\n')||(LA37_0>='\f' && LA37_0<='\r')||LA37_0==' '||LA37_0=='\u00A0'||LA37_0=='\u1680'||LA37_0=='\u180E'||(LA37_0>='\u2000' && LA37_0<='\u200B')||(LA37_0>='\u2028' && LA37_0<='\u2029')||LA37_0=='\u202F'||LA37_0=='\u205F'||LA37_0=='\u3000') ) {
                    alt37=1;
                }


                switch (alt37) {
            	case 1 :
            	    // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:
            	    {
            	    if ( (input.LA(1)>='\t' && input.LA(1)<='\n')||(input.LA(1)>='\f' && input.LA(1)<='\r')||input.LA(1)==' '||input.LA(1)=='\u00A0'||input.LA(1)=='\u1680'||input.LA(1)=='\u180E'||(input.LA(1)>='\u2000' && input.LA(1)<='\u200B')||(input.LA(1)>='\u2028' && input.LA(1)<='\u2029')||input.LA(1)=='\u202F'||input.LA(1)=='\u205F'||input.LA(1)=='\u3000' ) {
            	        input.consume();
            	    state.failed=false;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return ;}
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


            	    }
            	    break;

            	default :
            	    if ( cnt37 >= 1 ) break loop37;
            	    if (state.backtracking>0) {state.failed=true; return ;}
                        EarlyExitException eee =
                            new EarlyExitException(37, input);
                        throw eee;
                }
                cnt37++;
            } while (true);

            if ( state.backtracking==0 ) {
               _channel = HIDDEN; 
            }

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "WS"

    public void mTokens() throws RecognitionException {
        // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1:8: ( FTSPHRASE | URI | OR | AND | NOT | TILDA | LPAREN | RPAREN | PLUS | MINUS | COLON | STAR | DOTDOT | DOT | AMP | EXCLAMATION | BAR | EQUALS | QUESTION_MARK | LCURL | RCURL | LSQUARE | RSQUARE | TO | COMMA | CARAT | DOLLAR | GT | LT | AT | PERCENT | ID | DECIMAL_INTEGER_LITERAL | FTSWORD | FTSPRE | FTSWILD | FLOATING_POINT_LITERAL | WS )
        int alt38=38;
        alt38 = dfa38.predict(input);
        switch (alt38) {
            case 1 :
                // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1:10: FTSPHRASE
                {
                mFTSPHRASE(); if (state.failed) return ;

                }
                break;
            case 2 :
                // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1:20: URI
                {
                mURI(); if (state.failed) return ;

                }
                break;
            case 3 :
                // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1:24: OR
                {
                mOR(); if (state.failed) return ;

                }
                break;
            case 4 :
                // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1:27: AND
                {
                mAND(); if (state.failed) return ;

                }
                break;
            case 5 :
                // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1:31: NOT
                {
                mNOT(); if (state.failed) return ;

                }
                break;
            case 6 :
                // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1:35: TILDA
                {
                mTILDA(); if (state.failed) return ;

                }
                break;
            case 7 :
                // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1:41: LPAREN
                {
                mLPAREN(); if (state.failed) return ;

                }
                break;
            case 8 :
                // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1:48: RPAREN
                {
                mRPAREN(); if (state.failed) return ;

                }
                break;
            case 9 :
                // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1:55: PLUS
                {
                mPLUS(); if (state.failed) return ;

                }
                break;
            case 10 :
                // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1:60: MINUS
                {
                mMINUS(); if (state.failed) return ;

                }
                break;
            case 11 :
                // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1:66: COLON
                {
                mCOLON(); if (state.failed) return ;

                }
                break;
            case 12 :
                // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1:72: STAR
                {
                mSTAR(); if (state.failed) return ;

                }
                break;
            case 13 :
                // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1:77: DOTDOT
                {
                mDOTDOT(); if (state.failed) return ;

                }
                break;
            case 14 :
                // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1:84: DOT
                {
                mDOT(); if (state.failed) return ;

                }
                break;
            case 15 :
                // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1:88: AMP
                {
                mAMP(); if (state.failed) return ;

                }
                break;
            case 16 :
                // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1:92: EXCLAMATION
                {
                mEXCLAMATION(); if (state.failed) return ;

                }
                break;
            case 17 :
                // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1:104: BAR
                {
                mBAR(); if (state.failed) return ;

                }
                break;
            case 18 :
                // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1:108: EQUALS
                {
                mEQUALS(); if (state.failed) return ;

                }
                break;
            case 19 :
                // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1:115: QUESTION_MARK
                {
                mQUESTION_MARK(); if (state.failed) return ;

                }
                break;
            case 20 :
                // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1:129: LCURL
                {
                mLCURL(); if (state.failed) return ;

                }
                break;
            case 21 :
                // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1:135: RCURL
                {
                mRCURL(); if (state.failed) return ;

                }
                break;
            case 22 :
                // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1:141: LSQUARE
                {
                mLSQUARE(); if (state.failed) return ;

                }
                break;
            case 23 :
                // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1:149: RSQUARE
                {
                mRSQUARE(); if (state.failed) return ;

                }
                break;
            case 24 :
                // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1:157: TO
                {
                mTO(); if (state.failed) return ;

                }
                break;
            case 25 :
                // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1:160: COMMA
                {
                mCOMMA(); if (state.failed) return ;

                }
                break;
            case 26 :
                // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1:166: CARAT
                {
                mCARAT(); if (state.failed) return ;

                }
                break;
            case 27 :
                // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1:172: DOLLAR
                {
                mDOLLAR(); if (state.failed) return ;

                }
                break;
            case 28 :
                // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1:179: GT
                {
                mGT(); if (state.failed) return ;

                }
                break;
            case 29 :
                // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1:182: LT
                {
                mLT(); if (state.failed) return ;

                }
                break;
            case 30 :
                // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1:185: AT
                {
                mAT(); if (state.failed) return ;

                }
                break;
            case 31 :
                // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1:188: PERCENT
                {
                mPERCENT(); if (state.failed) return ;

                }
                break;
            case 32 :
                // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1:196: ID
                {
                mID(); if (state.failed) return ;

                }
                break;
            case 33 :
                // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1:199: DECIMAL_INTEGER_LITERAL
                {
                mDECIMAL_INTEGER_LITERAL(); if (state.failed) return ;

                }
                break;
            case 34 :
                // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1:223: FTSWORD
                {
                mFTSWORD(); if (state.failed) return ;

                }
                break;
            case 35 :
                // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1:231: FTSPRE
                {
                mFTSPRE(); if (state.failed) return ;

                }
                break;
            case 36 :
                // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1:238: FTSWILD
                {
                mFTSWILD(); if (state.failed) return ;

                }
                break;
            case 37 :
                // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1:246: FLOATING_POINT_LITERAL
                {
                mFLOATING_POINT_LITERAL(); if (state.failed) return ;

                }
                break;
            case 38 :
                // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1:269: WS
                {
                mWS(); if (state.failed) return ;

                }
                break;

        }

    }

    // $ANTLR start synpred1_FTS
    public final void synpred1_FTS_fragment() throws RecognitionException {   
        // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:899:17: ( F_URI_ALPHA | F_URI_DIGIT | F_URI_OTHER )
        // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:
        {
        if ( input.LA(1)=='!'||input.LA(1)=='$'||(input.LA(1)>='&' && input.LA(1)<='.')||(input.LA(1)>='0' && input.LA(1)<='9')||input.LA(1)==';'||input.LA(1)=='='||(input.LA(1)>='@' && input.LA(1)<='[')||input.LA(1)==']'||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z')||input.LA(1)=='~' ) {
            input.consume();
        state.failed=false;
        }
        else {
            if (state.backtracking>0) {state.failed=true; return ;}
            MismatchedSetException mse = new MismatchedSetException(null,input);
            recover(mse);
            throw mse;}


        }
    }
    // $ANTLR end synpred1_FTS

    // $ANTLR start synpred2_FTS
    public final void synpred2_FTS_fragment() throws RecognitionException {   
        // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:913:19: ( '//' )
        // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:913:20: '//'
        {
        match("//"); if (state.failed) return ;


        }
    }
    // $ANTLR end synpred2_FTS

    // $ANTLR start synpred3_FTS
    public final void synpred3_FTS_fragment() throws RecognitionException {   
        // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:915:25: ( F_URI_ALPHA | F_URI_DIGIT | F_URI_OTHER | COLON )
        // W:\\alfresco\\BRANCHES\\DEV\\SWIFT\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:
        {
        if ( input.LA(1)=='!'||input.LA(1)=='$'||(input.LA(1)>='&' && input.LA(1)<='.')||(input.LA(1)>='0' && input.LA(1)<=';')||input.LA(1)=='='||(input.LA(1)>='@' && input.LA(1)<='[')||input.LA(1)==']'||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z')||input.LA(1)=='~' ) {
            input.consume();
        state.failed=false;
        }
        else {
            if (state.backtracking>0) {state.failed=true; return ;}
            MismatchedSetException mse = new MismatchedSetException(null,input);
            recover(mse);
            throw mse;}


        }
    }
    // $ANTLR end synpred3_FTS

    public final boolean synpred1_FTS() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred1_FTS_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred2_FTS() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred2_FTS_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred3_FTS() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred3_FTS_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }


    protected DFA5 dfa5 = new DFA5(this);
    protected DFA28 dfa28 = new DFA28(this);
    protected DFA38 dfa38 = new DFA38(this);
    static final String DFA5_eotS =
        "\11\uffff";
    static final String DFA5_eofS =
        "\11\uffff";
    static final String DFA5_minS =
        "\2\41\5\uffff\1\0\1\uffff";
    static final String DFA5_maxS =
        "\2\176\5\uffff\1\0\1\uffff";
    static final String DFA5_acceptS =
        "\2\uffff\5\2\1\uffff\1\1";
    static final String DFA5_specialS =
        "\7\uffff\1\0\1\uffff}>";
    static final String[] DFA5_transitionS = {
            "\1\1\1\uffff\1\5\1\1\1\uffff\11\1\1\2\12\1\1\3\1\1\1\uffff"+
            "\1\1\1\uffff\1\4\34\1\1\uffff\1\1\1\uffff\1\1\1\uffff\32\1\2"+
            "\uffff\1\6\1\1",
            "\1\1\1\uffff\1\5\1\1\1\uffff\11\1\1\3\12\1\1\7\1\1\1\uffff"+
            "\1\1\1\uffff\1\4\34\1\1\uffff\1\1\1\uffff\1\1\1\uffff\32\1\2"+
            "\uffff\1\6\1\1",
            "",
            "",
            "",
            "",
            "",
            "\1\uffff",
            ""
    };

    static final short[] DFA5_eot = DFA.unpackEncodedString(DFA5_eotS);
    static final short[] DFA5_eof = DFA.unpackEncodedString(DFA5_eofS);
    static final char[] DFA5_min = DFA.unpackEncodedStringToUnsignedChars(DFA5_minS);
    static final char[] DFA5_max = DFA.unpackEncodedStringToUnsignedChars(DFA5_maxS);
    static final short[] DFA5_accept = DFA.unpackEncodedString(DFA5_acceptS);
    static final short[] DFA5_special = DFA.unpackEncodedString(DFA5_specialS);
    static final short[][] DFA5_transition;

    static {
        int numStates = DFA5_transitionS.length;
        DFA5_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA5_transition[i] = DFA.unpackEncodedString(DFA5_transitionS[i]);
        }
    }

    class DFA5 extends DFA {

        public DFA5(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 5;
            this.eot = DFA5_eot;
            this.eof = DFA5_eof;
            this.min = DFA5_min;
            this.max = DFA5_max;
            this.accept = DFA5_accept;
            this.special = DFA5_special;
            this.transition = DFA5_transition;
        }
        public String getDescription() {
            return "898:9: ( ( F_URI_ALPHA | F_URI_DIGIT | F_URI_OTHER )=> ( F_URI_ALPHA | F_URI_DIGIT | F_URI_OTHER )+ COLON )?";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            IntStream input = _input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA5_7 = input.LA(1);

                         
                        int index5_7 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_FTS()) ) {s = 8;}

                        else if ( (true) ) {s = 6;}

                         
                        input.seek(index5_7);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 5, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA28_eotS =
        "\4\uffff\1\7\1\uffff\1\13\5\uffff";
    static final String DFA28_eofS =
        "\14\uffff";
    static final String DFA28_minS =
        "\1\53\2\56\1\uffff\1\56\1\uffff\1\56\5\uffff";
    static final String DFA28_maxS =
        "\2\71\1\145\1\uffff\1\145\1\uffff\1\56\5\uffff";
    static final String DFA28_acceptS =
        "\3\uffff\1\4\1\uffff\1\5\1\uffff\3\3\1\2\1\1";
    static final String DFA28_specialS =
        "\14\uffff}>";
    static final String[] DFA28_transitionS = {
            "\1\1\1\uffff\1\1\1\3\1\uffff\12\2",
            "\1\3\1\uffff\12\2",
            "\1\4\1\uffff\12\2\13\uffff\1\5\37\uffff\1\5",
            "",
            "\1\6\1\uffff\12\10\13\uffff\1\11\37\uffff\1\11",
            "",
            "\1\12",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA28_eot = DFA.unpackEncodedString(DFA28_eotS);
    static final short[] DFA28_eof = DFA.unpackEncodedString(DFA28_eofS);
    static final char[] DFA28_min = DFA.unpackEncodedStringToUnsignedChars(DFA28_minS);
    static final char[] DFA28_max = DFA.unpackEncodedStringToUnsignedChars(DFA28_maxS);
    static final short[] DFA28_accept = DFA.unpackEncodedString(DFA28_acceptS);
    static final short[] DFA28_special = DFA.unpackEncodedString(DFA28_specialS);
    static final short[][] DFA28_transition;

    static {
        int numStates = DFA28_transitionS.length;
        DFA28_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA28_transition[i] = DFA.unpackEncodedString(DFA28_transitionS[i]);
        }
    }

    class DFA28 extends DFA {

        public DFA28(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 28;
            this.eot = DFA28_eot;
            this.eof = DFA28_eof;
            this.min = DFA28_min;
            this.max = DFA28_max;
            this.accept = DFA28_accept;
            this.special = DFA28_special;
            this.transition = DFA28_transition;
        }
        public String getDescription() {
            return "1580:1: FLOATING_POINT_LITERAL : (d= START_RANGE_I r= DOTDOT | d= START_RANGE_F r= DOTDOT | ( PLUS | MINUS )? ( DIGIT )+ DOT ( DIGIT )* ( EXPONENT )? | ( PLUS | MINUS )? DOT ( DIGIT )+ ( EXPONENT )? | ( PLUS | MINUS )? ( DIGIT )+ EXPONENT );";
        }
    }
    static final String DFA38_eotS =
        "\3\uffff\1\54\3\66\3\uffff\1\75\1\101\1\uffff\1\102\1\107\4\uffff"+
        "\1\111\3\uffff\1\66\7\uffff\1\66\2\114\2\uffff\1\123\10\uffff\2"+
        "\124\3\66\5\uffff\1\127\1\uffff\4\66\1\uffff\2\134\12\uffff\2\140"+
        "\1\uffff\1\123\1\uffff\1\123\1\114\2\123\2\uffff\2\66\1\uffff\2"+
        "\147\2\150\3\uffff\1\134\1\uffff\1\151\1\uffff\1\123\3\66\3\uffff"+
        "\1\123\3\66\1\123\3\66\1\123\3\66";
    static final String DFA38_eofS =
        "\166\uffff";
    static final String DFA38_minS =
        "\1\11\2\uffff\1\41\3\43\3\uffff\2\56\1\uffff\1\52\1\56\4\uffff"+
        "\1\52\3\uffff\1\43\7\uffff\1\43\2\52\1\0\1\uffff\1\52\10\uffff\5"+
        "\43\3\uffff\1\0\1\uffff\1\52\1\uffff\4\43\1\uffff\2\56\12\uffff"+
        "\2\43\1\uffff\1\52\1\uffff\4\52\2\uffff\2\43\1\uffff\4\43\3\uffff"+
        "\1\56\1\uffff\1\52\1\uffff\1\52\3\43\3\uffff\1\52\3\43\1\52\3\43"+
        "\1\52\3\43";
    static final String DFA38_maxS =
        "\1\uffdc\2\uffff\1\176\3\uffdc\3\uffff\2\71\1\uffff\1\uffdc\1\71"+
        "\4\uffff\1\uffdc\3\uffff\1\uffdc\7\uffff\3\uffdc\1\uffff\1\uffff"+
        "\1\uffdc\10\uffff\5\uffdc\3\uffff\1\uffff\1\uffff\1\uffdc\1\uffff"+
        "\4\uffdc\1\uffff\2\145\12\uffff\2\uffdc\1\uffff\1\uffdc\1\uffff"+
        "\4\uffdc\2\uffff\2\uffdc\1\uffff\4\uffdc\3\uffff\1\145\1\uffff\1"+
        "\uffdc\1\uffff\4\uffdc\3\uffff\14\uffdc";
    static final String DFA38_acceptS =
        "\1\uffff\2\1\4\uffff\1\6\1\7\1\10\2\uffff\1\13\2\uffff\1\17\1\20"+
        "\1\21\1\22\1\uffff\1\25\1\26\1\27\1\uffff\1\31\1\32\1\33\1\34\1"+
        "\35\1\36\1\37\4\uffff\1\40\1\uffff\1\46\6\2\1\24\5\uffff\3\40\1"+
        "\uffff\1\40\1\uffff\1\44\4\uffff\1\11\2\uffff\1\45\1\12\1\14\3\44"+
        "\1\15\1\16\1\45\1\23\2\uffff\1\41\1\uffff\1\45\4\uffff\1\42\1\3"+
        "\2\uffff\1\43\4\uffff\1\41\2\45\1\uffff\1\30\1\uffff\1\45\4\uffff"+
        "\1\4\1\5\1\42\14\uffff";
    static final String DFA38_specialS =
        "\42\uffff\1\1\22\uffff\1\0\100\uffff}>";
    static final String[] DFA38_transitionS = {
            "\2\45\1\uffff\2\45\22\uffff\1\45\1\20\1\1\1\uffff\1\32\1\36"+
            "\1\17\1\2\1\10\1\11\1\15\1\12\1\30\1\13\1\16\1\uffff\1\40\11"+
            "\41\1\14\1\uffff\1\34\1\22\1\33\1\23\1\35\1\5\14\37\1\6\1\4"+
            "\4\37\1\27\6\37\1\25\1\42\1\26\1\31\1\43\1\uffff\1\5\14\37\1"+
            "\6\1\4\4\37\1\27\6\37\1\3\1\21\1\24\1\7\41\uffff\1\45\11\uffff"+
            "\1\44\12\uffff\1\44\4\uffff\1\44\5\uffff\27\44\1\uffff\37\44"+
            "\1\uffff\u013f\44\31\uffff\162\44\4\uffff\14\44\16\uffff\5\44"+
            "\11\uffff\1\44\u008b\uffff\1\44\13\uffff\1\44\1\uffff\3\44\1"+
            "\uffff\1\44\1\uffff\24\44\1\uffff\54\44\1\uffff\46\44\1\uffff"+
            "\5\44\4\uffff\u0082\44\10\uffff\105\44\1\uffff\46\44\2\uffff"+
            "\2\44\6\uffff\20\44\41\uffff\46\44\2\uffff\1\44\7\uffff\47\44"+
            "\110\uffff\33\44\5\uffff\3\44\56\uffff\32\44\5\uffff\13\44\25"+
            "\uffff\12\44\4\uffff\2\44\1\uffff\143\44\1\uffff\1\44\17\uffff"+
            "\2\44\7\uffff\17\44\2\uffff\1\44\20\uffff\1\44\1\uffff\36\44"+
            "\35\uffff\3\44\60\uffff\46\44\13\uffff\1\44\u0152\uffff\66\44"+
            "\3\uffff\1\44\22\uffff\1\44\7\uffff\12\44\4\uffff\12\44\25\uffff"+
            "\10\44\2\uffff\2\44\2\uffff\26\44\1\uffff\7\44\1\uffff\1\44"+
            "\3\uffff\4\44\3\uffff\1\44\36\uffff\2\44\1\uffff\3\44\4\uffff"+
            "\14\44\23\uffff\6\44\4\uffff\2\44\2\uffff\26\44\1\uffff\7\44"+
            "\1\uffff\2\44\1\uffff\2\44\1\uffff\2\44\37\uffff\4\44\1\uffff"+
            "\1\44\7\uffff\12\44\2\uffff\3\44\20\uffff\11\44\1\uffff\3\44"+
            "\1\uffff\26\44\1\uffff\7\44\1\uffff\2\44\1\uffff\5\44\3\uffff"+
            "\1\44\22\uffff\1\44\17\uffff\2\44\4\uffff\12\44\25\uffff\10"+
            "\44\2\uffff\2\44\2\uffff\26\44\1\uffff\7\44\1\uffff\2\44\1\uffff"+
            "\5\44\3\uffff\1\44\36\uffff\2\44\1\uffff\3\44\4\uffff\12\44"+
            "\1\uffff\1\44\21\uffff\1\44\1\uffff\6\44\3\uffff\3\44\1\uffff"+
            "\4\44\3\uffff\2\44\1\uffff\1\44\1\uffff\2\44\3\uffff\2\44\3"+
            "\uffff\3\44\3\uffff\10\44\1\uffff\3\44\55\uffff\11\44\25\uffff"+
            "\10\44\1\uffff\3\44\1\uffff\27\44\1\uffff\12\44\1\uffff\5\44"+
            "\46\uffff\2\44\4\uffff\12\44\25\uffff\10\44\1\uffff\3\44\1\uffff"+
            "\27\44\1\uffff\12\44\1\uffff\5\44\3\uffff\1\44\40\uffff\1\44"+
            "\1\uffff\2\44\4\uffff\12\44\25\uffff\10\44\1\uffff\3\44\1\uffff"+
            "\27\44\1\uffff\20\44\46\uffff\2\44\4\uffff\12\44\25\uffff\22"+
            "\44\3\uffff\30\44\1\uffff\11\44\1\uffff\1\44\2\uffff\7\44\72"+
            "\uffff\60\44\1\uffff\2\44\14\uffff\7\44\11\uffff\12\44\47\uffff"+
            "\2\44\1\uffff\1\44\2\uffff\2\44\1\uffff\1\44\2\uffff\1\44\6"+
            "\uffff\4\44\1\uffff\7\44\1\uffff\3\44\1\uffff\1\44\1\uffff\1"+
            "\44\2\uffff\2\44\1\uffff\4\44\1\uffff\2\44\11\uffff\1\44\2\uffff"+
            "\5\44\1\uffff\1\44\11\uffff\12\44\2\uffff\2\44\42\uffff\1\44"+
            "\37\uffff\12\44\26\uffff\10\44\1\uffff\42\44\35\uffff\4\44\164"+
            "\uffff\42\44\1\uffff\5\44\1\uffff\2\44\25\uffff\12\44\6\uffff"+
            "\6\44\112\uffff\46\44\12\uffff\51\44\7\uffff\132\44\5\uffff"+
            "\104\44\5\uffff\122\44\6\uffff\7\44\1\uffff\77\44\1\uffff\1"+
            "\44\1\uffff\4\44\2\uffff\7\44\1\uffff\1\44\1\uffff\4\44\2\uffff"+
            "\47\44\1\uffff\1\44\1\uffff\4\44\2\uffff\37\44\1\uffff\1\44"+
            "\1\uffff\4\44\2\uffff\7\44\1\uffff\1\44\1\uffff\4\44\2\uffff"+
            "\7\44\1\uffff\7\44\1\uffff\27\44\1\uffff\37\44\1\uffff\1\44"+
            "\1\uffff\4\44\2\uffff\7\44\1\uffff\47\44\1\uffff\23\44\16\uffff"+
            "\11\44\56\uffff\125\44\14\uffff\u026c\44\2\uffff\10\44\11\uffff"+
            "\1\45\32\44\5\uffff\113\44\25\uffff\15\44\1\uffff\4\44\16\uffff"+
            "\22\44\16\uffff\22\44\16\uffff\15\44\1\uffff\3\44\17\uffff\64"+
            "\44\43\uffff\1\44\4\uffff\1\44\3\uffff\12\44\44\uffff\1\45\1"+
            "\uffff\12\44\6\uffff\130\44\10\uffff\51\44\127\uffff\35\44\51"+
            "\uffff\50\44\2\uffff\5\44\u038b\uffff\154\44\u0094\uffff\u009c"+
            "\44\4\uffff\132\44\6\uffff\26\44\2\uffff\6\44\2\uffff\46\44"+
            "\2\uffff\6\44\2\uffff\10\44\1\uffff\1\44\1\uffff\1\44\1\uffff"+
            "\1\44\1\uffff\37\44\2\uffff\65\44\1\uffff\7\44\1\uffff\1\44"+
            "\3\uffff\3\44\1\uffff\7\44\3\uffff\4\44\2\uffff\6\44\4\uffff"+
            "\15\44\5\uffff\3\44\1\uffff\7\44\3\uffff\14\45\34\uffff\2\45"+
            "\5\uffff\1\45\57\uffff\1\45\21\uffff\1\44\15\uffff\1\44\u0082"+
            "\uffff\1\44\4\uffff\1\44\2\uffff\12\44\1\uffff\1\44\3\uffff"+
            "\5\44\6\uffff\1\44\1\uffff\1\44\1\uffff\1\44\1\uffff\4\44\1"+
            "\uffff\3\44\1\uffff\7\44\3\uffff\3\44\5\uffff\5\44\u0eb6\uffff"+
            "\1\45\4\uffff\2\44\52\uffff\5\44\5\uffff\2\44\4\uffff\126\44"+
            "\6\uffff\3\44\1\uffff\132\44\1\uffff\4\44\5\uffff\50\44\4\uffff"+
            "\136\44\21\uffff\30\44\70\uffff\20\44\u0200\uffff\u19b6\44\112"+
            "\uffff\u51a6\44\132\uffff\u048d\44\u0773\uffff\u2ba4\44\u215c"+
            "\uffff\u012e\44\2\uffff\73\44\u0095\uffff\7\44\14\uffff\5\44"+
            "\5\uffff\1\44\1\uffff\12\44\1\uffff\15\44\1\uffff\5\44\1\uffff"+
            "\1\44\1\uffff\2\44\1\uffff\2\44\1\uffff\154\44\41\uffff\u016b"+
            "\44\22\uffff\100\44\2\uffff\66\44\50\uffff\14\44\164\uffff\5"+
            "\44\1\uffff\u0087\44\23\uffff\12\44\7\uffff\32\44\6\uffff\32"+
            "\44\13\uffff\131\44\3\uffff\6\44\2\uffff\6\44\2\uffff\6\44\2"+
            "\uffff\3\44",
            "",
            "",
            "\1\46\1\uffff\1\52\1\46\1\uffff\11\46\1\47\12\46\1\50\1\46"+
            "\1\uffff\1\46\1\uffff\1\51\34\46\1\uffff\1\46\1\uffff\1\46\1"+
            "\uffff\32\46\2\uffff\1\53\1\46",
            "\1\64\1\63\5\uffff\1\67\5\uffff\12\61\5\uffff\1\70\1\uffff"+
            "\21\60\1\56\10\60\1\uffff\1\65\2\uffff\1\62\1\uffff\21\57\1"+
            "\55\10\57\57\uffff\1\44\12\uffff\1\44\4\uffff\1\44\5\uffff\27"+
            "\44\1\uffff\37\44\1\uffff\u013f\44\31\uffff\162\44\4\uffff\14"+
            "\44\16\uffff\5\44\11\uffff\1\44\u008b\uffff\1\44\13\uffff\1"+
            "\44\1\uffff\3\44\1\uffff\1\44\1\uffff\24\44\1\uffff\54\44\1"+
            "\uffff\46\44\1\uffff\5\44\4\uffff\u0082\44\10\uffff\105\44\1"+
            "\uffff\46\44\2\uffff\2\44\6\uffff\20\44\41\uffff\46\44\2\uffff"+
            "\1\44\7\uffff\47\44\110\uffff\33\44\5\uffff\3\44\56\uffff\32"+
            "\44\5\uffff\13\44\25\uffff\12\44\4\uffff\2\44\1\uffff\143\44"+
            "\1\uffff\1\44\17\uffff\2\44\7\uffff\17\44\2\uffff\1\44\20\uffff"+
            "\1\44\1\uffff\36\44\35\uffff\3\44\60\uffff\46\44\13\uffff\1"+
            "\44\u0152\uffff\66\44\3\uffff\1\44\22\uffff\1\44\7\uffff\12"+
            "\44\4\uffff\12\44\25\uffff\10\44\2\uffff\2\44\2\uffff\26\44"+
            "\1\uffff\7\44\1\uffff\1\44\3\uffff\4\44\3\uffff\1\44\36\uffff"+
            "\2\44\1\uffff\3\44\4\uffff\14\44\23\uffff\6\44\4\uffff\2\44"+
            "\2\uffff\26\44\1\uffff\7\44\1\uffff\2\44\1\uffff\2\44\1\uffff"+
            "\2\44\37\uffff\4\44\1\uffff\1\44\7\uffff\12\44\2\uffff\3\44"+
            "\20\uffff\11\44\1\uffff\3\44\1\uffff\26\44\1\uffff\7\44\1\uffff"+
            "\2\44\1\uffff\5\44\3\uffff\1\44\22\uffff\1\44\17\uffff\2\44"+
            "\4\uffff\12\44\25\uffff\10\44\2\uffff\2\44\2\uffff\26\44\1\uffff"+
            "\7\44\1\uffff\2\44\1\uffff\5\44\3\uffff\1\44\36\uffff\2\44\1"+
            "\uffff\3\44\4\uffff\12\44\1\uffff\1\44\21\uffff\1\44\1\uffff"+
            "\6\44\3\uffff\3\44\1\uffff\4\44\3\uffff\2\44\1\uffff\1\44\1"+
            "\uffff\2\44\3\uffff\2\44\3\uffff\3\44\3\uffff\10\44\1\uffff"+
            "\3\44\55\uffff\11\44\25\uffff\10\44\1\uffff\3\44\1\uffff\27"+
            "\44\1\uffff\12\44\1\uffff\5\44\46\uffff\2\44\4\uffff\12\44\25"+
            "\uffff\10\44\1\uffff\3\44\1\uffff\27\44\1\uffff\12\44\1\uffff"+
            "\5\44\3\uffff\1\44\40\uffff\1\44\1\uffff\2\44\4\uffff\12\44"+
            "\25\uffff\10\44\1\uffff\3\44\1\uffff\27\44\1\uffff\20\44\46"+
            "\uffff\2\44\4\uffff\12\44\25\uffff\22\44\3\uffff\30\44\1\uffff"+
            "\11\44\1\uffff\1\44\2\uffff\7\44\72\uffff\60\44\1\uffff\2\44"+
            "\14\uffff\7\44\11\uffff\12\44\47\uffff\2\44\1\uffff\1\44\2\uffff"+
            "\2\44\1\uffff\1\44\2\uffff\1\44\6\uffff\4\44\1\uffff\7\44\1"+
            "\uffff\3\44\1\uffff\1\44\1\uffff\1\44\2\uffff\2\44\1\uffff\4"+
            "\44\1\uffff\2\44\11\uffff\1\44\2\uffff\5\44\1\uffff\1\44\11"+
            "\uffff\12\44\2\uffff\2\44\42\uffff\1\44\37\uffff\12\44\26\uffff"+
            "\10\44\1\uffff\42\44\35\uffff\4\44\164\uffff\42\44\1\uffff\5"+
            "\44\1\uffff\2\44\25\uffff\12\44\6\uffff\6\44\112\uffff\46\44"+
            "\12\uffff\51\44\7\uffff\132\44\5\uffff\104\44\5\uffff\122\44"+
            "\6\uffff\7\44\1\uffff\77\44\1\uffff\1\44\1\uffff\4\44\2\uffff"+
            "\7\44\1\uffff\1\44\1\uffff\4\44\2\uffff\47\44\1\uffff\1\44\1"+
            "\uffff\4\44\2\uffff\37\44\1\uffff\1\44\1\uffff\4\44\2\uffff"+
            "\7\44\1\uffff\1\44\1\uffff\4\44\2\uffff\7\44\1\uffff\7\44\1"+
            "\uffff\27\44\1\uffff\37\44\1\uffff\1\44\1\uffff\4\44\2\uffff"+
            "\7\44\1\uffff\47\44\1\uffff\23\44\16\uffff\11\44\56\uffff\125"+
            "\44\14\uffff\u026c\44\2\uffff\10\44\12\uffff\32\44\5\uffff\113"+
            "\44\25\uffff\15\44\1\uffff\4\44\16\uffff\22\44\16\uffff\22\44"+
            "\16\uffff\15\44\1\uffff\3\44\17\uffff\64\44\43\uffff\1\44\4"+
            "\uffff\1\44\3\uffff\12\44\46\uffff\12\44\6\uffff\130\44\10\uffff"+
            "\51\44\127\uffff\35\44\51\uffff\50\44\2\uffff\5\44\u038b\uffff"+
            "\154\44\u0094\uffff\u009c\44\4\uffff\132\44\6\uffff\26\44\2"+
            "\uffff\6\44\2\uffff\46\44\2\uffff\6\44\2\uffff\10\44\1\uffff"+
            "\1\44\1\uffff\1\44\1\uffff\1\44\1\uffff\37\44\2\uffff\65\44"+
            "\1\uffff\7\44\1\uffff\1\44\3\uffff\3\44\1\uffff\7\44\3\uffff"+
            "\4\44\2\uffff\6\44\4\uffff\15\44\5\uffff\3\44\1\uffff\7\44\164"+
            "\uffff\1\44\15\uffff\1\44\u0082\uffff\1\44\4\uffff\1\44\2\uffff"+
            "\12\44\1\uffff\1\44\3\uffff\5\44\6\uffff\1\44\1\uffff\1\44\1"+
            "\uffff\1\44\1\uffff\4\44\1\uffff\3\44\1\uffff\7\44\3\uffff\3"+
            "\44\5\uffff\5\44\u0ebb\uffff\2\44\52\uffff\5\44\5\uffff\2\44"+
            "\4\uffff\126\44\6\uffff\3\44\1\uffff\132\44\1\uffff\4\44\5\uffff"+
            "\50\44\4\uffff\136\44\21\uffff\30\44\70\uffff\20\44\u0200\uffff"+
            "\u19b6\44\112\uffff\u51a6\44\132\uffff\u048d\44\u0773\uffff"+
            "\u2ba4\44\u215c\uffff\u012e\44\2\uffff\73\44\u0095\uffff\7\44"+
            "\14\uffff\5\44\5\uffff\1\44\1\uffff\12\44\1\uffff\15\44\1\uffff"+
            "\5\44\1\uffff\1\44\1\uffff\2\44\1\uffff\2\44\1\uffff\154\44"+
            "\41\uffff\u016b\44\22\uffff\100\44\2\uffff\66\44\50\uffff\14"+
            "\44\164\uffff\5\44\1\uffff\u0087\44\23\uffff\12\44\7\uffff\32"+
            "\44\6\uffff\32\44\13\uffff\131\44\3\uffff\6\44\2\uffff\6\44"+
            "\2\uffff\6\44\2\uffff\3\44",
            "\1\64\1\63\5\uffff\1\67\5\uffff\12\61\5\uffff\1\70\1\uffff"+
            "\15\60\1\72\14\60\1\uffff\1\65\2\uffff\1\62\1\uffff\15\57\1"+
            "\71\14\57\57\uffff\1\44\12\uffff\1\44\4\uffff\1\44\5\uffff\27"+
            "\44\1\uffff\37\44\1\uffff\u013f\44\31\uffff\162\44\4\uffff\14"+
            "\44\16\uffff\5\44\11\uffff\1\44\u008b\uffff\1\44\13\uffff\1"+
            "\44\1\uffff\3\44\1\uffff\1\44\1\uffff\24\44\1\uffff\54\44\1"+
            "\uffff\46\44\1\uffff\5\44\4\uffff\u0082\44\10\uffff\105\44\1"+
            "\uffff\46\44\2\uffff\2\44\6\uffff\20\44\41\uffff\46\44\2\uffff"+
            "\1\44\7\uffff\47\44\110\uffff\33\44\5\uffff\3\44\56\uffff\32"+
            "\44\5\uffff\13\44\25\uffff\12\44\4\uffff\2\44\1\uffff\143\44"+
            "\1\uffff\1\44\17\uffff\2\44\7\uffff\17\44\2\uffff\1\44\20\uffff"+
            "\1\44\1\uffff\36\44\35\uffff\3\44\60\uffff\46\44\13\uffff\1"+
            "\44\u0152\uffff\66\44\3\uffff\1\44\22\uffff\1\44\7\uffff\12"+
            "\44\4\uffff\12\44\25\uffff\10\44\2\uffff\2\44\2\uffff\26\44"+
            "\1\uffff\7\44\1\uffff\1\44\3\uffff\4\44\3\uffff\1\44\36\uffff"+
            "\2\44\1\uffff\3\44\4\uffff\14\44\23\uffff\6\44\4\uffff\2\44"+
            "\2\uffff\26\44\1\uffff\7\44\1\uffff\2\44\1\uffff\2\44\1\uffff"+
            "\2\44\37\uffff\4\44\1\uffff\1\44\7\uffff\12\44\2\uffff\3\44"+
            "\20\uffff\11\44\1\uffff\3\44\1\uffff\26\44\1\uffff\7\44\1\uffff"+
            "\2\44\1\uffff\5\44\3\uffff\1\44\22\uffff\1\44\17\uffff\2\44"+
            "\4\uffff\12\44\25\uffff\10\44\2\uffff\2\44\2\uffff\26\44\1\uffff"+
            "\7\44\1\uffff\2\44\1\uffff\5\44\3\uffff\1\44\36\uffff\2\44\1"+
            "\uffff\3\44\4\uffff\12\44\1\uffff\1\44\21\uffff\1\44\1\uffff"+
            "\6\44\3\uffff\3\44\1\uffff\4\44\3\uffff\2\44\1\uffff\1\44\1"+
            "\uffff\2\44\3\uffff\2\44\3\uffff\3\44\3\uffff\10\44\1\uffff"+
            "\3\44\55\uffff\11\44\25\uffff\10\44\1\uffff\3\44\1\uffff\27"+
            "\44\1\uffff\12\44\1\uffff\5\44\46\uffff\2\44\4\uffff\12\44\25"+
            "\uffff\10\44\1\uffff\3\44\1\uffff\27\44\1\uffff\12\44\1\uffff"+
            "\5\44\3\uffff\1\44\40\uffff\1\44\1\uffff\2\44\4\uffff\12\44"+
            "\25\uffff\10\44\1\uffff\3\44\1\uffff\27\44\1\uffff\20\44\46"+
            "\uffff\2\44\4\uffff\12\44\25\uffff\22\44\3\uffff\30\44\1\uffff"+
            "\11\44\1\uffff\1\44\2\uffff\7\44\72\uffff\60\44\1\uffff\2\44"+
            "\14\uffff\7\44\11\uffff\12\44\47\uffff\2\44\1\uffff\1\44\2\uffff"+
            "\2\44\1\uffff\1\44\2\uffff\1\44\6\uffff\4\44\1\uffff\7\44\1"+
            "\uffff\3\44\1\uffff\1\44\1\uffff\1\44\2\uffff\2\44\1\uffff\4"+
            "\44\1\uffff\2\44\11\uffff\1\44\2\uffff\5\44\1\uffff\1\44\11"+
            "\uffff\12\44\2\uffff\2\44\42\uffff\1\44\37\uffff\12\44\26\uffff"+
            "\10\44\1\uffff\42\44\35\uffff\4\44\164\uffff\42\44\1\uffff\5"+
            "\44\1\uffff\2\44\25\uffff\12\44\6\uffff\6\44\112\uffff\46\44"+
            "\12\uffff\51\44\7\uffff\132\44\5\uffff\104\44\5\uffff\122\44"+
            "\6\uffff\7\44\1\uffff\77\44\1\uffff\1\44\1\uffff\4\44\2\uffff"+
            "\7\44\1\uffff\1\44\1\uffff\4\44\2\uffff\47\44\1\uffff\1\44\1"+
            "\uffff\4\44\2\uffff\37\44\1\uffff\1\44\1\uffff\4\44\2\uffff"+
            "\7\44\1\uffff\1\44\1\uffff\4\44\2\uffff\7\44\1\uffff\7\44\1"+
            "\uffff\27\44\1\uffff\37\44\1\uffff\1\44\1\uffff\4\44\2\uffff"+
            "\7\44\1\uffff\47\44\1\uffff\23\44\16\uffff\11\44\56\uffff\125"+
            "\44\14\uffff\u026c\44\2\uffff\10\44\12\uffff\32\44\5\uffff\113"+
            "\44\25\uffff\15\44\1\uffff\4\44\16\uffff\22\44\16\uffff\22\44"+
            "\16\uffff\15\44\1\uffff\3\44\17\uffff\64\44\43\uffff\1\44\4"+
            "\uffff\1\44\3\uffff\12\44\46\uffff\12\44\6\uffff\130\44\10\uffff"+
            "\51\44\127\uffff\35\44\51\uffff\50\44\2\uffff\5\44\u038b\uffff"+
            "\154\44\u0094\uffff\u009c\44\4\uffff\132\44\6\uffff\26\44\2"+
            "\uffff\6\44\2\uffff\46\44\2\uffff\6\44\2\uffff\10\44\1\uffff"+
            "\1\44\1\uffff\1\44\1\uffff\1\44\1\uffff\37\44\2\uffff\65\44"+
            "\1\uffff\7\44\1\uffff\1\44\3\uffff\3\44\1\uffff\7\44\3\uffff"+
            "\4\44\2\uffff\6\44\4\uffff\15\44\5\uffff\3\44\1\uffff\7\44\164"+
            "\uffff\1\44\15\uffff\1\44\u0082\uffff\1\44\4\uffff\1\44\2\uffff"+
            "\12\44\1\uffff\1\44\3\uffff\5\44\6\uffff\1\44\1\uffff\1\44\1"+
            "\uffff\1\44\1\uffff\4\44\1\uffff\3\44\1\uffff\7\44\3\uffff\3"+
            "\44\5\uffff\5\44\u0ebb\uffff\2\44\52\uffff\5\44\5\uffff\2\44"+
            "\4\uffff\126\44\6\uffff\3\44\1\uffff\132\44\1\uffff\4\44\5\uffff"+
            "\50\44\4\uffff\136\44\21\uffff\30\44\70\uffff\20\44\u0200\uffff"+
            "\u19b6\44\112\uffff\u51a6\44\132\uffff\u048d\44\u0773\uffff"+
            "\u2ba4\44\u215c\uffff\u012e\44\2\uffff\73\44\u0095\uffff\7\44"+
            "\14\uffff\5\44\5\uffff\1\44\1\uffff\12\44\1\uffff\15\44\1\uffff"+
            "\5\44\1\uffff\1\44\1\uffff\2\44\1\uffff\2\44\1\uffff\154\44"+
            "\41\uffff\u016b\44\22\uffff\100\44\2\uffff\66\44\50\uffff\14"+
            "\44\164\uffff\5\44\1\uffff\u0087\44\23\uffff\12\44\7\uffff\32"+
            "\44\6\uffff\32\44\13\uffff\131\44\3\uffff\6\44\2\uffff\6\44"+
            "\2\uffff\6\44\2\uffff\3\44",
            "\1\64\1\63\5\uffff\1\67\5\uffff\12\61\5\uffff\1\70\1\uffff"+
            "\16\60\1\74\13\60\1\uffff\1\65\2\uffff\1\62\1\uffff\16\57\1"+
            "\73\13\57\57\uffff\1\44\12\uffff\1\44\4\uffff\1\44\5\uffff\27"+
            "\44\1\uffff\37\44\1\uffff\u013f\44\31\uffff\162\44\4\uffff\14"+
            "\44\16\uffff\5\44\11\uffff\1\44\u008b\uffff\1\44\13\uffff\1"+
            "\44\1\uffff\3\44\1\uffff\1\44\1\uffff\24\44\1\uffff\54\44\1"+
            "\uffff\46\44\1\uffff\5\44\4\uffff\u0082\44\10\uffff\105\44\1"+
            "\uffff\46\44\2\uffff\2\44\6\uffff\20\44\41\uffff\46\44\2\uffff"+
            "\1\44\7\uffff\47\44\110\uffff\33\44\5\uffff\3\44\56\uffff\32"+
            "\44\5\uffff\13\44\25\uffff\12\44\4\uffff\2\44\1\uffff\143\44"+
            "\1\uffff\1\44\17\uffff\2\44\7\uffff\17\44\2\uffff\1\44\20\uffff"+
            "\1\44\1\uffff\36\44\35\uffff\3\44\60\uffff\46\44\13\uffff\1"+
            "\44\u0152\uffff\66\44\3\uffff\1\44\22\uffff\1\44\7\uffff\12"+
            "\44\4\uffff\12\44\25\uffff\10\44\2\uffff\2\44\2\uffff\26\44"+
            "\1\uffff\7\44\1\uffff\1\44\3\uffff\4\44\3\uffff\1\44\36\uffff"+
            "\2\44\1\uffff\3\44\4\uffff\14\44\23\uffff\6\44\4\uffff\2\44"+
            "\2\uffff\26\44\1\uffff\7\44\1\uffff\2\44\1\uffff\2\44\1\uffff"+
            "\2\44\37\uffff\4\44\1\uffff\1\44\7\uffff\12\44\2\uffff\3\44"+
            "\20\uffff\11\44\1\uffff\3\44\1\uffff\26\44\1\uffff\7\44\1\uffff"+
            "\2\44\1\uffff\5\44\3\uffff\1\44\22\uffff\1\44\17\uffff\2\44"+
            "\4\uffff\12\44\25\uffff\10\44\2\uffff\2\44\2\uffff\26\44\1\uffff"+
            "\7\44\1\uffff\2\44\1\uffff\5\44\3\uffff\1\44\36\uffff\2\44\1"+
            "\uffff\3\44\4\uffff\12\44\1\uffff\1\44\21\uffff\1\44\1\uffff"+
            "\6\44\3\uffff\3\44\1\uffff\4\44\3\uffff\2\44\1\uffff\1\44\1"+
            "\uffff\2\44\3\uffff\2\44\3\uffff\3\44\3\uffff\10\44\1\uffff"+
            "\3\44\55\uffff\11\44\25\uffff\10\44\1\uffff\3\44\1\uffff\27"+
            "\44\1\uffff\12\44\1\uffff\5\44\46\uffff\2\44\4\uffff\12\44\25"+
            "\uffff\10\44\1\uffff\3\44\1\uffff\27\44\1\uffff\12\44\1\uffff"+
            "\5\44\3\uffff\1\44\40\uffff\1\44\1\uffff\2\44\4\uffff\12\44"+
            "\25\uffff\10\44\1\uffff\3\44\1\uffff\27\44\1\uffff\20\44\46"+
            "\uffff\2\44\4\uffff\12\44\25\uffff\22\44\3\uffff\30\44\1\uffff"+
            "\11\44\1\uffff\1\44\2\uffff\7\44\72\uffff\60\44\1\uffff\2\44"+
            "\14\uffff\7\44\11\uffff\12\44\47\uffff\2\44\1\uffff\1\44\2\uffff"+
            "\2\44\1\uffff\1\44\2\uffff\1\44\6\uffff\4\44\1\uffff\7\44\1"+
            "\uffff\3\44\1\uffff\1\44\1\uffff\1\44\2\uffff\2\44\1\uffff\4"+
            "\44\1\uffff\2\44\11\uffff\1\44\2\uffff\5\44\1\uffff\1\44\11"+
            "\uffff\12\44\2\uffff\2\44\42\uffff\1\44\37\uffff\12\44\26\uffff"+
            "\10\44\1\uffff\42\44\35\uffff\4\44\164\uffff\42\44\1\uffff\5"+
            "\44\1\uffff\2\44\25\uffff\12\44\6\uffff\6\44\112\uffff\46\44"+
            "\12\uffff\51\44\7\uffff\132\44\5\uffff\104\44\5\uffff\122\44"+
            "\6\uffff\7\44\1\uffff\77\44\1\uffff\1\44\1\uffff\4\44\2\uffff"+
            "\7\44\1\uffff\1\44\1\uffff\4\44\2\uffff\47\44\1\uffff\1\44\1"+
            "\uffff\4\44\2\uffff\37\44\1\uffff\1\44\1\uffff\4\44\2\uffff"+
            "\7\44\1\uffff\1\44\1\uffff\4\44\2\uffff\7\44\1\uffff\7\44\1"+
            "\uffff\27\44\1\uffff\37\44\1\uffff\1\44\1\uffff\4\44\2\uffff"+
            "\7\44\1\uffff\47\44\1\uffff\23\44\16\uffff\11\44\56\uffff\125"+
            "\44\14\uffff\u026c\44\2\uffff\10\44\12\uffff\32\44\5\uffff\113"+
            "\44\25\uffff\15\44\1\uffff\4\44\16\uffff\22\44\16\uffff\22\44"+
            "\16\uffff\15\44\1\uffff\3\44\17\uffff\64\44\43\uffff\1\44\4"+
            "\uffff\1\44\3\uffff\12\44\46\uffff\12\44\6\uffff\130\44\10\uffff"+
            "\51\44\127\uffff\35\44\51\uffff\50\44\2\uffff\5\44\u038b\uffff"+
            "\154\44\u0094\uffff\u009c\44\4\uffff\132\44\6\uffff\26\44\2"+
            "\uffff\6\44\2\uffff\46\44\2\uffff\6\44\2\uffff\10\44\1\uffff"+
            "\1\44\1\uffff\1\44\1\uffff\1\44\1\uffff\37\44\2\uffff\65\44"+
            "\1\uffff\7\44\1\uffff\1\44\3\uffff\3\44\1\uffff\7\44\3\uffff"+
            "\4\44\2\uffff\6\44\4\uffff\15\44\5\uffff\3\44\1\uffff\7\44\164"+
            "\uffff\1\44\15\uffff\1\44\u0082\uffff\1\44\4\uffff\1\44\2\uffff"+
            "\12\44\1\uffff\1\44\3\uffff\5\44\6\uffff\1\44\1\uffff\1\44\1"+
            "\uffff\1\44\1\uffff\4\44\1\uffff\3\44\1\uffff\7\44\3\uffff\3"+
            "\44\5\uffff\5\44\u0ebb\uffff\2\44\52\uffff\5\44\5\uffff\2\44"+
            "\4\uffff\126\44\6\uffff\3\44\1\uffff\132\44\1\uffff\4\44\5\uffff"+
            "\50\44\4\uffff\136\44\21\uffff\30\44\70\uffff\20\44\u0200\uffff"+
            "\u19b6\44\112\uffff\u51a6\44\132\uffff\u048d\44\u0773\uffff"+
            "\u2ba4\44\u215c\uffff\u012e\44\2\uffff\73\44\u0095\uffff\7\44"+
            "\14\uffff\5\44\5\uffff\1\44\1\uffff\12\44\1\uffff\15\44\1\uffff"+
            "\5\44\1\uffff\1\44\1\uffff\2\44\1\uffff\2\44\1\uffff\154\44"+
            "\41\uffff\u016b\44\22\uffff\100\44\2\uffff\66\44\50\uffff\14"+
            "\44\164\uffff\5\44\1\uffff\u0087\44\23\uffff\12\44\7\uffff\32"+
            "\44\6\uffff\32\44\13\uffff\131\44\3\uffff\6\44\2\uffff\6\44"+
            "\2\uffff\6\44\2\uffff\3\44",
            "",
            "",
            "",
            "\1\100\1\uffff\1\76\11\77",
            "\1\100\1\uffff\1\76\11\77",
            "",
            "\1\105\5\uffff\12\104\5\uffff\1\70\1\uffff\32\104\1\uffff"+
            "\1\103\4\uffff\32\104\57\uffff\1\104\12\uffff\1\104\4\uffff"+
            "\1\104\5\uffff\27\104\1\uffff\37\104\1\uffff\u013f\104\31\uffff"+
            "\162\104\4\uffff\14\104\16\uffff\5\104\11\uffff\1\104\u008b"+
            "\uffff\1\104\13\uffff\1\104\1\uffff\3\104\1\uffff\1\104\1\uffff"+
            "\24\104\1\uffff\54\104\1\uffff\46\104\1\uffff\5\104\4\uffff"+
            "\u0082\104\10\uffff\105\104\1\uffff\46\104\2\uffff\2\104\6\uffff"+
            "\20\104\41\uffff\46\104\2\uffff\1\104\7\uffff\47\104\110\uffff"+
            "\33\104\5\uffff\3\104\56\uffff\32\104\5\uffff\13\104\25\uffff"+
            "\12\104\4\uffff\2\104\1\uffff\143\104\1\uffff\1\104\17\uffff"+
            "\2\104\7\uffff\17\104\2\uffff\1\104\20\uffff\1\104\1\uffff\36"+
            "\104\35\uffff\3\104\60\uffff\46\104\13\uffff\1\104\u0152\uffff"+
            "\66\104\3\uffff\1\104\22\uffff\1\104\7\uffff\12\104\4\uffff"+
            "\12\104\25\uffff\10\104\2\uffff\2\104\2\uffff\26\104\1\uffff"+
            "\7\104\1\uffff\1\104\3\uffff\4\104\3\uffff\1\104\36\uffff\2"+
            "\104\1\uffff\3\104\4\uffff\14\104\23\uffff\6\104\4\uffff\2\104"+
            "\2\uffff\26\104\1\uffff\7\104\1\uffff\2\104\1\uffff\2\104\1"+
            "\uffff\2\104\37\uffff\4\104\1\uffff\1\104\7\uffff\12\104\2\uffff"+
            "\3\104\20\uffff\11\104\1\uffff\3\104\1\uffff\26\104\1\uffff"+
            "\7\104\1\uffff\2\104\1\uffff\5\104\3\uffff\1\104\22\uffff\1"+
            "\104\17\uffff\2\104\4\uffff\12\104\25\uffff\10\104\2\uffff\2"+
            "\104\2\uffff\26\104\1\uffff\7\104\1\uffff\2\104\1\uffff\5\104"+
            "\3\uffff\1\104\36\uffff\2\104\1\uffff\3\104\4\uffff\12\104\1"+
            "\uffff\1\104\21\uffff\1\104\1\uffff\6\104\3\uffff\3\104\1\uffff"+
            "\4\104\3\uffff\2\104\1\uffff\1\104\1\uffff\2\104\3\uffff\2\104"+
            "\3\uffff\3\104\3\uffff\10\104\1\uffff\3\104\55\uffff\11\104"+
            "\25\uffff\10\104\1\uffff\3\104\1\uffff\27\104\1\uffff\12\104"+
            "\1\uffff\5\104\46\uffff\2\104\4\uffff\12\104\25\uffff\10\104"+
            "\1\uffff\3\104\1\uffff\27\104\1\uffff\12\104\1\uffff\5\104\3"+
            "\uffff\1\104\40\uffff\1\104\1\uffff\2\104\4\uffff\12\104\25"+
            "\uffff\10\104\1\uffff\3\104\1\uffff\27\104\1\uffff\20\104\46"+
            "\uffff\2\104\4\uffff\12\104\25\uffff\22\104\3\uffff\30\104\1"+
            "\uffff\11\104\1\uffff\1\104\2\uffff\7\104\72\uffff\60\104\1"+
            "\uffff\2\104\14\uffff\7\104\11\uffff\12\104\47\uffff\2\104\1"+
            "\uffff\1\104\2\uffff\2\104\1\uffff\1\104\2\uffff\1\104\6\uffff"+
            "\4\104\1\uffff\7\104\1\uffff\3\104\1\uffff\1\104\1\uffff\1\104"+
            "\2\uffff\2\104\1\uffff\4\104\1\uffff\2\104\11\uffff\1\104\2"+
            "\uffff\5\104\1\uffff\1\104\11\uffff\12\104\2\uffff\2\104\42"+
            "\uffff\1\104\37\uffff\12\104\26\uffff\10\104\1\uffff\42\104"+
            "\35\uffff\4\104\164\uffff\42\104\1\uffff\5\104\1\uffff\2\104"+
            "\25\uffff\12\104\6\uffff\6\104\112\uffff\46\104\12\uffff\51"+
            "\104\7\uffff\132\104\5\uffff\104\104\5\uffff\122\104\6\uffff"+
            "\7\104\1\uffff\77\104\1\uffff\1\104\1\uffff\4\104\2\uffff\7"+
            "\104\1\uffff\1\104\1\uffff\4\104\2\uffff\47\104\1\uffff\1\104"+
            "\1\uffff\4\104\2\uffff\37\104\1\uffff\1\104\1\uffff\4\104\2"+
            "\uffff\7\104\1\uffff\1\104\1\uffff\4\104\2\uffff\7\104\1\uffff"+
            "\7\104\1\uffff\27\104\1\uffff\37\104\1\uffff\1\104\1\uffff\4"+
            "\104\2\uffff\7\104\1\uffff\47\104\1\uffff\23\104\16\uffff\11"+
            "\104\56\uffff\125\104\14\uffff\u026c\104\2\uffff\10\104\12\uffff"+
            "\32\104\5\uffff\113\104\25\uffff\15\104\1\uffff\4\104\16\uffff"+
            "\22\104\16\uffff\22\104\16\uffff\15\104\1\uffff\3\104\17\uffff"+
            "\64\104\43\uffff\1\104\4\uffff\1\104\3\uffff\12\104\46\uffff"+
            "\12\104\6\uffff\130\104\10\uffff\51\104\127\uffff\35\104\51"+
            "\uffff\50\104\2\uffff\5\104\u038b\uffff\154\104\u0094\uffff"+
            "\u009c\104\4\uffff\132\104\6\uffff\26\104\2\uffff\6\104\2\uffff"+
            "\46\104\2\uffff\6\104\2\uffff\10\104\1\uffff\1\104\1\uffff\1"+
            "\104\1\uffff\1\104\1\uffff\37\104\2\uffff\65\104\1\uffff\7\104"+
            "\1\uffff\1\104\3\uffff\3\104\1\uffff\7\104\3\uffff\4\104\2\uffff"+
            "\6\104\4\uffff\15\104\5\uffff\3\104\1\uffff\7\104\164\uffff"+
            "\1\104\15\uffff\1\104\u0082\uffff\1\104\4\uffff\1\104\2\uffff"+
            "\12\104\1\uffff\1\104\3\uffff\5\104\6\uffff\1\104\1\uffff\1"+
            "\104\1\uffff\1\104\1\uffff\4\104\1\uffff\3\104\1\uffff\7\104"+
            "\3\uffff\3\104\5\uffff\5\104\u0ebb\uffff\2\104\52\uffff\5\104"+
            "\5\uffff\2\104\4\uffff\126\104\6\uffff\3\104\1\uffff\132\104"+
            "\1\uffff\4\104\5\uffff\50\104\4\uffff\136\104\21\uffff\30\104"+
            "\70\uffff\20\104\u0200\uffff\u19b6\104\112\uffff\u51a6\104\132"+
            "\uffff\u048d\104\u0773\uffff\u2ba4\104\u215c\uffff\u012e\104"+
            "\2\uffff\73\104\u0095\uffff\7\104\14\uffff\5\104\5\uffff\1\104"+
            "\1\uffff\12\104\1\uffff\15\104\1\uffff\5\104\1\uffff\1\104\1"+
            "\uffff\2\104\1\uffff\2\104\1\uffff\154\104\41\uffff\u016b\104"+
            "\22\uffff\100\104\2\uffff\66\104\50\uffff\14\104\164\uffff\5"+
            "\104\1\uffff\u0087\104\23\uffff\12\104\7\uffff\32\104\6\uffff"+
            "\32\104\13\uffff\131\104\3\uffff\6\104\2\uffff\6\104\2\uffff"+
            "\6\104\2\uffff\3\104",
            "\1\106\1\uffff\12\110",
            "",
            "",
            "",
            "",
            "\1\105\5\uffff\12\104\5\uffff\1\70\1\uffff\32\104\1\uffff"+
            "\1\103\4\uffff\32\104\57\uffff\1\104\12\uffff\1\104\4\uffff"+
            "\1\104\5\uffff\27\104\1\uffff\37\104\1\uffff\u013f\104\31\uffff"+
            "\162\104\4\uffff\14\104\16\uffff\5\104\11\uffff\1\104\u008b"+
            "\uffff\1\104\13\uffff\1\104\1\uffff\3\104\1\uffff\1\104\1\uffff"+
            "\24\104\1\uffff\54\104\1\uffff\46\104\1\uffff\5\104\4\uffff"+
            "\u0082\104\10\uffff\105\104\1\uffff\46\104\2\uffff\2\104\6\uffff"+
            "\20\104\41\uffff\46\104\2\uffff\1\104\7\uffff\47\104\110\uffff"+
            "\33\104\5\uffff\3\104\56\uffff\32\104\5\uffff\13\104\25\uffff"+
            "\12\104\4\uffff\2\104\1\uffff\143\104\1\uffff\1\104\17\uffff"+
            "\2\104\7\uffff\17\104\2\uffff\1\104\20\uffff\1\104\1\uffff\36"+
            "\104\35\uffff\3\104\60\uffff\46\104\13\uffff\1\104\u0152\uffff"+
            "\66\104\3\uffff\1\104\22\uffff\1\104\7\uffff\12\104\4\uffff"+
            "\12\104\25\uffff\10\104\2\uffff\2\104\2\uffff\26\104\1\uffff"+
            "\7\104\1\uffff\1\104\3\uffff\4\104\3\uffff\1\104\36\uffff\2"+
            "\104\1\uffff\3\104\4\uffff\14\104\23\uffff\6\104\4\uffff\2\104"+
            "\2\uffff\26\104\1\uffff\7\104\1\uffff\2\104\1\uffff\2\104\1"+
            "\uffff\2\104\37\uffff\4\104\1\uffff\1\104\7\uffff\12\104\2\uffff"+
            "\3\104\20\uffff\11\104\1\uffff\3\104\1\uffff\26\104\1\uffff"+
            "\7\104\1\uffff\2\104\1\uffff\5\104\3\uffff\1\104\22\uffff\1"+
            "\104\17\uffff\2\104\4\uffff\12\104\25\uffff\10\104\2\uffff\2"+
            "\104\2\uffff\26\104\1\uffff\7\104\1\uffff\2\104\1\uffff\5\104"+
            "\3\uffff\1\104\36\uffff\2\104\1\uffff\3\104\4\uffff\12\104\1"+
            "\uffff\1\104\21\uffff\1\104\1\uffff\6\104\3\uffff\3\104\1\uffff"+
            "\4\104\3\uffff\2\104\1\uffff\1\104\1\uffff\2\104\3\uffff\2\104"+
            "\3\uffff\3\104\3\uffff\10\104\1\uffff\3\104\55\uffff\11\104"+
            "\25\uffff\10\104\1\uffff\3\104\1\uffff\27\104\1\uffff\12\104"+
            "\1\uffff\5\104\46\uffff\2\104\4\uffff\12\104\25\uffff\10\104"+
            "\1\uffff\3\104\1\uffff\27\104\1\uffff\12\104\1\uffff\5\104\3"+
            "\uffff\1\104\40\uffff\1\104\1\uffff\2\104\4\uffff\12\104\25"+
            "\uffff\10\104\1\uffff\3\104\1\uffff\27\104\1\uffff\20\104\46"+
            "\uffff\2\104\4\uffff\12\104\25\uffff\22\104\3\uffff\30\104\1"+
            "\uffff\11\104\1\uffff\1\104\2\uffff\7\104\72\uffff\60\104\1"+
            "\uffff\2\104\14\uffff\7\104\11\uffff\12\104\47\uffff\2\104\1"+
            "\uffff\1\104\2\uffff\2\104\1\uffff\1\104\2\uffff\1\104\6\uffff"+
            "\4\104\1\uffff\7\104\1\uffff\3\104\1\uffff\1\104\1\uffff\1\104"+
            "\2\uffff\2\104\1\uffff\4\104\1\uffff\2\104\11\uffff\1\104\2"+
            "\uffff\5\104\1\uffff\1\104\11\uffff\12\104\2\uffff\2\104\42"+
            "\uffff\1\104\37\uffff\12\104\26\uffff\10\104\1\uffff\42\104"+
            "\35\uffff\4\104\164\uffff\42\104\1\uffff\5\104\1\uffff\2\104"+
            "\25\uffff\12\104\6\uffff\6\104\112\uffff\46\104\12\uffff\51"+
            "\104\7\uffff\132\104\5\uffff\104\104\5\uffff\122\104\6\uffff"+
            "\7\104\1\uffff\77\104\1\uffff\1\104\1\uffff\4\104\2\uffff\7"+
            "\104\1\uffff\1\104\1\uffff\4\104\2\uffff\47\104\1\uffff\1\104"+
            "\1\uffff\4\104\2\uffff\37\104\1\uffff\1\104\1\uffff\4\104\2"+
            "\uffff\7\104\1\uffff\1\104\1\uffff\4\104\2\uffff\7\104\1\uffff"+
            "\7\104\1\uffff\27\104\1\uffff\37\104\1\uffff\1\104\1\uffff\4"+
            "\104\2\uffff\7\104\1\uffff\47\104\1\uffff\23\104\16\uffff\11"+
            "\104\56\uffff\125\104\14\uffff\u026c\104\2\uffff\10\104\12\uffff"+
            "\32\104\5\uffff\113\104\25\uffff\15\104\1\uffff\4\104\16\uffff"+
            "\22\104\16\uffff\22\104\16\uffff\15\104\1\uffff\3\104\17\uffff"+
            "\64\104\43\uffff\1\104\4\uffff\1\104\3\uffff\12\104\46\uffff"+
            "\12\104\6\uffff\130\104\10\uffff\51\104\127\uffff\35\104\51"+
            "\uffff\50\104\2\uffff\5\104\u038b\uffff\154\104\u0094\uffff"+
            "\u009c\104\4\uffff\132\104\6\uffff\26\104\2\uffff\6\104\2\uffff"+
            "\46\104\2\uffff\6\104\2\uffff\10\104\1\uffff\1\104\1\uffff\1"+
            "\104\1\uffff\1\104\1\uffff\37\104\2\uffff\65\104\1\uffff\7\104"+
            "\1\uffff\1\104\3\uffff\3\104\1\uffff\7\104\3\uffff\4\104\2\uffff"+
            "\6\104\4\uffff\15\104\5\uffff\3\104\1\uffff\7\104\164\uffff"+
            "\1\104\15\uffff\1\104\u0082\uffff\1\104\4\uffff\1\104\2\uffff"+
            "\12\104\1\uffff\1\104\3\uffff\5\104\6\uffff\1\104\1\uffff\1"+
            "\104\1\uffff\1\104\1\uffff\4\104\1\uffff\3\104\1\uffff\7\104"+
            "\3\uffff\3\104\5\uffff\5\104\u0ebb\uffff\2\104\52\uffff\5\104"+
            "\5\uffff\2\104\4\uffff\126\104\6\uffff\3\104\1\uffff\132\104"+
            "\1\uffff\4\104\5\uffff\50\104\4\uffff\136\104\21\uffff\30\104"+
            "\70\uffff\20\104\u0200\uffff\u19b6\104\112\uffff\u51a6\104\132"+
            "\uffff\u048d\104\u0773\uffff\u2ba4\104\u215c\uffff\u012e\104"+
            "\2\uffff\73\104\u0095\uffff\7\104\14\uffff\5\104\5\uffff\1\104"+
            "\1\uffff\12\104\1\uffff\15\104\1\uffff\5\104\1\uffff\1\104\1"+
            "\uffff\2\104\1\uffff\2\104\1\uffff\154\104\41\uffff\u016b\104"+
            "\22\uffff\100\104\2\uffff\66\104\50\uffff\14\104\164\uffff\5"+
            "\104\1\uffff\u0087\104\23\uffff\12\104\7\uffff\32\104\6\uffff"+
            "\32\104\13\uffff\131\104\3\uffff\6\104\2\uffff\6\104\2\uffff"+
            "\6\104\2\uffff\3\104",
            "",
            "",
            "",
            "\1\64\1\63\5\uffff\1\67\5\uffff\12\61\5\uffff\1\70\1\uffff"+
            "\16\60\1\113\13\60\1\uffff\1\65\2\uffff\1\62\1\uffff\16\57\1"+
            "\112\13\57\57\uffff\1\44\12\uffff\1\44\4\uffff\1\44\5\uffff"+
            "\27\44\1\uffff\37\44\1\uffff\u013f\44\31\uffff\162\44\4\uffff"+
            "\14\44\16\uffff\5\44\11\uffff\1\44\u008b\uffff\1\44\13\uffff"+
            "\1\44\1\uffff\3\44\1\uffff\1\44\1\uffff\24\44\1\uffff\54\44"+
            "\1\uffff\46\44\1\uffff\5\44\4\uffff\u0082\44\10\uffff\105\44"+
            "\1\uffff\46\44\2\uffff\2\44\6\uffff\20\44\41\uffff\46\44\2\uffff"+
            "\1\44\7\uffff\47\44\110\uffff\33\44\5\uffff\3\44\56\uffff\32"+
            "\44\5\uffff\13\44\25\uffff\12\44\4\uffff\2\44\1\uffff\143\44"+
            "\1\uffff\1\44\17\uffff\2\44\7\uffff\17\44\2\uffff\1\44\20\uffff"+
            "\1\44\1\uffff\36\44\35\uffff\3\44\60\uffff\46\44\13\uffff\1"+
            "\44\u0152\uffff\66\44\3\uffff\1\44\22\uffff\1\44\7\uffff\12"+
            "\44\4\uffff\12\44\25\uffff\10\44\2\uffff\2\44\2\uffff\26\44"+
            "\1\uffff\7\44\1\uffff\1\44\3\uffff\4\44\3\uffff\1\44\36\uffff"+
            "\2\44\1\uffff\3\44\4\uffff\14\44\23\uffff\6\44\4\uffff\2\44"+
            "\2\uffff\26\44\1\uffff\7\44\1\uffff\2\44\1\uffff\2\44\1\uffff"+
            "\2\44\37\uffff\4\44\1\uffff\1\44\7\uffff\12\44\2\uffff\3\44"+
            "\20\uffff\11\44\1\uffff\3\44\1\uffff\26\44\1\uffff\7\44\1\uffff"+
            "\2\44\1\uffff\5\44\3\uffff\1\44\22\uffff\1\44\17\uffff\2\44"+
            "\4\uffff\12\44\25\uffff\10\44\2\uffff\2\44\2\uffff\26\44\1\uffff"+
            "\7\44\1\uffff\2\44\1\uffff\5\44\3\uffff\1\44\36\uffff\2\44\1"+
            "\uffff\3\44\4\uffff\12\44\1\uffff\1\44\21\uffff\1\44\1\uffff"+
            "\6\44\3\uffff\3\44\1\uffff\4\44\3\uffff\2\44\1\uffff\1\44\1"+
            "\uffff\2\44\3\uffff\2\44\3\uffff\3\44\3\uffff\10\44\1\uffff"+
            "\3\44\55\uffff\11\44\25\uffff\10\44\1\uffff\3\44\1\uffff\27"+
            "\44\1\uffff\12\44\1\uffff\5\44\46\uffff\2\44\4\uffff\12\44\25"+
            "\uffff\10\44\1\uffff\3\44\1\uffff\27\44\1\uffff\12\44\1\uffff"+
            "\5\44\3\uffff\1\44\40\uffff\1\44\1\uffff\2\44\4\uffff\12\44"+
            "\25\uffff\10\44\1\uffff\3\44\1\uffff\27\44\1\uffff\20\44\46"+
            "\uffff\2\44\4\uffff\12\44\25\uffff\22\44\3\uffff\30\44\1\uffff"+
            "\11\44\1\uffff\1\44\2\uffff\7\44\72\uffff\60\44\1\uffff\2\44"+
            "\14\uffff\7\44\11\uffff\12\44\47\uffff\2\44\1\uffff\1\44\2\uffff"+
            "\2\44\1\uffff\1\44\2\uffff\1\44\6\uffff\4\44\1\uffff\7\44\1"+
            "\uffff\3\44\1\uffff\1\44\1\uffff\1\44\2\uffff\2\44\1\uffff\4"+
            "\44\1\uffff\2\44\11\uffff\1\44\2\uffff\5\44\1\uffff\1\44\11"+
            "\uffff\12\44\2\uffff\2\44\42\uffff\1\44\37\uffff\12\44\26\uffff"+
            "\10\44\1\uffff\42\44\35\uffff\4\44\164\uffff\42\44\1\uffff\5"+
            "\44\1\uffff\2\44\25\uffff\12\44\6\uffff\6\44\112\uffff\46\44"+
            "\12\uffff\51\44\7\uffff\132\44\5\uffff\104\44\5\uffff\122\44"+
            "\6\uffff\7\44\1\uffff\77\44\1\uffff\1\44\1\uffff\4\44\2\uffff"+
            "\7\44\1\uffff\1\44\1\uffff\4\44\2\uffff\47\44\1\uffff\1\44\1"+
            "\uffff\4\44\2\uffff\37\44\1\uffff\1\44\1\uffff\4\44\2\uffff"+
            "\7\44\1\uffff\1\44\1\uffff\4\44\2\uffff\7\44\1\uffff\7\44\1"+
            "\uffff\27\44\1\uffff\37\44\1\uffff\1\44\1\uffff\4\44\2\uffff"+
            "\7\44\1\uffff\47\44\1\uffff\23\44\16\uffff\11\44\56\uffff\125"+
            "\44\14\uffff\u026c\44\2\uffff\10\44\12\uffff\32\44\5\uffff\113"+
            "\44\25\uffff\15\44\1\uffff\4\44\16\uffff\22\44\16\uffff\22\44"+
            "\16\uffff\15\44\1\uffff\3\44\17\uffff\64\44\43\uffff\1\44\4"+
            "\uffff\1\44\3\uffff\12\44\46\uffff\12\44\6\uffff\130\44\10\uffff"+
            "\51\44\127\uffff\35\44\51\uffff\50\44\2\uffff\5\44\u038b\uffff"+
            "\154\44\u0094\uffff\u009c\44\4\uffff\132\44\6\uffff\26\44\2"+
            "\uffff\6\44\2\uffff\46\44\2\uffff\6\44\2\uffff\10\44\1\uffff"+
            "\1\44\1\uffff\1\44\1\uffff\1\44\1\uffff\37\44\2\uffff\65\44"+
            "\1\uffff\7\44\1\uffff\1\44\3\uffff\3\44\1\uffff\7\44\3\uffff"+
            "\4\44\2\uffff\6\44\4\uffff\15\44\5\uffff\3\44\1\uffff\7\44\164"+
            "\uffff\1\44\15\uffff\1\44\u0082\uffff\1\44\4\uffff\1\44\2\uffff"+
            "\12\44\1\uffff\1\44\3\uffff\5\44\6\uffff\1\44\1\uffff\1\44\1"+
            "\uffff\1\44\1\uffff\4\44\1\uffff\3\44\1\uffff\7\44\3\uffff\3"+
            "\44\5\uffff\5\44\u0ebb\uffff\2\44\52\uffff\5\44\5\uffff\2\44"+
            "\4\uffff\126\44\6\uffff\3\44\1\uffff\132\44\1\uffff\4\44\5\uffff"+
            "\50\44\4\uffff\136\44\21\uffff\30\44\70\uffff\20\44\u0200\uffff"+
            "\u19b6\44\112\uffff\u51a6\44\132\uffff\u048d\44\u0773\uffff"+
            "\u2ba4\44\u215c\uffff\u012e\44\2\uffff\73\44\u0095\uffff\7\44"+
            "\14\uffff\5\44\5\uffff\1\44\1\uffff\12\44\1\uffff\15\44\1\uffff"+
            "\5\44\1\uffff\1\44\1\uffff\2\44\1\uffff\2\44\1\uffff\154\44"+
            "\41\uffff\u016b\44\22\uffff\100\44\2\uffff\66\44\50\uffff\14"+
            "\44\164\uffff\5\44\1\uffff\u0087\44\23\uffff\12\44\7\uffff\32"+
            "\44\6\uffff\32\44\13\uffff\131\44\3\uffff\6\44\2\uffff\6\44"+
            "\2\uffff\6\44\2\uffff\3\44",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\64\1\63\5\uffff\1\67\5\uffff\12\61\5\uffff\1\70\1\uffff"+
            "\32\60\1\uffff\1\65\2\uffff\1\62\1\uffff\32\57\57\uffff\1\44"+
            "\12\uffff\1\44\4\uffff\1\44\5\uffff\27\44\1\uffff\37\44\1\uffff"+
            "\u013f\44\31\uffff\162\44\4\uffff\14\44\16\uffff\5\44\11\uffff"+
            "\1\44\u008b\uffff\1\44\13\uffff\1\44\1\uffff\3\44\1\uffff\1"+
            "\44\1\uffff\24\44\1\uffff\54\44\1\uffff\46\44\1\uffff\5\44\4"+
            "\uffff\u0082\44\10\uffff\105\44\1\uffff\46\44\2\uffff\2\44\6"+
            "\uffff\20\44\41\uffff\46\44\2\uffff\1\44\7\uffff\47\44\110\uffff"+
            "\33\44\5\uffff\3\44\56\uffff\32\44\5\uffff\13\44\25\uffff\12"+
            "\44\4\uffff\2\44\1\uffff\143\44\1\uffff\1\44\17\uffff\2\44\7"+
            "\uffff\17\44\2\uffff\1\44\20\uffff\1\44\1\uffff\36\44\35\uffff"+
            "\3\44\60\uffff\46\44\13\uffff\1\44\u0152\uffff\66\44\3\uffff"+
            "\1\44\22\uffff\1\44\7\uffff\12\44\4\uffff\12\44\25\uffff\10"+
            "\44\2\uffff\2\44\2\uffff\26\44\1\uffff\7\44\1\uffff\1\44\3\uffff"+
            "\4\44\3\uffff\1\44\36\uffff\2\44\1\uffff\3\44\4\uffff\14\44"+
            "\23\uffff\6\44\4\uffff\2\44\2\uffff\26\44\1\uffff\7\44\1\uffff"+
            "\2\44\1\uffff\2\44\1\uffff\2\44\37\uffff\4\44\1\uffff\1\44\7"+
            "\uffff\12\44\2\uffff\3\44\20\uffff\11\44\1\uffff\3\44\1\uffff"+
            "\26\44\1\uffff\7\44\1\uffff\2\44\1\uffff\5\44\3\uffff\1\44\22"+
            "\uffff\1\44\17\uffff\2\44\4\uffff\12\44\25\uffff\10\44\2\uffff"+
            "\2\44\2\uffff\26\44\1\uffff\7\44\1\uffff\2\44\1\uffff\5\44\3"+
            "\uffff\1\44\36\uffff\2\44\1\uffff\3\44\4\uffff\12\44\1\uffff"+
            "\1\44\21\uffff\1\44\1\uffff\6\44\3\uffff\3\44\1\uffff\4\44\3"+
            "\uffff\2\44\1\uffff\1\44\1\uffff\2\44\3\uffff\2\44\3\uffff\3"+
            "\44\3\uffff\10\44\1\uffff\3\44\55\uffff\11\44\25\uffff\10\44"+
            "\1\uffff\3\44\1\uffff\27\44\1\uffff\12\44\1\uffff\5\44\46\uffff"+
            "\2\44\4\uffff\12\44\25\uffff\10\44\1\uffff\3\44\1\uffff\27\44"+
            "\1\uffff\12\44\1\uffff\5\44\3\uffff\1\44\40\uffff\1\44\1\uffff"+
            "\2\44\4\uffff\12\44\25\uffff\10\44\1\uffff\3\44\1\uffff\27\44"+
            "\1\uffff\20\44\46\uffff\2\44\4\uffff\12\44\25\uffff\22\44\3"+
            "\uffff\30\44\1\uffff\11\44\1\uffff\1\44\2\uffff\7\44\72\uffff"+
            "\60\44\1\uffff\2\44\14\uffff\7\44\11\uffff\12\44\47\uffff\2"+
            "\44\1\uffff\1\44\2\uffff\2\44\1\uffff\1\44\2\uffff\1\44\6\uffff"+
            "\4\44\1\uffff\7\44\1\uffff\3\44\1\uffff\1\44\1\uffff\1\44\2"+
            "\uffff\2\44\1\uffff\4\44\1\uffff\2\44\11\uffff\1\44\2\uffff"+
            "\5\44\1\uffff\1\44\11\uffff\12\44\2\uffff\2\44\42\uffff\1\44"+
            "\37\uffff\12\44\26\uffff\10\44\1\uffff\42\44\35\uffff\4\44\164"+
            "\uffff\42\44\1\uffff\5\44\1\uffff\2\44\25\uffff\12\44\6\uffff"+
            "\6\44\112\uffff\46\44\12\uffff\51\44\7\uffff\132\44\5\uffff"+
            "\104\44\5\uffff\122\44\6\uffff\7\44\1\uffff\77\44\1\uffff\1"+
            "\44\1\uffff\4\44\2\uffff\7\44\1\uffff\1\44\1\uffff\4\44\2\uffff"+
            "\47\44\1\uffff\1\44\1\uffff\4\44\2\uffff\37\44\1\uffff\1\44"+
            "\1\uffff\4\44\2\uffff\7\44\1\uffff\1\44\1\uffff\4\44\2\uffff"+
            "\7\44\1\uffff\7\44\1\uffff\27\44\1\uffff\37\44\1\uffff\1\44"+
            "\1\uffff\4\44\2\uffff\7\44\1\uffff\47\44\1\uffff\23\44\16\uffff"+
            "\11\44\56\uffff\125\44\14\uffff\u026c\44\2\uffff\10\44\12\uffff"+
            "\32\44\5\uffff\113\44\25\uffff\15\44\1\uffff\4\44\16\uffff\22"+
            "\44\16\uffff\22\44\16\uffff\15\44\1\uffff\3\44\17\uffff\64\44"+
            "\43\uffff\1\44\4\uffff\1\44\3\uffff\12\44\46\uffff\12\44\6\uffff"+
            "\130\44\10\uffff\51\44\127\uffff\35\44\51\uffff\50\44\2\uffff"+
            "\5\44\u038b\uffff\154\44\u0094\uffff\u009c\44\4\uffff\132\44"+
            "\6\uffff\26\44\2\uffff\6\44\2\uffff\46\44\2\uffff\6\44\2\uffff"+
            "\10\44\1\uffff\1\44\1\uffff\1\44\1\uffff\1\44\1\uffff\37\44"+
            "\2\uffff\65\44\1\uffff\7\44\1\uffff\1\44\3\uffff\3\44\1\uffff"+
            "\7\44\3\uffff\4\44\2\uffff\6\44\4\uffff\15\44\5\uffff\3\44\1"+
            "\uffff\7\44\164\uffff\1\44\15\uffff\1\44\u0082\uffff\1\44\4"+
            "\uffff\1\44\2\uffff\12\44\1\uffff\1\44\3\uffff\5\44\6\uffff"+
            "\1\44\1\uffff\1\44\1\uffff\1\44\1\uffff\4\44\1\uffff\3\44\1"+
            "\uffff\7\44\3\uffff\3\44\5\uffff\5\44\u0ebb\uffff\2\44\52\uffff"+
            "\5\44\5\uffff\2\44\4\uffff\126\44\6\uffff\3\44\1\uffff\132\44"+
            "\1\uffff\4\44\5\uffff\50\44\4\uffff\136\44\21\uffff\30\44\70"+
            "\uffff\20\44\u0200\uffff\u19b6\44\112\uffff\u51a6\44\132\uffff"+
            "\u048d\44\u0773\uffff\u2ba4\44\u215c\uffff\u012e\44\2\uffff"+
            "\73\44\u0095\uffff\7\44\14\uffff\5\44\5\uffff\1\44\1\uffff\12"+
            "\44\1\uffff\15\44\1\uffff\5\44\1\uffff\1\44\1\uffff\2\44\1\uffff"+
            "\2\44\1\uffff\154\44\41\uffff\u016b\44\22\uffff\100\44\2\uffff"+
            "\66\44\50\uffff\14\44\164\uffff\5\44\1\uffff\u0087\44\23\uffff"+
            "\12\44\7\uffff\32\44\6\uffff\32\44\13\uffff\131\44\3\uffff\6"+
            "\44\2\uffff\6\44\2\uffff\6\44\2\uffff\3\44",
            "\1\67\3\uffff\1\116\1\uffff\12\115\5\uffff\1\70\1\uffff\4"+
            "\44\1\117\25\44\1\uffff\1\42\4\uffff\4\44\1\117\25\44\57\uffff"+
            "\1\44\12\uffff\1\44\4\uffff\1\44\5\uffff\27\44\1\uffff\37\44"+
            "\1\uffff\u013f\44\31\uffff\162\44\4\uffff\14\44\16\uffff\5\44"+
            "\11\uffff\1\44\u008b\uffff\1\44\13\uffff\1\44\1\uffff\3\44\1"+
            "\uffff\1\44\1\uffff\24\44\1\uffff\54\44\1\uffff\46\44\1\uffff"+
            "\5\44\4\uffff\u0082\44\10\uffff\105\44\1\uffff\46\44\2\uffff"+
            "\2\44\6\uffff\20\44\41\uffff\46\44\2\uffff\1\44\7\uffff\47\44"+
            "\110\uffff\33\44\5\uffff\3\44\56\uffff\32\44\5\uffff\13\44\25"+
            "\uffff\12\44\4\uffff\2\44\1\uffff\143\44\1\uffff\1\44\17\uffff"+
            "\2\44\7\uffff\17\44\2\uffff\1\44\20\uffff\1\44\1\uffff\36\44"+
            "\35\uffff\3\44\60\uffff\46\44\13\uffff\1\44\u0152\uffff\66\44"+
            "\3\uffff\1\44\22\uffff\1\44\7\uffff\12\44\4\uffff\12\44\25\uffff"+
            "\10\44\2\uffff\2\44\2\uffff\26\44\1\uffff\7\44\1\uffff\1\44"+
            "\3\uffff\4\44\3\uffff\1\44\36\uffff\2\44\1\uffff\3\44\4\uffff"+
            "\14\44\23\uffff\6\44\4\uffff\2\44\2\uffff\26\44\1\uffff\7\44"+
            "\1\uffff\2\44\1\uffff\2\44\1\uffff\2\44\37\uffff\4\44\1\uffff"+
            "\1\44\7\uffff\12\44\2\uffff\3\44\20\uffff\11\44\1\uffff\3\44"+
            "\1\uffff\26\44\1\uffff\7\44\1\uffff\2\44\1\uffff\5\44\3\uffff"+
            "\1\44\22\uffff\1\44\17\uffff\2\44\4\uffff\12\44\25\uffff\10"+
            "\44\2\uffff\2\44\2\uffff\26\44\1\uffff\7\44\1\uffff\2\44\1\uffff"+
            "\5\44\3\uffff\1\44\36\uffff\2\44\1\uffff\3\44\4\uffff\12\44"+
            "\1\uffff\1\44\21\uffff\1\44\1\uffff\6\44\3\uffff\3\44\1\uffff"+
            "\4\44\3\uffff\2\44\1\uffff\1\44\1\uffff\2\44\3\uffff\2\44\3"+
            "\uffff\3\44\3\uffff\10\44\1\uffff\3\44\55\uffff\11\44\25\uffff"+
            "\10\44\1\uffff\3\44\1\uffff\27\44\1\uffff\12\44\1\uffff\5\44"+
            "\46\uffff\2\44\4\uffff\12\44\25\uffff\10\44\1\uffff\3\44\1\uffff"+
            "\27\44\1\uffff\12\44\1\uffff\5\44\3\uffff\1\44\40\uffff\1\44"+
            "\1\uffff\2\44\4\uffff\12\44\25\uffff\10\44\1\uffff\3\44\1\uffff"+
            "\27\44\1\uffff\20\44\46\uffff\2\44\4\uffff\12\44\25\uffff\22"+
            "\44\3\uffff\30\44\1\uffff\11\44\1\uffff\1\44\2\uffff\7\44\72"+
            "\uffff\60\44\1\uffff\2\44\14\uffff\7\44\11\uffff\12\44\47\uffff"+
            "\2\44\1\uffff\1\44\2\uffff\2\44\1\uffff\1\44\2\uffff\1\44\6"+
            "\uffff\4\44\1\uffff\7\44\1\uffff\3\44\1\uffff\1\44\1\uffff\1"+
            "\44\2\uffff\2\44\1\uffff\4\44\1\uffff\2\44\11\uffff\1\44\2\uffff"+
            "\5\44\1\uffff\1\44\11\uffff\12\44\2\uffff\2\44\42\uffff\1\44"+
            "\37\uffff\12\44\26\uffff\10\44\1\uffff\42\44\35\uffff\4\44\164"+
            "\uffff\42\44\1\uffff\5\44\1\uffff\2\44\25\uffff\12\44\6\uffff"+
            "\6\44\112\uffff\46\44\12\uffff\51\44\7\uffff\132\44\5\uffff"+
            "\104\44\5\uffff\122\44\6\uffff\7\44\1\uffff\77\44\1\uffff\1"+
            "\44\1\uffff\4\44\2\uffff\7\44\1\uffff\1\44\1\uffff\4\44\2\uffff"+
            "\47\44\1\uffff\1\44\1\uffff\4\44\2\uffff\37\44\1\uffff\1\44"+
            "\1\uffff\4\44\2\uffff\7\44\1\uffff\1\44\1\uffff\4\44\2\uffff"+
            "\7\44\1\uffff\7\44\1\uffff\27\44\1\uffff\37\44\1\uffff\1\44"+
            "\1\uffff\4\44\2\uffff\7\44\1\uffff\47\44\1\uffff\23\44\16\uffff"+
            "\11\44\56\uffff\125\44\14\uffff\u026c\44\2\uffff\10\44\12\uffff"+
            "\32\44\5\uffff\113\44\25\uffff\15\44\1\uffff\4\44\16\uffff\22"+
            "\44\16\uffff\22\44\16\uffff\15\44\1\uffff\3\44\17\uffff\64\44"+
            "\43\uffff\1\44\4\uffff\1\44\3\uffff\12\44\46\uffff\12\44\6\uffff"+
            "\130\44\10\uffff\51\44\127\uffff\35\44\51\uffff\50\44\2\uffff"+
            "\5\44\u038b\uffff\154\44\u0094\uffff\u009c\44\4\uffff\132\44"+
            "\6\uffff\26\44\2\uffff\6\44\2\uffff\46\44\2\uffff\6\44\2\uffff"+
            "\10\44\1\uffff\1\44\1\uffff\1\44\1\uffff\1\44\1\uffff\37\44"+
            "\2\uffff\65\44\1\uffff\7\44\1\uffff\1\44\3\uffff\3\44\1\uffff"+
            "\7\44\3\uffff\4\44\2\uffff\6\44\4\uffff\15\44\5\uffff\3\44\1"+
            "\uffff\7\44\164\uffff\1\44\15\uffff\1\44\u0082\uffff\1\44\4"+
            "\uffff\1\44\2\uffff\12\44\1\uffff\1\44\3\uffff\5\44\6\uffff"+
            "\1\44\1\uffff\1\44\1\uffff\1\44\1\uffff\4\44\1\uffff\3\44\1"+
            "\uffff\7\44\3\uffff\3\44\5\uffff\5\44\u0ebb\uffff\2\44\52\uffff"+
            "\5\44\5\uffff\2\44\4\uffff\126\44\6\uffff\3\44\1\uffff\132\44"+
            "\1\uffff\4\44\5\uffff\50\44\4\uffff\136\44\21\uffff\30\44\70"+
            "\uffff\20\44\u0200\uffff\u19b6\44\112\uffff\u51a6\44\132\uffff"+
            "\u048d\44\u0773\uffff\u2ba4\44\u215c\uffff\u012e\44\2\uffff"+
            "\73\44\u0095\uffff\7\44\14\uffff\5\44\5\uffff\1\44\1\uffff\12"+
            "\44\1\uffff\15\44\1\uffff\5\44\1\uffff\1\44\1\uffff\2\44\1\uffff"+
            "\2\44\1\uffff\154\44\41\uffff\u016b\44\22\uffff\100\44\2\uffff"+
            "\66\44\50\uffff\14\44\164\uffff\5\44\1\uffff\u0087\44\23\uffff"+
            "\12\44\7\uffff\32\44\6\uffff\32\44\13\uffff\131\44\3\uffff\6"+
            "\44\2\uffff\6\44\2\uffff\6\44\2\uffff\3\44",
            "\1\67\3\uffff\1\116\1\uffff\12\120\5\uffff\1\70\1\uffff\4"+
            "\44\1\117\25\44\1\uffff\1\42\4\uffff\4\44\1\117\25\44\57\uffff"+
            "\1\44\12\uffff\1\44\4\uffff\1\44\5\uffff\27\44\1\uffff\37\44"+
            "\1\uffff\u013f\44\31\uffff\162\44\4\uffff\14\44\16\uffff\5\44"+
            "\11\uffff\1\44\u008b\uffff\1\44\13\uffff\1\44\1\uffff\3\44\1"+
            "\uffff\1\44\1\uffff\24\44\1\uffff\54\44\1\uffff\46\44\1\uffff"+
            "\5\44\4\uffff\u0082\44\10\uffff\105\44\1\uffff\46\44\2\uffff"+
            "\2\44\6\uffff\20\44\41\uffff\46\44\2\uffff\1\44\7\uffff\47\44"+
            "\110\uffff\33\44\5\uffff\3\44\56\uffff\32\44\5\uffff\13\44\25"+
            "\uffff\12\44\4\uffff\2\44\1\uffff\143\44\1\uffff\1\44\17\uffff"+
            "\2\44\7\uffff\17\44\2\uffff\1\44\20\uffff\1\44\1\uffff\36\44"+
            "\35\uffff\3\44\60\uffff\46\44\13\uffff\1\44\u0152\uffff\66\44"+
            "\3\uffff\1\44\22\uffff\1\44\7\uffff\12\44\4\uffff\12\44\25\uffff"+
            "\10\44\2\uffff\2\44\2\uffff\26\44\1\uffff\7\44\1\uffff\1\44"+
            "\3\uffff\4\44\3\uffff\1\44\36\uffff\2\44\1\uffff\3\44\4\uffff"+
            "\14\44\23\uffff\6\44\4\uffff\2\44\2\uffff\26\44\1\uffff\7\44"+
            "\1\uffff\2\44\1\uffff\2\44\1\uffff\2\44\37\uffff\4\44\1\uffff"+
            "\1\44\7\uffff\12\44\2\uffff\3\44\20\uffff\11\44\1\uffff\3\44"+
            "\1\uffff\26\44\1\uffff\7\44\1\uffff\2\44\1\uffff\5\44\3\uffff"+
            "\1\44\22\uffff\1\44\17\uffff\2\44\4\uffff\12\44\25\uffff\10"+
            "\44\2\uffff\2\44\2\uffff\26\44\1\uffff\7\44\1\uffff\2\44\1\uffff"+
            "\5\44\3\uffff\1\44\36\uffff\2\44\1\uffff\3\44\4\uffff\12\44"+
            "\1\uffff\1\44\21\uffff\1\44\1\uffff\6\44\3\uffff\3\44\1\uffff"+
            "\4\44\3\uffff\2\44\1\uffff\1\44\1\uffff\2\44\3\uffff\2\44\3"+
            "\uffff\3\44\3\uffff\10\44\1\uffff\3\44\55\uffff\11\44\25\uffff"+
            "\10\44\1\uffff\3\44\1\uffff\27\44\1\uffff\12\44\1\uffff\5\44"+
            "\46\uffff\2\44\4\uffff\12\44\25\uffff\10\44\1\uffff\3\44\1\uffff"+
            "\27\44\1\uffff\12\44\1\uffff\5\44\3\uffff\1\44\40\uffff\1\44"+
            "\1\uffff\2\44\4\uffff\12\44\25\uffff\10\44\1\uffff\3\44\1\uffff"+
            "\27\44\1\uffff\20\44\46\uffff\2\44\4\uffff\12\44\25\uffff\22"+
            "\44\3\uffff\30\44\1\uffff\11\44\1\uffff\1\44\2\uffff\7\44\72"+
            "\uffff\60\44\1\uffff\2\44\14\uffff\7\44\11\uffff\12\44\47\uffff"+
            "\2\44\1\uffff\1\44\2\uffff\2\44\1\uffff\1\44\2\uffff\1\44\6"+
            "\uffff\4\44\1\uffff\7\44\1\uffff\3\44\1\uffff\1\44\1\uffff\1"+
            "\44\2\uffff\2\44\1\uffff\4\44\1\uffff\2\44\11\uffff\1\44\2\uffff"+
            "\5\44\1\uffff\1\44\11\uffff\12\44\2\uffff\2\44\42\uffff\1\44"+
            "\37\uffff\12\44\26\uffff\10\44\1\uffff\42\44\35\uffff\4\44\164"+
            "\uffff\42\44\1\uffff\5\44\1\uffff\2\44\25\uffff\12\44\6\uffff"+
            "\6\44\112\uffff\46\44\12\uffff\51\44\7\uffff\132\44\5\uffff"+
            "\104\44\5\uffff\122\44\6\uffff\7\44\1\uffff\77\44\1\uffff\1"+
            "\44\1\uffff\4\44\2\uffff\7\44\1\uffff\1\44\1\uffff\4\44\2\uffff"+
            "\47\44\1\uffff\1\44\1\uffff\4\44\2\uffff\37\44\1\uffff\1\44"+
            "\1\uffff\4\44\2\uffff\7\44\1\uffff\1\44\1\uffff\4\44\2\uffff"+
            "\7\44\1\uffff\7\44\1\uffff\27\44\1\uffff\37\44\1\uffff\1\44"+
            "\1\uffff\4\44\2\uffff\7\44\1\uffff\47\44\1\uffff\23\44\16\uffff"+
            "\11\44\56\uffff\125\44\14\uffff\u026c\44\2\uffff\10\44\12\uffff"+
            "\32\44\5\uffff\113\44\25\uffff\15\44\1\uffff\4\44\16\uffff\22"+
            "\44\16\uffff\22\44\16\uffff\15\44\1\uffff\3\44\17\uffff\64\44"+
            "\43\uffff\1\44\4\uffff\1\44\3\uffff\12\44\46\uffff\12\44\6\uffff"+
            "\130\44\10\uffff\51\44\127\uffff\35\44\51\uffff\50\44\2\uffff"+
            "\5\44\u038b\uffff\154\44\u0094\uffff\u009c\44\4\uffff\132\44"+
            "\6\uffff\26\44\2\uffff\6\44\2\uffff\46\44\2\uffff\6\44\2\uffff"+
            "\10\44\1\uffff\1\44\1\uffff\1\44\1\uffff\1\44\1\uffff\37\44"+
            "\2\uffff\65\44\1\uffff\7\44\1\uffff\1\44\3\uffff\3\44\1\uffff"+
            "\7\44\3\uffff\4\44\2\uffff\6\44\4\uffff\15\44\5\uffff\3\44\1"+
            "\uffff\7\44\164\uffff\1\44\15\uffff\1\44\u0082\uffff\1\44\4"+
            "\uffff\1\44\2\uffff\12\44\1\uffff\1\44\3\uffff\5\44\6\uffff"+
            "\1\44\1\uffff\1\44\1\uffff\1\44\1\uffff\4\44\1\uffff\3\44\1"+
            "\uffff\7\44\3\uffff\3\44\5\uffff\5\44\u0ebb\uffff\2\44\52\uffff"+
            "\5\44\5\uffff\2\44\4\uffff\126\44\6\uffff\3\44\1\uffff\132\44"+
            "\1\uffff\4\44\5\uffff\50\44\4\uffff\136\44\21\uffff\30\44\70"+
            "\uffff\20\44\u0200\uffff\u19b6\44\112\uffff\u51a6\44\132\uffff"+
            "\u048d\44\u0773\uffff\u2ba4\44\u215c\uffff\u012e\44\2\uffff"+
            "\73\44\u0095\uffff\7\44\14\uffff\5\44\5\uffff\1\44\1\uffff\12"+
            "\44\1\uffff\15\44\1\uffff\5\44\1\uffff\1\44\1\uffff\2\44\1\uffff"+
            "\2\44\1\uffff\154\44\41\uffff\u016b\44\22\uffff\100\44\2\uffff"+
            "\66\44\50\uffff\14\44\164\uffff\5\44\1\uffff\u0087\44\23\uffff"+
            "\12\44\7\uffff\32\44\6\uffff\32\44\13\uffff\131\44\3\uffff\6"+
            "\44\2\uffff\6\44\2\uffff\6\44\2\uffff\3\44",
            "\165\122\1\121\uff8a\122",
            "",
            "\1\67\5\uffff\12\44\5\uffff\1\70\1\uffff\32\44\1\uffff\1\42"+
            "\4\uffff\32\44\57\uffff\1\44\12\uffff\1\44\4\uffff\1\44\5\uffff"+
            "\27\44\1\uffff\37\44\1\uffff\u013f\44\31\uffff\162\44\4\uffff"+
            "\14\44\16\uffff\5\44\11\uffff\1\44\u008b\uffff\1\44\13\uffff"+
            "\1\44\1\uffff\3\44\1\uffff\1\44\1\uffff\24\44\1\uffff\54\44"+
            "\1\uffff\46\44\1\uffff\5\44\4\uffff\u0082\44\10\uffff\105\44"+
            "\1\uffff\46\44\2\uffff\2\44\6\uffff\20\44\41\uffff\46\44\2\uffff"+
            "\1\44\7\uffff\47\44\110\uffff\33\44\5\uffff\3\44\56\uffff\32"+
            "\44\5\uffff\13\44\25\uffff\12\44\4\uffff\2\44\1\uffff\143\44"+
            "\1\uffff\1\44\17\uffff\2\44\7\uffff\17\44\2\uffff\1\44\20\uffff"+
            "\1\44\1\uffff\36\44\35\uffff\3\44\60\uffff\46\44\13\uffff\1"+
            "\44\u0152\uffff\66\44\3\uffff\1\44\22\uffff\1\44\7\uffff\12"+
            "\44\4\uffff\12\44\25\uffff\10\44\2\uffff\2\44\2\uffff\26\44"+
            "\1\uffff\7\44\1\uffff\1\44\3\uffff\4\44\3\uffff\1\44\36\uffff"+
            "\2\44\1\uffff\3\44\4\uffff\14\44\23\uffff\6\44\4\uffff\2\44"+
            "\2\uffff\26\44\1\uffff\7\44\1\uffff\2\44\1\uffff\2\44\1\uffff"+
            "\2\44\37\uffff\4\44\1\uffff\1\44\7\uffff\12\44\2\uffff\3\44"+
            "\20\uffff\11\44\1\uffff\3\44\1\uffff\26\44\1\uffff\7\44\1\uffff"+
            "\2\44\1\uffff\5\44\3\uffff\1\44\22\uffff\1\44\17\uffff\2\44"+
            "\4\uffff\12\44\25\uffff\10\44\2\uffff\2\44\2\uffff\26\44\1\uffff"+
            "\7\44\1\uffff\2\44\1\uffff\5\44\3\uffff\1\44\36\uffff\2\44\1"+
            "\uffff\3\44\4\uffff\12\44\1\uffff\1\44\21\uffff\1\44\1\uffff"+
            "\6\44\3\uffff\3\44\1\uffff\4\44\3\uffff\2\44\1\uffff\1\44\1"+
            "\uffff\2\44\3\uffff\2\44\3\uffff\3\44\3\uffff\10\44\1\uffff"+
            "\3\44\55\uffff\11\44\25\uffff\10\44\1\uffff\3\44\1\uffff\27"+
            "\44\1\uffff\12\44\1\uffff\5\44\46\uffff\2\44\4\uffff\12\44\25"+
            "\uffff\10\44\1\uffff\3\44\1\uffff\27\44\1\uffff\12\44\1\uffff"+
            "\5\44\3\uffff\1\44\40\uffff\1\44\1\uffff\2\44\4\uffff\12\44"+
            "\25\uffff\10\44\1\uffff\3\44\1\uffff\27\44\1\uffff\20\44\46"+
            "\uffff\2\44\4\uffff\12\44\25\uffff\22\44\3\uffff\30\44\1\uffff"+
            "\11\44\1\uffff\1\44\2\uffff\7\44\72\uffff\60\44\1\uffff\2\44"+
            "\14\uffff\7\44\11\uffff\12\44\47\uffff\2\44\1\uffff\1\44\2\uffff"+
            "\2\44\1\uffff\1\44\2\uffff\1\44\6\uffff\4\44\1\uffff\7\44\1"+
            "\uffff\3\44\1\uffff\1\44\1\uffff\1\44\2\uffff\2\44\1\uffff\4"+
            "\44\1\uffff\2\44\11\uffff\1\44\2\uffff\5\44\1\uffff\1\44\11"+
            "\uffff\12\44\2\uffff\2\44\42\uffff\1\44\37\uffff\12\44\26\uffff"+
            "\10\44\1\uffff\42\44\35\uffff\4\44\164\uffff\42\44\1\uffff\5"+
            "\44\1\uffff\2\44\25\uffff\12\44\6\uffff\6\44\112\uffff\46\44"+
            "\12\uffff\51\44\7\uffff\132\44\5\uffff\104\44\5\uffff\122\44"+
            "\6\uffff\7\44\1\uffff\77\44\1\uffff\1\44\1\uffff\4\44\2\uffff"+
            "\7\44\1\uffff\1\44\1\uffff\4\44\2\uffff\47\44\1\uffff\1\44\1"+
            "\uffff\4\44\2\uffff\37\44\1\uffff\1\44\1\uffff\4\44\2\uffff"+
            "\7\44\1\uffff\1\44\1\uffff\4\44\2\uffff\7\44\1\uffff\7\44\1"+
            "\uffff\27\44\1\uffff\37\44\1\uffff\1\44\1\uffff\4\44\2\uffff"+
            "\7\44\1\uffff\47\44\1\uffff\23\44\16\uffff\11\44\56\uffff\125"+
            "\44\14\uffff\u026c\44\2\uffff\10\44\12\uffff\32\44\5\uffff\113"+
            "\44\25\uffff\15\44\1\uffff\4\44\16\uffff\22\44\16\uffff\22\44"+
            "\16\uffff\15\44\1\uffff\3\44\17\uffff\64\44\43\uffff\1\44\4"+
            "\uffff\1\44\3\uffff\12\44\46\uffff\12\44\6\uffff\130\44\10\uffff"+
            "\51\44\127\uffff\35\44\51\uffff\50\44\2\uffff\5\44\u038b\uffff"+
            "\154\44\u0094\uffff\u009c\44\4\uffff\132\44\6\uffff\26\44\2"+
            "\uffff\6\44\2\uffff\46\44\2\uffff\6\44\2\uffff\10\44\1\uffff"+
            "\1\44\1\uffff\1\44\1\uffff\1\44\1\uffff\37\44\2\uffff\65\44"+
            "\1\uffff\7\44\1\uffff\1\44\3\uffff\3\44\1\uffff\7\44\3\uffff"+
            "\4\44\2\uffff\6\44\4\uffff\15\44\5\uffff\3\44\1\uffff\7\44\164"+
            "\uffff\1\44\15\uffff\1\44\u0082\uffff\1\44\4\uffff\1\44\2\uffff"+
            "\12\44\1\uffff\1\44\3\uffff\5\44\6\uffff\1\44\1\uffff\1\44\1"+
            "\uffff\1\44\1\uffff\4\44\1\uffff\3\44\1\uffff\7\44\3\uffff\3"+
            "\44\5\uffff\5\44\u0ebb\uffff\2\44\52\uffff\5\44\5\uffff\2\44"+
            "\4\uffff\126\44\6\uffff\3\44\1\uffff\132\44\1\uffff\4\44\5\uffff"+
            "\50\44\4\uffff\136\44\21\uffff\30\44\70\uffff\20\44\u0200\uffff"+
            "\u19b6\44\112\uffff\u51a6\44\132\uffff\u048d\44\u0773\uffff"+
            "\u2ba4\44\u215c\uffff\u012e\44\2\uffff\73\44\u0095\uffff\7\44"+
            "\14\uffff\5\44\5\uffff\1\44\1\uffff\12\44\1\uffff\15\44\1\uffff"+
            "\5\44\1\uffff\1\44\1\uffff\2\44\1\uffff\2\44\1\uffff\154\44"+
            "\41\uffff\u016b\44\22\uffff\100\44\2\uffff\66\44\50\uffff\14"+
            "\44\164\uffff\5\44\1\uffff\u0087\44\23\uffff\12\44\7\uffff\32"+
            "\44\6\uffff\32\44\13\uffff\131\44\3\uffff\6\44\2\uffff\6\44"+
            "\2\uffff\6\44\2\uffff\3\44",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\64\1\63\5\uffff\1\67\5\uffff\12\61\5\uffff\1\70\1\uffff"+
            "\32\60\1\uffff\1\65\2\uffff\1\62\1\uffff\32\57\57\uffff\1\44"+
            "\12\uffff\1\44\4\uffff\1\44\5\uffff\27\44\1\uffff\37\44\1\uffff"+
            "\u013f\44\31\uffff\162\44\4\uffff\14\44\16\uffff\5\44\11\uffff"+
            "\1\44\u008b\uffff\1\44\13\uffff\1\44\1\uffff\3\44\1\uffff\1"+
            "\44\1\uffff\24\44\1\uffff\54\44\1\uffff\46\44\1\uffff\5\44\4"+
            "\uffff\u0082\44\10\uffff\105\44\1\uffff\46\44\2\uffff\2\44\6"+
            "\uffff\20\44\41\uffff\46\44\2\uffff\1\44\7\uffff\47\44\110\uffff"+
            "\33\44\5\uffff\3\44\56\uffff\32\44\5\uffff\13\44\25\uffff\12"+
            "\44\4\uffff\2\44\1\uffff\143\44\1\uffff\1\44\17\uffff\2\44\7"+
            "\uffff\17\44\2\uffff\1\44\20\uffff\1\44\1\uffff\36\44\35\uffff"+
            "\3\44\60\uffff\46\44\13\uffff\1\44\u0152\uffff\66\44\3\uffff"+
            "\1\44\22\uffff\1\44\7\uffff\12\44\4\uffff\12\44\25\uffff\10"+
            "\44\2\uffff\2\44\2\uffff\26\44\1\uffff\7\44\1\uffff\1\44\3\uffff"+
            "\4\44\3\uffff\1\44\36\uffff\2\44\1\uffff\3\44\4\uffff\14\44"+
            "\23\uffff\6\44\4\uffff\2\44\2\uffff\26\44\1\uffff\7\44\1\uffff"+
            "\2\44\1\uffff\2\44\1\uffff\2\44\37\uffff\4\44\1\uffff\1\44\7"+
            "\uffff\12\44\2\uffff\3\44\20\uffff\11\44\1\uffff\3\44\1\uffff"+
            "\26\44\1\uffff\7\44\1\uffff\2\44\1\uffff\5\44\3\uffff\1\44\22"+
            "\uffff\1\44\17\uffff\2\44\4\uffff\12\44\25\uffff\10\44\2\uffff"+
            "\2\44\2\uffff\26\44\1\uffff\7\44\1\uffff\2\44\1\uffff\5\44\3"+
            "\uffff\1\44\36\uffff\2\44\1\uffff\3\44\4\uffff\12\44\1\uffff"+
            "\1\44\21\uffff\1\44\1\uffff\6\44\3\uffff\3\44\1\uffff\4\44\3"+
            "\uffff\2\44\1\uffff\1\44\1\uffff\2\44\3\uffff\2\44\3\uffff\3"+
            "\44\3\uffff\10\44\1\uffff\3\44\55\uffff\11\44\25\uffff\10\44"+
            "\1\uffff\3\44\1\uffff\27\44\1\uffff\12\44\1\uffff\5\44\46\uffff"+
            "\2\44\4\uffff\12\44\25\uffff\10\44\1\uffff\3\44\1\uffff\27\44"+
            "\1\uffff\12\44\1\uffff\5\44\3\uffff\1\44\40\uffff\1\44\1\uffff"+
            "\2\44\4\uffff\12\44\25\uffff\10\44\1\uffff\3\44\1\uffff\27\44"+
            "\1\uffff\20\44\46\uffff\2\44\4\uffff\12\44\25\uffff\22\44\3"+
            "\uffff\30\44\1\uffff\11\44\1\uffff\1\44\2\uffff\7\44\72\uffff"+
            "\60\44\1\uffff\2\44\14\uffff\7\44\11\uffff\12\44\47\uffff\2"+
            "\44\1\uffff\1\44\2\uffff\2\44\1\uffff\1\44\2\uffff\1\44\6\uffff"+
            "\4\44\1\uffff\7\44\1\uffff\3\44\1\uffff\1\44\1\uffff\1\44\2"+
            "\uffff\2\44\1\uffff\4\44\1\uffff\2\44\11\uffff\1\44\2\uffff"+
            "\5\44\1\uffff\1\44\11\uffff\12\44\2\uffff\2\44\42\uffff\1\44"+
            "\37\uffff\12\44\26\uffff\10\44\1\uffff\42\44\35\uffff\4\44\164"+
            "\uffff\42\44\1\uffff\5\44\1\uffff\2\44\25\uffff\12\44\6\uffff"+
            "\6\44\112\uffff\46\44\12\uffff\51\44\7\uffff\132\44\5\uffff"+
            "\104\44\5\uffff\122\44\6\uffff\7\44\1\uffff\77\44\1\uffff\1"+
            "\44\1\uffff\4\44\2\uffff\7\44\1\uffff\1\44\1\uffff\4\44\2\uffff"+
            "\47\44\1\uffff\1\44\1\uffff\4\44\2\uffff\37\44\1\uffff\1\44"+
            "\1\uffff\4\44\2\uffff\7\44\1\uffff\1\44\1\uffff\4\44\2\uffff"+
            "\7\44\1\uffff\7\44\1\uffff\27\44\1\uffff\37\44\1\uffff\1\44"+
            "\1\uffff\4\44\2\uffff\7\44\1\uffff\47\44\1\uffff\23\44\16\uffff"+
            "\11\44\56\uffff\125\44\14\uffff\u026c\44\2\uffff\10\44\12\uffff"+
            "\32\44\5\uffff\113\44\25\uffff\15\44\1\uffff\4\44\16\uffff\22"+
            "\44\16\uffff\22\44\16\uffff\15\44\1\uffff\3\44\17\uffff\64\44"+
            "\43\uffff\1\44\4\uffff\1\44\3\uffff\12\44\46\uffff\12\44\6\uffff"+
            "\130\44\10\uffff\51\44\127\uffff\35\44\51\uffff\50\44\2\uffff"+
            "\5\44\u038b\uffff\154\44\u0094\uffff\u009c\44\4\uffff\132\44"+
            "\6\uffff\26\44\2\uffff\6\44\2\uffff\46\44\2\uffff\6\44\2\uffff"+
            "\10\44\1\uffff\1\44\1\uffff\1\44\1\uffff\1\44\1\uffff\37\44"+
            "\2\uffff\65\44\1\uffff\7\44\1\uffff\1\44\3\uffff\3\44\1\uffff"+
            "\7\44\3\uffff\4\44\2\uffff\6\44\4\uffff\15\44\5\uffff\3\44\1"+
            "\uffff\7\44\164\uffff\1\44\15\uffff\1\44\u0082\uffff\1\44\4"+
            "\uffff\1\44\2\uffff\12\44\1\uffff\1\44\3\uffff\5\44\6\uffff"+
            "\1\44\1\uffff\1\44\1\uffff\1\44\1\uffff\4\44\1\uffff\3\44\1"+
            "\uffff\7\44\3\uffff\3\44\5\uffff\5\44\u0ebb\uffff\2\44\52\uffff"+
            "\5\44\5\uffff\2\44\4\uffff\126\44\6\uffff\3\44\1\uffff\132\44"+
            "\1\uffff\4\44\5\uffff\50\44\4\uffff\136\44\21\uffff\30\44\70"+
            "\uffff\20\44\u0200\uffff\u19b6\44\112\uffff\u51a6\44\132\uffff"+
            "\u048d\44\u0773\uffff\u2ba4\44\u215c\uffff\u012e\44\2\uffff"+
            "\73\44\u0095\uffff\7\44\14\uffff\5\44\5\uffff\1\44\1\uffff\12"+
            "\44\1\uffff\15\44\1\uffff\5\44\1\uffff\1\44\1\uffff\2\44\1\uffff"+
            "\2\44\1\uffff\154\44\41\uffff\u016b\44\22\uffff\100\44\2\uffff"+
            "\66\44\50\uffff\14\44\164\uffff\5\44\1\uffff\u0087\44\23\uffff"+
            "\12\44\7\uffff\32\44\6\uffff\32\44\13\uffff\131\44\3\uffff\6"+
            "\44\2\uffff\6\44\2\uffff\6\44\2\uffff\3\44",
            "\1\64\1\63\5\uffff\1\67\5\uffff\12\61\5\uffff\1\70\1\uffff"+
            "\32\60\1\uffff\1\65\2\uffff\1\62\1\uffff\32\57\57\uffff\1\44"+
            "\12\uffff\1\44\4\uffff\1\44\5\uffff\27\44\1\uffff\37\44\1\uffff"+
            "\u013f\44\31\uffff\162\44\4\uffff\14\44\16\uffff\5\44\11\uffff"+
            "\1\44\u008b\uffff\1\44\13\uffff\1\44\1\uffff\3\44\1\uffff\1"+
            "\44\1\uffff\24\44\1\uffff\54\44\1\uffff\46\44\1\uffff\5\44\4"+
            "\uffff\u0082\44\10\uffff\105\44\1\uffff\46\44\2\uffff\2\44\6"+
            "\uffff\20\44\41\uffff\46\44\2\uffff\1\44\7\uffff\47\44\110\uffff"+
            "\33\44\5\uffff\3\44\56\uffff\32\44\5\uffff\13\44\25\uffff\12"+
            "\44\4\uffff\2\44\1\uffff\143\44\1\uffff\1\44\17\uffff\2\44\7"+
            "\uffff\17\44\2\uffff\1\44\20\uffff\1\44\1\uffff\36\44\35\uffff"+
            "\3\44\60\uffff\46\44\13\uffff\1\44\u0152\uffff\66\44\3\uffff"+
            "\1\44\22\uffff\1\44\7\uffff\12\44\4\uffff\12\44\25\uffff\10"+
            "\44\2\uffff\2\44\2\uffff\26\44\1\uffff\7\44\1\uffff\1\44\3\uffff"+
            "\4\44\3\uffff\1\44\36\uffff\2\44\1\uffff\3\44\4\uffff\14\44"+
            "\23\uffff\6\44\4\uffff\2\44\2\uffff\26\44\1\uffff\7\44\1\uffff"+
            "\2\44\1\uffff\2\44\1\uffff\2\44\37\uffff\4\44\1\uffff\1\44\7"+
            "\uffff\12\44\2\uffff\3\44\20\uffff\11\44\1\uffff\3\44\1\uffff"+
            "\26\44\1\uffff\7\44\1\uffff\2\44\1\uffff\5\44\3\uffff\1\44\22"+
            "\uffff\1\44\17\uffff\2\44\4\uffff\12\44\25\uffff\10\44\2\uffff"+
            "\2\44\2\uffff\26\44\1\uffff\7\44\1\uffff\2\44\1\uffff\5\44\3"+
            "\uffff\1\44\36\uffff\2\44\1\uffff\3\44\4\uffff\12\44\1\uffff"+
            "\1\44\21\uffff\1\44\1\uffff\6\44\3\uffff\3\44\1\uffff\4\44\3"+
            "\uffff\2\44\1\uffff\1\44\1\uffff\2\44\3\uffff\2\44\3\uffff\3"+
            "\44\3\uffff\10\44\1\uffff\3\44\55\uffff\11\44\25\uffff\10\44"+
            "\1\uffff\3\44\1\uffff\27\44\1\uffff\12\44\1\uffff\5\44\46\uffff"+
            "\2\44\4\uffff\12\44\25\uffff\10\44\1\uffff\3\44\1\uffff\27\44"+
            "\1\uffff\12\44\1\uffff\5\44\3\uffff\1\44\40\uffff\1\44\1\uffff"+
            "\2\44\4\uffff\12\44\25\uffff\10\44\1\uffff\3\44\1\uffff\27\44"+
            "\1\uffff\20\44\46\uffff\2\44\4\uffff\12\44\25\uffff\22\44\3"+
            "\uffff\30\44\1\uffff\11\44\1\uffff\1\44\2\uffff\7\44\72\uffff"+
            "\60\44\1\uffff\2\44\14\uffff\7\44\11\uffff\12\44\47\uffff\2"+
            "\44\1\uffff\1\44\2\uffff\2\44\1\uffff\1\44\2\uffff\1\44\6\uffff"+
            "\4\44\1\uffff\7\44\1\uffff\3\44\1\uffff\1\44\1\uffff\1\44\2"+
            "\uffff\2\44\1\uffff\4\44\1\uffff\2\44\11\uffff\1\44\2\uffff"+
            "\5\44\1\uffff\1\44\11\uffff\12\44\2\uffff\2\44\42\uffff\1\44"+
            "\37\uffff\12\44\26\uffff\10\44\1\uffff\42\44\35\uffff\4\44\164"+
            "\uffff\42\44\1\uffff\5\44\1\uffff\2\44\25\uffff\12\44\6\uffff"+
            "\6\44\112\uffff\46\44\12\uffff\51\44\7\uffff\132\44\5\uffff"+
            "\104\44\5\uffff\122\44\6\uffff\7\44\1\uffff\77\44\1\uffff\1"+
            "\44\1\uffff\4\44\2\uffff\7\44\1\uffff\1\44\1\uffff\4\44\2\uffff"+
            "\47\44\1\uffff\1\44\1\uffff\4\44\2\uffff\37\44\1\uffff\1\44"+
            "\1\uffff\4\44\2\uffff\7\44\1\uffff\1\44\1\uffff\4\44\2\uffff"+
            "\7\44\1\uffff\7\44\1\uffff\27\44\1\uffff\37\44\1\uffff\1\44"+
            "\1\uffff\4\44\2\uffff\7\44\1\uffff\47\44\1\uffff\23\44\16\uffff"+
            "\11\44\56\uffff\125\44\14\uffff\u026c\44\2\uffff\10\44\12\uffff"+
            "\32\44\5\uffff\113\44\25\uffff\15\44\1\uffff\4\44\16\uffff\22"+
            "\44\16\uffff\22\44\16\uffff\15\44\1\uffff\3\44\17\uffff\64\44"+
            "\43\uffff\1\44\4\uffff\1\44\3\uffff\12\44\46\uffff\12\44\6\uffff"+
            "\130\44\10\uffff\51\44\127\uffff\35\44\51\uffff\50\44\2\uffff"+
            "\5\44\u038b\uffff\154\44\u0094\uffff\u009c\44\4\uffff\132\44"+
            "\6\uffff\26\44\2\uffff\6\44\2\uffff\46\44\2\uffff\6\44\2\uffff"+
            "\10\44\1\uffff\1\44\1\uffff\1\44\1\uffff\1\44\1\uffff\37\44"+
            "\2\uffff\65\44\1\uffff\7\44\1\uffff\1\44\3\uffff\3\44\1\uffff"+
            "\7\44\3\uffff\4\44\2\uffff\6\44\4\uffff\15\44\5\uffff\3\44\1"+
            "\uffff\7\44\164\uffff\1\44\15\uffff\1\44\u0082\uffff\1\44\4"+
            "\uffff\1\44\2\uffff\12\44\1\uffff\1\44\3\uffff\5\44\6\uffff"+
            "\1\44\1\uffff\1\44\1\uffff\1\44\1\uffff\4\44\1\uffff\3\44\1"+
            "\uffff\7\44\3\uffff\3\44\5\uffff\5\44\u0ebb\uffff\2\44\52\uffff"+
            "\5\44\5\uffff\2\44\4\uffff\126\44\6\uffff\3\44\1\uffff\132\44"+
            "\1\uffff\4\44\5\uffff\50\44\4\uffff\136\44\21\uffff\30\44\70"+
            "\uffff\20\44\u0200\uffff\u19b6\44\112\uffff\u51a6\44\132\uffff"+
            "\u048d\44\u0773\uffff\u2ba4\44\u215c\uffff\u012e\44\2\uffff"+
            "\73\44\u0095\uffff\7\44\14\uffff\5\44\5\uffff\1\44\1\uffff\12"+
            "\44\1\uffff\15\44\1\uffff\5\44\1\uffff\1\44\1\uffff\2\44\1\uffff"+
            "\2\44\1\uffff\154\44\41\uffff\u016b\44\22\uffff\100\44\2\uffff"+
            "\66\44\50\uffff\14\44\164\uffff\5\44\1\uffff\u0087\44\23\uffff"+
            "\12\44\7\uffff\32\44\6\uffff\32\44\13\uffff\131\44\3\uffff\6"+
            "\44\2\uffff\6\44\2\uffff\6\44\2\uffff\3\44",
            "\1\64\1\63\5\uffff\1\67\5\uffff\12\61\5\uffff\1\70\1\uffff"+
            "\32\60\1\uffff\1\65\2\uffff\1\62\1\uffff\32\57\57\uffff\1\44"+
            "\12\uffff\1\44\4\uffff\1\44\5\uffff\27\44\1\uffff\37\44\1\uffff"+
            "\u013f\44\31\uffff\162\44\4\uffff\14\44\16\uffff\5\44\11\uffff"+
            "\1\44\u008b\uffff\1\44\13\uffff\1\44\1\uffff\3\44\1\uffff\1"+
            "\44\1\uffff\24\44\1\uffff\54\44\1\uffff\46\44\1\uffff\5\44\4"+
            "\uffff\u0082\44\10\uffff\105\44\1\uffff\46\44\2\uffff\2\44\6"+
            "\uffff\20\44\41\uffff\46\44\2\uffff\1\44\7\uffff\47\44\110\uffff"+
            "\33\44\5\uffff\3\44\56\uffff\32\44\5\uffff\13\44\25\uffff\12"+
            "\44\4\uffff\2\44\1\uffff\143\44\1\uffff\1\44\17\uffff\2\44\7"+
            "\uffff\17\44\2\uffff\1\44\20\uffff\1\44\1\uffff\36\44\35\uffff"+
            "\3\44\60\uffff\46\44\13\uffff\1\44\u0152\uffff\66\44\3\uffff"+
            "\1\44\22\uffff\1\44\7\uffff\12\44\4\uffff\12\44\25\uffff\10"+
            "\44\2\uffff\2\44\2\uffff\26\44\1\uffff\7\44\1\uffff\1\44\3\uffff"+
            "\4\44\3\uffff\1\44\36\uffff\2\44\1\uffff\3\44\4\uffff\14\44"+
            "\23\uffff\6\44\4\uffff\2\44\2\uffff\26\44\1\uffff\7\44\1\uffff"+
            "\2\44\1\uffff\2\44\1\uffff\2\44\37\uffff\4\44\1\uffff\1\44\7"+
            "\uffff\12\44\2\uffff\3\44\20\uffff\11\44\1\uffff\3\44\1\uffff"+
            "\26\44\1\uffff\7\44\1\uffff\2\44\1\uffff\5\44\3\uffff\1\44\22"+
            "\uffff\1\44\17\uffff\2\44\4\uffff\12\44\25\uffff\10\44\2\uffff"+
            "\2\44\2\uffff\26\44\1\uffff\7\44\1\uffff\2\44\1\uffff\5\44\3"+
            "\uffff\1\44\36\uffff\2\44\1\uffff\3\44\4\uffff\12\44\1\uffff"+
            "\1\44\21\uffff\1\44\1\uffff\6\44\3\uffff\3\44\1\uffff\4\44\3"+
            "\uffff\2\44\1\uffff\1\44\1\uffff\2\44\3\uffff\2\44\3\uffff\3"+
            "\44\3\uffff\10\44\1\uffff\3\44\55\uffff\11\44\25\uffff\10\44"+
            "\1\uffff\3\44\1\uffff\27\44\1\uffff\12\44\1\uffff\5\44\46\uffff"+
            "\2\44\4\uffff\12\44\25\uffff\10\44\1\uffff\3\44\1\uffff\27\44"+
            "\1\uffff\12\44\1\uffff\5\44\3\uffff\1\44\40\uffff\1\44\1\uffff"+
            "\2\44\4\uffff\12\44\25\uffff\10\44\1\uffff\3\44\1\uffff\27\44"+
            "\1\uffff\20\44\46\uffff\2\44\4\uffff\12\44\25\uffff\22\44\3"+
            "\uffff\30\44\1\uffff\11\44\1\uffff\1\44\2\uffff\7\44\72\uffff"+
            "\60\44\1\uffff\2\44\14\uffff\7\44\11\uffff\12\44\47\uffff\2"+
            "\44\1\uffff\1\44\2\uffff\2\44\1\uffff\1\44\2\uffff\1\44\6\uffff"+
            "\4\44\1\uffff\7\44\1\uffff\3\44\1\uffff\1\44\1\uffff\1\44\2"+
            "\uffff\2\44\1\uffff\4\44\1\uffff\2\44\11\uffff\1\44\2\uffff"+
            "\5\44\1\uffff\1\44\11\uffff\12\44\2\uffff\2\44\42\uffff\1\44"+
            "\37\uffff\12\44\26\uffff\10\44\1\uffff\42\44\35\uffff\4\44\164"+
            "\uffff\42\44\1\uffff\5\44\1\uffff\2\44\25\uffff\12\44\6\uffff"+
            "\6\44\112\uffff\46\44\12\uffff\51\44\7\uffff\132\44\5\uffff"+
            "\104\44\5\uffff\122\44\6\uffff\7\44\1\uffff\77\44\1\uffff\1"+
            "\44\1\uffff\4\44\2\uffff\7\44\1\uffff\1\44\1\uffff\4\44\2\uffff"+
            "\47\44\1\uffff\1\44\1\uffff\4\44\2\uffff\37\44\1\uffff\1\44"+
            "\1\uffff\4\44\2\uffff\7\44\1\uffff\1\44\1\uffff\4\44\2\uffff"+
            "\7\44\1\uffff\7\44\1\uffff\27\44\1\uffff\37\44\1\uffff\1\44"+
            "\1\uffff\4\44\2\uffff\7\44\1\uffff\47\44\1\uffff\23\44\16\uffff"+
            "\11\44\56\uffff\125\44\14\uffff\u026c\44\2\uffff\10\44\12\uffff"+
            "\32\44\5\uffff\113\44\25\uffff\15\44\1\uffff\4\44\16\uffff\22"+
            "\44\16\uffff\22\44\16\uffff\15\44\1\uffff\3\44\17\uffff\64\44"+
            "\43\uffff\1\44\4\uffff\1\44\3\uffff\12\44\46\uffff\12\44\6\uffff"+
            "\130\44\10\uffff\51\44\127\uffff\35\44\51\uffff\50\44\2\uffff"+
            "\5\44\u038b\uffff\154\44\u0094\uffff\u009c\44\4\uffff\132\44"+
            "\6\uffff\26\44\2\uffff\6\44\2\uffff\46\44\2\uffff\6\44\2\uffff"+
            "\10\44\1\uffff\1\44\1\uffff\1\44\1\uffff\1\44\1\uffff\37\44"+
            "\2\uffff\65\44\1\uffff\7\44\1\uffff\1\44\3\uffff\3\44\1\uffff"+
            "\7\44\3\uffff\4\44\2\uffff\6\44\4\uffff\15\44\5\uffff\3\44\1"+
            "\uffff\7\44\164\uffff\1\44\15\uffff\1\44\u0082\uffff\1\44\4"+
            "\uffff\1\44\2\uffff\12\44\1\uffff\1\44\3\uffff\5\44\6\uffff"+
            "\1\44\1\uffff\1\44\1\uffff\1\44\1\uffff\4\44\1\uffff\3\44\1"+
            "\uffff\7\44\3\uffff\3\44\5\uffff\5\44\u0ebb\uffff\2\44\52\uffff"+
            "\5\44\5\uffff\2\44\4\uffff\126\44\6\uffff\3\44\1\uffff\132\44"+
            "\1\uffff\4\44\5\uffff\50\44\4\uffff\136\44\21\uffff\30\44\70"+
            "\uffff\20\44\u0200\uffff\u19b6\44\112\uffff\u51a6\44\132\uffff"+
            "\u048d\44\u0773\uffff\u2ba4\44\u215c\uffff\u012e\44\2\uffff"+
            "\73\44\u0095\uffff\7\44\14\uffff\5\44\5\uffff\1\44\1\uffff\12"+
            "\44\1\uffff\15\44\1\uffff\5\44\1\uffff\1\44\1\uffff\2\44\1\uffff"+
            "\2\44\1\uffff\154\44\41\uffff\u016b\44\22\uffff\100\44\2\uffff"+
            "\66\44\50\uffff\14\44\164\uffff\5\44\1\uffff\u0087\44\23\uffff"+
            "\12\44\7\uffff\32\44\6\uffff\32\44\13\uffff\131\44\3\uffff\6"+
            "\44\2\uffff\6\44\2\uffff\6\44\2\uffff\3\44",
            "\1\64\1\63\5\uffff\1\67\5\uffff\12\61\5\uffff\1\70\1\uffff"+
            "\32\60\1\uffff\1\65\2\uffff\1\62\1\uffff\32\57\57\uffff\1\44"+
            "\12\uffff\1\44\4\uffff\1\44\5\uffff\27\44\1\uffff\37\44\1\uffff"+
            "\u013f\44\31\uffff\162\44\4\uffff\14\44\16\uffff\5\44\11\uffff"+
            "\1\44\u008b\uffff\1\44\13\uffff\1\44\1\uffff\3\44\1\uffff\1"+
            "\44\1\uffff\24\44\1\uffff\54\44\1\uffff\46\44\1\uffff\5\44\4"+
            "\uffff\u0082\44\10\uffff\105\44\1\uffff\46\44\2\uffff\2\44\6"+
            "\uffff\20\44\41\uffff\46\44\2\uffff\1\44\7\uffff\47\44\110\uffff"+
            "\33\44\5\uffff\3\44\56\uffff\32\44\5\uffff\13\44\25\uffff\12"+
            "\44\4\uffff\2\44\1\uffff\143\44\1\uffff\1\44\17\uffff\2\44\7"+
            "\uffff\17\44\2\uffff\1\44\20\uffff\1\44\1\uffff\36\44\35\uffff"+
            "\3\44\60\uffff\46\44\13\uffff\1\44\u0152\uffff\66\44\3\uffff"+
            "\1\44\22\uffff\1\44\7\uffff\12\44\4\uffff\12\44\25\uffff\10"+
            "\44\2\uffff\2\44\2\uffff\26\44\1\uffff\7\44\1\uffff\1\44\3\uffff"+
            "\4\44\3\uffff\1\44\36\uffff\2\44\1\uffff\3\44\4\uffff\14\44"+
            "\23\uffff\6\44\4\uffff\2\44\2\uffff\26\44\1\uffff\7\44\1\uffff"+
            "\2\44\1\uffff\2\44\1\uffff\2\44\37\uffff\4\44\1\uffff\1\44\7"+
            "\uffff\12\44\2\uffff\3\44\20\uffff\11\44\1\uffff\3\44\1\uffff"+
            "\26\44\1\uffff\7\44\1\uffff\2\44\1\uffff\5\44\3\uffff\1\44\22"+
            "\uffff\1\44\17\uffff\2\44\4\uffff\12\44\25\uffff\10\44\2\uffff"+
            "\2\44\2\uffff\26\44\1\uffff\7\44\1\uffff\2\44\1\uffff\5\44\3"+
            "\uffff\1\44\36\uffff\2\44\1\uffff\3\44\4\uffff\12\44\1\uffff"+
            "\1\44\21\uffff\1\44\1\uffff\6\44\3\uffff\3\44\1\uffff\4\44\3"+
            "\uffff\2\44\1\uffff\1\44\1\uffff\2\44\3\uffff\2\44\3\uffff\3"+
            "\44\3\uffff\10\44\1\uffff\3\44\55\uffff\11\44\25\uffff\10\44"+
            "\1\uffff\3\44\1\uffff\27\44\1\uffff\12\44\1\uffff\5\44\46\uffff"+
            "\2\44\4\uffff\12\44\25\uffff\10\44\1\uffff\3\44\1\uffff\27\44"+
            "\1\uffff\12\44\1\uffff\5\44\3\uffff\1\44\40\uffff\1\44\1\uffff"+
            "\2\44\4\uffff\12\44\25\uffff\10\44\1\uffff\3\44\1\uffff\27\44"+
            "\1\uffff\20\44\46\uffff\2\44\4\uffff\12\44\25\uffff\22\44\3"+
            "\uffff\30\44\1\uffff\11\44\1\uffff\1\44\2\uffff\7\44\72\uffff"+
            "\60\44\1\uffff\2\44\14\uffff\7\44\11\uffff\12\44\47\uffff\2"+
            "\44\1\uffff\1\44\2\uffff\2\44\1\uffff\1\44\2\uffff\1\44\6\uffff"+
            "\4\44\1\uffff\7\44\1\uffff\3\44\1\uffff\1\44\1\uffff\1\44\2"+
            "\uffff\2\44\1\uffff\4\44\1\uffff\2\44\11\uffff\1\44\2\uffff"+
            "\5\44\1\uffff\1\44\11\uffff\12\44\2\uffff\2\44\42\uffff\1\44"+
            "\37\uffff\12\44\26\uffff\10\44\1\uffff\42\44\35\uffff\4\44\164"+
            "\uffff\42\44\1\uffff\5\44\1\uffff\2\44\25\uffff\12\44\6\uffff"+
            "\6\44\112\uffff\46\44\12\uffff\51\44\7\uffff\132\44\5\uffff"+
            "\104\44\5\uffff\122\44\6\uffff\7\44\1\uffff\77\44\1\uffff\1"+
            "\44\1\uffff\4\44\2\uffff\7\44\1\uffff\1\44\1\uffff\4\44\2\uffff"+
            "\47\44\1\uffff\1\44\1\uffff\4\44\2\uffff\37\44\1\uffff\1\44"+
            "\1\uffff\4\44\2\uffff\7\44\1\uffff\1\44\1\uffff\4\44\2\uffff"+
            "\7\44\1\uffff\7\44\1\uffff\27\44\1\uffff\37\44\1\uffff\1\44"+
            "\1\uffff\4\44\2\uffff\7\44\1\uffff\47\44\1\uffff\23\44\16\uffff"+
            "\11\44\56\uffff\125\44\14\uffff\u026c\44\2\uffff\10\44\12\uffff"+
            "\32\44\5\uffff\113\44\25\uffff\15\44\1\uffff\4\44\16\uffff\22"+
            "\44\16\uffff\22\44\16\uffff\15\44\1\uffff\3\44\17\uffff\64\44"+
            "\43\uffff\1\44\4\uffff\1\44\3\uffff\12\44\46\uffff\12\44\6\uffff"+
            "\130\44\10\uffff\51\44\127\uffff\35\44\51\uffff\50\44\2\uffff"+
            "\5\44\u038b\uffff\154\44\u0094\uffff\u009c\44\4\uffff\132\44"+
            "\6\uffff\26\44\2\uffff\6\44\2\uffff\46\44\2\uffff\6\44\2\uffff"+
            "\10\44\1\uffff\1\44\1\uffff\1\44\1\uffff\1\44\1\uffff\37\44"+
            "\2\uffff\65\44\1\uffff\7\44\1\uffff\1\44\3\uffff\3\44\1\uffff"+
            "\7\44\3\uffff\4\44\2\uffff\6\44\4\uffff\15\44\5\uffff\3\44\1"+
            "\uffff\7\44\164\uffff\1\44\15\uffff\1\44\u0082\uffff\1\44\4"+
            "\uffff\1\44\2\uffff\12\44\1\uffff\1\44\3\uffff\5\44\6\uffff"+
            "\1\44\1\uffff\1\44\1\uffff\1\44\1\uffff\4\44\1\uffff\3\44\1"+
            "\uffff\7\44\3\uffff\3\44\5\uffff\5\44\u0ebb\uffff\2\44\52\uffff"+
            "\5\44\5\uffff\2\44\4\uffff\126\44\6\uffff\3\44\1\uffff\132\44"+
            "\1\uffff\4\44\5\uffff\50\44\4\uffff\136\44\21\uffff\30\44\70"+
            "\uffff\20\44\u0200\uffff\u19b6\44\112\uffff\u51a6\44\132\uffff"+
            "\u048d\44\u0773\uffff\u2ba4\44\u215c\uffff\u012e\44\2\uffff"+
            "\73\44\u0095\uffff\7\44\14\uffff\5\44\5\uffff\1\44\1\uffff\12"+
            "\44\1\uffff\15\44\1\uffff\5\44\1\uffff\1\44\1\uffff\2\44\1\uffff"+
            "\2\44\1\uffff\154\44\41\uffff\u016b\44\22\uffff\100\44\2\uffff"+
            "\66\44\50\uffff\14\44\164\uffff\5\44\1\uffff\u0087\44\23\uffff"+
            "\12\44\7\uffff\32\44\6\uffff\32\44\13\uffff\131\44\3\uffff\6"+
            "\44\2\uffff\6\44\2\uffff\6\44\2\uffff\3\44",
            "\1\64\1\63\5\uffff\1\67\5\uffff\12\61\5\uffff\1\70\1\uffff"+
            "\32\60\1\uffff\1\65\2\uffff\1\62\1\uffff\32\57\57\uffff\1\44"+
            "\12\uffff\1\44\4\uffff\1\44\5\uffff\27\44\1\uffff\37\44\1\uffff"+
            "\u013f\44\31\uffff\162\44\4\uffff\14\44\16\uffff\5\44\11\uffff"+
            "\1\44\u008b\uffff\1\44\13\uffff\1\44\1\uffff\3\44\1\uffff\1"+
            "\44\1\uffff\24\44\1\uffff\54\44\1\uffff\46\44\1\uffff\5\44\4"+
            "\uffff\u0082\44\10\uffff\105\44\1\uffff\46\44\2\uffff\2\44\6"+
            "\uffff\20\44\41\uffff\46\44\2\uffff\1\44\7\uffff\47\44\110\uffff"+
            "\33\44\5\uffff\3\44\56\uffff\32\44\5\uffff\13\44\25\uffff\12"+
            "\44\4\uffff\2\44\1\uffff\143\44\1\uffff\1\44\17\uffff\2\44\7"+
            "\uffff\17\44\2\uffff\1\44\20\uffff\1\44\1\uffff\36\44\35\uffff"+
            "\3\44\60\uffff\46\44\13\uffff\1\44\u0152\uffff\66\44\3\uffff"+
            "\1\44\22\uffff\1\44\7\uffff\12\44\4\uffff\12\44\25\uffff\10"+
            "\44\2\uffff\2\44\2\uffff\26\44\1\uffff\7\44\1\uffff\1\44\3\uffff"+
            "\4\44\3\uffff\1\44\36\uffff\2\44\1\uffff\3\44\4\uffff\14\44"+
            "\23\uffff\6\44\4\uffff\2\44\2\uffff\26\44\1\uffff\7\44\1\uffff"+
            "\2\44\1\uffff\2\44\1\uffff\2\44\37\uffff\4\44\1\uffff\1\44\7"+
            "\uffff\12\44\2\uffff\3\44\20\uffff\11\44\1\uffff\3\44\1\uffff"+
            "\26\44\1\uffff\7\44\1\uffff\2\44\1\uffff\5\44\3\uffff\1\44\22"+
            "\uffff\1\44\17\uffff\2\44\4\uffff\12\44\25\uffff\10\44\2\uffff"+
            "\2\44\2\uffff\26\44\1\uffff\7\44\1\uffff\2\44\1\uffff\5\44\3"+
            "\uffff\1\44\36\uffff\2\44\1\uffff\3\44\4\uffff\12\44\1\uffff"+
            "\1\44\21\uffff\1\44\1\uffff\6\44\3\uffff\3\44\1\uffff\4\44\3"+
            "\uffff\2\44\1\uffff\1\44\1\uffff\2\44\3\uffff\2\44\3\uffff\3"+
            "\44\3\uffff\10\44\1\uffff\3\44\55\uffff\11\44\25\uffff\10\44"+
            "\1\uffff\3\44\1\uffff\27\44\1\uffff\12\44\1\uffff\5\44\46\uffff"+
            "\2\44\4\uffff\12\44\25\uffff\10\44\1\uffff\3\44\1\uffff\27\44"+
            "\1\uffff\12\44\1\uffff\5\44\3\uffff\1\44\40\uffff\1\44\1\uffff"+
            "\2\44\4\uffff\12\44\25\uffff\10\44\1\uffff\3\44\1\uffff\27\44"+
            "\1\uffff\20\44\46\uffff\2\44\4\uffff\12\44\25\uffff\22\44\3"+
            "\uffff\30\44\1\uffff\11\44\1\uffff\1\44\2\uffff\7\44\72\uffff"+
            "\60\44\1\uffff\2\44\14\uffff\7\44\11\uffff\12\44\47\uffff\2"+
            "\44\1\uffff\1\44\2\uffff\2\44\1\uffff\1\44\2\uffff\1\44\6\uffff"+
            "\4\44\1\uffff\7\44\1\uffff\3\44\1\uffff\1\44\1\uffff\1\44\2"+
            "\uffff\2\44\1\uffff\4\44\1\uffff\2\44\11\uffff\1\44\2\uffff"+
            "\5\44\1\uffff\1\44\11\uffff\12\44\2\uffff\2\44\42\uffff\1\44"+
            "\37\uffff\12\44\26\uffff\10\44\1\uffff\42\44\35\uffff\4\44\164"+
            "\uffff\42\44\1\uffff\5\44\1\uffff\2\44\25\uffff\12\44\6\uffff"+
            "\6\44\112\uffff\46\44\12\uffff\51\44\7\uffff\132\44\5\uffff"+
            "\104\44\5\uffff\122\44\6\uffff\7\44\1\uffff\77\44\1\uffff\1"+
            "\44\1\uffff\4\44\2\uffff\7\44\1\uffff\1\44\1\uffff\4\44\2\uffff"+
            "\47\44\1\uffff\1\44\1\uffff\4\44\2\uffff\37\44\1\uffff\1\44"+
            "\1\uffff\4\44\2\uffff\7\44\1\uffff\1\44\1\uffff\4\44\2\uffff"+
            "\7\44\1\uffff\7\44\1\uffff\27\44\1\uffff\37\44\1\uffff\1\44"+
            "\1\uffff\4\44\2\uffff\7\44\1\uffff\47\44\1\uffff\23\44\16\uffff"+
            "\11\44\56\uffff\125\44\14\uffff\u026c\44\2\uffff\10\44\12\uffff"+
            "\32\44\5\uffff\113\44\25\uffff\15\44\1\uffff\4\44\16\uffff\22"+
            "\44\16\uffff\22\44\16\uffff\15\44\1\uffff\3\44\17\uffff\64\44"+
            "\43\uffff\1\44\4\uffff\1\44\3\uffff\12\44\46\uffff\12\44\6\uffff"+
            "\130\44\10\uffff\51\44\127\uffff\35\44\51\uffff\50\44\2\uffff"+
            "\5\44\u038b\uffff\154\44\u0094\uffff\u009c\44\4\uffff\132\44"+
            "\6\uffff\26\44\2\uffff\6\44\2\uffff\46\44\2\uffff\6\44\2\uffff"+
            "\10\44\1\uffff\1\44\1\uffff\1\44\1\uffff\1\44\1\uffff\37\44"+
            "\2\uffff\65\44\1\uffff\7\44\1\uffff\1\44\3\uffff\3\44\1\uffff"+
            "\7\44\3\uffff\4\44\2\uffff\6\44\4\uffff\15\44\5\uffff\3\44\1"+
            "\uffff\7\44\164\uffff\1\44\15\uffff\1\44\u0082\uffff\1\44\4"+
            "\uffff\1\44\2\uffff\12\44\1\uffff\1\44\3\uffff\5\44\6\uffff"+
            "\1\44\1\uffff\1\44\1\uffff\1\44\1\uffff\4\44\1\uffff\3\44\1"+
            "\uffff\7\44\3\uffff\3\44\5\uffff\5\44\u0ebb\uffff\2\44\52\uffff"+
            "\5\44\5\uffff\2\44\4\uffff\126\44\6\uffff\3\44\1\uffff\132\44"+
            "\1\uffff\4\44\5\uffff\50\44\4\uffff\136\44\21\uffff\30\44\70"+
            "\uffff\20\44\u0200\uffff\u19b6\44\112\uffff\u51a6\44\132\uffff"+
            "\u048d\44\u0773\uffff\u2ba4\44\u215c\uffff\u012e\44\2\uffff"+
            "\73\44\u0095\uffff\7\44\14\uffff\5\44\5\uffff\1\44\1\uffff\12"+
            "\44\1\uffff\15\44\1\uffff\5\44\1\uffff\1\44\1\uffff\2\44\1\uffff"+
            "\2\44\1\uffff\154\44\41\uffff\u016b\44\22\uffff\100\44\2\uffff"+
            "\66\44\50\uffff\14\44\164\uffff\5\44\1\uffff\u0087\44\23\uffff"+
            "\12\44\7\uffff\32\44\6\uffff\32\44\13\uffff\131\44\3\uffff\6"+
            "\44\2\uffff\6\44\2\uffff\6\44\2\uffff\3\44",
            "",
            "",
            "",
            "\165\126\1\125\uff8a\126",
            "",
            "\1\105\5\uffff\12\104\5\uffff\1\70\1\uffff\32\104\1\uffff"+
            "\1\103\4\uffff\32\104\57\uffff\1\104\12\uffff\1\104\4\uffff"+
            "\1\104\5\uffff\27\104\1\uffff\37\104\1\uffff\u013f\104\31\uffff"+
            "\162\104\4\uffff\14\104\16\uffff\5\104\11\uffff\1\104\u008b"+
            "\uffff\1\104\13\uffff\1\104\1\uffff\3\104\1\uffff\1\104\1\uffff"+
            "\24\104\1\uffff\54\104\1\uffff\46\104\1\uffff\5\104\4\uffff"+
            "\u0082\104\10\uffff\105\104\1\uffff\46\104\2\uffff\2\104\6\uffff"+
            "\20\104\41\uffff\46\104\2\uffff\1\104\7\uffff\47\104\110\uffff"+
            "\33\104\5\uffff\3\104\56\uffff\32\104\5\uffff\13\104\25\uffff"+
            "\12\104\4\uffff\2\104\1\uffff\143\104\1\uffff\1\104\17\uffff"+
            "\2\104\7\uffff\17\104\2\uffff\1\104\20\uffff\1\104\1\uffff\36"+
            "\104\35\uffff\3\104\60\uffff\46\104\13\uffff\1\104\u0152\uffff"+
            "\66\104\3\uffff\1\104\22\uffff\1\104\7\uffff\12\104\4\uffff"+
            "\12\104\25\uffff\10\104\2\uffff\2\104\2\uffff\26\104\1\uffff"+
            "\7\104\1\uffff\1\104\3\uffff\4\104\3\uffff\1\104\36\uffff\2"+
            "\104\1\uffff\3\104\4\uffff\14\104\23\uffff\6\104\4\uffff\2\104"+
            "\2\uffff\26\104\1\uffff\7\104\1\uffff\2\104\1\uffff\2\104\1"+
            "\uffff\2\104\37\uffff\4\104\1\uffff\1\104\7\uffff\12\104\2\uffff"+
            "\3\104\20\uffff\11\104\1\uffff\3\104\1\uffff\26\104\1\uffff"+
            "\7\104\1\uffff\2\104\1\uffff\5\104\3\uffff\1\104\22\uffff\1"+
            "\104\17\uffff\2\104\4\uffff\12\104\25\uffff\10\104\2\uffff\2"+
            "\104\2\uffff\26\104\1\uffff\7\104\1\uffff\2\104\1\uffff\5\104"+
            "\3\uffff\1\104\36\uffff\2\104\1\uffff\3\104\4\uffff\12\104\1"+
            "\uffff\1\104\21\uffff\1\104\1\uffff\6\104\3\uffff\3\104\1\uffff"+
            "\4\104\3\uffff\2\104\1\uffff\1\104\1\uffff\2\104\3\uffff\2\104"+
            "\3\uffff\3\104\3\uffff\10\104\1\uffff\3\104\55\uffff\11\104"+
            "\25\uffff\10\104\1\uffff\3\104\1\uffff\27\104\1\uffff\12\104"+
            "\1\uffff\5\104\46\uffff\2\104\4\uffff\12\104\25\uffff\10\104"+
            "\1\uffff\3\104\1\uffff\27\104\1\uffff\12\104\1\uffff\5\104\3"+
            "\uffff\1\104\40\uffff\1\104\1\uffff\2\104\4\uffff\12\104\25"+
            "\uffff\10\104\1\uffff\3\104\1\uffff\27\104\1\uffff\20\104\46"+
            "\uffff\2\104\4\uffff\12\104\25\uffff\22\104\3\uffff\30\104\1"+
            "\uffff\11\104\1\uffff\1\104\2\uffff\7\104\72\uffff\60\104\1"+
            "\uffff\2\104\14\uffff\7\104\11\uffff\12\104\47\uffff\2\104\1"+
            "\uffff\1\104\2\uffff\2\104\1\uffff\1\104\2\uffff\1\104\6\uffff"+
            "\4\104\1\uffff\7\104\1\uffff\3\104\1\uffff\1\104\1\uffff\1\104"+
            "\2\uffff\2\104\1\uffff\4\104\1\uffff\2\104\11\uffff\1\104\2"+
            "\uffff\5\104\1\uffff\1\104\11\uffff\12\104\2\uffff\2\104\42"+
            "\uffff\1\104\37\uffff\12\104\26\uffff\10\104\1\uffff\42\104"+
            "\35\uffff\4\104\164\uffff\42\104\1\uffff\5\104\1\uffff\2\104"+
            "\25\uffff\12\104\6\uffff\6\104\112\uffff\46\104\12\uffff\51"+
            "\104\7\uffff\132\104\5\uffff\104\104\5\uffff\122\104\6\uffff"+
            "\7\104\1\uffff\77\104\1\uffff\1\104\1\uffff\4\104\2\uffff\7"+
            "\104\1\uffff\1\104\1\uffff\4\104\2\uffff\47\104\1\uffff\1\104"+
            "\1\uffff\4\104\2\uffff\37\104\1\uffff\1\104\1\uffff\4\104\2"+
            "\uffff\7\104\1\uffff\1\104\1\uffff\4\104\2\uffff\7\104\1\uffff"+
            "\7\104\1\uffff\27\104\1\uffff\37\104\1\uffff\1\104\1\uffff\4"+
            "\104\2\uffff\7\104\1\uffff\47\104\1\uffff\23\104\16\uffff\11"+
            "\104\56\uffff\125\104\14\uffff\u026c\104\2\uffff\10\104\12\uffff"+
            "\32\104\5\uffff\113\104\25\uffff\15\104\1\uffff\4\104\16\uffff"+
            "\22\104\16\uffff\22\104\16\uffff\15\104\1\uffff\3\104\17\uffff"+
            "\64\104\43\uffff\1\104\4\uffff\1\104\3\uffff\12\104\46\uffff"+
            "\12\104\6\uffff\130\104\10\uffff\51\104\127\uffff\35\104\51"+
            "\uffff\50\104\2\uffff\5\104\u038b\uffff\154\104\u0094\uffff"+
            "\u009c\104\4\uffff\132\104\6\uffff\26\104\2\uffff\6\104\2\uffff"+
            "\46\104\2\uffff\6\104\2\uffff\10\104\1\uffff\1\104\1\uffff\1"+
            "\104\1\uffff\1\104\1\uffff\37\104\2\uffff\65\104\1\uffff\7\104"+
            "\1\uffff\1\104\3\uffff\3\104\1\uffff\7\104\3\uffff\4\104\2\uffff"+
            "\6\104\4\uffff\15\104\5\uffff\3\104\1\uffff\7\104\164\uffff"+
            "\1\104\15\uffff\1\104\u0082\uffff\1\104\4\uffff\1\104\2\uffff"+
            "\12\104\1\uffff\1\104\3\uffff\5\104\6\uffff\1\104\1\uffff\1"+
            "\104\1\uffff\1\104\1\uffff\4\104\1\uffff\3\104\1\uffff\7\104"+
            "\3\uffff\3\104\5\uffff\5\104\u0ebb\uffff\2\104\52\uffff\5\104"+
            "\5\uffff\2\104\4\uffff\126\104\6\uffff\3\104\1\uffff\132\104"+
            "\1\uffff\4\104\5\uffff\50\104\4\uffff\136\104\21\uffff\30\104"+
            "\70\uffff\20\104\u0200\uffff\u19b6\104\112\uffff\u51a6\104\132"+
            "\uffff\u048d\104\u0773\uffff\u2ba4\104\u215c\uffff\u012e\104"+
            "\2\uffff\73\104\u0095\uffff\7\104\14\uffff\5\104\5\uffff\1\104"+
            "\1\uffff\12\104\1\uffff\15\104\1\uffff\5\104\1\uffff\1\104\1"+
            "\uffff\2\104\1\uffff\2\104\1\uffff\154\104\41\uffff\u016b\104"+
            "\22\uffff\100\104\2\uffff\66\104\50\uffff\14\104\164\uffff\5"+
            "\104\1\uffff\u0087\104\23\uffff\12\104\7\uffff\32\104\6\uffff"+
            "\32\104\13\uffff\131\104\3\uffff\6\104\2\uffff\6\104\2\uffff"+
            "\6\104\2\uffff\3\104",
            "",
            "\1\64\1\63\5\uffff\1\67\5\uffff\12\61\5\uffff\1\70\1\uffff"+
            "\3\60\1\131\26\60\1\uffff\1\65\2\uffff\1\62\1\uffff\3\57\1\130"+
            "\26\57\57\uffff\1\44\12\uffff\1\44\4\uffff\1\44\5\uffff\27\44"+
            "\1\uffff\37\44\1\uffff\u013f\44\31\uffff\162\44\4\uffff\14\44"+
            "\16\uffff\5\44\11\uffff\1\44\u008b\uffff\1\44\13\uffff\1\44"+
            "\1\uffff\3\44\1\uffff\1\44\1\uffff\24\44\1\uffff\54\44\1\uffff"+
            "\46\44\1\uffff\5\44\4\uffff\u0082\44\10\uffff\105\44\1\uffff"+
            "\46\44\2\uffff\2\44\6\uffff\20\44\41\uffff\46\44\2\uffff\1\44"+
            "\7\uffff\47\44\110\uffff\33\44\5\uffff\3\44\56\uffff\32\44\5"+
            "\uffff\13\44\25\uffff\12\44\4\uffff\2\44\1\uffff\143\44\1\uffff"+
            "\1\44\17\uffff\2\44\7\uffff\17\44\2\uffff\1\44\20\uffff\1\44"+
            "\1\uffff\36\44\35\uffff\3\44\60\uffff\46\44\13\uffff\1\44\u0152"+
            "\uffff\66\44\3\uffff\1\44\22\uffff\1\44\7\uffff\12\44\4\uffff"+
            "\12\44\25\uffff\10\44\2\uffff\2\44\2\uffff\26\44\1\uffff\7\44"+
            "\1\uffff\1\44\3\uffff\4\44\3\uffff\1\44\36\uffff\2\44\1\uffff"+
            "\3\44\4\uffff\14\44\23\uffff\6\44\4\uffff\2\44\2\uffff\26\44"+
            "\1\uffff\7\44\1\uffff\2\44\1\uffff\2\44\1\uffff\2\44\37\uffff"+
            "\4\44\1\uffff\1\44\7\uffff\12\44\2\uffff\3\44\20\uffff\11\44"+
            "\1\uffff\3\44\1\uffff\26\44\1\uffff\7\44\1\uffff\2\44\1\uffff"+
            "\5\44\3\uffff\1\44\22\uffff\1\44\17\uffff\2\44\4\uffff\12\44"+
            "\25\uffff\10\44\2\uffff\2\44\2\uffff\26\44\1\uffff\7\44\1\uffff"+
            "\2\44\1\uffff\5\44\3\uffff\1\44\36\uffff\2\44\1\uffff\3\44\4"+
            "\uffff\12\44\1\uffff\1\44\21\uffff\1\44\1\uffff\6\44\3\uffff"+
            "\3\44\1\uffff\4\44\3\uffff\2\44\1\uffff\1\44\1\uffff\2\44\3"+
            "\uffff\2\44\3\uffff\3\44\3\uffff\10\44\1\uffff\3\44\55\uffff"+
            "\11\44\25\uffff\10\44\1\uffff\3\44\1\uffff\27\44\1\uffff\12"+
            "\44\1\uffff\5\44\46\uffff\2\44\4\uffff\12\44\25\uffff\10\44"+
            "\1\uffff\3\44\1\uffff\27\44\1\uffff\12\44\1\uffff\5\44\3\uffff"+
            "\1\44\40\uffff\1\44\1\uffff\2\44\4\uffff\12\44\25\uffff\10\44"+
            "\1\uffff\3\44\1\uffff\27\44\1\uffff\20\44\46\uffff\2\44\4\uffff"+
            "\12\44\25\uffff\22\44\3\uffff\30\44\1\uffff\11\44\1\uffff\1"+
            "\44\2\uffff\7\44\72\uffff\60\44\1\uffff\2\44\14\uffff\7\44\11"+
            "\uffff\12\44\47\uffff\2\44\1\uffff\1\44\2\uffff\2\44\1\uffff"+
            "\1\44\2\uffff\1\44\6\uffff\4\44\1\uffff\7\44\1\uffff\3\44\1"+
            "\uffff\1\44\1\uffff\1\44\2\uffff\2\44\1\uffff\4\44\1\uffff\2"+
            "\44\11\uffff\1\44\2\uffff\5\44\1\uffff\1\44\11\uffff\12\44\2"+
            "\uffff\2\44\42\uffff\1\44\37\uffff\12\44\26\uffff\10\44\1\uffff"+
            "\42\44\35\uffff\4\44\164\uffff\42\44\1\uffff\5\44\1\uffff\2"+
            "\44\25\uffff\12\44\6\uffff\6\44\112\uffff\46\44\12\uffff\51"+
            "\44\7\uffff\132\44\5\uffff\104\44\5\uffff\122\44\6\uffff\7\44"+
            "\1\uffff\77\44\1\uffff\1\44\1\uffff\4\44\2\uffff\7\44\1\uffff"+
            "\1\44\1\uffff\4\44\2\uffff\47\44\1\uffff\1\44\1\uffff\4\44\2"+
            "\uffff\37\44\1\uffff\1\44\1\uffff\4\44\2\uffff\7\44\1\uffff"+
            "\1\44\1\uffff\4\44\2\uffff\7\44\1\uffff\7\44\1\uffff\27\44\1"+
            "\uffff\37\44\1\uffff\1\44\1\uffff\4\44\2\uffff\7\44\1\uffff"+
            "\47\44\1\uffff\23\44\16\uffff\11\44\56\uffff\125\44\14\uffff"+
            "\u026c\44\2\uffff\10\44\12\uffff\32\44\5\uffff\113\44\25\uffff"+
            "\15\44\1\uffff\4\44\16\uffff\22\44\16\uffff\22\44\16\uffff\15"+
            "\44\1\uffff\3\44\17\uffff\64\44\43\uffff\1\44\4\uffff\1\44\3"+
            "\uffff\12\44\46\uffff\12\44\6\uffff\130\44\10\uffff\51\44\127"+
            "\uffff\35\44\51\uffff\50\44\2\uffff\5\44\u038b\uffff\154\44"+
            "\u0094\uffff\u009c\44\4\uffff\132\44\6\uffff\26\44\2\uffff\6"+
            "\44\2\uffff\46\44\2\uffff\6\44\2\uffff\10\44\1\uffff\1\44\1"+
            "\uffff\1\44\1\uffff\1\44\1\uffff\37\44\2\uffff\65\44\1\uffff"+
            "\7\44\1\uffff\1\44\3\uffff\3\44\1\uffff\7\44\3\uffff\4\44\2"+
            "\uffff\6\44\4\uffff\15\44\5\uffff\3\44\1\uffff\7\44\164\uffff"+
            "\1\44\15\uffff\1\44\u0082\uffff\1\44\4\uffff\1\44\2\uffff\12"+
            "\44\1\uffff\1\44\3\uffff\5\44\6\uffff\1\44\1\uffff\1\44\1\uffff"+
            "\1\44\1\uffff\4\44\1\uffff\3\44\1\uffff\7\44\3\uffff\3\44\5"+
            "\uffff\5\44\u0ebb\uffff\2\44\52\uffff\5\44\5\uffff\2\44\4\uffff"+
            "\126\44\6\uffff\3\44\1\uffff\132\44\1\uffff\4\44\5\uffff\50"+
            "\44\4\uffff\136\44\21\uffff\30\44\70\uffff\20\44\u0200\uffff"+
            "\u19b6\44\112\uffff\u51a6\44\132\uffff\u048d\44\u0773\uffff"+
            "\u2ba4\44\u215c\uffff\u012e\44\2\uffff\73\44\u0095\uffff\7\44"+
            "\14\uffff\5\44\5\uffff\1\44\1\uffff\12\44\1\uffff\15\44\1\uffff"+
            "\5\44\1\uffff\1\44\1\uffff\2\44\1\uffff\2\44\1\uffff\154\44"+
            "\41\uffff\u016b\44\22\uffff\100\44\2\uffff\66\44\50\uffff\14"+
            "\44\164\uffff\5\44\1\uffff\u0087\44\23\uffff\12\44\7\uffff\32"+
            "\44\6\uffff\32\44\13\uffff\131\44\3\uffff\6\44\2\uffff\6\44"+
            "\2\uffff\6\44\2\uffff\3\44",
            "\1\64\1\63\5\uffff\1\67\5\uffff\12\61\5\uffff\1\70\1\uffff"+
            "\3\60\1\131\26\60\1\uffff\1\65\2\uffff\1\62\1\uffff\3\57\1\130"+
            "\26\57\57\uffff\1\44\12\uffff\1\44\4\uffff\1\44\5\uffff\27\44"+
            "\1\uffff\37\44\1\uffff\u013f\44\31\uffff\162\44\4\uffff\14\44"+
            "\16\uffff\5\44\11\uffff\1\44\u008b\uffff\1\44\13\uffff\1\44"+
            "\1\uffff\3\44\1\uffff\1\44\1\uffff\24\44\1\uffff\54\44\1\uffff"+
            "\46\44\1\uffff\5\44\4\uffff\u0082\44\10\uffff\105\44\1\uffff"+
            "\46\44\2\uffff\2\44\6\uffff\20\44\41\uffff\46\44\2\uffff\1\44"+
            "\7\uffff\47\44\110\uffff\33\44\5\uffff\3\44\56\uffff\32\44\5"+
            "\uffff\13\44\25\uffff\12\44\4\uffff\2\44\1\uffff\143\44\1\uffff"+
            "\1\44\17\uffff\2\44\7\uffff\17\44\2\uffff\1\44\20\uffff\1\44"+
            "\1\uffff\36\44\35\uffff\3\44\60\uffff\46\44\13\uffff\1\44\u0152"+
            "\uffff\66\44\3\uffff\1\44\22\uffff\1\44\7\uffff\12\44\4\uffff"+
            "\12\44\25\uffff\10\44\2\uffff\2\44\2\uffff\26\44\1\uffff\7\44"+
            "\1\uffff\1\44\3\uffff\4\44\3\uffff\1\44\36\uffff\2\44\1\uffff"+
            "\3\44\4\uffff\14\44\23\uffff\6\44\4\uffff\2\44\2\uffff\26\44"+
            "\1\uffff\7\44\1\uffff\2\44\1\uffff\2\44\1\uffff\2\44\37\uffff"+
            "\4\44\1\uffff\1\44\7\uffff\12\44\2\uffff\3\44\20\uffff\11\44"+
            "\1\uffff\3\44\1\uffff\26\44\1\uffff\7\44\1\uffff\2\44\1\uffff"+
            "\5\44\3\uffff\1\44\22\uffff\1\44\17\uffff\2\44\4\uffff\12\44"+
            "\25\uffff\10\44\2\uffff\2\44\2\uffff\26\44\1\uffff\7\44\1\uffff"+
            "\2\44\1\uffff\5\44\3\uffff\1\44\36\uffff\2\44\1\uffff\3\44\4"+
            "\uffff\12\44\1\uffff\1\44\21\uffff\1\44\1\uffff\6\44\3\uffff"+
            "\3\44\1\uffff\4\44\3\uffff\2\44\1\uffff\1\44\1\uffff\2\44\3"+
            "\uffff\2\44\3\uffff\3\44\3\uffff\10\44\1\uffff\3\44\55\uffff"+
            "\11\44\25\uffff\10\44\1\uffff\3\44\1\uffff\27\44\1\uffff\12"+
            "\44\1\uffff\5\44\46\uffff\2\44\4\uffff\12\44\25\uffff\10\44"+
            "\1\uffff\3\44\1\uffff\27\44\1\uffff\12\44\1\uffff\5\44\3\uffff"+
            "\1\44\40\uffff\1\44\1\uffff\2\44\4\uffff\12\44\25\uffff\10\44"+
            "\1\uffff\3\44\1\uffff\27\44\1\uffff\20\44\46\uffff\2\44\4\uffff"+
            "\12\44\25\uffff\22\44\3\uffff\30\44\1\uffff\11\44\1\uffff\1"+
            "\44\2\uffff\7\44\72\uffff\60\44\1\uffff\2\44\14\uffff\7\44\11"+
            "\uffff\12\44\47\uffff\2\44\1\uffff\1\44\2\uffff\2\44\1\uffff"+
            "\1\44\2\uffff\1\44\6\uffff\4\44\1\uffff\7\44\1\uffff\3\44\1"+
            "\uffff\1\44\1\uffff\1\44\2\uffff\2\44\1\uffff\4\44\1\uffff\2"+
            "\44\11\uffff\1\44\2\uffff\5\44\1\uffff\1\44\11\uffff\12\44\2"+
            "\uffff\2\44\42\uffff\1\44\37\uffff\12\44\26\uffff\10\44\1\uffff"+
            "\42\44\35\uffff\4\44\164\uffff\42\44\1\uffff\5\44\1\uffff\2"+
            "\44\25\uffff\12\44\6\uffff\6\44\112\uffff\46\44\12\uffff\51"+
            "\44\7\uffff\132\44\5\uffff\104\44\5\uffff\122\44\6\uffff\7\44"+
            "\1\uffff\77\44\1\uffff\1\44\1\uffff\4\44\2\uffff\7\44\1\uffff"+
            "\1\44\1\uffff\4\44\2\uffff\47\44\1\uffff\1\44\1\uffff\4\44\2"+
            "\uffff\37\44\1\uffff\1\44\1\uffff\4\44\2\uffff\7\44\1\uffff"+
            "\1\44\1\uffff\4\44\2\uffff\7\44\1\uffff\7\44\1\uffff\27\44\1"+
            "\uffff\37\44\1\uffff\1\44\1\uffff\4\44\2\uffff\7\44\1\uffff"+
            "\47\44\1\uffff\23\44\16\uffff\11\44\56\uffff\125\44\14\uffff"+
            "\u026c\44\2\uffff\10\44\12\uffff\32\44\5\uffff\113\44\25\uffff"+
            "\15\44\1\uffff\4\44\16\uffff\22\44\16\uffff\22\44\16\uffff\15"+
            "\44\1\uffff\3\44\17\uffff\64\44\43\uffff\1\44\4\uffff\1\44\3"+
            "\uffff\12\44\46\uffff\12\44\6\uffff\130\44\10\uffff\51\44\127"+
            "\uffff\35\44\51\uffff\50\44\2\uffff\5\44\u038b\uffff\154\44"+
            "\u0094\uffff\u009c\44\4\uffff\132\44\6\uffff\26\44\2\uffff\6"+
            "\44\2\uffff\46\44\2\uffff\6\44\2\uffff\10\44\1\uffff\1\44\1"+
            "\uffff\1\44\1\uffff\1\44\1\uffff\37\44\2\uffff\65\44\1\uffff"+
            "\7\44\1\uffff\1\44\3\uffff\3\44\1\uffff\7\44\3\uffff\4\44\2"+
            "\uffff\6\44\4\uffff\15\44\5\uffff\3\44\1\uffff\7\44\164\uffff"+
            "\1\44\15\uffff\1\44\u0082\uffff\1\44\4\uffff\1\44\2\uffff\12"+
            "\44\1\uffff\1\44\3\uffff\5\44\6\uffff\1\44\1\uffff\1\44\1\uffff"+
            "\1\44\1\uffff\4\44\1\uffff\3\44\1\uffff\7\44\3\uffff\3\44\5"+
            "\uffff\5\44\u0ebb\uffff\2\44\52\uffff\5\44\5\uffff\2\44\4\uffff"+
            "\126\44\6\uffff\3\44\1\uffff\132\44\1\uffff\4\44\5\uffff\50"+
            "\44\4\uffff\136\44\21\uffff\30\44\70\uffff\20\44\u0200\uffff"+
            "\u19b6\44\112\uffff\u51a6\44\132\uffff\u048d\44\u0773\uffff"+
            "\u2ba4\44\u215c\uffff\u012e\44\2\uffff\73\44\u0095\uffff\7\44"+
            "\14\uffff\5\44\5\uffff\1\44\1\uffff\12\44\1\uffff\15\44\1\uffff"+
            "\5\44\1\uffff\1\44\1\uffff\2\44\1\uffff\2\44\1\uffff\154\44"+
            "\41\uffff\u016b\44\22\uffff\100\44\2\uffff\66\44\50\uffff\14"+
            "\44\164\uffff\5\44\1\uffff\u0087\44\23\uffff\12\44\7\uffff\32"+
            "\44\6\uffff\32\44\13\uffff\131\44\3\uffff\6\44\2\uffff\6\44"+
            "\2\uffff\6\44\2\uffff\3\44",
            "\1\64\1\63\5\uffff\1\67\5\uffff\12\61\5\uffff\1\70\1\uffff"+
            "\23\60\1\133\6\60\1\uffff\1\65\2\uffff\1\62\1\uffff\23\57\1"+
            "\132\6\57\57\uffff\1\44\12\uffff\1\44\4\uffff\1\44\5\uffff\27"+
            "\44\1\uffff\37\44\1\uffff\u013f\44\31\uffff\162\44\4\uffff\14"+
            "\44\16\uffff\5\44\11\uffff\1\44\u008b\uffff\1\44\13\uffff\1"+
            "\44\1\uffff\3\44\1\uffff\1\44\1\uffff\24\44\1\uffff\54\44\1"+
            "\uffff\46\44\1\uffff\5\44\4\uffff\u0082\44\10\uffff\105\44\1"+
            "\uffff\46\44\2\uffff\2\44\6\uffff\20\44\41\uffff\46\44\2\uffff"+
            "\1\44\7\uffff\47\44\110\uffff\33\44\5\uffff\3\44\56\uffff\32"+
            "\44\5\uffff\13\44\25\uffff\12\44\4\uffff\2\44\1\uffff\143\44"+
            "\1\uffff\1\44\17\uffff\2\44\7\uffff\17\44\2\uffff\1\44\20\uffff"+
            "\1\44\1\uffff\36\44\35\uffff\3\44\60\uffff\46\44\13\uffff\1"+
            "\44\u0152\uffff\66\44\3\uffff\1\44\22\uffff\1\44\7\uffff\12"+
            "\44\4\uffff\12\44\25\uffff\10\44\2\uffff\2\44\2\uffff\26\44"+
            "\1\uffff\7\44\1\uffff\1\44\3\uffff\4\44\3\uffff\1\44\36\uffff"+
            "\2\44\1\uffff\3\44\4\uffff\14\44\23\uffff\6\44\4\uffff\2\44"+
            "\2\uffff\26\44\1\uffff\7\44\1\uffff\2\44\1\uffff\2\44\1\uffff"+
            "\2\44\37\uffff\4\44\1\uffff\1\44\7\uffff\12\44\2\uffff\3\44"+
            "\20\uffff\11\44\1\uffff\3\44\1\uffff\26\44\1\uffff\7\44\1\uffff"+
            "\2\44\1\uffff\5\44\3\uffff\1\44\22\uffff\1\44\17\uffff\2\44"+
            "\4\uffff\12\44\25\uffff\10\44\2\uffff\2\44\2\uffff\26\44\1\uffff"+
            "\7\44\1\uffff\2\44\1\uffff\5\44\3\uffff\1\44\36\uffff\2\44\1"+
            "\uffff\3\44\4\uffff\12\44\1\uffff\1\44\21\uffff\1\44\1\uffff"+
            "\6\44\3\uffff\3\44\1\uffff\4\44\3\uffff\2\44\1\uffff\1\44\1"+
            "\uffff\2\44\3\uffff\2\44\3\uffff\3\44\3\uffff\10\44\1\uffff"+
            "\3\44\55\uffff\11\44\25\uffff\10\44\1\uffff\3\44\1\uffff\27"+
            "\44\1\uffff\12\44\1\uffff\5\44\46\uffff\2\44\4\uffff\12\44\25"+
            "\uffff\10\44\1\uffff\3\44\1\uffff\27\44\1\uffff\12\44\1\uffff"+
            "\5\44\3\uffff\1\44\40\uffff\1\44\1\uffff\2\44\4\uffff\12\44"+
            "\25\uffff\10\44\1\uffff\3\44\1\uffff\27\44\1\uffff\20\44\46"+
            "\uffff\2\44\4\uffff\12\44\25\uffff\22\44\3\uffff\30\44\1\uffff"+
            "\11\44\1\uffff\1\44\2\uffff\7\44\72\uffff\60\44\1\uffff\2\44"+
            "\14\uffff\7\44\11\uffff\12\44\47\uffff\2\44\1\uffff\1\44\2\uffff"+
            "\2\44\1\uffff\1\44\2\uffff\1\44\6\uffff\4\44\1\uffff\7\44\1"+
            "\uffff\3\44\1\uffff\1\44\1\uffff\1\44\2\uffff\2\44\1\uffff\4"+
            "\44\1\uffff\2\44\11\uffff\1\44\2\uffff\5\44\1\uffff\1\44\11"+
            "\uffff\12\44\2\uffff\2\44\42\uffff\1\44\37\uffff\12\44\26\uffff"+
            "\10\44\1\uffff\42\44\35\uffff\4\44\164\uffff\42\44\1\uffff\5"+
            "\44\1\uffff\2\44\25\uffff\12\44\6\uffff\6\44\112\uffff\46\44"+
            "\12\uffff\51\44\7\uffff\132\44\5\uffff\104\44\5\uffff\122\44"+
            "\6\uffff\7\44\1\uffff\77\44\1\uffff\1\44\1\uffff\4\44\2\uffff"+
            "\7\44\1\uffff\1\44\1\uffff\4\44\2\uffff\47\44\1\uffff\1\44\1"+
            "\uffff\4\44\2\uffff\37\44\1\uffff\1\44\1\uffff\4\44\2\uffff"+
            "\7\44\1\uffff\1\44\1\uffff\4\44\2\uffff\7\44\1\uffff\7\44\1"+
            "\uffff\27\44\1\uffff\37\44\1\uffff\1\44\1\uffff\4\44\2\uffff"+
            "\7\44\1\uffff\47\44\1\uffff\23\44\16\uffff\11\44\56\uffff\125"+
            "\44\14\uffff\u026c\44\2\uffff\10\44\12\uffff\32\44\5\uffff\113"+
            "\44\25\uffff\15\44\1\uffff\4\44\16\uffff\22\44\16\uffff\22\44"+
            "\16\uffff\15\44\1\uffff\3\44\17\uffff\64\44\43\uffff\1\44\4"+
            "\uffff\1\44\3\uffff\12\44\46\uffff\12\44\6\uffff\130\44\10\uffff"+
            "\51\44\127\uffff\35\44\51\uffff\50\44\2\uffff\5\44\u038b\uffff"+
            "\154\44\u0094\uffff\u009c\44\4\uffff\132\44\6\uffff\26\44\2"+
            "\uffff\6\44\2\uffff\46\44\2\uffff\6\44\2\uffff\10\44\1\uffff"+
            "\1\44\1\uffff\1\44\1\uffff\1\44\1\uffff\37\44\2\uffff\65\44"+
            "\1\uffff\7\44\1\uffff\1\44\3\uffff\3\44\1\uffff\7\44\3\uffff"+
            "\4\44\2\uffff\6\44\4\uffff\15\44\5\uffff\3\44\1\uffff\7\44\164"+
            "\uffff\1\44\15\uffff\1\44\u0082\uffff\1\44\4\uffff\1\44\2\uffff"+
            "\12\44\1\uffff\1\44\3\uffff\5\44\6\uffff\1\44\1\uffff\1\44\1"+
            "\uffff\1\44\1\uffff\4\44\1\uffff\3\44\1\uffff\7\44\3\uffff\3"+
            "\44\5\uffff\5\44\u0ebb\uffff\2\44\52\uffff\5\44\5\uffff\2\44"+
            "\4\uffff\126\44\6\uffff\3\44\1\uffff\132\44\1\uffff\4\44\5\uffff"+
            "\50\44\4\uffff\136\44\21\uffff\30\44\70\uffff\20\44\u0200\uffff"+
            "\u19b6\44\112\uffff\u51a6\44\132\uffff\u048d\44\u0773\uffff"+
            "\u2ba4\44\u215c\uffff\u012e\44\2\uffff\73\44\u0095\uffff\7\44"+
            "\14\uffff\5\44\5\uffff\1\44\1\uffff\12\44\1\uffff\15\44\1\uffff"+
            "\5\44\1\uffff\1\44\1\uffff\2\44\1\uffff\2\44\1\uffff\154\44"+
            "\41\uffff\u016b\44\22\uffff\100\44\2\uffff\66\44\50\uffff\14"+
            "\44\164\uffff\5\44\1\uffff\u0087\44\23\uffff\12\44\7\uffff\32"+
            "\44\6\uffff\32\44\13\uffff\131\44\3\uffff\6\44\2\uffff\6\44"+
            "\2\uffff\6\44\2\uffff\3\44",
            "\1\64\1\63\5\uffff\1\67\5\uffff\12\61\5\uffff\1\70\1\uffff"+
            "\23\60\1\133\6\60\1\uffff\1\65\2\uffff\1\62\1\uffff\23\57\1"+
            "\132\6\57\57\uffff\1\44\12\uffff\1\44\4\uffff\1\44\5\uffff\27"+
            "\44\1\uffff\37\44\1\uffff\u013f\44\31\uffff\162\44\4\uffff\14"+
            "\44\16\uffff\5\44\11\uffff\1\44\u008b\uffff\1\44\13\uffff\1"+
            "\44\1\uffff\3\44\1\uffff\1\44\1\uffff\24\44\1\uffff\54\44\1"+
            "\uffff\46\44\1\uffff\5\44\4\uffff\u0082\44\10\uffff\105\44\1"+
            "\uffff\46\44\2\uffff\2\44\6\uffff\20\44\41\uffff\46\44\2\uffff"+
            "\1\44\7\uffff\47\44\110\uffff\33\44\5\uffff\3\44\56\uffff\32"+
            "\44\5\uffff\13\44\25\uffff\12\44\4\uffff\2\44\1\uffff\143\44"+
            "\1\uffff\1\44\17\uffff\2\44\7\uffff\17\44\2\uffff\1\44\20\uffff"+
            "\1\44\1\uffff\36\44\35\uffff\3\44\60\uffff\46\44\13\uffff\1"+
            "\44\u0152\uffff\66\44\3\uffff\1\44\22\uffff\1\44\7\uffff\12"+
            "\44\4\uffff\12\44\25\uffff\10\44\2\uffff\2\44\2\uffff\26\44"+
            "\1\uffff\7\44\1\uffff\1\44\3\uffff\4\44\3\uffff\1\44\36\uffff"+
            "\2\44\1\uffff\3\44\4\uffff\14\44\23\uffff\6\44\4\uffff\2\44"+
            "\2\uffff\26\44\1\uffff\7\44\1\uffff\2\44\1\uffff\2\44\1\uffff"+
            "\2\44\37\uffff\4\44\1\uffff\1\44\7\uffff\12\44\2\uffff\3\44"+
            "\20\uffff\11\44\1\uffff\3\44\1\uffff\26\44\1\uffff\7\44\1\uffff"+
            "\2\44\1\uffff\5\44\3\uffff\1\44\22\uffff\1\44\17\uffff\2\44"+
            "\4\uffff\12\44\25\uffff\10\44\2\uffff\2\44\2\uffff\26\44\1\uffff"+
            "\7\44\1\uffff\2\44\1\uffff\5\44\3\uffff\1\44\36\uffff\2\44\1"+
            "\uffff\3\44\4\uffff\12\44\1\uffff\1\44\21\uffff\1\44\1\uffff"+
            "\6\44\3\uffff\3\44\1\uffff\4\44\3\uffff\2\44\1\uffff\1\44\1"+
            "\uffff\2\44\3\uffff\2\44\3\uffff\3\44\3\uffff\10\44\1\uffff"+
            "\3\44\55\uffff\11\44\25\uffff\10\44\1\uffff\3\44\1\uffff\27"+
            "\44\1\uffff\12\44\1\uffff\5\44\46\uffff\2\44\4\uffff\12\44\25"+
            "\uffff\10\44\1\uffff\3\44\1\uffff\27\44\1\uffff\12\44\1\uffff"+
            "\5\44\3\uffff\1\44\40\uffff\1\44\1\uffff\2\44\4\uffff\12\44"+
            "\25\uffff\10\44\1\uffff\3\44\1\uffff\27\44\1\uffff\20\44\46"+
            "\uffff\2\44\4\uffff\12\44\25\uffff\22\44\3\uffff\30\44\1\uffff"+
            "\11\44\1\uffff\1\44\2\uffff\7\44\72\uffff\60\44\1\uffff\2\44"+
            "\14\uffff\7\44\11\uffff\12\44\47\uffff\2\44\1\uffff\1\44\2\uffff"+
            "\2\44\1\uffff\1\44\2\uffff\1\44\6\uffff\4\44\1\uffff\7\44\1"+
            "\uffff\3\44\1\uffff\1\44\1\uffff\1\44\2\uffff\2\44\1\uffff\4"+
            "\44\1\uffff\2\44\11\uffff\1\44\2\uffff\5\44\1\uffff\1\44\11"+
            "\uffff\12\44\2\uffff\2\44\42\uffff\1\44\37\uffff\12\44\26\uffff"+
            "\10\44\1\uffff\42\44\35\uffff\4\44\164\uffff\42\44\1\uffff\5"+
            "\44\1\uffff\2\44\25\uffff\12\44\6\uffff\6\44\112\uffff\46\44"+
            "\12\uffff\51\44\7\uffff\132\44\5\uffff\104\44\5\uffff\122\44"+
            "\6\uffff\7\44\1\uffff\77\44\1\uffff\1\44\1\uffff\4\44\2\uffff"+
            "\7\44\1\uffff\1\44\1\uffff\4\44\2\uffff\47\44\1\uffff\1\44\1"+
            "\uffff\4\44\2\uffff\37\44\1\uffff\1\44\1\uffff\4\44\2\uffff"+
            "\7\44\1\uffff\1\44\1\uffff\4\44\2\uffff\7\44\1\uffff\7\44\1"+
            "\uffff\27\44\1\uffff\37\44\1\uffff\1\44\1\uffff\4\44\2\uffff"+
            "\7\44\1\uffff\47\44\1\uffff\23\44\16\uffff\11\44\56\uffff\125"+
            "\44\14\uffff\u026c\44\2\uffff\10\44\12\uffff\32\44\5\uffff\113"+
            "\44\25\uffff\15\44\1\uffff\4\44\16\uffff\22\44\16\uffff\22\44"+
            "\16\uffff\15\44\1\uffff\3\44\17\uffff\64\44\43\uffff\1\44\4"+
            "\uffff\1\44\3\uffff\12\44\46\uffff\12\44\6\uffff\130\44\10\uffff"+
            "\51\44\127\uffff\35\44\51\uffff\50\44\2\uffff\5\44\u038b\uffff"+
            "\154\44\u0094\uffff\u009c\44\4\uffff\132\44\6\uffff\26\44\2"+
            "\uffff\6\44\2\uffff\46\44\2\uffff\6\44\2\uffff\10\44\1\uffff"+
            "\1\44\1\uffff\1\44\1\uffff\1\44\1\uffff\37\44\2\uffff\65\44"+
            "\1\uffff\7\44\1\uffff\1\44\3\uffff\3\44\1\uffff\7\44\3\uffff"+
            "\4\44\2\uffff\6\44\4\uffff\15\44\5\uffff\3\44\1\uffff\7\44\164"+
            "\uffff\1\44\15\uffff\1\44\u0082\uffff\1\44\4\uffff\1\44\2\uffff"+
            "\12\44\1\uffff\1\44\3\uffff\5\44\6\uffff\1\44\1\uffff\1\44\1"+
            "\uffff\1\44\1\uffff\4\44\1\uffff\3\44\1\uffff\7\44\3\uffff\3"+
            "\44\5\uffff\5\44\u0ebb\uffff\2\44\52\uffff\5\44\5\uffff\2\44"+
            "\4\uffff\126\44\6\uffff\3\44\1\uffff\132\44\1\uffff\4\44\5\uffff"+
            "\50\44\4\uffff\136\44\21\uffff\30\44\70\uffff\20\44\u0200\uffff"+
            "\u19b6\44\112\uffff\u51a6\44\132\uffff\u048d\44\u0773\uffff"+
            "\u2ba4\44\u215c\uffff\u012e\44\2\uffff\73\44\u0095\uffff\7\44"+
            "\14\uffff\5\44\5\uffff\1\44\1\uffff\12\44\1\uffff\15\44\1\uffff"+
            "\5\44\1\uffff\1\44\1\uffff\2\44\1\uffff\2\44\1\uffff\154\44"+
            "\41\uffff\u016b\44\22\uffff\100\44\2\uffff\66\44\50\uffff\14"+
            "\44\164\uffff\5\44\1\uffff\u0087\44\23\uffff\12\44\7\uffff\32"+
            "\44\6\uffff\32\44\13\uffff\131\44\3\uffff\6\44\2\uffff\6\44"+
            "\2\uffff\6\44\2\uffff\3\44",
            "",
            "\1\116\1\uffff\12\135\13\uffff\1\136\37\uffff\1\136",
            "\1\116\1\uffff\12\137\13\uffff\1\136\37\uffff\1\136",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\64\1\63\5\uffff\1\67\5\uffff\12\61\5\uffff\1\70\1\uffff"+
            "\32\60\1\uffff\1\65\2\uffff\1\62\1\uffff\32\57\57\uffff\1\44"+
            "\12\uffff\1\44\4\uffff\1\44\5\uffff\27\44\1\uffff\37\44\1\uffff"+
            "\u013f\44\31\uffff\162\44\4\uffff\14\44\16\uffff\5\44\11\uffff"+
            "\1\44\u008b\uffff\1\44\13\uffff\1\44\1\uffff\3\44\1\uffff\1"+
            "\44\1\uffff\24\44\1\uffff\54\44\1\uffff\46\44\1\uffff\5\44\4"+
            "\uffff\u0082\44\10\uffff\105\44\1\uffff\46\44\2\uffff\2\44\6"+
            "\uffff\20\44\41\uffff\46\44\2\uffff\1\44\7\uffff\47\44\110\uffff"+
            "\33\44\5\uffff\3\44\56\uffff\32\44\5\uffff\13\44\25\uffff\12"+
            "\44\4\uffff\2\44\1\uffff\143\44\1\uffff\1\44\17\uffff\2\44\7"+
            "\uffff\17\44\2\uffff\1\44\20\uffff\1\44\1\uffff\36\44\35\uffff"+
            "\3\44\60\uffff\46\44\13\uffff\1\44\u0152\uffff\66\44\3\uffff"+
            "\1\44\22\uffff\1\44\7\uffff\12\44\4\uffff\12\44\25\uffff\10"+
            "\44\2\uffff\2\44\2\uffff\26\44\1\uffff\7\44\1\uffff\1\44\3\uffff"+
            "\4\44\3\uffff\1\44\36\uffff\2\44\1\uffff\3\44\4\uffff\14\44"+
            "\23\uffff\6\44\4\uffff\2\44\2\uffff\26\44\1\uffff\7\44\1\uffff"+
            "\2\44\1\uffff\2\44\1\uffff\2\44\37\uffff\4\44\1\uffff\1\44\7"+
            "\uffff\12\44\2\uffff\3\44\20\uffff\11\44\1\uffff\3\44\1\uffff"+
            "\26\44\1\uffff\7\44\1\uffff\2\44\1\uffff\5\44\3\uffff\1\44\22"+
            "\uffff\1\44\17\uffff\2\44\4\uffff\12\44\25\uffff\10\44\2\uffff"+
            "\2\44\2\uffff\26\44\1\uffff\7\44\1\uffff\2\44\1\uffff\5\44\3"+
            "\uffff\1\44\36\uffff\2\44\1\uffff\3\44\4\uffff\12\44\1\uffff"+
            "\1\44\21\uffff\1\44\1\uffff\6\44\3\uffff\3\44\1\uffff\4\44\3"+
            "\uffff\2\44\1\uffff\1\44\1\uffff\2\44\3\uffff\2\44\3\uffff\3"+
            "\44\3\uffff\10\44\1\uffff\3\44\55\uffff\11\44\25\uffff\10\44"+
            "\1\uffff\3\44\1\uffff\27\44\1\uffff\12\44\1\uffff\5\44\46\uffff"+
            "\2\44\4\uffff\12\44\25\uffff\10\44\1\uffff\3\44\1\uffff\27\44"+
            "\1\uffff\12\44\1\uffff\5\44\3\uffff\1\44\40\uffff\1\44\1\uffff"+
            "\2\44\4\uffff\12\44\25\uffff\10\44\1\uffff\3\44\1\uffff\27\44"+
            "\1\uffff\20\44\46\uffff\2\44\4\uffff\12\44\25\uffff\22\44\3"+
            "\uffff\30\44\1\uffff\11\44\1\uffff\1\44\2\uffff\7\44\72\uffff"+
            "\60\44\1\uffff\2\44\14\uffff\7\44\11\uffff\12\44\47\uffff\2"+
            "\44\1\uffff\1\44\2\uffff\2\44\1\uffff\1\44\2\uffff\1\44\6\uffff"+
            "\4\44\1\uffff\7\44\1\uffff\3\44\1\uffff\1\44\1\uffff\1\44\2"+
            "\uffff\2\44\1\uffff\4\44\1\uffff\2\44\11\uffff\1\44\2\uffff"+
            "\5\44\1\uffff\1\44\11\uffff\12\44\2\uffff\2\44\42\uffff\1\44"+
            "\37\uffff\12\44\26\uffff\10\44\1\uffff\42\44\35\uffff\4\44\164"+
            "\uffff\42\44\1\uffff\5\44\1\uffff\2\44\25\uffff\12\44\6\uffff"+
            "\6\44\112\uffff\46\44\12\uffff\51\44\7\uffff\132\44\5\uffff"+
            "\104\44\5\uffff\122\44\6\uffff\7\44\1\uffff\77\44\1\uffff\1"+
            "\44\1\uffff\4\44\2\uffff\7\44\1\uffff\1\44\1\uffff\4\44\2\uffff"+
            "\47\44\1\uffff\1\44\1\uffff\4\44\2\uffff\37\44\1\uffff\1\44"+
            "\1\uffff\4\44\2\uffff\7\44\1\uffff\1\44\1\uffff\4\44\2\uffff"+
            "\7\44\1\uffff\7\44\1\uffff\27\44\1\uffff\37\44\1\uffff\1\44"+
            "\1\uffff\4\44\2\uffff\7\44\1\uffff\47\44\1\uffff\23\44\16\uffff"+
            "\11\44\56\uffff\125\44\14\uffff\u026c\44\2\uffff\10\44\12\uffff"+
            "\32\44\5\uffff\113\44\25\uffff\15\44\1\uffff\4\44\16\uffff\22"+
            "\44\16\uffff\22\44\16\uffff\15\44\1\uffff\3\44\17\uffff\64\44"+
            "\43\uffff\1\44\4\uffff\1\44\3\uffff\12\44\46\uffff\12\44\6\uffff"+
            "\130\44\10\uffff\51\44\127\uffff\35\44\51\uffff\50\44\2\uffff"+
            "\5\44\u038b\uffff\154\44\u0094\uffff\u009c\44\4\uffff\132\44"+
            "\6\uffff\26\44\2\uffff\6\44\2\uffff\46\44\2\uffff\6\44\2\uffff"+
            "\10\44\1\uffff\1\44\1\uffff\1\44\1\uffff\1\44\1\uffff\37\44"+
            "\2\uffff\65\44\1\uffff\7\44\1\uffff\1\44\3\uffff\3\44\1\uffff"+
            "\7\44\3\uffff\4\44\2\uffff\6\44\4\uffff\15\44\5\uffff\3\44\1"+
            "\uffff\7\44\164\uffff\1\44\15\uffff\1\44\u0082\uffff\1\44\4"+
            "\uffff\1\44\2\uffff\12\44\1\uffff\1\44\3\uffff\5\44\6\uffff"+
            "\1\44\1\uffff\1\44\1\uffff\1\44\1\uffff\4\44\1\uffff\3\44\1"+
            "\uffff\7\44\3\uffff\3\44\5\uffff\5\44\u0ebb\uffff\2\44\52\uffff"+
            "\5\44\5\uffff\2\44\4\uffff\126\44\6\uffff\3\44\1\uffff\132\44"+
            "\1\uffff\4\44\5\uffff\50\44\4\uffff\136\44\21\uffff\30\44\70"+
            "\uffff\20\44\u0200\uffff\u19b6\44\112\uffff\u51a6\44\132\uffff"+
            "\u048d\44\u0773\uffff\u2ba4\44\u215c\uffff\u012e\44\2\uffff"+
            "\73\44\u0095\uffff\7\44\14\uffff\5\44\5\uffff\1\44\1\uffff\12"+
            "\44\1\uffff\15\44\1\uffff\5\44\1\uffff\1\44\1\uffff\2\44\1\uffff"+
            "\2\44\1\uffff\154\44\41\uffff\u016b\44\22\uffff\100\44\2\uffff"+
            "\66\44\50\uffff\14\44\164\uffff\5\44\1\uffff\u0087\44\23\uffff"+
            "\12\44\7\uffff\32\44\6\uffff\32\44\13\uffff\131\44\3\uffff\6"+
            "\44\2\uffff\6\44\2\uffff\6\44\2\uffff\3\44",
            "\1\64\1\63\5\uffff\1\67\5\uffff\12\61\5\uffff\1\70\1\uffff"+
            "\32\60\1\uffff\1\65\2\uffff\1\62\1\uffff\32\57\57\uffff\1\44"+
            "\12\uffff\1\44\4\uffff\1\44\5\uffff\27\44\1\uffff\37\44\1\uffff"+
            "\u013f\44\31\uffff\162\44\4\uffff\14\44\16\uffff\5\44\11\uffff"+
            "\1\44\u008b\uffff\1\44\13\uffff\1\44\1\uffff\3\44\1\uffff\1"+
            "\44\1\uffff\24\44\1\uffff\54\44\1\uffff\46\44\1\uffff\5\44\4"+
            "\uffff\u0082\44\10\uffff\105\44\1\uffff\46\44\2\uffff\2\44\6"+
            "\uffff\20\44\41\uffff\46\44\2\uffff\1\44\7\uffff\47\44\110\uffff"+
            "\33\44\5\uffff\3\44\56\uffff\32\44\5\uffff\13\44\25\uffff\12"+
            "\44\4\uffff\2\44\1\uffff\143\44\1\uffff\1\44\17\uffff\2\44\7"+
            "\uffff\17\44\2\uffff\1\44\20\uffff\1\44\1\uffff\36\44\35\uffff"+
            "\3\44\60\uffff\46\44\13\uffff\1\44\u0152\uffff\66\44\3\uffff"+
            "\1\44\22\uffff\1\44\7\uffff\12\44\4\uffff\12\44\25\uffff\10"+
            "\44\2\uffff\2\44\2\uffff\26\44\1\uffff\7\44\1\uffff\1\44\3\uffff"+
            "\4\44\3\uffff\1\44\36\uffff\2\44\1\uffff\3\44\4\uffff\14\44"+
            "\23\uffff\6\44\4\uffff\2\44\2\uffff\26\44\1\uffff\7\44\1\uffff"+
            "\2\44\1\uffff\2\44\1\uffff\2\44\37\uffff\4\44\1\uffff\1\44\7"+
            "\uffff\12\44\2\uffff\3\44\20\uffff\11\44\1\uffff\3\44\1\uffff"+
            "\26\44\1\uffff\7\44\1\uffff\2\44\1\uffff\5\44\3\uffff\1\44\22"+
            "\uffff\1\44\17\uffff\2\44\4\uffff\12\44\25\uffff\10\44\2\uffff"+
            "\2\44\2\uffff\26\44\1\uffff\7\44\1\uffff\2\44\1\uffff\5\44\3"+
            "\uffff\1\44\36\uffff\2\44\1\uffff\3\44\4\uffff\12\44\1\uffff"+
            "\1\44\21\uffff\1\44\1\uffff\6\44\3\uffff\3\44\1\uffff\4\44\3"+
            "\uffff\2\44\1\uffff\1\44\1\uffff\2\44\3\uffff\2\44\3\uffff\3"+
            "\44\3\uffff\10\44\1\uffff\3\44\55\uffff\11\44\25\uffff\10\44"+
            "\1\uffff\3\44\1\uffff\27\44\1\uffff\12\44\1\uffff\5\44\46\uffff"+
            "\2\44\4\uffff\12\44\25\uffff\10\44\1\uffff\3\44\1\uffff\27\44"+
            "\1\uffff\12\44\1\uffff\5\44\3\uffff\1\44\40\uffff\1\44\1\uffff"+
            "\2\44\4\uffff\12\44\25\uffff\10\44\1\uffff\3\44\1\uffff\27\44"+
            "\1\uffff\20\44\46\uffff\2\44\4\uffff\12\44\25\uffff\22\44\3"+
            "\uffff\30\44\1\uffff\11\44\1\uffff\1\44\2\uffff\7\44\72\uffff"+
            "\60\44\1\uffff\2\44\14\uffff\7\44\11\uffff\12\44\47\uffff\2"+
            "\44\1\uffff\1\44\2\uffff\2\44\1\uffff\1\44\2\uffff\1\44\6\uffff"+
            "\4\44\1\uffff\7\44\1\uffff\3\44\1\uffff\1\44\1\uffff\1\44\2"+
            "\uffff\2\44\1\uffff\4\44\1\uffff\2\44\11\uffff\1\44\2\uffff"+
            "\5\44\1\uffff\1\44\11\uffff\12\44\2\uffff\2\44\42\uffff\1\44"+
            "\37\uffff\12\44\26\uffff\10\44\1\uffff\42\44\35\uffff\4\44\164"+
            "\uffff\42\44\1\uffff\5\44\1\uffff\2\44\25\uffff\12\44\6\uffff"+
            "\6\44\112\uffff\46\44\12\uffff\51\44\7\uffff\132\44\5\uffff"+
            "\104\44\5\uffff\122\44\6\uffff\7\44\1\uffff\77\44\1\uffff\1"+
            "\44\1\uffff\4\44\2\uffff\7\44\1\uffff\1\44\1\uffff\4\44\2\uffff"+
            "\47\44\1\uffff\1\44\1\uffff\4\44\2\uffff\37\44\1\uffff\1\44"+
            "\1\uffff\4\44\2\uffff\7\44\1\uffff\1\44\1\uffff\4\44\2\uffff"+
            "\7\44\1\uffff\7\44\1\uffff\27\44\1\uffff\37\44\1\uffff\1\44"+
            "\1\uffff\4\44\2\uffff\7\44\1\uffff\47\44\1\uffff\23\44\16\uffff"+
            "\11\44\56\uffff\125\44\14\uffff\u026c\44\2\uffff\10\44\12\uffff"+
            "\32\44\5\uffff\113\44\25\uffff\15\44\1\uffff\4\44\16\uffff\22"+
            "\44\16\uffff\22\44\16\uffff\15\44\1\uffff\3\44\17\uffff\64\44"+
            "\43\uffff\1\44\4\uffff\1\44\3\uffff\12\44\46\uffff\12\44\6\uffff"+
            "\130\44\10\uffff\51\44\127\uffff\35\44\51\uffff\50\44\2\uffff"+
            "\5\44\u038b\uffff\154\44\u0094\uffff\u009c\44\4\uffff\132\44"+
            "\6\uffff\26\44\2\uffff\6\44\2\uffff\46\44\2\uffff\6\44\2\uffff"+
            "\10\44\1\uffff\1\44\1\uffff\1\44\1\uffff\1\44\1\uffff\37\44"+
            "\2\uffff\65\44\1\uffff\7\44\1\uffff\1\44\3\uffff\3\44\1\uffff"+
            "\7\44\3\uffff\4\44\2\uffff\6\44\4\uffff\15\44\5\uffff\3\44\1"+
            "\uffff\7\44\164\uffff\1\44\15\uffff\1\44\u0082\uffff\1\44\4"+
            "\uffff\1\44\2\uffff\12\44\1\uffff\1\44\3\uffff\5\44\6\uffff"+
            "\1\44\1\uffff\1\44\1\uffff\1\44\1\uffff\4\44\1\uffff\3\44\1"+
            "\uffff\7\44\3\uffff\3\44\5\uffff\5\44\u0ebb\uffff\2\44\52\uffff"+
            "\5\44\5\uffff\2\44\4\uffff\126\44\6\uffff\3\44\1\uffff\132\44"+
            "\1\uffff\4\44\5\uffff\50\44\4\uffff\136\44\21\uffff\30\44\70"+
            "\uffff\20\44\u0200\uffff\u19b6\44\112\uffff\u51a6\44\132\uffff"+
            "\u048d\44\u0773\uffff\u2ba4\44\u215c\uffff\u012e\44\2\uffff"+
            "\73\44\u0095\uffff\7\44\14\uffff\5\44\5\uffff\1\44\1\uffff\12"+
            "\44\1\uffff\15\44\1\uffff\5\44\1\uffff\1\44\1\uffff\2\44\1\uffff"+
            "\2\44\1\uffff\154\44\41\uffff\u016b\44\22\uffff\100\44\2\uffff"+
            "\66\44\50\uffff\14\44\164\uffff\5\44\1\uffff\u0087\44\23\uffff"+
            "\12\44\7\uffff\32\44\6\uffff\32\44\13\uffff\131\44\3\uffff\6"+
            "\44\2\uffff\6\44\2\uffff\6\44\2\uffff\3\44",
            "",
            "\1\67\3\uffff\1\116\1\uffff\12\115\5\uffff\1\70\1\uffff\4"+
            "\44\1\117\25\44\1\uffff\1\42\4\uffff\4\44\1\117\25\44\57\uffff"+
            "\1\44\12\uffff\1\44\4\uffff\1\44\5\uffff\27\44\1\uffff\37\44"+
            "\1\uffff\u013f\44\31\uffff\162\44\4\uffff\14\44\16\uffff\5\44"+
            "\11\uffff\1\44\u008b\uffff\1\44\13\uffff\1\44\1\uffff\3\44\1"+
            "\uffff\1\44\1\uffff\24\44\1\uffff\54\44\1\uffff\46\44\1\uffff"+
            "\5\44\4\uffff\u0082\44\10\uffff\105\44\1\uffff\46\44\2\uffff"+
            "\2\44\6\uffff\20\44\41\uffff\46\44\2\uffff\1\44\7\uffff\47\44"+
            "\110\uffff\33\44\5\uffff\3\44\56\uffff\32\44\5\uffff\13\44\25"+
            "\uffff\12\44\4\uffff\2\44\1\uffff\143\44\1\uffff\1\44\17\uffff"+
            "\2\44\7\uffff\17\44\2\uffff\1\44\20\uffff\1\44\1\uffff\36\44"+
            "\35\uffff\3\44\60\uffff\46\44\13\uffff\1\44\u0152\uffff\66\44"+
            "\3\uffff\1\44\22\uffff\1\44\7\uffff\12\44\4\uffff\12\44\25\uffff"+
            "\10\44\2\uffff\2\44\2\uffff\26\44\1\uffff\7\44\1\uffff\1\44"+
            "\3\uffff\4\44\3\uffff\1\44\36\uffff\2\44\1\uffff\3\44\4\uffff"+
            "\14\44\23\uffff\6\44\4\uffff\2\44\2\uffff\26\44\1\uffff\7\44"+
            "\1\uffff\2\44\1\uffff\2\44\1\uffff\2\44\37\uffff\4\44\1\uffff"+
            "\1\44\7\uffff\12\44\2\uffff\3\44\20\uffff\11\44\1\uffff\3\44"+
            "\1\uffff\26\44\1\uffff\7\44\1\uffff\2\44\1\uffff\5\44\3\uffff"+
            "\1\44\22\uffff\1\44\17\uffff\2\44\4\uffff\12\44\25\uffff\10"+
            "\44\2\uffff\2\44\2\uffff\26\44\1\uffff\7\44\1\uffff\2\44\1\uffff"+
            "\5\44\3\uffff\1\44\36\uffff\2\44\1\uffff\3\44\4\uffff\12\44"+
            "\1\uffff\1\44\21\uffff\1\44\1\uffff\6\44\3\uffff\3\44\1\uffff"+
            "\4\44\3\uffff\2\44\1\uffff\1\44\1\uffff\2\44\3\uffff\2\44\3"+
            "\uffff\3\44\3\uffff\10\44\1\uffff\3\44\55\uffff\11\44\25\uffff"+
            "\10\44\1\uffff\3\44\1\uffff\27\44\1\uffff\12\44\1\uffff\5\44"+
            "\46\uffff\2\44\4\uffff\12\44\25\uffff\10\44\1\uffff\3\44\1\uffff"+
            "\27\44\1\uffff\12\44\1\uffff\5\44\3\uffff\1\44\40\uffff\1\44"+
            "\1\uffff\2\44\4\uffff\12\44\25\uffff\10\44\1\uffff\3\44\1\uffff"+
            "\27\44\1\uffff\20\44\46\uffff\2\44\4\uffff\12\44\25\uffff\22"+
            "\44\3\uffff\30\44\1\uffff\11\44\1\uffff\1\44\2\uffff\7\44\72"+
            "\uffff\60\44\1\uffff\2\44\14\uffff\7\44\11\uffff\12\44\47\uffff"+
            "\2\44\1\uffff\1\44\2\uffff\2\44\1\uffff\1\44\2\uffff\1\44\6"+
            "\uffff\4\44\1\uffff\7\44\1\uffff\3\44\1\uffff\1\44\1\uffff\1"+
            "\44\2\uffff\2\44\1\uffff\4\44\1\uffff\2\44\11\uffff\1\44\2\uffff"+
            "\5\44\1\uffff\1\44\11\uffff\12\44\2\uffff\2\44\42\uffff\1\44"+
            "\37\uffff\12\44\26\uffff\10\44\1\uffff\42\44\35\uffff\4\44\164"+
            "\uffff\42\44\1\uffff\5\44\1\uffff\2\44\25\uffff\12\44\6\uffff"+
            "\6\44\112\uffff\46\44\12\uffff\51\44\7\uffff\132\44\5\uffff"+
            "\104\44\5\uffff\122\44\6\uffff\7\44\1\uffff\77\44\1\uffff\1"+
            "\44\1\uffff\4\44\2\uffff\7\44\1\uffff\1\44\1\uffff\4\44\2\uffff"+
            "\47\44\1\uffff\1\44\1\uffff\4\44\2\uffff\37\44\1\uffff\1\44"+
            "\1\uffff\4\44\2\uffff\7\44\1\uffff\1\44\1\uffff\4\44\2\uffff"+
            "\7\44\1\uffff\7\44\1\uffff\27\44\1\uffff\37\44\1\uffff\1\44"+
            "\1\uffff\4\44\2\uffff\7\44\1\uffff\47\44\1\uffff\23\44\16\uffff"+
            "\11\44\56\uffff\125\44\14\uffff\u026c\44\2\uffff\10\44\12\uffff"+
            "\32\44\5\uffff\113\44\25\uffff\15\44\1\uffff\4\44\16\uffff\22"+
            "\44\16\uffff\22\44\16\uffff\15\44\1\uffff\3\44\17\uffff\64\44"+
            "\43\uffff\1\44\4\uffff\1\44\3\uffff\12\44\46\uffff\12\44\6\uffff"+
            "\130\44\10\uffff\51\44\127\uffff\35\44\51\uffff\50\44\2\uffff"+
            "\5\44\u038b\uffff\154\44\u0094\uffff\u009c\44\4\uffff\132\44"+
            "\6\uffff\26\44\2\uffff\6\44\2\uffff\46\44\2\uffff\6\44\2\uffff"+
            "\10\44\1\uffff\1\44\1\uffff\1\44\1\uffff\1\44\1\uffff\37\44"+
            "\2\uffff\65\44\1\uffff\7\44\1\uffff\1\44\3\uffff\3\44\1\uffff"+
            "\7\44\3\uffff\4\44\2\uffff\6\44\4\uffff\15\44\5\uffff\3\44\1"+
            "\uffff\7\44\164\uffff\1\44\15\uffff\1\44\u0082\uffff\1\44\4"+
            "\uffff\1\44\2\uffff\12\44\1\uffff\1\44\3\uffff\5\44\6\uffff"+
            "\1\44\1\uffff\1\44\1\uffff\1\44\1\uffff\4\44\1\uffff\3\44\1"+
            "\uffff\7\44\3\uffff\3\44\5\uffff\5\44\u0ebb\uffff\2\44\52\uffff"+
            "\5\44\5\uffff\2\44\4\uffff\126\44\6\uffff\3\44\1\uffff\132\44"+
            "\1\uffff\4\44\5\uffff\50\44\4\uffff\136\44\21\uffff\30\44\70"+
            "\uffff\20\44\u0200\uffff\u19b6\44\112\uffff\u51a6\44\132\uffff"+
            "\u048d\44\u0773\uffff\u2ba4\44\u215c\uffff\u012e\44\2\uffff"+
            "\73\44\u0095\uffff\7\44\14\uffff\5\44\5\uffff\1\44\1\uffff\12"+
            "\44\1\uffff\15\44\1\uffff\5\44\1\uffff\1\44\1\uffff\2\44\1\uffff"+
            "\2\44\1\uffff\154\44\41\uffff\u016b\44\22\uffff\100\44\2\uffff"+
            "\66\44\50\uffff\14\44\164\uffff\5\44\1\uffff\u0087\44\23\uffff"+
            "\12\44\7\uffff\32\44\6\uffff\32\44\13\uffff\131\44\3\uffff\6"+
            "\44\2\uffff\6\44\2\uffff\6\44\2\uffff\3\44",
            "",
            "\1\67\1\142\1\uffff\1\142\2\uffff\12\141\5\uffff\1\70\1\uffff"+
            "\32\44\1\uffff\1\42\4\uffff\32\44\57\uffff\1\44\12\uffff\1\44"+
            "\4\uffff\1\44\5\uffff\27\44\1\uffff\37\44\1\uffff\u013f\44\31"+
            "\uffff\162\44\4\uffff\14\44\16\uffff\5\44\11\uffff\1\44\u008b"+
            "\uffff\1\44\13\uffff\1\44\1\uffff\3\44\1\uffff\1\44\1\uffff"+
            "\24\44\1\uffff\54\44\1\uffff\46\44\1\uffff\5\44\4\uffff\u0082"+
            "\44\10\uffff\105\44\1\uffff\46\44\2\uffff\2\44\6\uffff\20\44"+
            "\41\uffff\46\44\2\uffff\1\44\7\uffff\47\44\110\uffff\33\44\5"+
            "\uffff\3\44\56\uffff\32\44\5\uffff\13\44\25\uffff\12\44\4\uffff"+
            "\2\44\1\uffff\143\44\1\uffff\1\44\17\uffff\2\44\7\uffff\17\44"+
            "\2\uffff\1\44\20\uffff\1\44\1\uffff\36\44\35\uffff\3\44\60\uffff"+
            "\46\44\13\uffff\1\44\u0152\uffff\66\44\3\uffff\1\44\22\uffff"+
            "\1\44\7\uffff\12\44\4\uffff\12\44\25\uffff\10\44\2\uffff\2\44"+
            "\2\uffff\26\44\1\uffff\7\44\1\uffff\1\44\3\uffff\4\44\3\uffff"+
            "\1\44\36\uffff\2\44\1\uffff\3\44\4\uffff\14\44\23\uffff\6\44"+
            "\4\uffff\2\44\2\uffff\26\44\1\uffff\7\44\1\uffff\2\44\1\uffff"+
            "\2\44\1\uffff\2\44\37\uffff\4\44\1\uffff\1\44\7\uffff\12\44"+
            "\2\uffff\3\44\20\uffff\11\44\1\uffff\3\44\1\uffff\26\44\1\uffff"+
            "\7\44\1\uffff\2\44\1\uffff\5\44\3\uffff\1\44\22\uffff\1\44\17"+
            "\uffff\2\44\4\uffff\12\44\25\uffff\10\44\2\uffff\2\44\2\uffff"+
            "\26\44\1\uffff\7\44\1\uffff\2\44\1\uffff\5\44\3\uffff\1\44\36"+
            "\uffff\2\44\1\uffff\3\44\4\uffff\12\44\1\uffff\1\44\21\uffff"+
            "\1\44\1\uffff\6\44\3\uffff\3\44\1\uffff\4\44\3\uffff\2\44\1"+
            "\uffff\1\44\1\uffff\2\44\3\uffff\2\44\3\uffff\3\44\3\uffff\10"+
            "\44\1\uffff\3\44\55\uffff\11\44\25\uffff\10\44\1\uffff\3\44"+
            "\1\uffff\27\44\1\uffff\12\44\1\uffff\5\44\46\uffff\2\44\4\uffff"+
            "\12\44\25\uffff\10\44\1\uffff\3\44\1\uffff\27\44\1\uffff\12"+
            "\44\1\uffff\5\44\3\uffff\1\44\40\uffff\1\44\1\uffff\2\44\4\uffff"+
            "\12\44\25\uffff\10\44\1\uffff\3\44\1\uffff\27\44\1\uffff\20"+
            "\44\46\uffff\2\44\4\uffff\12\44\25\uffff\22\44\3\uffff\30\44"+
            "\1\uffff\11\44\1\uffff\1\44\2\uffff\7\44\72\uffff\60\44\1\uffff"+
            "\2\44\14\uffff\7\44\11\uffff\12\44\47\uffff\2\44\1\uffff\1\44"+
            "\2\uffff\2\44\1\uffff\1\44\2\uffff\1\44\6\uffff\4\44\1\uffff"+
            "\7\44\1\uffff\3\44\1\uffff\1\44\1\uffff\1\44\2\uffff\2\44\1"+
            "\uffff\4\44\1\uffff\2\44\11\uffff\1\44\2\uffff\5\44\1\uffff"+
            "\1\44\11\uffff\12\44\2\uffff\2\44\42\uffff\1\44\37\uffff\12"+
            "\44\26\uffff\10\44\1\uffff\42\44\35\uffff\4\44\164\uffff\42"+
            "\44\1\uffff\5\44\1\uffff\2\44\25\uffff\12\44\6\uffff\6\44\112"+
            "\uffff\46\44\12\uffff\51\44\7\uffff\132\44\5\uffff\104\44\5"+
            "\uffff\122\44\6\uffff\7\44\1\uffff\77\44\1\uffff\1\44\1\uffff"+
            "\4\44\2\uffff\7\44\1\uffff\1\44\1\uffff\4\44\2\uffff\47\44\1"+
            "\uffff\1\44\1\uffff\4\44\2\uffff\37\44\1\uffff\1\44\1\uffff"+
            "\4\44\2\uffff\7\44\1\uffff\1\44\1\uffff\4\44\2\uffff\7\44\1"+
            "\uffff\7\44\1\uffff\27\44\1\uffff\37\44\1\uffff\1\44\1\uffff"+
            "\4\44\2\uffff\7\44\1\uffff\47\44\1\uffff\23\44\16\uffff\11\44"+
            "\56\uffff\125\44\14\uffff\u026c\44\2\uffff\10\44\12\uffff\32"+
            "\44\5\uffff\113\44\25\uffff\15\44\1\uffff\4\44\16\uffff\22\44"+
            "\16\uffff\22\44\16\uffff\15\44\1\uffff\3\44\17\uffff\64\44\43"+
            "\uffff\1\44\4\uffff\1\44\3\uffff\12\44\46\uffff\12\44\6\uffff"+
            "\130\44\10\uffff\51\44\127\uffff\35\44\51\uffff\50\44\2\uffff"+
            "\5\44\u038b\uffff\154\44\u0094\uffff\u009c\44\4\uffff\132\44"+
            "\6\uffff\26\44\2\uffff\6\44\2\uffff\46\44\2\uffff\6\44\2\uffff"+
            "\10\44\1\uffff\1\44\1\uffff\1\44\1\uffff\1\44\1\uffff\37\44"+
            "\2\uffff\65\44\1\uffff\7\44\1\uffff\1\44\3\uffff\3\44\1\uffff"+
            "\7\44\3\uffff\4\44\2\uffff\6\44\4\uffff\15\44\5\uffff\3\44\1"+
            "\uffff\7\44\164\uffff\1\44\15\uffff\1\44\u0082\uffff\1\44\4"+
            "\uffff\1\44\2\uffff\12\44\1\uffff\1\44\3\uffff\5\44\6\uffff"+
            "\1\44\1\uffff\1\44\1\uffff\1\44\1\uffff\4\44\1\uffff\3\44\1"+
            "\uffff\7\44\3\uffff\3\44\5\uffff\5\44\u0ebb\uffff\2\44\52\uffff"+
            "\5\44\5\uffff\2\44\4\uffff\126\44\6\uffff\3\44\1\uffff\132\44"+
            "\1\uffff\4\44\5\uffff\50\44\4\uffff\136\44\21\uffff\30\44\70"+
            "\uffff\20\44\u0200\uffff\u19b6\44\112\uffff\u51a6\44\132\uffff"+
            "\u048d\44\u0773\uffff\u2ba4\44\u215c\uffff\u012e\44\2\uffff"+
            "\73\44\u0095\uffff\7\44\14\uffff\5\44\5\uffff\1\44\1\uffff\12"+
            "\44\1\uffff\15\44\1\uffff\5\44\1\uffff\1\44\1\uffff\2\44\1\uffff"+
            "\2\44\1\uffff\154\44\41\uffff\u016b\44\22\uffff\100\44\2\uffff"+
            "\66\44\50\uffff\14\44\164\uffff\5\44\1\uffff\u0087\44\23\uffff"+
            "\12\44\7\uffff\32\44\6\uffff\32\44\13\uffff\131\44\3\uffff\6"+
            "\44\2\uffff\6\44\2\uffff\6\44\2\uffff\3\44",
            "\1\67\3\uffff\1\116\1\uffff\12\120\5\uffff\1\70\1\uffff\4"+
            "\44\1\117\25\44\1\uffff\1\42\4\uffff\4\44\1\117\25\44\57\uffff"+
            "\1\44\12\uffff\1\44\4\uffff\1\44\5\uffff\27\44\1\uffff\37\44"+
            "\1\uffff\u013f\44\31\uffff\162\44\4\uffff\14\44\16\uffff\5\44"+
            "\11\uffff\1\44\u008b\uffff\1\44\13\uffff\1\44\1\uffff\3\44\1"+
            "\uffff\1\44\1\uffff\24\44\1\uffff\54\44\1\uffff\46\44\1\uffff"+
            "\5\44\4\uffff\u0082\44\10\uffff\105\44\1\uffff\46\44\2\uffff"+
            "\2\44\6\uffff\20\44\41\uffff\46\44\2\uffff\1\44\7\uffff\47\44"+
            "\110\uffff\33\44\5\uffff\3\44\56\uffff\32\44\5\uffff\13\44\25"+
            "\uffff\12\44\4\uffff\2\44\1\uffff\143\44\1\uffff\1\44\17\uffff"+
            "\2\44\7\uffff\17\44\2\uffff\1\44\20\uffff\1\44\1\uffff\36\44"+
            "\35\uffff\3\44\60\uffff\46\44\13\uffff\1\44\u0152\uffff\66\44"+
            "\3\uffff\1\44\22\uffff\1\44\7\uffff\12\44\4\uffff\12\44\25\uffff"+
            "\10\44\2\uffff\2\44\2\uffff\26\44\1\uffff\7\44\1\uffff\1\44"+
            "\3\uffff\4\44\3\uffff\1\44\36\uffff\2\44\1\uffff\3\44\4\uffff"+
            "\14\44\23\uffff\6\44\4\uffff\2\44\2\uffff\26\44\1\uffff\7\44"+
            "\1\uffff\2\44\1\uffff\2\44\1\uffff\2\44\37\uffff\4\44\1\uffff"+
            "\1\44\7\uffff\12\44\2\uffff\3\44\20\uffff\11\44\1\uffff\3\44"+
            "\1\uffff\26\44\1\uffff\7\44\1\uffff\2\44\1\uffff\5\44\3\uffff"+
            "\1\44\22\uffff\1\44\17\uffff\2\44\4\uffff\12\44\25\uffff\10"+
            "\44\2\uffff\2\44\2\uffff\26\44\1\uffff\7\44\1\uffff\2\44\1\uffff"+
            "\5\44\3\uffff\1\44\36\uffff\2\44\1\uffff\3\44\4\uffff\12\44"+
            "\1\uffff\1\44\21\uffff\1\44\1\uffff\6\44\3\uffff\3\44\1\uffff"+
            "\4\44\3\uffff\2\44\1\uffff\1\44\1\uffff\2\44\3\uffff\2\44\3"+
            "\uffff\3\44\3\uffff\10\44\1\uffff\3\44\55\uffff\11\44\25\uffff"+
            "\10\44\1\uffff\3\44\1\uffff\27\44\1\uffff\12\44\1\uffff\5\44"+
            "\46\uffff\2\44\4\uffff\12\44\25\uffff\10\44\1\uffff\3\44\1\uffff"+
            "\27\44\1\uffff\12\44\1\uffff\5\44\3\uffff\1\44\40\uffff\1\44"+
            "\1\uffff\2\44\4\uffff\12\44\25\uffff\10\44\1\uffff\3\44\1\uffff"+
            "\27\44\1\uffff\20\44\46\uffff\2\44\4\uffff\12\44\25\uffff\22"+
            "\44\3\uffff\30\44\1\uffff\11\44\1\uffff\1\44\2\uffff\7\44\72"+
            "\uffff\60\44\1\uffff\2\44\14\uffff\7\44\11\uffff\12\44\47\uffff"+
            "\2\44\1\uffff\1\44\2\uffff\2\44\1\uffff\1\44\2\uffff\1\44\6"+
            "\uffff\4\44\1\uffff\7\44\1\uffff\3\44\1\uffff\1\44\1\uffff\1"+
            "\44\2\uffff\2\44\1\uffff\4\44\1\uffff\2\44\11\uffff\1\44\2\uffff"+
            "\5\44\1\uffff\1\44\11\uffff\12\44\2\uffff\2\44\42\uffff\1\44"+
            "\37\uffff\12\44\26\uffff\10\44\1\uffff\42\44\35\uffff\4\44\164"+
            "\uffff\42\44\1\uffff\5\44\1\uffff\2\44\25\uffff\12\44\6\uffff"+
            "\6\44\112\uffff\46\44\12\uffff\51\44\7\uffff\132\44\5\uffff"+
            "\104\44\5\uffff\122\44\6\uffff\7\44\1\uffff\77\44\1\uffff\1"+
            "\44\1\uffff\4\44\2\uffff\7\44\1\uffff\1\44\1\uffff\4\44\2\uffff"+
            "\47\44\1\uffff\1\44\1\uffff\4\44\2\uffff\37\44\1\uffff\1\44"+
            "\1\uffff\4\44\2\uffff\7\44\1\uffff\1\44\1\uffff\4\44\2\uffff"+
            "\7\44\1\uffff\7\44\1\uffff\27\44\1\uffff\37\44\1\uffff\1\44"+
            "\1\uffff\4\44\2\uffff\7\44\1\uffff\47\44\1\uffff\23\44\16\uffff"+
            "\11\44\56\uffff\125\44\14\uffff\u026c\44\2\uffff\10\44\12\uffff"+
            "\32\44\5\uffff\113\44\25\uffff\15\44\1\uffff\4\44\16\uffff\22"+
            "\44\16\uffff\22\44\16\uffff\15\44\1\uffff\3\44\17\uffff\64\44"+
            "\43\uffff\1\44\4\uffff\1\44\3\uffff\12\44\46\uffff\12\44\6\uffff"+
            "\130\44\10\uffff\51\44\127\uffff\35\44\51\uffff\50\44\2\uffff"+
            "\5\44\u038b\uffff\154\44\u0094\uffff\u009c\44\4\uffff\132\44"+
            "\6\uffff\26\44\2\uffff\6\44\2\uffff\46\44\2\uffff\6\44\2\uffff"+
            "\10\44\1\uffff\1\44\1\uffff\1\44\1\uffff\1\44\1\uffff\37\44"+
            "\2\uffff\65\44\1\uffff\7\44\1\uffff\1\44\3\uffff\3\44\1\uffff"+
            "\7\44\3\uffff\4\44\2\uffff\6\44\4\uffff\15\44\5\uffff\3\44\1"+
            "\uffff\7\44\164\uffff\1\44\15\uffff\1\44\u0082\uffff\1\44\4"+
            "\uffff\1\44\2\uffff\12\44\1\uffff\1\44\3\uffff\5\44\6\uffff"+
            "\1\44\1\uffff\1\44\1\uffff\1\44\1\uffff\4\44\1\uffff\3\44\1"+
            "\uffff\7\44\3\uffff\3\44\5\uffff\5\44\u0ebb\uffff\2\44\52\uffff"+
            "\5\44\5\uffff\2\44\4\uffff\126\44\6\uffff\3\44\1\uffff\132\44"+
            "\1\uffff\4\44\5\uffff\50\44\4\uffff\136\44\21\uffff\30\44\70"+
            "\uffff\20\44\u0200\uffff\u19b6\44\112\uffff\u51a6\44\132\uffff"+
            "\u048d\44\u0773\uffff\u2ba4\44\u215c\uffff\u012e\44\2\uffff"+
            "\73\44\u0095\uffff\7\44\14\uffff\5\44\5\uffff\1\44\1\uffff\12"+
            "\44\1\uffff\15\44\1\uffff\5\44\1\uffff\1\44\1\uffff\2\44\1\uffff"+
            "\2\44\1\uffff\154\44\41\uffff\u016b\44\22\uffff\100\44\2\uffff"+
            "\66\44\50\uffff\14\44\164\uffff\5\44\1\uffff\u0087\44\23\uffff"+
            "\12\44\7\uffff\32\44\6\uffff\32\44\13\uffff\131\44\3\uffff\6"+
            "\44\2\uffff\6\44\2\uffff\6\44\2\uffff\3\44",
            "\1\67\5\uffff\12\143\5\uffff\1\70\1\uffff\6\143\24\44\1\uffff"+
            "\1\42\4\uffff\6\143\24\44\57\uffff\1\44\12\uffff\1\44\4\uffff"+
            "\1\44\5\uffff\27\44\1\uffff\37\44\1\uffff\u013f\44\31\uffff"+
            "\162\44\4\uffff\14\44\16\uffff\5\44\11\uffff\1\44\u008b\uffff"+
            "\1\44\13\uffff\1\44\1\uffff\3\44\1\uffff\1\44\1\uffff\24\44"+
            "\1\uffff\54\44\1\uffff\46\44\1\uffff\5\44\4\uffff\u0082\44\10"+
            "\uffff\105\44\1\uffff\46\44\2\uffff\2\44\6\uffff\20\44\41\uffff"+
            "\46\44\2\uffff\1\44\7\uffff\47\44\110\uffff\33\44\5\uffff\3"+
            "\44\56\uffff\32\44\5\uffff\13\44\25\uffff\12\44\4\uffff\2\44"+
            "\1\uffff\143\44\1\uffff\1\44\17\uffff\2\44\7\uffff\17\44\2\uffff"+
            "\1\44\20\uffff\1\44\1\uffff\36\44\35\uffff\3\44\60\uffff\46"+
            "\44\13\uffff\1\44\u0152\uffff\66\44\3\uffff\1\44\22\uffff\1"+
            "\44\7\uffff\12\44\4\uffff\12\44\25\uffff\10\44\2\uffff\2\44"+
            "\2\uffff\26\44\1\uffff\7\44\1\uffff\1\44\3\uffff\4\44\3\uffff"+
            "\1\44\36\uffff\2\44\1\uffff\3\44\4\uffff\14\44\23\uffff\6\44"+
            "\4\uffff\2\44\2\uffff\26\44\1\uffff\7\44\1\uffff\2\44\1\uffff"+
            "\2\44\1\uffff\2\44\37\uffff\4\44\1\uffff\1\44\7\uffff\12\44"+
            "\2\uffff\3\44\20\uffff\11\44\1\uffff\3\44\1\uffff\26\44\1\uffff"+
            "\7\44\1\uffff\2\44\1\uffff\5\44\3\uffff\1\44\22\uffff\1\44\17"+
            "\uffff\2\44\4\uffff\12\44\25\uffff\10\44\2\uffff\2\44\2\uffff"+
            "\26\44\1\uffff\7\44\1\uffff\2\44\1\uffff\5\44\3\uffff\1\44\36"+
            "\uffff\2\44\1\uffff\3\44\4\uffff\12\44\1\uffff\1\44\21\uffff"+
            "\1\44\1\uffff\6\44\3\uffff\3\44\1\uffff\4\44\3\uffff\2\44\1"+
            "\uffff\1\44\1\uffff\2\44\3\uffff\2\44\3\uffff\3\44\3\uffff\10"+
            "\44\1\uffff\3\44\55\uffff\11\44\25\uffff\10\44\1\uffff\3\44"+
            "\1\uffff\27\44\1\uffff\12\44\1\uffff\5\44\46\uffff\2\44\4\uffff"+
            "\12\44\25\uffff\10\44\1\uffff\3\44\1\uffff\27\44\1\uffff\12"+
            "\44\1\uffff\5\44\3\uffff\1\44\40\uffff\1\44\1\uffff\2\44\4\uffff"+
            "\12\44\25\uffff\10\44\1\uffff\3\44\1\uffff\27\44\1\uffff\20"+
            "\44\46\uffff\2\44\4\uffff\12\44\25\uffff\22\44\3\uffff\30\44"+
            "\1\uffff\11\44\1\uffff\1\44\2\uffff\7\44\72\uffff\60\44\1\uffff"+
            "\2\44\14\uffff\7\44\11\uffff\12\44\47\uffff\2\44\1\uffff\1\44"+
            "\2\uffff\2\44\1\uffff\1\44\2\uffff\1\44\6\uffff\4\44\1\uffff"+
            "\7\44\1\uffff\3\44\1\uffff\1\44\1\uffff\1\44\2\uffff\2\44\1"+
            "\uffff\4\44\1\uffff\2\44\11\uffff\1\44\2\uffff\5\44\1\uffff"+
            "\1\44\11\uffff\12\44\2\uffff\2\44\42\uffff\1\44\37\uffff\12"+
            "\44\26\uffff\10\44\1\uffff\42\44\35\uffff\4\44\164\uffff\42"+
            "\44\1\uffff\5\44\1\uffff\2\44\25\uffff\12\44\6\uffff\6\44\112"+
            "\uffff\46\44\12\uffff\51\44\7\uffff\132\44\5\uffff\104\44\5"+
            "\uffff\122\44\6\uffff\7\44\1\uffff\77\44\1\uffff\1\44\1\uffff"+
            "\4\44\2\uffff\7\44\1\uffff\1\44\1\uffff\4\44\2\uffff\47\44\1"+
            "\uffff\1\44\1\uffff\4\44\2\uffff\37\44\1\uffff\1\44\1\uffff"+
            "\4\44\2\uffff\7\44\1\uffff\1\44\1\uffff\4\44\2\uffff\7\44\1"+
            "\uffff\7\44\1\uffff\27\44\1\uffff\37\44\1\uffff\1\44\1\uffff"+
            "\4\44\2\uffff\7\44\1\uffff\47\44\1\uffff\23\44\16\uffff\11\44"+
            "\56\uffff\125\44\14\uffff\u026c\44\2\uffff\10\44\12\uffff\32"+
            "\44\5\uffff\113\44\25\uffff\15\44\1\uffff\4\44\16\uffff\22\44"+
            "\16\uffff\22\44\16\uffff\15\44\1\uffff\3\44\17\uffff\64\44\43"+
            "\uffff\1\44\4\uffff\1\44\3\uffff\12\44\46\uffff\12\44\6\uffff"+
            "\130\44\10\uffff\51\44\127\uffff\35\44\51\uffff\50\44\2\uffff"+
            "\5\44\u038b\uffff\154\44\u0094\uffff\u009c\44\4\uffff\132\44"+
            "\6\uffff\26\44\2\uffff\6\44\2\uffff\46\44\2\uffff\6\44\2\uffff"+
            "\10\44\1\uffff\1\44\1\uffff\1\44\1\uffff\1\44\1\uffff\37\44"+
            "\2\uffff\65\44\1\uffff\7\44\1\uffff\1\44\3\uffff\3\44\1\uffff"+
            "\7\44\3\uffff\4\44\2\uffff\6\44\4\uffff\15\44\5\uffff\3\44\1"+
            "\uffff\7\44\164\uffff\1\44\15\uffff\1\44\u0082\uffff\1\44\4"+
            "\uffff\1\44\2\uffff\12\44\1\uffff\1\44\3\uffff\5\44\6\uffff"+
            "\1\44\1\uffff\1\44\1\uffff\1\44\1\uffff\4\44\1\uffff\3\44\1"+
            "\uffff\7\44\3\uffff\3\44\5\uffff\5\44\u0ebb\uffff\2\44\52\uffff"+
            "\5\44\5\uffff\2\44\4\uffff\126\44\6\uffff\3\44\1\uffff\132\44"+
            "\1\uffff\4\44\5\uffff\50\44\4\uffff\136\44\21\uffff\30\44\70"+
            "\uffff\20\44\u0200\uffff\u19b6\44\112\uffff\u51a6\44\132\uffff"+
            "\u048d\44\u0773\uffff\u2ba4\44\u215c\uffff\u012e\44\2\uffff"+
            "\73\44\u0095\uffff\7\44\14\uffff\5\44\5\uffff\1\44\1\uffff\12"+
            "\44\1\uffff\15\44\1\uffff\5\44\1\uffff\1\44\1\uffff\2\44\1\uffff"+
            "\2\44\1\uffff\154\44\41\uffff\u016b\44\22\uffff\100\44\2\uffff"+
            "\66\44\50\uffff\14\44\164\uffff\5\44\1\uffff\u0087\44\23\uffff"+
            "\12\44\7\uffff\32\44\6\uffff\32\44\13\uffff\131\44\3\uffff\6"+
            "\44\2\uffff\6\44\2\uffff\6\44\2\uffff\3\44",
            "\1\67\5\uffff\12\44\5\uffff\1\70\1\uffff\32\44\1\uffff\1\42"+
            "\4\uffff\32\44\57\uffff\1\44\12\uffff\1\44\4\uffff\1\44\5\uffff"+
            "\27\44\1\uffff\37\44\1\uffff\u013f\44\31\uffff\162\44\4\uffff"+
            "\14\44\16\uffff\5\44\11\uffff\1\44\u008b\uffff\1\44\13\uffff"+
            "\1\44\1\uffff\3\44\1\uffff\1\44\1\uffff\24\44\1\uffff\54\44"+
            "\1\uffff\46\44\1\uffff\5\44\4\uffff\u0082\44\10\uffff\105\44"+
            "\1\uffff\46\44\2\uffff\2\44\6\uffff\20\44\41\uffff\46\44\2\uffff"+
            "\1\44\7\uffff\47\44\110\uffff\33\44\5\uffff\3\44\56\uffff\32"+
            "\44\5\uffff\13\44\25\uffff\12\44\4\uffff\2\44\1\uffff\143\44"+
            "\1\uffff\1\44\17\uffff\2\44\7\uffff\17\44\2\uffff\1\44\20\uffff"+
            "\1\44\1\uffff\36\44\35\uffff\3\44\60\uffff\46\44\13\uffff\1"+
            "\44\u0152\uffff\66\44\3\uffff\1\44\22\uffff\1\44\7\uffff\12"+
            "\44\4\uffff\12\44\25\uffff\10\44\2\uffff\2\44\2\uffff\26\44"+
            "\1\uffff\7\44\1\uffff\1\44\3\uffff\4\44\3\uffff\1\44\36\uffff"+
            "\2\44\1\uffff\3\44\4\uffff\14\44\23\uffff\6\44\4\uffff\2\44"+
            "\2\uffff\26\44\1\uffff\7\44\1\uffff\2\44\1\uffff\2\44\1\uffff"+
            "\2\44\37\uffff\4\44\1\uffff\1\44\7\uffff\12\44\2\uffff\3\44"+
            "\20\uffff\11\44\1\uffff\3\44\1\uffff\26\44\1\uffff\7\44\1\uffff"+
            "\2\44\1\uffff\5\44\3\uffff\1\44\22\uffff\1\44\17\uffff\2\44"+
            "\4\uffff\12\44\25\uffff\10\44\2\uffff\2\44\2\uffff\26\44\1\uffff"+
            "\7\44\1\uffff\2\44\1\uffff\5\44\3\uffff\1\44\36\uffff\2\44\1"+
            "\uffff\3\44\4\uffff\12\44\1\uffff\1\44\21\uffff\1\44\1\uffff"+
            "\6\44\3\uffff\3\44\1\uffff\4\44\3\uffff\2\44\1\uffff\1\44\1"+
            "\uffff\2\44\3\uffff\2\44\3\uffff\3\44\3\uffff\10\44\1\uffff"+
            "\3\44\55\uffff\11\44\25\uffff\10\44\1\uffff\3\44\1\uffff\27"+
            "\44\1\uffff\12\44\1\uffff\5\44\46\uffff\2\44\4\uffff\12\44\25"+
            "\uffff\10\44\1\uffff\3\44\1\uffff\27\44\1\uffff\12\44\1\uffff"+
            "\5\44\3\uffff\1\44\40\uffff\1\44\1\uffff\2\44\4\uffff\12\44"+
            "\25\uffff\10\44\1\uffff\3\44\1\uffff\27\44\1\uffff\20\44\46"+
            "\uffff\2\44\4\uffff\12\44\25\uffff\22\44\3\uffff\30\44\1\uffff"+
            "\11\44\1\uffff\1\44\2\uffff\7\44\72\uffff\60\44\1\uffff\2\44"+
            "\14\uffff\7\44\11\uffff\12\44\47\uffff\2\44\1\uffff\1\44\2\uffff"+
            "\2\44\1\uffff\1\44\2\uffff\1\44\6\uffff\4\44\1\uffff\7\44\1"+
            "\uffff\3\44\1\uffff\1\44\1\uffff\1\44\2\uffff\2\44\1\uffff\4"+
            "\44\1\uffff\2\44\11\uffff\1\44\2\uffff\5\44\1\uffff\1\44\11"+
            "\uffff\12\44\2\uffff\2\44\42\uffff\1\44\37\uffff\12\44\26\uffff"+
            "\10\44\1\uffff\42\44\35\uffff\4\44\164\uffff\42\44\1\uffff\5"+
            "\44\1\uffff\2\44\25\uffff\12\44\6\uffff\6\44\112\uffff\46\44"+
            "\12\uffff\51\44\7\uffff\132\44\5\uffff\104\44\5\uffff\122\44"+
            "\6\uffff\7\44\1\uffff\77\44\1\uffff\1\44\1\uffff\4\44\2\uffff"+
            "\7\44\1\uffff\1\44\1\uffff\4\44\2\uffff\47\44\1\uffff\1\44\1"+
            "\uffff\4\44\2\uffff\37\44\1\uffff\1\44\1\uffff\4\44\2\uffff"+
            "\7\44\1\uffff\1\44\1\uffff\4\44\2\uffff\7\44\1\uffff\7\44\1"+
            "\uffff\27\44\1\uffff\37\44\1\uffff\1\44\1\uffff\4\44\2\uffff"+
            "\7\44\1\uffff\47\44\1\uffff\23\44\16\uffff\11\44\56\uffff\125"+
            "\44\14\uffff\u026c\44\2\uffff\10\44\12\uffff\32\44\5\uffff\113"+
            "\44\25\uffff\15\44\1\uffff\4\44\16\uffff\22\44\16\uffff\22\44"+
            "\16\uffff\15\44\1\uffff\3\44\17\uffff\64\44\43\uffff\1\44\4"+
            "\uffff\1\44\3\uffff\12\44\46\uffff\12\44\6\uffff\130\44\10\uffff"+
            "\51\44\127\uffff\35\44\51\uffff\50\44\2\uffff\5\44\u038b\uffff"+
            "\154\44\u0094\uffff\u009c\44\4\uffff\132\44\6\uffff\26\44\2"+
            "\uffff\6\44\2\uffff\46\44\2\uffff\6\44\2\uffff\10\44\1\uffff"+
            "\1\44\1\uffff\1\44\1\uffff\1\44\1\uffff\37\44\2\uffff\65\44"+
            "\1\uffff\7\44\1\uffff\1\44\3\uffff\3\44\1\uffff\7\44\3\uffff"+
            "\4\44\2\uffff\6\44\4\uffff\15\44\5\uffff\3\44\1\uffff\7\44\164"+
            "\uffff\1\44\15\uffff\1\44\u0082\uffff\1\44\4\uffff\1\44\2\uffff"+
            "\12\44\1\uffff\1\44\3\uffff\5\44\6\uffff\1\44\1\uffff\1\44\1"+
            "\uffff\1\44\1\uffff\4\44\1\uffff\3\44\1\uffff\7\44\3\uffff\3"+
            "\44\5\uffff\5\44\u0ebb\uffff\2\44\52\uffff\5\44\5\uffff\2\44"+
            "\4\uffff\126\44\6\uffff\3\44\1\uffff\132\44\1\uffff\4\44\5\uffff"+
            "\50\44\4\uffff\136\44\21\uffff\30\44\70\uffff\20\44\u0200\uffff"+
            "\u19b6\44\112\uffff\u51a6\44\132\uffff\u048d\44\u0773\uffff"+
            "\u2ba4\44\u215c\uffff\u012e\44\2\uffff\73\44\u0095\uffff\7\44"+
            "\14\uffff\5\44\5\uffff\1\44\1\uffff\12\44\1\uffff\15\44\1\uffff"+
            "\5\44\1\uffff\1\44\1\uffff\2\44\1\uffff\2\44\1\uffff\154\44"+
            "\41\uffff\u016b\44\22\uffff\100\44\2\uffff\66\44\50\uffff\14"+
            "\44\164\uffff\5\44\1\uffff\u0087\44\23\uffff\12\44\7\uffff\32"+
            "\44\6\uffff\32\44\13\uffff\131\44\3\uffff\6\44\2\uffff\6\44"+
            "\2\uffff\6\44\2\uffff\3\44",
            "",
            "",
            "\1\64\1\63\5\uffff\1\67\5\uffff\12\146\5\uffff\1\70\1\uffff"+
            "\6\145\24\60\1\uffff\1\65\2\uffff\1\62\1\uffff\6\144\24\57\57"+
            "\uffff\1\44\12\uffff\1\44\4\uffff\1\44\5\uffff\27\44\1\uffff"+
            "\37\44\1\uffff\u013f\44\31\uffff\162\44\4\uffff\14\44\16\uffff"+
            "\5\44\11\uffff\1\44\u008b\uffff\1\44\13\uffff\1\44\1\uffff\3"+
            "\44\1\uffff\1\44\1\uffff\24\44\1\uffff\54\44\1\uffff\46\44\1"+
            "\uffff\5\44\4\uffff\u0082\44\10\uffff\105\44\1\uffff\46\44\2"+
            "\uffff\2\44\6\uffff\20\44\41\uffff\46\44\2\uffff\1\44\7\uffff"+
            "\47\44\110\uffff\33\44\5\uffff\3\44\56\uffff\32\44\5\uffff\13"+
            "\44\25\uffff\12\44\4\uffff\2\44\1\uffff\143\44\1\uffff\1\44"+
            "\17\uffff\2\44\7\uffff\17\44\2\uffff\1\44\20\uffff\1\44\1\uffff"+
            "\36\44\35\uffff\3\44\60\uffff\46\44\13\uffff\1\44\u0152\uffff"+
            "\66\44\3\uffff\1\44\22\uffff\1\44\7\uffff\12\44\4\uffff\12\44"+
            "\25\uffff\10\44\2\uffff\2\44\2\uffff\26\44\1\uffff\7\44\1\uffff"+
            "\1\44\3\uffff\4\44\3\uffff\1\44\36\uffff\2\44\1\uffff\3\44\4"+
            "\uffff\14\44\23\uffff\6\44\4\uffff\2\44\2\uffff\26\44\1\uffff"+
            "\7\44\1\uffff\2\44\1\uffff\2\44\1\uffff\2\44\37\uffff\4\44\1"+
            "\uffff\1\44\7\uffff\12\44\2\uffff\3\44\20\uffff\11\44\1\uffff"+
            "\3\44\1\uffff\26\44\1\uffff\7\44\1\uffff\2\44\1\uffff\5\44\3"+
            "\uffff\1\44\22\uffff\1\44\17\uffff\2\44\4\uffff\12\44\25\uffff"+
            "\10\44\2\uffff\2\44\2\uffff\26\44\1\uffff\7\44\1\uffff\2\44"+
            "\1\uffff\5\44\3\uffff\1\44\36\uffff\2\44\1\uffff\3\44\4\uffff"+
            "\12\44\1\uffff\1\44\21\uffff\1\44\1\uffff\6\44\3\uffff\3\44"+
            "\1\uffff\4\44\3\uffff\2\44\1\uffff\1\44\1\uffff\2\44\3\uffff"+
            "\2\44\3\uffff\3\44\3\uffff\10\44\1\uffff\3\44\55\uffff\11\44"+
            "\25\uffff\10\44\1\uffff\3\44\1\uffff\27\44\1\uffff\12\44\1\uffff"+
            "\5\44\46\uffff\2\44\4\uffff\12\44\25\uffff\10\44\1\uffff\3\44"+
            "\1\uffff\27\44\1\uffff\12\44\1\uffff\5\44\3\uffff\1\44\40\uffff"+
            "\1\44\1\uffff\2\44\4\uffff\12\44\25\uffff\10\44\1\uffff\3\44"+
            "\1\uffff\27\44\1\uffff\20\44\46\uffff\2\44\4\uffff\12\44\25"+
            "\uffff\22\44\3\uffff\30\44\1\uffff\11\44\1\uffff\1\44\2\uffff"+
            "\7\44\72\uffff\60\44\1\uffff\2\44\14\uffff\7\44\11\uffff\12"+
            "\44\47\uffff\2\44\1\uffff\1\44\2\uffff\2\44\1\uffff\1\44\2\uffff"+
            "\1\44\6\uffff\4\44\1\uffff\7\44\1\uffff\3\44\1\uffff\1\44\1"+
            "\uffff\1\44\2\uffff\2\44\1\uffff\4\44\1\uffff\2\44\11\uffff"+
            "\1\44\2\uffff\5\44\1\uffff\1\44\11\uffff\12\44\2\uffff\2\44"+
            "\42\uffff\1\44\37\uffff\12\44\26\uffff\10\44\1\uffff\42\44\35"+
            "\uffff\4\44\164\uffff\42\44\1\uffff\5\44\1\uffff\2\44\25\uffff"+
            "\12\44\6\uffff\6\44\112\uffff\46\44\12\uffff\51\44\7\uffff\132"+
            "\44\5\uffff\104\44\5\uffff\122\44\6\uffff\7\44\1\uffff\77\44"+
            "\1\uffff\1\44\1\uffff\4\44\2\uffff\7\44\1\uffff\1\44\1\uffff"+
            "\4\44\2\uffff\47\44\1\uffff\1\44\1\uffff\4\44\2\uffff\37\44"+
            "\1\uffff\1\44\1\uffff\4\44\2\uffff\7\44\1\uffff\1\44\1\uffff"+
            "\4\44\2\uffff\7\44\1\uffff\7\44\1\uffff\27\44\1\uffff\37\44"+
            "\1\uffff\1\44\1\uffff\4\44\2\uffff\7\44\1\uffff\47\44\1\uffff"+
            "\23\44\16\uffff\11\44\56\uffff\125\44\14\uffff\u026c\44\2\uffff"+
            "\10\44\12\uffff\32\44\5\uffff\113\44\25\uffff\15\44\1\uffff"+
            "\4\44\16\uffff\22\44\16\uffff\22\44\16\uffff\15\44\1\uffff\3"+
            "\44\17\uffff\64\44\43\uffff\1\44\4\uffff\1\44\3\uffff\12\44"+
            "\46\uffff\12\44\6\uffff\130\44\10\uffff\51\44\127\uffff\35\44"+
            "\51\uffff\50\44\2\uffff\5\44\u038b\uffff\154\44\u0094\uffff"+
            "\u009c\44\4\uffff\132\44\6\uffff\26\44\2\uffff\6\44\2\uffff"+
            "\46\44\2\uffff\6\44\2\uffff\10\44\1\uffff\1\44\1\uffff\1\44"+
            "\1\uffff\1\44\1\uffff\37\44\2\uffff\65\44\1\uffff\7\44\1\uffff"+
            "\1\44\3\uffff\3\44\1\uffff\7\44\3\uffff\4\44\2\uffff\6\44\4"+
            "\uffff\15\44\5\uffff\3\44\1\uffff\7\44\164\uffff\1\44\15\uffff"+
            "\1\44\u0082\uffff\1\44\4\uffff\1\44\2\uffff\12\44\1\uffff\1"+
            "\44\3\uffff\5\44\6\uffff\1\44\1\uffff\1\44\1\uffff\1\44\1\uffff"+
            "\4\44\1\uffff\3\44\1\uffff\7\44\3\uffff\3\44\5\uffff\5\44\u0ebb"+
            "\uffff\2\44\52\uffff\5\44\5\uffff\2\44\4\uffff\126\44\6\uffff"+
            "\3\44\1\uffff\132\44\1\uffff\4\44\5\uffff\50\44\4\uffff\136"+
            "\44\21\uffff\30\44\70\uffff\20\44\u0200\uffff\u19b6\44\112\uffff"+
            "\u51a6\44\132\uffff\u048d\44\u0773\uffff\u2ba4\44\u215c\uffff"+
            "\u012e\44\2\uffff\73\44\u0095\uffff\7\44\14\uffff\5\44\5\uffff"+
            "\1\44\1\uffff\12\44\1\uffff\15\44\1\uffff\5\44\1\uffff\1\44"+
            "\1\uffff\2\44\1\uffff\2\44\1\uffff\154\44\41\uffff\u016b\44"+
            "\22\uffff\100\44\2\uffff\66\44\50\uffff\14\44\164\uffff\5\44"+
            "\1\uffff\u0087\44\23\uffff\12\44\7\uffff\32\44\6\uffff\32\44"+
            "\13\uffff\131\44\3\uffff\6\44\2\uffff\6\44\2\uffff\6\44\2\uffff"+
            "\3\44",
            "\1\64\1\63\5\uffff\1\67\5\uffff\12\61\5\uffff\1\70\1\uffff"+
            "\32\60\1\uffff\1\65\2\uffff\1\62\1\uffff\32\57\57\uffff\1\44"+
            "\12\uffff\1\44\4\uffff\1\44\5\uffff\27\44\1\uffff\37\44\1\uffff"+
            "\u013f\44\31\uffff\162\44\4\uffff\14\44\16\uffff\5\44\11\uffff"+
            "\1\44\u008b\uffff\1\44\13\uffff\1\44\1\uffff\3\44\1\uffff\1"+
            "\44\1\uffff\24\44\1\uffff\54\44\1\uffff\46\44\1\uffff\5\44\4"+
            "\uffff\u0082\44\10\uffff\105\44\1\uffff\46\44\2\uffff\2\44\6"+
            "\uffff\20\44\41\uffff\46\44\2\uffff\1\44\7\uffff\47\44\110\uffff"+
            "\33\44\5\uffff\3\44\56\uffff\32\44\5\uffff\13\44\25\uffff\12"+
            "\44\4\uffff\2\44\1\uffff\143\44\1\uffff\1\44\17\uffff\2\44\7"+
            "\uffff\17\44\2\uffff\1\44\20\uffff\1\44\1\uffff\36\44\35\uffff"+
            "\3\44\60\uffff\46\44\13\uffff\1\44\u0152\uffff\66\44\3\uffff"+
            "\1\44\22\uffff\1\44\7\uffff\12\44\4\uffff\12\44\25\uffff\10"+
            "\44\2\uffff\2\44\2\uffff\26\44\1\uffff\7\44\1\uffff\1\44\3\uffff"+
            "\4\44\3\uffff\1\44\36\uffff\2\44\1\uffff\3\44\4\uffff\14\44"+
            "\23\uffff\6\44\4\uffff\2\44\2\uffff\26\44\1\uffff\7\44\1\uffff"+
            "\2\44\1\uffff\2\44\1\uffff\2\44\37\uffff\4\44\1\uffff\1\44\7"+
            "\uffff\12\44\2\uffff\3\44\20\uffff\11\44\1\uffff\3\44\1\uffff"+
            "\26\44\1\uffff\7\44\1\uffff\2\44\1\uffff\5\44\3\uffff\1\44\22"+
            "\uffff\1\44\17\uffff\2\44\4\uffff\12\44\25\uffff\10\44\2\uffff"+
            "\2\44\2\uffff\26\44\1\uffff\7\44\1\uffff\2\44\1\uffff\5\44\3"+
            "\uffff\1\44\36\uffff\2\44\1\uffff\3\44\4\uffff\12\44\1\uffff"+
            "\1\44\21\uffff\1\44\1\uffff\6\44\3\uffff\3\44\1\uffff\4\44\3"+
            "\uffff\2\44\1\uffff\1\44\1\uffff\2\44\3\uffff\2\44\3\uffff\3"+
            "\44\3\uffff\10\44\1\uffff\3\44\55\uffff\11\44\25\uffff\10\44"+
            "\1\uffff\3\44\1\uffff\27\44\1\uffff\12\44\1\uffff\5\44\46\uffff"+
            "\2\44\4\uffff\12\44\25\uffff\10\44\1\uffff\3\44\1\uffff\27\44"+
            "\1\uffff\12\44\1\uffff\5\44\3\uffff\1\44\40\uffff\1\44\1\uffff"+
            "\2\44\4\uffff\12\44\25\uffff\10\44\1\uffff\3\44\1\uffff\27\44"+
            "\1\uffff\20\44\46\uffff\2\44\4\uffff\12\44\25\uffff\22\44\3"+
            "\uffff\30\44\1\uffff\11\44\1\uffff\1\44\2\uffff\7\44\72\uffff"+
            "\60\44\1\uffff\2\44\14\uffff\7\44\11\uffff\12\44\47\uffff\2"+
            "\44\1\uffff\1\44\2\uffff\2\44\1\uffff\1\44\2\uffff\1\44\6\uffff"+
            "\4\44\1\uffff\7\44\1\uffff\3\44\1\uffff\1\44\1\uffff\1\44\2"+
            "\uffff\2\44\1\uffff\4\44\1\uffff\2\44\11\uffff\1\44\2\uffff"+
            "\5\44\1\uffff\1\44\11\uffff\12\44\2\uffff\2\44\42\uffff\1\44"+
            "\37\uffff\12\44\26\uffff\10\44\1\uffff\42\44\35\uffff\4\44\164"+
            "\uffff\42\44\1\uffff\5\44\1\uffff\2\44\25\uffff\12\44\6\uffff"+
            "\6\44\112\uffff\46\44\12\uffff\51\44\7\uffff\132\44\5\uffff"+
            "\104\44\5\uffff\122\44\6\uffff\7\44\1\uffff\77\44\1\uffff\1"+
            "\44\1\uffff\4\44\2\uffff\7\44\1\uffff\1\44\1\uffff\4\44\2\uffff"+
            "\47\44\1\uffff\1\44\1\uffff\4\44\2\uffff\37\44\1\uffff\1\44"+
            "\1\uffff\4\44\2\uffff\7\44\1\uffff\1\44\1\uffff\4\44\2\uffff"+
            "\7\44\1\uffff\7\44\1\uffff\27\44\1\uffff\37\44\1\uffff\1\44"+
            "\1\uffff\4\44\2\uffff\7\44\1\uffff\47\44\1\uffff\23\44\16\uffff"+
            "\11\44\56\uffff\125\44\14\uffff\u026c\44\2\uffff\10\44\12\uffff"+
            "\32\44\5\uffff\113\44\25\uffff\15\44\1\uffff\4\44\16\uffff\22"+
            "\44\16\uffff\22\44\16\uffff\15\44\1\uffff\3\44\17\uffff\64\44"+
            "\43\uffff\1\44\4\uffff\1\44\3\uffff\12\44\46\uffff\12\44\6\uffff"+
            "\130\44\10\uffff\51\44\127\uffff\35\44\51\uffff\50\44\2\uffff"+
            "\5\44\u038b\uffff\154\44\u0094\uffff\u009c\44\4\uffff\132\44"+
            "\6\uffff\26\44\2\uffff\6\44\2\uffff\46\44\2\uffff\6\44\2\uffff"+
            "\10\44\1\uffff\1\44\1\uffff\1\44\1\uffff\1\44\1\uffff\37\44"+
            "\2\uffff\65\44\1\uffff\7\44\1\uffff\1\44\3\uffff\3\44\1\uffff"+
            "\7\44\3\uffff\4\44\2\uffff\6\44\4\uffff\15\44\5\uffff\3\44\1"+
            "\uffff\7\44\164\uffff\1\44\15\uffff\1\44\u0082\uffff\1\44\4"+
            "\uffff\1\44\2\uffff\12\44\1\uffff\1\44\3\uffff\5\44\6\uffff"+
            "\1\44\1\uffff\1\44\1\uffff\1\44\1\uffff\4\44\1\uffff\3\44\1"+
            "\uffff\7\44\3\uffff\3\44\5\uffff\5\44\u0ebb\uffff\2\44\52\uffff"+
            "\5\44\5\uffff\2\44\4\uffff\126\44\6\uffff\3\44\1\uffff\132\44"+
            "\1\uffff\4\44\5\uffff\50\44\4\uffff\136\44\21\uffff\30\44\70"+
            "\uffff\20\44\u0200\uffff\u19b6\44\112\uffff\u51a6\44\132\uffff"+
            "\u048d\44\u0773\uffff\u2ba4\44\u215c\uffff\u012e\44\2\uffff"+
            "\73\44\u0095\uffff\7\44\14\uffff\5\44\5\uffff\1\44\1\uffff\12"+
            "\44\1\uffff\15\44\1\uffff\5\44\1\uffff\1\44\1\uffff\2\44\1\uffff"+
            "\2\44\1\uffff\154\44\41\uffff\u016b\44\22\uffff\100\44\2\uffff"+
            "\66\44\50\uffff\14\44\164\uffff\5\44\1\uffff\u0087\44\23\uffff"+
            "\12\44\7\uffff\32\44\6\uffff\32\44\13\uffff\131\44\3\uffff\6"+
            "\44\2\uffff\6\44\2\uffff\6\44\2\uffff\3\44",
            "",
            "\1\64\1\63\5\uffff\1\67\5\uffff\12\61\5\uffff\1\70\1\uffff"+
            "\32\60\1\uffff\1\65\2\uffff\1\62\1\uffff\32\57\57\uffff\1\44"+
            "\12\uffff\1\44\4\uffff\1\44\5\uffff\27\44\1\uffff\37\44\1\uffff"+
            "\u013f\44\31\uffff\162\44\4\uffff\14\44\16\uffff\5\44\11\uffff"+
            "\1\44\u008b\uffff\1\44\13\uffff\1\44\1\uffff\3\44\1\uffff\1"+
            "\44\1\uffff\24\44\1\uffff\54\44\1\uffff\46\44\1\uffff\5\44\4"+
            "\uffff\u0082\44\10\uffff\105\44\1\uffff\46\44\2\uffff\2\44\6"+
            "\uffff\20\44\41\uffff\46\44\2\uffff\1\44\7\uffff\47\44\110\uffff"+
            "\33\44\5\uffff\3\44\56\uffff\32\44\5\uffff\13\44\25\uffff\12"+
            "\44\4\uffff\2\44\1\uffff\143\44\1\uffff\1\44\17\uffff\2\44\7"+
            "\uffff\17\44\2\uffff\1\44\20\uffff\1\44\1\uffff\36\44\35\uffff"+
            "\3\44\60\uffff\46\44\13\uffff\1\44\u0152\uffff\66\44\3\uffff"+
            "\1\44\22\uffff\1\44\7\uffff\12\44\4\uffff\12\44\25\uffff\10"+
            "\44\2\uffff\2\44\2\uffff\26\44\1\uffff\7\44\1\uffff\1\44\3\uffff"+
            "\4\44\3\uffff\1\44\36\uffff\2\44\1\uffff\3\44\4\uffff\14\44"+
            "\23\uffff\6\44\4\uffff\2\44\2\uffff\26\44\1\uffff\7\44\1\uffff"+
            "\2\44\1\uffff\2\44\1\uffff\2\44\37\uffff\4\44\1\uffff\1\44\7"+
            "\uffff\12\44\2\uffff\3\44\20\uffff\11\44\1\uffff\3\44\1\uffff"+
            "\26\44\1\uffff\7\44\1\uffff\2\44\1\uffff\5\44\3\uffff\1\44\22"+
            "\uffff\1\44\17\uffff\2\44\4\uffff\12\44\25\uffff\10\44\2\uffff"+
            "\2\44\2\uffff\26\44\1\uffff\7\44\1\uffff\2\44\1\uffff\5\44\3"+
            "\uffff\1\44\36\uffff\2\44\1\uffff\3\44\4\uffff\12\44\1\uffff"+
            "\1\44\21\uffff\1\44\1\uffff\6\44\3\uffff\3\44\1\uffff\4\44\3"+
            "\uffff\2\44\1\uffff\1\44\1\uffff\2\44\3\uffff\2\44\3\uffff\3"+
            "\44\3\uffff\10\44\1\uffff\3\44\55\uffff\11\44\25\uffff\10\44"+
            "\1\uffff\3\44\1\uffff\27\44\1\uffff\12\44\1\uffff\5\44\46\uffff"+
            "\2\44\4\uffff\12\44\25\uffff\10\44\1\uffff\3\44\1\uffff\27\44"+
            "\1\uffff\12\44\1\uffff\5\44\3\uffff\1\44\40\uffff\1\44\1\uffff"+
            "\2\44\4\uffff\12\44\25\uffff\10\44\1\uffff\3\44\1\uffff\27\44"+
            "\1\uffff\20\44\46\uffff\2\44\4\uffff\12\44\25\uffff\22\44\3"+
            "\uffff\30\44\1\uffff\11\44\1\uffff\1\44\2\uffff\7\44\72\uffff"+
            "\60\44\1\uffff\2\44\14\uffff\7\44\11\uffff\12\44\47\uffff\2"+
            "\44\1\uffff\1\44\2\uffff\2\44\1\uffff\1\44\2\uffff\1\44\6\uffff"+
            "\4\44\1\uffff\7\44\1\uffff\3\44\1\uffff\1\44\1\uffff\1\44\2"+
            "\uffff\2\44\1\uffff\4\44\1\uffff\2\44\11\uffff\1\44\2\uffff"+
            "\5\44\1\uffff\1\44\11\uffff\12\44\2\uffff\2\44\42\uffff\1\44"+
            "\37\uffff\12\44\26\uffff\10\44\1\uffff\42\44\35\uffff\4\44\164"+
            "\uffff\42\44\1\uffff\5\44\1\uffff\2\44\25\uffff\12\44\6\uffff"+
            "\6\44\112\uffff\46\44\12\uffff\51\44\7\uffff\132\44\5\uffff"+
            "\104\44\5\uffff\122\44\6\uffff\7\44\1\uffff\77\44\1\uffff\1"+
            "\44\1\uffff\4\44\2\uffff\7\44\1\uffff\1\44\1\uffff\4\44\2\uffff"+
            "\47\44\1\uffff\1\44\1\uffff\4\44\2\uffff\37\44\1\uffff\1\44"+
            "\1\uffff\4\44\2\uffff\7\44\1\uffff\1\44\1\uffff\4\44\2\uffff"+
            "\7\44\1\uffff\7\44\1\uffff\27\44\1\uffff\37\44\1\uffff\1\44"+
            "\1\uffff\4\44\2\uffff\7\44\1\uffff\47\44\1\uffff\23\44\16\uffff"+
            "\11\44\56\uffff\125\44\14\uffff\u026c\44\2\uffff\10\44\12\uffff"+
            "\32\44\5\uffff\113\44\25\uffff\15\44\1\uffff\4\44\16\uffff\22"+
            "\44\16\uffff\22\44\16\uffff\15\44\1\uffff\3\44\17\uffff\64\44"+
            "\43\uffff\1\44\4\uffff\1\44\3\uffff\12\44\46\uffff\12\44\6\uffff"+
            "\130\44\10\uffff\51\44\127\uffff\35\44\51\uffff\50\44\2\uffff"+
            "\5\44\u038b\uffff\154\44\u0094\uffff\u009c\44\4\uffff\132\44"+
            "\6\uffff\26\44\2\uffff\6\44\2\uffff\46\44\2\uffff\6\44\2\uffff"+
            "\10\44\1\uffff\1\44\1\uffff\1\44\1\uffff\1\44\1\uffff\37\44"+
            "\2\uffff\65\44\1\uffff\7\44\1\uffff\1\44\3\uffff\3\44\1\uffff"+
            "\7\44\3\uffff\4\44\2\uffff\6\44\4\uffff\15\44\5\uffff\3\44\1"+
            "\uffff\7\44\164\uffff\1\44\15\uffff\1\44\u0082\uffff\1\44\4"+
            "\uffff\1\44\2\uffff\12\44\1\uffff\1\44\3\uffff\5\44\6\uffff"+
            "\1\44\1\uffff\1\44\1\uffff\1\44\1\uffff\4\44\1\uffff\3\44\1"+
            "\uffff\7\44\3\uffff\3\44\5\uffff\5\44\u0ebb\uffff\2\44\52\uffff"+
            "\5\44\5\uffff\2\44\4\uffff\126\44\6\uffff\3\44\1\uffff\132\44"+
            "\1\uffff\4\44\5\uffff\50\44\4\uffff\136\44\21\uffff\30\44\70"+
            "\uffff\20\44\u0200\uffff\u19b6\44\112\uffff\u51a6\44\132\uffff"+
            "\u048d\44\u0773\uffff\u2ba4\44\u215c\uffff\u012e\44\2\uffff"+
            "\73\44\u0095\uffff\7\44\14\uffff\5\44\5\uffff\1\44\1\uffff\12"+
            "\44\1\uffff\15\44\1\uffff\5\44\1\uffff\1\44\1\uffff\2\44\1\uffff"+
            "\2\44\1\uffff\154\44\41\uffff\u016b\44\22\uffff\100\44\2\uffff"+
            "\66\44\50\uffff\14\44\164\uffff\5\44\1\uffff\u0087\44\23\uffff"+
            "\12\44\7\uffff\32\44\6\uffff\32\44\13\uffff\131\44\3\uffff\6"+
            "\44\2\uffff\6\44\2\uffff\6\44\2\uffff\3\44",
            "\1\64\1\63\5\uffff\1\67\5\uffff\12\61\5\uffff\1\70\1\uffff"+
            "\32\60\1\uffff\1\65\2\uffff\1\62\1\uffff\32\57\57\uffff\1\44"+
            "\12\uffff\1\44\4\uffff\1\44\5\uffff\27\44\1\uffff\37\44\1\uffff"+
            "\u013f\44\31\uffff\162\44\4\uffff\14\44\16\uffff\5\44\11\uffff"+
            "\1\44\u008b\uffff\1\44\13\uffff\1\44\1\uffff\3\44\1\uffff\1"+
            "\44\1\uffff\24\44\1\uffff\54\44\1\uffff\46\44\1\uffff\5\44\4"+
            "\uffff\u0082\44\10\uffff\105\44\1\uffff\46\44\2\uffff\2\44\6"+
            "\uffff\20\44\41\uffff\46\44\2\uffff\1\44\7\uffff\47\44\110\uffff"+
            "\33\44\5\uffff\3\44\56\uffff\32\44\5\uffff\13\44\25\uffff\12"+
            "\44\4\uffff\2\44\1\uffff\143\44\1\uffff\1\44\17\uffff\2\44\7"+
            "\uffff\17\44\2\uffff\1\44\20\uffff\1\44\1\uffff\36\44\35\uffff"+
            "\3\44\60\uffff\46\44\13\uffff\1\44\u0152\uffff\66\44\3\uffff"+
            "\1\44\22\uffff\1\44\7\uffff\12\44\4\uffff\12\44\25\uffff\10"+
            "\44\2\uffff\2\44\2\uffff\26\44\1\uffff\7\44\1\uffff\1\44\3\uffff"+
            "\4\44\3\uffff\1\44\36\uffff\2\44\1\uffff\3\44\4\uffff\14\44"+
            "\23\uffff\6\44\4\uffff\2\44\2\uffff\26\44\1\uffff\7\44\1\uffff"+
            "\2\44\1\uffff\2\44\1\uffff\2\44\37\uffff\4\44\1\uffff\1\44\7"+
            "\uffff\12\44\2\uffff\3\44\20\uffff\11\44\1\uffff\3\44\1\uffff"+
            "\26\44\1\uffff\7\44\1\uffff\2\44\1\uffff\5\44\3\uffff\1\44\22"+
            "\uffff\1\44\17\uffff\2\44\4\uffff\12\44\25\uffff\10\44\2\uffff"+
            "\2\44\2\uffff\26\44\1\uffff\7\44\1\uffff\2\44\1\uffff\5\44\3"+
            "\uffff\1\44\36\uffff\2\44\1\uffff\3\44\4\uffff\12\44\1\uffff"+
            "\1\44\21\uffff\1\44\1\uffff\6\44\3\uffff\3\44\1\uffff\4\44\3"+
            "\uffff\2\44\1\uffff\1\44\1\uffff\2\44\3\uffff\2\44\3\uffff\3"+
            "\44\3\uffff\10\44\1\uffff\3\44\55\uffff\11\44\25\uffff\10\44"+
            "\1\uffff\3\44\1\uffff\27\44\1\uffff\12\44\1\uffff\5\44\46\uffff"+
            "\2\44\4\uffff\12\44\25\uffff\10\44\1\uffff\3\44\1\uffff\27\44"+
            "\1\uffff\12\44\1\uffff\5\44\3\uffff\1\44\40\uffff\1\44\1\uffff"+
            "\2\44\4\uffff\12\44\25\uffff\10\44\1\uffff\3\44\1\uffff\27\44"+
            "\1\uffff\20\44\46\uffff\2\44\4\uffff\12\44\25\uffff\22\44\3"+
            "\uffff\30\44\1\uffff\11\44\1\uffff\1\44\2\uffff\7\44\72\uffff"+
            "\60\44\1\uffff\2\44\14\uffff\7\44\11\uffff\12\44\47\uffff\2"+
            "\44\1\uffff\1\44\2\uffff\2\44\1\uffff\1\44\2\uffff\1\44\6\uffff"+
            "\4\44\1\uffff\7\44\1\uffff\3\44\1\uffff\1\44\1\uffff\1\44\2"+
            "\uffff\2\44\1\uffff\4\44\1\uffff\2\44\11\uffff\1\44\2\uffff"+
            "\5\44\1\uffff\1\44\11\uffff\12\44\2\uffff\2\44\42\uffff\1\44"+
            "\37\uffff\12\44\26\uffff\10\44\1\uffff\42\44\35\uffff\4\44\164"+
            "\uffff\42\44\1\uffff\5\44\1\uffff\2\44\25\uffff\12\44\6\uffff"+
            "\6\44\112\uffff\46\44\12\uffff\51\44\7\uffff\132\44\5\uffff"+
            "\104\44\5\uffff\122\44\6\uffff\7\44\1\uffff\77\44\1\uffff\1"+
            "\44\1\uffff\4\44\2\uffff\7\44\1\uffff\1\44\1\uffff\4\44\2\uffff"+
            "\47\44\1\uffff\1\44\1\uffff\4\44\2\uffff\37\44\1\uffff\1\44"+
            "\1\uffff\4\44\2\uffff\7\44\1\uffff\1\44\1\uffff\4\44\2\uffff"+
            "\7\44\1\uffff\7\44\1\uffff\27\44\1\uffff\37\44\1\uffff\1\44"+
            "\1\uffff\4\44\2\uffff\7\44\1\uffff\47\44\1\uffff\23\44\16\uffff"+
            "\11\44\56\uffff\125\44\14\uffff\u026c\44\2\uffff\10\44\12\uffff"+
            "\32\44\5\uffff\113\44\25\uffff\15\44\1\uffff\4\44\16\uffff\22"+
            "\44\16\uffff\22\44\16\uffff\15\44\1\uffff\3\44\17\uffff\64\44"+
            "\43\uffff\1\44\4\uffff\1\44\3\uffff\12\44\46\uffff\12\44\6\uffff"+
            "\130\44\10\uffff\51\44\127\uffff\35\44\51\uffff\50\44\2\uffff"+
            "\5\44\u038b\uffff\154\44\u0094\uffff\u009c\44\4\uffff\132\44"+
            "\6\uffff\26\44\2\uffff\6\44\2\uffff\46\44\2\uffff\6\44\2\uffff"+
            "\10\44\1\uffff\1\44\1\uffff\1\44\1\uffff\1\44\1\uffff\37\44"+
            "\2\uffff\65\44\1\uffff\7\44\1\uffff\1\44\3\uffff\3\44\1\uffff"+
            "\7\44\3\uffff\4\44\2\uffff\6\44\4\uffff\15\44\5\uffff\3\44\1"+
            "\uffff\7\44\164\uffff\1\44\15\uffff\1\44\u0082\uffff\1\44\4"+
            "\uffff\1\44\2\uffff\12\44\1\uffff\1\44\3\uffff\5\44\6\uffff"+
            "\1\44\1\uffff\1\44\1\uffff\1\44\1\uffff\4\44\1\uffff\3\44\1"+
            "\uffff\7\44\3\uffff\3\44\5\uffff\5\44\u0ebb\uffff\2\44\52\uffff"+
            "\5\44\5\uffff\2\44\4\uffff\126\44\6\uffff\3\44\1\uffff\132\44"+
            "\1\uffff\4\44\5\uffff\50\44\4\uffff\136\44\21\uffff\30\44\70"+
            "\uffff\20\44\u0200\uffff\u19b6\44\112\uffff\u51a6\44\132\uffff"+
            "\u048d\44\u0773\uffff\u2ba4\44\u215c\uffff\u012e\44\2\uffff"+
            "\73\44\u0095\uffff\7\44\14\uffff\5\44\5\uffff\1\44\1\uffff\12"+
            "\44\1\uffff\15\44\1\uffff\5\44\1\uffff\1\44\1\uffff\2\44\1\uffff"+
            "\2\44\1\uffff\154\44\41\uffff\u016b\44\22\uffff\100\44\2\uffff"+
            "\66\44\50\uffff\14\44\164\uffff\5\44\1\uffff\u0087\44\23\uffff"+
            "\12\44\7\uffff\32\44\6\uffff\32\44\13\uffff\131\44\3\uffff\6"+
            "\44\2\uffff\6\44\2\uffff\6\44\2\uffff\3\44",
            "\1\64\1\63\5\uffff\1\67\5\uffff\12\61\5\uffff\1\70\1\uffff"+
            "\32\60\1\uffff\1\65\2\uffff\1\62\1\uffff\32\57\57\uffff\1\44"+
            "\12\uffff\1\44\4\uffff\1\44\5\uffff\27\44\1\uffff\37\44\1\uffff"+
            "\u013f\44\31\uffff\162\44\4\uffff\14\44\16\uffff\5\44\11\uffff"+
            "\1\44\u008b\uffff\1\44\13\uffff\1\44\1\uffff\3\44\1\uffff\1"+
            "\44\1\uffff\24\44\1\uffff\54\44\1\uffff\46\44\1\uffff\5\44\4"+
            "\uffff\u0082\44\10\uffff\105\44\1\uffff\46\44\2\uffff\2\44\6"+
            "\uffff\20\44\41\uffff\46\44\2\uffff\1\44\7\uffff\47\44\110\uffff"+
            "\33\44\5\uffff\3\44\56\uffff\32\44\5\uffff\13\44\25\uffff\12"+
            "\44\4\uffff\2\44\1\uffff\143\44\1\uffff\1\44\17\uffff\2\44\7"+
            "\uffff\17\44\2\uffff\1\44\20\uffff\1\44\1\uffff\36\44\35\uffff"+
            "\3\44\60\uffff\46\44\13\uffff\1\44\u0152\uffff\66\44\3\uffff"+
            "\1\44\22\uffff\1\44\7\uffff\12\44\4\uffff\12\44\25\uffff\10"+
            "\44\2\uffff\2\44\2\uffff\26\44\1\uffff\7\44\1\uffff\1\44\3\uffff"+
            "\4\44\3\uffff\1\44\36\uffff\2\44\1\uffff\3\44\4\uffff\14\44"+
            "\23\uffff\6\44\4\uffff\2\44\2\uffff\26\44\1\uffff\7\44\1\uffff"+
            "\2\44\1\uffff\2\44\1\uffff\2\44\37\uffff\4\44\1\uffff\1\44\7"+
            "\uffff\12\44\2\uffff\3\44\20\uffff\11\44\1\uffff\3\44\1\uffff"+
            "\26\44\1\uffff\7\44\1\uffff\2\44\1\uffff\5\44\3\uffff\1\44\22"+
            "\uffff\1\44\17\uffff\2\44\4\uffff\12\44\25\uffff\10\44\2\uffff"+
            "\2\44\2\uffff\26\44\1\uffff\7\44\1\uffff\2\44\1\uffff\5\44\3"+
            "\uffff\1\44\36\uffff\2\44\1\uffff\3\44\4\uffff\12\44\1\uffff"+
            "\1\44\21\uffff\1\44\1\uffff\6\44\3\uffff\3\44\1\uffff\4\44\3"+
            "\uffff\2\44\1\uffff\1\44\1\uffff\2\44\3\uffff\2\44\3\uffff\3"+
            "\44\3\uffff\10\44\1\uffff\3\44\55\uffff\11\44\25\uffff\10\44"+
            "\1\uffff\3\44\1\uffff\27\44\1\uffff\12\44\1\uffff\5\44\46\uffff"+
            "\2\44\4\uffff\12\44\25\uffff\10\44\1\uffff\3\44\1\uffff\27\44"+
            "\1\uffff\12\44\1\uffff\5\44\3\uffff\1\44\40\uffff\1\44\1\uffff"+
            "\2\44\4\uffff\12\44\25\uffff\10\44\1\uffff\3\44\1\uffff\27\44"+
            "\1\uffff\20\44\46\uffff\2\44\4\uffff\12\44\25\uffff\22\44\3"+
            "\uffff\30\44\1\uffff\11\44\1\uffff\1\44\2\uffff\7\44\72\uffff"+
            "\60\44\1\uffff\2\44\14\uffff\7\44\11\uffff\12\44\47\uffff\2"+
            "\44\1\uffff\1\44\2\uffff\2\44\1\uffff\1\44\2\uffff\1\44\6\uffff"+
            "\4\44\1\uffff\7\44\1\uffff\3\44\1\uffff\1\44\1\uffff\1\44\2"+
            "\uffff\2\44\1\uffff\4\44\1\uffff\2\44\11\uffff\1\44\2\uffff"+
            "\5\44\1\uffff\1\44\11\uffff\12\44\2\uffff\2\44\42\uffff\1\44"+
            "\37\uffff\12\44\26\uffff\10\44\1\uffff\42\44\35\uffff\4\44\164"+
            "\uffff\42\44\1\uffff\5\44\1\uffff\2\44\25\uffff\12\44\6\uffff"+
            "\6\44\112\uffff\46\44\12\uffff\51\44\7\uffff\132\44\5\uffff"+
            "\104\44\5\uffff\122\44\6\uffff\7\44\1\uffff\77\44\1\uffff\1"+
            "\44\1\uffff\4\44\2\uffff\7\44\1\uffff\1\44\1\uffff\4\44\2\uffff"+
            "\47\44\1\uffff\1\44\1\uffff\4\44\2\uffff\37\44\1\uffff\1\44"+
            "\1\uffff\4\44\2\uffff\7\44\1\uffff\1\44\1\uffff\4\44\2\uffff"+
            "\7\44\1\uffff\7\44\1\uffff\27\44\1\uffff\37\44\1\uffff\1\44"+
            "\1\uffff\4\44\2\uffff\7\44\1\uffff\47\44\1\uffff\23\44\16\uffff"+
            "\11\44\56\uffff\125\44\14\uffff\u026c\44\2\uffff\10\44\12\uffff"+
            "\32\44\5\uffff\113\44\25\uffff\15\44\1\uffff\4\44\16\uffff\22"+
            "\44\16\uffff\22\44\16\uffff\15\44\1\uffff\3\44\17\uffff\64\44"+
            "\43\uffff\1\44\4\uffff\1\44\3\uffff\12\44\46\uffff\12\44\6\uffff"+
            "\130\44\10\uffff\51\44\127\uffff\35\44\51\uffff\50\44\2\uffff"+
            "\5\44\u038b\uffff\154\44\u0094\uffff\u009c\44\4\uffff\132\44"+
            "\6\uffff\26\44\2\uffff\6\44\2\uffff\46\44\2\uffff\6\44\2\uffff"+
            "\10\44\1\uffff\1\44\1\uffff\1\44\1\uffff\1\44\1\uffff\37\44"+
            "\2\uffff\65\44\1\uffff\7\44\1\uffff\1\44\3\uffff\3\44\1\uffff"+
            "\7\44\3\uffff\4\44\2\uffff\6\44\4\uffff\15\44\5\uffff\3\44\1"+
            "\uffff\7\44\164\uffff\1\44\15\uffff\1\44\u0082\uffff\1\44\4"+
            "\uffff\1\44\2\uffff\12\44\1\uffff\1\44\3\uffff\5\44\6\uffff"+
            "\1\44\1\uffff\1\44\1\uffff\1\44\1\uffff\4\44\1\uffff\3\44\1"+
            "\uffff\7\44\3\uffff\3\44\5\uffff\5\44\u0ebb\uffff\2\44\52\uffff"+
            "\5\44\5\uffff\2\44\4\uffff\126\44\6\uffff\3\44\1\uffff\132\44"+
            "\1\uffff\4\44\5\uffff\50\44\4\uffff\136\44\21\uffff\30\44\70"+
            "\uffff\20\44\u0200\uffff\u19b6\44\112\uffff\u51a6\44\132\uffff"+
            "\u048d\44\u0773\uffff\u2ba4\44\u215c\uffff\u012e\44\2\uffff"+
            "\73\44\u0095\uffff\7\44\14\uffff\5\44\5\uffff\1\44\1\uffff\12"+
            "\44\1\uffff\15\44\1\uffff\5\44\1\uffff\1\44\1\uffff\2\44\1\uffff"+
            "\2\44\1\uffff\154\44\41\uffff\u016b\44\22\uffff\100\44\2\uffff"+
            "\66\44\50\uffff\14\44\164\uffff\5\44\1\uffff\u0087\44\23\uffff"+
            "\12\44\7\uffff\32\44\6\uffff\32\44\13\uffff\131\44\3\uffff\6"+
            "\44\2\uffff\6\44\2\uffff\6\44\2\uffff\3\44",
            "\1\64\1\63\5\uffff\1\67\5\uffff\12\61\5\uffff\1\70\1\uffff"+
            "\32\60\1\uffff\1\65\2\uffff\1\62\1\uffff\32\57\57\uffff\1\44"+
            "\12\uffff\1\44\4\uffff\1\44\5\uffff\27\44\1\uffff\37\44\1\uffff"+
            "\u013f\44\31\uffff\162\44\4\uffff\14\44\16\uffff\5\44\11\uffff"+
            "\1\44\u008b\uffff\1\44\13\uffff\1\44\1\uffff\3\44\1\uffff\1"+
            "\44\1\uffff\24\44\1\uffff\54\44\1\uffff\46\44\1\uffff\5\44\4"+
            "\uffff\u0082\44\10\uffff\105\44\1\uffff\46\44\2\uffff\2\44\6"+
            "\uffff\20\44\41\uffff\46\44\2\uffff\1\44\7\uffff\47\44\110\uffff"+
            "\33\44\5\uffff\3\44\56\uffff\32\44\5\uffff\13\44\25\uffff\12"+
            "\44\4\uffff\2\44\1\uffff\143\44\1\uffff\1\44\17\uffff\2\44\7"+
            "\uffff\17\44\2\uffff\1\44\20\uffff\1\44\1\uffff\36\44\35\uffff"+
            "\3\44\60\uffff\46\44\13\uffff\1\44\u0152\uffff\66\44\3\uffff"+
            "\1\44\22\uffff\1\44\7\uffff\12\44\4\uffff\12\44\25\uffff\10"+
            "\44\2\uffff\2\44\2\uffff\26\44\1\uffff\7\44\1\uffff\1\44\3\uffff"+
            "\4\44\3\uffff\1\44\36\uffff\2\44\1\uffff\3\44\4\uffff\14\44"+
            "\23\uffff\6\44\4\uffff\2\44\2\uffff\26\44\1\uffff\7\44\1\uffff"+
            "\2\44\1\uffff\2\44\1\uffff\2\44\37\uffff\4\44\1\uffff\1\44\7"+
            "\uffff\12\44\2\uffff\3\44\20\uffff\11\44\1\uffff\3\44\1\uffff"+
            "\26\44\1\uffff\7\44\1\uffff\2\44\1\uffff\5\44\3\uffff\1\44\22"+
            "\uffff\1\44\17\uffff\2\44\4\uffff\12\44\25\uffff\10\44\2\uffff"+
            "\2\44\2\uffff\26\44\1\uffff\7\44\1\uffff\2\44\1\uffff\5\44\3"+
            "\uffff\1\44\36\uffff\2\44\1\uffff\3\44\4\uffff\12\44\1\uffff"+
            "\1\44\21\uffff\1\44\1\uffff\6\44\3\uffff\3\44\1\uffff\4\44\3"+
            "\uffff\2\44\1\uffff\1\44\1\uffff\2\44\3\uffff\2\44\3\uffff\3"+
            "\44\3\uffff\10\44\1\uffff\3\44\55\uffff\11\44\25\uffff\10\44"+
            "\1\uffff\3\44\1\uffff\27\44\1\uffff\12\44\1\uffff\5\44\46\uffff"+
            "\2\44\4\uffff\12\44\25\uffff\10\44\1\uffff\3\44\1\uffff\27\44"+
            "\1\uffff\12\44\1\uffff\5\44\3\uffff\1\44\40\uffff\1\44\1\uffff"+
            "\2\44\4\uffff\12\44\25\uffff\10\44\1\uffff\3\44\1\uffff\27\44"+
            "\1\uffff\20\44\46\uffff\2\44\4\uffff\12\44\25\uffff\22\44\3"+
            "\uffff\30\44\1\uffff\11\44\1\uffff\1\44\2\uffff\7\44\72\uffff"+
            "\60\44\1\uffff\2\44\14\uffff\7\44\11\uffff\12\44\47\uffff\2"+
            "\44\1\uffff\1\44\2\uffff\2\44\1\uffff\1\44\2\uffff\1\44\6\uffff"+
            "\4\44\1\uffff\7\44\1\uffff\3\44\1\uffff\1\44\1\uffff\1\44\2"+
            "\uffff\2\44\1\uffff\4\44\1\uffff\2\44\11\uffff\1\44\2\uffff"+
            "\5\44\1\uffff\1\44\11\uffff\12\44\2\uffff\2\44\42\uffff\1\44"+
            "\37\uffff\12\44\26\uffff\10\44\1\uffff\42\44\35\uffff\4\44\164"+
            "\uffff\42\44\1\uffff\5\44\1\uffff\2\44\25\uffff\12\44\6\uffff"+
            "\6\44\112\uffff\46\44\12\uffff\51\44\7\uffff\132\44\5\uffff"+
            "\104\44\5\uffff\122\44\6\uffff\7\44\1\uffff\77\44\1\uffff\1"+
            "\44\1\uffff\4\44\2\uffff\7\44\1\uffff\1\44\1\uffff\4\44\2\uffff"+
            "\47\44\1\uffff\1\44\1\uffff\4\44\2\uffff\37\44\1\uffff\1\44"+
            "\1\uffff\4\44\2\uffff\7\44\1\uffff\1\44\1\uffff\4\44\2\uffff"+
            "\7\44\1\uffff\7\44\1\uffff\27\44\1\uffff\37\44\1\uffff\1\44"+
            "\1\uffff\4\44\2\uffff\7\44\1\uffff\47\44\1\uffff\23\44\16\uffff"+
            "\11\44\56\uffff\125\44\14\uffff\u026c\44\2\uffff\10\44\12\uffff"+
            "\32\44\5\uffff\113\44\25\uffff\15\44\1\uffff\4\44\16\uffff\22"+
            "\44\16\uffff\22\44\16\uffff\15\44\1\uffff\3\44\17\uffff\64\44"+
            "\43\uffff\1\44\4\uffff\1\44\3\uffff\12\44\46\uffff\12\44\6\uffff"+
            "\130\44\10\uffff\51\44\127\uffff\35\44\51\uffff\50\44\2\uffff"+
            "\5\44\u038b\uffff\154\44\u0094\uffff\u009c\44\4\uffff\132\44"+
            "\6\uffff\26\44\2\uffff\6\44\2\uffff\46\44\2\uffff\6\44\2\uffff"+
            "\10\44\1\uffff\1\44\1\uffff\1\44\1\uffff\1\44\1\uffff\37\44"+
            "\2\uffff\65\44\1\uffff\7\44\1\uffff\1\44\3\uffff\3\44\1\uffff"+
            "\7\44\3\uffff\4\44\2\uffff\6\44\4\uffff\15\44\5\uffff\3\44\1"+
            "\uffff\7\44\164\uffff\1\44\15\uffff\1\44\u0082\uffff\1\44\4"+
            "\uffff\1\44\2\uffff\12\44\1\uffff\1\44\3\uffff\5\44\6\uffff"+
            "\1\44\1\uffff\1\44\1\uffff\1\44\1\uffff\4\44\1\uffff\3\44\1"+
            "\uffff\7\44\3\uffff\3\44\5\uffff\5\44\u0ebb\uffff\2\44\52\uffff"+
            "\5\44\5\uffff\2\44\4\uffff\126\44\6\uffff\3\44\1\uffff\132\44"+
            "\1\uffff\4\44\5\uffff\50\44\4\uffff\136\44\21\uffff\30\44\70"+
            "\uffff\20\44\u0200\uffff\u19b6\44\112\uffff\u51a6\44\132\uffff"+
            "\u048d\44\u0773\uffff\u2ba4\44\u215c\uffff\u012e\44\2\uffff"+
            "\73\44\u0095\uffff\7\44\14\uffff\5\44\5\uffff\1\44\1\uffff\12"+
            "\44\1\uffff\15\44\1\uffff\5\44\1\uffff\1\44\1\uffff\2\44\1\uffff"+
            "\2\44\1\uffff\154\44\41\uffff\u016b\44\22\uffff\100\44\2\uffff"+
            "\66\44\50\uffff\14\44\164\uffff\5\44\1\uffff\u0087\44\23\uffff"+
            "\12\44\7\uffff\32\44\6\uffff\32\44\13\uffff\131\44\3\uffff\6"+
            "\44\2\uffff\6\44\2\uffff\6\44\2\uffff\3\44",
            "",
            "",
            "",
            "\1\116\1\uffff\12\137\13\uffff\1\136\37\uffff\1\136",
            "",
            "\1\67\5\uffff\12\141\5\uffff\1\70\1\uffff\32\44\1\uffff\1"+
            "\42\4\uffff\32\44\57\uffff\1\44\12\uffff\1\44\4\uffff\1\44\5"+
            "\uffff\27\44\1\uffff\37\44\1\uffff\u013f\44\31\uffff\162\44"+
            "\4\uffff\14\44\16\uffff\5\44\11\uffff\1\44\u008b\uffff\1\44"+
            "\13\uffff\1\44\1\uffff\3\44\1\uffff\1\44\1\uffff\24\44\1\uffff"+
            "\54\44\1\uffff\46\44\1\uffff\5\44\4\uffff\u0082\44\10\uffff"+
            "\105\44\1\uffff\46\44\2\uffff\2\44\6\uffff\20\44\41\uffff\46"+
            "\44\2\uffff\1\44\7\uffff\47\44\110\uffff\33\44\5\uffff\3\44"+
            "\56\uffff\32\44\5\uffff\13\44\25\uffff\12\44\4\uffff\2\44\1"+
            "\uffff\143\44\1\uffff\1\44\17\uffff\2\44\7\uffff\17\44\2\uffff"+
            "\1\44\20\uffff\1\44\1\uffff\36\44\35\uffff\3\44\60\uffff\46"+
            "\44\13\uffff\1\44\u0152\uffff\66\44\3\uffff\1\44\22\uffff\1"+
            "\44\7\uffff\12\44\4\uffff\12\44\25\uffff\10\44\2\uffff\2\44"+
            "\2\uffff\26\44\1\uffff\7\44\1\uffff\1\44\3\uffff\4\44\3\uffff"+
            "\1\44\36\uffff\2\44\1\uffff\3\44\4\uffff\14\44\23\uffff\6\44"+
            "\4\uffff\2\44\2\uffff\26\44\1\uffff\7\44\1\uffff\2\44\1\uffff"+
            "\2\44\1\uffff\2\44\37\uffff\4\44\1\uffff\1\44\7\uffff\12\44"+
            "\2\uffff\3\44\20\uffff\11\44\1\uffff\3\44\1\uffff\26\44\1\uffff"+
            "\7\44\1\uffff\2\44\1\uffff\5\44\3\uffff\1\44\22\uffff\1\44\17"+
            "\uffff\2\44\4\uffff\12\44\25\uffff\10\44\2\uffff\2\44\2\uffff"+
            "\26\44\1\uffff\7\44\1\uffff\2\44\1\uffff\5\44\3\uffff\1\44\36"+
            "\uffff\2\44\1\uffff\3\44\4\uffff\12\44\1\uffff\1\44\21\uffff"+
            "\1\44\1\uffff\6\44\3\uffff\3\44\1\uffff\4\44\3\uffff\2\44\1"+
            "\uffff\1\44\1\uffff\2\44\3\uffff\2\44\3\uffff\3\44\3\uffff\10"+
            "\44\1\uffff\3\44\55\uffff\11\44\25\uffff\10\44\1\uffff\3\44"+
            "\1\uffff\27\44\1\uffff\12\44\1\uffff\5\44\46\uffff\2\44\4\uffff"+
            "\12\44\25\uffff\10\44\1\uffff\3\44\1\uffff\27\44\1\uffff\12"+
            "\44\1\uffff\5\44\3\uffff\1\44\40\uffff\1\44\1\uffff\2\44\4\uffff"+
            "\12\44\25\uffff\10\44\1\uffff\3\44\1\uffff\27\44\1\uffff\20"+
            "\44\46\uffff\2\44\4\uffff\12\44\25\uffff\22\44\3\uffff\30\44"+
            "\1\uffff\11\44\1\uffff\1\44\2\uffff\7\44\72\uffff\60\44\1\uffff"+
            "\2\44\14\uffff\7\44\11\uffff\12\44\47\uffff\2\44\1\uffff\1\44"+
            "\2\uffff\2\44\1\uffff\1\44\2\uffff\1\44\6\uffff\4\44\1\uffff"+
            "\7\44\1\uffff\3\44\1\uffff\1\44\1\uffff\1\44\2\uffff\2\44\1"+
            "\uffff\4\44\1\uffff\2\44\11\uffff\1\44\2\uffff\5\44\1\uffff"+
            "\1\44\11\uffff\12\44\2\uffff\2\44\42\uffff\1\44\37\uffff\12"+
            "\44\26\uffff\10\44\1\uffff\42\44\35\uffff\4\44\164\uffff\42"+
            "\44\1\uffff\5\44\1\uffff\2\44\25\uffff\12\44\6\uffff\6\44\112"+
            "\uffff\46\44\12\uffff\51\44\7\uffff\132\44\5\uffff\104\44\5"+
            "\uffff\122\44\6\uffff\7\44\1\uffff\77\44\1\uffff\1\44\1\uffff"+
            "\4\44\2\uffff\7\44\1\uffff\1\44\1\uffff\4\44\2\uffff\47\44\1"+
            "\uffff\1\44\1\uffff\4\44\2\uffff\37\44\1\uffff\1\44\1\uffff"+
            "\4\44\2\uffff\7\44\1\uffff\1\44\1\uffff\4\44\2\uffff\7\44\1"+
            "\uffff\7\44\1\uffff\27\44\1\uffff\37\44\1\uffff\1\44\1\uffff"+
            "\4\44\2\uffff\7\44\1\uffff\47\44\1\uffff\23\44\16\uffff\11\44"+
            "\56\uffff\125\44\14\uffff\u026c\44\2\uffff\10\44\12\uffff\32"+
            "\44\5\uffff\113\44\25\uffff\15\44\1\uffff\4\44\16\uffff\22\44"+
            "\16\uffff\22\44\16\uffff\15\44\1\uffff\3\44\17\uffff\64\44\43"+
            "\uffff\1\44\4\uffff\1\44\3\uffff\12\44\46\uffff\12\44\6\uffff"+
            "\130\44\10\uffff\51\44\127\uffff\35\44\51\uffff\50\44\2\uffff"+
            "\5\44\u038b\uffff\154\44\u0094\uffff\u009c\44\4\uffff\132\44"+
            "\6\uffff\26\44\2\uffff\6\44\2\uffff\46\44\2\uffff\6\44\2\uffff"+
            "\10\44\1\uffff\1\44\1\uffff\1\44\1\uffff\1\44\1\uffff\37\44"+
            "\2\uffff\65\44\1\uffff\7\44\1\uffff\1\44\3\uffff\3\44\1\uffff"+
            "\7\44\3\uffff\4\44\2\uffff\6\44\4\uffff\15\44\5\uffff\3\44\1"+
            "\uffff\7\44\164\uffff\1\44\15\uffff\1\44\u0082\uffff\1\44\4"+
            "\uffff\1\44\2\uffff\12\44\1\uffff\1\44\3\uffff\5\44\6\uffff"+
            "\1\44\1\uffff\1\44\1\uffff\1\44\1\uffff\4\44\1\uffff\3\44\1"+
            "\uffff\7\44\3\uffff\3\44\5\uffff\5\44\u0ebb\uffff\2\44\52\uffff"+
            "\5\44\5\uffff\2\44\4\uffff\126\44\6\uffff\3\44\1\uffff\132\44"+
            "\1\uffff\4\44\5\uffff\50\44\4\uffff\136\44\21\uffff\30\44\70"+
            "\uffff\20\44\u0200\uffff\u19b6\44\112\uffff\u51a6\44\132\uffff"+
            "\u048d\44\u0773\uffff\u2ba4\44\u215c\uffff\u012e\44\2\uffff"+
            "\73\44\u0095\uffff\7\44\14\uffff\5\44\5\uffff\1\44\1\uffff\12"+
            "\44\1\uffff\15\44\1\uffff\5\44\1\uffff\1\44\1\uffff\2\44\1\uffff"+
            "\2\44\1\uffff\154\44\41\uffff\u016b\44\22\uffff\100\44\2\uffff"+
            "\66\44\50\uffff\14\44\164\uffff\5\44\1\uffff\u0087\44\23\uffff"+
            "\12\44\7\uffff\32\44\6\uffff\32\44\13\uffff\131\44\3\uffff\6"+
            "\44\2\uffff\6\44\2\uffff\6\44\2\uffff\3\44",
            "",
            "\1\67\5\uffff\12\152\5\uffff\1\70\1\uffff\6\152\24\44\1\uffff"+
            "\1\42\4\uffff\6\152\24\44\57\uffff\1\44\12\uffff\1\44\4\uffff"+
            "\1\44\5\uffff\27\44\1\uffff\37\44\1\uffff\u013f\44\31\uffff"+
            "\162\44\4\uffff\14\44\16\uffff\5\44\11\uffff\1\44\u008b\uffff"+
            "\1\44\13\uffff\1\44\1\uffff\3\44\1\uffff\1\44\1\uffff\24\44"+
            "\1\uffff\54\44\1\uffff\46\44\1\uffff\5\44\4\uffff\u0082\44\10"+
            "\uffff\105\44\1\uffff\46\44\2\uffff\2\44\6\uffff\20\44\41\uffff"+
            "\46\44\2\uffff\1\44\7\uffff\47\44\110\uffff\33\44\5\uffff\3"+
            "\44\56\uffff\32\44\5\uffff\13\44\25\uffff\12\44\4\uffff\2\44"+
            "\1\uffff\143\44\1\uffff\1\44\17\uffff\2\44\7\uffff\17\44\2\uffff"+
            "\1\44\20\uffff\1\44\1\uffff\36\44\35\uffff\3\44\60\uffff\46"+
            "\44\13\uffff\1\44\u0152\uffff\66\44\3\uffff\1\44\22\uffff\1"+
            "\44\7\uffff\12\44\4\uffff\12\44\25\uffff\10\44\2\uffff\2\44"+
            "\2\uffff\26\44\1\uffff\7\44\1\uffff\1\44\3\uffff\4\44\3\uffff"+
            "\1\44\36\uffff\2\44\1\uffff\3\44\4\uffff\14\44\23\uffff\6\44"+
            "\4\uffff\2\44\2\uffff\26\44\1\uffff\7\44\1\uffff\2\44\1\uffff"+
            "\2\44\1\uffff\2\44\37\uffff\4\44\1\uffff\1\44\7\uffff\12\44"+
            "\2\uffff\3\44\20\uffff\11\44\1\uffff\3\44\1\uffff\26\44\1\uffff"+
            "\7\44\1\uffff\2\44\1\uffff\5\44\3\uffff\1\44\22\uffff\1\44\17"+
            "\uffff\2\44\4\uffff\12\44\25\uffff\10\44\2\uffff\2\44\2\uffff"+
            "\26\44\1\uffff\7\44\1\uffff\2\44\1\uffff\5\44\3\uffff\1\44\36"+
            "\uffff\2\44\1\uffff\3\44\4\uffff\12\44\1\uffff\1\44\21\uffff"+
            "\1\44\1\uffff\6\44\3\uffff\3\44\1\uffff\4\44\3\uffff\2\44\1"+
            "\uffff\1\44\1\uffff\2\44\3\uffff\2\44\3\uffff\3\44\3\uffff\10"+
            "\44\1\uffff\3\44\55\uffff\11\44\25\uffff\10\44\1\uffff\3\44"+
            "\1\uffff\27\44\1\uffff\12\44\1\uffff\5\44\46\uffff\2\44\4\uffff"+
            "\12\44\25\uffff\10\44\1\uffff\3\44\1\uffff\27\44\1\uffff\12"+
            "\44\1\uffff\5\44\3\uffff\1\44\40\uffff\1\44\1\uffff\2\44\4\uffff"+
            "\12\44\25\uffff\10\44\1\uffff\3\44\1\uffff\27\44\1\uffff\20"+
            "\44\46\uffff\2\44\4\uffff\12\44\25\uffff\22\44\3\uffff\30\44"+
            "\1\uffff\11\44\1\uffff\1\44\2\uffff\7\44\72\uffff\60\44\1\uffff"+
            "\2\44\14\uffff\7\44\11\uffff\12\44\47\uffff\2\44\1\uffff\1\44"+
            "\2\uffff\2\44\1\uffff\1\44\2\uffff\1\44\6\uffff\4\44\1\uffff"+
            "\7\44\1\uffff\3\44\1\uffff\1\44\1\uffff\1\44\2\uffff\2\44\1"+
            "\uffff\4\44\1\uffff\2\44\11\uffff\1\44\2\uffff\5\44\1\uffff"+
            "\1\44\11\uffff\12\44\2\uffff\2\44\42\uffff\1\44\37\uffff\12"+
            "\44\26\uffff\10\44\1\uffff\42\44\35\uffff\4\44\164\uffff\42"+
            "\44\1\uffff\5\44\1\uffff\2\44\25\uffff\12\44\6\uffff\6\44\112"+
            "\uffff\46\44\12\uffff\51\44\7\uffff\132\44\5\uffff\104\44\5"+
            "\uffff\122\44\6\uffff\7\44\1\uffff\77\44\1\uffff\1\44\1\uffff"+
            "\4\44\2\uffff\7\44\1\uffff\1\44\1\uffff\4\44\2\uffff\47\44\1"+
            "\uffff\1\44\1\uffff\4\44\2\uffff\37\44\1\uffff\1\44\1\uffff"+
            "\4\44\2\uffff\7\44\1\uffff\1\44\1\uffff\4\44\2\uffff\7\44\1"+
            "\uffff\7\44\1\uffff\27\44\1\uffff\37\44\1\uffff\1\44\1\uffff"+
            "\4\44\2\uffff\7\44\1\uffff\47\44\1\uffff\23\44\16\uffff\11\44"+
            "\56\uffff\125\44\14\uffff\u026c\44\2\uffff\10\44\12\uffff\32"+
            "\44\5\uffff\113\44\25\uffff\15\44\1\uffff\4\44\16\uffff\22\44"+
            "\16\uffff\22\44\16\uffff\15\44\1\uffff\3\44\17\uffff\64\44\43"+
            "\uffff\1\44\4\uffff\1\44\3\uffff\12\44\46\uffff\12\44\6\uffff"+
            "\130\44\10\uffff\51\44\127\uffff\35\44\51\uffff\50\44\2\uffff"+
            "\5\44\u038b\uffff\154\44\u0094\uffff\u009c\44\4\uffff\132\44"+
            "\6\uffff\26\44\2\uffff\6\44\2\uffff\46\44\2\uffff\6\44\2\uffff"+
            "\10\44\1\uffff\1\44\1\uffff\1\44\1\uffff\1\44\1\uffff\37\44"+
            "\2\uffff\65\44\1\uffff\7\44\1\uffff\1\44\3\uffff\3\44\1\uffff"+
            "\7\44\3\uffff\4\44\2\uffff\6\44\4\uffff\15\44\5\uffff\3\44\1"+
            "\uffff\7\44\164\uffff\1\44\15\uffff\1\44\u0082\uffff\1\44\4"+
            "\uffff\1\44\2\uffff\12\44\1\uffff\1\44\3\uffff\5\44\6\uffff"+
            "\1\44\1\uffff\1\44\1\uffff\1\44\1\uffff\4\44\1\uffff\3\44\1"+
            "\uffff\7\44\3\uffff\3\44\5\uffff\5\44\u0ebb\uffff\2\44\52\uffff"+
            "\5\44\5\uffff\2\44\4\uffff\126\44\6\uffff\3\44\1\uffff\132\44"+
            "\1\uffff\4\44\5\uffff\50\44\4\uffff\136\44\21\uffff\30\44\70"+
            "\uffff\20\44\u0200\uffff\u19b6\44\112\uffff\u51a6\44\132\uffff"+
            "\u048d\44\u0773\uffff\u2ba4\44\u215c\uffff\u012e\44\2\uffff"+
            "\73\44\u0095\uffff\7\44\14\uffff\5\44\5\uffff\1\44\1\uffff\12"+
            "\44\1\uffff\15\44\1\uffff\5\44\1\uffff\1\44\1\uffff\2\44\1\uffff"+
            "\2\44\1\uffff\154\44\41\uffff\u016b\44\22\uffff\100\44\2\uffff"+
            "\66\44\50\uffff\14\44\164\uffff\5\44\1\uffff\u0087\44\23\uffff"+
            "\12\44\7\uffff\32\44\6\uffff\32\44\13\uffff\131\44\3\uffff\6"+
            "\44\2\uffff\6\44\2\uffff\6\44\2\uffff\3\44",
            "\1\64\1\63\5\uffff\1\67\5\uffff\12\155\5\uffff\1\70\1\uffff"+
            "\6\154\24\60\1\uffff\1\65\2\uffff\1\62\1\uffff\6\153\24\57\57"+
            "\uffff\1\44\12\uffff\1\44\4\uffff\1\44\5\uffff\27\44\1\uffff"+
            "\37\44\1\uffff\u013f\44\31\uffff\162\44\4\uffff\14\44\16\uffff"+
            "\5\44\11\uffff\1\44\u008b\uffff\1\44\13\uffff\1\44\1\uffff\3"+
            "\44\1\uffff\1\44\1\uffff\24\44\1\uffff\54\44\1\uffff\46\44\1"+
            "\uffff\5\44\4\uffff\u0082\44\10\uffff\105\44\1\uffff\46\44\2"+
            "\uffff\2\44\6\uffff\20\44\41\uffff\46\44\2\uffff\1\44\7\uffff"+
            "\47\44\110\uffff\33\44\5\uffff\3\44\56\uffff\32\44\5\uffff\13"+
            "\44\25\uffff\12\44\4\uffff\2\44\1\uffff\143\44\1\uffff\1\44"+
            "\17\uffff\2\44\7\uffff\17\44\2\uffff\1\44\20\uffff\1\44\1\uffff"+
            "\36\44\35\uffff\3\44\60\uffff\46\44\13\uffff\1\44\u0152\uffff"+
            "\66\44\3\uffff\1\44\22\uffff\1\44\7\uffff\12\44\4\uffff\12\44"+
            "\25\uffff\10\44\2\uffff\2\44\2\uffff\26\44\1\uffff\7\44\1\uffff"+
            "\1\44\3\uffff\4\44\3\uffff\1\44\36\uffff\2\44\1\uffff\3\44\4"+
            "\uffff\14\44\23\uffff\6\44\4\uffff\2\44\2\uffff\26\44\1\uffff"+
            "\7\44\1\uffff\2\44\1\uffff\2\44\1\uffff\2\44\37\uffff\4\44\1"+
            "\uffff\1\44\7\uffff\12\44\2\uffff\3\44\20\uffff\11\44\1\uffff"+
            "\3\44\1\uffff\26\44\1\uffff\7\44\1\uffff\2\44\1\uffff\5\44\3"+
            "\uffff\1\44\22\uffff\1\44\17\uffff\2\44\4\uffff\12\44\25\uffff"+
            "\10\44\2\uffff\2\44\2\uffff\26\44\1\uffff\7\44\1\uffff\2\44"+
            "\1\uffff\5\44\3\uffff\1\44\36\uffff\2\44\1\uffff\3\44\4\uffff"+
            "\12\44\1\uffff\1\44\21\uffff\1\44\1\uffff\6\44\3\uffff\3\44"+
            "\1\uffff\4\44\3\uffff\2\44\1\uffff\1\44\1\uffff\2\44\3\uffff"+
            "\2\44\3\uffff\3\44\3\uffff\10\44\1\uffff\3\44\55\uffff\11\44"+
            "\25\uffff\10\44\1\uffff\3\44\1\uffff\27\44\1\uffff\12\44\1\uffff"+
            "\5\44\46\uffff\2\44\4\uffff\12\44\25\uffff\10\44\1\uffff\3\44"+
            "\1\uffff\27\44\1\uffff\12\44\1\uffff\5\44\3\uffff\1\44\40\uffff"+
            "\1\44\1\uffff\2\44\4\uffff\12\44\25\uffff\10\44\1\uffff\3\44"+
            "\1\uffff\27\44\1\uffff\20\44\46\uffff\2\44\4\uffff\12\44\25"+
            "\uffff\22\44\3\uffff\30\44\1\uffff\11\44\1\uffff\1\44\2\uffff"+
            "\7\44\72\uffff\60\44\1\uffff\2\44\14\uffff\7\44\11\uffff\12"+
            "\44\47\uffff\2\44\1\uffff\1\44\2\uffff\2\44\1\uffff\1\44\2\uffff"+
            "\1\44\6\uffff\4\44\1\uffff\7\44\1\uffff\3\44\1\uffff\1\44\1"+
            "\uffff\1\44\2\uffff\2\44\1\uffff\4\44\1\uffff\2\44\11\uffff"+
            "\1\44\2\uffff\5\44\1\uffff\1\44\11\uffff\12\44\2\uffff\2\44"+
            "\42\uffff\1\44\37\uffff\12\44\26\uffff\10\44\1\uffff\42\44\35"+
            "\uffff\4\44\164\uffff\42\44\1\uffff\5\44\1\uffff\2\44\25\uffff"+
            "\12\44\6\uffff\6\44\112\uffff\46\44\12\uffff\51\44\7\uffff\132"+
            "\44\5\uffff\104\44\5\uffff\122\44\6\uffff\7\44\1\uffff\77\44"+
            "\1\uffff\1\44\1\uffff\4\44\2\uffff\7\44\1\uffff\1\44\1\uffff"+
            "\4\44\2\uffff\47\44\1\uffff\1\44\1\uffff\4\44\2\uffff\37\44"+
            "\1\uffff\1\44\1\uffff\4\44\2\uffff\7\44\1\uffff\1\44\1\uffff"+
            "\4\44\2\uffff\7\44\1\uffff\7\44\1\uffff\27\44\1\uffff\37\44"+
            "\1\uffff\1\44\1\uffff\4\44\2\uffff\7\44\1\uffff\47\44\1\uffff"+
            "\23\44\16\uffff\11\44\56\uffff\125\44\14\uffff\u026c\44\2\uffff"+
            "\10\44\12\uffff\32\44\5\uffff\113\44\25\uffff\15\44\1\uffff"+
            "\4\44\16\uffff\22\44\16\uffff\22\44\16\uffff\15\44\1\uffff\3"+
            "\44\17\uffff\64\44\43\uffff\1\44\4\uffff\1\44\3\uffff\12\44"+
            "\46\uffff\12\44\6\uffff\130\44\10\uffff\51\44\127\uffff\35\44"+
            "\51\uffff\50\44\2\uffff\5\44\u038b\uffff\154\44\u0094\uffff"+
            "\u009c\44\4\uffff\132\44\6\uffff\26\44\2\uffff\6\44\2\uffff"+
            "\46\44\2\uffff\6\44\2\uffff\10\44\1\uffff\1\44\1\uffff\1\44"+
            "\1\uffff\1\44\1\uffff\37\44\2\uffff\65\44\1\uffff\7\44\1\uffff"+
            "\1\44\3\uffff\3\44\1\uffff\7\44\3\uffff\4\44\2\uffff\6\44\4"+
            "\uffff\15\44\5\uffff\3\44\1\uffff\7\44\164\uffff\1\44\15\uffff"+
            "\1\44\u0082\uffff\1\44\4\uffff\1\44\2\uffff\12\44\1\uffff\1"+
            "\44\3\uffff\5\44\6\uffff\1\44\1\uffff\1\44\1\uffff\1\44\1\uffff"+
            "\4\44\1\uffff\3\44\1\uffff\7\44\3\uffff\3\44\5\uffff\5\44\u0ebb"+
            "\uffff\2\44\52\uffff\5\44\5\uffff\2\44\4\uffff\126\44\6\uffff"+
            "\3\44\1\uffff\132\44\1\uffff\4\44\5\uffff\50\44\4\uffff\136"+
            "\44\21\uffff\30\44\70\uffff\20\44\u0200\uffff\u19b6\44\112\uffff"+
            "\u51a6\44\132\uffff\u048d\44\u0773\uffff\u2ba4\44\u215c\uffff"+
            "\u012e\44\2\uffff\73\44\u0095\uffff\7\44\14\uffff\5\44\5\uffff"+
            "\1\44\1\uffff\12\44\1\uffff\15\44\1\uffff\5\44\1\uffff\1\44"+
            "\1\uffff\2\44\1\uffff\2\44\1\uffff\154\44\41\uffff\u016b\44"+
            "\22\uffff\100\44\2\uffff\66\44\50\uffff\14\44\164\uffff\5\44"+
            "\1\uffff\u0087\44\23\uffff\12\44\7\uffff\32\44\6\uffff\32\44"+
            "\13\uffff\131\44\3\uffff\6\44\2\uffff\6\44\2\uffff\6\44\2\uffff"+
            "\3\44",
            "\1\64\1\63\5\uffff\1\67\5\uffff\12\155\5\uffff\1\70\1\uffff"+
            "\6\154\24\60\1\uffff\1\65\2\uffff\1\62\1\uffff\6\153\24\57\57"+
            "\uffff\1\44\12\uffff\1\44\4\uffff\1\44\5\uffff\27\44\1\uffff"+
            "\37\44\1\uffff\u013f\44\31\uffff\162\44\4\uffff\14\44\16\uffff"+
            "\5\44\11\uffff\1\44\u008b\uffff\1\44\13\uffff\1\44\1\uffff\3"+
            "\44\1\uffff\1\44\1\uffff\24\44\1\uffff\54\44\1\uffff\46\44\1"+
            "\uffff\5\44\4\uffff\u0082\44\10\uffff\105\44\1\uffff\46\44\2"+
            "\uffff\2\44\6\uffff\20\44\41\uffff\46\44\2\uffff\1\44\7\uffff"+
            "\47\44\110\uffff\33\44\5\uffff\3\44\56\uffff\32\44\5\uffff\13"+
            "\44\25\uffff\12\44\4\uffff\2\44\1\uffff\143\44\1\uffff\1\44"+
            "\17\uffff\2\44\7\uffff\17\44\2\uffff\1\44\20\uffff\1\44\1\uffff"+
            "\36\44\35\uffff\3\44\60\uffff\46\44\13\uffff\1\44\u0152\uffff"+
            "\66\44\3\uffff\1\44\22\uffff\1\44\7\uffff\12\44\4\uffff\12\44"+
            "\25\uffff\10\44\2\uffff\2\44\2\uffff\26\44\1\uffff\7\44\1\uffff"+
            "\1\44\3\uffff\4\44\3\uffff\1\44\36\uffff\2\44\1\uffff\3\44\4"+
            "\uffff\14\44\23\uffff\6\44\4\uffff\2\44\2\uffff\26\44\1\uffff"+
            "\7\44\1\uffff\2\44\1\uffff\2\44\1\uffff\2\44\37\uffff\4\44\1"+
            "\uffff\1\44\7\uffff\12\44\2\uffff\3\44\20\uffff\11\44\1\uffff"+
            "\3\44\1\uffff\26\44\1\uffff\7\44\1\uffff\2\44\1\uffff\5\44\3"+
            "\uffff\1\44\22\uffff\1\44\17\uffff\2\44\4\uffff\12\44\25\uffff"+
            "\10\44\2\uffff\2\44\2\uffff\26\44\1\uffff\7\44\1\uffff\2\44"+
            "\1\uffff\5\44\3\uffff\1\44\36\uffff\2\44\1\uffff\3\44\4\uffff"+
            "\12\44\1\uffff\1\44\21\uffff\1\44\1\uffff\6\44\3\uffff\3\44"+
            "\1\uffff\4\44\3\uffff\2\44\1\uffff\1\44\1\uffff\2\44\3\uffff"+
            "\2\44\3\uffff\3\44\3\uffff\10\44\1\uffff\3\44\55\uffff\11\44"+
            "\25\uffff\10\44\1\uffff\3\44\1\uffff\27\44\1\uffff\12\44\1\uffff"+
            "\5\44\46\uffff\2\44\4\uffff\12\44\25\uffff\10\44\1\uffff\3\44"+
            "\1\uffff\27\44\1\uffff\12\44\1\uffff\5\44\3\uffff\1\44\40\uffff"+
            "\1\44\1\uffff\2\44\4\uffff\12\44\25\uffff\10\44\1\uffff\3\44"+
            "\1\uffff\27\44\1\uffff\20\44\46\uffff\2\44\4\uffff\12\44\25"+
            "\uffff\22\44\3\uffff\30\44\1\uffff\11\44\1\uffff\1\44\2\uffff"+
            "\7\44\72\uffff\60\44\1\uffff\2\44\14\uffff\7\44\11\uffff\12"+
            "\44\47\uffff\2\44\1\uffff\1\44\2\uffff\2\44\1\uffff\1\44\2\uffff"+
            "\1\44\6\uffff\4\44\1\uffff\7\44\1\uffff\3\44\1\uffff\1\44\1"+
            "\uffff\1\44\2\uffff\2\44\1\uffff\4\44\1\uffff\2\44\11\uffff"+
            "\1\44\2\uffff\5\44\1\uffff\1\44\11\uffff\12\44\2\uffff\2\44"+
            "\42\uffff\1\44\37\uffff\12\44\26\uffff\10\44\1\uffff\42\44\35"+
            "\uffff\4\44\164\uffff\42\44\1\uffff\5\44\1\uffff\2\44\25\uffff"+
            "\12\44\6\uffff\6\44\112\uffff\46\44\12\uffff\51\44\7\uffff\132"+
            "\44\5\uffff\104\44\5\uffff\122\44\6\uffff\7\44\1\uffff\77\44"+
            "\1\uffff\1\44\1\uffff\4\44\2\uffff\7\44\1\uffff\1\44\1\uffff"+
            "\4\44\2\uffff\47\44\1\uffff\1\44\1\uffff\4\44\2\uffff\37\44"+
            "\1\uffff\1\44\1\uffff\4\44\2\uffff\7\44\1\uffff\1\44\1\uffff"+
            "\4\44\2\uffff\7\44\1\uffff\7\44\1\uffff\27\44\1\uffff\37\44"+
            "\1\uffff\1\44\1\uffff\4\44\2\uffff\7\44\1\uffff\47\44\1\uffff"+
            "\23\44\16\uffff\11\44\56\uffff\125\44\14\uffff\u026c\44\2\uffff"+
            "\10\44\12\uffff\32\44\5\uffff\113\44\25\uffff\15\44\1\uffff"+
            "\4\44\16\uffff\22\44\16\uffff\22\44\16\uffff\15\44\1\uffff\3"+
            "\44\17\uffff\64\44\43\uffff\1\44\4\uffff\1\44\3\uffff\12\44"+
            "\46\uffff\12\44\6\uffff\130\44\10\uffff\51\44\127\uffff\35\44"+
            "\51\uffff\50\44\2\uffff\5\44\u038b\uffff\154\44\u0094\uffff"+
            "\u009c\44\4\uffff\132\44\6\uffff\26\44\2\uffff\6\44\2\uffff"+
            "\46\44\2\uffff\6\44\2\uffff\10\44\1\uffff\1\44\1\uffff\1\44"+
            "\1\uffff\1\44\1\uffff\37\44\2\uffff\65\44\1\uffff\7\44\1\uffff"+
            "\1\44\3\uffff\3\44\1\uffff\7\44\3\uffff\4\44\2\uffff\6\44\4"+
            "\uffff\15\44\5\uffff\3\44\1\uffff\7\44\164\uffff\1\44\15\uffff"+
            "\1\44\u0082\uffff\1\44\4\uffff\1\44\2\uffff\12\44\1\uffff\1"+
            "\44\3\uffff\5\44\6\uffff\1\44\1\uffff\1\44\1\uffff\1\44\1\uffff"+
            "\4\44\1\uffff\3\44\1\uffff\7\44\3\uffff\3\44\5\uffff\5\44\u0ebb"+
            "\uffff\2\44\52\uffff\5\44\5\uffff\2\44\4\uffff\126\44\6\uffff"+
            "\3\44\1\uffff\132\44\1\uffff\4\44\5\uffff\50\44\4\uffff\136"+
            "\44\21\uffff\30\44\70\uffff\20\44\u0200\uffff\u19b6\44\112\uffff"+
            "\u51a6\44\132\uffff\u048d\44\u0773\uffff\u2ba4\44\u215c\uffff"+
            "\u012e\44\2\uffff\73\44\u0095\uffff\7\44\14\uffff\5\44\5\uffff"+
            "\1\44\1\uffff\12\44\1\uffff\15\44\1\uffff\5\44\1\uffff\1\44"+
            "\1\uffff\2\44\1\uffff\2\44\1\uffff\154\44\41\uffff\u016b\44"+
            "\22\uffff\100\44\2\uffff\66\44\50\uffff\14\44\164\uffff\5\44"+
            "\1\uffff\u0087\44\23\uffff\12\44\7\uffff\32\44\6\uffff\32\44"+
            "\13\uffff\131\44\3\uffff\6\44\2\uffff\6\44\2\uffff\6\44\2\uffff"+
            "\3\44",
            "\1\64\1\63\5\uffff\1\67\5\uffff\12\155\5\uffff\1\70\1\uffff"+
            "\6\154\24\60\1\uffff\1\65\2\uffff\1\62\1\uffff\6\153\24\57\57"+
            "\uffff\1\44\12\uffff\1\44\4\uffff\1\44\5\uffff\27\44\1\uffff"+
            "\37\44\1\uffff\u013f\44\31\uffff\162\44\4\uffff\14\44\16\uffff"+
            "\5\44\11\uffff\1\44\u008b\uffff\1\44\13\uffff\1\44\1\uffff\3"+
            "\44\1\uffff\1\44\1\uffff\24\44\1\uffff\54\44\1\uffff\46\44\1"+
            "\uffff\5\44\4\uffff\u0082\44\10\uffff\105\44\1\uffff\46\44\2"+
            "\uffff\2\44\6\uffff\20\44\41\uffff\46\44\2\uffff\1\44\7\uffff"+
            "\47\44\110\uffff\33\44\5\uffff\3\44\56\uffff\32\44\5\uffff\13"+
            "\44\25\uffff\12\44\4\uffff\2\44\1\uffff\143\44\1\uffff\1\44"+
            "\17\uffff\2\44\7\uffff\17\44\2\uffff\1\44\20\uffff\1\44\1\uffff"+
            "\36\44\35\uffff\3\44\60\uffff\46\44\13\uffff\1\44\u0152\uffff"+
            "\66\44\3\uffff\1\44\22\uffff\1\44\7\uffff\12\44\4\uffff\12\44"+
            "\25\uffff\10\44\2\uffff\2\44\2\uffff\26\44\1\uffff\7\44\1\uffff"+
            "\1\44\3\uffff\4\44\3\uffff\1\44\36\uffff\2\44\1\uffff\3\44\4"+
            "\uffff\14\44\23\uffff\6\44\4\uffff\2\44\2\uffff\26\44\1\uffff"+
            "\7\44\1\uffff\2\44\1\uffff\2\44\1\uffff\2\44\37\uffff\4\44\1"+
            "\uffff\1\44\7\uffff\12\44\2\uffff\3\44\20\uffff\11\44\1\uffff"+
            "\3\44\1\uffff\26\44\1\uffff\7\44\1\uffff\2\44\1\uffff\5\44\3"+
            "\uffff\1\44\22\uffff\1\44\17\uffff\2\44\4\uffff\12\44\25\uffff"+
            "\10\44\2\uffff\2\44\2\uffff\26\44\1\uffff\7\44\1\uffff\2\44"+
            "\1\uffff\5\44\3\uffff\1\44\36\uffff\2\44\1\uffff\3\44\4\uffff"+
            "\12\44\1\uffff\1\44\21\uffff\1\44\1\uffff\6\44\3\uffff\3\44"+
            "\1\uffff\4\44\3\uffff\2\44\1\uffff\1\44\1\uffff\2\44\3\uffff"+
            "\2\44\3\uffff\3\44\3\uffff\10\44\1\uffff\3\44\55\uffff\11\44"+
            "\25\uffff\10\44\1\uffff\3\44\1\uffff\27\44\1\uffff\12\44\1\uffff"+
            "\5\44\46\uffff\2\44\4\uffff\12\44\25\uffff\10\44\1\uffff\3\44"+
            "\1\uffff\27\44\1\uffff\12\44\1\uffff\5\44\3\uffff\1\44\40\uffff"+
            "\1\44\1\uffff\2\44\4\uffff\12\44\25\uffff\10\44\1\uffff\3\44"+
            "\1\uffff\27\44\1\uffff\20\44\46\uffff\2\44\4\uffff\12\44\25"+
            "\uffff\22\44\3\uffff\30\44\1\uffff\11\44\1\uffff\1\44\2\uffff"+
            "\7\44\72\uffff\60\44\1\uffff\2\44\14\uffff\7\44\11\uffff\12"+
            "\44\47\uffff\2\44\1\uffff\1\44\2\uffff\2\44\1\uffff\1\44\2\uffff"+
            "\1\44\6\uffff\4\44\1\uffff\7\44\1\uffff\3\44\1\uffff\1\44\1"+
            "\uffff\1\44\2\uffff\2\44\1\uffff\4\44\1\uffff\2\44\11\uffff"+
            "\1\44\2\uffff\5\44\1\uffff\1\44\11\uffff\12\44\2\uffff\2\44"+
            "\42\uffff\1\44\37\uffff\12\44\26\uffff\10\44\1\uffff\42\44\35"+
            "\uffff\4\44\164\uffff\42\44\1\uffff\5\44\1\uffff\2\44\25\uffff"+
            "\12\44\6\uffff\6\44\112\uffff\46\44\12\uffff\51\44\7\uffff\132"+
            "\44\5\uffff\104\44\5\uffff\122\44\6\uffff\7\44\1\uffff\77\44"+
            "\1\uffff\1\44\1\uffff\4\44\2\uffff\7\44\1\uffff\1\44\1\uffff"+
            "\4\44\2\uffff\47\44\1\uffff\1\44\1\uffff\4\44\2\uffff\37\44"+
            "\1\uffff\1\44\1\uffff\4\44\2\uffff\7\44\1\uffff\1\44\1\uffff"+
            "\4\44\2\uffff\7\44\1\uffff\7\44\1\uffff\27\44\1\uffff\37\44"+
            "\1\uffff\1\44\1\uffff\4\44\2\uffff\7\44\1\uffff\47\44\1\uffff"+
            "\23\44\16\uffff\11\44\56\uffff\125\44\14\uffff\u026c\44\2\uffff"+
            "\10\44\12\uffff\32\44\5\uffff\113\44\25\uffff\15\44\1\uffff"+
            "\4\44\16\uffff\22\44\16\uffff\22\44\16\uffff\15\44\1\uffff\3"+
            "\44\17\uffff\64\44\43\uffff\1\44\4\uffff\1\44\3\uffff\12\44"+
            "\46\uffff\12\44\6\uffff\130\44\10\uffff\51\44\127\uffff\35\44"+
            "\51\uffff\50\44\2\uffff\5\44\u038b\uffff\154\44\u0094\uffff"+
            "\u009c\44\4\uffff\132\44\6\uffff\26\44\2\uffff\6\44\2\uffff"+
            "\46\44\2\uffff\6\44\2\uffff\10\44\1\uffff\1\44\1\uffff\1\44"+
            "\1\uffff\1\44\1\uffff\37\44\2\uffff\65\44\1\uffff\7\44\1\uffff"+
            "\1\44\3\uffff\3\44\1\uffff\7\44\3\uffff\4\44\2\uffff\6\44\4"+
            "\uffff\15\44\5\uffff\3\44\1\uffff\7\44\164\uffff\1\44\15\uffff"+
            "\1\44\u0082\uffff\1\44\4\uffff\1\44\2\uffff\12\44\1\uffff\1"+
            "\44\3\uffff\5\44\6\uffff\1\44\1\uffff\1\44\1\uffff\1\44\1\uffff"+
            "\4\44\1\uffff\3\44\1\uffff\7\44\3\uffff\3\44\5\uffff\5\44\u0ebb"+
            "\uffff\2\44\52\uffff\5\44\5\uffff\2\44\4\uffff\126\44\6\uffff"+
            "\3\44\1\uffff\132\44\1\uffff\4\44\5\uffff\50\44\4\uffff\136"+
            "\44\21\uffff\30\44\70\uffff\20\44\u0200\uffff\u19b6\44\112\uffff"+
            "\u51a6\44\132\uffff\u048d\44\u0773\uffff\u2ba4\44\u215c\uffff"+
            "\u012e\44\2\uffff\73\44\u0095\uffff\7\44\14\uffff\5\44\5\uffff"+
            "\1\44\1\uffff\12\44\1\uffff\15\44\1\uffff\5\44\1\uffff\1\44"+
            "\1\uffff\2\44\1\uffff\2\44\1\uffff\154\44\41\uffff\u016b\44"+
            "\22\uffff\100\44\2\uffff\66\44\50\uffff\14\44\164\uffff\5\44"+
            "\1\uffff\u0087\44\23\uffff\12\44\7\uffff\32\44\6\uffff\32\44"+
            "\13\uffff\131\44\3\uffff\6\44\2\uffff\6\44\2\uffff\6\44\2\uffff"+
            "\3\44",
            "",
            "",
            "",
            "\1\67\5\uffff\12\156\5\uffff\1\70\1\uffff\6\156\24\44\1\uffff"+
            "\1\42\4\uffff\6\156\24\44\57\uffff\1\44\12\uffff\1\44\4\uffff"+
            "\1\44\5\uffff\27\44\1\uffff\37\44\1\uffff\u013f\44\31\uffff"+
            "\162\44\4\uffff\14\44\16\uffff\5\44\11\uffff\1\44\u008b\uffff"+
            "\1\44\13\uffff\1\44\1\uffff\3\44\1\uffff\1\44\1\uffff\24\44"+
            "\1\uffff\54\44\1\uffff\46\44\1\uffff\5\44\4\uffff\u0082\44\10"+
            "\uffff\105\44\1\uffff\46\44\2\uffff\2\44\6\uffff\20\44\41\uffff"+
            "\46\44\2\uffff\1\44\7\uffff\47\44\110\uffff\33\44\5\uffff\3"+
            "\44\56\uffff\32\44\5\uffff\13\44\25\uffff\12\44\4\uffff\2\44"+
            "\1\uffff\143\44\1\uffff\1\44\17\uffff\2\44\7\uffff\17\44\2\uffff"+
            "\1\44\20\uffff\1\44\1\uffff\36\44\35\uffff\3\44\60\uffff\46"+
            "\44\13\uffff\1\44\u0152\uffff\66\44\3\uffff\1\44\22\uffff\1"+
            "\44\7\uffff\12\44\4\uffff\12\44\25\uffff\10\44\2\uffff\2\44"+
            "\2\uffff\26\44\1\uffff\7\44\1\uffff\1\44\3\uffff\4\44\3\uffff"+
            "\1\44\36\uffff\2\44\1\uffff\3\44\4\uffff\14\44\23\uffff\6\44"+
            "\4\uffff\2\44\2\uffff\26\44\1\uffff\7\44\1\uffff\2\44\1\uffff"+
            "\2\44\1\uffff\2\44\37\uffff\4\44\1\uffff\1\44\7\uffff\12\44"+
            "\2\uffff\3\44\20\uffff\11\44\1\uffff\3\44\1\uffff\26\44\1\uffff"+
            "\7\44\1\uffff\2\44\1\uffff\5\44\3\uffff\1\44\22\uffff\1\44\17"+
            "\uffff\2\44\4\uffff\12\44\25\uffff\10\44\2\uffff\2\44\2\uffff"+
            "\26\44\1\uffff\7\44\1\uffff\2\44\1\uffff\5\44\3\uffff\1\44\36"+
            "\uffff\2\44\1\uffff\3\44\4\uffff\12\44\1\uffff\1\44\21\uffff"+
            "\1\44\1\uffff\6\44\3\uffff\3\44\1\uffff\4\44\3\uffff\2\44\1"+
            "\uffff\1\44\1\uffff\2\44\3\uffff\2\44\3\uffff\3\44\3\uffff\10"+
            "\44\1\uffff\3\44\55\uffff\11\44\25\uffff\10\44\1\uffff\3\44"+
            "\1\uffff\27\44\1\uffff\12\44\1\uffff\5\44\46\uffff\2\44\4\uffff"+
            "\12\44\25\uffff\10\44\1\uffff\3\44\1\uffff\27\44\1\uffff\12"+
            "\44\1\uffff\5\44\3\uffff\1\44\40\uffff\1\44\1\uffff\2\44\4\uffff"+
            "\12\44\25\uffff\10\44\1\uffff\3\44\1\uffff\27\44\1\uffff\20"+
            "\44\46\uffff\2\44\4\uffff\12\44\25\uffff\22\44\3\uffff\30\44"+
            "\1\uffff\11\44\1\uffff\1\44\2\uffff\7\44\72\uffff\60\44\1\uffff"+
            "\2\44\14\uffff\7\44\11\uffff\12\44\47\uffff\2\44\1\uffff\1\44"+
            "\2\uffff\2\44\1\uffff\1\44\2\uffff\1\44\6\uffff\4\44\1\uffff"+
            "\7\44\1\uffff\3\44\1\uffff\1\44\1\uffff\1\44\2\uffff\2\44\1"+
            "\uffff\4\44\1\uffff\2\44\11\uffff\1\44\2\uffff\5\44\1\uffff"+
            "\1\44\11\uffff\12\44\2\uffff\2\44\42\uffff\1\44\37\uffff\12"+
            "\44\26\uffff\10\44\1\uffff\42\44\35\uffff\4\44\164\uffff\42"+
            "\44\1\uffff\5\44\1\uffff\2\44\25\uffff\12\44\6\uffff\6\44\112"+
            "\uffff\46\44\12\uffff\51\44\7\uffff\132\44\5\uffff\104\44\5"+
            "\uffff\122\44\6\uffff\7\44\1\uffff\77\44\1\uffff\1\44\1\uffff"+
            "\4\44\2\uffff\7\44\1\uffff\1\44\1\uffff\4\44\2\uffff\47\44\1"+
            "\uffff\1\44\1\uffff\4\44\2\uffff\37\44\1\uffff\1\44\1\uffff"+
            "\4\44\2\uffff\7\44\1\uffff\1\44\1\uffff\4\44\2\uffff\7\44\1"+
            "\uffff\7\44\1\uffff\27\44\1\uffff\37\44\1\uffff\1\44\1\uffff"+
            "\4\44\2\uffff\7\44\1\uffff\47\44\1\uffff\23\44\16\uffff\11\44"+
            "\56\uffff\125\44\14\uffff\u026c\44\2\uffff\10\44\12\uffff\32"+
            "\44\5\uffff\113\44\25\uffff\15\44\1\uffff\4\44\16\uffff\22\44"+
            "\16\uffff\22\44\16\uffff\15\44\1\uffff\3\44\17\uffff\64\44\43"+
            "\uffff\1\44\4\uffff\1\44\3\uffff\12\44\46\uffff\12\44\6\uffff"+
            "\130\44\10\uffff\51\44\127\uffff\35\44\51\uffff\50\44\2\uffff"+
            "\5\44\u038b\uffff\154\44\u0094\uffff\u009c\44\4\uffff\132\44"+
            "\6\uffff\26\44\2\uffff\6\44\2\uffff\46\44\2\uffff\6\44\2\uffff"+
            "\10\44\1\uffff\1\44\1\uffff\1\44\1\uffff\1\44\1\uffff\37\44"+
            "\2\uffff\65\44\1\uffff\7\44\1\uffff\1\44\3\uffff\3\44\1\uffff"+
            "\7\44\3\uffff\4\44\2\uffff\6\44\4\uffff\15\44\5\uffff\3\44\1"+
            "\uffff\7\44\164\uffff\1\44\15\uffff\1\44\u0082\uffff\1\44\4"+
            "\uffff\1\44\2\uffff\12\44\1\uffff\1\44\3\uffff\5\44\6\uffff"+
            "\1\44\1\uffff\1\44\1\uffff\1\44\1\uffff\4\44\1\uffff\3\44\1"+
            "\uffff\7\44\3\uffff\3\44\5\uffff\5\44\u0ebb\uffff\2\44\52\uffff"+
            "\5\44\5\uffff\2\44\4\uffff\126\44\6\uffff\3\44\1\uffff\132\44"+
            "\1\uffff\4\44\5\uffff\50\44\4\uffff\136\44\21\uffff\30\44\70"+
            "\uffff\20\44\u0200\uffff\u19b6\44\112\uffff\u51a6\44\132\uffff"+
            "\u048d\44\u0773\uffff\u2ba4\44\u215c\uffff\u012e\44\2\uffff"+
            "\73\44\u0095\uffff\7\44\14\uffff\5\44\5\uffff\1\44\1\uffff\12"+
            "\44\1\uffff\15\44\1\uffff\5\44\1\uffff\1\44\1\uffff\2\44\1\uffff"+
            "\2\44\1\uffff\154\44\41\uffff\u016b\44\22\uffff\100\44\2\uffff"+
            "\66\44\50\uffff\14\44\164\uffff\5\44\1\uffff\u0087\44\23\uffff"+
            "\12\44\7\uffff\32\44\6\uffff\32\44\13\uffff\131\44\3\uffff\6"+
            "\44\2\uffff\6\44\2\uffff\6\44\2\uffff\3\44",
            "\1\64\1\63\5\uffff\1\67\5\uffff\12\161\5\uffff\1\70\1\uffff"+
            "\6\160\24\60\1\uffff\1\65\2\uffff\1\62\1\uffff\6\157\24\57\57"+
            "\uffff\1\44\12\uffff\1\44\4\uffff\1\44\5\uffff\27\44\1\uffff"+
            "\37\44\1\uffff\u013f\44\31\uffff\162\44\4\uffff\14\44\16\uffff"+
            "\5\44\11\uffff\1\44\u008b\uffff\1\44\13\uffff\1\44\1\uffff\3"+
            "\44\1\uffff\1\44\1\uffff\24\44\1\uffff\54\44\1\uffff\46\44\1"+
            "\uffff\5\44\4\uffff\u0082\44\10\uffff\105\44\1\uffff\46\44\2"+
            "\uffff\2\44\6\uffff\20\44\41\uffff\46\44\2\uffff\1\44\7\uffff"+
            "\47\44\110\uffff\33\44\5\uffff\3\44\56\uffff\32\44\5\uffff\13"+
            "\44\25\uffff\12\44\4\uffff\2\44\1\uffff\143\44\1\uffff\1\44"+
            "\17\uffff\2\44\7\uffff\17\44\2\uffff\1\44\20\uffff\1\44\1\uffff"+
            "\36\44\35\uffff\3\44\60\uffff\46\44\13\uffff\1\44\u0152\uffff"+
            "\66\44\3\uffff\1\44\22\uffff\1\44\7\uffff\12\44\4\uffff\12\44"+
            "\25\uffff\10\44\2\uffff\2\44\2\uffff\26\44\1\uffff\7\44\1\uffff"+
            "\1\44\3\uffff\4\44\3\uffff\1\44\36\uffff\2\44\1\uffff\3\44\4"+
            "\uffff\14\44\23\uffff\6\44\4\uffff\2\44\2\uffff\26\44\1\uffff"+
            "\7\44\1\uffff\2\44\1\uffff\2\44\1\uffff\2\44\37\uffff\4\44\1"+
            "\uffff\1\44\7\uffff\12\44\2\uffff\3\44\20\uffff\11\44\1\uffff"+
            "\3\44\1\uffff\26\44\1\uffff\7\44\1\uffff\2\44\1\uffff\5\44\3"+
            "\uffff\1\44\22\uffff\1\44\17\uffff\2\44\4\uffff\12\44\25\uffff"+
            "\10\44\2\uffff\2\44\2\uffff\26\44\1\uffff\7\44\1\uffff\2\44"+
            "\1\uffff\5\44\3\uffff\1\44\36\uffff\2\44\1\uffff\3\44\4\uffff"+
            "\12\44\1\uffff\1\44\21\uffff\1\44\1\uffff\6\44\3\uffff\3\44"+
            "\1\uffff\4\44\3\uffff\2\44\1\uffff\1\44\1\uffff\2\44\3\uffff"+
            "\2\44\3\uffff\3\44\3\uffff\10\44\1\uffff\3\44\55\uffff\11\44"+
            "\25\uffff\10\44\1\uffff\3\44\1\uffff\27\44\1\uffff\12\44\1\uffff"+
            "\5\44\46\uffff\2\44\4\uffff\12\44\25\uffff\10\44\1\uffff\3\44"+
            "\1\uffff\27\44\1\uffff\12\44\1\uffff\5\44\3\uffff\1\44\40\uffff"+
            "\1\44\1\uffff\2\44\4\uffff\12\44\25\uffff\10\44\1\uffff\3\44"+
            "\1\uffff\27\44\1\uffff\20\44\46\uffff\2\44\4\uffff\12\44\25"+
            "\uffff\22\44\3\uffff\30\44\1\uffff\11\44\1\uffff\1\44\2\uffff"+
            "\7\44\72\uffff\60\44\1\uffff\2\44\14\uffff\7\44\11\uffff\12"+
            "\44\47\uffff\2\44\1\uffff\1\44\2\uffff\2\44\1\uffff\1\44\2\uffff"+
            "\1\44\6\uffff\4\44\1\uffff\7\44\1\uffff\3\44\1\uffff\1\44\1"+
            "\uffff\1\44\2\uffff\2\44\1\uffff\4\44\1\uffff\2\44\11\uffff"+
            "\1\44\2\uffff\5\44\1\uffff\1\44\11\uffff\12\44\2\uffff\2\44"+
            "\42\uffff\1\44\37\uffff\12\44\26\uffff\10\44\1\uffff\42\44\35"+
            "\uffff\4\44\164\uffff\42\44\1\uffff\5\44\1\uffff\2\44\25\uffff"+
            "\12\44\6\uffff\6\44\112\uffff\46\44\12\uffff\51\44\7\uffff\132"+
            "\44\5\uffff\104\44\5\uffff\122\44\6\uffff\7\44\1\uffff\77\44"+
            "\1\uffff\1\44\1\uffff\4\44\2\uffff\7\44\1\uffff\1\44\1\uffff"+
            "\4\44\2\uffff\47\44\1\uffff\1\44\1\uffff\4\44\2\uffff\37\44"+
            "\1\uffff\1\44\1\uffff\4\44\2\uffff\7\44\1\uffff\1\44\1\uffff"+
            "\4\44\2\uffff\7\44\1\uffff\7\44\1\uffff\27\44\1\uffff\37\44"+
            "\1\uffff\1\44\1\uffff\4\44\2\uffff\7\44\1\uffff\47\44\1\uffff"+
            "\23\44\16\uffff\11\44\56\uffff\125\44\14\uffff\u026c\44\2\uffff"+
            "\10\44\12\uffff\32\44\5\uffff\113\44\25\uffff\15\44\1\uffff"+
            "\4\44\16\uffff\22\44\16\uffff\22\44\16\uffff\15\44\1\uffff\3"+
            "\44\17\uffff\64\44\43\uffff\1\44\4\uffff\1\44\3\uffff\12\44"+
            "\46\uffff\12\44\6\uffff\130\44\10\uffff\51\44\127\uffff\35\44"+
            "\51\uffff\50\44\2\uffff\5\44\u038b\uffff\154\44\u0094\uffff"+
            "\u009c\44\4\uffff\132\44\6\uffff\26\44\2\uffff\6\44\2\uffff"+
            "\46\44\2\uffff\6\44\2\uffff\10\44\1\uffff\1\44\1\uffff\1\44"+
            "\1\uffff\1\44\1\uffff\37\44\2\uffff\65\44\1\uffff\7\44\1\uffff"+
            "\1\44\3\uffff\3\44\1\uffff\7\44\3\uffff\4\44\2\uffff\6\44\4"+
            "\uffff\15\44\5\uffff\3\44\1\uffff\7\44\164\uffff\1\44\15\uffff"+
            "\1\44\u0082\uffff\1\44\4\uffff\1\44\2\uffff\12\44\1\uffff\1"+
            "\44\3\uffff\5\44\6\uffff\1\44\1\uffff\1\44\1\uffff\1\44\1\uffff"+
            "\4\44\1\uffff\3\44\1\uffff\7\44\3\uffff\3\44\5\uffff\5\44\u0ebb"+
            "\uffff\2\44\52\uffff\5\44\5\uffff\2\44\4\uffff\126\44\6\uffff"+
            "\3\44\1\uffff\132\44\1\uffff\4\44\5\uffff\50\44\4\uffff\136"+
            "\44\21\uffff\30\44\70\uffff\20\44\u0200\uffff\u19b6\44\112\uffff"+
            "\u51a6\44\132\uffff\u048d\44\u0773\uffff\u2ba4\44\u215c\uffff"+
            "\u012e\44\2\uffff\73\44\u0095\uffff\7\44\14\uffff\5\44\5\uffff"+
            "\1\44\1\uffff\12\44\1\uffff\15\44\1\uffff\5\44\1\uffff\1\44"+
            "\1\uffff\2\44\1\uffff\2\44\1\uffff\154\44\41\uffff\u016b\44"+
            "\22\uffff\100\44\2\uffff\66\44\50\uffff\14\44\164\uffff\5\44"+
            "\1\uffff\u0087\44\23\uffff\12\44\7\uffff\32\44\6\uffff\32\44"+
            "\13\uffff\131\44\3\uffff\6\44\2\uffff\6\44\2\uffff\6\44\2\uffff"+
            "\3\44",
            "\1\64\1\63\5\uffff\1\67\5\uffff\12\161\5\uffff\1\70\1\uffff"+
            "\6\160\24\60\1\uffff\1\65\2\uffff\1\62\1\uffff\6\157\24\57\57"+
            "\uffff\1\44\12\uffff\1\44\4\uffff\1\44\5\uffff\27\44\1\uffff"+
            "\37\44\1\uffff\u013f\44\31\uffff\162\44\4\uffff\14\44\16\uffff"+
            "\5\44\11\uffff\1\44\u008b\uffff\1\44\13\uffff\1\44\1\uffff\3"+
            "\44\1\uffff\1\44\1\uffff\24\44\1\uffff\54\44\1\uffff\46\44\1"+
            "\uffff\5\44\4\uffff\u0082\44\10\uffff\105\44\1\uffff\46\44\2"+
            "\uffff\2\44\6\uffff\20\44\41\uffff\46\44\2\uffff\1\44\7\uffff"+
            "\47\44\110\uffff\33\44\5\uffff\3\44\56\uffff\32\44\5\uffff\13"+
            "\44\25\uffff\12\44\4\uffff\2\44\1\uffff\143\44\1\uffff\1\44"+
            "\17\uffff\2\44\7\uffff\17\44\2\uffff\1\44\20\uffff\1\44\1\uffff"+
            "\36\44\35\uffff\3\44\60\uffff\46\44\13\uffff\1\44\u0152\uffff"+
            "\66\44\3\uffff\1\44\22\uffff\1\44\7\uffff\12\44\4\uffff\12\44"+
            "\25\uffff\10\44\2\uffff\2\44\2\uffff\26\44\1\uffff\7\44\1\uffff"+
            "\1\44\3\uffff\4\44\3\uffff\1\44\36\uffff\2\44\1\uffff\3\44\4"+
            "\uffff\14\44\23\uffff\6\44\4\uffff\2\44\2\uffff\26\44\1\uffff"+
            "\7\44\1\uffff\2\44\1\uffff\2\44\1\uffff\2\44\37\uffff\4\44\1"+
            "\uffff\1\44\7\uffff\12\44\2\uffff\3\44\20\uffff\11\44\1\uffff"+
            "\3\44\1\uffff\26\44\1\uffff\7\44\1\uffff\2\44\1\uffff\5\44\3"+
            "\uffff\1\44\22\uffff\1\44\17\uffff\2\44\4\uffff\12\44\25\uffff"+
            "\10\44\2\uffff\2\44\2\uffff\26\44\1\uffff\7\44\1\uffff\2\44"+
            "\1\uffff\5\44\3\uffff\1\44\36\uffff\2\44\1\uffff\3\44\4\uffff"+
            "\12\44\1\uffff\1\44\21\uffff\1\44\1\uffff\6\44\3\uffff\3\44"+
            "\1\uffff\4\44\3\uffff\2\44\1\uffff\1\44\1\uffff\2\44\3\uffff"+
            "\2\44\3\uffff\3\44\3\uffff\10\44\1\uffff\3\44\55\uffff\11\44"+
            "\25\uffff\10\44\1\uffff\3\44\1\uffff\27\44\1\uffff\12\44\1\uffff"+
            "\5\44\46\uffff\2\44\4\uffff\12\44\25\uffff\10\44\1\uffff\3\44"+
            "\1\uffff\27\44\1\uffff\12\44\1\uffff\5\44\3\uffff\1\44\40\uffff"+
            "\1\44\1\uffff\2\44\4\uffff\12\44\25\uffff\10\44\1\uffff\3\44"+
            "\1\uffff\27\44\1\uffff\20\44\46\uffff\2\44\4\uffff\12\44\25"+
            "\uffff\22\44\3\uffff\30\44\1\uffff\11\44\1\uffff\1\44\2\uffff"+
            "\7\44\72\uffff\60\44\1\uffff\2\44\14\uffff\7\44\11\uffff\12"+
            "\44\47\uffff\2\44\1\uffff\1\44\2\uffff\2\44\1\uffff\1\44\2\uffff"+
            "\1\44\6\uffff\4\44\1\uffff\7\44\1\uffff\3\44\1\uffff\1\44\1"+
            "\uffff\1\44\2\uffff\2\44\1\uffff\4\44\1\uffff\2\44\11\uffff"+
            "\1\44\2\uffff\5\44\1\uffff\1\44\11\uffff\12\44\2\uffff\2\44"+
            "\42\uffff\1\44\37\uffff\12\44\26\uffff\10\44\1\uffff\42\44\35"+
            "\uffff\4\44\164\uffff\42\44\1\uffff\5\44\1\uffff\2\44\25\uffff"+
            "\12\44\6\uffff\6\44\112\uffff\46\44\12\uffff\51\44\7\uffff\132"+
            "\44\5\uffff\104\44\5\uffff\122\44\6\uffff\7\44\1\uffff\77\44"+
            "\1\uffff\1\44\1\uffff\4\44\2\uffff\7\44\1\uffff\1\44\1\uffff"+
            "\4\44\2\uffff\47\44\1\uffff\1\44\1\uffff\4\44\2\uffff\37\44"+
            "\1\uffff\1\44\1\uffff\4\44\2\uffff\7\44\1\uffff\1\44\1\uffff"+
            "\4\44\2\uffff\7\44\1\uffff\7\44\1\uffff\27\44\1\uffff\37\44"+
            "\1\uffff\1\44\1\uffff\4\44\2\uffff\7\44\1\uffff\47\44\1\uffff"+
            "\23\44\16\uffff\11\44\56\uffff\125\44\14\uffff\u026c\44\2\uffff"+
            "\10\44\12\uffff\32\44\5\uffff\113\44\25\uffff\15\44\1\uffff"+
            "\4\44\16\uffff\22\44\16\uffff\22\44\16\uffff\15\44\1\uffff\3"+
            "\44\17\uffff\64\44\43\uffff\1\44\4\uffff\1\44\3\uffff\12\44"+
            "\46\uffff\12\44\6\uffff\130\44\10\uffff\51\44\127\uffff\35\44"+
            "\51\uffff\50\44\2\uffff\5\44\u038b\uffff\154\44\u0094\uffff"+
            "\u009c\44\4\uffff\132\44\6\uffff\26\44\2\uffff\6\44\2\uffff"+
            "\46\44\2\uffff\6\44\2\uffff\10\44\1\uffff\1\44\1\uffff\1\44"+
            "\1\uffff\1\44\1\uffff\37\44\2\uffff\65\44\1\uffff\7\44\1\uffff"+
            "\1\44\3\uffff\3\44\1\uffff\7\44\3\uffff\4\44\2\uffff\6\44\4"+
            "\uffff\15\44\5\uffff\3\44\1\uffff\7\44\164\uffff\1\44\15\uffff"+
            "\1\44\u0082\uffff\1\44\4\uffff\1\44\2\uffff\12\44\1\uffff\1"+
            "\44\3\uffff\5\44\6\uffff\1\44\1\uffff\1\44\1\uffff\1\44\1\uffff"+
            "\4\44\1\uffff\3\44\1\uffff\7\44\3\uffff\3\44\5\uffff\5\44\u0ebb"+
            "\uffff\2\44\52\uffff\5\44\5\uffff\2\44\4\uffff\126\44\6\uffff"+
            "\3\44\1\uffff\132\44\1\uffff\4\44\5\uffff\50\44\4\uffff\136"+
            "\44\21\uffff\30\44\70\uffff\20\44\u0200\uffff\u19b6\44\112\uffff"+
            "\u51a6\44\132\uffff\u048d\44\u0773\uffff\u2ba4\44\u215c\uffff"+
            "\u012e\44\2\uffff\73\44\u0095\uffff\7\44\14\uffff\5\44\5\uffff"+
            "\1\44\1\uffff\12\44\1\uffff\15\44\1\uffff\5\44\1\uffff\1\44"+
            "\1\uffff\2\44\1\uffff\2\44\1\uffff\154\44\41\uffff\u016b\44"+
            "\22\uffff\100\44\2\uffff\66\44\50\uffff\14\44\164\uffff\5\44"+
            "\1\uffff\u0087\44\23\uffff\12\44\7\uffff\32\44\6\uffff\32\44"+
            "\13\uffff\131\44\3\uffff\6\44\2\uffff\6\44\2\uffff\6\44\2\uffff"+
            "\3\44",
            "\1\64\1\63\5\uffff\1\67\5\uffff\12\161\5\uffff\1\70\1\uffff"+
            "\6\160\24\60\1\uffff\1\65\2\uffff\1\62\1\uffff\6\157\24\57\57"+
            "\uffff\1\44\12\uffff\1\44\4\uffff\1\44\5\uffff\27\44\1\uffff"+
            "\37\44\1\uffff\u013f\44\31\uffff\162\44\4\uffff\14\44\16\uffff"+
            "\5\44\11\uffff\1\44\u008b\uffff\1\44\13\uffff\1\44\1\uffff\3"+
            "\44\1\uffff\1\44\1\uffff\24\44\1\uffff\54\44\1\uffff\46\44\1"+
            "\uffff\5\44\4\uffff\u0082\44\10\uffff\105\44\1\uffff\46\44\2"+
            "\uffff\2\44\6\uffff\20\44\41\uffff\46\44\2\uffff\1\44\7\uffff"+
            "\47\44\110\uffff\33\44\5\uffff\3\44\56\uffff\32\44\5\uffff\13"+
            "\44\25\uffff\12\44\4\uffff\2\44\1\uffff\143\44\1\uffff\1\44"+
            "\17\uffff\2\44\7\uffff\17\44\2\uffff\1\44\20\uffff\1\44\1\uffff"+
            "\36\44\35\uffff\3\44\60\uffff\46\44\13\uffff\1\44\u0152\uffff"+
            "\66\44\3\uffff\1\44\22\uffff\1\44\7\uffff\12\44\4\uffff\12\44"+
            "\25\uffff\10\44\2\uffff\2\44\2\uffff\26\44\1\uffff\7\44\1\uffff"+
            "\1\44\3\uffff\4\44\3\uffff\1\44\36\uffff\2\44\1\uffff\3\44\4"+
            "\uffff\14\44\23\uffff\6\44\4\uffff\2\44\2\uffff\26\44\1\uffff"+
            "\7\44\1\uffff\2\44\1\uffff\2\44\1\uffff\2\44\37\uffff\4\44\1"+
            "\uffff\1\44\7\uffff\12\44\2\uffff\3\44\20\uffff\11\44\1\uffff"+
            "\3\44\1\uffff\26\44\1\uffff\7\44\1\uffff\2\44\1\uffff\5\44\3"+
            "\uffff\1\44\22\uffff\1\44\17\uffff\2\44\4\uffff\12\44\25\uffff"+
            "\10\44\2\uffff\2\44\2\uffff\26\44\1\uffff\7\44\1\uffff\2\44"+
            "\1\uffff\5\44\3\uffff\1\44\36\uffff\2\44\1\uffff\3\44\4\uffff"+
            "\12\44\1\uffff\1\44\21\uffff\1\44\1\uffff\6\44\3\uffff\3\44"+
            "\1\uffff\4\44\3\uffff\2\44\1\uffff\1\44\1\uffff\2\44\3\uffff"+
            "\2\44\3\uffff\3\44\3\uffff\10\44\1\uffff\3\44\55\uffff\11\44"+
            "\25\uffff\10\44\1\uffff\3\44\1\uffff\27\44\1\uffff\12\44\1\uffff"+
            "\5\44\46\uffff\2\44\4\uffff\12\44\25\uffff\10\44\1\uffff\3\44"+
            "\1\uffff\27\44\1\uffff\12\44\1\uffff\5\44\3\uffff\1\44\40\uffff"+
            "\1\44\1\uffff\2\44\4\uffff\12\44\25\uffff\10\44\1\uffff\3\44"+
            "\1\uffff\27\44\1\uffff\20\44\46\uffff\2\44\4\uffff\12\44\25"+
            "\uffff\22\44\3\uffff\30\44\1\uffff\11\44\1\uffff\1\44\2\uffff"+
            "\7\44\72\uffff\60\44\1\uffff\2\44\14\uffff\7\44\11\uffff\12"+
            "\44\47\uffff\2\44\1\uffff\1\44\2\uffff\2\44\1\uffff\1\44\2\uffff"+
            "\1\44\6\uffff\4\44\1\uffff\7\44\1\uffff\3\44\1\uffff\1\44\1"+
            "\uffff\1\44\2\uffff\2\44\1\uffff\4\44\1\uffff\2\44\11\uffff"+
            "\1\44\2\uffff\5\44\1\uffff\1\44\11\uffff\12\44\2\uffff\2\44"+
            "\42\uffff\1\44\37\uffff\12\44\26\uffff\10\44\1\uffff\42\44\35"+
            "\uffff\4\44\164\uffff\42\44\1\uffff\5\44\1\uffff\2\44\25\uffff"+
            "\12\44\6\uffff\6\44\112\uffff\46\44\12\uffff\51\44\7\uffff\132"+
            "\44\5\uffff\104\44\5\uffff\122\44\6\uffff\7\44\1\uffff\77\44"+
            "\1\uffff\1\44\1\uffff\4\44\2\uffff\7\44\1\uffff\1\44\1\uffff"+
            "\4\44\2\uffff\47\44\1\uffff\1\44\1\uffff\4\44\2\uffff\37\44"+
            "\1\uffff\1\44\1\uffff\4\44\2\uffff\7\44\1\uffff\1\44\1\uffff"+
            "\4\44\2\uffff\7\44\1\uffff\7\44\1\uffff\27\44\1\uffff\37\44"+
            "\1\uffff\1\44\1\uffff\4\44\2\uffff\7\44\1\uffff\47\44\1\uffff"+
            "\23\44\16\uffff\11\44\56\uffff\125\44\14\uffff\u026c\44\2\uffff"+
            "\10\44\12\uffff\32\44\5\uffff\113\44\25\uffff\15\44\1\uffff"+
            "\4\44\16\uffff\22\44\16\uffff\22\44\16\uffff\15\44\1\uffff\3"+
            "\44\17\uffff\64\44\43\uffff\1\44\4\uffff\1\44\3\uffff\12\44"+
            "\46\uffff\12\44\6\uffff\130\44\10\uffff\51\44\127\uffff\35\44"+
            "\51\uffff\50\44\2\uffff\5\44\u038b\uffff\154\44\u0094\uffff"+
            "\u009c\44\4\uffff\132\44\6\uffff\26\44\2\uffff\6\44\2\uffff"+
            "\46\44\2\uffff\6\44\2\uffff\10\44\1\uffff\1\44\1\uffff\1\44"+
            "\1\uffff\1\44\1\uffff\37\44\2\uffff\65\44\1\uffff\7\44\1\uffff"+
            "\1\44\3\uffff\3\44\1\uffff\7\44\3\uffff\4\44\2\uffff\6\44\4"+
            "\uffff\15\44\5\uffff\3\44\1\uffff\7\44\164\uffff\1\44\15\uffff"+
            "\1\44\u0082\uffff\1\44\4\uffff\1\44\2\uffff\12\44\1\uffff\1"+
            "\44\3\uffff\5\44\6\uffff\1\44\1\uffff\1\44\1\uffff\1\44\1\uffff"+
            "\4\44\1\uffff\3\44\1\uffff\7\44\3\uffff\3\44\5\uffff\5\44\u0ebb"+
            "\uffff\2\44\52\uffff\5\44\5\uffff\2\44\4\uffff\126\44\6\uffff"+
            "\3\44\1\uffff\132\44\1\uffff\4\44\5\uffff\50\44\4\uffff\136"+
            "\44\21\uffff\30\44\70\uffff\20\44\u0200\uffff\u19b6\44\112\uffff"+
            "\u51a6\44\132\uffff\u048d\44\u0773\uffff\u2ba4\44\u215c\uffff"+
            "\u012e\44\2\uffff\73\44\u0095\uffff\7\44\14\uffff\5\44\5\uffff"+
            "\1\44\1\uffff\12\44\1\uffff\15\44\1\uffff\5\44\1\uffff\1\44"+
            "\1\uffff\2\44\1\uffff\2\44\1\uffff\154\44\41\uffff\u016b\44"+
            "\22\uffff\100\44\2\uffff\66\44\50\uffff\14\44\164\uffff\5\44"+
            "\1\uffff\u0087\44\23\uffff\12\44\7\uffff\32\44\6\uffff\32\44"+
            "\13\uffff\131\44\3\uffff\6\44\2\uffff\6\44\2\uffff\6\44\2\uffff"+
            "\3\44",
            "\1\67\5\uffff\12\162\5\uffff\1\70\1\uffff\6\162\24\44\1\uffff"+
            "\1\42\4\uffff\6\162\24\44\57\uffff\1\44\12\uffff\1\44\4\uffff"+
            "\1\44\5\uffff\27\44\1\uffff\37\44\1\uffff\u013f\44\31\uffff"+
            "\162\44\4\uffff\14\44\16\uffff\5\44\11\uffff\1\44\u008b\uffff"+
            "\1\44\13\uffff\1\44\1\uffff\3\44\1\uffff\1\44\1\uffff\24\44"+
            "\1\uffff\54\44\1\uffff\46\44\1\uffff\5\44\4\uffff\u0082\44\10"+
            "\uffff\105\44\1\uffff\46\44\2\uffff\2\44\6\uffff\20\44\41\uffff"+
            "\46\44\2\uffff\1\44\7\uffff\47\44\110\uffff\33\44\5\uffff\3"+
            "\44\56\uffff\32\44\5\uffff\13\44\25\uffff\12\44\4\uffff\2\44"+
            "\1\uffff\143\44\1\uffff\1\44\17\uffff\2\44\7\uffff\17\44\2\uffff"+
            "\1\44\20\uffff\1\44\1\uffff\36\44\35\uffff\3\44\60\uffff\46"+
            "\44\13\uffff\1\44\u0152\uffff\66\44\3\uffff\1\44\22\uffff\1"+
            "\44\7\uffff\12\44\4\uffff\12\44\25\uffff\10\44\2\uffff\2\44"+
            "\2\uffff\26\44\1\uffff\7\44\1\uffff\1\44\3\uffff\4\44\3\uffff"+
            "\1\44\36\uffff\2\44\1\uffff\3\44\4\uffff\14\44\23\uffff\6\44"+
            "\4\uffff\2\44\2\uffff\26\44\1\uffff\7\44\1\uffff\2\44\1\uffff"+
            "\2\44\1\uffff\2\44\37\uffff\4\44\1\uffff\1\44\7\uffff\12\44"+
            "\2\uffff\3\44\20\uffff\11\44\1\uffff\3\44\1\uffff\26\44\1\uffff"+
            "\7\44\1\uffff\2\44\1\uffff\5\44\3\uffff\1\44\22\uffff\1\44\17"+
            "\uffff\2\44\4\uffff\12\44\25\uffff\10\44\2\uffff\2\44\2\uffff"+
            "\26\44\1\uffff\7\44\1\uffff\2\44\1\uffff\5\44\3\uffff\1\44\36"+
            "\uffff\2\44\1\uffff\3\44\4\uffff\12\44\1\uffff\1\44\21\uffff"+
            "\1\44\1\uffff\6\44\3\uffff\3\44\1\uffff\4\44\3\uffff\2\44\1"+
            "\uffff\1\44\1\uffff\2\44\3\uffff\2\44\3\uffff\3\44\3\uffff\10"+
            "\44\1\uffff\3\44\55\uffff\11\44\25\uffff\10\44\1\uffff\3\44"+
            "\1\uffff\27\44\1\uffff\12\44\1\uffff\5\44\46\uffff\2\44\4\uffff"+
            "\12\44\25\uffff\10\44\1\uffff\3\44\1\uffff\27\44\1\uffff\12"+
            "\44\1\uffff\5\44\3\uffff\1\44\40\uffff\1\44\1\uffff\2\44\4\uffff"+
            "\12\44\25\uffff\10\44\1\uffff\3\44\1\uffff\27\44\1\uffff\20"+
            "\44\46\uffff\2\44\4\uffff\12\44\25\uffff\22\44\3\uffff\30\44"+
            "\1\uffff\11\44\1\uffff\1\44\2\uffff\7\44\72\uffff\60\44\1\uffff"+
            "\2\44\14\uffff\7\44\11\uffff\12\44\47\uffff\2\44\1\uffff\1\44"+
            "\2\uffff\2\44\1\uffff\1\44\2\uffff\1\44\6\uffff\4\44\1\uffff"+
            "\7\44\1\uffff\3\44\1\uffff\1\44\1\uffff\1\44\2\uffff\2\44\1"+
            "\uffff\4\44\1\uffff\2\44\11\uffff\1\44\2\uffff\5\44\1\uffff"+
            "\1\44\11\uffff\12\44\2\uffff\2\44\42\uffff\1\44\37\uffff\12"+
            "\44\26\uffff\10\44\1\uffff\42\44\35\uffff\4\44\164\uffff\42"+
            "\44\1\uffff\5\44\1\uffff\2\44\25\uffff\12\44\6\uffff\6\44\112"+
            "\uffff\46\44\12\uffff\51\44\7\uffff\132\44\5\uffff\104\44\5"+
            "\uffff\122\44\6\uffff\7\44\1\uffff\77\44\1\uffff\1\44\1\uffff"+
            "\4\44\2\uffff\7\44\1\uffff\1\44\1\uffff\4\44\2\uffff\47\44\1"+
            "\uffff\1\44\1\uffff\4\44\2\uffff\37\44\1\uffff\1\44\1\uffff"+
            "\4\44\2\uffff\7\44\1\uffff\1\44\1\uffff\4\44\2\uffff\7\44\1"+
            "\uffff\7\44\1\uffff\27\44\1\uffff\37\44\1\uffff\1\44\1\uffff"+
            "\4\44\2\uffff\7\44\1\uffff\47\44\1\uffff\23\44\16\uffff\11\44"+
            "\56\uffff\125\44\14\uffff\u026c\44\2\uffff\10\44\12\uffff\32"+
            "\44\5\uffff\113\44\25\uffff\15\44\1\uffff\4\44\16\uffff\22\44"+
            "\16\uffff\22\44\16\uffff\15\44\1\uffff\3\44\17\uffff\64\44\43"+
            "\uffff\1\44\4\uffff\1\44\3\uffff\12\44\46\uffff\12\44\6\uffff"+
            "\130\44\10\uffff\51\44\127\uffff\35\44\51\uffff\50\44\2\uffff"+
            "\5\44\u038b\uffff\154\44\u0094\uffff\u009c\44\4\uffff\132\44"+
            "\6\uffff\26\44\2\uffff\6\44\2\uffff\46\44\2\uffff\6\44\2\uffff"+
            "\10\44\1\uffff\1\44\1\uffff\1\44\1\uffff\1\44\1\uffff\37\44"+
            "\2\uffff\65\44\1\uffff\7\44\1\uffff\1\44\3\uffff\3\44\1\uffff"+
            "\7\44\3\uffff\4\44\2\uffff\6\44\4\uffff\15\44\5\uffff\3\44\1"+
            "\uffff\7\44\164\uffff\1\44\15\uffff\1\44\u0082\uffff\1\44\4"+
            "\uffff\1\44\2\uffff\12\44\1\uffff\1\44\3\uffff\5\44\6\uffff"+
            "\1\44\1\uffff\1\44\1\uffff\1\44\1\uffff\4\44\1\uffff\3\44\1"+
            "\uffff\7\44\3\uffff\3\44\5\uffff\5\44\u0ebb\uffff\2\44\52\uffff"+
            "\5\44\5\uffff\2\44\4\uffff\126\44\6\uffff\3\44\1\uffff\132\44"+
            "\1\uffff\4\44\5\uffff\50\44\4\uffff\136\44\21\uffff\30\44\70"+
            "\uffff\20\44\u0200\uffff\u19b6\44\112\uffff\u51a6\44\132\uffff"+
            "\u048d\44\u0773\uffff\u2ba4\44\u215c\uffff\u012e\44\2\uffff"+
            "\73\44\u0095\uffff\7\44\14\uffff\5\44\5\uffff\1\44\1\uffff\12"+
            "\44\1\uffff\15\44\1\uffff\5\44\1\uffff\1\44\1\uffff\2\44\1\uffff"+
            "\2\44\1\uffff\154\44\41\uffff\u016b\44\22\uffff\100\44\2\uffff"+
            "\66\44\50\uffff\14\44\164\uffff\5\44\1\uffff\u0087\44\23\uffff"+
            "\12\44\7\uffff\32\44\6\uffff\32\44\13\uffff\131\44\3\uffff\6"+
            "\44\2\uffff\6\44\2\uffff\6\44\2\uffff\3\44",
            "\1\64\1\63\5\uffff\1\67\5\uffff\12\165\5\uffff\1\70\1\uffff"+
            "\6\164\24\60\1\uffff\1\65\2\uffff\1\62\1\uffff\6\163\24\57\57"+
            "\uffff\1\44\12\uffff\1\44\4\uffff\1\44\5\uffff\27\44\1\uffff"+
            "\37\44\1\uffff\u013f\44\31\uffff\162\44\4\uffff\14\44\16\uffff"+
            "\5\44\11\uffff\1\44\u008b\uffff\1\44\13\uffff\1\44\1\uffff\3"+
            "\44\1\uffff\1\44\1\uffff\24\44\1\uffff\54\44\1\uffff\46\44\1"+
            "\uffff\5\44\4\uffff\u0082\44\10\uffff\105\44\1\uffff\46\44\2"+
            "\uffff\2\44\6\uffff\20\44\41\uffff\46\44\2\uffff\1\44\7\uffff"+
            "\47\44\110\uffff\33\44\5\uffff\3\44\56\uffff\32\44\5\uffff\13"+
            "\44\25\uffff\12\44\4\uffff\2\44\1\uffff\143\44\1\uffff\1\44"+
            "\17\uffff\2\44\7\uffff\17\44\2\uffff\1\44\20\uffff\1\44\1\uffff"+
            "\36\44\35\uffff\3\44\60\uffff\46\44\13\uffff\1\44\u0152\uffff"+
            "\66\44\3\uffff\1\44\22\uffff\1\44\7\uffff\12\44\4\uffff\12\44"+
            "\25\uffff\10\44\2\uffff\2\44\2\uffff\26\44\1\uffff\7\44\1\uffff"+
            "\1\44\3\uffff\4\44\3\uffff\1\44\36\uffff\2\44\1\uffff\3\44\4"+
            "\uffff\14\44\23\uffff\6\44\4\uffff\2\44\2\uffff\26\44\1\uffff"+
            "\7\44\1\uffff\2\44\1\uffff\2\44\1\uffff\2\44\37\uffff\4\44\1"+
            "\uffff\1\44\7\uffff\12\44\2\uffff\3\44\20\uffff\11\44\1\uffff"+
            "\3\44\1\uffff\26\44\1\uffff\7\44\1\uffff\2\44\1\uffff\5\44\3"+
            "\uffff\1\44\22\uffff\1\44\17\uffff\2\44\4\uffff\12\44\25\uffff"+
            "\10\44\2\uffff\2\44\2\uffff\26\44\1\uffff\7\44\1\uffff\2\44"+
            "\1\uffff\5\44\3\uffff\1\44\36\uffff\2\44\1\uffff\3\44\4\uffff"+
            "\12\44\1\uffff\1\44\21\uffff\1\44\1\uffff\6\44\3\uffff\3\44"+
            "\1\uffff\4\44\3\uffff\2\44\1\uffff\1\44\1\uffff\2\44\3\uffff"+
            "\2\44\3\uffff\3\44\3\uffff\10\44\1\uffff\3\44\55\uffff\11\44"+
            "\25\uffff\10\44\1\uffff\3\44\1\uffff\27\44\1\uffff\12\44\1\uffff"+
            "\5\44\46\uffff\2\44\4\uffff\12\44\25\uffff\10\44\1\uffff\3\44"+
            "\1\uffff\27\44\1\uffff\12\44\1\uffff\5\44\3\uffff\1\44\40\uffff"+
            "\1\44\1\uffff\2\44\4\uffff\12\44\25\uffff\10\44\1\uffff\3\44"+
            "\1\uffff\27\44\1\uffff\20\44\46\uffff\2\44\4\uffff\12\44\25"+
            "\uffff\22\44\3\uffff\30\44\1\uffff\11\44\1\uffff\1\44\2\uffff"+
            "\7\44\72\uffff\60\44\1\uffff\2\44\14\uffff\7\44\11\uffff\12"+
            "\44\47\uffff\2\44\1\uffff\1\44\2\uffff\2\44\1\uffff\1\44\2\uffff"+
            "\1\44\6\uffff\4\44\1\uffff\7\44\1\uffff\3\44\1\uffff\1\44\1"+
            "\uffff\1\44\2\uffff\2\44\1\uffff\4\44\1\uffff\2\44\11\uffff"+
            "\1\44\2\uffff\5\44\1\uffff\1\44\11\uffff\12\44\2\uffff\2\44"+
            "\42\uffff\1\44\37\uffff\12\44\26\uffff\10\44\1\uffff\42\44\35"+
            "\uffff\4\44\164\uffff\42\44\1\uffff\5\44\1\uffff\2\44\25\uffff"+
            "\12\44\6\uffff\6\44\112\uffff\46\44\12\uffff\51\44\7\uffff\132"+
            "\44\5\uffff\104\44\5\uffff\122\44\6\uffff\7\44\1\uffff\77\44"+
            "\1\uffff\1\44\1\uffff\4\44\2\uffff\7\44\1\uffff\1\44\1\uffff"+
            "\4\44\2\uffff\47\44\1\uffff\1\44\1\uffff\4\44\2\uffff\37\44"+
            "\1\uffff\1\44\1\uffff\4\44\2\uffff\7\44\1\uffff\1\44\1\uffff"+
            "\4\44\2\uffff\7\44\1\uffff\7\44\1\uffff\27\44\1\uffff\37\44"+
            "\1\uffff\1\44\1\uffff\4\44\2\uffff\7\44\1\uffff\47\44\1\uffff"+
            "\23\44\16\uffff\11\44\56\uffff\125\44\14\uffff\u026c\44\2\uffff"+
            "\10\44\12\uffff\32\44\5\uffff\113\44\25\uffff\15\44\1\uffff"+
            "\4\44\16\uffff\22\44\16\uffff\22\44\16\uffff\15\44\1\uffff\3"+
            "\44\17\uffff\64\44\43\uffff\1\44\4\uffff\1\44\3\uffff\12\44"+
            "\46\uffff\12\44\6\uffff\130\44\10\uffff\51\44\127\uffff\35\44"+
            "\51\uffff\50\44\2\uffff\5\44\u038b\uffff\154\44\u0094\uffff"+
            "\u009c\44\4\uffff\132\44\6\uffff\26\44\2\uffff\6\44\2\uffff"+
            "\46\44\2\uffff\6\44\2\uffff\10\44\1\uffff\1\44\1\uffff\1\44"+
            "\1\uffff\1\44\1\uffff\37\44\2\uffff\65\44\1\uffff\7\44\1\uffff"+
            "\1\44\3\uffff\3\44\1\uffff\7\44\3\uffff\4\44\2\uffff\6\44\4"+
            "\uffff\15\44\5\uffff\3\44\1\uffff\7\44\164\uffff\1\44\15\uffff"+
            "\1\44\u0082\uffff\1\44\4\uffff\1\44\2\uffff\12\44\1\uffff\1"+
            "\44\3\uffff\5\44\6\uffff\1\44\1\uffff\1\44\1\uffff\1\44\1\uffff"+
            "\4\44\1\uffff\3\44\1\uffff\7\44\3\uffff\3\44\5\uffff\5\44\u0ebb"+
            "\uffff\2\44\52\uffff\5\44\5\uffff\2\44\4\uffff\126\44\6\uffff"+
            "\3\44\1\uffff\132\44\1\uffff\4\44\5\uffff\50\44\4\uffff\136"+
            "\44\21\uffff\30\44\70\uffff\20\44\u0200\uffff\u19b6\44\112\uffff"+
            "\u51a6\44\132\uffff\u048d\44\u0773\uffff\u2ba4\44\u215c\uffff"+
            "\u012e\44\2\uffff\73\44\u0095\uffff\7\44\14\uffff\5\44\5\uffff"+
            "\1\44\1\uffff\12\44\1\uffff\15\44\1\uffff\5\44\1\uffff\1\44"+
            "\1\uffff\2\44\1\uffff\2\44\1\uffff\154\44\41\uffff\u016b\44"+
            "\22\uffff\100\44\2\uffff\66\44\50\uffff\14\44\164\uffff\5\44"+
            "\1\uffff\u0087\44\23\uffff\12\44\7\uffff\32\44\6\uffff\32\44"+
            "\13\uffff\131\44\3\uffff\6\44\2\uffff\6\44\2\uffff\6\44\2\uffff"+
            "\3\44",
            "\1\64\1\63\5\uffff\1\67\5\uffff\12\165\5\uffff\1\70\1\uffff"+
            "\6\164\24\60\1\uffff\1\65\2\uffff\1\62\1\uffff\6\163\24\57\57"+
            "\uffff\1\44\12\uffff\1\44\4\uffff\1\44\5\uffff\27\44\1\uffff"+
            "\37\44\1\uffff\u013f\44\31\uffff\162\44\4\uffff\14\44\16\uffff"+
            "\5\44\11\uffff\1\44\u008b\uffff\1\44\13\uffff\1\44\1\uffff\3"+
            "\44\1\uffff\1\44\1\uffff\24\44\1\uffff\54\44\1\uffff\46\44\1"+
            "\uffff\5\44\4\uffff\u0082\44\10\uffff\105\44\1\uffff\46\44\2"+
            "\uffff\2\44\6\uffff\20\44\41\uffff\46\44\2\uffff\1\44\7\uffff"+
            "\47\44\110\uffff\33\44\5\uffff\3\44\56\uffff\32\44\5\uffff\13"+
            "\44\25\uffff\12\44\4\uffff\2\44\1\uffff\143\44\1\uffff\1\44"+
            "\17\uffff\2\44\7\uffff\17\44\2\uffff\1\44\20\uffff\1\44\1\uffff"+
            "\36\44\35\uffff\3\44\60\uffff\46\44\13\uffff\1\44\u0152\uffff"+
            "\66\44\3\uffff\1\44\22\uffff\1\44\7\uffff\12\44\4\uffff\12\44"+
            "\25\uffff\10\44\2\uffff\2\44\2\uffff\26\44\1\uffff\7\44\1\uffff"+
            "\1\44\3\uffff\4\44\3\uffff\1\44\36\uffff\2\44\1\uffff\3\44\4"+
            "\uffff\14\44\23\uffff\6\44\4\uffff\2\44\2\uffff\26\44\1\uffff"+
            "\7\44\1\uffff\2\44\1\uffff\2\44\1\uffff\2\44\37\uffff\4\44\1"+
            "\uffff\1\44\7\uffff\12\44\2\uffff\3\44\20\uffff\11\44\1\uffff"+
            "\3\44\1\uffff\26\44\1\uffff\7\44\1\uffff\2\44\1\uffff\5\44\3"+
            "\uffff\1\44\22\uffff\1\44\17\uffff\2\44\4\uffff\12\44\25\uffff"+
            "\10\44\2\uffff\2\44\2\uffff\26\44\1\uffff\7\44\1\uffff\2\44"+
            "\1\uffff\5\44\3\uffff\1\44\36\uffff\2\44\1\uffff\3\44\4\uffff"+
            "\12\44\1\uffff\1\44\21\uffff\1\44\1\uffff\6\44\3\uffff\3\44"+
            "\1\uffff\4\44\3\uffff\2\44\1\uffff\1\44\1\uffff\2\44\3\uffff"+
            "\2\44\3\uffff\3\44\3\uffff\10\44\1\uffff\3\44\55\uffff\11\44"+
            "\25\uffff\10\44\1\uffff\3\44\1\uffff\27\44\1\uffff\12\44\1\uffff"+
            "\5\44\46\uffff\2\44\4\uffff\12\44\25\uffff\10\44\1\uffff\3\44"+
            "\1\uffff\27\44\1\uffff\12\44\1\uffff\5\44\3\uffff\1\44\40\uffff"+
            "\1\44\1\uffff\2\44\4\uffff\12\44\25\uffff\10\44\1\uffff\3\44"+
            "\1\uffff\27\44\1\uffff\20\44\46\uffff\2\44\4\uffff\12\44\25"+
            "\uffff\22\44\3\uffff\30\44\1\uffff\11\44\1\uffff\1\44\2\uffff"+
            "\7\44\72\uffff\60\44\1\uffff\2\44\14\uffff\7\44\11\uffff\12"+
            "\44\47\uffff\2\44\1\uffff\1\44\2\uffff\2\44\1\uffff\1\44\2\uffff"+
            "\1\44\6\uffff\4\44\1\uffff\7\44\1\uffff\3\44\1\uffff\1\44\1"+
            "\uffff\1\44\2\uffff\2\44\1\uffff\4\44\1\uffff\2\44\11\uffff"+
            "\1\44\2\uffff\5\44\1\uffff\1\44\11\uffff\12\44\2\uffff\2\44"+
            "\42\uffff\1\44\37\uffff\12\44\26\uffff\10\44\1\uffff\42\44\35"+
            "\uffff\4\44\164\uffff\42\44\1\uffff\5\44\1\uffff\2\44\25\uffff"+
            "\12\44\6\uffff\6\44\112\uffff\46\44\12\uffff\51\44\7\uffff\132"+
            "\44\5\uffff\104\44\5\uffff\122\44\6\uffff\7\44\1\uffff\77\44"+
            "\1\uffff\1\44\1\uffff\4\44\2\uffff\7\44\1\uffff\1\44\1\uffff"+
            "\4\44\2\uffff\47\44\1\uffff\1\44\1\uffff\4\44\2\uffff\37\44"+
            "\1\uffff\1\44\1\uffff\4\44\2\uffff\7\44\1\uffff\1\44\1\uffff"+
            "\4\44\2\uffff\7\44\1\uffff\7\44\1\uffff\27\44\1\uffff\37\44"+
            "\1\uffff\1\44\1\uffff\4\44\2\uffff\7\44\1\uffff\47\44\1\uffff"+
            "\23\44\16\uffff\11\44\56\uffff\125\44\14\uffff\u026c\44\2\uffff"+
            "\10\44\12\uffff\32\44\5\uffff\113\44\25\uffff\15\44\1\uffff"+
            "\4\44\16\uffff\22\44\16\uffff\22\44\16\uffff\15\44\1\uffff\3"+
            "\44\17\uffff\64\44\43\uffff\1\44\4\uffff\1\44\3\uffff\12\44"+
            "\46\uffff\12\44\6\uffff\130\44\10\uffff\51\44\127\uffff\35\44"+
            "\51\uffff\50\44\2\uffff\5\44\u038b\uffff\154\44\u0094\uffff"+
            "\u009c\44\4\uffff\132\44\6\uffff\26\44\2\uffff\6\44\2\uffff"+
            "\46\44\2\uffff\6\44\2\uffff\10\44\1\uffff\1\44\1\uffff\1\44"+
            "\1\uffff\1\44\1\uffff\37\44\2\uffff\65\44\1\uffff\7\44\1\uffff"+
            "\1\44\3\uffff\3\44\1\uffff\7\44\3\uffff\4\44\2\uffff\6\44\4"+
            "\uffff\15\44\5\uffff\3\44\1\uffff\7\44\164\uffff\1\44\15\uffff"+
            "\1\44\u0082\uffff\1\44\4\uffff\1\44\2\uffff\12\44\1\uffff\1"+
            "\44\3\uffff\5\44\6\uffff\1\44\1\uffff\1\44\1\uffff\1\44\1\uffff"+
            "\4\44\1\uffff\3\44\1\uffff\7\44\3\uffff\3\44\5\uffff\5\44\u0ebb"+
            "\uffff\2\44\52\uffff\5\44\5\uffff\2\44\4\uffff\126\44\6\uffff"+
            "\3\44\1\uffff\132\44\1\uffff\4\44\5\uffff\50\44\4\uffff\136"+
            "\44\21\uffff\30\44\70\uffff\20\44\u0200\uffff\u19b6\44\112\uffff"+
            "\u51a6\44\132\uffff\u048d\44\u0773\uffff\u2ba4\44\u215c\uffff"+
            "\u012e\44\2\uffff\73\44\u0095\uffff\7\44\14\uffff\5\44\5\uffff"+
            "\1\44\1\uffff\12\44\1\uffff\15\44\1\uffff\5\44\1\uffff\1\44"+
            "\1\uffff\2\44\1\uffff\2\44\1\uffff\154\44\41\uffff\u016b\44"+
            "\22\uffff\100\44\2\uffff\66\44\50\uffff\14\44\164\uffff\5\44"+
            "\1\uffff\u0087\44\23\uffff\12\44\7\uffff\32\44\6\uffff\32\44"+
            "\13\uffff\131\44\3\uffff\6\44\2\uffff\6\44\2\uffff\6\44\2\uffff"+
            "\3\44",
            "\1\64\1\63\5\uffff\1\67\5\uffff\12\165\5\uffff\1\70\1\uffff"+
            "\6\164\24\60\1\uffff\1\65\2\uffff\1\62\1\uffff\6\163\24\57\57"+
            "\uffff\1\44\12\uffff\1\44\4\uffff\1\44\5\uffff\27\44\1\uffff"+
            "\37\44\1\uffff\u013f\44\31\uffff\162\44\4\uffff\14\44\16\uffff"+
            "\5\44\11\uffff\1\44\u008b\uffff\1\44\13\uffff\1\44\1\uffff\3"+
            "\44\1\uffff\1\44\1\uffff\24\44\1\uffff\54\44\1\uffff\46\44\1"+
            "\uffff\5\44\4\uffff\u0082\44\10\uffff\105\44\1\uffff\46\44\2"+
            "\uffff\2\44\6\uffff\20\44\41\uffff\46\44\2\uffff\1\44\7\uffff"+
            "\47\44\110\uffff\33\44\5\uffff\3\44\56\uffff\32\44\5\uffff\13"+
            "\44\25\uffff\12\44\4\uffff\2\44\1\uffff\143\44\1\uffff\1\44"+
            "\17\uffff\2\44\7\uffff\17\44\2\uffff\1\44\20\uffff\1\44\1\uffff"+
            "\36\44\35\uffff\3\44\60\uffff\46\44\13\uffff\1\44\u0152\uffff"+
            "\66\44\3\uffff\1\44\22\uffff\1\44\7\uffff\12\44\4\uffff\12\44"+
            "\25\uffff\10\44\2\uffff\2\44\2\uffff\26\44\1\uffff\7\44\1\uffff"+
            "\1\44\3\uffff\4\44\3\uffff\1\44\36\uffff\2\44\1\uffff\3\44\4"+
            "\uffff\14\44\23\uffff\6\44\4\uffff\2\44\2\uffff\26\44\1\uffff"+
            "\7\44\1\uffff\2\44\1\uffff\2\44\1\uffff\2\44\37\uffff\4\44\1"+
            "\uffff\1\44\7\uffff\12\44\2\uffff\3\44\20\uffff\11\44\1\uffff"+
            "\3\44\1\uffff\26\44\1\uffff\7\44\1\uffff\2\44\1\uffff\5\44\3"+
            "\uffff\1\44\22\uffff\1\44\17\uffff\2\44\4\uffff\12\44\25\uffff"+
            "\10\44\2\uffff\2\44\2\uffff\26\44\1\uffff\7\44\1\uffff\2\44"+
            "\1\uffff\5\44\3\uffff\1\44\36\uffff\2\44\1\uffff\3\44\4\uffff"+
            "\12\44\1\uffff\1\44\21\uffff\1\44\1\uffff\6\44\3\uffff\3\44"+
            "\1\uffff\4\44\3\uffff\2\44\1\uffff\1\44\1\uffff\2\44\3\uffff"+
            "\2\44\3\uffff\3\44\3\uffff\10\44\1\uffff\3\44\55\uffff\11\44"+
            "\25\uffff\10\44\1\uffff\3\44\1\uffff\27\44\1\uffff\12\44\1\uffff"+
            "\5\44\46\uffff\2\44\4\uffff\12\44\25\uffff\10\44\1\uffff\3\44"+
            "\1\uffff\27\44\1\uffff\12\44\1\uffff\5\44\3\uffff\1\44\40\uffff"+
            "\1\44\1\uffff\2\44\4\uffff\12\44\25\uffff\10\44\1\uffff\3\44"+
            "\1\uffff\27\44\1\uffff\20\44\46\uffff\2\44\4\uffff\12\44\25"+
            "\uffff\22\44\3\uffff\30\44\1\uffff\11\44\1\uffff\1\44\2\uffff"+
            "\7\44\72\uffff\60\44\1\uffff\2\44\14\uffff\7\44\11\uffff\12"+
            "\44\47\uffff\2\44\1\uffff\1\44\2\uffff\2\44\1\uffff\1\44\2\uffff"+
            "\1\44\6\uffff\4\44\1\uffff\7\44\1\uffff\3\44\1\uffff\1\44\1"+
            "\uffff\1\44\2\uffff\2\44\1\uffff\4\44\1\uffff\2\44\11\uffff"+
            "\1\44\2\uffff\5\44\1\uffff\1\44\11\uffff\12\44\2\uffff\2\44"+
            "\42\uffff\1\44\37\uffff\12\44\26\uffff\10\44\1\uffff\42\44\35"+
            "\uffff\4\44\164\uffff\42\44\1\uffff\5\44\1\uffff\2\44\25\uffff"+
            "\12\44\6\uffff\6\44\112\uffff\46\44\12\uffff\51\44\7\uffff\132"+
            "\44\5\uffff\104\44\5\uffff\122\44\6\uffff\7\44\1\uffff\77\44"+
            "\1\uffff\1\44\1\uffff\4\44\2\uffff\7\44\1\uffff\1\44\1\uffff"+
            "\4\44\2\uffff\47\44\1\uffff\1\44\1\uffff\4\44\2\uffff\37\44"+
            "\1\uffff\1\44\1\uffff\4\44\2\uffff\7\44\1\uffff\1\44\1\uffff"+
            "\4\44\2\uffff\7\44\1\uffff\7\44\1\uffff\27\44\1\uffff\37\44"+
            "\1\uffff\1\44\1\uffff\4\44\2\uffff\7\44\1\uffff\47\44\1\uffff"+
            "\23\44\16\uffff\11\44\56\uffff\125\44\14\uffff\u026c\44\2\uffff"+
            "\10\44\12\uffff\32\44\5\uffff\113\44\25\uffff\15\44\1\uffff"+
            "\4\44\16\uffff\22\44\16\uffff\22\44\16\uffff\15\44\1\uffff\3"+
            "\44\17\uffff\64\44\43\uffff\1\44\4\uffff\1\44\3\uffff\12\44"+
            "\46\uffff\12\44\6\uffff\130\44\10\uffff\51\44\127\uffff\35\44"+
            "\51\uffff\50\44\2\uffff\5\44\u038b\uffff\154\44\u0094\uffff"+
            "\u009c\44\4\uffff\132\44\6\uffff\26\44\2\uffff\6\44\2\uffff"+
            "\46\44\2\uffff\6\44\2\uffff\10\44\1\uffff\1\44\1\uffff\1\44"+
            "\1\uffff\1\44\1\uffff\37\44\2\uffff\65\44\1\uffff\7\44\1\uffff"+
            "\1\44\3\uffff\3\44\1\uffff\7\44\3\uffff\4\44\2\uffff\6\44\4"+
            "\uffff\15\44\5\uffff\3\44\1\uffff\7\44\164\uffff\1\44\15\uffff"+
            "\1\44\u0082\uffff\1\44\4\uffff\1\44\2\uffff\12\44\1\uffff\1"+
            "\44\3\uffff\5\44\6\uffff\1\44\1\uffff\1\44\1\uffff\1\44\1\uffff"+
            "\4\44\1\uffff\3\44\1\uffff\7\44\3\uffff\3\44\5\uffff\5\44\u0ebb"+
            "\uffff\2\44\52\uffff\5\44\5\uffff\2\44\4\uffff\126\44\6\uffff"+
            "\3\44\1\uffff\132\44\1\uffff\4\44\5\uffff\50\44\4\uffff\136"+
            "\44\21\uffff\30\44\70\uffff\20\44\u0200\uffff\u19b6\44\112\uffff"+
            "\u51a6\44\132\uffff\u048d\44\u0773\uffff\u2ba4\44\u215c\uffff"+
            "\u012e\44\2\uffff\73\44\u0095\uffff\7\44\14\uffff\5\44\5\uffff"+
            "\1\44\1\uffff\12\44\1\uffff\15\44\1\uffff\5\44\1\uffff\1\44"+
            "\1\uffff\2\44\1\uffff\2\44\1\uffff\154\44\41\uffff\u016b\44"+
            "\22\uffff\100\44\2\uffff\66\44\50\uffff\14\44\164\uffff\5\44"+
            "\1\uffff\u0087\44\23\uffff\12\44\7\uffff\32\44\6\uffff\32\44"+
            "\13\uffff\131\44\3\uffff\6\44\2\uffff\6\44\2\uffff\6\44\2\uffff"+
            "\3\44",
            "\1\67\5\uffff\12\44\5\uffff\1\70\1\uffff\32\44\1\uffff\1\42"+
            "\4\uffff\32\44\57\uffff\1\44\12\uffff\1\44\4\uffff\1\44\5\uffff"+
            "\27\44\1\uffff\37\44\1\uffff\u013f\44\31\uffff\162\44\4\uffff"+
            "\14\44\16\uffff\5\44\11\uffff\1\44\u008b\uffff\1\44\13\uffff"+
            "\1\44\1\uffff\3\44\1\uffff\1\44\1\uffff\24\44\1\uffff\54\44"+
            "\1\uffff\46\44\1\uffff\5\44\4\uffff\u0082\44\10\uffff\105\44"+
            "\1\uffff\46\44\2\uffff\2\44\6\uffff\20\44\41\uffff\46\44\2\uffff"+
            "\1\44\7\uffff\47\44\110\uffff\33\44\5\uffff\3\44\56\uffff\32"+
            "\44\5\uffff\13\44\25\uffff\12\44\4\uffff\2\44\1\uffff\143\44"+
            "\1\uffff\1\44\17\uffff\2\44\7\uffff\17\44\2\uffff\1\44\20\uffff"+
            "\1\44\1\uffff\36\44\35\uffff\3\44\60\uffff\46\44\13\uffff\1"+
            "\44\u0152\uffff\66\44\3\uffff\1\44\22\uffff\1\44\7\uffff\12"+
            "\44\4\uffff\12\44\25\uffff\10\44\2\uffff\2\44\2\uffff\26\44"+
            "\1\uffff\7\44\1\uffff\1\44\3\uffff\4\44\3\uffff\1\44\36\uffff"+
            "\2\44\1\uffff\3\44\4\uffff\14\44\23\uffff\6\44\4\uffff\2\44"+
            "\2\uffff\26\44\1\uffff\7\44\1\uffff\2\44\1\uffff\2\44\1\uffff"+
            "\2\44\37\uffff\4\44\1\uffff\1\44\7\uffff\12\44\2\uffff\3\44"+
            "\20\uffff\11\44\1\uffff\3\44\1\uffff\26\44\1\uffff\7\44\1\uffff"+
            "\2\44\1\uffff\5\44\3\uffff\1\44\22\uffff\1\44\17\uffff\2\44"+
            "\4\uffff\12\44\25\uffff\10\44\2\uffff\2\44\2\uffff\26\44\1\uffff"+
            "\7\44\1\uffff\2\44\1\uffff\5\44\3\uffff\1\44\36\uffff\2\44\1"+
            "\uffff\3\44\4\uffff\12\44\1\uffff\1\44\21\uffff\1\44\1\uffff"+
            "\6\44\3\uffff\3\44\1\uffff\4\44\3\uffff\2\44\1\uffff\1\44\1"+
            "\uffff\2\44\3\uffff\2\44\3\uffff\3\44\3\uffff\10\44\1\uffff"+
            "\3\44\55\uffff\11\44\25\uffff\10\44\1\uffff\3\44\1\uffff\27"+
            "\44\1\uffff\12\44\1\uffff\5\44\46\uffff\2\44\4\uffff\12\44\25"+
            "\uffff\10\44\1\uffff\3\44\1\uffff\27\44\1\uffff\12\44\1\uffff"+
            "\5\44\3\uffff\1\44\40\uffff\1\44\1\uffff\2\44\4\uffff\12\44"+
            "\25\uffff\10\44\1\uffff\3\44\1\uffff\27\44\1\uffff\20\44\46"+
            "\uffff\2\44\4\uffff\12\44\25\uffff\22\44\3\uffff\30\44\1\uffff"+
            "\11\44\1\uffff\1\44\2\uffff\7\44\72\uffff\60\44\1\uffff\2\44"+
            "\14\uffff\7\44\11\uffff\12\44\47\uffff\2\44\1\uffff\1\44\2\uffff"+
            "\2\44\1\uffff\1\44\2\uffff\1\44\6\uffff\4\44\1\uffff\7\44\1"+
            "\uffff\3\44\1\uffff\1\44\1\uffff\1\44\2\uffff\2\44\1\uffff\4"+
            "\44\1\uffff\2\44\11\uffff\1\44\2\uffff\5\44\1\uffff\1\44\11"+
            "\uffff\12\44\2\uffff\2\44\42\uffff\1\44\37\uffff\12\44\26\uffff"+
            "\10\44\1\uffff\42\44\35\uffff\4\44\164\uffff\42\44\1\uffff\5"+
            "\44\1\uffff\2\44\25\uffff\12\44\6\uffff\6\44\112\uffff\46\44"+
            "\12\uffff\51\44\7\uffff\132\44\5\uffff\104\44\5\uffff\122\44"+
            "\6\uffff\7\44\1\uffff\77\44\1\uffff\1\44\1\uffff\4\44\2\uffff"+
            "\7\44\1\uffff\1\44\1\uffff\4\44\2\uffff\47\44\1\uffff\1\44\1"+
            "\uffff\4\44\2\uffff\37\44\1\uffff\1\44\1\uffff\4\44\2\uffff"+
            "\7\44\1\uffff\1\44\1\uffff\4\44\2\uffff\7\44\1\uffff\7\44\1"+
            "\uffff\27\44\1\uffff\37\44\1\uffff\1\44\1\uffff\4\44\2\uffff"+
            "\7\44\1\uffff\47\44\1\uffff\23\44\16\uffff\11\44\56\uffff\125"+
            "\44\14\uffff\u026c\44\2\uffff\10\44\12\uffff\32\44\5\uffff\113"+
            "\44\25\uffff\15\44\1\uffff\4\44\16\uffff\22\44\16\uffff\22\44"+
            "\16\uffff\15\44\1\uffff\3\44\17\uffff\64\44\43\uffff\1\44\4"+
            "\uffff\1\44\3\uffff\12\44\46\uffff\12\44\6\uffff\130\44\10\uffff"+
            "\51\44\127\uffff\35\44\51\uffff\50\44\2\uffff\5\44\u038b\uffff"+
            "\154\44\u0094\uffff\u009c\44\4\uffff\132\44\6\uffff\26\44\2"+
            "\uffff\6\44\2\uffff\46\44\2\uffff\6\44\2\uffff\10\44\1\uffff"+
            "\1\44\1\uffff\1\44\1\uffff\1\44\1\uffff\37\44\2\uffff\65\44"+
            "\1\uffff\7\44\1\uffff\1\44\3\uffff\3\44\1\uffff\7\44\3\uffff"+
            "\4\44\2\uffff\6\44\4\uffff\15\44\5\uffff\3\44\1\uffff\7\44\164"+
            "\uffff\1\44\15\uffff\1\44\u0082\uffff\1\44\4\uffff\1\44\2\uffff"+
            "\12\44\1\uffff\1\44\3\uffff\5\44\6\uffff\1\44\1\uffff\1\44\1"+
            "\uffff\1\44\1\uffff\4\44\1\uffff\3\44\1\uffff\7\44\3\uffff\3"+
            "\44\5\uffff\5\44\u0ebb\uffff\2\44\52\uffff\5\44\5\uffff\2\44"+
            "\4\uffff\126\44\6\uffff\3\44\1\uffff\132\44\1\uffff\4\44\5\uffff"+
            "\50\44\4\uffff\136\44\21\uffff\30\44\70\uffff\20\44\u0200\uffff"+
            "\u19b6\44\112\uffff\u51a6\44\132\uffff\u048d\44\u0773\uffff"+
            "\u2ba4\44\u215c\uffff\u012e\44\2\uffff\73\44\u0095\uffff\7\44"+
            "\14\uffff\5\44\5\uffff\1\44\1\uffff\12\44\1\uffff\15\44\1\uffff"+
            "\5\44\1\uffff\1\44\1\uffff\2\44\1\uffff\2\44\1\uffff\154\44"+
            "\41\uffff\u016b\44\22\uffff\100\44\2\uffff\66\44\50\uffff\14"+
            "\44\164\uffff\5\44\1\uffff\u0087\44\23\uffff\12\44\7\uffff\32"+
            "\44\6\uffff\32\44\13\uffff\131\44\3\uffff\6\44\2\uffff\6\44"+
            "\2\uffff\6\44\2\uffff\3\44",
            "\1\64\1\63\5\uffff\1\67\5\uffff\12\61\5\uffff\1\70\1\uffff"+
            "\32\60\1\uffff\1\65\2\uffff\1\62\1\uffff\32\57\57\uffff\1\44"+
            "\12\uffff\1\44\4\uffff\1\44\5\uffff\27\44\1\uffff\37\44\1\uffff"+
            "\u013f\44\31\uffff\162\44\4\uffff\14\44\16\uffff\5\44\11\uffff"+
            "\1\44\u008b\uffff\1\44\13\uffff\1\44\1\uffff\3\44\1\uffff\1"+
            "\44\1\uffff\24\44\1\uffff\54\44\1\uffff\46\44\1\uffff\5\44\4"+
            "\uffff\u0082\44\10\uffff\105\44\1\uffff\46\44\2\uffff\2\44\6"+
            "\uffff\20\44\41\uffff\46\44\2\uffff\1\44\7\uffff\47\44\110\uffff"+
            "\33\44\5\uffff\3\44\56\uffff\32\44\5\uffff\13\44\25\uffff\12"+
            "\44\4\uffff\2\44\1\uffff\143\44\1\uffff\1\44\17\uffff\2\44\7"+
            "\uffff\17\44\2\uffff\1\44\20\uffff\1\44\1\uffff\36\44\35\uffff"+
            "\3\44\60\uffff\46\44\13\uffff\1\44\u0152\uffff\66\44\3\uffff"+
            "\1\44\22\uffff\1\44\7\uffff\12\44\4\uffff\12\44\25\uffff\10"+
            "\44\2\uffff\2\44\2\uffff\26\44\1\uffff\7\44\1\uffff\1\44\3\uffff"+
            "\4\44\3\uffff\1\44\36\uffff\2\44\1\uffff\3\44\4\uffff\14\44"+
            "\23\uffff\6\44\4\uffff\2\44\2\uffff\26\44\1\uffff\7\44\1\uffff"+
            "\2\44\1\uffff\2\44\1\uffff\2\44\37\uffff\4\44\1\uffff\1\44\7"+
            "\uffff\12\44\2\uffff\3\44\20\uffff\11\44\1\uffff\3\44\1\uffff"+
            "\26\44\1\uffff\7\44\1\uffff\2\44\1\uffff\5\44\3\uffff\1\44\22"+
            "\uffff\1\44\17\uffff\2\44\4\uffff\12\44\25\uffff\10\44\2\uffff"+
            "\2\44\2\uffff\26\44\1\uffff\7\44\1\uffff\2\44\1\uffff\5\44\3"+
            "\uffff\1\44\36\uffff\2\44\1\uffff\3\44\4\uffff\12\44\1\uffff"+
            "\1\44\21\uffff\1\44\1\uffff\6\44\3\uffff\3\44\1\uffff\4\44\3"+
            "\uffff\2\44\1\uffff\1\44\1\uffff\2\44\3\uffff\2\44\3\uffff\3"+
            "\44\3\uffff\10\44\1\uffff\3\44\55\uffff\11\44\25\uffff\10\44"+
            "\1\uffff\3\44\1\uffff\27\44\1\uffff\12\44\1\uffff\5\44\46\uffff"+
            "\2\44\4\uffff\12\44\25\uffff\10\44\1\uffff\3\44\1\uffff\27\44"+
            "\1\uffff\12\44\1\uffff\5\44\3\uffff\1\44\40\uffff\1\44\1\uffff"+
            "\2\44\4\uffff\12\44\25\uffff\10\44\1\uffff\3\44\1\uffff\27\44"+
            "\1\uffff\20\44\46\uffff\2\44\4\uffff\12\44\25\uffff\22\44\3"+
            "\uffff\30\44\1\uffff\11\44\1\uffff\1\44\2\uffff\7\44\72\uffff"+
            "\60\44\1\uffff\2\44\14\uffff\7\44\11\uffff\12\44\47\uffff\2"+
            "\44\1\uffff\1\44\2\uffff\2\44\1\uffff\1\44\2\uffff\1\44\6\uffff"+
            "\4\44\1\uffff\7\44\1\uffff\3\44\1\uffff\1\44\1\uffff\1\44\2"+
            "\uffff\2\44\1\uffff\4\44\1\uffff\2\44\11\uffff\1\44\2\uffff"+
            "\5\44\1\uffff\1\44\11\uffff\12\44\2\uffff\2\44\42\uffff\1\44"+
            "\37\uffff\12\44\26\uffff\10\44\1\uffff\42\44\35\uffff\4\44\164"+
            "\uffff\42\44\1\uffff\5\44\1\uffff\2\44\25\uffff\12\44\6\uffff"+
            "\6\44\112\uffff\46\44\12\uffff\51\44\7\uffff\132\44\5\uffff"+
            "\104\44\5\uffff\122\44\6\uffff\7\44\1\uffff\77\44\1\uffff\1"+
            "\44\1\uffff\4\44\2\uffff\7\44\1\uffff\1\44\1\uffff\4\44\2\uffff"+
            "\47\44\1\uffff\1\44\1\uffff\4\44\2\uffff\37\44\1\uffff\1\44"+
            "\1\uffff\4\44\2\uffff\7\44\1\uffff\1\44\1\uffff\4\44\2\uffff"+
            "\7\44\1\uffff\7\44\1\uffff\27\44\1\uffff\37\44\1\uffff\1\44"+
            "\1\uffff\4\44\2\uffff\7\44\1\uffff\47\44\1\uffff\23\44\16\uffff"+
            "\11\44\56\uffff\125\44\14\uffff\u026c\44\2\uffff\10\44\12\uffff"+
            "\32\44\5\uffff\113\44\25\uffff\15\44\1\uffff\4\44\16\uffff\22"+
            "\44\16\uffff\22\44\16\uffff\15\44\1\uffff\3\44\17\uffff\64\44"+
            "\43\uffff\1\44\4\uffff\1\44\3\uffff\12\44\46\uffff\12\44\6\uffff"+
            "\130\44\10\uffff\51\44\127\uffff\35\44\51\uffff\50\44\2\uffff"+
            "\5\44\u038b\uffff\154\44\u0094\uffff\u009c\44\4\uffff\132\44"+
            "\6\uffff\26\44\2\uffff\6\44\2\uffff\46\44\2\uffff\6\44\2\uffff"+
            "\10\44\1\uffff\1\44\1\uffff\1\44\1\uffff\1\44\1\uffff\37\44"+
            "\2\uffff\65\44\1\uffff\7\44\1\uffff\1\44\3\uffff\3\44\1\uffff"+
            "\7\44\3\uffff\4\44\2\uffff\6\44\4\uffff\15\44\5\uffff\3\44\1"+
            "\uffff\7\44\164\uffff\1\44\15\uffff\1\44\u0082\uffff\1\44\4"+
            "\uffff\1\44\2\uffff\12\44\1\uffff\1\44\3\uffff\5\44\6\uffff"+
            "\1\44\1\uffff\1\44\1\uffff\1\44\1\uffff\4\44\1\uffff\3\44\1"+
            "\uffff\7\44\3\uffff\3\44\5\uffff\5\44\u0ebb\uffff\2\44\52\uffff"+
            "\5\44\5\uffff\2\44\4\uffff\126\44\6\uffff\3\44\1\uffff\132\44"+
            "\1\uffff\4\44\5\uffff\50\44\4\uffff\136\44\21\uffff\30\44\70"+
            "\uffff\20\44\u0200\uffff\u19b6\44\112\uffff\u51a6\44\132\uffff"+
            "\u048d\44\u0773\uffff\u2ba4\44\u215c\uffff\u012e\44\2\uffff"+
            "\73\44\u0095\uffff\7\44\14\uffff\5\44\5\uffff\1\44\1\uffff\12"+
            "\44\1\uffff\15\44\1\uffff\5\44\1\uffff\1\44\1\uffff\2\44\1\uffff"+
            "\2\44\1\uffff\154\44\41\uffff\u016b\44\22\uffff\100\44\2\uffff"+
            "\66\44\50\uffff\14\44\164\uffff\5\44\1\uffff\u0087\44\23\uffff"+
            "\12\44\7\uffff\32\44\6\uffff\32\44\13\uffff\131\44\3\uffff\6"+
            "\44\2\uffff\6\44\2\uffff\6\44\2\uffff\3\44",
            "\1\64\1\63\5\uffff\1\67\5\uffff\12\61\5\uffff\1\70\1\uffff"+
            "\32\60\1\uffff\1\65\2\uffff\1\62\1\uffff\32\57\57\uffff\1\44"+
            "\12\uffff\1\44\4\uffff\1\44\5\uffff\27\44\1\uffff\37\44\1\uffff"+
            "\u013f\44\31\uffff\162\44\4\uffff\14\44\16\uffff\5\44\11\uffff"+
            "\1\44\u008b\uffff\1\44\13\uffff\1\44\1\uffff\3\44\1\uffff\1"+
            "\44\1\uffff\24\44\1\uffff\54\44\1\uffff\46\44\1\uffff\5\44\4"+
            "\uffff\u0082\44\10\uffff\105\44\1\uffff\46\44\2\uffff\2\44\6"+
            "\uffff\20\44\41\uffff\46\44\2\uffff\1\44\7\uffff\47\44\110\uffff"+
            "\33\44\5\uffff\3\44\56\uffff\32\44\5\uffff\13\44\25\uffff\12"+
            "\44\4\uffff\2\44\1\uffff\143\44\1\uffff\1\44\17\uffff\2\44\7"+
            "\uffff\17\44\2\uffff\1\44\20\uffff\1\44\1\uffff\36\44\35\uffff"+
            "\3\44\60\uffff\46\44\13\uffff\1\44\u0152\uffff\66\44\3\uffff"+
            "\1\44\22\uffff\1\44\7\uffff\12\44\4\uffff\12\44\25\uffff\10"+
            "\44\2\uffff\2\44\2\uffff\26\44\1\uffff\7\44\1\uffff\1\44\3\uffff"+
            "\4\44\3\uffff\1\44\36\uffff\2\44\1\uffff\3\44\4\uffff\14\44"+
            "\23\uffff\6\44\4\uffff\2\44\2\uffff\26\44\1\uffff\7\44\1\uffff"+
            "\2\44\1\uffff\2\44\1\uffff\2\44\37\uffff\4\44\1\uffff\1\44\7"+
            "\uffff\12\44\2\uffff\3\44\20\uffff\11\44\1\uffff\3\44\1\uffff"+
            "\26\44\1\uffff\7\44\1\uffff\2\44\1\uffff\5\44\3\uffff\1\44\22"+
            "\uffff\1\44\17\uffff\2\44\4\uffff\12\44\25\uffff\10\44\2\uffff"+
            "\2\44\2\uffff\26\44\1\uffff\7\44\1\uffff\2\44\1\uffff\5\44\3"+
            "\uffff\1\44\36\uffff\2\44\1\uffff\3\44\4\uffff\12\44\1\uffff"+
            "\1\44\21\uffff\1\44\1\uffff\6\44\3\uffff\3\44\1\uffff\4\44\3"+
            "\uffff\2\44\1\uffff\1\44\1\uffff\2\44\3\uffff\2\44\3\uffff\3"+
            "\44\3\uffff\10\44\1\uffff\3\44\55\uffff\11\44\25\uffff\10\44"+
            "\1\uffff\3\44\1\uffff\27\44\1\uffff\12\44\1\uffff\5\44\46\uffff"+
            "\2\44\4\uffff\12\44\25\uffff\10\44\1\uffff\3\44\1\uffff\27\44"+
            "\1\uffff\12\44\1\uffff\5\44\3\uffff\1\44\40\uffff\1\44\1\uffff"+
            "\2\44\4\uffff\12\44\25\uffff\10\44\1\uffff\3\44\1\uffff\27\44"+
            "\1\uffff\20\44\46\uffff\2\44\4\uffff\12\44\25\uffff\22\44\3"+
            "\uffff\30\44\1\uffff\11\44\1\uffff\1\44\2\uffff\7\44\72\uffff"+
            "\60\44\1\uffff\2\44\14\uffff\7\44\11\uffff\12\44\47\uffff\2"+
            "\44\1\uffff\1\44\2\uffff\2\44\1\uffff\1\44\2\uffff\1\44\6\uffff"+
            "\4\44\1\uffff\7\44\1\uffff\3\44\1\uffff\1\44\1\uffff\1\44\2"+
            "\uffff\2\44\1\uffff\4\44\1\uffff\2\44\11\uffff\1\44\2\uffff"+
            "\5\44\1\uffff\1\44\11\uffff\12\44\2\uffff\2\44\42\uffff\1\44"+
            "\37\uffff\12\44\26\uffff\10\44\1\uffff\42\44\35\uffff\4\44\164"+
            "\uffff\42\44\1\uffff\5\44\1\uffff\2\44\25\uffff\12\44\6\uffff"+
            "\6\44\112\uffff\46\44\12\uffff\51\44\7\uffff\132\44\5\uffff"+
            "\104\44\5\uffff\122\44\6\uffff\7\44\1\uffff\77\44\1\uffff\1"+
            "\44\1\uffff\4\44\2\uffff\7\44\1\uffff\1\44\1\uffff\4\44\2\uffff"+
            "\47\44\1\uffff\1\44\1\uffff\4\44\2\uffff\37\44\1\uffff\1\44"+
            "\1\uffff\4\44\2\uffff\7\44\1\uffff\1\44\1\uffff\4\44\2\uffff"+
            "\7\44\1\uffff\7\44\1\uffff\27\44\1\uffff\37\44\1\uffff\1\44"+
            "\1\uffff\4\44\2\uffff\7\44\1\uffff\47\44\1\uffff\23\44\16\uffff"+
            "\11\44\56\uffff\125\44\14\uffff\u026c\44\2\uffff\10\44\12\uffff"+
            "\32\44\5\uffff\113\44\25\uffff\15\44\1\uffff\4\44\16\uffff\22"+
            "\44\16\uffff\22\44\16\uffff\15\44\1\uffff\3\44\17\uffff\64\44"+
            "\43\uffff\1\44\4\uffff\1\44\3\uffff\12\44\46\uffff\12\44\6\uffff"+
            "\130\44\10\uffff\51\44\127\uffff\35\44\51\uffff\50\44\2\uffff"+
            "\5\44\u038b\uffff\154\44\u0094\uffff\u009c\44\4\uffff\132\44"+
            "\6\uffff\26\44\2\uffff\6\44\2\uffff\46\44\2\uffff\6\44\2\uffff"+
            "\10\44\1\uffff\1\44\1\uffff\1\44\1\uffff\1\44\1\uffff\37\44"+
            "\2\uffff\65\44\1\uffff\7\44\1\uffff\1\44\3\uffff\3\44\1\uffff"+
            "\7\44\3\uffff\4\44\2\uffff\6\44\4\uffff\15\44\5\uffff\3\44\1"+
            "\uffff\7\44\164\uffff\1\44\15\uffff\1\44\u0082\uffff\1\44\4"+
            "\uffff\1\44\2\uffff\12\44\1\uffff\1\44\3\uffff\5\44\6\uffff"+
            "\1\44\1\uffff\1\44\1\uffff\1\44\1\uffff\4\44\1\uffff\3\44\1"+
            "\uffff\7\44\3\uffff\3\44\5\uffff\5\44\u0ebb\uffff\2\44\52\uffff"+
            "\5\44\5\uffff\2\44\4\uffff\126\44\6\uffff\3\44\1\uffff\132\44"+
            "\1\uffff\4\44\5\uffff\50\44\4\uffff\136\44\21\uffff\30\44\70"+
            "\uffff\20\44\u0200\uffff\u19b6\44\112\uffff\u51a6\44\132\uffff"+
            "\u048d\44\u0773\uffff\u2ba4\44\u215c\uffff\u012e\44\2\uffff"+
            "\73\44\u0095\uffff\7\44\14\uffff\5\44\5\uffff\1\44\1\uffff\12"+
            "\44\1\uffff\15\44\1\uffff\5\44\1\uffff\1\44\1\uffff\2\44\1\uffff"+
            "\2\44\1\uffff\154\44\41\uffff\u016b\44\22\uffff\100\44\2\uffff"+
            "\66\44\50\uffff\14\44\164\uffff\5\44\1\uffff\u0087\44\23\uffff"+
            "\12\44\7\uffff\32\44\6\uffff\32\44\13\uffff\131\44\3\uffff\6"+
            "\44\2\uffff\6\44\2\uffff\6\44\2\uffff\3\44",
            "\1\64\1\63\5\uffff\1\67\5\uffff\12\61\5\uffff\1\70\1\uffff"+
            "\32\60\1\uffff\1\65\2\uffff\1\62\1\uffff\32\57\57\uffff\1\44"+
            "\12\uffff\1\44\4\uffff\1\44\5\uffff\27\44\1\uffff\37\44\1\uffff"+
            "\u013f\44\31\uffff\162\44\4\uffff\14\44\16\uffff\5\44\11\uffff"+
            "\1\44\u008b\uffff\1\44\13\uffff\1\44\1\uffff\3\44\1\uffff\1"+
            "\44\1\uffff\24\44\1\uffff\54\44\1\uffff\46\44\1\uffff\5\44\4"+
            "\uffff\u0082\44\10\uffff\105\44\1\uffff\46\44\2\uffff\2\44\6"+
            "\uffff\20\44\41\uffff\46\44\2\uffff\1\44\7\uffff\47\44\110\uffff"+
            "\33\44\5\uffff\3\44\56\uffff\32\44\5\uffff\13\44\25\uffff\12"+
            "\44\4\uffff\2\44\1\uffff\143\44\1\uffff\1\44\17\uffff\2\44\7"+
            "\uffff\17\44\2\uffff\1\44\20\uffff\1\44\1\uffff\36\44\35\uffff"+
            "\3\44\60\uffff\46\44\13\uffff\1\44\u0152\uffff\66\44\3\uffff"+
            "\1\44\22\uffff\1\44\7\uffff\12\44\4\uffff\12\44\25\uffff\10"+
            "\44\2\uffff\2\44\2\uffff\26\44\1\uffff\7\44\1\uffff\1\44\3\uffff"+
            "\4\44\3\uffff\1\44\36\uffff\2\44\1\uffff\3\44\4\uffff\14\44"+
            "\23\uffff\6\44\4\uffff\2\44\2\uffff\26\44\1\uffff\7\44\1\uffff"+
            "\2\44\1\uffff\2\44\1\uffff\2\44\37\uffff\4\44\1\uffff\1\44\7"+
            "\uffff\12\44\2\uffff\3\44\20\uffff\11\44\1\uffff\3\44\1\uffff"+
            "\26\44\1\uffff\7\44\1\uffff\2\44\1\uffff\5\44\3\uffff\1\44\22"+
            "\uffff\1\44\17\uffff\2\44\4\uffff\12\44\25\uffff\10\44\2\uffff"+
            "\2\44\2\uffff\26\44\1\uffff\7\44\1\uffff\2\44\1\uffff\5\44\3"+
            "\uffff\1\44\36\uffff\2\44\1\uffff\3\44\4\uffff\12\44\1\uffff"+
            "\1\44\21\uffff\1\44\1\uffff\6\44\3\uffff\3\44\1\uffff\4\44\3"+
            "\uffff\2\44\1\uffff\1\44\1\uffff\2\44\3\uffff\2\44\3\uffff\3"+
            "\44\3\uffff\10\44\1\uffff\3\44\55\uffff\11\44\25\uffff\10\44"+
            "\1\uffff\3\44\1\uffff\27\44\1\uffff\12\44\1\uffff\5\44\46\uffff"+
            "\2\44\4\uffff\12\44\25\uffff\10\44\1\uffff\3\44\1\uffff\27\44"+
            "\1\uffff\12\44\1\uffff\5\44\3\uffff\1\44\40\uffff\1\44\1\uffff"+
            "\2\44\4\uffff\12\44\25\uffff\10\44\1\uffff\3\44\1\uffff\27\44"+
            "\1\uffff\20\44\46\uffff\2\44\4\uffff\12\44\25\uffff\22\44\3"+
            "\uffff\30\44\1\uffff\11\44\1\uffff\1\44\2\uffff\7\44\72\uffff"+
            "\60\44\1\uffff\2\44\14\uffff\7\44\11\uffff\12\44\47\uffff\2"+
            "\44\1\uffff\1\44\2\uffff\2\44\1\uffff\1\44\2\uffff\1\44\6\uffff"+
            "\4\44\1\uffff\7\44\1\uffff\3\44\1\uffff\1\44\1\uffff\1\44\2"+
            "\uffff\2\44\1\uffff\4\44\1\uffff\2\44\11\uffff\1\44\2\uffff"+
            "\5\44\1\uffff\1\44\11\uffff\12\44\2\uffff\2\44\42\uffff\1\44"+
            "\37\uffff\12\44\26\uffff\10\44\1\uffff\42\44\35\uffff\4\44\164"+
            "\uffff\42\44\1\uffff\5\44\1\uffff\2\44\25\uffff\12\44\6\uffff"+
            "\6\44\112\uffff\46\44\12\uffff\51\44\7\uffff\132\44\5\uffff"+
            "\104\44\5\uffff\122\44\6\uffff\7\44\1\uffff\77\44\1\uffff\1"+
            "\44\1\uffff\4\44\2\uffff\7\44\1\uffff\1\44\1\uffff\4\44\2\uffff"+
            "\47\44\1\uffff\1\44\1\uffff\4\44\2\uffff\37\44\1\uffff\1\44"+
            "\1\uffff\4\44\2\uffff\7\44\1\uffff\1\44\1\uffff\4\44\2\uffff"+
            "\7\44\1\uffff\7\44\1\uffff\27\44\1\uffff\37\44\1\uffff\1\44"+
            "\1\uffff\4\44\2\uffff\7\44\1\uffff\47\44\1\uffff\23\44\16\uffff"+
            "\11\44\56\uffff\125\44\14\uffff\u026c\44\2\uffff\10\44\12\uffff"+
            "\32\44\5\uffff\113\44\25\uffff\15\44\1\uffff\4\44\16\uffff\22"+
            "\44\16\uffff\22\44\16\uffff\15\44\1\uffff\3\44\17\uffff\64\44"+
            "\43\uffff\1\44\4\uffff\1\44\3\uffff\12\44\46\uffff\12\44\6\uffff"+
            "\130\44\10\uffff\51\44\127\uffff\35\44\51\uffff\50\44\2\uffff"+
            "\5\44\u038b\uffff\154\44\u0094\uffff\u009c\44\4\uffff\132\44"+
            "\6\uffff\26\44\2\uffff\6\44\2\uffff\46\44\2\uffff\6\44\2\uffff"+
            "\10\44\1\uffff\1\44\1\uffff\1\44\1\uffff\1\44\1\uffff\37\44"+
            "\2\uffff\65\44\1\uffff\7\44\1\uffff\1\44\3\uffff\3\44\1\uffff"+
            "\7\44\3\uffff\4\44\2\uffff\6\44\4\uffff\15\44\5\uffff\3\44\1"+
            "\uffff\7\44\164\uffff\1\44\15\uffff\1\44\u0082\uffff\1\44\4"+
            "\uffff\1\44\2\uffff\12\44\1\uffff\1\44\3\uffff\5\44\6\uffff"+
            "\1\44\1\uffff\1\44\1\uffff\1\44\1\uffff\4\44\1\uffff\3\44\1"+
            "\uffff\7\44\3\uffff\3\44\5\uffff\5\44\u0ebb\uffff\2\44\52\uffff"+
            "\5\44\5\uffff\2\44\4\uffff\126\44\6\uffff\3\44\1\uffff\132\44"+
            "\1\uffff\4\44\5\uffff\50\44\4\uffff\136\44\21\uffff\30\44\70"+
            "\uffff\20\44\u0200\uffff\u19b6\44\112\uffff\u51a6\44\132\uffff"+
            "\u048d\44\u0773\uffff\u2ba4\44\u215c\uffff\u012e\44\2\uffff"+
            "\73\44\u0095\uffff\7\44\14\uffff\5\44\5\uffff\1\44\1\uffff\12"+
            "\44\1\uffff\15\44\1\uffff\5\44\1\uffff\1\44\1\uffff\2\44\1\uffff"+
            "\2\44\1\uffff\154\44\41\uffff\u016b\44\22\uffff\100\44\2\uffff"+
            "\66\44\50\uffff\14\44\164\uffff\5\44\1\uffff\u0087\44\23\uffff"+
            "\12\44\7\uffff\32\44\6\uffff\32\44\13\uffff\131\44\3\uffff\6"+
            "\44\2\uffff\6\44\2\uffff\6\44\2\uffff\3\44"
    };

    static final short[] DFA38_eot = DFA.unpackEncodedString(DFA38_eotS);
    static final short[] DFA38_eof = DFA.unpackEncodedString(DFA38_eofS);
    static final char[] DFA38_min = DFA.unpackEncodedStringToUnsignedChars(DFA38_minS);
    static final char[] DFA38_max = DFA.unpackEncodedStringToUnsignedChars(DFA38_maxS);
    static final short[] DFA38_accept = DFA.unpackEncodedString(DFA38_acceptS);
    static final short[] DFA38_special = DFA.unpackEncodedString(DFA38_specialS);
    static final short[][] DFA38_transition;

    static {
        int numStates = DFA38_transitionS.length;
        DFA38_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA38_transition[i] = DFA.unpackEncodedString(DFA38_transitionS[i]);
        }
    }

    class DFA38 extends DFA {

        public DFA38(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 38;
            this.eot = DFA38_eot;
            this.eof = DFA38_eof;
            this.min = DFA38_min;
            this.max = DFA38_max;
            this.accept = DFA38_accept;
            this.special = DFA38_special;
            this.transition = DFA38_transition;
        }
        public String getDescription() {
            return "1:1: Tokens : ( FTSPHRASE | URI | OR | AND | NOT | TILDA | LPAREN | RPAREN | PLUS | MINUS | COLON | STAR | DOTDOT | DOT | AMP | EXCLAMATION | BAR | EQUALS | QUESTION_MARK | LCURL | RCURL | LSQUARE | RSQUARE | TO | COMMA | CARAT | DOLLAR | GT | LT | AT | PERCENT | ID | DECIMAL_INTEGER_LITERAL | FTSWORD | FTSPRE | FTSWILD | FLOATING_POINT_LITERAL | WS );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            IntStream input = _input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA38_53 = input.LA(1);

                        s = -1;
                        if ( (LA38_53=='u') ) {s = 85;}

                        else if ( ((LA38_53>='\u0000' && LA38_53<='t')||(LA38_53>='v' && LA38_53<='\uFFFF')) ) {s = 86;}

                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA38_34 = input.LA(1);

                        s = -1;
                        if ( (LA38_34=='u') ) {s = 81;}

                        else if ( ((LA38_34>='\u0000' && LA38_34<='t')||(LA38_34>='v' && LA38_34<='\uFFFF')) ) {s = 82;}

                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 38, _s, input);
            error(nvae);
            throw nvae;
        }
    }
 

}