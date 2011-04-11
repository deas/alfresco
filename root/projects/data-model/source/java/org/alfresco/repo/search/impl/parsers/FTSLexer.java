// $ANTLR 3.2 Sep 23, 2009 12:02:23 C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g 2011-01-17 14:15:32

package org.alfresco.repo.search.impl.parsers;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
public class FTSLexer extends Lexer {
    public static final int PREFIX=38;
    public static final int EXPONENT=91;
    public static final int LT=66;
    public static final int STAR=63;
    public static final int LSQUARE=65;
    public static final int FG_TERM=27;
    public static final int FUZZY=41;
    public static final int FIELD_DISJUNCTION=19;
    public static final int EQUALS=54;
    public static final int F_URI_ALPHA=78;
    public static final int FG_EXACT_PHRASE=30;
    public static final int FG_EXACT_TERM=28;
    public static final int NOT=60;
    public static final int FIELD_EXCLUDE=26;
    public static final int EOF=-1;
    public static final int NAME_SPACE=39;
    public static final int RPAREN=47;
    public static final int EXCLAMATION=76;
    public static final int FLOATING_POINT_LITERAL=62;
    public static final int QUESTION_MARK=72;
    public static final int ZERO_DIGIT=92;
    public static final int FIELD_OPTIONAL=25;
    public static final int SYNONYM=12;
    public static final int E=94;
    public static final int CONJUNCTION=6;
    public static final int FTSWORD=57;
    public static final int URI=70;
    public static final int DISJUNCTION=5;
    public static final int FTS=4;
    public static final int FG_SYNONYM=31;
    public static final int WS=96;
    public static final int FTSPHRASE=55;
    public static final int FIELD_CONJUNCTION=20;
    public static final int INCLUSIVE=35;
    public static final int OR=73;
    public static final int GT=68;
    public static final int F_HEX=81;
    public static final int DECIMAL_INTEGER_LITERAL=51;
    public static final int FTSPRE=58;
    public static final int FG_PHRASE=29;
    public static final int FIELD_NEGATION=21;
    public static final int TERM=8;
    public static final int DOLLAR=85;
    public static final int START_RANGE_I=88;
    public static final int AMP=75;
    public static final int FG_PROXIMITY=32;
    public static final int EXACT_TERM=9;
    public static final int START_RANGE_F=89;
    public static final int DOTDOT=64;
    public static final int MANDATORY=16;
    public static final int EXCLUSIVE=36;
    public static final int ID=56;
    public static final int AND=74;
    public static final int LPAREN=46;
    public static final int BOOST=40;
    public static final int AT=69;
    public static final int TILDA=50;
    public static final int DECIMAL_NUMERAL=86;
    public static final int COMMA=49;
    public static final int F_URI_DIGIT=79;
    public static final int SIGNED_INTEGER=95;
    public static final int FIELD_DEFAULT=23;
    public static final int CARAT=52;
    public static final int PLUS=43;
    public static final int DIGIT=90;
    public static final int DOT=71;
    public static final int F_ESC=77;
    public static final int EXCLUDE=18;
    public static final int PERCENT=48;
    public static final int NON_ZERO_DIGIT=93;
    public static final int QUALIFIER=37;
    public static final int TO=61;
    public static final int FIELD_GROUP=22;
    public static final int DEFAULT=15;
    public static final int INWORD=87;
    public static final int EXACT_PHRASE=11;
    public static final int RANGE=13;
    public static final int MINUS=45;
    public static final int RSQUARE=67;
    public static final int FIELD_REF=34;
    public static final int PROXIMITY=14;
    public static final int PHRASE=10;
    public static final int OPTIONAL=17;
    public static final int COLON=53;
    public static final int LCURL=83;
    public static final int F_URI_OTHER=80;
    public static final int NEGATION=7;
    public static final int F_URI_ESC=82;
    public static final int TEMPLATE=42;
    public static final int RCURL=84;
    public static final int FIELD_MANDATORY=24;
    public static final int FG_RANGE=33;
    public static final int BAR=44;
    public static final int FTSWILD=59;

    List tokens = new ArrayList();
    public void emit(Token token) {
            state.token = token;
            tokens.add(token);
    }
    public Token nextToken() {
            nextTokenImpl();
            if ( tokens.size()==0 ) {
                return Token.EOF_TOKEN;
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
                    return Token.EOF_TOKEN;
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
    public String getGrammarFileName() { return "C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g"; }

    // $ANTLR start "FTSPHRASE"
    public final void mFTSPHRASE() throws RecognitionException {
        try {
            int _type = FTSPHRASE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:855:9: ( '\"' ( F_ESC | ~ ( '\\\\' | '\"' ) )* '\"' | '\\'' ( F_ESC | ~ ( '\\\\' | '\\'' ) )* '\\'' )
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
                    // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:856:9: '\"' ( F_ESC | ~ ( '\\\\' | '\"' ) )* '\"'
                    {
                    match('\"'); if (state.failed) return ;
                    // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:857:9: ( F_ESC | ~ ( '\\\\' | '\"' ) )*
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
                    	    // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:858:17: F_ESC
                    	    {
                    	    mF_ESC(); if (state.failed) return ;

                    	    }
                    	    break;
                    	case 2 :
                    	    // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:860:17: ~ ( '\\\\' | '\"' )
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
                    // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:866:11: '\\'' ( F_ESC | ~ ( '\\\\' | '\\'' ) )* '\\''
                    {
                    match('\''); if (state.failed) return ;
                    // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:867:9: ( F_ESC | ~ ( '\\\\' | '\\'' ) )*
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
                    	    // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:868:17: F_ESC
                    	    {
                    	    mF_ESC(); if (state.failed) return ;

                    	    }
                    	    break;
                    	case 2 :
                    	    // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:870:17: ~ ( '\\\\' | '\\'' )
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
            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:884:9: ( '{' ( ( F_URI_ALPHA | F_URI_DIGIT | F_URI_OTHER )=> ( F_URI_ALPHA | F_URI_DIGIT | F_URI_OTHER )+ COLON )? ( ( ( '//' )=> '//' ) ( ( F_URI_ALPHA | F_URI_DIGIT | F_URI_OTHER | COLON )=> ( F_URI_ALPHA | F_URI_DIGIT | F_URI_OTHER | COLON ) )* )? ( F_URI_ALPHA | F_URI_DIGIT | F_URI_OTHER | COLON | '/' )* ( '?' ( F_URI_ALPHA | F_URI_DIGIT | F_URI_OTHER | COLON | '/' | '?' )* )? ( '#' ( F_URI_ALPHA | F_URI_DIGIT | F_URI_OTHER | COLON | '/' | '?' | '#' )* )? '}' )
            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:885:9: '{' ( ( F_URI_ALPHA | F_URI_DIGIT | F_URI_OTHER )=> ( F_URI_ALPHA | F_URI_DIGIT | F_URI_OTHER )+ COLON )? ( ( ( '//' )=> '//' ) ( ( F_URI_ALPHA | F_URI_DIGIT | F_URI_OTHER | COLON )=> ( F_URI_ALPHA | F_URI_DIGIT | F_URI_OTHER | COLON ) )* )? ( F_URI_ALPHA | F_URI_DIGIT | F_URI_OTHER | COLON | '/' )* ( '?' ( F_URI_ALPHA | F_URI_DIGIT | F_URI_OTHER | COLON | '/' | '?' )* )? ( '#' ( F_URI_ALPHA | F_URI_DIGIT | F_URI_OTHER | COLON | '/' | '?' | '#' )* )? '}'
            {
            match('{'); if (state.failed) return ;
            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:886:9: ( ( F_URI_ALPHA | F_URI_DIGIT | F_URI_OTHER )=> ( F_URI_ALPHA | F_URI_DIGIT | F_URI_OTHER )+ COLON )?
            int alt5=2;
            alt5 = dfa5.predict(input);
            switch (alt5) {
                case 1 :
                    // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:887:17: ( F_URI_ALPHA | F_URI_DIGIT | F_URI_OTHER )=> ( F_URI_ALPHA | F_URI_DIGIT | F_URI_OTHER )+ COLON
                    {
                    // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:893:17: ( F_URI_ALPHA | F_URI_DIGIT | F_URI_OTHER )+
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
                    	    // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:
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

            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:900:9: ( ( ( '//' )=> '//' ) ( ( F_URI_ALPHA | F_URI_DIGIT | F_URI_OTHER | COLON )=> ( F_URI_ALPHA | F_URI_DIGIT | F_URI_OTHER | COLON ) )* )?
            int alt7=2;
            int LA7_0 = input.LA(1);

            if ( (LA7_0=='/') ) {
                int LA7_1 = input.LA(2);

                if ( (LA7_1=='/') ) {
                    int LA7_3 = input.LA(3);

                    if ( (synpred2_FTS()) ) {
                        alt7=1;
                    }
                }
            }
            switch (alt7) {
                case 1 :
                    // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:901:17: ( ( '//' )=> '//' ) ( ( F_URI_ALPHA | F_URI_DIGIT | F_URI_OTHER | COLON )=> ( F_URI_ALPHA | F_URI_DIGIT | F_URI_OTHER | COLON ) )*
                    {
                    // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:901:17: ( ( '//' )=> '//' )
                    // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:901:19: ( '//' )=> '//'
                    {
                    match("//"); if (state.failed) return ;


                    }

                    // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:902:17: ( ( F_URI_ALPHA | F_URI_DIGIT | F_URI_OTHER | COLON )=> ( F_URI_ALPHA | F_URI_DIGIT | F_URI_OTHER | COLON ) )*
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
                    	    // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:903:25: ( F_URI_ALPHA | F_URI_DIGIT | F_URI_OTHER | COLON )=> ( F_URI_ALPHA | F_URI_DIGIT | F_URI_OTHER | COLON )
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

            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:918:9: ( F_URI_ALPHA | F_URI_DIGIT | F_URI_OTHER | COLON | '/' )*
            loop8:
            do {
                int alt8=2;
                int LA8_0 = input.LA(1);

                if ( (LA8_0=='!'||LA8_0=='$'||(LA8_0>='&' && LA8_0<=';')||LA8_0=='='||(LA8_0>='@' && LA8_0<='[')||LA8_0==']'||LA8_0=='_'||(LA8_0>='a' && LA8_0<='z')||LA8_0=='~') ) {
                    alt8=1;
                }


                switch (alt8) {
            	case 1 :
            	    // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:
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

            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:925:9: ( '?' ( F_URI_ALPHA | F_URI_DIGIT | F_URI_OTHER | COLON | '/' | '?' )* )?
            int alt10=2;
            int LA10_0 = input.LA(1);

            if ( (LA10_0=='?') ) {
                alt10=1;
            }
            switch (alt10) {
                case 1 :
                    // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:926:17: '?' ( F_URI_ALPHA | F_URI_DIGIT | F_URI_OTHER | COLON | '/' | '?' )*
                    {
                    match('?'); if (state.failed) return ;
                    // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:927:17: ( F_URI_ALPHA | F_URI_DIGIT | F_URI_OTHER | COLON | '/' | '?' )*
                    loop9:
                    do {
                        int alt9=2;
                        int LA9_0 = input.LA(1);

                        if ( (LA9_0=='!'||LA9_0=='$'||(LA9_0>='&' && LA9_0<=';')||LA9_0=='='||(LA9_0>='?' && LA9_0<='[')||LA9_0==']'||LA9_0=='_'||(LA9_0>='a' && LA9_0<='z')||LA9_0=='~') ) {
                            alt9=1;
                        }


                        switch (alt9) {
                    	case 1 :
                    	    // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:
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

            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:936:9: ( '#' ( F_URI_ALPHA | F_URI_DIGIT | F_URI_OTHER | COLON | '/' | '?' | '#' )* )?
            int alt12=2;
            int LA12_0 = input.LA(1);

            if ( (LA12_0=='#') ) {
                alt12=1;
            }
            switch (alt12) {
                case 1 :
                    // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:937:17: '#' ( F_URI_ALPHA | F_URI_DIGIT | F_URI_OTHER | COLON | '/' | '?' | '#' )*
                    {
                    match('#'); if (state.failed) return ;
                    // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:938:17: ( F_URI_ALPHA | F_URI_DIGIT | F_URI_OTHER | COLON | '/' | '?' | '#' )*
                    loop11:
                    do {
                        int alt11=2;
                        int LA11_0 = input.LA(1);

                        if ( (LA11_0=='!'||(LA11_0>='#' && LA11_0<='$')||(LA11_0>='&' && LA11_0<=';')||LA11_0=='='||(LA11_0>='?' && LA11_0<='[')||LA11_0==']'||LA11_0=='_'||(LA11_0>='a' && LA11_0<='z')||LA11_0=='~') ) {
                            alt11=1;
                        }


                        switch (alt11) {
                    	case 1 :
                    	    // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:
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
            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:953:9: ( 'A' .. 'Z' | 'a' .. 'z' )
            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:
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
            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:960:9: ( '0' .. '9' )
            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:961:9: '0' .. '9'
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
            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:966:9: ( '%' F_HEX F_HEX )
            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:967:9: '%' F_HEX F_HEX
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
            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:972:9: ( '-' | '.' | '_' | '~' | '[' | ']' | '@' | '!' | '$' | '&' | '\\'' | '(' | ')' | '*' | '+' | ',' | ';' | '=' )
            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:
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
            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:998:9: ( ( 'O' | 'o' ) ( 'R' | 'r' ) )
            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:999:9: ( 'O' | 'o' ) ( 'R' | 'r' )
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
            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1010:9: ( ( 'A' | 'a' ) ( 'N' | 'n' ) ( 'D' | 'd' ) )
            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1011:9: ( 'A' | 'a' ) ( 'N' | 'n' ) ( 'D' | 'd' )
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
            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1026:9: ( ( 'N' | 'n' ) ( 'O' | 'o' ) ( 'T' | 't' ) )
            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1027:9: ( 'N' | 'n' ) ( 'O' | 'o' ) ( 'T' | 't' )
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
            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1042:9: ( '~' )
            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1043:9: '~'
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
            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1047:9: ( '(' )
            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1048:9: '('
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
            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1052:9: ( ')' )
            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1053:9: ')'
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
            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1057:9: ( '+' )
            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1058:9: '+'
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
            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1062:9: ( '-' )
            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1063:9: '-'
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
            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1067:9: ( ':' )
            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1068:9: ':'
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
            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1072:9: ( '*' )
            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1073:9: '*'
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
            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1077:9: ( '..' )
            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1078:9: '..'
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
            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1082:9: ( '.' )
            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1083:9: '.'
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
            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1087:9: ( '&' )
            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1088:9: '&'
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
            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1092:9: ( '!' )
            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1093:9: '!'
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
            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1097:9: ( '|' )
            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1098:9: '|'
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
            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1102:9: ( '=' )
            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1103:9: '='
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
            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1107:9: ( '?' )
            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1108:9: '?'
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
            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1112:9: ( '{' )
            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1113:9: '{'
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
            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1117:9: ( '}' )
            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1118:9: '}'
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
            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1122:9: ( '[' )
            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1123:9: '['
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
            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1127:9: ( ']' )
            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1128:9: ']'
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
            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1132:9: ( ( 'T' | 't' ) ( 'O' | 'o' ) )
            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1133:9: ( 'T' | 't' ) ( 'O' | 'o' )
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
            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1144:9: ( ',' )
            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1145:9: ','
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
            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1149:9: ( '^' )
            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1150:9: '^'
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
            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1154:9: ( '$' )
            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1155:9: '$'
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
            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1159:9: ( '>' )
            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1160:9: '>'
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
            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1164:9: ( '<' )
            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1165:9: '<'
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
            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1169:9: ( '@' )
            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1170:9: '@'
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
            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1174:9: ( '%' )
            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1175:9: '%'
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
            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1184:9: ( ( 'a' .. 'z' | 'A' .. 'Z' | '_' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' | '$' | '#' | F_ESC )* )
            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1185:9: ( 'a' .. 'z' | 'A' .. 'Z' | '_' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' | '$' | '#' | F_ESC )*
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

            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1190:9: ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' | '$' | '#' | F_ESC )*
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
            	    // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1191:17: 'a' .. 'z'
            	    {
            	    matchRange('a','z'); if (state.failed) return ;

            	    }
            	    break;
            	case 2 :
            	    // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1192:19: 'A' .. 'Z'
            	    {
            	    matchRange('A','Z'); if (state.failed) return ;

            	    }
            	    break;
            	case 3 :
            	    // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1193:19: '0' .. '9'
            	    {
            	    matchRange('0','9'); if (state.failed) return ;

            	    }
            	    break;
            	case 4 :
            	    // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1194:19: '_'
            	    {
            	    match('_'); if (state.failed) return ;

            	    }
            	    break;
            	case 5 :
            	    // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1195:19: '$'
            	    {
            	    match('$'); if (state.failed) return ;

            	    }
            	    break;
            	case 6 :
            	    // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1196:19: '#'
            	    {
            	    match('#'); if (state.failed) return ;

            	    }
            	    break;
            	case 7 :
            	    // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1197:19: F_ESC
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
            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1202:9: ( ( PLUS | MINUS )? DECIMAL_NUMERAL )
            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1203:9: ( PLUS | MINUS )? DECIMAL_NUMERAL
            {
            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1203:9: ( PLUS | MINUS )?
            int alt14=2;
            int LA14_0 = input.LA(1);

            if ( (LA14_0=='+'||LA14_0=='-') ) {
                alt14=1;
            }
            switch (alt14) {
                case 1 :
                    // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:
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
            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1211:9: ( ( F_ESC | INWORD )+ )
            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1212:9: ( F_ESC | INWORD )+
            {
            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1212:9: ( F_ESC | INWORD )+
            int cnt15=0;
            loop15:
            do {
                int alt15=3;
                int LA15_0 = input.LA(1);

                if ( (LA15_0=='\\') ) {
                    alt15=1;
                }
                else if ( ((LA15_0>='0' && LA15_0<='9')||(LA15_0>='A' && LA15_0<='Z')||(LA15_0>='a' && LA15_0<='z')||(LA15_0>='\u00C0' && LA15_0<='\u00D6')||(LA15_0>='\u00D8' && LA15_0<='\u00F6')||(LA15_0>='\u00F8' && LA15_0<='\u1FFF')||(LA15_0>='\u3040' && LA15_0<='\u318F')||(LA15_0>='\u3300' && LA15_0<='\u337F')||(LA15_0>='\u3400' && LA15_0<='\u3D2D')||(LA15_0>='\u4E00' && LA15_0<='\u9FFF')||(LA15_0>='\uAC00' && LA15_0<='\uD7AF')||(LA15_0>='\uF900' && LA15_0<='\uFAFF')) ) {
                    alt15=2;
                }


                switch (alt15) {
            	case 1 :
            	    // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1213:17: F_ESC
            	    {
            	    mF_ESC(); if (state.failed) return ;

            	    }
            	    break;
            	case 2 :
            	    // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1214:19: INWORD
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
            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1219:9: ( ( F_ESC | INWORD )+ STAR )
            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1220:9: ( F_ESC | INWORD )+ STAR
            {
            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1220:9: ( F_ESC | INWORD )+
            int cnt16=0;
            loop16:
            do {
                int alt16=3;
                int LA16_0 = input.LA(1);

                if ( (LA16_0=='\\') ) {
                    alt16=1;
                }
                else if ( ((LA16_0>='0' && LA16_0<='9')||(LA16_0>='A' && LA16_0<='Z')||(LA16_0>='a' && LA16_0<='z')||(LA16_0>='\u00C0' && LA16_0<='\u00D6')||(LA16_0>='\u00D8' && LA16_0<='\u00F6')||(LA16_0>='\u00F8' && LA16_0<='\u1FFF')||(LA16_0>='\u3040' && LA16_0<='\u318F')||(LA16_0>='\u3300' && LA16_0<='\u337F')||(LA16_0>='\u3400' && LA16_0<='\u3D2D')||(LA16_0>='\u4E00' && LA16_0<='\u9FFF')||(LA16_0>='\uAC00' && LA16_0<='\uD7AF')||(LA16_0>='\uF900' && LA16_0<='\uFAFF')) ) {
                    alt16=2;
                }


                switch (alt16) {
            	case 1 :
            	    // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1221:17: F_ESC
            	    {
            	    mF_ESC(); if (state.failed) return ;

            	    }
            	    break;
            	case 2 :
            	    // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1222:19: INWORD
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
            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1228:9: ( ( F_ESC | INWORD | STAR | QUESTION_MARK )+ )
            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1229:9: ( F_ESC | INWORD | STAR | QUESTION_MARK )+
            {
            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1229:9: ( F_ESC | INWORD | STAR | QUESTION_MARK )+
            int cnt17=0;
            loop17:
            do {
                int alt17=5;
                int LA17_0 = input.LA(1);

                if ( (LA17_0=='\\') ) {
                    alt17=1;
                }
                else if ( ((LA17_0>='0' && LA17_0<='9')||(LA17_0>='A' && LA17_0<='Z')||(LA17_0>='a' && LA17_0<='z')||(LA17_0>='\u00C0' && LA17_0<='\u00D6')||(LA17_0>='\u00D8' && LA17_0<='\u00F6')||(LA17_0>='\u00F8' && LA17_0<='\u1FFF')||(LA17_0>='\u3040' && LA17_0<='\u318F')||(LA17_0>='\u3300' && LA17_0<='\u337F')||(LA17_0>='\u3400' && LA17_0<='\u3D2D')||(LA17_0>='\u4E00' && LA17_0<='\u9FFF')||(LA17_0>='\uAC00' && LA17_0<='\uD7AF')||(LA17_0>='\uF900' && LA17_0<='\uFAFF')) ) {
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
            	    // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1230:17: F_ESC
            	    {
            	    mF_ESC(); if (state.failed) return ;

            	    }
            	    break;
            	case 2 :
            	    // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1231:19: INWORD
            	    {
            	    mINWORD(); if (state.failed) return ;

            	    }
            	    break;
            	case 3 :
            	    // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1232:19: STAR
            	    {
            	    mSTAR(); if (state.failed) return ;

            	    }
            	    break;
            	case 4 :
            	    // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1233:19: QUESTION_MARK
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
            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1239:9: ( '\\\\' ( 'u' F_HEX F_HEX F_HEX F_HEX | . ) )
            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1240:9: '\\\\' ( 'u' F_HEX F_HEX F_HEX F_HEX | . )
            {
            match('\\'); if (state.failed) return ;
            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1241:9: ( 'u' F_HEX F_HEX F_HEX F_HEX | . )
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
                    // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1243:17: 'u' F_HEX F_HEX F_HEX F_HEX
                    {
                    match('u'); if (state.failed) return ;
                    mF_HEX(); if (state.failed) return ;
                    mF_HEX(); if (state.failed) return ;
                    mF_HEX(); if (state.failed) return ;
                    mF_HEX(); if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1245:19: .
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
            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1251:9: ( '0' .. '9' | 'a' .. 'f' | 'A' .. 'F' )
            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:
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
            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1259:9: ( '\\u0041' .. '\\u005A' | '\\u0061' .. '\\u007A' | '\\u00C0' .. '\\u00D6' | '\\u00D8' .. '\\u00F6' | '\\u00F8' .. '\\u00FF' | '\\u0100' .. '\\u1FFF' | '\\u3040' .. '\\u318F' | '\\u3300' .. '\\u337F' | '\\u3400' .. '\\u3D2D' | '\\u4E00' .. '\\u9FFF' | '\\uF900' .. '\\uFAFF' | '\\uAC00' .. '\\uD7AF' | '\\u0030' .. '\\u0039' | '\\u0660' .. '\\u0669' | '\\u06F0' .. '\\u06F9' | '\\u0966' .. '\\u096F' | '\\u09E6' .. '\\u09EF' | '\\u0A66' .. '\\u0A6F' | '\\u0AE6' .. '\\u0AEF' | '\\u0B66' .. '\\u0B6F' | '\\u0BE7' .. '\\u0BEF' | '\\u0C66' .. '\\u0C6F' | '\\u0CE6' .. '\\u0CEF' | '\\u0D66' .. '\\u0D6F' | '\\u0E50' .. '\\u0E59' | '\\u0ED0' .. '\\u0ED9' | '\\u1040' .. '\\u1049' )
            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:
            {
            if ( (input.LA(1)>='0' && input.LA(1)<='9')||(input.LA(1)>='A' && input.LA(1)<='Z')||(input.LA(1)>='a' && input.LA(1)<='z')||(input.LA(1)>='\u00C0' && input.LA(1)<='\u00D6')||(input.LA(1)>='\u00D8' && input.LA(1)<='\u00F6')||(input.LA(1)>='\u00F8' && input.LA(1)<='\u1FFF')||(input.LA(1)>='\u3040' && input.LA(1)<='\u318F')||(input.LA(1)>='\u3300' && input.LA(1)<='\u337F')||(input.LA(1)>='\u3400' && input.LA(1)<='\u3D2D')||(input.LA(1)>='\u4E00' && input.LA(1)<='\u9FFF')||(input.LA(1)>='\uAC00' && input.LA(1)<='\uD7AF')||(input.LA(1)>='\uF900' && input.LA(1)<='\uFAFF') ) {
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

            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1304:9: (d= START_RANGE_I r= DOTDOT | d= START_RANGE_F r= DOTDOT | ( PLUS | MINUS )? ( DIGIT )+ DOT ( DIGIT )* ( EXPONENT )? | ( PLUS | MINUS )? DOT ( DIGIT )+ ( EXPONENT )? | ( PLUS | MINUS )? ( DIGIT )+ EXPONENT )
            int alt28=5;
            alt28 = dfa28.predict(input);
            switch (alt28) {
                case 1 :
                    // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1305:9: d= START_RANGE_I r= DOTDOT
                    {
                    int dStart5073 = getCharIndex();
                    mSTART_RANGE_I(); if (state.failed) return ;
                    d = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, dStart5073, getCharIndex()-1);
                    int rStart5077 = getCharIndex();
                    mDOTDOT(); if (state.failed) return ;
                    r = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, rStart5077, getCharIndex()-1);
                    if ( state.backtracking==0 ) {

                            			d.setType(DECIMAL_INTEGER_LITERAL);
                            			emit(d);
                            			r.setType(DOTDOT);
                            			emit(r);
                          		
                    }

                    }
                    break;
                case 2 :
                    // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1312:11: d= START_RANGE_F r= DOTDOT
                    {
                    int dStart5102 = getCharIndex();
                    mSTART_RANGE_F(); if (state.failed) return ;
                    d = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, dStart5102, getCharIndex()-1);
                    int rStart5106 = getCharIndex();
                    mDOTDOT(); if (state.failed) return ;
                    r = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, rStart5106, getCharIndex()-1);
                    if ( state.backtracking==0 ) {

                            			d.setType(FLOATING_POINT_LITERAL);
                            			emit(d);
                            			r.setType(DOTDOT);
                            			emit(r);
                          		
                    }

                    }
                    break;
                case 3 :
                    // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1320:9: ( PLUS | MINUS )? ( DIGIT )+ DOT ( DIGIT )* ( EXPONENT )?
                    {
                    // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1320:9: ( PLUS | MINUS )?
                    int alt19=2;
                    int LA19_0 = input.LA(1);

                    if ( (LA19_0=='+'||LA19_0=='-') ) {
                        alt19=1;
                    }
                    switch (alt19) {
                        case 1 :
                            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:
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

                    // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1324:9: ( DIGIT )+
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
                    	    // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1324:9: DIGIT
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
                    // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1324:20: ( DIGIT )*
                    loop21:
                    do {
                        int alt21=2;
                        int LA21_0 = input.LA(1);

                        if ( ((LA21_0>='0' && LA21_0<='9')) ) {
                            alt21=1;
                        }


                        switch (alt21) {
                    	case 1 :
                    	    // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1324:20: DIGIT
                    	    {
                    	    mDIGIT(); if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    break loop21;
                        }
                    } while (true);

                    // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1324:27: ( EXPONENT )?
                    int alt22=2;
                    int LA22_0 = input.LA(1);

                    if ( (LA22_0=='E'||LA22_0=='e') ) {
                        alt22=1;
                    }
                    switch (alt22) {
                        case 1 :
                            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1324:27: EXPONENT
                            {
                            mEXPONENT(); if (state.failed) return ;

                            }
                            break;

                    }


                    }
                    break;
                case 4 :
                    // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1326:9: ( PLUS | MINUS )? DOT ( DIGIT )+ ( EXPONENT )?
                    {
                    // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1326:9: ( PLUS | MINUS )?
                    int alt23=2;
                    int LA23_0 = input.LA(1);

                    if ( (LA23_0=='+'||LA23_0=='-') ) {
                        alt23=1;
                    }
                    switch (alt23) {
                        case 1 :
                            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:
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
                    // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1330:13: ( DIGIT )+
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
                    	    // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1330:13: DIGIT
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

                    // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1330:20: ( EXPONENT )?
                    int alt25=2;
                    int LA25_0 = input.LA(1);

                    if ( (LA25_0=='E'||LA25_0=='e') ) {
                        alt25=1;
                    }
                    switch (alt25) {
                        case 1 :
                            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1330:20: EXPONENT
                            {
                            mEXPONENT(); if (state.failed) return ;

                            }
                            break;

                    }


                    }
                    break;
                case 5 :
                    // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1332:9: ( PLUS | MINUS )? ( DIGIT )+ EXPONENT
                    {
                    // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1332:9: ( PLUS | MINUS )?
                    int alt26=2;
                    int LA26_0 = input.LA(1);

                    if ( (LA26_0=='+'||LA26_0=='-') ) {
                        alt26=1;
                    }
                    switch (alt26) {
                        case 1 :
                            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:
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

                    // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1336:9: ( DIGIT )+
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
                    	    // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1336:9: DIGIT
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
            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1341:9: ( ( PLUS | MINUS )? ( DIGIT )+ )
            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1342:9: ( PLUS | MINUS )? ( DIGIT )+
            {
            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1342:9: ( PLUS | MINUS )?
            int alt29=2;
            int LA29_0 = input.LA(1);

            if ( (LA29_0=='+'||LA29_0=='-') ) {
                alt29=1;
            }
            switch (alt29) {
                case 1 :
                    // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:
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

            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1346:9: ( DIGIT )+
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
            	    // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1346:9: DIGIT
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
            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1351:9: ( ( PLUS | MINUS )? ( DIGIT )+ DOT )
            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1352:9: ( PLUS | MINUS )? ( DIGIT )+ DOT
            {
            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1352:9: ( PLUS | MINUS )?
            int alt31=2;
            int LA31_0 = input.LA(1);

            if ( (LA31_0=='+'||LA31_0=='-') ) {
                alt31=1;
            }
            switch (alt31) {
                case 1 :
                    // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:
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

            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1356:9: ( DIGIT )+
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
            	    // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1356:9: DIGIT
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
            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1364:9: ( ZERO_DIGIT | NON_ZERO_DIGIT ( DIGIT )* )
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
                    // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1365:9: ZERO_DIGIT
                    {
                    mZERO_DIGIT(); if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1366:11: NON_ZERO_DIGIT ( DIGIT )*
                    {
                    mNON_ZERO_DIGIT(); if (state.failed) return ;
                    // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1366:26: ( DIGIT )*
                    loop33:
                    do {
                        int alt33=2;
                        int LA33_0 = input.LA(1);

                        if ( ((LA33_0>='0' && LA33_0<='9')) ) {
                            alt33=1;
                        }


                        switch (alt33) {
                    	case 1 :
                    	    // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1366:26: DIGIT
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
            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1371:9: ( ZERO_DIGIT | NON_ZERO_DIGIT )
            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:
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
            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1378:9: ( '0' )
            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1379:9: '0'
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
            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1384:9: ( '1' .. '9' )
            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1385:9: '1' .. '9'
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
            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1390:9: ( ( 'e' | 'E' ) )
            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1391:9: ( 'e' | 'E' )
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
            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1399:9: ( E SIGNED_INTEGER )
            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1400:9: E SIGNED_INTEGER
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
            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1405:9: ( ( PLUS | MINUS )? ( DIGIT )+ )
            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1406:9: ( PLUS | MINUS )? ( DIGIT )+
            {
            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1406:9: ( PLUS | MINUS )?
            int alt35=2;
            int LA35_0 = input.LA(1);

            if ( (LA35_0=='+'||LA35_0=='-') ) {
                alt35=1;
            }
            switch (alt35) {
                case 1 :
                    // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:
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

            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1410:9: ( DIGIT )+
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
            	    // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1410:9: DIGIT
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
            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1419:9: ( ( ' ' | '\\t' | '\\r' | '\\n' )+ )
            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1420:9: ( ' ' | '\\t' | '\\r' | '\\n' )+
            {
            // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1420:9: ( ' ' | '\\t' | '\\r' | '\\n' )+
            int cnt37=0;
            loop37:
            do {
                int alt37=2;
                int LA37_0 = input.LA(1);

                if ( ((LA37_0>='\t' && LA37_0<='\n')||LA37_0=='\r'||LA37_0==' ') ) {
                    alt37=1;
                }


                switch (alt37) {
            	case 1 :
            	    // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:
            	    {
            	    if ( (input.LA(1)>='\t' && input.LA(1)<='\n')||input.LA(1)=='\r'||input.LA(1)==' ' ) {
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
        // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1:8: ( FTSPHRASE | URI | OR | AND | NOT | TILDA | LPAREN | RPAREN | PLUS | MINUS | COLON | STAR | DOTDOT | DOT | AMP | EXCLAMATION | BAR | EQUALS | QUESTION_MARK | LCURL | RCURL | LSQUARE | RSQUARE | TO | COMMA | CARAT | DOLLAR | GT | LT | AT | PERCENT | ID | DECIMAL_INTEGER_LITERAL | FTSWORD | FTSPRE | FTSWILD | FLOATING_POINT_LITERAL | WS )
        int alt38=38;
        alt38 = dfa38.predict(input);
        switch (alt38) {
            case 1 :
                // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1:10: FTSPHRASE
                {
                mFTSPHRASE(); if (state.failed) return ;

                }
                break;
            case 2 :
                // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1:20: URI
                {
                mURI(); if (state.failed) return ;

                }
                break;
            case 3 :
                // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1:24: OR
                {
                mOR(); if (state.failed) return ;

                }
                break;
            case 4 :
                // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1:27: AND
                {
                mAND(); if (state.failed) return ;

                }
                break;
            case 5 :
                // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1:31: NOT
                {
                mNOT(); if (state.failed) return ;

                }
                break;
            case 6 :
                // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1:35: TILDA
                {
                mTILDA(); if (state.failed) return ;

                }
                break;
            case 7 :
                // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1:41: LPAREN
                {
                mLPAREN(); if (state.failed) return ;

                }
                break;
            case 8 :
                // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1:48: RPAREN
                {
                mRPAREN(); if (state.failed) return ;

                }
                break;
            case 9 :
                // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1:55: PLUS
                {
                mPLUS(); if (state.failed) return ;

                }
                break;
            case 10 :
                // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1:60: MINUS
                {
                mMINUS(); if (state.failed) return ;

                }
                break;
            case 11 :
                // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1:66: COLON
                {
                mCOLON(); if (state.failed) return ;

                }
                break;
            case 12 :
                // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1:72: STAR
                {
                mSTAR(); if (state.failed) return ;

                }
                break;
            case 13 :
                // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1:77: DOTDOT
                {
                mDOTDOT(); if (state.failed) return ;

                }
                break;
            case 14 :
                // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1:84: DOT
                {
                mDOT(); if (state.failed) return ;

                }
                break;
            case 15 :
                // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1:88: AMP
                {
                mAMP(); if (state.failed) return ;

                }
                break;
            case 16 :
                // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1:92: EXCLAMATION
                {
                mEXCLAMATION(); if (state.failed) return ;

                }
                break;
            case 17 :
                // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1:104: BAR
                {
                mBAR(); if (state.failed) return ;

                }
                break;
            case 18 :
                // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1:108: EQUALS
                {
                mEQUALS(); if (state.failed) return ;

                }
                break;
            case 19 :
                // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1:115: QUESTION_MARK
                {
                mQUESTION_MARK(); if (state.failed) return ;

                }
                break;
            case 20 :
                // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1:129: LCURL
                {
                mLCURL(); if (state.failed) return ;

                }
                break;
            case 21 :
                // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1:135: RCURL
                {
                mRCURL(); if (state.failed) return ;

                }
                break;
            case 22 :
                // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1:141: LSQUARE
                {
                mLSQUARE(); if (state.failed) return ;

                }
                break;
            case 23 :
                // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1:149: RSQUARE
                {
                mRSQUARE(); if (state.failed) return ;

                }
                break;
            case 24 :
                // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1:157: TO
                {
                mTO(); if (state.failed) return ;

                }
                break;
            case 25 :
                // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1:160: COMMA
                {
                mCOMMA(); if (state.failed) return ;

                }
                break;
            case 26 :
                // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1:166: CARAT
                {
                mCARAT(); if (state.failed) return ;

                }
                break;
            case 27 :
                // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1:172: DOLLAR
                {
                mDOLLAR(); if (state.failed) return ;

                }
                break;
            case 28 :
                // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1:179: GT
                {
                mGT(); if (state.failed) return ;

                }
                break;
            case 29 :
                // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1:182: LT
                {
                mLT(); if (state.failed) return ;

                }
                break;
            case 30 :
                // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1:185: AT
                {
                mAT(); if (state.failed) return ;

                }
                break;
            case 31 :
                // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1:188: PERCENT
                {
                mPERCENT(); if (state.failed) return ;

                }
                break;
            case 32 :
                // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1:196: ID
                {
                mID(); if (state.failed) return ;

                }
                break;
            case 33 :
                // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1:199: DECIMAL_INTEGER_LITERAL
                {
                mDECIMAL_INTEGER_LITERAL(); if (state.failed) return ;

                }
                break;
            case 34 :
                // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1:223: FTSWORD
                {
                mFTSWORD(); if (state.failed) return ;

                }
                break;
            case 35 :
                // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1:231: FTSPRE
                {
                mFTSPRE(); if (state.failed) return ;

                }
                break;
            case 36 :
                // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1:238: FTSWILD
                {
                mFTSWILD(); if (state.failed) return ;

                }
                break;
            case 37 :
                // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1:246: FLOATING_POINT_LITERAL
                {
                mFLOATING_POINT_LITERAL(); if (state.failed) return ;

                }
                break;
            case 38 :
                // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1:269: WS
                {
                mWS(); if (state.failed) return ;

                }
                break;

        }

    }

    // $ANTLR start synpred1_FTS
    public final void synpred1_FTS_fragment() throws RecognitionException {   
        // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:887:17: ( F_URI_ALPHA | F_URI_DIGIT | F_URI_OTHER )
        // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:
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
        // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:901:19: ( '//' )
        // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:901:20: '//'
        {
        match("//"); if (state.failed) return ;


        }
    }
    // $ANTLR end synpred2_FTS

    // $ANTLR start synpred3_FTS
    public final void synpred3_FTS_fragment() throws RecognitionException {   
        // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:903:25: ( F_URI_ALPHA | F_URI_DIGIT | F_URI_OTHER | COLON )
        // C:\\workspaces\\V3.4-BUG-FIX\\V3.4-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:
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
        "\5\uffff";
    static final String DFA5_eofS =
        "\5\uffff";
    static final String DFA5_minS =
        "\2\41\1\uffff\1\0\1\uffff";
    static final String DFA5_maxS =
        "\2\176\1\uffff\1\0\1\uffff";
    static final String DFA5_acceptS =
        "\2\uffff\1\2\1\uffff\1\1";
    static final String DFA5_specialS =
        "\3\uffff\1\0\1\uffff}>";
    static final String[] DFA5_transitionS = {
            "\1\1\1\uffff\1\2\1\1\1\uffff\11\1\1\2\12\1\1\2\1\1\1\uffff"+
            "\1\1\1\uffff\1\2\34\1\1\uffff\1\1\1\uffff\1\1\1\uffff\32\1\2"+
            "\uffff\1\2\1\1",
            "\1\1\1\uffff\1\2\1\1\1\uffff\11\1\1\2\12\1\1\3\1\1\1\uffff"+
            "\1\1\1\uffff\1\2\34\1\1\uffff\1\1\1\uffff\1\1\1\uffff\32\1\2"+
            "\uffff\1\2\1\1",
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
            return "886:9: ( ( F_URI_ALPHA | F_URI_DIGIT | F_URI_OTHER )=> ( F_URI_ALPHA | F_URI_DIGIT | F_URI_OTHER )+ COLON )?";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            IntStream input = _input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA5_3 = input.LA(1);

                         
                        int index5_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_FTS()) ) {s = 4;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index5_3);
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
        "\5\uffff\1\7\1\11\3\uffff";
    static final String DFA28_eofS =
        "\12\uffff";
    static final String DFA28_minS =
        "\1\53\2\56\2\uffff\2\56\3\uffff";
    static final String DFA28_maxS =
        "\2\71\1\145\2\uffff\2\56\3\uffff";
    static final String DFA28_acceptS =
        "\3\uffff\1\4\1\5\2\uffff\1\3\1\2\1\1";
    static final String DFA28_specialS =
        "\12\uffff}>";
    static final String[] DFA28_transitionS = {
            "\1\1\1\uffff\1\1\1\3\1\uffff\12\2",
            "\1\3\1\uffff\12\2",
            "\1\5\1\uffff\12\2\13\uffff\1\4\37\uffff\1\4",
            "",
            "",
            "\1\6",
            "\1\10",
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
            return "1302:1: FLOATING_POINT_LITERAL : (d= START_RANGE_I r= DOTDOT | d= START_RANGE_F r= DOTDOT | ( PLUS | MINUS )? ( DIGIT )+ DOT ( DIGIT )* ( EXPONENT )? | ( PLUS | MINUS )? DOT ( DIGIT )+ ( EXPONENT )? | ( PLUS | MINUS )? ( DIGIT )+ EXPONENT );";
        }
    }
    static final String DFA38_eotS =
        "\2\uffff\1\45\3\42\3\uffff\1\64\1\67\1\uffff\1\70\1\72\4\uffff"+
        "\1\73\3\uffff\1\42\7\uffff\1\42\2\76\2\uffff\1\104\3\uffff\1\105"+
        "\1\uffff\1\42\1\110\1\uffff\1\105\6\42\1\76\2\uffff\1\76\5\uffff"+
        "\2\116\1\uffff\2\104\1\76\2\104\2\uffff\2\42\1\uffff\2\124\2\125"+
        "\1\76\1\uffff\2\104\3\42\2\uffff\1\104\3\42\1\104\3\42\1\104\3\42";
    static final String DFA38_eofS =
        "\142\uffff";
    static final String DFA38_minS =
        "\1\11\1\uffff\1\41\3\52\3\uffff\2\56\1\uffff\1\52\1\56\4\uffff"+
        "\1\52\3\uffff\1\52\7\uffff\3\52\1\0\1\uffff\1\52\3\uffff\1\43\1"+
        "\0\2\52\1\uffff\1\43\6\52\1\56\2\uffff\1\56\5\uffff\2\43\1\uffff"+
        "\5\52\2\uffff\2\52\1\uffff\4\43\1\56\1\uffff\5\52\2\uffff\14\52";
    static final String DFA38_maxS =
        "\1\ufaff\1\uffff\1\176\3\ufaff\3\uffff\2\71\1\uffff\1\ufaff\1\71"+
        "\4\uffff\1\ufaff\3\uffff\1\ufaff\7\uffff\3\ufaff\1\uffff\1\uffff"+
        "\1\ufaff\3\uffff\1\ufaff\1\uffff\2\ufaff\1\uffff\7\ufaff\1\145\2"+
        "\uffff\1\145\5\uffff\2\ufaff\1\uffff\5\ufaff\2\uffff\2\ufaff\1\uffff"+
        "\4\ufaff\1\145\1\uffff\5\ufaff\2\uffff\14\ufaff";
    static final String DFA38_acceptS =
        "\1\uffff\1\1\4\uffff\1\6\1\7\1\10\2\uffff\1\13\2\uffff\1\17\1\20"+
        "\1\21\1\22\1\uffff\1\25\1\26\1\27\1\uffff\1\31\1\32\1\33\1\34\1"+
        "\35\1\36\1\37\4\uffff\1\40\1\uffff\1\46\1\24\1\2\4\uffff\1\44\10"+
        "\uffff\1\11\1\45\1\uffff\1\12\1\14\1\15\1\16\1\23\2\uffff\1\41\5"+
        "\uffff\1\42\1\3\2\uffff\1\43\5\uffff\1\30\5\uffff\1\4\1\5\14\uffff";
    static final String DFA38_specialS =
        "\41\uffff\1\1\6\uffff\1\0\71\uffff}>";
    static final String[] DFA38_transitionS = {
            "\2\44\2\uffff\1\44\22\uffff\1\44\1\17\1\1\1\uffff\1\31\1\35"+
            "\1\16\1\1\1\7\1\10\1\14\1\11\1\27\1\12\1\15\1\uffff\1\37\11"+
            "\40\1\13\1\uffff\1\33\1\21\1\32\1\22\1\34\1\4\14\36\1\5\1\3"+
            "\4\36\1\26\6\36\1\24\1\41\1\25\1\30\1\42\1\uffff\1\4\14\36\1"+
            "\5\1\3\4\36\1\26\6\36\1\2\1\20\1\23\1\6\101\uffff\27\43\1\uffff"+
            "\37\43\1\uffff\u1f08\43\u1040\uffff\u0150\43\u0170\uffff\u0080"+
            "\43\u0080\uffff\u092e\43\u10d2\uffff\u5200\43\u0c00\uffff\u2bb0"+
            "\43\u2150\uffff\u0200\43",
            "",
            "\1\46\1\uffff\2\46\1\uffff\26\46\1\uffff\1\46\1\uffff\35\46"+
            "\1\uffff\1\46\1\uffff\1\46\1\uffff\32\46\2\uffff\2\46",
            "\1\52\5\uffff\12\56\5\uffff\1\53\1\uffff\21\55\1\54\10\55"+
            "\1\uffff\1\50\4\uffff\21\51\1\47\10\51\105\uffff\27\43\1\uffff"+
            "\37\43\1\uffff\u1f08\43\u1040\uffff\u0150\43\u0170\uffff\u0080"+
            "\43\u0080\uffff\u092e\43\u10d2\uffff\u5200\43\u0c00\uffff\u2bb0"+
            "\43\u2150\uffff\u0200\43",
            "\1\52\5\uffff\12\56\5\uffff\1\53\1\uffff\15\55\1\60\14\55"+
            "\1\uffff\1\50\4\uffff\15\51\1\57\14\51\105\uffff\27\43\1\uffff"+
            "\37\43\1\uffff\u1f08\43\u1040\uffff\u0150\43\u0170\uffff\u0080"+
            "\43\u0080\uffff\u092e\43\u10d2\uffff\u5200\43\u0c00\uffff\u2bb0"+
            "\43\u2150\uffff\u0200\43",
            "\1\52\5\uffff\12\56\5\uffff\1\53\1\uffff\16\55\1\62\13\55"+
            "\1\uffff\1\50\4\uffff\16\51\1\61\13\51\105\uffff\27\43\1\uffff"+
            "\37\43\1\uffff\u1f08\43\u1040\uffff\u0150\43\u0170\uffff\u0080"+
            "\43\u0080\uffff\u092e\43\u10d2\uffff\u5200\43\u0c00\uffff\u2bb0"+
            "\43\u2150\uffff\u0200\43",
            "",
            "",
            "",
            "\1\65\1\uffff\1\63\11\66",
            "\1\65\1\uffff\1\63\11\66",
            "",
            "\1\53\5\uffff\12\53\5\uffff\1\53\1\uffff\32\53\1\uffff\1\53"+
            "\4\uffff\32\53\105\uffff\27\53\1\uffff\37\53\1\uffff\u1f08\53"+
            "\u1040\uffff\u0150\53\u0170\uffff\u0080\53\u0080\uffff\u092e"+
            "\53\u10d2\uffff\u5200\53\u0c00\uffff\u2bb0\53\u2150\uffff\u0200"+
            "\53",
            "\1\71\1\uffff\12\65",
            "",
            "",
            "",
            "",
            "\1\53\5\uffff\12\53\5\uffff\1\53\1\uffff\32\53\1\uffff\1\53"+
            "\4\uffff\32\53\105\uffff\27\53\1\uffff\37\53\1\uffff\u1f08\53"+
            "\u1040\uffff\u0150\53\u0170\uffff\u0080\53\u0080\uffff\u092e"+
            "\53\u10d2\uffff\u5200\53\u0c00\uffff\u2bb0\53\u2150\uffff\u0200"+
            "\53",
            "",
            "",
            "",
            "\1\52\5\uffff\12\56\5\uffff\1\53\1\uffff\16\55\1\75\13\55"+
            "\1\uffff\1\50\4\uffff\16\51\1\74\13\51\105\uffff\27\43\1\uffff"+
            "\37\43\1\uffff\u1f08\43\u1040\uffff\u0150\43\u0170\uffff\u0080"+
            "\43\u0080\uffff\u092e\43\u10d2\uffff\u5200\43\u0c00\uffff\u2bb0"+
            "\43\u2150\uffff\u0200\43",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\52\5\uffff\12\56\5\uffff\1\53\1\uffff\32\55\1\uffff\1\50"+
            "\4\uffff\32\51\105\uffff\27\43\1\uffff\37\43\1\uffff\u1f08\43"+
            "\u1040\uffff\u0150\43\u0170\uffff\u0080\43\u0080\uffff\u092e"+
            "\43\u10d2\uffff\u5200\43\u0c00\uffff\u2bb0\43\u2150\uffff\u0200"+
            "\43",
            "\1\52\3\uffff\1\65\1\uffff\12\77\5\uffff\1\53\1\uffff\4\43"+
            "\1\100\25\43\1\uffff\1\41\4\uffff\4\43\1\100\25\43\105\uffff"+
            "\27\43\1\uffff\37\43\1\uffff\u1f08\43\u1040\uffff\u0150\43\u0170"+
            "\uffff\u0080\43\u0080\uffff\u092e\43\u10d2\uffff\u5200\43\u0c00"+
            "\uffff\u2bb0\43\u2150\uffff\u0200\43",
            "\1\52\3\uffff\1\65\1\uffff\12\101\5\uffff\1\53\1\uffff\4\43"+
            "\1\100\25\43\1\uffff\1\41\4\uffff\4\43\1\100\25\43\105\uffff"+
            "\27\43\1\uffff\37\43\1\uffff\u1f08\43\u1040\uffff\u0150\43\u0170"+
            "\uffff\u0080\43\u0080\uffff\u092e\43\u10d2\uffff\u5200\43\u0c00"+
            "\uffff\u2bb0\43\u2150\uffff\u0200\43",
            "\165\103\1\102\uff8a\103",
            "",
            "\1\52\5\uffff\12\43\5\uffff\1\53\1\uffff\32\43\1\uffff\1\41"+
            "\4\uffff\32\43\105\uffff\27\43\1\uffff\37\43\1\uffff\u1f08\43"+
            "\u1040\uffff\u0150\43\u0170\uffff\u0080\43\u0080\uffff\u092e"+
            "\43\u10d2\uffff\u5200\43\u0c00\uffff\u2bb0\43\u2150\uffff\u0200"+
            "\43",
            "",
            "",
            "",
            "\2\42\5\uffff\1\52\5\uffff\12\56\5\uffff\1\53\1\uffff\32\55"+
            "\1\uffff\1\50\2\uffff\1\42\1\uffff\32\51\105\uffff\27\43\1\uffff"+
            "\37\43\1\uffff\u1f08\43\u1040\uffff\u0150\43\u0170\uffff\u0080"+
            "\43\u0080\uffff\u092e\43\u10d2\uffff\u5200\43\u0c00\uffff\u2bb0"+
            "\43\u2150\uffff\u0200\43",
            "\165\107\1\106\uff8a\107",
            "\1\52\5\uffff\12\56\5\uffff\1\53\1\uffff\32\55\1\uffff\1\50"+
            "\4\uffff\32\51\105\uffff\27\43\1\uffff\37\43\1\uffff\u1f08\43"+
            "\u1040\uffff\u0150\43\u0170\uffff\u0080\43\u0080\uffff\u092e"+
            "\43\u10d2\uffff\u5200\43\u0c00\uffff\u2bb0\43\u2150\uffff\u0200"+
            "\43",
            "\1\53\5\uffff\12\53\5\uffff\1\53\1\uffff\32\53\1\uffff\1\53"+
            "\4\uffff\32\53\105\uffff\27\53\1\uffff\37\53\1\uffff\u1f08\53"+
            "\u1040\uffff\u0150\53\u0170\uffff\u0080\53\u0080\uffff\u092e"+
            "\53\u10d2\uffff\u5200\53\u0c00\uffff\u2bb0\53\u2150\uffff\u0200"+
            "\53",
            "",
            "\2\42\5\uffff\1\52\5\uffff\12\56\5\uffff\1\53\1\uffff\32\55"+
            "\1\uffff\1\50\2\uffff\1\42\1\uffff\32\51\105\uffff\27\43\1\uffff"+
            "\37\43\1\uffff\u1f08\43\u1040\uffff\u0150\43\u0170\uffff\u0080"+
            "\43\u0080\uffff\u092e\43\u10d2\uffff\u5200\43\u0c00\uffff\u2bb0"+
            "\43\u2150\uffff\u0200\43",
            "\1\52\5\uffff\12\56\5\uffff\1\53\1\uffff\32\55\1\uffff\1\50"+
            "\4\uffff\32\51\105\uffff\27\43\1\uffff\37\43\1\uffff\u1f08\43"+
            "\u1040\uffff\u0150\43\u0170\uffff\u0080\43\u0080\uffff\u092e"+
            "\43\u10d2\uffff\u5200\43\u0c00\uffff\u2bb0\43\u2150\uffff\u0200"+
            "\43",
            "\1\52\5\uffff\12\56\5\uffff\1\53\1\uffff\32\55\1\uffff\1\50"+
            "\4\uffff\32\51\105\uffff\27\43\1\uffff\37\43\1\uffff\u1f08\43"+
            "\u1040\uffff\u0150\43\u0170\uffff\u0080\43\u0080\uffff\u092e"+
            "\43\u10d2\uffff\u5200\43\u0c00\uffff\u2bb0\43\u2150\uffff\u0200"+
            "\43",
            "\1\52\5\uffff\12\56\5\uffff\1\53\1\uffff\3\55\1\112\26\55"+
            "\1\uffff\1\50\4\uffff\3\51\1\111\26\51\105\uffff\27\43\1\uffff"+
            "\37\43\1\uffff\u1f08\43\u1040\uffff\u0150\43\u0170\uffff\u0080"+
            "\43\u0080\uffff\u092e\43\u10d2\uffff\u5200\43\u0c00\uffff\u2bb0"+
            "\43\u2150\uffff\u0200\43",
            "\1\52\5\uffff\12\56\5\uffff\1\53\1\uffff\3\55\1\112\26\55"+
            "\1\uffff\1\50\4\uffff\3\51\1\111\26\51\105\uffff\27\43\1\uffff"+
            "\37\43\1\uffff\u1f08\43\u1040\uffff\u0150\43\u0170\uffff\u0080"+
            "\43\u0080\uffff\u092e\43\u10d2\uffff\u5200\43\u0c00\uffff\u2bb0"+
            "\43\u2150\uffff\u0200\43",
            "\1\52\5\uffff\12\56\5\uffff\1\53\1\uffff\23\55\1\114\6\55"+
            "\1\uffff\1\50\4\uffff\23\51\1\113\6\51\105\uffff\27\43\1\uffff"+
            "\37\43\1\uffff\u1f08\43\u1040\uffff\u0150\43\u0170\uffff\u0080"+
            "\43\u0080\uffff\u092e\43\u10d2\uffff\u5200\43\u0c00\uffff\u2bb0"+
            "\43\u2150\uffff\u0200\43",
            "\1\52\5\uffff\12\56\5\uffff\1\53\1\uffff\23\55\1\114\6\55"+
            "\1\uffff\1\50\4\uffff\23\51\1\113\6\51\105\uffff\27\43\1\uffff"+
            "\37\43\1\uffff\u1f08\43\u1040\uffff\u0150\43\u0170\uffff\u0080"+
            "\43\u0080\uffff\u092e\43\u10d2\uffff\u5200\43\u0c00\uffff\u2bb0"+
            "\43\u2150\uffff\u0200\43",
            "\1\65\1\uffff\12\65\13\uffff\1\65\37\uffff\1\65",
            "",
            "",
            "\1\65\1\uffff\12\115\13\uffff\1\65\37\uffff\1\65",
            "",
            "",
            "",
            "",
            "",
            "\2\42\5\uffff\1\52\5\uffff\12\56\5\uffff\1\53\1\uffff\32\55"+
            "\1\uffff\1\50\2\uffff\1\42\1\uffff\32\51\105\uffff\27\43\1\uffff"+
            "\37\43\1\uffff\u1f08\43\u1040\uffff\u0150\43\u0170\uffff\u0080"+
            "\43\u0080\uffff\u092e\43\u10d2\uffff\u5200\43\u0c00\uffff\u2bb0"+
            "\43\u2150\uffff\u0200\43",
            "\2\42\5\uffff\1\52\5\uffff\12\56\5\uffff\1\53\1\uffff\32\55"+
            "\1\uffff\1\50\2\uffff\1\42\1\uffff\32\51\105\uffff\27\43\1\uffff"+
            "\37\43\1\uffff\u1f08\43\u1040\uffff\u0150\43\u0170\uffff\u0080"+
            "\43\u0080\uffff\u092e\43\u10d2\uffff\u5200\43\u0c00\uffff\u2bb0"+
            "\43\u2150\uffff\u0200\43",
            "",
            "\1\52\3\uffff\1\65\1\uffff\12\77\5\uffff\1\53\1\uffff\4\43"+
            "\1\100\25\43\1\uffff\1\41\4\uffff\4\43\1\100\25\43\105\uffff"+
            "\27\43\1\uffff\37\43\1\uffff\u1f08\43\u1040\uffff\u0150\43\u0170"+
            "\uffff\u0080\43\u0080\uffff\u092e\43\u10d2\uffff\u5200\43\u0c00"+
            "\uffff\u2bb0\43\u2150\uffff\u0200\43",
            "\1\52\1\65\1\uffff\1\65\2\uffff\12\117\5\uffff\1\53\1\uffff"+
            "\32\43\1\uffff\1\41\4\uffff\32\43\105\uffff\27\43\1\uffff\37"+
            "\43\1\uffff\u1f08\43\u1040\uffff\u0150\43\u0170\uffff\u0080"+
            "\43\u0080\uffff\u092e\43\u10d2\uffff\u5200\43\u0c00\uffff\u2bb0"+
            "\43\u2150\uffff\u0200\43",
            "\1\52\3\uffff\1\65\1\uffff\12\101\5\uffff\1\53\1\uffff\4\43"+
            "\1\100\25\43\1\uffff\1\41\4\uffff\4\43\1\100\25\43\105\uffff"+
            "\27\43\1\uffff\37\43\1\uffff\u1f08\43\u1040\uffff\u0150\43\u0170"+
            "\uffff\u0080\43\u0080\uffff\u092e\43\u10d2\uffff\u5200\43\u0c00"+
            "\uffff\u2bb0\43\u2150\uffff\u0200\43",
            "\1\52\5\uffff\12\120\5\uffff\1\53\1\uffff\6\120\24\43\1\uffff"+
            "\1\41\4\uffff\6\120\24\43\105\uffff\27\43\1\uffff\37\43\1\uffff"+
            "\u1f08\43\u1040\uffff\u0150\43\u0170\uffff\u0080\43\u0080\uffff"+
            "\u092e\43\u10d2\uffff\u5200\43\u0c00\uffff\u2bb0\43\u2150\uffff"+
            "\u0200\43",
            "\1\52\5\uffff\12\43\5\uffff\1\53\1\uffff\32\43\1\uffff\1\41"+
            "\4\uffff\32\43\105\uffff\27\43\1\uffff\37\43\1\uffff\u1f08\43"+
            "\u1040\uffff\u0150\43\u0170\uffff\u0080\43\u0080\uffff\u092e"+
            "\43\u10d2\uffff\u5200\43\u0c00\uffff\u2bb0\43\u2150\uffff\u0200"+
            "\43",
            "",
            "",
            "\1\52\5\uffff\12\123\5\uffff\1\53\1\uffff\6\122\24\55\1\uffff"+
            "\1\50\4\uffff\6\121\24\51\105\uffff\27\43\1\uffff\37\43\1\uffff"+
            "\u1f08\43\u1040\uffff\u0150\43\u0170\uffff\u0080\43\u0080\uffff"+
            "\u092e\43\u10d2\uffff\u5200\43\u0c00\uffff\u2bb0\43\u2150\uffff"+
            "\u0200\43",
            "\1\52\5\uffff\12\56\5\uffff\1\53\1\uffff\32\55\1\uffff\1\50"+
            "\4\uffff\32\51\105\uffff\27\43\1\uffff\37\43\1\uffff\u1f08\43"+
            "\u1040\uffff\u0150\43\u0170\uffff\u0080\43\u0080\uffff\u092e"+
            "\43\u10d2\uffff\u5200\43\u0c00\uffff\u2bb0\43\u2150\uffff\u0200"+
            "\43",
            "",
            "\2\42\5\uffff\1\52\5\uffff\12\56\5\uffff\1\53\1\uffff\32\55"+
            "\1\uffff\1\50\2\uffff\1\42\1\uffff\32\51\105\uffff\27\43\1\uffff"+
            "\37\43\1\uffff\u1f08\43\u1040\uffff\u0150\43\u0170\uffff\u0080"+
            "\43\u0080\uffff\u092e\43\u10d2\uffff\u5200\43\u0c00\uffff\u2bb0"+
            "\43\u2150\uffff\u0200\43",
            "\2\42\5\uffff\1\52\5\uffff\12\56\5\uffff\1\53\1\uffff\32\55"+
            "\1\uffff\1\50\2\uffff\1\42\1\uffff\32\51\105\uffff\27\43\1\uffff"+
            "\37\43\1\uffff\u1f08\43\u1040\uffff\u0150\43\u0170\uffff\u0080"+
            "\43\u0080\uffff\u092e\43\u10d2\uffff\u5200\43\u0c00\uffff\u2bb0"+
            "\43\u2150\uffff\u0200\43",
            "\2\42\5\uffff\1\52\5\uffff\12\56\5\uffff\1\53\1\uffff\32\55"+
            "\1\uffff\1\50\2\uffff\1\42\1\uffff\32\51\105\uffff\27\43\1\uffff"+
            "\37\43\1\uffff\u1f08\43\u1040\uffff\u0150\43\u0170\uffff\u0080"+
            "\43\u0080\uffff\u092e\43\u10d2\uffff\u5200\43\u0c00\uffff\u2bb0"+
            "\43\u2150\uffff\u0200\43",
            "\2\42\5\uffff\1\52\5\uffff\12\56\5\uffff\1\53\1\uffff\32\55"+
            "\1\uffff\1\50\2\uffff\1\42\1\uffff\32\51\105\uffff\27\43\1\uffff"+
            "\37\43\1\uffff\u1f08\43\u1040\uffff\u0150\43\u0170\uffff\u0080"+
            "\43\u0080\uffff\u092e\43\u10d2\uffff\u5200\43\u0c00\uffff\u2bb0"+
            "\43\u2150\uffff\u0200\43",
            "\1\65\1\uffff\12\115\13\uffff\1\65\37\uffff\1\65",
            "",
            "\1\52\5\uffff\12\117\5\uffff\1\53\1\uffff\32\43\1\uffff\1"+
            "\41\4\uffff\32\43\105\uffff\27\43\1\uffff\37\43\1\uffff\u1f08"+
            "\43\u1040\uffff\u0150\43\u0170\uffff\u0080\43\u0080\uffff\u092e"+
            "\43\u10d2\uffff\u5200\43\u0c00\uffff\u2bb0\43\u2150\uffff\u0200"+
            "\43",
            "\1\52\5\uffff\12\126\5\uffff\1\53\1\uffff\6\126\24\43\1\uffff"+
            "\1\41\4\uffff\6\126\24\43\105\uffff\27\43\1\uffff\37\43\1\uffff"+
            "\u1f08\43\u1040\uffff\u0150\43\u0170\uffff\u0080\43\u0080\uffff"+
            "\u092e\43\u10d2\uffff\u5200\43\u0c00\uffff\u2bb0\43\u2150\uffff"+
            "\u0200\43",
            "\1\52\5\uffff\12\131\5\uffff\1\53\1\uffff\6\130\24\55\1\uffff"+
            "\1\50\4\uffff\6\127\24\51\105\uffff\27\43\1\uffff\37\43\1\uffff"+
            "\u1f08\43\u1040\uffff\u0150\43\u0170\uffff\u0080\43\u0080\uffff"+
            "\u092e\43\u10d2\uffff\u5200\43\u0c00\uffff\u2bb0\43\u2150\uffff"+
            "\u0200\43",
            "\1\52\5\uffff\12\131\5\uffff\1\53\1\uffff\6\130\24\55\1\uffff"+
            "\1\50\4\uffff\6\127\24\51\105\uffff\27\43\1\uffff\37\43\1\uffff"+
            "\u1f08\43\u1040\uffff\u0150\43\u0170\uffff\u0080\43\u0080\uffff"+
            "\u092e\43\u10d2\uffff\u5200\43\u0c00\uffff\u2bb0\43\u2150\uffff"+
            "\u0200\43",
            "\1\52\5\uffff\12\131\5\uffff\1\53\1\uffff\6\130\24\55\1\uffff"+
            "\1\50\4\uffff\6\127\24\51\105\uffff\27\43\1\uffff\37\43\1\uffff"+
            "\u1f08\43\u1040\uffff\u0150\43\u0170\uffff\u0080\43\u0080\uffff"+
            "\u092e\43\u10d2\uffff\u5200\43\u0c00\uffff\u2bb0\43\u2150\uffff"+
            "\u0200\43",
            "",
            "",
            "\1\52\5\uffff\12\132\5\uffff\1\53\1\uffff\6\132\24\43\1\uffff"+
            "\1\41\4\uffff\6\132\24\43\105\uffff\27\43\1\uffff\37\43\1\uffff"+
            "\u1f08\43\u1040\uffff\u0150\43\u0170\uffff\u0080\43\u0080\uffff"+
            "\u092e\43\u10d2\uffff\u5200\43\u0c00\uffff\u2bb0\43\u2150\uffff"+
            "\u0200\43",
            "\1\52\5\uffff\12\135\5\uffff\1\53\1\uffff\6\134\24\55\1\uffff"+
            "\1\50\4\uffff\6\133\24\51\105\uffff\27\43\1\uffff\37\43\1\uffff"+
            "\u1f08\43\u1040\uffff\u0150\43\u0170\uffff\u0080\43\u0080\uffff"+
            "\u092e\43\u10d2\uffff\u5200\43\u0c00\uffff\u2bb0\43\u2150\uffff"+
            "\u0200\43",
            "\1\52\5\uffff\12\135\5\uffff\1\53\1\uffff\6\134\24\55\1\uffff"+
            "\1\50\4\uffff\6\133\24\51\105\uffff\27\43\1\uffff\37\43\1\uffff"+
            "\u1f08\43\u1040\uffff\u0150\43\u0170\uffff\u0080\43\u0080\uffff"+
            "\u092e\43\u10d2\uffff\u5200\43\u0c00\uffff\u2bb0\43\u2150\uffff"+
            "\u0200\43",
            "\1\52\5\uffff\12\135\5\uffff\1\53\1\uffff\6\134\24\55\1\uffff"+
            "\1\50\4\uffff\6\133\24\51\105\uffff\27\43\1\uffff\37\43\1\uffff"+
            "\u1f08\43\u1040\uffff\u0150\43\u0170\uffff\u0080\43\u0080\uffff"+
            "\u092e\43\u10d2\uffff\u5200\43\u0c00\uffff\u2bb0\43\u2150\uffff"+
            "\u0200\43",
            "\1\52\5\uffff\12\136\5\uffff\1\53\1\uffff\6\136\24\43\1\uffff"+
            "\1\41\4\uffff\6\136\24\43\105\uffff\27\43\1\uffff\37\43\1\uffff"+
            "\u1f08\43\u1040\uffff\u0150\43\u0170\uffff\u0080\43\u0080\uffff"+
            "\u092e\43\u10d2\uffff\u5200\43\u0c00\uffff\u2bb0\43\u2150\uffff"+
            "\u0200\43",
            "\1\52\5\uffff\12\141\5\uffff\1\53\1\uffff\6\140\24\55\1\uffff"+
            "\1\50\4\uffff\6\137\24\51\105\uffff\27\43\1\uffff\37\43\1\uffff"+
            "\u1f08\43\u1040\uffff\u0150\43\u0170\uffff\u0080\43\u0080\uffff"+
            "\u092e\43\u10d2\uffff\u5200\43\u0c00\uffff\u2bb0\43\u2150\uffff"+
            "\u0200\43",
            "\1\52\5\uffff\12\141\5\uffff\1\53\1\uffff\6\140\24\55\1\uffff"+
            "\1\50\4\uffff\6\137\24\51\105\uffff\27\43\1\uffff\37\43\1\uffff"+
            "\u1f08\43\u1040\uffff\u0150\43\u0170\uffff\u0080\43\u0080\uffff"+
            "\u092e\43\u10d2\uffff\u5200\43\u0c00\uffff\u2bb0\43\u2150\uffff"+
            "\u0200\43",
            "\1\52\5\uffff\12\141\5\uffff\1\53\1\uffff\6\140\24\55\1\uffff"+
            "\1\50\4\uffff\6\137\24\51\105\uffff\27\43\1\uffff\37\43\1\uffff"+
            "\u1f08\43\u1040\uffff\u0150\43\u0170\uffff\u0080\43\u0080\uffff"+
            "\u092e\43\u10d2\uffff\u5200\43\u0c00\uffff\u2bb0\43\u2150\uffff"+
            "\u0200\43",
            "\1\52\5\uffff\12\43\5\uffff\1\53\1\uffff\32\43\1\uffff\1\41"+
            "\4\uffff\32\43\105\uffff\27\43\1\uffff\37\43\1\uffff\u1f08\43"+
            "\u1040\uffff\u0150\43\u0170\uffff\u0080\43\u0080\uffff\u092e"+
            "\43\u10d2\uffff\u5200\43\u0c00\uffff\u2bb0\43\u2150\uffff\u0200"+
            "\43",
            "\1\52\5\uffff\12\56\5\uffff\1\53\1\uffff\32\55\1\uffff\1\50"+
            "\4\uffff\32\51\105\uffff\27\43\1\uffff\37\43\1\uffff\u1f08\43"+
            "\u1040\uffff\u0150\43\u0170\uffff\u0080\43\u0080\uffff\u092e"+
            "\43\u10d2\uffff\u5200\43\u0c00\uffff\u2bb0\43\u2150\uffff\u0200"+
            "\43",
            "\1\52\5\uffff\12\56\5\uffff\1\53\1\uffff\32\55\1\uffff\1\50"+
            "\4\uffff\32\51\105\uffff\27\43\1\uffff\37\43\1\uffff\u1f08\43"+
            "\u1040\uffff\u0150\43\u0170\uffff\u0080\43\u0080\uffff\u092e"+
            "\43\u10d2\uffff\u5200\43\u0c00\uffff\u2bb0\43\u2150\uffff\u0200"+
            "\43",
            "\1\52\5\uffff\12\56\5\uffff\1\53\1\uffff\32\55\1\uffff\1\50"+
            "\4\uffff\32\51\105\uffff\27\43\1\uffff\37\43\1\uffff\u1f08\43"+
            "\u1040\uffff\u0150\43\u0170\uffff\u0080\43\u0080\uffff\u092e"+
            "\43\u10d2\uffff\u5200\43\u0c00\uffff\u2bb0\43\u2150\uffff\u0200"+
            "\43"
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
                        int LA38_40 = input.LA(1);

                        s = -1;
                        if ( (LA38_40=='u') ) {s = 70;}

                        else if ( ((LA38_40>='\u0000' && LA38_40<='t')||(LA38_40>='v' && LA38_40<='\uFFFF')) ) {s = 71;}

                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA38_33 = input.LA(1);

                        s = -1;
                        if ( (LA38_33=='u') ) {s = 66;}

                        else if ( ((LA38_33>='\u0000' && LA38_33<='t')||(LA38_33>='v' && LA38_33<='\uFFFF')) ) {s = 67;}

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