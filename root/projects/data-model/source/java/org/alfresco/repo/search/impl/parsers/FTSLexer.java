// $ANTLR 3.4 W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g 2013-08-07 19:43:03

package org.alfresco.repo.search.impl.parsers;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

@SuppressWarnings({"all", "warnings", "unchecked"})
public class FTSLexer extends Lexer {
    public static final int EOF=-1;
    public static final int AMP=4;
    public static final int AND=5;
    public static final int AT=6;
    public static final int BAR=7;
    public static final int BOOST=8;
    public static final int CARAT=9;
    public static final int COLON=10;
    public static final int COMMA=11;
    public static final int CONJUNCTION=12;
    public static final int DECIMAL_INTEGER_LITERAL=13;
    public static final int DECIMAL_NUMERAL=14;
    public static final int DEFAULT=15;
    public static final int DIGIT=16;
    public static final int DISJUNCTION=17;
    public static final int DOLLAR=18;
    public static final int DOT=19;
    public static final int DOTDOT=20;
    public static final int E=21;
    public static final int EQUALS=22;
    public static final int EXACT_PHRASE=23;
    public static final int EXACT_TERM=24;
    public static final int EXCLAMATION=25;
    public static final int EXCLUDE=26;
    public static final int EXCLUSIVE=27;
    public static final int EXPONENT=28;
    public static final int FG_EXACT_PHRASE=29;
    public static final int FG_EXACT_TERM=30;
    public static final int FG_PHRASE=31;
    public static final int FG_PROXIMITY=32;
    public static final int FG_RANGE=33;
    public static final int FG_SYNONYM=34;
    public static final int FG_TERM=35;
    public static final int FIELD_CONJUNCTION=36;
    public static final int FIELD_DEFAULT=37;
    public static final int FIELD_DISJUNCTION=38;
    public static final int FIELD_EXCLUDE=39;
    public static final int FIELD_GROUP=40;
    public static final int FIELD_MANDATORY=41;
    public static final int FIELD_NEGATION=42;
    public static final int FIELD_OPTIONAL=43;
    public static final int FIELD_REF=44;
    public static final int FLOATING_POINT_LITERAL=45;
    public static final int FTS=46;
    public static final int FTSPHRASE=47;
    public static final int FTSPRE=48;
    public static final int FTSWILD=49;
    public static final int FTSWORD=50;
    public static final int FUZZY=51;
    public static final int F_ESC=52;
    public static final int F_HEX=53;
    public static final int F_URI_ALPHA=54;
    public static final int F_URI_DIGIT=55;
    public static final int F_URI_ESC=56;
    public static final int F_URI_OTHER=57;
    public static final int GT=58;
    public static final int ID=59;
    public static final int INCLUSIVE=60;
    public static final int IN_WORD=61;
    public static final int LCURL=62;
    public static final int LPAREN=63;
    public static final int LSQUARE=64;
    public static final int LT=65;
    public static final int MANDATORY=66;
    public static final int MINUS=67;
    public static final int NAME_SPACE=68;
    public static final int NEGATION=69;
    public static final int NON_ZERO_DIGIT=70;
    public static final int NOT=71;
    public static final int OPTIONAL=72;
    public static final int OR=73;
    public static final int PERCENT=74;
    public static final int PHRASE=75;
    public static final int PLUS=76;
    public static final int PREFIX=77;
    public static final int PROXIMITY=78;
    public static final int QUALIFIER=79;
    public static final int QUESTION_MARK=80;
    public static final int RANGE=81;
    public static final int RCURL=82;
    public static final int RPAREN=83;
    public static final int RSQUARE=84;
    public static final int SIGNED_INTEGER=85;
    public static final int STAR=86;
    public static final int START_WORD=87;
    public static final int SYNONYM=88;
    public static final int TEMPLATE=89;
    public static final int TERM=90;
    public static final int TILDA=91;
    public static final int TO=92;
    public static final int URI=93;
    public static final int WS=94;
    public static final int ZERO_DIGIT=95;

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
    public Lexer[] getDelegates() {
        return new Lexer[] {};
    }

    public FTSLexer() {} 
    public FTSLexer(CharStream input) {
        this(input, new RecognizerSharedState());
    }
    public FTSLexer(CharStream input, RecognizerSharedState state) {
        super(input,state);
    }
    public String getGrammarFileName() { return "W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g"; }

    // $ANTLR start "FTSPHRASE"
    public final void mFTSPHRASE() throws RecognitionException {
        try {
            int _type = FTSPHRASE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:934:9: ( '\"' ( F_ESC |~ ( '\\\\' | '\"' ) )* '\"' | '\\'' ( F_ESC |~ ( '\\\\' | '\\'' ) )* '\\'' )
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
                    // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:935:9: '\"' ( F_ESC |~ ( '\\\\' | '\"' ) )* '\"'
                    {
                    match('\"'); if (state.failed) return ;

                    // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:936:9: ( F_ESC |~ ( '\\\\' | '\"' ) )*
                    loop1:
                    do {
                        int alt1=3;
                        int LA1_0 = input.LA(1);

                        if ( (LA1_0=='\\') ) {
                            alt1=1;
                        }
                        else if ( ((LA1_0 >= '\u0000' && LA1_0 <= '!')||(LA1_0 >= '#' && LA1_0 <= '[')||(LA1_0 >= ']' && LA1_0 <= '\uFFFF')) ) {
                            alt1=2;
                        }


                        switch (alt1) {
                    	case 1 :
                    	    // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:937:17: F_ESC
                    	    {
                    	    mF_ESC(); if (state.failed) return ;


                    	    }
                    	    break;
                    	case 2 :
                    	    // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:939:17: ~ ( '\\\\' | '\"' )
                    	    {
                    	    if ( (input.LA(1) >= '\u0000' && input.LA(1) <= '!')||(input.LA(1) >= '#' && input.LA(1) <= '[')||(input.LA(1) >= ']' && input.LA(1) <= '\uFFFF') ) {
                    	        input.consume();
                    	        state.failed=false;
                    	    }
                    	    else {
                    	        if (state.backtracking>0) {state.failed=true; return ;}
                    	        MismatchedSetException mse = new MismatchedSetException(null,input);
                    	        recover(mse);
                    	        throw mse;
                    	    }


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
                    // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:945:11: '\\'' ( F_ESC |~ ( '\\\\' | '\\'' ) )* '\\''
                    {
                    match('\''); if (state.failed) return ;

                    // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:946:9: ( F_ESC |~ ( '\\\\' | '\\'' ) )*
                    loop2:
                    do {
                        int alt2=3;
                        int LA2_0 = input.LA(1);

                        if ( (LA2_0=='\\') ) {
                            alt2=1;
                        }
                        else if ( ((LA2_0 >= '\u0000' && LA2_0 <= '&')||(LA2_0 >= '(' && LA2_0 <= '[')||(LA2_0 >= ']' && LA2_0 <= '\uFFFF')) ) {
                            alt2=2;
                        }


                        switch (alt2) {
                    	case 1 :
                    	    // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:947:17: F_ESC
                    	    {
                    	    mF_ESC(); if (state.failed) return ;


                    	    }
                    	    break;
                    	case 2 :
                    	    // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:949:17: ~ ( '\\\\' | '\\'' )
                    	    {
                    	    if ( (input.LA(1) >= '\u0000' && input.LA(1) <= '&')||(input.LA(1) >= '(' && input.LA(1) <= '[')||(input.LA(1) >= ']' && input.LA(1) <= '\uFFFF') ) {
                    	        input.consume();
                    	        state.failed=false;
                    	    }
                    	    else {
                    	        if (state.backtracking>0) {state.failed=true; return ;}
                    	        MismatchedSetException mse = new MismatchedSetException(null,input);
                    	        recover(mse);
                    	        throw mse;
                    	    }


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
        	// do for sure before leaving
        }
    }
    // $ANTLR end "FTSPHRASE"

    // $ANTLR start "URI"
    public final void mURI() throws RecognitionException {
        try {
            int _type = URI;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:963:9: ( '{' ( ( F_URI_ALPHA | F_URI_DIGIT | F_URI_OTHER )=> ( F_URI_ALPHA | F_URI_DIGIT | F_URI_OTHER )+ COLON )? ( ( ( '//' )=> '//' ) ( ( F_URI_ALPHA | F_URI_DIGIT | F_URI_OTHER | COLON )=> ( F_URI_ALPHA | F_URI_DIGIT | F_URI_OTHER | COLON ) )* )? ( F_URI_ALPHA | F_URI_DIGIT | F_URI_OTHER | COLON | '/' )* ( '?' ( F_URI_ALPHA | F_URI_DIGIT | F_URI_OTHER | COLON | '/' | '?' )* )? ( '#' ( F_URI_ALPHA | F_URI_DIGIT | F_URI_OTHER | COLON | '/' | '?' | '#' )* )? '}' )
            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:964:9: '{' ( ( F_URI_ALPHA | F_URI_DIGIT | F_URI_OTHER )=> ( F_URI_ALPHA | F_URI_DIGIT | F_URI_OTHER )+ COLON )? ( ( ( '//' )=> '//' ) ( ( F_URI_ALPHA | F_URI_DIGIT | F_URI_OTHER | COLON )=> ( F_URI_ALPHA | F_URI_DIGIT | F_URI_OTHER | COLON ) )* )? ( F_URI_ALPHA | F_URI_DIGIT | F_URI_OTHER | COLON | '/' )* ( '?' ( F_URI_ALPHA | F_URI_DIGIT | F_URI_OTHER | COLON | '/' | '?' )* )? ( '#' ( F_URI_ALPHA | F_URI_DIGIT | F_URI_OTHER | COLON | '/' | '?' | '#' )* )? '}'
            {
            match('{'); if (state.failed) return ;

            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:965:9: ( ( F_URI_ALPHA | F_URI_DIGIT | F_URI_OTHER )=> ( F_URI_ALPHA | F_URI_DIGIT | F_URI_OTHER )+ COLON )?
            int alt5=2;
            alt5 = dfa5.predict(input);
            switch (alt5) {
                case 1 :
                    // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:966:17: ( F_URI_ALPHA | F_URI_DIGIT | F_URI_OTHER )=> ( F_URI_ALPHA | F_URI_DIGIT | F_URI_OTHER )+ COLON
                    {
                    // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:972:17: ( F_URI_ALPHA | F_URI_DIGIT | F_URI_OTHER )+
                    int cnt4=0;
                    loop4:
                    do {
                        int alt4=2;
                        int LA4_0 = input.LA(1);

                        if ( (LA4_0=='!'||LA4_0=='$'||(LA4_0 >= '&' && LA4_0 <= '.')||(LA4_0 >= '0' && LA4_0 <= '9')||LA4_0==';'||LA4_0=='='||(LA4_0 >= '@' && LA4_0 <= '[')||LA4_0==']'||LA4_0=='_'||(LA4_0 >= 'a' && LA4_0 <= 'z')||LA4_0=='~') ) {
                            alt4=1;
                        }


                        switch (alt4) {
                    	case 1 :
                    	    // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:
                    	    {
                    	    if ( input.LA(1)=='!'||input.LA(1)=='$'||(input.LA(1) >= '&' && input.LA(1) <= '.')||(input.LA(1) >= '0' && input.LA(1) <= '9')||input.LA(1)==';'||input.LA(1)=='='||(input.LA(1) >= '@' && input.LA(1) <= '[')||input.LA(1)==']'||input.LA(1)=='_'||(input.LA(1) >= 'a' && input.LA(1) <= 'z')||input.LA(1)=='~' ) {
                    	        input.consume();
                    	        state.failed=false;
                    	    }
                    	    else {
                    	        if (state.backtracking>0) {state.failed=true; return ;}
                    	        MismatchedSetException mse = new MismatchedSetException(null,input);
                    	        recover(mse);
                    	        throw mse;
                    	    }


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


            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:979:9: ( ( ( '//' )=> '//' ) ( ( F_URI_ALPHA | F_URI_DIGIT | F_URI_OTHER | COLON )=> ( F_URI_ALPHA | F_URI_DIGIT | F_URI_OTHER | COLON ) )* )?
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
                    // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:980:17: ( ( '//' )=> '//' ) ( ( F_URI_ALPHA | F_URI_DIGIT | F_URI_OTHER | COLON )=> ( F_URI_ALPHA | F_URI_DIGIT | F_URI_OTHER | COLON ) )*
                    {
                    // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:980:17: ( ( '//' )=> '//' )
                    // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:980:19: ( '//' )=> '//'
                    {
                    match("//"); if (state.failed) return ;



                    }


                    // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:981:17: ( ( F_URI_ALPHA | F_URI_DIGIT | F_URI_OTHER | COLON )=> ( F_URI_ALPHA | F_URI_DIGIT | F_URI_OTHER | COLON ) )*
                    loop6:
                    do {
                        int alt6=2;
                        int LA6_0 = input.LA(1);

                        if ( (LA6_0=='!'||LA6_0=='$'||(LA6_0 >= '&' && LA6_0 <= '.')||(LA6_0 >= '0' && LA6_0 <= ';')||LA6_0=='='||(LA6_0 >= '@' && LA6_0 <= '[')||LA6_0==']'||LA6_0=='_'||(LA6_0 >= 'a' && LA6_0 <= 'z')||LA6_0=='~') ) {
                            int LA6_1 = input.LA(2);

                            if ( (synpred3_FTS()) ) {
                                alt6=1;
                            }


                        }


                        switch (alt6) {
                    	case 1 :
                    	    // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:982:25: ( F_URI_ALPHA | F_URI_DIGIT | F_URI_OTHER | COLON )=> ( F_URI_ALPHA | F_URI_DIGIT | F_URI_OTHER | COLON )
                    	    {
                    	    if ( input.LA(1)=='!'||input.LA(1)=='$'||(input.LA(1) >= '&' && input.LA(1) <= '.')||(input.LA(1) >= '0' && input.LA(1) <= ';')||input.LA(1)=='='||(input.LA(1) >= '@' && input.LA(1) <= '[')||input.LA(1)==']'||input.LA(1)=='_'||(input.LA(1) >= 'a' && input.LA(1) <= 'z')||input.LA(1)=='~' ) {
                    	        input.consume();
                    	        state.failed=false;
                    	    }
                    	    else {
                    	        if (state.backtracking>0) {state.failed=true; return ;}
                    	        MismatchedSetException mse = new MismatchedSetException(null,input);
                    	        recover(mse);
                    	        throw mse;
                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop6;
                        }
                    } while (true);


                    }
                    break;

            }


            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:997:9: ( F_URI_ALPHA | F_URI_DIGIT | F_URI_OTHER | COLON | '/' )*
            loop8:
            do {
                int alt8=2;
                int LA8_0 = input.LA(1);

                if ( (LA8_0=='!'||LA8_0=='$'||(LA8_0 >= '&' && LA8_0 <= ';')||LA8_0=='='||(LA8_0 >= '@' && LA8_0 <= '[')||LA8_0==']'||LA8_0=='_'||(LA8_0 >= 'a' && LA8_0 <= 'z')||LA8_0=='~') ) {
                    alt8=1;
                }


                switch (alt8) {
            	case 1 :
            	    // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:
            	    {
            	    if ( input.LA(1)=='!'||input.LA(1)=='$'||(input.LA(1) >= '&' && input.LA(1) <= ';')||input.LA(1)=='='||(input.LA(1) >= '@' && input.LA(1) <= '[')||input.LA(1)==']'||input.LA(1)=='_'||(input.LA(1) >= 'a' && input.LA(1) <= 'z')||input.LA(1)=='~' ) {
            	        input.consume();
            	        state.failed=false;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return ;}
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    break loop8;
                }
            } while (true);


            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1004:9: ( '?' ( F_URI_ALPHA | F_URI_DIGIT | F_URI_OTHER | COLON | '/' | '?' )* )?
            int alt10=2;
            int LA10_0 = input.LA(1);

            if ( (LA10_0=='?') ) {
                alt10=1;
            }
            switch (alt10) {
                case 1 :
                    // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1005:17: '?' ( F_URI_ALPHA | F_URI_DIGIT | F_URI_OTHER | COLON | '/' | '?' )*
                    {
                    match('?'); if (state.failed) return ;

                    // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1006:17: ( F_URI_ALPHA | F_URI_DIGIT | F_URI_OTHER | COLON | '/' | '?' )*
                    loop9:
                    do {
                        int alt9=2;
                        int LA9_0 = input.LA(1);

                        if ( (LA9_0=='!'||LA9_0=='$'||(LA9_0 >= '&' && LA9_0 <= ';')||LA9_0=='='||(LA9_0 >= '?' && LA9_0 <= '[')||LA9_0==']'||LA9_0=='_'||(LA9_0 >= 'a' && LA9_0 <= 'z')||LA9_0=='~') ) {
                            alt9=1;
                        }


                        switch (alt9) {
                    	case 1 :
                    	    // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:
                    	    {
                    	    if ( input.LA(1)=='!'||input.LA(1)=='$'||(input.LA(1) >= '&' && input.LA(1) <= ';')||input.LA(1)=='='||(input.LA(1) >= '?' && input.LA(1) <= '[')||input.LA(1)==']'||input.LA(1)=='_'||(input.LA(1) >= 'a' && input.LA(1) <= 'z')||input.LA(1)=='~' ) {
                    	        input.consume();
                    	        state.failed=false;
                    	    }
                    	    else {
                    	        if (state.backtracking>0) {state.failed=true; return ;}
                    	        MismatchedSetException mse = new MismatchedSetException(null,input);
                    	        recover(mse);
                    	        throw mse;
                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop9;
                        }
                    } while (true);


                    }
                    break;

            }


            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1015:9: ( '#' ( F_URI_ALPHA | F_URI_DIGIT | F_URI_OTHER | COLON | '/' | '?' | '#' )* )?
            int alt12=2;
            int LA12_0 = input.LA(1);

            if ( (LA12_0=='#') ) {
                alt12=1;
            }
            switch (alt12) {
                case 1 :
                    // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1016:17: '#' ( F_URI_ALPHA | F_URI_DIGIT | F_URI_OTHER | COLON | '/' | '?' | '#' )*
                    {
                    match('#'); if (state.failed) return ;

                    // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1017:17: ( F_URI_ALPHA | F_URI_DIGIT | F_URI_OTHER | COLON | '/' | '?' | '#' )*
                    loop11:
                    do {
                        int alt11=2;
                        int LA11_0 = input.LA(1);

                        if ( (LA11_0=='!'||(LA11_0 >= '#' && LA11_0 <= '$')||(LA11_0 >= '&' && LA11_0 <= ';')||LA11_0=='='||(LA11_0 >= '?' && LA11_0 <= '[')||LA11_0==']'||LA11_0=='_'||(LA11_0 >= 'a' && LA11_0 <= 'z')||LA11_0=='~') ) {
                            alt11=1;
                        }


                        switch (alt11) {
                    	case 1 :
                    	    // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:
                    	    {
                    	    if ( input.LA(1)=='!'||(input.LA(1) >= '#' && input.LA(1) <= '$')||(input.LA(1) >= '&' && input.LA(1) <= ';')||input.LA(1)=='='||(input.LA(1) >= '?' && input.LA(1) <= '[')||input.LA(1)==']'||input.LA(1)=='_'||(input.LA(1) >= 'a' && input.LA(1) <= 'z')||input.LA(1)=='~' ) {
                    	        input.consume();
                    	        state.failed=false;
                    	    }
                    	    else {
                    	        if (state.backtracking>0) {state.failed=true; return ;}
                    	        MismatchedSetException mse = new MismatchedSetException(null,input);
                    	        recover(mse);
                    	        throw mse;
                    	    }


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
        	// do for sure before leaving
        }
    }
    // $ANTLR end "URI"

    // $ANTLR start "F_URI_ALPHA"
    public final void mF_URI_ALPHA() throws RecognitionException {
        try {
            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1033:9: ( 'A' .. 'Z' | 'a' .. 'z' )
            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:
            {
            if ( (input.LA(1) >= 'A' && input.LA(1) <= 'Z')||(input.LA(1) >= 'a' && input.LA(1) <= 'z') ) {
                input.consume();
                state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "F_URI_ALPHA"

    // $ANTLR start "F_URI_DIGIT"
    public final void mF_URI_DIGIT() throws RecognitionException {
        try {
            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1040:9: ( '0' .. '9' )
            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:
            {
            if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
                input.consume();
                state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "F_URI_DIGIT"

    // $ANTLR start "F_URI_ESC"
    public final void mF_URI_ESC() throws RecognitionException {
        try {
            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1046:9: ( '%' F_HEX F_HEX )
            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1047:9: '%' F_HEX F_HEX
            {
            match('%'); if (state.failed) return ;

            mF_HEX(); if (state.failed) return ;


            mF_HEX(); if (state.failed) return ;


            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "F_URI_ESC"

    // $ANTLR start "F_URI_OTHER"
    public final void mF_URI_OTHER() throws RecognitionException {
        try {
            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1052:9: ( '-' | '.' | '_' | '~' | '[' | ']' | '@' | '!' | '$' | '&' | '\\'' | '(' | ')' | '*' | '+' | ',' | ';' | '=' )
            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:
            {
            if ( input.LA(1)=='!'||input.LA(1)=='$'||(input.LA(1) >= '&' && input.LA(1) <= '.')||input.LA(1)==';'||input.LA(1)=='='||input.LA(1)=='@'||input.LA(1)=='['||input.LA(1)==']'||input.LA(1)=='_'||input.LA(1)=='~' ) {
                input.consume();
                state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "F_URI_OTHER"

    // $ANTLR start "OR"
    public final void mOR() throws RecognitionException {
        try {
            int _type = OR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1077:9: ( ( 'O' | 'o' ) ( 'R' | 'r' ) )
            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1078:9: ( 'O' | 'o' ) ( 'R' | 'r' )
            {
            if ( input.LA(1)=='O'||input.LA(1)=='o' ) {
                input.consume();
                state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            if ( input.LA(1)=='R'||input.LA(1)=='r' ) {
                input.consume();
                state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "OR"

    // $ANTLR start "AND"
    public final void mAND() throws RecognitionException {
        try {
            int _type = AND;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1089:9: ( ( 'A' | 'a' ) ( 'N' | 'n' ) ( 'D' | 'd' ) )
            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1090:9: ( 'A' | 'a' ) ( 'N' | 'n' ) ( 'D' | 'd' )
            {
            if ( input.LA(1)=='A'||input.LA(1)=='a' ) {
                input.consume();
                state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            if ( input.LA(1)=='N'||input.LA(1)=='n' ) {
                input.consume();
                state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            if ( input.LA(1)=='D'||input.LA(1)=='d' ) {
                input.consume();
                state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "AND"

    // $ANTLR start "NOT"
    public final void mNOT() throws RecognitionException {
        try {
            int _type = NOT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1105:9: ( ( 'N' | 'n' ) ( 'O' | 'o' ) ( 'T' | 't' ) )
            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1106:9: ( 'N' | 'n' ) ( 'O' | 'o' ) ( 'T' | 't' )
            {
            if ( input.LA(1)=='N'||input.LA(1)=='n' ) {
                input.consume();
                state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            if ( input.LA(1)=='O'||input.LA(1)=='o' ) {
                input.consume();
                state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            if ( input.LA(1)=='T'||input.LA(1)=='t' ) {
                input.consume();
                state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "NOT"

    // $ANTLR start "TILDA"
    public final void mTILDA() throws RecognitionException {
        try {
            int _type = TILDA;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1121:9: ( '~' )
            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1122:9: '~'
            {
            match('~'); if (state.failed) return ;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "TILDA"

    // $ANTLR start "LPAREN"
    public final void mLPAREN() throws RecognitionException {
        try {
            int _type = LPAREN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1126:9: ( '(' )
            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1127:9: '('
            {
            match('('); if (state.failed) return ;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "LPAREN"

    // $ANTLR start "RPAREN"
    public final void mRPAREN() throws RecognitionException {
        try {
            int _type = RPAREN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1131:9: ( ')' )
            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1132:9: ')'
            {
            match(')'); if (state.failed) return ;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "RPAREN"

    // $ANTLR start "PLUS"
    public final void mPLUS() throws RecognitionException {
        try {
            int _type = PLUS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1136:9: ( '+' )
            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1137:9: '+'
            {
            match('+'); if (state.failed) return ;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "PLUS"

    // $ANTLR start "MINUS"
    public final void mMINUS() throws RecognitionException {
        try {
            int _type = MINUS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1141:9: ( '-' )
            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1142:9: '-'
            {
            match('-'); if (state.failed) return ;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "MINUS"

    // $ANTLR start "COLON"
    public final void mCOLON() throws RecognitionException {
        try {
            int _type = COLON;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1146:9: ( ':' )
            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1147:9: ':'
            {
            match(':'); if (state.failed) return ;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "COLON"

    // $ANTLR start "STAR"
    public final void mSTAR() throws RecognitionException {
        try {
            int _type = STAR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1151:9: ( '*' )
            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1152:9: '*'
            {
            match('*'); if (state.failed) return ;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "STAR"

    // $ANTLR start "DOTDOT"
    public final void mDOTDOT() throws RecognitionException {
        try {
            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1157:9: ( '..' )
            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1158:9: '..'
            {
            match(".."); if (state.failed) return ;



            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "DOTDOT"

    // $ANTLR start "DOT"
    public final void mDOT() throws RecognitionException {
        try {
            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1163:9: ( '.' )
            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1164:9: '.'
            {
            match('.'); if (state.failed) return ;

            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "DOT"

    // $ANTLR start "AMP"
    public final void mAMP() throws RecognitionException {
        try {
            int _type = AMP;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1168:9: ( '&' )
            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1169:9: '&'
            {
            match('&'); if (state.failed) return ;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "AMP"

    // $ANTLR start "EXCLAMATION"
    public final void mEXCLAMATION() throws RecognitionException {
        try {
            int _type = EXCLAMATION;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1173:9: ( '!' )
            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1174:9: '!'
            {
            match('!'); if (state.failed) return ;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "EXCLAMATION"

    // $ANTLR start "BAR"
    public final void mBAR() throws RecognitionException {
        try {
            int _type = BAR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1178:9: ( '|' )
            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1179:9: '|'
            {
            match('|'); if (state.failed) return ;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "BAR"

    // $ANTLR start "EQUALS"
    public final void mEQUALS() throws RecognitionException {
        try {
            int _type = EQUALS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1183:9: ( '=' )
            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1184:9: '='
            {
            match('='); if (state.failed) return ;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "EQUALS"

    // $ANTLR start "QUESTION_MARK"
    public final void mQUESTION_MARK() throws RecognitionException {
        try {
            int _type = QUESTION_MARK;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1188:9: ( '?' )
            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1189:9: '?'
            {
            match('?'); if (state.failed) return ;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "QUESTION_MARK"

    // $ANTLR start "LCURL"
    public final void mLCURL() throws RecognitionException {
        try {
            int _type = LCURL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1193:9: ( '{' )
            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1194:9: '{'
            {
            match('{'); if (state.failed) return ;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "LCURL"

    // $ANTLR start "RCURL"
    public final void mRCURL() throws RecognitionException {
        try {
            int _type = RCURL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1198:9: ( '}' )
            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1199:9: '}'
            {
            match('}'); if (state.failed) return ;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "RCURL"

    // $ANTLR start "LSQUARE"
    public final void mLSQUARE() throws RecognitionException {
        try {
            int _type = LSQUARE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1203:9: ( '[' )
            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1204:9: '['
            {
            match('['); if (state.failed) return ;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "LSQUARE"

    // $ANTLR start "RSQUARE"
    public final void mRSQUARE() throws RecognitionException {
        try {
            int _type = RSQUARE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1208:9: ( ']' )
            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1209:9: ']'
            {
            match(']'); if (state.failed) return ;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "RSQUARE"

    // $ANTLR start "TO"
    public final void mTO() throws RecognitionException {
        try {
            int _type = TO;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1213:9: ( ( 'T' | 't' ) ( 'O' | 'o' ) )
            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1214:9: ( 'T' | 't' ) ( 'O' | 'o' )
            {
            if ( input.LA(1)=='T'||input.LA(1)=='t' ) {
                input.consume();
                state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            if ( input.LA(1)=='O'||input.LA(1)=='o' ) {
                input.consume();
                state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "TO"

    // $ANTLR start "COMMA"
    public final void mCOMMA() throws RecognitionException {
        try {
            int _type = COMMA;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1225:9: ( ',' )
            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1226:9: ','
            {
            match(','); if (state.failed) return ;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "COMMA"

    // $ANTLR start "CARAT"
    public final void mCARAT() throws RecognitionException {
        try {
            int _type = CARAT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1230:9: ( '^' )
            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1231:9: '^'
            {
            match('^'); if (state.failed) return ;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "CARAT"

    // $ANTLR start "DOLLAR"
    public final void mDOLLAR() throws RecognitionException {
        try {
            int _type = DOLLAR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1235:9: ( '$' )
            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1236:9: '$'
            {
            match('$'); if (state.failed) return ;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "DOLLAR"

    // $ANTLR start "GT"
    public final void mGT() throws RecognitionException {
        try {
            int _type = GT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1240:9: ( '>' )
            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1241:9: '>'
            {
            match('>'); if (state.failed) return ;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "GT"

    // $ANTLR start "LT"
    public final void mLT() throws RecognitionException {
        try {
            int _type = LT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1245:9: ( '<' )
            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1246:9: '<'
            {
            match('<'); if (state.failed) return ;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "LT"

    // $ANTLR start "AT"
    public final void mAT() throws RecognitionException {
        try {
            int _type = AT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1250:9: ( '@' )
            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1251:9: '@'
            {
            match('@'); if (state.failed) return ;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "AT"

    // $ANTLR start "PERCENT"
    public final void mPERCENT() throws RecognitionException {
        try {
            int _type = PERCENT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1255:9: ( '%' )
            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1256:9: '%'
            {
            match('%'); if (state.failed) return ;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "PERCENT"

    // $ANTLR start "ID"
    public final void mID() throws RecognitionException {
        try {
            int _type = ID;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1270:9: ( ( 'a' .. 'z' | 'A' .. 'Z' | '_' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' | '$' | '#' | F_ESC )* )
            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1271:9: ( 'a' .. 'z' | 'A' .. 'Z' | '_' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' | '$' | '#' | F_ESC )*
            {
            if ( (input.LA(1) >= 'A' && input.LA(1) <= 'Z')||input.LA(1)=='_'||(input.LA(1) >= 'a' && input.LA(1) <= 'z') ) {
                input.consume();
                state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1276:9: ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' | '$' | '#' | F_ESC )*
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
            	    // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1277:17: 'a' .. 'z'
            	    {
            	    matchRange('a','z'); if (state.failed) return ;

            	    }
            	    break;
            	case 2 :
            	    // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1278:19: 'A' .. 'Z'
            	    {
            	    matchRange('A','Z'); if (state.failed) return ;

            	    }
            	    break;
            	case 3 :
            	    // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1279:19: '0' .. '9'
            	    {
            	    matchRange('0','9'); if (state.failed) return ;

            	    }
            	    break;
            	case 4 :
            	    // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1280:19: '_'
            	    {
            	    match('_'); if (state.failed) return ;

            	    }
            	    break;
            	case 5 :
            	    // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1281:19: '$'
            	    {
            	    match('$'); if (state.failed) return ;

            	    }
            	    break;
            	case 6 :
            	    // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1282:19: '#'
            	    {
            	    match('#'); if (state.failed) return ;

            	    }
            	    break;
            	case 7 :
            	    // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1283:19: F_ESC
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
        	// do for sure before leaving
        }
    }
    // $ANTLR end "ID"

    // $ANTLR start "DECIMAL_INTEGER_LITERAL"
    public final void mDECIMAL_INTEGER_LITERAL() throws RecognitionException {
        try {
            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1284:9: ()
            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1285:9: 
            {
            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "DECIMAL_INTEGER_LITERAL"

    // $ANTLR start "FLOATING_POINT_LITERAL"
    public final void mFLOATING_POINT_LITERAL() throws RecognitionException {
        try {
            int _type = FLOATING_POINT_LITERAL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1288:9: ( ( PLUS | MINUS )? ( ( DIGIT )+ ({...}? => DOT ( ( DIGIT )+ ( EXPONENT |{...}? => DOT |) | EXPONENT |) | ( EXPONENT |) ) | DOT ( ( DIGIT )+ ( EXPONENT |{...}?{...}? => DOT |) |{...}? => '.' |) ) )
            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1289:10: ( PLUS | MINUS )? ( ( DIGIT )+ ({...}? => DOT ( ( DIGIT )+ ( EXPONENT |{...}? => DOT |) | EXPONENT |) | ( EXPONENT |) ) | DOT ( ( DIGIT )+ ( EXPONENT |{...}?{...}? => DOT |) |{...}? => '.' |) )
            {
            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1289:10: ( PLUS | MINUS )?
            int alt14=2;
            int LA14_0 = input.LA(1);

            if ( (LA14_0=='+'||LA14_0=='-') ) {
                alt14=1;
            }
            switch (alt14) {
                case 1 :
                    // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:
                    {
                    if ( input.LA(1)=='+'||input.LA(1)=='-' ) {
                        input.consume();
                        state.failed=false;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return ;}
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        recover(mse);
                        throw mse;
                    }


                    }
                    break;

            }


            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1290:10: ( ( DIGIT )+ ({...}? => DOT ( ( DIGIT )+ ( EXPONENT |{...}? => DOT |) | EXPONENT |) | ( EXPONENT |) ) | DOT ( ( DIGIT )+ ( EXPONENT |{...}?{...}? => DOT |) |{...}? => '.' |) )
            int alt24=2;
            int LA24_0 = input.LA(1);

            if ( ((LA24_0 >= '0' && LA24_0 <= '9')) ) {
                alt24=1;
            }
            else if ( (LA24_0=='.') ) {
                alt24=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 24, 0, input);

                throw nvae;

            }
            switch (alt24) {
                case 1 :
                    // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1291:17: ( DIGIT )+ ({...}? => DOT ( ( DIGIT )+ ( EXPONENT |{...}? => DOT |) | EXPONENT |) | ( EXPONENT |) )
                    {
                    // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1291:17: ( DIGIT )+
                    int cnt15=0;
                    loop15:
                    do {
                        int alt15=2;
                        int LA15_0 = input.LA(1);

                        if ( ((LA15_0 >= '0' && LA15_0 <= '9')) ) {
                            alt15=1;
                        }


                        switch (alt15) {
                    	case 1 :
                    	    // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:
                    	    {
                    	    if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
                    	        input.consume();
                    	        state.failed=false;
                    	    }
                    	    else {
                    	        if (state.backtracking>0) {state.failed=true; return ;}
                    	        MismatchedSetException mse = new MismatchedSetException(null,input);
                    	        recover(mse);
                    	        throw mse;
                    	    }


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


                    // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1292:17: ({...}? => DOT ( ( DIGIT )+ ( EXPONENT |{...}? => DOT |) | EXPONENT |) | ( EXPONENT |) )
                    int alt20=2;
                    int LA20_0 = input.LA(1);

                    if ( (LA20_0=='.') && ((input.LA(2) != '.'))) {
                        alt20=1;
                    }
                    else if ( (LA20_0=='E'||LA20_0=='e') ) {
                        alt20=2;
                    }
                    else {
                        alt20=2;
                    }
                    switch (alt20) {
                        case 1 :
                            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1293:25: {...}? => DOT ( ( DIGIT )+ ( EXPONENT |{...}? => DOT |) | EXPONENT |)
                            {
                            if ( !((input.LA(2) != '.')) ) {
                                if (state.backtracking>0) {state.failed=true; return ;}
                                throw new FailedPredicateException(input, "FLOATING_POINT_LITERAL", "input.LA(2) != '.'");
                            }

                            mDOT(); if (state.failed) return ;


                            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1294:25: ( ( DIGIT )+ ( EXPONENT |{...}? => DOT |) | EXPONENT |)
                            int alt18=3;
                            switch ( input.LA(1) ) {
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
                                alt18=1;
                                }
                                break;
                            case 'E':
                            case 'e':
                                {
                                alt18=2;
                                }
                                break;
                            default:
                                alt18=3;
                            }

                            switch (alt18) {
                                case 1 :
                                    // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1295:33: ( DIGIT )+ ( EXPONENT |{...}? => DOT |)
                                    {
                                    // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1295:33: ( DIGIT )+
                                    int cnt16=0;
                                    loop16:
                                    do {
                                        int alt16=2;
                                        int LA16_0 = input.LA(1);

                                        if ( ((LA16_0 >= '0' && LA16_0 <= '9')) ) {
                                            alt16=1;
                                        }


                                        switch (alt16) {
                                    	case 1 :
                                    	    // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:
                                    	    {
                                    	    if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
                                    	        input.consume();
                                    	        state.failed=false;
                                    	    }
                                    	    else {
                                    	        if (state.backtracking>0) {state.failed=true; return ;}
                                    	        MismatchedSetException mse = new MismatchedSetException(null,input);
                                    	        recover(mse);
                                    	        throw mse;
                                    	    }


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


                                    // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1296:33: ( EXPONENT |{...}? => DOT |)
                                    int alt17=3;
                                    int LA17_0 = input.LA(1);

                                    if ( (LA17_0=='E'||LA17_0=='e') ) {
                                        alt17=1;
                                    }
                                    else if ( (LA17_0=='.') && ((input.LA(2) != '.'))) {
                                        alt17=2;
                                    }
                                    else {
                                        alt17=3;
                                    }
                                    switch (alt17) {
                                        case 1 :
                                            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1297:37: EXPONENT
                                            {
                                            mEXPONENT(); if (state.failed) return ;


                                            if ( state.backtracking==0 ) {_type = FLOATING_POINT_LITERAL; }

                                            }
                                            break;
                                        case 2 :
                                            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1300:37: {...}? => DOT
                                            {
                                            if ( !((input.LA(2) != '.')) ) {
                                                if (state.backtracking>0) {state.failed=true; return ;}
                                                throw new FailedPredicateException(input, "FLOATING_POINT_LITERAL", "input.LA(2) != '.'");
                                            }

                                            mDOT(); if (state.failed) return ;


                                            if ( state.backtracking==0 ) {
                                                                                     int index = getText().indexOf('.');
                                                                                     
                                                                                     CommonToken digits1 = new CommonToken(input, DECIMAL_INTEGER_LITERAL, Token.DEFAULT_CHANNEL, state.tokenStartCharPositionInLine, state.tokenStartCharPositionInLine+index-1);
                                                                                     emit(digits1);
                                                                                    
                                                                                     CommonToken dot1 = new CommonToken(input, DOT, Token.DEFAULT_CHANNEL, state.tokenStartCharPositionInLine+index, state.tokenStartCharPositionInLine+index);
                                                                                     emit(dot1);
                                                                
                                                                                     CommonToken digits2 = new CommonToken(input, DECIMAL_INTEGER_LITERAL, Token.DEFAULT_CHANNEL, state.tokenStartCharPositionInLine+index+1, state.tokenStartCharPositionInLine + getText().length() -2);
                                                                                     emit(digits2);
                                                                            
                                                                                     CommonToken dot2 = new CommonToken(input, DOT, Token.DEFAULT_CHANNEL, state.tokenStartCharPositionInLine + getText().length() -1, state.tokenStartCharPositionInLine + getText().length() -1);
                                                                                     emit(dot2);
                                                                                    
                                                                                }

                                            }
                                            break;
                                        case 3 :
                                            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1318:37: 
                                            {
                                            if ( state.backtracking==0 ) {_type = FLOATING_POINT_LITERAL; }

                                            }
                                            break;

                                    }


                                    }
                                    break;
                                case 2 :
                                    // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1321:33: EXPONENT
                                    {
                                    mEXPONENT(); if (state.failed) return ;


                                    if ( state.backtracking==0 ) {_type = FLOATING_POINT_LITERAL; }

                                    }
                                    break;
                                case 3 :
                                    // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1324:33: 
                                    {
                                    if ( state.backtracking==0 ) {_type = FLOATING_POINT_LITERAL; }

                                    }
                                    break;

                            }


                            }
                            break;
                        case 2 :
                            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1327:25: ( EXPONENT |)
                            {
                            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1327:25: ( EXPONENT |)
                            int alt19=2;
                            int LA19_0 = input.LA(1);

                            if ( (LA19_0=='E'||LA19_0=='e') ) {
                                alt19=1;
                            }
                            else {
                                alt19=2;
                            }
                            switch (alt19) {
                                case 1 :
                                    // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1328:33: EXPONENT
                                    {
                                    mEXPONENT(); if (state.failed) return ;


                                    if ( state.backtracking==0 ) {_type = FLOATING_POINT_LITERAL; }

                                    }
                                    break;
                                case 2 :
                                    // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1331:33: 
                                    {
                                    if ( state.backtracking==0 ) {_type = DECIMAL_INTEGER_LITERAL; }

                                    }
                                    break;

                            }


                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1337:17: DOT ( ( DIGIT )+ ( EXPONENT |{...}?{...}? => DOT |) |{...}? => '.' |)
                    {
                    mDOT(); if (state.failed) return ;


                    // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1338:17: ( ( DIGIT )+ ( EXPONENT |{...}?{...}? => DOT |) |{...}? => '.' |)
                    int alt23=3;
                    int LA23_0 = input.LA(1);

                    if ( ((LA23_0 >= '0' && LA23_0 <= '9')) ) {
                        alt23=1;
                    }
                    else if ( (LA23_0=='.') && ((input.LA(2) != '.'))) {
                        alt23=2;
                    }
                    else {
                        alt23=3;
                    }
                    switch (alt23) {
                        case 1 :
                            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1339:25: ( DIGIT )+ ( EXPONENT |{...}?{...}? => DOT |)
                            {
                            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1339:25: ( DIGIT )+
                            int cnt21=0;
                            loop21:
                            do {
                                int alt21=2;
                                int LA21_0 = input.LA(1);

                                if ( ((LA21_0 >= '0' && LA21_0 <= '9')) ) {
                                    alt21=1;
                                }


                                switch (alt21) {
                            	case 1 :
                            	    // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:
                            	    {
                            	    if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
                            	        input.consume();
                            	        state.failed=false;
                            	    }
                            	    else {
                            	        if (state.backtracking>0) {state.failed=true; return ;}
                            	        MismatchedSetException mse = new MismatchedSetException(null,input);
                            	        recover(mse);
                            	        throw mse;
                            	    }


                            	    }
                            	    break;

                            	default :
                            	    if ( cnt21 >= 1 ) break loop21;
                            	    if (state.backtracking>0) {state.failed=true; return ;}
                                        EarlyExitException eee =
                                            new EarlyExitException(21, input);
                                        throw eee;
                                }
                                cnt21++;
                            } while (true);


                            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1340:25: ( EXPONENT |{...}?{...}? => DOT |)
                            int alt22=3;
                            int LA22_0 = input.LA(1);

                            if ( (LA22_0=='E'||LA22_0=='e') ) {
                                alt22=1;
                            }
                            else if ( (LA22_0=='.') && ((input.LA(2) != '.'))) {
                                alt22=2;
                            }
                            else {
                                alt22=3;
                            }
                            switch (alt22) {
                                case 1 :
                                    // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1341:29: EXPONENT
                                    {
                                    mEXPONENT(); if (state.failed) return ;


                                    if ( state.backtracking==0 ) {_type = FLOATING_POINT_LITERAL; }

                                    }
                                    break;
                                case 2 :
                                    // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1344:29: {...}?{...}? => DOT
                                    {
                                    if ( !((getText().startsWith("."))) ) {
                                        if (state.backtracking>0) {state.failed=true; return ;}
                                        throw new FailedPredicateException(input, "FLOATING_POINT_LITERAL", "$text.startsWith(\".\")");
                                    }

                                    if ( !((input.LA(2) != '.')) ) {
                                        if (state.backtracking>0) {state.failed=true; return ;}
                                        throw new FailedPredicateException(input, "FLOATING_POINT_LITERAL", "input.LA(2) != '.'");
                                    }

                                    mDOT(); if (state.failed) return ;


                                    if ( state.backtracking==0 ) {
                                                                   
                                                                    CommonToken dot1 = new CommonToken(input, DOT, Token.DEFAULT_CHANNEL, state.tokenStartCharPositionInLine, state.tokenStartCharPositionInLine);
                                                                    emit(dot1);
                                                        
                                                                    CommonToken digits = new CommonToken(input, DECIMAL_INTEGER_LITERAL, Token.DEFAULT_CHANNEL, state.tokenStartCharPositionInLine+1, state.tokenStartCharPositionInLine + getText().length() -2);
                                                                    emit(digits);
                                                                    
                                                                    CommonToken dot2 = new CommonToken(input, DOT, Token.DEFAULT_CHANNEL, state.tokenStartCharPositionInLine + getText().length() -1, state.tokenStartCharPositionInLine + getText().length() -1);
                                                                    emit(dot2);
                                                                   
                                                                    }

                                    }
                                    break;
                                case 3 :
                                    // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1359:29: 
                                    {
                                    if ( state.backtracking==0 ) {_type = FLOATING_POINT_LITERAL; }

                                    }
                                    break;

                            }


                            }
                            break;
                        case 2 :
                            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1363:25: {...}? => '.'
                            {
                            if ( !((input.LA(2) != '.')) ) {
                                if (state.backtracking>0) {state.failed=true; return ;}
                                throw new FailedPredicateException(input, "FLOATING_POINT_LITERAL", "input.LA(2) != '.'");
                            }

                            match('.'); if (state.failed) return ;

                            if ( state.backtracking==0 ) {_type = DOTDOT; }

                            }
                            break;
                        case 3 :
                            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1366:25: 
                            {
                            if ( state.backtracking==0 ) {_type = DOT; }

                            }
                            break;

                    }


                    }
                    break;

            }


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "FLOATING_POINT_LITERAL"

    // $ANTLR start "DECIMAL_NUMERAL"
    public final void mDECIMAL_NUMERAL() throws RecognitionException {
        try {
            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1395:9: ( ZERO_DIGIT | NON_ZERO_DIGIT ( DIGIT )* )
            int alt26=2;
            int LA26_0 = input.LA(1);

            if ( (LA26_0=='0') ) {
                alt26=1;
            }
            else if ( ((LA26_0 >= '1' && LA26_0 <= '9')) ) {
                alt26=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 26, 0, input);

                throw nvae;

            }
            switch (alt26) {
                case 1 :
                    // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1396:9: ZERO_DIGIT
                    {
                    mZERO_DIGIT(); if (state.failed) return ;


                    }
                    break;
                case 2 :
                    // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1397:11: NON_ZERO_DIGIT ( DIGIT )*
                    {
                    mNON_ZERO_DIGIT(); if (state.failed) return ;


                    // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1397:26: ( DIGIT )*
                    loop25:
                    do {
                        int alt25=2;
                        int LA25_0 = input.LA(1);

                        if ( ((LA25_0 >= '0' && LA25_0 <= '9')) ) {
                            alt25=1;
                        }


                        switch (alt25) {
                    	case 1 :
                    	    // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:
                    	    {
                    	    if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
                    	        input.consume();
                    	        state.failed=false;
                    	    }
                    	    else {
                    	        if (state.backtracking>0) {state.failed=true; return ;}
                    	        MismatchedSetException mse = new MismatchedSetException(null,input);
                    	        recover(mse);
                    	        throw mse;
                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop25;
                        }
                    } while (true);


                    }
                    break;

            }

        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "DECIMAL_NUMERAL"

    // $ANTLR start "DIGIT"
    public final void mDIGIT() throws RecognitionException {
        try {
            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1399:9: ( ZERO_DIGIT | NON_ZERO_DIGIT )
            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:
            {
            if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
                input.consume();
                state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "DIGIT"

    // $ANTLR start "ZERO_DIGIT"
    public final void mZERO_DIGIT() throws RecognitionException {
        try {
            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1406:9: ( '0' )
            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1407:9: '0'
            {
            match('0'); if (state.failed) return ;

            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "ZERO_DIGIT"

    // $ANTLR start "NON_ZERO_DIGIT"
    public final void mNON_ZERO_DIGIT() throws RecognitionException {
        try {
            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1412:9: ( '1' .. '9' )
            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:
            {
            if ( (input.LA(1) >= '1' && input.LA(1) <= '9') ) {
                input.consume();
                state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "NON_ZERO_DIGIT"

    // $ANTLR start "E"
    public final void mE() throws RecognitionException {
        try {
            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1418:9: ( ( 'e' | 'E' ) )
            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:
            {
            if ( input.LA(1)=='E'||input.LA(1)=='e' ) {
                input.consume();
                state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "E"

    // $ANTLR start "EXPONENT"
    public final void mEXPONENT() throws RecognitionException {
        try {
            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1427:9: ( E SIGNED_INTEGER )
            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1428:9: E SIGNED_INTEGER
            {
            mE(); if (state.failed) return ;


            mSIGNED_INTEGER(); if (state.failed) return ;


            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "EXPONENT"

    // $ANTLR start "SIGNED_INTEGER"
    public final void mSIGNED_INTEGER() throws RecognitionException {
        try {
            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1433:9: ( ( PLUS | MINUS )? ( DIGIT )+ )
            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1434:9: ( PLUS | MINUS )? ( DIGIT )+
            {
            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1434:9: ( PLUS | MINUS )?
            int alt27=2;
            int LA27_0 = input.LA(1);

            if ( (LA27_0=='+'||LA27_0=='-') ) {
                alt27=1;
            }
            switch (alt27) {
                case 1 :
                    // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:
                    {
                    if ( input.LA(1)=='+'||input.LA(1)=='-' ) {
                        input.consume();
                        state.failed=false;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return ;}
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        recover(mse);
                        throw mse;
                    }


                    }
                    break;

            }


            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1438:9: ( DIGIT )+
            int cnt28=0;
            loop28:
            do {
                int alt28=2;
                int LA28_0 = input.LA(1);

                if ( ((LA28_0 >= '0' && LA28_0 <= '9')) ) {
                    alt28=1;
                }


                switch (alt28) {
            	case 1 :
            	    // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:
            	    {
            	    if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
            	        input.consume();
            	        state.failed=false;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return ;}
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    if ( cnt28 >= 1 ) break loop28;
            	    if (state.backtracking>0) {state.failed=true; return ;}
                        EarlyExitException eee =
                            new EarlyExitException(28, input);
                        throw eee;
                }
                cnt28++;
            } while (true);


            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "SIGNED_INTEGER"

    // $ANTLR start "FTSWORD"
    public final void mFTSWORD() throws RecognitionException {
        try {
            int _type = FTSWORD;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1442:9: ( ( F_ESC | START_WORD ) ( F_ESC | IN_WORD )* )
            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1443:9: ( F_ESC | START_WORD ) ( F_ESC | IN_WORD )*
            {
            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1443:9: ( F_ESC | START_WORD )
            int alt29=2;
            int LA29_0 = input.LA(1);

            if ( (LA29_0=='\\') ) {
                alt29=1;
            }
            else if ( (LA29_0=='$'||(LA29_0 >= '0' && LA29_0 <= '9')||(LA29_0 >= 'A' && LA29_0 <= 'Z')||(LA29_0 >= 'a' && LA29_0 <= 'z')||(LA29_0 >= '\u00A2' && LA29_0 <= '\u00A7')||(LA29_0 >= '\u00A9' && LA29_0 <= '\u00AA')||LA29_0=='\u00AE'||LA29_0=='\u00B0'||(LA29_0 >= '\u00B2' && LA29_0 <= '\u00B3')||(LA29_0 >= '\u00B5' && LA29_0 <= '\u00B6')||(LA29_0 >= '\u00B9' && LA29_0 <= '\u00BA')||(LA29_0 >= '\u00BC' && LA29_0 <= '\u00BE')||(LA29_0 >= '\u00C0' && LA29_0 <= '\u00D6')||(LA29_0 >= '\u00D8' && LA29_0 <= '\u00F6')||(LA29_0 >= '\u00F8' && LA29_0 <= '\u0236')||(LA29_0 >= '\u0250' && LA29_0 <= '\u02C1')||(LA29_0 >= '\u02C6' && LA29_0 <= '\u02D1')||(LA29_0 >= '\u02E0' && LA29_0 <= '\u02E4')||LA29_0=='\u02EE'||(LA29_0 >= '\u0300' && LA29_0 <= '\u0357')||(LA29_0 >= '\u035D' && LA29_0 <= '\u036F')||LA29_0=='\u037A'||LA29_0=='\u0386'||(LA29_0 >= '\u0388' && LA29_0 <= '\u038A')||LA29_0=='\u038C'||(LA29_0 >= '\u038E' && LA29_0 <= '\u03A1')||(LA29_0 >= '\u03A3' && LA29_0 <= '\u03CE')||(LA29_0 >= '\u03D0' && LA29_0 <= '\u03F5')||(LA29_0 >= '\u03F7' && LA29_0 <= '\u03FB')||(LA29_0 >= '\u0400' && LA29_0 <= '\u0486')||(LA29_0 >= '\u0488' && LA29_0 <= '\u04CE')||(LA29_0 >= '\u04D0' && LA29_0 <= '\u04F5')||(LA29_0 >= '\u04F8' && LA29_0 <= '\u04F9')||(LA29_0 >= '\u0500' && LA29_0 <= '\u050F')||(LA29_0 >= '\u0531' && LA29_0 <= '\u0556')||LA29_0=='\u0559'||(LA29_0 >= '\u0561' && LA29_0 <= '\u0587')||(LA29_0 >= '\u0591' && LA29_0 <= '\u05A1')||(LA29_0 >= '\u05A3' && LA29_0 <= '\u05B9')||(LA29_0 >= '\u05BB' && LA29_0 <= '\u05BD')||LA29_0=='\u05BF'||(LA29_0 >= '\u05C1' && LA29_0 <= '\u05C2')||LA29_0=='\u05C4'||(LA29_0 >= '\u05D0' && LA29_0 <= '\u05EA')||(LA29_0 >= '\u05F0' && LA29_0 <= '\u05F2')||(LA29_0 >= '\u060E' && LA29_0 <= '\u0615')||(LA29_0 >= '\u0621' && LA29_0 <= '\u063A')||(LA29_0 >= '\u0640' && LA29_0 <= '\u0658')||(LA29_0 >= '\u0660' && LA29_0 <= '\u0669')||(LA29_0 >= '\u066E' && LA29_0 <= '\u06D3')||(LA29_0 >= '\u06D5' && LA29_0 <= '\u06DC')||(LA29_0 >= '\u06DE' && LA29_0 <= '\u06FF')||(LA29_0 >= '\u0710' && LA29_0 <= '\u074A')||(LA29_0 >= '\u074D' && LA29_0 <= '\u074F')||(LA29_0 >= '\u0780' && LA29_0 <= '\u07B1')||(LA29_0 >= '\u0901' && LA29_0 <= '\u0939')||(LA29_0 >= '\u093C' && LA29_0 <= '\u094D')||(LA29_0 >= '\u0950' && LA29_0 <= '\u0954')||(LA29_0 >= '\u0958' && LA29_0 <= '\u0963')||(LA29_0 >= '\u0966' && LA29_0 <= '\u096F')||(LA29_0 >= '\u0981' && LA29_0 <= '\u0983')||(LA29_0 >= '\u0985' && LA29_0 <= '\u098C')||(LA29_0 >= '\u098F' && LA29_0 <= '\u0990')||(LA29_0 >= '\u0993' && LA29_0 <= '\u09A8')||(LA29_0 >= '\u09AA' && LA29_0 <= '\u09B0')||LA29_0=='\u09B2'||(LA29_0 >= '\u09B6' && LA29_0 <= '\u09B9')||(LA29_0 >= '\u09BC' && LA29_0 <= '\u09C4')||(LA29_0 >= '\u09C7' && LA29_0 <= '\u09C8')||(LA29_0 >= '\u09CB' && LA29_0 <= '\u09CD')||LA29_0=='\u09D7'||(LA29_0 >= '\u09DC' && LA29_0 <= '\u09DD')||(LA29_0 >= '\u09DF' && LA29_0 <= '\u09E3')||(LA29_0 >= '\u09E6' && LA29_0 <= '\u09FA')||(LA29_0 >= '\u0A01' && LA29_0 <= '\u0A03')||(LA29_0 >= '\u0A05' && LA29_0 <= '\u0A0A')||(LA29_0 >= '\u0A0F' && LA29_0 <= '\u0A10')||(LA29_0 >= '\u0A13' && LA29_0 <= '\u0A28')||(LA29_0 >= '\u0A2A' && LA29_0 <= '\u0A30')||(LA29_0 >= '\u0A32' && LA29_0 <= '\u0A33')||(LA29_0 >= '\u0A35' && LA29_0 <= '\u0A36')||(LA29_0 >= '\u0A38' && LA29_0 <= '\u0A39')||LA29_0=='\u0A3C'||(LA29_0 >= '\u0A3E' && LA29_0 <= '\u0A42')||(LA29_0 >= '\u0A47' && LA29_0 <= '\u0A48')||(LA29_0 >= '\u0A4B' && LA29_0 <= '\u0A4D')||(LA29_0 >= '\u0A59' && LA29_0 <= '\u0A5C')||LA29_0=='\u0A5E'||(LA29_0 >= '\u0A66' && LA29_0 <= '\u0A74')||(LA29_0 >= '\u0A81' && LA29_0 <= '\u0A83')||(LA29_0 >= '\u0A85' && LA29_0 <= '\u0A8D')||(LA29_0 >= '\u0A8F' && LA29_0 <= '\u0A91')||(LA29_0 >= '\u0A93' && LA29_0 <= '\u0AA8')||(LA29_0 >= '\u0AAA' && LA29_0 <= '\u0AB0')||(LA29_0 >= '\u0AB2' && LA29_0 <= '\u0AB3')||(LA29_0 >= '\u0AB5' && LA29_0 <= '\u0AB9')||(LA29_0 >= '\u0ABC' && LA29_0 <= '\u0AC5')||(LA29_0 >= '\u0AC7' && LA29_0 <= '\u0AC9')||(LA29_0 >= '\u0ACB' && LA29_0 <= '\u0ACD')||LA29_0=='\u0AD0'||(LA29_0 >= '\u0AE0' && LA29_0 <= '\u0AE3')||(LA29_0 >= '\u0AE6' && LA29_0 <= '\u0AEF')||LA29_0=='\u0AF1'||(LA29_0 >= '\u0B01' && LA29_0 <= '\u0B03')||(LA29_0 >= '\u0B05' && LA29_0 <= '\u0B0C')||(LA29_0 >= '\u0B0F' && LA29_0 <= '\u0B10')||(LA29_0 >= '\u0B13' && LA29_0 <= '\u0B28')||(LA29_0 >= '\u0B2A' && LA29_0 <= '\u0B30')||(LA29_0 >= '\u0B32' && LA29_0 <= '\u0B33')||(LA29_0 >= '\u0B35' && LA29_0 <= '\u0B39')||(LA29_0 >= '\u0B3C' && LA29_0 <= '\u0B43')||(LA29_0 >= '\u0B47' && LA29_0 <= '\u0B48')||(LA29_0 >= '\u0B4B' && LA29_0 <= '\u0B4D')||(LA29_0 >= '\u0B56' && LA29_0 <= '\u0B57')||(LA29_0 >= '\u0B5C' && LA29_0 <= '\u0B5D')||(LA29_0 >= '\u0B5F' && LA29_0 <= '\u0B61')||(LA29_0 >= '\u0B66' && LA29_0 <= '\u0B71')||(LA29_0 >= '\u0B82' && LA29_0 <= '\u0B83')||(LA29_0 >= '\u0B85' && LA29_0 <= '\u0B8A')||(LA29_0 >= '\u0B8E' && LA29_0 <= '\u0B90')||(LA29_0 >= '\u0B92' && LA29_0 <= '\u0B95')||(LA29_0 >= '\u0B99' && LA29_0 <= '\u0B9A')||LA29_0=='\u0B9C'||(LA29_0 >= '\u0B9E' && LA29_0 <= '\u0B9F')||(LA29_0 >= '\u0BA3' && LA29_0 <= '\u0BA4')||(LA29_0 >= '\u0BA8' && LA29_0 <= '\u0BAA')||(LA29_0 >= '\u0BAE' && LA29_0 <= '\u0BB5')||(LA29_0 >= '\u0BB7' && LA29_0 <= '\u0BB9')||(LA29_0 >= '\u0BBE' && LA29_0 <= '\u0BC2')||(LA29_0 >= '\u0BC6' && LA29_0 <= '\u0BC8')||(LA29_0 >= '\u0BCA' && LA29_0 <= '\u0BCD')||LA29_0=='\u0BD7'||(LA29_0 >= '\u0BE7' && LA29_0 <= '\u0BFA')||(LA29_0 >= '\u0C01' && LA29_0 <= '\u0C03')||(LA29_0 >= '\u0C05' && LA29_0 <= '\u0C0C')||(LA29_0 >= '\u0C0E' && LA29_0 <= '\u0C10')||(LA29_0 >= '\u0C12' && LA29_0 <= '\u0C28')||(LA29_0 >= '\u0C2A' && LA29_0 <= '\u0C33')||(LA29_0 >= '\u0C35' && LA29_0 <= '\u0C39')||(LA29_0 >= '\u0C3E' && LA29_0 <= '\u0C44')||(LA29_0 >= '\u0C46' && LA29_0 <= '\u0C48')||(LA29_0 >= '\u0C4A' && LA29_0 <= '\u0C4D')||(LA29_0 >= '\u0C55' && LA29_0 <= '\u0C56')||(LA29_0 >= '\u0C60' && LA29_0 <= '\u0C61')||(LA29_0 >= '\u0C66' && LA29_0 <= '\u0C6F')||(LA29_0 >= '\u0C82' && LA29_0 <= '\u0C83')||(LA29_0 >= '\u0C85' && LA29_0 <= '\u0C8C')||(LA29_0 >= '\u0C8E' && LA29_0 <= '\u0C90')||(LA29_0 >= '\u0C92' && LA29_0 <= '\u0CA8')||(LA29_0 >= '\u0CAA' && LA29_0 <= '\u0CB3')||(LA29_0 >= '\u0CB5' && LA29_0 <= '\u0CB9')||(LA29_0 >= '\u0CBC' && LA29_0 <= '\u0CC4')||(LA29_0 >= '\u0CC6' && LA29_0 <= '\u0CC8')||(LA29_0 >= '\u0CCA' && LA29_0 <= '\u0CCD')||(LA29_0 >= '\u0CD5' && LA29_0 <= '\u0CD6')||LA29_0=='\u0CDE'||(LA29_0 >= '\u0CE0' && LA29_0 <= '\u0CE1')||(LA29_0 >= '\u0CE6' && LA29_0 <= '\u0CEF')||(LA29_0 >= '\u0D02' && LA29_0 <= '\u0D03')||(LA29_0 >= '\u0D05' && LA29_0 <= '\u0D0C')||(LA29_0 >= '\u0D0E' && LA29_0 <= '\u0D10')||(LA29_0 >= '\u0D12' && LA29_0 <= '\u0D28')||(LA29_0 >= '\u0D2A' && LA29_0 <= '\u0D39')||(LA29_0 >= '\u0D3E' && LA29_0 <= '\u0D43')||(LA29_0 >= '\u0D46' && LA29_0 <= '\u0D48')||(LA29_0 >= '\u0D4A' && LA29_0 <= '\u0D4D')||LA29_0=='\u0D57'||(LA29_0 >= '\u0D60' && LA29_0 <= '\u0D61')||(LA29_0 >= '\u0D66' && LA29_0 <= '\u0D6F')||(LA29_0 >= '\u0D82' && LA29_0 <= '\u0D83')||(LA29_0 >= '\u0D85' && LA29_0 <= '\u0D96')||(LA29_0 >= '\u0D9A' && LA29_0 <= '\u0DB1')||(LA29_0 >= '\u0DB3' && LA29_0 <= '\u0DBB')||LA29_0=='\u0DBD'||(LA29_0 >= '\u0DC0' && LA29_0 <= '\u0DC6')||LA29_0=='\u0DCA'||(LA29_0 >= '\u0DCF' && LA29_0 <= '\u0DD4')||LA29_0=='\u0DD6'||(LA29_0 >= '\u0DD8' && LA29_0 <= '\u0DDF')||(LA29_0 >= '\u0DF2' && LA29_0 <= '\u0DF3')||(LA29_0 >= '\u0E01' && LA29_0 <= '\u0E3A')||(LA29_0 >= '\u0E3F' && LA29_0 <= '\u0E4E')||(LA29_0 >= '\u0E50' && LA29_0 <= '\u0E59')||(LA29_0 >= '\u0E81' && LA29_0 <= '\u0E82')||LA29_0=='\u0E84'||(LA29_0 >= '\u0E87' && LA29_0 <= '\u0E88')||LA29_0=='\u0E8A'||LA29_0=='\u0E8D'||(LA29_0 >= '\u0E94' && LA29_0 <= '\u0E97')||(LA29_0 >= '\u0E99' && LA29_0 <= '\u0E9F')||(LA29_0 >= '\u0EA1' && LA29_0 <= '\u0EA3')||LA29_0=='\u0EA5'||LA29_0=='\u0EA7'||(LA29_0 >= '\u0EAA' && LA29_0 <= '\u0EAB')||(LA29_0 >= '\u0EAD' && LA29_0 <= '\u0EB9')||(LA29_0 >= '\u0EBB' && LA29_0 <= '\u0EBD')||(LA29_0 >= '\u0EC0' && LA29_0 <= '\u0EC4')||LA29_0=='\u0EC6'||(LA29_0 >= '\u0EC8' && LA29_0 <= '\u0ECD')||(LA29_0 >= '\u0ED0' && LA29_0 <= '\u0ED9')||(LA29_0 >= '\u0EDC' && LA29_0 <= '\u0EDD')||(LA29_0 >= '\u0F00' && LA29_0 <= '\u0F03')||(LA29_0 >= '\u0F13' && LA29_0 <= '\u0F39')||(LA29_0 >= '\u0F3E' && LA29_0 <= '\u0F47')||(LA29_0 >= '\u0F49' && LA29_0 <= '\u0F6A')||(LA29_0 >= '\u0F71' && LA29_0 <= '\u0F84')||(LA29_0 >= '\u0F86' && LA29_0 <= '\u0F8B')||(LA29_0 >= '\u0F90' && LA29_0 <= '\u0F97')||(LA29_0 >= '\u0F99' && LA29_0 <= '\u0FBC')||(LA29_0 >= '\u0FBE' && LA29_0 <= '\u0FCC')||LA29_0=='\u0FCF'||(LA29_0 >= '\u1000' && LA29_0 <= '\u1021')||(LA29_0 >= '\u1023' && LA29_0 <= '\u1027')||(LA29_0 >= '\u1029' && LA29_0 <= '\u102A')||(LA29_0 >= '\u102C' && LA29_0 <= '\u1032')||(LA29_0 >= '\u1036' && LA29_0 <= '\u1039')||(LA29_0 >= '\u1040' && LA29_0 <= '\u1049')||(LA29_0 >= '\u1050' && LA29_0 <= '\u1059')||(LA29_0 >= '\u10A0' && LA29_0 <= '\u10C5')||(LA29_0 >= '\u10D0' && LA29_0 <= '\u10F8')||(LA29_0 >= '\u1100' && LA29_0 <= '\u1159')||(LA29_0 >= '\u115F' && LA29_0 <= '\u11A2')||(LA29_0 >= '\u11A8' && LA29_0 <= '\u11F9')||(LA29_0 >= '\u1200' && LA29_0 <= '\u1206')||(LA29_0 >= '\u1208' && LA29_0 <= '\u1246')||LA29_0=='\u1248'||(LA29_0 >= '\u124A' && LA29_0 <= '\u124D')||(LA29_0 >= '\u1250' && LA29_0 <= '\u1256')||LA29_0=='\u1258'||(LA29_0 >= '\u125A' && LA29_0 <= '\u125D')||(LA29_0 >= '\u1260' && LA29_0 <= '\u1286')||LA29_0=='\u1288'||(LA29_0 >= '\u128A' && LA29_0 <= '\u128D')||(LA29_0 >= '\u1290' && LA29_0 <= '\u12AE')||LA29_0=='\u12B0'||(LA29_0 >= '\u12B2' && LA29_0 <= '\u12B5')||(LA29_0 >= '\u12B8' && LA29_0 <= '\u12BE')||LA29_0=='\u12C0'||(LA29_0 >= '\u12C2' && LA29_0 <= '\u12C5')||(LA29_0 >= '\u12C8' && LA29_0 <= '\u12CE')||(LA29_0 >= '\u12D0' && LA29_0 <= '\u12D6')||(LA29_0 >= '\u12D8' && LA29_0 <= '\u12EE')||(LA29_0 >= '\u12F0' && LA29_0 <= '\u130E')||LA29_0=='\u1310'||(LA29_0 >= '\u1312' && LA29_0 <= '\u1315')||(LA29_0 >= '\u1318' && LA29_0 <= '\u131E')||(LA29_0 >= '\u1320' && LA29_0 <= '\u1346')||(LA29_0 >= '\u1348' && LA29_0 <= '\u135A')||(LA29_0 >= '\u1369' && LA29_0 <= '\u137C')||(LA29_0 >= '\u13A0' && LA29_0 <= '\u13F4')||(LA29_0 >= '\u1401' && LA29_0 <= '\u166C')||(LA29_0 >= '\u166F' && LA29_0 <= '\u1676')||(LA29_0 >= '\u1681' && LA29_0 <= '\u169A')||(LA29_0 >= '\u16A0' && LA29_0 <= '\u16EA')||(LA29_0 >= '\u16EE' && LA29_0 <= '\u16F0')||(LA29_0 >= '\u1700' && LA29_0 <= '\u170C')||(LA29_0 >= '\u170E' && LA29_0 <= '\u1714')||(LA29_0 >= '\u1720' && LA29_0 <= '\u1734')||(LA29_0 >= '\u1740' && LA29_0 <= '\u1753')||(LA29_0 >= '\u1760' && LA29_0 <= '\u176C')||(LA29_0 >= '\u176E' && LA29_0 <= '\u1770')||(LA29_0 >= '\u1772' && LA29_0 <= '\u1773')||(LA29_0 >= '\u1780' && LA29_0 <= '\u17B3')||(LA29_0 >= '\u17B6' && LA29_0 <= '\u17D3')||LA29_0=='\u17D7'||(LA29_0 >= '\u17DB' && LA29_0 <= '\u17DD')||(LA29_0 >= '\u17E0' && LA29_0 <= '\u17E9')||(LA29_0 >= '\u17F0' && LA29_0 <= '\u17F9')||(LA29_0 >= '\u180B' && LA29_0 <= '\u180D')||(LA29_0 >= '\u1810' && LA29_0 <= '\u1819')||(LA29_0 >= '\u1820' && LA29_0 <= '\u1877')||(LA29_0 >= '\u1880' && LA29_0 <= '\u18A9')||(LA29_0 >= '\u1900' && LA29_0 <= '\u191C')||(LA29_0 >= '\u1920' && LA29_0 <= '\u192B')||(LA29_0 >= '\u1930' && LA29_0 <= '\u193B')||LA29_0=='\u1940'||(LA29_0 >= '\u1946' && LA29_0 <= '\u196D')||(LA29_0 >= '\u1970' && LA29_0 <= '\u1974')||(LA29_0 >= '\u19E0' && LA29_0 <= '\u19FF')||(LA29_0 >= '\u1D00' && LA29_0 <= '\u1D6B')||(LA29_0 >= '\u1E00' && LA29_0 <= '\u1E9B')||(LA29_0 >= '\u1EA0' && LA29_0 <= '\u1EF9')||(LA29_0 >= '\u1F00' && LA29_0 <= '\u1F15')||(LA29_0 >= '\u1F18' && LA29_0 <= '\u1F1D')||(LA29_0 >= '\u1F20' && LA29_0 <= '\u1F45')||(LA29_0 >= '\u1F48' && LA29_0 <= '\u1F4D')||(LA29_0 >= '\u1F50' && LA29_0 <= '\u1F57')||LA29_0=='\u1F59'||LA29_0=='\u1F5B'||LA29_0=='\u1F5D'||(LA29_0 >= '\u1F5F' && LA29_0 <= '\u1F7D')||(LA29_0 >= '\u1F80' && LA29_0 <= '\u1FB4')||(LA29_0 >= '\u1FB6' && LA29_0 <= '\u1FBC')||LA29_0=='\u1FBE'||(LA29_0 >= '\u1FC2' && LA29_0 <= '\u1FC4')||(LA29_0 >= '\u1FC6' && LA29_0 <= '\u1FCC')||(LA29_0 >= '\u1FD0' && LA29_0 <= '\u1FD3')||(LA29_0 >= '\u1FD6' && LA29_0 <= '\u1FDB')||(LA29_0 >= '\u1FE0' && LA29_0 <= '\u1FEC')||(LA29_0 >= '\u1FF2' && LA29_0 <= '\u1FF4')||(LA29_0 >= '\u1FF6' && LA29_0 <= '\u1FFC')||(LA29_0 >= '\u2070' && LA29_0 <= '\u2071')||(LA29_0 >= '\u2074' && LA29_0 <= '\u2079')||(LA29_0 >= '\u207F' && LA29_0 <= '\u2089')||(LA29_0 >= '\u20A0' && LA29_0 <= '\u20B1')||(LA29_0 >= '\u20D0' && LA29_0 <= '\u20EA')||(LA29_0 >= '\u2100' && LA29_0 <= '\u213B')||(LA29_0 >= '\u213D' && LA29_0 <= '\u213F')||(LA29_0 >= '\u2145' && LA29_0 <= '\u214A')||(LA29_0 >= '\u2153' && LA29_0 <= '\u2183')||(LA29_0 >= '\u2195' && LA29_0 <= '\u2199')||(LA29_0 >= '\u219C' && LA29_0 <= '\u219F')||(LA29_0 >= '\u21A1' && LA29_0 <= '\u21A2')||(LA29_0 >= '\u21A4' && LA29_0 <= '\u21A5')||(LA29_0 >= '\u21A7' && LA29_0 <= '\u21AD')||(LA29_0 >= '\u21AF' && LA29_0 <= '\u21CD')||(LA29_0 >= '\u21D0' && LA29_0 <= '\u21D1')||LA29_0=='\u21D3'||(LA29_0 >= '\u21D5' && LA29_0 <= '\u21F3')||(LA29_0 >= '\u2300' && LA29_0 <= '\u2307')||(LA29_0 >= '\u230C' && LA29_0 <= '\u231F')||(LA29_0 >= '\u2322' && LA29_0 <= '\u2328')||(LA29_0 >= '\u232B' && LA29_0 <= '\u237B')||(LA29_0 >= '\u237D' && LA29_0 <= '\u239A')||(LA29_0 >= '\u23B7' && LA29_0 <= '\u23D0')||(LA29_0 >= '\u2400' && LA29_0 <= '\u2426')||(LA29_0 >= '\u2440' && LA29_0 <= '\u244A')||(LA29_0 >= '\u2460' && LA29_0 <= '\u25B6')||(LA29_0 >= '\u25B8' && LA29_0 <= '\u25C0')||(LA29_0 >= '\u25C2' && LA29_0 <= '\u25F7')||(LA29_0 >= '\u2600' && LA29_0 <= '\u2617')||(LA29_0 >= '\u2619' && LA29_0 <= '\u266E')||(LA29_0 >= '\u2670' && LA29_0 <= '\u267D')||(LA29_0 >= '\u2680' && LA29_0 <= '\u2691')||(LA29_0 >= '\u26A0' && LA29_0 <= '\u26A1')||(LA29_0 >= '\u2701' && LA29_0 <= '\u2704')||(LA29_0 >= '\u2706' && LA29_0 <= '\u2709')||(LA29_0 >= '\u270C' && LA29_0 <= '\u2727')||(LA29_0 >= '\u2729' && LA29_0 <= '\u274B')||LA29_0=='\u274D'||(LA29_0 >= '\u274F' && LA29_0 <= '\u2752')||LA29_0=='\u2756'||(LA29_0 >= '\u2758' && LA29_0 <= '\u275E')||(LA29_0 >= '\u2761' && LA29_0 <= '\u2767')||(LA29_0 >= '\u2776' && LA29_0 <= '\u2794')||(LA29_0 >= '\u2798' && LA29_0 <= '\u27AF')||(LA29_0 >= '\u27B1' && LA29_0 <= '\u27BE')||(LA29_0 >= '\u2800' && LA29_0 <= '\u28FF')||(LA29_0 >= '\u2B00' && LA29_0 <= '\u2B0D')||(LA29_0 >= '\u2E80' && LA29_0 <= '\u2E99')||(LA29_0 >= '\u2E9B' && LA29_0 <= '\u2EF3')||(LA29_0 >= '\u2F00' && LA29_0 <= '\u2FD5')||(LA29_0 >= '\u2FF0' && LA29_0 <= '\u2FFB')||(LA29_0 >= '\u3004' && LA29_0 <= '\u3007')||(LA29_0 >= '\u3012' && LA29_0 <= '\u3013')||(LA29_0 >= '\u3020' && LA29_0 <= '\u302F')||(LA29_0 >= '\u3031' && LA29_0 <= '\u303C')||(LA29_0 >= '\u303E' && LA29_0 <= '\u303F')||(LA29_0 >= '\u3041' && LA29_0 <= '\u3096')||(LA29_0 >= '\u3099' && LA29_0 <= '\u309A')||(LA29_0 >= '\u309D' && LA29_0 <= '\u309F')||(LA29_0 >= '\u30A1' && LA29_0 <= '\u30FA')||(LA29_0 >= '\u30FC' && LA29_0 <= '\u30FF')||(LA29_0 >= '\u3105' && LA29_0 <= '\u312C')||(LA29_0 >= '\u3131' && LA29_0 <= '\u318E')||(LA29_0 >= '\u3190' && LA29_0 <= '\u31B7')||(LA29_0 >= '\u31F0' && LA29_0 <= '\u321E')||(LA29_0 >= '\u3220' && LA29_0 <= '\u3243')||(LA29_0 >= '\u3250' && LA29_0 <= '\u327D')||(LA29_0 >= '\u327F' && LA29_0 <= '\u32FE')||(LA29_0 >= '\u3300' && LA29_0 <= '\u4DB5')||(LA29_0 >= '\u4DC0' && LA29_0 <= '\u9FA5')||(LA29_0 >= '\uA000' && LA29_0 <= '\uA48C')||(LA29_0 >= '\uA490' && LA29_0 <= '\uA4C6')||(LA29_0 >= '\uAC00' && LA29_0 <= '\uD7A3')||(LA29_0 >= '\uF900' && LA29_0 <= '\uFA2D')||(LA29_0 >= '\uFA30' && LA29_0 <= '\uFA6A')||(LA29_0 >= '\uFB00' && LA29_0 <= '\uFB06')||(LA29_0 >= '\uFB13' && LA29_0 <= '\uFB17')||(LA29_0 >= '\uFB1D' && LA29_0 <= '\uFB28')||(LA29_0 >= '\uFB2A' && LA29_0 <= '\uFB36')||(LA29_0 >= '\uFB38' && LA29_0 <= '\uFB3C')||LA29_0=='\uFB3E'||(LA29_0 >= '\uFB40' && LA29_0 <= '\uFB41')||(LA29_0 >= '\uFB43' && LA29_0 <= '\uFB44')||(LA29_0 >= '\uFB46' && LA29_0 <= '\uFBB1')||(LA29_0 >= '\uFBD3' && LA29_0 <= '\uFD3D')||(LA29_0 >= '\uFD50' && LA29_0 <= '\uFD8F')||(LA29_0 >= '\uFD92' && LA29_0 <= '\uFDC7')||(LA29_0 >= '\uFDF0' && LA29_0 <= '\uFDFD')||(LA29_0 >= '\uFE00' && LA29_0 <= '\uFE0F')||(LA29_0 >= '\uFE20' && LA29_0 <= '\uFE23')||LA29_0=='\uFE69'||(LA29_0 >= '\uFE70' && LA29_0 <= '\uFE74')||(LA29_0 >= '\uFE76' && LA29_0 <= '\uFEFC')||LA29_0=='\uFF04'||(LA29_0 >= '\uFF10' && LA29_0 <= '\uFF19')||(LA29_0 >= '\uFF21' && LA29_0 <= '\uFF3A')||(LA29_0 >= '\uFF41' && LA29_0 <= '\uFF5A')||(LA29_0 >= '\uFF66' && LA29_0 <= '\uFFBE')||(LA29_0 >= '\uFFC2' && LA29_0 <= '\uFFC7')||(LA29_0 >= '\uFFCA' && LA29_0 <= '\uFFCF')||(LA29_0 >= '\uFFD2' && LA29_0 <= '\uFFD7')||(LA29_0 >= '\uFFDA' && LA29_0 <= '\uFFDC')||(LA29_0 >= '\uFFE0' && LA29_0 <= '\uFFE1')||(LA29_0 >= '\uFFE4' && LA29_0 <= '\uFFE6')||LA29_0=='\uFFE8'||(LA29_0 >= '\uFFED' && LA29_0 <= '\uFFEE')) ) {
                alt29=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 29, 0, input);

                throw nvae;

            }
            switch (alt29) {
                case 1 :
                    // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1444:17: F_ESC
                    {
                    mF_ESC(); if (state.failed) return ;


                    }
                    break;
                case 2 :
                    // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1445:19: START_WORD
                    {
                    mSTART_WORD(); if (state.failed) return ;


                    }
                    break;

            }


            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1447:9: ( F_ESC | IN_WORD )*
            loop30:
            do {
                int alt30=3;
                int LA30_0 = input.LA(1);

                if ( (LA30_0=='\\') ) {
                    alt30=1;
                }
                else if ( ((LA30_0 >= '!' && LA30_0 <= '\'')||LA30_0=='+'||LA30_0=='-'||(LA30_0 >= '/' && LA30_0 <= '9')||LA30_0==';'||LA30_0=='='||(LA30_0 >= '@' && LA30_0 <= 'Z')||LA30_0=='_'||(LA30_0 >= 'a' && LA30_0 <= 'z')||LA30_0=='|'||(LA30_0 >= '\u00A1' && LA30_0 <= '\u00A7')||(LA30_0 >= '\u00A9' && LA30_0 <= '\u00AA')||LA30_0=='\u00AC'||LA30_0=='\u00AE'||(LA30_0 >= '\u00B0' && LA30_0 <= '\u00B3')||(LA30_0 >= '\u00B5' && LA30_0 <= '\u00B7')||(LA30_0 >= '\u00B9' && LA30_0 <= '\u00BA')||(LA30_0 >= '\u00BC' && LA30_0 <= '\u0236')||(LA30_0 >= '\u0250' && LA30_0 <= '\u02C1')||(LA30_0 >= '\u02C6' && LA30_0 <= '\u02D1')||(LA30_0 >= '\u02E0' && LA30_0 <= '\u02E4')||LA30_0=='\u02EE'||(LA30_0 >= '\u0300' && LA30_0 <= '\u0357')||(LA30_0 >= '\u035D' && LA30_0 <= '\u036F')||LA30_0=='\u037A'||LA30_0=='\u037E'||(LA30_0 >= '\u0386' && LA30_0 <= '\u038A')||LA30_0=='\u038C'||(LA30_0 >= '\u038E' && LA30_0 <= '\u03A1')||(LA30_0 >= '\u03A3' && LA30_0 <= '\u03CE')||(LA30_0 >= '\u03D0' && LA30_0 <= '\u03FB')||(LA30_0 >= '\u0400' && LA30_0 <= '\u0486')||(LA30_0 >= '\u0488' && LA30_0 <= '\u04CE')||(LA30_0 >= '\u04D0' && LA30_0 <= '\u04F5')||(LA30_0 >= '\u04F8' && LA30_0 <= '\u04F9')||(LA30_0 >= '\u0500' && LA30_0 <= '\u050F')||(LA30_0 >= '\u0531' && LA30_0 <= '\u0556')||(LA30_0 >= '\u0559' && LA30_0 <= '\u055F')||(LA30_0 >= '\u0561' && LA30_0 <= '\u0587')||(LA30_0 >= '\u0589' && LA30_0 <= '\u058A')||(LA30_0 >= '\u0591' && LA30_0 <= '\u05A1')||(LA30_0 >= '\u05A3' && LA30_0 <= '\u05B9')||(LA30_0 >= '\u05BB' && LA30_0 <= '\u05C4')||(LA30_0 >= '\u05D0' && LA30_0 <= '\u05EA')||(LA30_0 >= '\u05F0' && LA30_0 <= '\u05F4')||(LA30_0 >= '\u060C' && LA30_0 <= '\u0615')||LA30_0=='\u061B'||LA30_0=='\u061F'||(LA30_0 >= '\u0621' && LA30_0 <= '\u063A')||(LA30_0 >= '\u0640' && LA30_0 <= '\u0658')||(LA30_0 >= '\u0660' && LA30_0 <= '\u06DC')||(LA30_0 >= '\u06DE' && LA30_0 <= '\u070D')||(LA30_0 >= '\u0710' && LA30_0 <= '\u074A')||(LA30_0 >= '\u074D' && LA30_0 <= '\u074F')||(LA30_0 >= '\u0780' && LA30_0 <= '\u07B1')||(LA30_0 >= '\u0901' && LA30_0 <= '\u0939')||(LA30_0 >= '\u093C' && LA30_0 <= '\u094D')||(LA30_0 >= '\u0950' && LA30_0 <= '\u0954')||(LA30_0 >= '\u0958' && LA30_0 <= '\u0970')||(LA30_0 >= '\u0981' && LA30_0 <= '\u0983')||(LA30_0 >= '\u0985' && LA30_0 <= '\u098C')||(LA30_0 >= '\u098F' && LA30_0 <= '\u0990')||(LA30_0 >= '\u0993' && LA30_0 <= '\u09A8')||(LA30_0 >= '\u09AA' && LA30_0 <= '\u09B0')||LA30_0=='\u09B2'||(LA30_0 >= '\u09B6' && LA30_0 <= '\u09B9')||(LA30_0 >= '\u09BC' && LA30_0 <= '\u09C4')||(LA30_0 >= '\u09C7' && LA30_0 <= '\u09C8')||(LA30_0 >= '\u09CB' && LA30_0 <= '\u09CD')||LA30_0=='\u09D7'||(LA30_0 >= '\u09DC' && LA30_0 <= '\u09DD')||(LA30_0 >= '\u09DF' && LA30_0 <= '\u09E3')||(LA30_0 >= '\u09E6' && LA30_0 <= '\u09FA')||(LA30_0 >= '\u0A01' && LA30_0 <= '\u0A03')||(LA30_0 >= '\u0A05' && LA30_0 <= '\u0A0A')||(LA30_0 >= '\u0A0F' && LA30_0 <= '\u0A10')||(LA30_0 >= '\u0A13' && LA30_0 <= '\u0A28')||(LA30_0 >= '\u0A2A' && LA30_0 <= '\u0A30')||(LA30_0 >= '\u0A32' && LA30_0 <= '\u0A33')||(LA30_0 >= '\u0A35' && LA30_0 <= '\u0A36')||(LA30_0 >= '\u0A38' && LA30_0 <= '\u0A39')||LA30_0=='\u0A3C'||(LA30_0 >= '\u0A3E' && LA30_0 <= '\u0A42')||(LA30_0 >= '\u0A47' && LA30_0 <= '\u0A48')||(LA30_0 >= '\u0A4B' && LA30_0 <= '\u0A4D')||(LA30_0 >= '\u0A59' && LA30_0 <= '\u0A5C')||LA30_0=='\u0A5E'||(LA30_0 >= '\u0A66' && LA30_0 <= '\u0A74')||(LA30_0 >= '\u0A81' && LA30_0 <= '\u0A83')||(LA30_0 >= '\u0A85' && LA30_0 <= '\u0A8D')||(LA30_0 >= '\u0A8F' && LA30_0 <= '\u0A91')||(LA30_0 >= '\u0A93' && LA30_0 <= '\u0AA8')||(LA30_0 >= '\u0AAA' && LA30_0 <= '\u0AB0')||(LA30_0 >= '\u0AB2' && LA30_0 <= '\u0AB3')||(LA30_0 >= '\u0AB5' && LA30_0 <= '\u0AB9')||(LA30_0 >= '\u0ABC' && LA30_0 <= '\u0AC5')||(LA30_0 >= '\u0AC7' && LA30_0 <= '\u0AC9')||(LA30_0 >= '\u0ACB' && LA30_0 <= '\u0ACD')||LA30_0=='\u0AD0'||(LA30_0 >= '\u0AE0' && LA30_0 <= '\u0AE3')||(LA30_0 >= '\u0AE6' && LA30_0 <= '\u0AEF')||LA30_0=='\u0AF1'||(LA30_0 >= '\u0B01' && LA30_0 <= '\u0B03')||(LA30_0 >= '\u0B05' && LA30_0 <= '\u0B0C')||(LA30_0 >= '\u0B0F' && LA30_0 <= '\u0B10')||(LA30_0 >= '\u0B13' && LA30_0 <= '\u0B28')||(LA30_0 >= '\u0B2A' && LA30_0 <= '\u0B30')||(LA30_0 >= '\u0B32' && LA30_0 <= '\u0B33')||(LA30_0 >= '\u0B35' && LA30_0 <= '\u0B39')||(LA30_0 >= '\u0B3C' && LA30_0 <= '\u0B43')||(LA30_0 >= '\u0B47' && LA30_0 <= '\u0B48')||(LA30_0 >= '\u0B4B' && LA30_0 <= '\u0B4D')||(LA30_0 >= '\u0B56' && LA30_0 <= '\u0B57')||(LA30_0 >= '\u0B5C' && LA30_0 <= '\u0B5D')||(LA30_0 >= '\u0B5F' && LA30_0 <= '\u0B61')||(LA30_0 >= '\u0B66' && LA30_0 <= '\u0B71')||(LA30_0 >= '\u0B82' && LA30_0 <= '\u0B83')||(LA30_0 >= '\u0B85' && LA30_0 <= '\u0B8A')||(LA30_0 >= '\u0B8E' && LA30_0 <= '\u0B90')||(LA30_0 >= '\u0B92' && LA30_0 <= '\u0B95')||(LA30_0 >= '\u0B99' && LA30_0 <= '\u0B9A')||LA30_0=='\u0B9C'||(LA30_0 >= '\u0B9E' && LA30_0 <= '\u0B9F')||(LA30_0 >= '\u0BA3' && LA30_0 <= '\u0BA4')||(LA30_0 >= '\u0BA8' && LA30_0 <= '\u0BAA')||(LA30_0 >= '\u0BAE' && LA30_0 <= '\u0BB5')||(LA30_0 >= '\u0BB7' && LA30_0 <= '\u0BB9')||(LA30_0 >= '\u0BBE' && LA30_0 <= '\u0BC2')||(LA30_0 >= '\u0BC6' && LA30_0 <= '\u0BC8')||(LA30_0 >= '\u0BCA' && LA30_0 <= '\u0BCD')||LA30_0=='\u0BD7'||(LA30_0 >= '\u0BE7' && LA30_0 <= '\u0BFA')||(LA30_0 >= '\u0C01' && LA30_0 <= '\u0C03')||(LA30_0 >= '\u0C05' && LA30_0 <= '\u0C0C')||(LA30_0 >= '\u0C0E' && LA30_0 <= '\u0C10')||(LA30_0 >= '\u0C12' && LA30_0 <= '\u0C28')||(LA30_0 >= '\u0C2A' && LA30_0 <= '\u0C33')||(LA30_0 >= '\u0C35' && LA30_0 <= '\u0C39')||(LA30_0 >= '\u0C3E' && LA30_0 <= '\u0C44')||(LA30_0 >= '\u0C46' && LA30_0 <= '\u0C48')||(LA30_0 >= '\u0C4A' && LA30_0 <= '\u0C4D')||(LA30_0 >= '\u0C55' && LA30_0 <= '\u0C56')||(LA30_0 >= '\u0C60' && LA30_0 <= '\u0C61')||(LA30_0 >= '\u0C66' && LA30_0 <= '\u0C6F')||(LA30_0 >= '\u0C82' && LA30_0 <= '\u0C83')||(LA30_0 >= '\u0C85' && LA30_0 <= '\u0C8C')||(LA30_0 >= '\u0C8E' && LA30_0 <= '\u0C90')||(LA30_0 >= '\u0C92' && LA30_0 <= '\u0CA8')||(LA30_0 >= '\u0CAA' && LA30_0 <= '\u0CB3')||(LA30_0 >= '\u0CB5' && LA30_0 <= '\u0CB9')||(LA30_0 >= '\u0CBC' && LA30_0 <= '\u0CC4')||(LA30_0 >= '\u0CC6' && LA30_0 <= '\u0CC8')||(LA30_0 >= '\u0CCA' && LA30_0 <= '\u0CCD')||(LA30_0 >= '\u0CD5' && LA30_0 <= '\u0CD6')||LA30_0=='\u0CDE'||(LA30_0 >= '\u0CE0' && LA30_0 <= '\u0CE1')||(LA30_0 >= '\u0CE6' && LA30_0 <= '\u0CEF')||(LA30_0 >= '\u0D02' && LA30_0 <= '\u0D03')||(LA30_0 >= '\u0D05' && LA30_0 <= '\u0D0C')||(LA30_0 >= '\u0D0E' && LA30_0 <= '\u0D10')||(LA30_0 >= '\u0D12' && LA30_0 <= '\u0D28')||(LA30_0 >= '\u0D2A' && LA30_0 <= '\u0D39')||(LA30_0 >= '\u0D3E' && LA30_0 <= '\u0D43')||(LA30_0 >= '\u0D46' && LA30_0 <= '\u0D48')||(LA30_0 >= '\u0D4A' && LA30_0 <= '\u0D4D')||LA30_0=='\u0D57'||(LA30_0 >= '\u0D60' && LA30_0 <= '\u0D61')||(LA30_0 >= '\u0D66' && LA30_0 <= '\u0D6F')||(LA30_0 >= '\u0D82' && LA30_0 <= '\u0D83')||(LA30_0 >= '\u0D85' && LA30_0 <= '\u0D96')||(LA30_0 >= '\u0D9A' && LA30_0 <= '\u0DB1')||(LA30_0 >= '\u0DB3' && LA30_0 <= '\u0DBB')||LA30_0=='\u0DBD'||(LA30_0 >= '\u0DC0' && LA30_0 <= '\u0DC6')||LA30_0=='\u0DCA'||(LA30_0 >= '\u0DCF' && LA30_0 <= '\u0DD4')||LA30_0=='\u0DD6'||(LA30_0 >= '\u0DD8' && LA30_0 <= '\u0DDF')||(LA30_0 >= '\u0DF2' && LA30_0 <= '\u0DF4')||(LA30_0 >= '\u0E01' && LA30_0 <= '\u0E3A')||(LA30_0 >= '\u0E3F' && LA30_0 <= '\u0E5B')||(LA30_0 >= '\u0E81' && LA30_0 <= '\u0E82')||LA30_0=='\u0E84'||(LA30_0 >= '\u0E87' && LA30_0 <= '\u0E88')||LA30_0=='\u0E8A'||LA30_0=='\u0E8D'||(LA30_0 >= '\u0E94' && LA30_0 <= '\u0E97')||(LA30_0 >= '\u0E99' && LA30_0 <= '\u0E9F')||(LA30_0 >= '\u0EA1' && LA30_0 <= '\u0EA3')||LA30_0=='\u0EA5'||LA30_0=='\u0EA7'||(LA30_0 >= '\u0EAA' && LA30_0 <= '\u0EAB')||(LA30_0 >= '\u0EAD' && LA30_0 <= '\u0EB9')||(LA30_0 >= '\u0EBB' && LA30_0 <= '\u0EBD')||(LA30_0 >= '\u0EC0' && LA30_0 <= '\u0EC4')||LA30_0=='\u0EC6'||(LA30_0 >= '\u0EC8' && LA30_0 <= '\u0ECD')||(LA30_0 >= '\u0ED0' && LA30_0 <= '\u0ED9')||(LA30_0 >= '\u0EDC' && LA30_0 <= '\u0EDD')||(LA30_0 >= '\u0F00' && LA30_0 <= '\u0F39')||(LA30_0 >= '\u0F3E' && LA30_0 <= '\u0F47')||(LA30_0 >= '\u0F49' && LA30_0 <= '\u0F6A')||(LA30_0 >= '\u0F71' && LA30_0 <= '\u0F8B')||(LA30_0 >= '\u0F90' && LA30_0 <= '\u0F97')||(LA30_0 >= '\u0F99' && LA30_0 <= '\u0FBC')||(LA30_0 >= '\u0FBE' && LA30_0 <= '\u0FCC')||LA30_0=='\u0FCF'||(LA30_0 >= '\u1000' && LA30_0 <= '\u1021')||(LA30_0 >= '\u1023' && LA30_0 <= '\u1027')||(LA30_0 >= '\u1029' && LA30_0 <= '\u102A')||(LA30_0 >= '\u102C' && LA30_0 <= '\u1032')||(LA30_0 >= '\u1036' && LA30_0 <= '\u1039')||(LA30_0 >= '\u1040' && LA30_0 <= '\u1059')||(LA30_0 >= '\u10A0' && LA30_0 <= '\u10C5')||(LA30_0 >= '\u10D0' && LA30_0 <= '\u10F8')||LA30_0=='\u10FB'||(LA30_0 >= '\u1100' && LA30_0 <= '\u1159')||(LA30_0 >= '\u115F' && LA30_0 <= '\u11A2')||(LA30_0 >= '\u11A8' && LA30_0 <= '\u11F9')||(LA30_0 >= '\u1200' && LA30_0 <= '\u1206')||(LA30_0 >= '\u1208' && LA30_0 <= '\u1246')||LA30_0=='\u1248'||(LA30_0 >= '\u124A' && LA30_0 <= '\u124D')||(LA30_0 >= '\u1250' && LA30_0 <= '\u1256')||LA30_0=='\u1258'||(LA30_0 >= '\u125A' && LA30_0 <= '\u125D')||(LA30_0 >= '\u1260' && LA30_0 <= '\u1286')||LA30_0=='\u1288'||(LA30_0 >= '\u128A' && LA30_0 <= '\u128D')||(LA30_0 >= '\u1290' && LA30_0 <= '\u12AE')||LA30_0=='\u12B0'||(LA30_0 >= '\u12B2' && LA30_0 <= '\u12B5')||(LA30_0 >= '\u12B8' && LA30_0 <= '\u12BE')||LA30_0=='\u12C0'||(LA30_0 >= '\u12C2' && LA30_0 <= '\u12C5')||(LA30_0 >= '\u12C8' && LA30_0 <= '\u12CE')||(LA30_0 >= '\u12D0' && LA30_0 <= '\u12D6')||(LA30_0 >= '\u12D8' && LA30_0 <= '\u12EE')||(LA30_0 >= '\u12F0' && LA30_0 <= '\u130E')||LA30_0=='\u1310'||(LA30_0 >= '\u1312' && LA30_0 <= '\u1315')||(LA30_0 >= '\u1318' && LA30_0 <= '\u131E')||(LA30_0 >= '\u1320' && LA30_0 <= '\u1346')||(LA30_0 >= '\u1348' && LA30_0 <= '\u135A')||(LA30_0 >= '\u1361' && LA30_0 <= '\u137C')||(LA30_0 >= '\u13A0' && LA30_0 <= '\u13F4')||(LA30_0 >= '\u1401' && LA30_0 <= '\u1676')||(LA30_0 >= '\u1681' && LA30_0 <= '\u169A')||(LA30_0 >= '\u16A0' && LA30_0 <= '\u16F0')||(LA30_0 >= '\u1700' && LA30_0 <= '\u170C')||(LA30_0 >= '\u170E' && LA30_0 <= '\u1714')||(LA30_0 >= '\u1720' && LA30_0 <= '\u1736')||(LA30_0 >= '\u1740' && LA30_0 <= '\u1753')||(LA30_0 >= '\u1760' && LA30_0 <= '\u176C')||(LA30_0 >= '\u176E' && LA30_0 <= '\u1770')||(LA30_0 >= '\u1772' && LA30_0 <= '\u1773')||(LA30_0 >= '\u1780' && LA30_0 <= '\u17B3')||(LA30_0 >= '\u17B6' && LA30_0 <= '\u17DD')||(LA30_0 >= '\u17E0' && LA30_0 <= '\u17E9')||(LA30_0 >= '\u17F0' && LA30_0 <= '\u17F9')||(LA30_0 >= '\u1800' && LA30_0 <= '\u180D')||(LA30_0 >= '\u1810' && LA30_0 <= '\u1819')||(LA30_0 >= '\u1820' && LA30_0 <= '\u1877')||(LA30_0 >= '\u1880' && LA30_0 <= '\u18A9')||(LA30_0 >= '\u1900' && LA30_0 <= '\u191C')||(LA30_0 >= '\u1920' && LA30_0 <= '\u192B')||(LA30_0 >= '\u1930' && LA30_0 <= '\u193B')||LA30_0=='\u1940'||(LA30_0 >= '\u1944' && LA30_0 <= '\u196D')||(LA30_0 >= '\u1970' && LA30_0 <= '\u1974')||(LA30_0 >= '\u19E0' && LA30_0 <= '\u19FF')||(LA30_0 >= '\u1D00' && LA30_0 <= '\u1D6B')||(LA30_0 >= '\u1E00' && LA30_0 <= '\u1E9B')||(LA30_0 >= '\u1EA0' && LA30_0 <= '\u1EF9')||(LA30_0 >= '\u1F00' && LA30_0 <= '\u1F15')||(LA30_0 >= '\u1F18' && LA30_0 <= '\u1F1D')||(LA30_0 >= '\u1F20' && LA30_0 <= '\u1F45')||(LA30_0 >= '\u1F48' && LA30_0 <= '\u1F4D')||(LA30_0 >= '\u1F50' && LA30_0 <= '\u1F57')||LA30_0=='\u1F59'||LA30_0=='\u1F5B'||LA30_0=='\u1F5D'||(LA30_0 >= '\u1F5F' && LA30_0 <= '\u1F7D')||(LA30_0 >= '\u1F80' && LA30_0 <= '\u1FB4')||(LA30_0 >= '\u1FB6' && LA30_0 <= '\u1FBC')||LA30_0=='\u1FBE'||(LA30_0 >= '\u1FC2' && LA30_0 <= '\u1FC4')||(LA30_0 >= '\u1FC6' && LA30_0 <= '\u1FCC')||(LA30_0 >= '\u1FD0' && LA30_0 <= '\u1FD3')||(LA30_0 >= '\u1FD6' && LA30_0 <= '\u1FDB')||(LA30_0 >= '\u1FE0' && LA30_0 <= '\u1FEC')||(LA30_0 >= '\u1FF2' && LA30_0 <= '\u1FF4')||(LA30_0 >= '\u1FF6' && LA30_0 <= '\u1FFC')||(LA30_0 >= '\u2010' && LA30_0 <= '\u2017')||(LA30_0 >= '\u2020' && LA30_0 <= '\u2027')||(LA30_0 >= '\u2030' && LA30_0 <= '\u2038')||(LA30_0 >= '\u203B' && LA30_0 <= '\u2044')||(LA30_0 >= '\u2047' && LA30_0 <= '\u2054')||LA30_0=='\u2057'||(LA30_0 >= '\u2070' && LA30_0 <= '\u2071')||(LA30_0 >= '\u2074' && LA30_0 <= '\u207C')||(LA30_0 >= '\u207F' && LA30_0 <= '\u208C')||(LA30_0 >= '\u20A0' && LA30_0 <= '\u20B1')||(LA30_0 >= '\u20D0' && LA30_0 <= '\u20EA')||(LA30_0 >= '\u2100' && LA30_0 <= '\u213B')||(LA30_0 >= '\u213D' && LA30_0 <= '\u214B')||(LA30_0 >= '\u2153' && LA30_0 <= '\u2183')||(LA30_0 >= '\u2190' && LA30_0 <= '\u2328')||(LA30_0 >= '\u232B' && LA30_0 <= '\u23B3')||(LA30_0 >= '\u23B6' && LA30_0 <= '\u23D0')||(LA30_0 >= '\u2400' && LA30_0 <= '\u2426')||(LA30_0 >= '\u2440' && LA30_0 <= '\u244A')||(LA30_0 >= '\u2460' && LA30_0 <= '\u2617')||(LA30_0 >= '\u2619' && LA30_0 <= '\u267D')||(LA30_0 >= '\u2680' && LA30_0 <= '\u2691')||(LA30_0 >= '\u26A0' && LA30_0 <= '\u26A1')||(LA30_0 >= '\u2701' && LA30_0 <= '\u2704')||(LA30_0 >= '\u2706' && LA30_0 <= '\u2709')||(LA30_0 >= '\u270C' && LA30_0 <= '\u2727')||(LA30_0 >= '\u2729' && LA30_0 <= '\u274B')||LA30_0=='\u274D'||(LA30_0 >= '\u274F' && LA30_0 <= '\u2752')||LA30_0=='\u2756'||(LA30_0 >= '\u2758' && LA30_0 <= '\u275E')||(LA30_0 >= '\u2761' && LA30_0 <= '\u2767')||(LA30_0 >= '\u2776' && LA30_0 <= '\u2794')||(LA30_0 >= '\u2798' && LA30_0 <= '\u27AF')||(LA30_0 >= '\u27B1' && LA30_0 <= '\u27BE')||(LA30_0 >= '\u27D0' && LA30_0 <= '\u27E5')||(LA30_0 >= '\u27F0' && LA30_0 <= '\u2982')||(LA30_0 >= '\u2999' && LA30_0 <= '\u29D7')||(LA30_0 >= '\u29DC' && LA30_0 <= '\u29FB')||(LA30_0 >= '\u29FE' && LA30_0 <= '\u2B0D')||(LA30_0 >= '\u2E80' && LA30_0 <= '\u2E99')||(LA30_0 >= '\u2E9B' && LA30_0 <= '\u2EF3')||(LA30_0 >= '\u2F00' && LA30_0 <= '\u2FD5')||(LA30_0 >= '\u2FF0' && LA30_0 <= '\u2FFB')||(LA30_0 >= '\u3001' && LA30_0 <= '\u3007')||(LA30_0 >= '\u3012' && LA30_0 <= '\u3013')||LA30_0=='\u301C'||(LA30_0 >= '\u3020' && LA30_0 <= '\u303F')||(LA30_0 >= '\u3041' && LA30_0 <= '\u3096')||(LA30_0 >= '\u3099' && LA30_0 <= '\u309A')||(LA30_0 >= '\u309D' && LA30_0 <= '\u30FF')||(LA30_0 >= '\u3105' && LA30_0 <= '\u312C')||(LA30_0 >= '\u3131' && LA30_0 <= '\u318E')||(LA30_0 >= '\u3190' && LA30_0 <= '\u31B7')||(LA30_0 >= '\u31F0' && LA30_0 <= '\u321E')||(LA30_0 >= '\u3220' && LA30_0 <= '\u3243')||(LA30_0 >= '\u3250' && LA30_0 <= '\u327D')||(LA30_0 >= '\u327F' && LA30_0 <= '\u32FE')||(LA30_0 >= '\u3300' && LA30_0 <= '\u4DB5')||(LA30_0 >= '\u4DC0' && LA30_0 <= '\u9FA5')||(LA30_0 >= '\uA000' && LA30_0 <= '\uA48C')||(LA30_0 >= '\uA490' && LA30_0 <= '\uA4C6')||(LA30_0 >= '\uAC00' && LA30_0 <= '\uD7A3')||(LA30_0 >= '\uF900' && LA30_0 <= '\uFA2D')||(LA30_0 >= '\uFA30' && LA30_0 <= '\uFA6A')||(LA30_0 >= '\uFB00' && LA30_0 <= '\uFB06')||(LA30_0 >= '\uFB13' && LA30_0 <= '\uFB17')||(LA30_0 >= '\uFB1D' && LA30_0 <= '\uFB36')||(LA30_0 >= '\uFB38' && LA30_0 <= '\uFB3C')||LA30_0=='\uFB3E'||(LA30_0 >= '\uFB40' && LA30_0 <= '\uFB41')||(LA30_0 >= '\uFB43' && LA30_0 <= '\uFB44')||(LA30_0 >= '\uFB46' && LA30_0 <= '\uFBB1')||(LA30_0 >= '\uFBD3' && LA30_0 <= '\uFD3D')||(LA30_0 >= '\uFD50' && LA30_0 <= '\uFD8F')||(LA30_0 >= '\uFD92' && LA30_0 <= '\uFDC7')||(LA30_0 >= '\uFDF0' && LA30_0 <= '\uFDFD')||(LA30_0 >= '\uFE00' && LA30_0 <= '\uFE0F')||(LA30_0 >= '\uFE20' && LA30_0 <= '\uFE23')||(LA30_0 >= '\uFE30' && LA30_0 <= '\uFE34')||(LA30_0 >= '\uFE45' && LA30_0 <= '\uFE46')||(LA30_0 >= '\uFE49' && LA30_0 <= '\uFE52')||(LA30_0 >= '\uFE54' && LA30_0 <= '\uFE58')||(LA30_0 >= '\uFE5F' && LA30_0 <= '\uFE66')||(LA30_0 >= '\uFE68' && LA30_0 <= '\uFE6B')||(LA30_0 >= '\uFE70' && LA30_0 <= '\uFE74')||(LA30_0 >= '\uFE76' && LA30_0 <= '\uFEFC')||(LA30_0 >= '\uFF01' && LA30_0 <= '\uFF07')||(LA30_0 >= '\uFF0A' && LA30_0 <= '\uFF3A')||LA30_0=='\uFF3C'||LA30_0=='\uFF3F'||(LA30_0 >= '\uFF41' && LA30_0 <= '\uFF5A')||LA30_0=='\uFF5C'||LA30_0=='\uFF5E'||LA30_0=='\uFF61'||(LA30_0 >= '\uFF64' && LA30_0 <= '\uFFBE')||(LA30_0 >= '\uFFC2' && LA30_0 <= '\uFFC7')||(LA30_0 >= '\uFFCA' && LA30_0 <= '\uFFCF')||(LA30_0 >= '\uFFD2' && LA30_0 <= '\uFFD7')||(LA30_0 >= '\uFFDA' && LA30_0 <= '\uFFDC')||(LA30_0 >= '\uFFE0' && LA30_0 <= '\uFFE2')||(LA30_0 >= '\uFFE4' && LA30_0 <= '\uFFE6')||(LA30_0 >= '\uFFE8' && LA30_0 <= '\uFFEE')) ) {
                    alt30=2;
                }


                switch (alt30) {
            	case 1 :
            	    // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1448:17: F_ESC
            	    {
            	    mF_ESC(); if (state.failed) return ;


            	    }
            	    break;
            	case 2 :
            	    // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1449:19: IN_WORD
            	    {
            	    mIN_WORD(); if (state.failed) return ;


            	    }
            	    break;

            	default :
            	    break loop30;
                }
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "FTSWORD"

    // $ANTLR start "FTSPRE"
    public final void mFTSPRE() throws RecognitionException {
        try {
            int _type = FTSPRE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1454:9: ( ( F_ESC | START_WORD ) ( F_ESC | IN_WORD )* STAR )
            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1455:9: ( F_ESC | START_WORD ) ( F_ESC | IN_WORD )* STAR
            {
            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1455:9: ( F_ESC | START_WORD )
            int alt31=2;
            int LA31_0 = input.LA(1);

            if ( (LA31_0=='\\') ) {
                alt31=1;
            }
            else if ( (LA31_0=='$'||(LA31_0 >= '0' && LA31_0 <= '9')||(LA31_0 >= 'A' && LA31_0 <= 'Z')||(LA31_0 >= 'a' && LA31_0 <= 'z')||(LA31_0 >= '\u00A2' && LA31_0 <= '\u00A7')||(LA31_0 >= '\u00A9' && LA31_0 <= '\u00AA')||LA31_0=='\u00AE'||LA31_0=='\u00B0'||(LA31_0 >= '\u00B2' && LA31_0 <= '\u00B3')||(LA31_0 >= '\u00B5' && LA31_0 <= '\u00B6')||(LA31_0 >= '\u00B9' && LA31_0 <= '\u00BA')||(LA31_0 >= '\u00BC' && LA31_0 <= '\u00BE')||(LA31_0 >= '\u00C0' && LA31_0 <= '\u00D6')||(LA31_0 >= '\u00D8' && LA31_0 <= '\u00F6')||(LA31_0 >= '\u00F8' && LA31_0 <= '\u0236')||(LA31_0 >= '\u0250' && LA31_0 <= '\u02C1')||(LA31_0 >= '\u02C6' && LA31_0 <= '\u02D1')||(LA31_0 >= '\u02E0' && LA31_0 <= '\u02E4')||LA31_0=='\u02EE'||(LA31_0 >= '\u0300' && LA31_0 <= '\u0357')||(LA31_0 >= '\u035D' && LA31_0 <= '\u036F')||LA31_0=='\u037A'||LA31_0=='\u0386'||(LA31_0 >= '\u0388' && LA31_0 <= '\u038A')||LA31_0=='\u038C'||(LA31_0 >= '\u038E' && LA31_0 <= '\u03A1')||(LA31_0 >= '\u03A3' && LA31_0 <= '\u03CE')||(LA31_0 >= '\u03D0' && LA31_0 <= '\u03F5')||(LA31_0 >= '\u03F7' && LA31_0 <= '\u03FB')||(LA31_0 >= '\u0400' && LA31_0 <= '\u0486')||(LA31_0 >= '\u0488' && LA31_0 <= '\u04CE')||(LA31_0 >= '\u04D0' && LA31_0 <= '\u04F5')||(LA31_0 >= '\u04F8' && LA31_0 <= '\u04F9')||(LA31_0 >= '\u0500' && LA31_0 <= '\u050F')||(LA31_0 >= '\u0531' && LA31_0 <= '\u0556')||LA31_0=='\u0559'||(LA31_0 >= '\u0561' && LA31_0 <= '\u0587')||(LA31_0 >= '\u0591' && LA31_0 <= '\u05A1')||(LA31_0 >= '\u05A3' && LA31_0 <= '\u05B9')||(LA31_0 >= '\u05BB' && LA31_0 <= '\u05BD')||LA31_0=='\u05BF'||(LA31_0 >= '\u05C1' && LA31_0 <= '\u05C2')||LA31_0=='\u05C4'||(LA31_0 >= '\u05D0' && LA31_0 <= '\u05EA')||(LA31_0 >= '\u05F0' && LA31_0 <= '\u05F2')||(LA31_0 >= '\u060E' && LA31_0 <= '\u0615')||(LA31_0 >= '\u0621' && LA31_0 <= '\u063A')||(LA31_0 >= '\u0640' && LA31_0 <= '\u0658')||(LA31_0 >= '\u0660' && LA31_0 <= '\u0669')||(LA31_0 >= '\u066E' && LA31_0 <= '\u06D3')||(LA31_0 >= '\u06D5' && LA31_0 <= '\u06DC')||(LA31_0 >= '\u06DE' && LA31_0 <= '\u06FF')||(LA31_0 >= '\u0710' && LA31_0 <= '\u074A')||(LA31_0 >= '\u074D' && LA31_0 <= '\u074F')||(LA31_0 >= '\u0780' && LA31_0 <= '\u07B1')||(LA31_0 >= '\u0901' && LA31_0 <= '\u0939')||(LA31_0 >= '\u093C' && LA31_0 <= '\u094D')||(LA31_0 >= '\u0950' && LA31_0 <= '\u0954')||(LA31_0 >= '\u0958' && LA31_0 <= '\u0963')||(LA31_0 >= '\u0966' && LA31_0 <= '\u096F')||(LA31_0 >= '\u0981' && LA31_0 <= '\u0983')||(LA31_0 >= '\u0985' && LA31_0 <= '\u098C')||(LA31_0 >= '\u098F' && LA31_0 <= '\u0990')||(LA31_0 >= '\u0993' && LA31_0 <= '\u09A8')||(LA31_0 >= '\u09AA' && LA31_0 <= '\u09B0')||LA31_0=='\u09B2'||(LA31_0 >= '\u09B6' && LA31_0 <= '\u09B9')||(LA31_0 >= '\u09BC' && LA31_0 <= '\u09C4')||(LA31_0 >= '\u09C7' && LA31_0 <= '\u09C8')||(LA31_0 >= '\u09CB' && LA31_0 <= '\u09CD')||LA31_0=='\u09D7'||(LA31_0 >= '\u09DC' && LA31_0 <= '\u09DD')||(LA31_0 >= '\u09DF' && LA31_0 <= '\u09E3')||(LA31_0 >= '\u09E6' && LA31_0 <= '\u09FA')||(LA31_0 >= '\u0A01' && LA31_0 <= '\u0A03')||(LA31_0 >= '\u0A05' && LA31_0 <= '\u0A0A')||(LA31_0 >= '\u0A0F' && LA31_0 <= '\u0A10')||(LA31_0 >= '\u0A13' && LA31_0 <= '\u0A28')||(LA31_0 >= '\u0A2A' && LA31_0 <= '\u0A30')||(LA31_0 >= '\u0A32' && LA31_0 <= '\u0A33')||(LA31_0 >= '\u0A35' && LA31_0 <= '\u0A36')||(LA31_0 >= '\u0A38' && LA31_0 <= '\u0A39')||LA31_0=='\u0A3C'||(LA31_0 >= '\u0A3E' && LA31_0 <= '\u0A42')||(LA31_0 >= '\u0A47' && LA31_0 <= '\u0A48')||(LA31_0 >= '\u0A4B' && LA31_0 <= '\u0A4D')||(LA31_0 >= '\u0A59' && LA31_0 <= '\u0A5C')||LA31_0=='\u0A5E'||(LA31_0 >= '\u0A66' && LA31_0 <= '\u0A74')||(LA31_0 >= '\u0A81' && LA31_0 <= '\u0A83')||(LA31_0 >= '\u0A85' && LA31_0 <= '\u0A8D')||(LA31_0 >= '\u0A8F' && LA31_0 <= '\u0A91')||(LA31_0 >= '\u0A93' && LA31_0 <= '\u0AA8')||(LA31_0 >= '\u0AAA' && LA31_0 <= '\u0AB0')||(LA31_0 >= '\u0AB2' && LA31_0 <= '\u0AB3')||(LA31_0 >= '\u0AB5' && LA31_0 <= '\u0AB9')||(LA31_0 >= '\u0ABC' && LA31_0 <= '\u0AC5')||(LA31_0 >= '\u0AC7' && LA31_0 <= '\u0AC9')||(LA31_0 >= '\u0ACB' && LA31_0 <= '\u0ACD')||LA31_0=='\u0AD0'||(LA31_0 >= '\u0AE0' && LA31_0 <= '\u0AE3')||(LA31_0 >= '\u0AE6' && LA31_0 <= '\u0AEF')||LA31_0=='\u0AF1'||(LA31_0 >= '\u0B01' && LA31_0 <= '\u0B03')||(LA31_0 >= '\u0B05' && LA31_0 <= '\u0B0C')||(LA31_0 >= '\u0B0F' && LA31_0 <= '\u0B10')||(LA31_0 >= '\u0B13' && LA31_0 <= '\u0B28')||(LA31_0 >= '\u0B2A' && LA31_0 <= '\u0B30')||(LA31_0 >= '\u0B32' && LA31_0 <= '\u0B33')||(LA31_0 >= '\u0B35' && LA31_0 <= '\u0B39')||(LA31_0 >= '\u0B3C' && LA31_0 <= '\u0B43')||(LA31_0 >= '\u0B47' && LA31_0 <= '\u0B48')||(LA31_0 >= '\u0B4B' && LA31_0 <= '\u0B4D')||(LA31_0 >= '\u0B56' && LA31_0 <= '\u0B57')||(LA31_0 >= '\u0B5C' && LA31_0 <= '\u0B5D')||(LA31_0 >= '\u0B5F' && LA31_0 <= '\u0B61')||(LA31_0 >= '\u0B66' && LA31_0 <= '\u0B71')||(LA31_0 >= '\u0B82' && LA31_0 <= '\u0B83')||(LA31_0 >= '\u0B85' && LA31_0 <= '\u0B8A')||(LA31_0 >= '\u0B8E' && LA31_0 <= '\u0B90')||(LA31_0 >= '\u0B92' && LA31_0 <= '\u0B95')||(LA31_0 >= '\u0B99' && LA31_0 <= '\u0B9A')||LA31_0=='\u0B9C'||(LA31_0 >= '\u0B9E' && LA31_0 <= '\u0B9F')||(LA31_0 >= '\u0BA3' && LA31_0 <= '\u0BA4')||(LA31_0 >= '\u0BA8' && LA31_0 <= '\u0BAA')||(LA31_0 >= '\u0BAE' && LA31_0 <= '\u0BB5')||(LA31_0 >= '\u0BB7' && LA31_0 <= '\u0BB9')||(LA31_0 >= '\u0BBE' && LA31_0 <= '\u0BC2')||(LA31_0 >= '\u0BC6' && LA31_0 <= '\u0BC8')||(LA31_0 >= '\u0BCA' && LA31_0 <= '\u0BCD')||LA31_0=='\u0BD7'||(LA31_0 >= '\u0BE7' && LA31_0 <= '\u0BFA')||(LA31_0 >= '\u0C01' && LA31_0 <= '\u0C03')||(LA31_0 >= '\u0C05' && LA31_0 <= '\u0C0C')||(LA31_0 >= '\u0C0E' && LA31_0 <= '\u0C10')||(LA31_0 >= '\u0C12' && LA31_0 <= '\u0C28')||(LA31_0 >= '\u0C2A' && LA31_0 <= '\u0C33')||(LA31_0 >= '\u0C35' && LA31_0 <= '\u0C39')||(LA31_0 >= '\u0C3E' && LA31_0 <= '\u0C44')||(LA31_0 >= '\u0C46' && LA31_0 <= '\u0C48')||(LA31_0 >= '\u0C4A' && LA31_0 <= '\u0C4D')||(LA31_0 >= '\u0C55' && LA31_0 <= '\u0C56')||(LA31_0 >= '\u0C60' && LA31_0 <= '\u0C61')||(LA31_0 >= '\u0C66' && LA31_0 <= '\u0C6F')||(LA31_0 >= '\u0C82' && LA31_0 <= '\u0C83')||(LA31_0 >= '\u0C85' && LA31_0 <= '\u0C8C')||(LA31_0 >= '\u0C8E' && LA31_0 <= '\u0C90')||(LA31_0 >= '\u0C92' && LA31_0 <= '\u0CA8')||(LA31_0 >= '\u0CAA' && LA31_0 <= '\u0CB3')||(LA31_0 >= '\u0CB5' && LA31_0 <= '\u0CB9')||(LA31_0 >= '\u0CBC' && LA31_0 <= '\u0CC4')||(LA31_0 >= '\u0CC6' && LA31_0 <= '\u0CC8')||(LA31_0 >= '\u0CCA' && LA31_0 <= '\u0CCD')||(LA31_0 >= '\u0CD5' && LA31_0 <= '\u0CD6')||LA31_0=='\u0CDE'||(LA31_0 >= '\u0CE0' && LA31_0 <= '\u0CE1')||(LA31_0 >= '\u0CE6' && LA31_0 <= '\u0CEF')||(LA31_0 >= '\u0D02' && LA31_0 <= '\u0D03')||(LA31_0 >= '\u0D05' && LA31_0 <= '\u0D0C')||(LA31_0 >= '\u0D0E' && LA31_0 <= '\u0D10')||(LA31_0 >= '\u0D12' && LA31_0 <= '\u0D28')||(LA31_0 >= '\u0D2A' && LA31_0 <= '\u0D39')||(LA31_0 >= '\u0D3E' && LA31_0 <= '\u0D43')||(LA31_0 >= '\u0D46' && LA31_0 <= '\u0D48')||(LA31_0 >= '\u0D4A' && LA31_0 <= '\u0D4D')||LA31_0=='\u0D57'||(LA31_0 >= '\u0D60' && LA31_0 <= '\u0D61')||(LA31_0 >= '\u0D66' && LA31_0 <= '\u0D6F')||(LA31_0 >= '\u0D82' && LA31_0 <= '\u0D83')||(LA31_0 >= '\u0D85' && LA31_0 <= '\u0D96')||(LA31_0 >= '\u0D9A' && LA31_0 <= '\u0DB1')||(LA31_0 >= '\u0DB3' && LA31_0 <= '\u0DBB')||LA31_0=='\u0DBD'||(LA31_0 >= '\u0DC0' && LA31_0 <= '\u0DC6')||LA31_0=='\u0DCA'||(LA31_0 >= '\u0DCF' && LA31_0 <= '\u0DD4')||LA31_0=='\u0DD6'||(LA31_0 >= '\u0DD8' && LA31_0 <= '\u0DDF')||(LA31_0 >= '\u0DF2' && LA31_0 <= '\u0DF3')||(LA31_0 >= '\u0E01' && LA31_0 <= '\u0E3A')||(LA31_0 >= '\u0E3F' && LA31_0 <= '\u0E4E')||(LA31_0 >= '\u0E50' && LA31_0 <= '\u0E59')||(LA31_0 >= '\u0E81' && LA31_0 <= '\u0E82')||LA31_0=='\u0E84'||(LA31_0 >= '\u0E87' && LA31_0 <= '\u0E88')||LA31_0=='\u0E8A'||LA31_0=='\u0E8D'||(LA31_0 >= '\u0E94' && LA31_0 <= '\u0E97')||(LA31_0 >= '\u0E99' && LA31_0 <= '\u0E9F')||(LA31_0 >= '\u0EA1' && LA31_0 <= '\u0EA3')||LA31_0=='\u0EA5'||LA31_0=='\u0EA7'||(LA31_0 >= '\u0EAA' && LA31_0 <= '\u0EAB')||(LA31_0 >= '\u0EAD' && LA31_0 <= '\u0EB9')||(LA31_0 >= '\u0EBB' && LA31_0 <= '\u0EBD')||(LA31_0 >= '\u0EC0' && LA31_0 <= '\u0EC4')||LA31_0=='\u0EC6'||(LA31_0 >= '\u0EC8' && LA31_0 <= '\u0ECD')||(LA31_0 >= '\u0ED0' && LA31_0 <= '\u0ED9')||(LA31_0 >= '\u0EDC' && LA31_0 <= '\u0EDD')||(LA31_0 >= '\u0F00' && LA31_0 <= '\u0F03')||(LA31_0 >= '\u0F13' && LA31_0 <= '\u0F39')||(LA31_0 >= '\u0F3E' && LA31_0 <= '\u0F47')||(LA31_0 >= '\u0F49' && LA31_0 <= '\u0F6A')||(LA31_0 >= '\u0F71' && LA31_0 <= '\u0F84')||(LA31_0 >= '\u0F86' && LA31_0 <= '\u0F8B')||(LA31_0 >= '\u0F90' && LA31_0 <= '\u0F97')||(LA31_0 >= '\u0F99' && LA31_0 <= '\u0FBC')||(LA31_0 >= '\u0FBE' && LA31_0 <= '\u0FCC')||LA31_0=='\u0FCF'||(LA31_0 >= '\u1000' && LA31_0 <= '\u1021')||(LA31_0 >= '\u1023' && LA31_0 <= '\u1027')||(LA31_0 >= '\u1029' && LA31_0 <= '\u102A')||(LA31_0 >= '\u102C' && LA31_0 <= '\u1032')||(LA31_0 >= '\u1036' && LA31_0 <= '\u1039')||(LA31_0 >= '\u1040' && LA31_0 <= '\u1049')||(LA31_0 >= '\u1050' && LA31_0 <= '\u1059')||(LA31_0 >= '\u10A0' && LA31_0 <= '\u10C5')||(LA31_0 >= '\u10D0' && LA31_0 <= '\u10F8')||(LA31_0 >= '\u1100' && LA31_0 <= '\u1159')||(LA31_0 >= '\u115F' && LA31_0 <= '\u11A2')||(LA31_0 >= '\u11A8' && LA31_0 <= '\u11F9')||(LA31_0 >= '\u1200' && LA31_0 <= '\u1206')||(LA31_0 >= '\u1208' && LA31_0 <= '\u1246')||LA31_0=='\u1248'||(LA31_0 >= '\u124A' && LA31_0 <= '\u124D')||(LA31_0 >= '\u1250' && LA31_0 <= '\u1256')||LA31_0=='\u1258'||(LA31_0 >= '\u125A' && LA31_0 <= '\u125D')||(LA31_0 >= '\u1260' && LA31_0 <= '\u1286')||LA31_0=='\u1288'||(LA31_0 >= '\u128A' && LA31_0 <= '\u128D')||(LA31_0 >= '\u1290' && LA31_0 <= '\u12AE')||LA31_0=='\u12B0'||(LA31_0 >= '\u12B2' && LA31_0 <= '\u12B5')||(LA31_0 >= '\u12B8' && LA31_0 <= '\u12BE')||LA31_0=='\u12C0'||(LA31_0 >= '\u12C2' && LA31_0 <= '\u12C5')||(LA31_0 >= '\u12C8' && LA31_0 <= '\u12CE')||(LA31_0 >= '\u12D0' && LA31_0 <= '\u12D6')||(LA31_0 >= '\u12D8' && LA31_0 <= '\u12EE')||(LA31_0 >= '\u12F0' && LA31_0 <= '\u130E')||LA31_0=='\u1310'||(LA31_0 >= '\u1312' && LA31_0 <= '\u1315')||(LA31_0 >= '\u1318' && LA31_0 <= '\u131E')||(LA31_0 >= '\u1320' && LA31_0 <= '\u1346')||(LA31_0 >= '\u1348' && LA31_0 <= '\u135A')||(LA31_0 >= '\u1369' && LA31_0 <= '\u137C')||(LA31_0 >= '\u13A0' && LA31_0 <= '\u13F4')||(LA31_0 >= '\u1401' && LA31_0 <= '\u166C')||(LA31_0 >= '\u166F' && LA31_0 <= '\u1676')||(LA31_0 >= '\u1681' && LA31_0 <= '\u169A')||(LA31_0 >= '\u16A0' && LA31_0 <= '\u16EA')||(LA31_0 >= '\u16EE' && LA31_0 <= '\u16F0')||(LA31_0 >= '\u1700' && LA31_0 <= '\u170C')||(LA31_0 >= '\u170E' && LA31_0 <= '\u1714')||(LA31_0 >= '\u1720' && LA31_0 <= '\u1734')||(LA31_0 >= '\u1740' && LA31_0 <= '\u1753')||(LA31_0 >= '\u1760' && LA31_0 <= '\u176C')||(LA31_0 >= '\u176E' && LA31_0 <= '\u1770')||(LA31_0 >= '\u1772' && LA31_0 <= '\u1773')||(LA31_0 >= '\u1780' && LA31_0 <= '\u17B3')||(LA31_0 >= '\u17B6' && LA31_0 <= '\u17D3')||LA31_0=='\u17D7'||(LA31_0 >= '\u17DB' && LA31_0 <= '\u17DD')||(LA31_0 >= '\u17E0' && LA31_0 <= '\u17E9')||(LA31_0 >= '\u17F0' && LA31_0 <= '\u17F9')||(LA31_0 >= '\u180B' && LA31_0 <= '\u180D')||(LA31_0 >= '\u1810' && LA31_0 <= '\u1819')||(LA31_0 >= '\u1820' && LA31_0 <= '\u1877')||(LA31_0 >= '\u1880' && LA31_0 <= '\u18A9')||(LA31_0 >= '\u1900' && LA31_0 <= '\u191C')||(LA31_0 >= '\u1920' && LA31_0 <= '\u192B')||(LA31_0 >= '\u1930' && LA31_0 <= '\u193B')||LA31_0=='\u1940'||(LA31_0 >= '\u1946' && LA31_0 <= '\u196D')||(LA31_0 >= '\u1970' && LA31_0 <= '\u1974')||(LA31_0 >= '\u19E0' && LA31_0 <= '\u19FF')||(LA31_0 >= '\u1D00' && LA31_0 <= '\u1D6B')||(LA31_0 >= '\u1E00' && LA31_0 <= '\u1E9B')||(LA31_0 >= '\u1EA0' && LA31_0 <= '\u1EF9')||(LA31_0 >= '\u1F00' && LA31_0 <= '\u1F15')||(LA31_0 >= '\u1F18' && LA31_0 <= '\u1F1D')||(LA31_0 >= '\u1F20' && LA31_0 <= '\u1F45')||(LA31_0 >= '\u1F48' && LA31_0 <= '\u1F4D')||(LA31_0 >= '\u1F50' && LA31_0 <= '\u1F57')||LA31_0=='\u1F59'||LA31_0=='\u1F5B'||LA31_0=='\u1F5D'||(LA31_0 >= '\u1F5F' && LA31_0 <= '\u1F7D')||(LA31_0 >= '\u1F80' && LA31_0 <= '\u1FB4')||(LA31_0 >= '\u1FB6' && LA31_0 <= '\u1FBC')||LA31_0=='\u1FBE'||(LA31_0 >= '\u1FC2' && LA31_0 <= '\u1FC4')||(LA31_0 >= '\u1FC6' && LA31_0 <= '\u1FCC')||(LA31_0 >= '\u1FD0' && LA31_0 <= '\u1FD3')||(LA31_0 >= '\u1FD6' && LA31_0 <= '\u1FDB')||(LA31_0 >= '\u1FE0' && LA31_0 <= '\u1FEC')||(LA31_0 >= '\u1FF2' && LA31_0 <= '\u1FF4')||(LA31_0 >= '\u1FF6' && LA31_0 <= '\u1FFC')||(LA31_0 >= '\u2070' && LA31_0 <= '\u2071')||(LA31_0 >= '\u2074' && LA31_0 <= '\u2079')||(LA31_0 >= '\u207F' && LA31_0 <= '\u2089')||(LA31_0 >= '\u20A0' && LA31_0 <= '\u20B1')||(LA31_0 >= '\u20D0' && LA31_0 <= '\u20EA')||(LA31_0 >= '\u2100' && LA31_0 <= '\u213B')||(LA31_0 >= '\u213D' && LA31_0 <= '\u213F')||(LA31_0 >= '\u2145' && LA31_0 <= '\u214A')||(LA31_0 >= '\u2153' && LA31_0 <= '\u2183')||(LA31_0 >= '\u2195' && LA31_0 <= '\u2199')||(LA31_0 >= '\u219C' && LA31_0 <= '\u219F')||(LA31_0 >= '\u21A1' && LA31_0 <= '\u21A2')||(LA31_0 >= '\u21A4' && LA31_0 <= '\u21A5')||(LA31_0 >= '\u21A7' && LA31_0 <= '\u21AD')||(LA31_0 >= '\u21AF' && LA31_0 <= '\u21CD')||(LA31_0 >= '\u21D0' && LA31_0 <= '\u21D1')||LA31_0=='\u21D3'||(LA31_0 >= '\u21D5' && LA31_0 <= '\u21F3')||(LA31_0 >= '\u2300' && LA31_0 <= '\u2307')||(LA31_0 >= '\u230C' && LA31_0 <= '\u231F')||(LA31_0 >= '\u2322' && LA31_0 <= '\u2328')||(LA31_0 >= '\u232B' && LA31_0 <= '\u237B')||(LA31_0 >= '\u237D' && LA31_0 <= '\u239A')||(LA31_0 >= '\u23B7' && LA31_0 <= '\u23D0')||(LA31_0 >= '\u2400' && LA31_0 <= '\u2426')||(LA31_0 >= '\u2440' && LA31_0 <= '\u244A')||(LA31_0 >= '\u2460' && LA31_0 <= '\u25B6')||(LA31_0 >= '\u25B8' && LA31_0 <= '\u25C0')||(LA31_0 >= '\u25C2' && LA31_0 <= '\u25F7')||(LA31_0 >= '\u2600' && LA31_0 <= '\u2617')||(LA31_0 >= '\u2619' && LA31_0 <= '\u266E')||(LA31_0 >= '\u2670' && LA31_0 <= '\u267D')||(LA31_0 >= '\u2680' && LA31_0 <= '\u2691')||(LA31_0 >= '\u26A0' && LA31_0 <= '\u26A1')||(LA31_0 >= '\u2701' && LA31_0 <= '\u2704')||(LA31_0 >= '\u2706' && LA31_0 <= '\u2709')||(LA31_0 >= '\u270C' && LA31_0 <= '\u2727')||(LA31_0 >= '\u2729' && LA31_0 <= '\u274B')||LA31_0=='\u274D'||(LA31_0 >= '\u274F' && LA31_0 <= '\u2752')||LA31_0=='\u2756'||(LA31_0 >= '\u2758' && LA31_0 <= '\u275E')||(LA31_0 >= '\u2761' && LA31_0 <= '\u2767')||(LA31_0 >= '\u2776' && LA31_0 <= '\u2794')||(LA31_0 >= '\u2798' && LA31_0 <= '\u27AF')||(LA31_0 >= '\u27B1' && LA31_0 <= '\u27BE')||(LA31_0 >= '\u2800' && LA31_0 <= '\u28FF')||(LA31_0 >= '\u2B00' && LA31_0 <= '\u2B0D')||(LA31_0 >= '\u2E80' && LA31_0 <= '\u2E99')||(LA31_0 >= '\u2E9B' && LA31_0 <= '\u2EF3')||(LA31_0 >= '\u2F00' && LA31_0 <= '\u2FD5')||(LA31_0 >= '\u2FF0' && LA31_0 <= '\u2FFB')||(LA31_0 >= '\u3004' && LA31_0 <= '\u3007')||(LA31_0 >= '\u3012' && LA31_0 <= '\u3013')||(LA31_0 >= '\u3020' && LA31_0 <= '\u302F')||(LA31_0 >= '\u3031' && LA31_0 <= '\u303C')||(LA31_0 >= '\u303E' && LA31_0 <= '\u303F')||(LA31_0 >= '\u3041' && LA31_0 <= '\u3096')||(LA31_0 >= '\u3099' && LA31_0 <= '\u309A')||(LA31_0 >= '\u309D' && LA31_0 <= '\u309F')||(LA31_0 >= '\u30A1' && LA31_0 <= '\u30FA')||(LA31_0 >= '\u30FC' && LA31_0 <= '\u30FF')||(LA31_0 >= '\u3105' && LA31_0 <= '\u312C')||(LA31_0 >= '\u3131' && LA31_0 <= '\u318E')||(LA31_0 >= '\u3190' && LA31_0 <= '\u31B7')||(LA31_0 >= '\u31F0' && LA31_0 <= '\u321E')||(LA31_0 >= '\u3220' && LA31_0 <= '\u3243')||(LA31_0 >= '\u3250' && LA31_0 <= '\u327D')||(LA31_0 >= '\u327F' && LA31_0 <= '\u32FE')||(LA31_0 >= '\u3300' && LA31_0 <= '\u4DB5')||(LA31_0 >= '\u4DC0' && LA31_0 <= '\u9FA5')||(LA31_0 >= '\uA000' && LA31_0 <= '\uA48C')||(LA31_0 >= '\uA490' && LA31_0 <= '\uA4C6')||(LA31_0 >= '\uAC00' && LA31_0 <= '\uD7A3')||(LA31_0 >= '\uF900' && LA31_0 <= '\uFA2D')||(LA31_0 >= '\uFA30' && LA31_0 <= '\uFA6A')||(LA31_0 >= '\uFB00' && LA31_0 <= '\uFB06')||(LA31_0 >= '\uFB13' && LA31_0 <= '\uFB17')||(LA31_0 >= '\uFB1D' && LA31_0 <= '\uFB28')||(LA31_0 >= '\uFB2A' && LA31_0 <= '\uFB36')||(LA31_0 >= '\uFB38' && LA31_0 <= '\uFB3C')||LA31_0=='\uFB3E'||(LA31_0 >= '\uFB40' && LA31_0 <= '\uFB41')||(LA31_0 >= '\uFB43' && LA31_0 <= '\uFB44')||(LA31_0 >= '\uFB46' && LA31_0 <= '\uFBB1')||(LA31_0 >= '\uFBD3' && LA31_0 <= '\uFD3D')||(LA31_0 >= '\uFD50' && LA31_0 <= '\uFD8F')||(LA31_0 >= '\uFD92' && LA31_0 <= '\uFDC7')||(LA31_0 >= '\uFDF0' && LA31_0 <= '\uFDFD')||(LA31_0 >= '\uFE00' && LA31_0 <= '\uFE0F')||(LA31_0 >= '\uFE20' && LA31_0 <= '\uFE23')||LA31_0=='\uFE69'||(LA31_0 >= '\uFE70' && LA31_0 <= '\uFE74')||(LA31_0 >= '\uFE76' && LA31_0 <= '\uFEFC')||LA31_0=='\uFF04'||(LA31_0 >= '\uFF10' && LA31_0 <= '\uFF19')||(LA31_0 >= '\uFF21' && LA31_0 <= '\uFF3A')||(LA31_0 >= '\uFF41' && LA31_0 <= '\uFF5A')||(LA31_0 >= '\uFF66' && LA31_0 <= '\uFFBE')||(LA31_0 >= '\uFFC2' && LA31_0 <= '\uFFC7')||(LA31_0 >= '\uFFCA' && LA31_0 <= '\uFFCF')||(LA31_0 >= '\uFFD2' && LA31_0 <= '\uFFD7')||(LA31_0 >= '\uFFDA' && LA31_0 <= '\uFFDC')||(LA31_0 >= '\uFFE0' && LA31_0 <= '\uFFE1')||(LA31_0 >= '\uFFE4' && LA31_0 <= '\uFFE6')||LA31_0=='\uFFE8'||(LA31_0 >= '\uFFED' && LA31_0 <= '\uFFEE')) ) {
                alt31=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 31, 0, input);

                throw nvae;

            }
            switch (alt31) {
                case 1 :
                    // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1456:17: F_ESC
                    {
                    mF_ESC(); if (state.failed) return ;


                    }
                    break;
                case 2 :
                    // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1457:19: START_WORD
                    {
                    mSTART_WORD(); if (state.failed) return ;


                    }
                    break;

            }


            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1459:9: ( F_ESC | IN_WORD )*
            loop32:
            do {
                int alt32=3;
                int LA32_0 = input.LA(1);

                if ( (LA32_0=='\\') ) {
                    alt32=1;
                }
                else if ( ((LA32_0 >= '!' && LA32_0 <= '\'')||LA32_0=='+'||LA32_0=='-'||(LA32_0 >= '/' && LA32_0 <= '9')||LA32_0==';'||LA32_0=='='||(LA32_0 >= '@' && LA32_0 <= 'Z')||LA32_0=='_'||(LA32_0 >= 'a' && LA32_0 <= 'z')||LA32_0=='|'||(LA32_0 >= '\u00A1' && LA32_0 <= '\u00A7')||(LA32_0 >= '\u00A9' && LA32_0 <= '\u00AA')||LA32_0=='\u00AC'||LA32_0=='\u00AE'||(LA32_0 >= '\u00B0' && LA32_0 <= '\u00B3')||(LA32_0 >= '\u00B5' && LA32_0 <= '\u00B7')||(LA32_0 >= '\u00B9' && LA32_0 <= '\u00BA')||(LA32_0 >= '\u00BC' && LA32_0 <= '\u0236')||(LA32_0 >= '\u0250' && LA32_0 <= '\u02C1')||(LA32_0 >= '\u02C6' && LA32_0 <= '\u02D1')||(LA32_0 >= '\u02E0' && LA32_0 <= '\u02E4')||LA32_0=='\u02EE'||(LA32_0 >= '\u0300' && LA32_0 <= '\u0357')||(LA32_0 >= '\u035D' && LA32_0 <= '\u036F')||LA32_0=='\u037A'||LA32_0=='\u037E'||(LA32_0 >= '\u0386' && LA32_0 <= '\u038A')||LA32_0=='\u038C'||(LA32_0 >= '\u038E' && LA32_0 <= '\u03A1')||(LA32_0 >= '\u03A3' && LA32_0 <= '\u03CE')||(LA32_0 >= '\u03D0' && LA32_0 <= '\u03FB')||(LA32_0 >= '\u0400' && LA32_0 <= '\u0486')||(LA32_0 >= '\u0488' && LA32_0 <= '\u04CE')||(LA32_0 >= '\u04D0' && LA32_0 <= '\u04F5')||(LA32_0 >= '\u04F8' && LA32_0 <= '\u04F9')||(LA32_0 >= '\u0500' && LA32_0 <= '\u050F')||(LA32_0 >= '\u0531' && LA32_0 <= '\u0556')||(LA32_0 >= '\u0559' && LA32_0 <= '\u055F')||(LA32_0 >= '\u0561' && LA32_0 <= '\u0587')||(LA32_0 >= '\u0589' && LA32_0 <= '\u058A')||(LA32_0 >= '\u0591' && LA32_0 <= '\u05A1')||(LA32_0 >= '\u05A3' && LA32_0 <= '\u05B9')||(LA32_0 >= '\u05BB' && LA32_0 <= '\u05C4')||(LA32_0 >= '\u05D0' && LA32_0 <= '\u05EA')||(LA32_0 >= '\u05F0' && LA32_0 <= '\u05F4')||(LA32_0 >= '\u060C' && LA32_0 <= '\u0615')||LA32_0=='\u061B'||LA32_0=='\u061F'||(LA32_0 >= '\u0621' && LA32_0 <= '\u063A')||(LA32_0 >= '\u0640' && LA32_0 <= '\u0658')||(LA32_0 >= '\u0660' && LA32_0 <= '\u06DC')||(LA32_0 >= '\u06DE' && LA32_0 <= '\u070D')||(LA32_0 >= '\u0710' && LA32_0 <= '\u074A')||(LA32_0 >= '\u074D' && LA32_0 <= '\u074F')||(LA32_0 >= '\u0780' && LA32_0 <= '\u07B1')||(LA32_0 >= '\u0901' && LA32_0 <= '\u0939')||(LA32_0 >= '\u093C' && LA32_0 <= '\u094D')||(LA32_0 >= '\u0950' && LA32_0 <= '\u0954')||(LA32_0 >= '\u0958' && LA32_0 <= '\u0970')||(LA32_0 >= '\u0981' && LA32_0 <= '\u0983')||(LA32_0 >= '\u0985' && LA32_0 <= '\u098C')||(LA32_0 >= '\u098F' && LA32_0 <= '\u0990')||(LA32_0 >= '\u0993' && LA32_0 <= '\u09A8')||(LA32_0 >= '\u09AA' && LA32_0 <= '\u09B0')||LA32_0=='\u09B2'||(LA32_0 >= '\u09B6' && LA32_0 <= '\u09B9')||(LA32_0 >= '\u09BC' && LA32_0 <= '\u09C4')||(LA32_0 >= '\u09C7' && LA32_0 <= '\u09C8')||(LA32_0 >= '\u09CB' && LA32_0 <= '\u09CD')||LA32_0=='\u09D7'||(LA32_0 >= '\u09DC' && LA32_0 <= '\u09DD')||(LA32_0 >= '\u09DF' && LA32_0 <= '\u09E3')||(LA32_0 >= '\u09E6' && LA32_0 <= '\u09FA')||(LA32_0 >= '\u0A01' && LA32_0 <= '\u0A03')||(LA32_0 >= '\u0A05' && LA32_0 <= '\u0A0A')||(LA32_0 >= '\u0A0F' && LA32_0 <= '\u0A10')||(LA32_0 >= '\u0A13' && LA32_0 <= '\u0A28')||(LA32_0 >= '\u0A2A' && LA32_0 <= '\u0A30')||(LA32_0 >= '\u0A32' && LA32_0 <= '\u0A33')||(LA32_0 >= '\u0A35' && LA32_0 <= '\u0A36')||(LA32_0 >= '\u0A38' && LA32_0 <= '\u0A39')||LA32_0=='\u0A3C'||(LA32_0 >= '\u0A3E' && LA32_0 <= '\u0A42')||(LA32_0 >= '\u0A47' && LA32_0 <= '\u0A48')||(LA32_0 >= '\u0A4B' && LA32_0 <= '\u0A4D')||(LA32_0 >= '\u0A59' && LA32_0 <= '\u0A5C')||LA32_0=='\u0A5E'||(LA32_0 >= '\u0A66' && LA32_0 <= '\u0A74')||(LA32_0 >= '\u0A81' && LA32_0 <= '\u0A83')||(LA32_0 >= '\u0A85' && LA32_0 <= '\u0A8D')||(LA32_0 >= '\u0A8F' && LA32_0 <= '\u0A91')||(LA32_0 >= '\u0A93' && LA32_0 <= '\u0AA8')||(LA32_0 >= '\u0AAA' && LA32_0 <= '\u0AB0')||(LA32_0 >= '\u0AB2' && LA32_0 <= '\u0AB3')||(LA32_0 >= '\u0AB5' && LA32_0 <= '\u0AB9')||(LA32_0 >= '\u0ABC' && LA32_0 <= '\u0AC5')||(LA32_0 >= '\u0AC7' && LA32_0 <= '\u0AC9')||(LA32_0 >= '\u0ACB' && LA32_0 <= '\u0ACD')||LA32_0=='\u0AD0'||(LA32_0 >= '\u0AE0' && LA32_0 <= '\u0AE3')||(LA32_0 >= '\u0AE6' && LA32_0 <= '\u0AEF')||LA32_0=='\u0AF1'||(LA32_0 >= '\u0B01' && LA32_0 <= '\u0B03')||(LA32_0 >= '\u0B05' && LA32_0 <= '\u0B0C')||(LA32_0 >= '\u0B0F' && LA32_0 <= '\u0B10')||(LA32_0 >= '\u0B13' && LA32_0 <= '\u0B28')||(LA32_0 >= '\u0B2A' && LA32_0 <= '\u0B30')||(LA32_0 >= '\u0B32' && LA32_0 <= '\u0B33')||(LA32_0 >= '\u0B35' && LA32_0 <= '\u0B39')||(LA32_0 >= '\u0B3C' && LA32_0 <= '\u0B43')||(LA32_0 >= '\u0B47' && LA32_0 <= '\u0B48')||(LA32_0 >= '\u0B4B' && LA32_0 <= '\u0B4D')||(LA32_0 >= '\u0B56' && LA32_0 <= '\u0B57')||(LA32_0 >= '\u0B5C' && LA32_0 <= '\u0B5D')||(LA32_0 >= '\u0B5F' && LA32_0 <= '\u0B61')||(LA32_0 >= '\u0B66' && LA32_0 <= '\u0B71')||(LA32_0 >= '\u0B82' && LA32_0 <= '\u0B83')||(LA32_0 >= '\u0B85' && LA32_0 <= '\u0B8A')||(LA32_0 >= '\u0B8E' && LA32_0 <= '\u0B90')||(LA32_0 >= '\u0B92' && LA32_0 <= '\u0B95')||(LA32_0 >= '\u0B99' && LA32_0 <= '\u0B9A')||LA32_0=='\u0B9C'||(LA32_0 >= '\u0B9E' && LA32_0 <= '\u0B9F')||(LA32_0 >= '\u0BA3' && LA32_0 <= '\u0BA4')||(LA32_0 >= '\u0BA8' && LA32_0 <= '\u0BAA')||(LA32_0 >= '\u0BAE' && LA32_0 <= '\u0BB5')||(LA32_0 >= '\u0BB7' && LA32_0 <= '\u0BB9')||(LA32_0 >= '\u0BBE' && LA32_0 <= '\u0BC2')||(LA32_0 >= '\u0BC6' && LA32_0 <= '\u0BC8')||(LA32_0 >= '\u0BCA' && LA32_0 <= '\u0BCD')||LA32_0=='\u0BD7'||(LA32_0 >= '\u0BE7' && LA32_0 <= '\u0BFA')||(LA32_0 >= '\u0C01' && LA32_0 <= '\u0C03')||(LA32_0 >= '\u0C05' && LA32_0 <= '\u0C0C')||(LA32_0 >= '\u0C0E' && LA32_0 <= '\u0C10')||(LA32_0 >= '\u0C12' && LA32_0 <= '\u0C28')||(LA32_0 >= '\u0C2A' && LA32_0 <= '\u0C33')||(LA32_0 >= '\u0C35' && LA32_0 <= '\u0C39')||(LA32_0 >= '\u0C3E' && LA32_0 <= '\u0C44')||(LA32_0 >= '\u0C46' && LA32_0 <= '\u0C48')||(LA32_0 >= '\u0C4A' && LA32_0 <= '\u0C4D')||(LA32_0 >= '\u0C55' && LA32_0 <= '\u0C56')||(LA32_0 >= '\u0C60' && LA32_0 <= '\u0C61')||(LA32_0 >= '\u0C66' && LA32_0 <= '\u0C6F')||(LA32_0 >= '\u0C82' && LA32_0 <= '\u0C83')||(LA32_0 >= '\u0C85' && LA32_0 <= '\u0C8C')||(LA32_0 >= '\u0C8E' && LA32_0 <= '\u0C90')||(LA32_0 >= '\u0C92' && LA32_0 <= '\u0CA8')||(LA32_0 >= '\u0CAA' && LA32_0 <= '\u0CB3')||(LA32_0 >= '\u0CB5' && LA32_0 <= '\u0CB9')||(LA32_0 >= '\u0CBC' && LA32_0 <= '\u0CC4')||(LA32_0 >= '\u0CC6' && LA32_0 <= '\u0CC8')||(LA32_0 >= '\u0CCA' && LA32_0 <= '\u0CCD')||(LA32_0 >= '\u0CD5' && LA32_0 <= '\u0CD6')||LA32_0=='\u0CDE'||(LA32_0 >= '\u0CE0' && LA32_0 <= '\u0CE1')||(LA32_0 >= '\u0CE6' && LA32_0 <= '\u0CEF')||(LA32_0 >= '\u0D02' && LA32_0 <= '\u0D03')||(LA32_0 >= '\u0D05' && LA32_0 <= '\u0D0C')||(LA32_0 >= '\u0D0E' && LA32_0 <= '\u0D10')||(LA32_0 >= '\u0D12' && LA32_0 <= '\u0D28')||(LA32_0 >= '\u0D2A' && LA32_0 <= '\u0D39')||(LA32_0 >= '\u0D3E' && LA32_0 <= '\u0D43')||(LA32_0 >= '\u0D46' && LA32_0 <= '\u0D48')||(LA32_0 >= '\u0D4A' && LA32_0 <= '\u0D4D')||LA32_0=='\u0D57'||(LA32_0 >= '\u0D60' && LA32_0 <= '\u0D61')||(LA32_0 >= '\u0D66' && LA32_0 <= '\u0D6F')||(LA32_0 >= '\u0D82' && LA32_0 <= '\u0D83')||(LA32_0 >= '\u0D85' && LA32_0 <= '\u0D96')||(LA32_0 >= '\u0D9A' && LA32_0 <= '\u0DB1')||(LA32_0 >= '\u0DB3' && LA32_0 <= '\u0DBB')||LA32_0=='\u0DBD'||(LA32_0 >= '\u0DC0' && LA32_0 <= '\u0DC6')||LA32_0=='\u0DCA'||(LA32_0 >= '\u0DCF' && LA32_0 <= '\u0DD4')||LA32_0=='\u0DD6'||(LA32_0 >= '\u0DD8' && LA32_0 <= '\u0DDF')||(LA32_0 >= '\u0DF2' && LA32_0 <= '\u0DF4')||(LA32_0 >= '\u0E01' && LA32_0 <= '\u0E3A')||(LA32_0 >= '\u0E3F' && LA32_0 <= '\u0E5B')||(LA32_0 >= '\u0E81' && LA32_0 <= '\u0E82')||LA32_0=='\u0E84'||(LA32_0 >= '\u0E87' && LA32_0 <= '\u0E88')||LA32_0=='\u0E8A'||LA32_0=='\u0E8D'||(LA32_0 >= '\u0E94' && LA32_0 <= '\u0E97')||(LA32_0 >= '\u0E99' && LA32_0 <= '\u0E9F')||(LA32_0 >= '\u0EA1' && LA32_0 <= '\u0EA3')||LA32_0=='\u0EA5'||LA32_0=='\u0EA7'||(LA32_0 >= '\u0EAA' && LA32_0 <= '\u0EAB')||(LA32_0 >= '\u0EAD' && LA32_0 <= '\u0EB9')||(LA32_0 >= '\u0EBB' && LA32_0 <= '\u0EBD')||(LA32_0 >= '\u0EC0' && LA32_0 <= '\u0EC4')||LA32_0=='\u0EC6'||(LA32_0 >= '\u0EC8' && LA32_0 <= '\u0ECD')||(LA32_0 >= '\u0ED0' && LA32_0 <= '\u0ED9')||(LA32_0 >= '\u0EDC' && LA32_0 <= '\u0EDD')||(LA32_0 >= '\u0F00' && LA32_0 <= '\u0F39')||(LA32_0 >= '\u0F3E' && LA32_0 <= '\u0F47')||(LA32_0 >= '\u0F49' && LA32_0 <= '\u0F6A')||(LA32_0 >= '\u0F71' && LA32_0 <= '\u0F8B')||(LA32_0 >= '\u0F90' && LA32_0 <= '\u0F97')||(LA32_0 >= '\u0F99' && LA32_0 <= '\u0FBC')||(LA32_0 >= '\u0FBE' && LA32_0 <= '\u0FCC')||LA32_0=='\u0FCF'||(LA32_0 >= '\u1000' && LA32_0 <= '\u1021')||(LA32_0 >= '\u1023' && LA32_0 <= '\u1027')||(LA32_0 >= '\u1029' && LA32_0 <= '\u102A')||(LA32_0 >= '\u102C' && LA32_0 <= '\u1032')||(LA32_0 >= '\u1036' && LA32_0 <= '\u1039')||(LA32_0 >= '\u1040' && LA32_0 <= '\u1059')||(LA32_0 >= '\u10A0' && LA32_0 <= '\u10C5')||(LA32_0 >= '\u10D0' && LA32_0 <= '\u10F8')||LA32_0=='\u10FB'||(LA32_0 >= '\u1100' && LA32_0 <= '\u1159')||(LA32_0 >= '\u115F' && LA32_0 <= '\u11A2')||(LA32_0 >= '\u11A8' && LA32_0 <= '\u11F9')||(LA32_0 >= '\u1200' && LA32_0 <= '\u1206')||(LA32_0 >= '\u1208' && LA32_0 <= '\u1246')||LA32_0=='\u1248'||(LA32_0 >= '\u124A' && LA32_0 <= '\u124D')||(LA32_0 >= '\u1250' && LA32_0 <= '\u1256')||LA32_0=='\u1258'||(LA32_0 >= '\u125A' && LA32_0 <= '\u125D')||(LA32_0 >= '\u1260' && LA32_0 <= '\u1286')||LA32_0=='\u1288'||(LA32_0 >= '\u128A' && LA32_0 <= '\u128D')||(LA32_0 >= '\u1290' && LA32_0 <= '\u12AE')||LA32_0=='\u12B0'||(LA32_0 >= '\u12B2' && LA32_0 <= '\u12B5')||(LA32_0 >= '\u12B8' && LA32_0 <= '\u12BE')||LA32_0=='\u12C0'||(LA32_0 >= '\u12C2' && LA32_0 <= '\u12C5')||(LA32_0 >= '\u12C8' && LA32_0 <= '\u12CE')||(LA32_0 >= '\u12D0' && LA32_0 <= '\u12D6')||(LA32_0 >= '\u12D8' && LA32_0 <= '\u12EE')||(LA32_0 >= '\u12F0' && LA32_0 <= '\u130E')||LA32_0=='\u1310'||(LA32_0 >= '\u1312' && LA32_0 <= '\u1315')||(LA32_0 >= '\u1318' && LA32_0 <= '\u131E')||(LA32_0 >= '\u1320' && LA32_0 <= '\u1346')||(LA32_0 >= '\u1348' && LA32_0 <= '\u135A')||(LA32_0 >= '\u1361' && LA32_0 <= '\u137C')||(LA32_0 >= '\u13A0' && LA32_0 <= '\u13F4')||(LA32_0 >= '\u1401' && LA32_0 <= '\u1676')||(LA32_0 >= '\u1681' && LA32_0 <= '\u169A')||(LA32_0 >= '\u16A0' && LA32_0 <= '\u16F0')||(LA32_0 >= '\u1700' && LA32_0 <= '\u170C')||(LA32_0 >= '\u170E' && LA32_0 <= '\u1714')||(LA32_0 >= '\u1720' && LA32_0 <= '\u1736')||(LA32_0 >= '\u1740' && LA32_0 <= '\u1753')||(LA32_0 >= '\u1760' && LA32_0 <= '\u176C')||(LA32_0 >= '\u176E' && LA32_0 <= '\u1770')||(LA32_0 >= '\u1772' && LA32_0 <= '\u1773')||(LA32_0 >= '\u1780' && LA32_0 <= '\u17B3')||(LA32_0 >= '\u17B6' && LA32_0 <= '\u17DD')||(LA32_0 >= '\u17E0' && LA32_0 <= '\u17E9')||(LA32_0 >= '\u17F0' && LA32_0 <= '\u17F9')||(LA32_0 >= '\u1800' && LA32_0 <= '\u180D')||(LA32_0 >= '\u1810' && LA32_0 <= '\u1819')||(LA32_0 >= '\u1820' && LA32_0 <= '\u1877')||(LA32_0 >= '\u1880' && LA32_0 <= '\u18A9')||(LA32_0 >= '\u1900' && LA32_0 <= '\u191C')||(LA32_0 >= '\u1920' && LA32_0 <= '\u192B')||(LA32_0 >= '\u1930' && LA32_0 <= '\u193B')||LA32_0=='\u1940'||(LA32_0 >= '\u1944' && LA32_0 <= '\u196D')||(LA32_0 >= '\u1970' && LA32_0 <= '\u1974')||(LA32_0 >= '\u19E0' && LA32_0 <= '\u19FF')||(LA32_0 >= '\u1D00' && LA32_0 <= '\u1D6B')||(LA32_0 >= '\u1E00' && LA32_0 <= '\u1E9B')||(LA32_0 >= '\u1EA0' && LA32_0 <= '\u1EF9')||(LA32_0 >= '\u1F00' && LA32_0 <= '\u1F15')||(LA32_0 >= '\u1F18' && LA32_0 <= '\u1F1D')||(LA32_0 >= '\u1F20' && LA32_0 <= '\u1F45')||(LA32_0 >= '\u1F48' && LA32_0 <= '\u1F4D')||(LA32_0 >= '\u1F50' && LA32_0 <= '\u1F57')||LA32_0=='\u1F59'||LA32_0=='\u1F5B'||LA32_0=='\u1F5D'||(LA32_0 >= '\u1F5F' && LA32_0 <= '\u1F7D')||(LA32_0 >= '\u1F80' && LA32_0 <= '\u1FB4')||(LA32_0 >= '\u1FB6' && LA32_0 <= '\u1FBC')||LA32_0=='\u1FBE'||(LA32_0 >= '\u1FC2' && LA32_0 <= '\u1FC4')||(LA32_0 >= '\u1FC6' && LA32_0 <= '\u1FCC')||(LA32_0 >= '\u1FD0' && LA32_0 <= '\u1FD3')||(LA32_0 >= '\u1FD6' && LA32_0 <= '\u1FDB')||(LA32_0 >= '\u1FE0' && LA32_0 <= '\u1FEC')||(LA32_0 >= '\u1FF2' && LA32_0 <= '\u1FF4')||(LA32_0 >= '\u1FF6' && LA32_0 <= '\u1FFC')||(LA32_0 >= '\u2010' && LA32_0 <= '\u2017')||(LA32_0 >= '\u2020' && LA32_0 <= '\u2027')||(LA32_0 >= '\u2030' && LA32_0 <= '\u2038')||(LA32_0 >= '\u203B' && LA32_0 <= '\u2044')||(LA32_0 >= '\u2047' && LA32_0 <= '\u2054')||LA32_0=='\u2057'||(LA32_0 >= '\u2070' && LA32_0 <= '\u2071')||(LA32_0 >= '\u2074' && LA32_0 <= '\u207C')||(LA32_0 >= '\u207F' && LA32_0 <= '\u208C')||(LA32_0 >= '\u20A0' && LA32_0 <= '\u20B1')||(LA32_0 >= '\u20D0' && LA32_0 <= '\u20EA')||(LA32_0 >= '\u2100' && LA32_0 <= '\u213B')||(LA32_0 >= '\u213D' && LA32_0 <= '\u214B')||(LA32_0 >= '\u2153' && LA32_0 <= '\u2183')||(LA32_0 >= '\u2190' && LA32_0 <= '\u2328')||(LA32_0 >= '\u232B' && LA32_0 <= '\u23B3')||(LA32_0 >= '\u23B6' && LA32_0 <= '\u23D0')||(LA32_0 >= '\u2400' && LA32_0 <= '\u2426')||(LA32_0 >= '\u2440' && LA32_0 <= '\u244A')||(LA32_0 >= '\u2460' && LA32_0 <= '\u2617')||(LA32_0 >= '\u2619' && LA32_0 <= '\u267D')||(LA32_0 >= '\u2680' && LA32_0 <= '\u2691')||(LA32_0 >= '\u26A0' && LA32_0 <= '\u26A1')||(LA32_0 >= '\u2701' && LA32_0 <= '\u2704')||(LA32_0 >= '\u2706' && LA32_0 <= '\u2709')||(LA32_0 >= '\u270C' && LA32_0 <= '\u2727')||(LA32_0 >= '\u2729' && LA32_0 <= '\u274B')||LA32_0=='\u274D'||(LA32_0 >= '\u274F' && LA32_0 <= '\u2752')||LA32_0=='\u2756'||(LA32_0 >= '\u2758' && LA32_0 <= '\u275E')||(LA32_0 >= '\u2761' && LA32_0 <= '\u2767')||(LA32_0 >= '\u2776' && LA32_0 <= '\u2794')||(LA32_0 >= '\u2798' && LA32_0 <= '\u27AF')||(LA32_0 >= '\u27B1' && LA32_0 <= '\u27BE')||(LA32_0 >= '\u27D0' && LA32_0 <= '\u27E5')||(LA32_0 >= '\u27F0' && LA32_0 <= '\u2982')||(LA32_0 >= '\u2999' && LA32_0 <= '\u29D7')||(LA32_0 >= '\u29DC' && LA32_0 <= '\u29FB')||(LA32_0 >= '\u29FE' && LA32_0 <= '\u2B0D')||(LA32_0 >= '\u2E80' && LA32_0 <= '\u2E99')||(LA32_0 >= '\u2E9B' && LA32_0 <= '\u2EF3')||(LA32_0 >= '\u2F00' && LA32_0 <= '\u2FD5')||(LA32_0 >= '\u2FF0' && LA32_0 <= '\u2FFB')||(LA32_0 >= '\u3001' && LA32_0 <= '\u3007')||(LA32_0 >= '\u3012' && LA32_0 <= '\u3013')||LA32_0=='\u301C'||(LA32_0 >= '\u3020' && LA32_0 <= '\u303F')||(LA32_0 >= '\u3041' && LA32_0 <= '\u3096')||(LA32_0 >= '\u3099' && LA32_0 <= '\u309A')||(LA32_0 >= '\u309D' && LA32_0 <= '\u30FF')||(LA32_0 >= '\u3105' && LA32_0 <= '\u312C')||(LA32_0 >= '\u3131' && LA32_0 <= '\u318E')||(LA32_0 >= '\u3190' && LA32_0 <= '\u31B7')||(LA32_0 >= '\u31F0' && LA32_0 <= '\u321E')||(LA32_0 >= '\u3220' && LA32_0 <= '\u3243')||(LA32_0 >= '\u3250' && LA32_0 <= '\u327D')||(LA32_0 >= '\u327F' && LA32_0 <= '\u32FE')||(LA32_0 >= '\u3300' && LA32_0 <= '\u4DB5')||(LA32_0 >= '\u4DC0' && LA32_0 <= '\u9FA5')||(LA32_0 >= '\uA000' && LA32_0 <= '\uA48C')||(LA32_0 >= '\uA490' && LA32_0 <= '\uA4C6')||(LA32_0 >= '\uAC00' && LA32_0 <= '\uD7A3')||(LA32_0 >= '\uF900' && LA32_0 <= '\uFA2D')||(LA32_0 >= '\uFA30' && LA32_0 <= '\uFA6A')||(LA32_0 >= '\uFB00' && LA32_0 <= '\uFB06')||(LA32_0 >= '\uFB13' && LA32_0 <= '\uFB17')||(LA32_0 >= '\uFB1D' && LA32_0 <= '\uFB36')||(LA32_0 >= '\uFB38' && LA32_0 <= '\uFB3C')||LA32_0=='\uFB3E'||(LA32_0 >= '\uFB40' && LA32_0 <= '\uFB41')||(LA32_0 >= '\uFB43' && LA32_0 <= '\uFB44')||(LA32_0 >= '\uFB46' && LA32_0 <= '\uFBB1')||(LA32_0 >= '\uFBD3' && LA32_0 <= '\uFD3D')||(LA32_0 >= '\uFD50' && LA32_0 <= '\uFD8F')||(LA32_0 >= '\uFD92' && LA32_0 <= '\uFDC7')||(LA32_0 >= '\uFDF0' && LA32_0 <= '\uFDFD')||(LA32_0 >= '\uFE00' && LA32_0 <= '\uFE0F')||(LA32_0 >= '\uFE20' && LA32_0 <= '\uFE23')||(LA32_0 >= '\uFE30' && LA32_0 <= '\uFE34')||(LA32_0 >= '\uFE45' && LA32_0 <= '\uFE46')||(LA32_0 >= '\uFE49' && LA32_0 <= '\uFE52')||(LA32_0 >= '\uFE54' && LA32_0 <= '\uFE58')||(LA32_0 >= '\uFE5F' && LA32_0 <= '\uFE66')||(LA32_0 >= '\uFE68' && LA32_0 <= '\uFE6B')||(LA32_0 >= '\uFE70' && LA32_0 <= '\uFE74')||(LA32_0 >= '\uFE76' && LA32_0 <= '\uFEFC')||(LA32_0 >= '\uFF01' && LA32_0 <= '\uFF07')||(LA32_0 >= '\uFF0A' && LA32_0 <= '\uFF3A')||LA32_0=='\uFF3C'||LA32_0=='\uFF3F'||(LA32_0 >= '\uFF41' && LA32_0 <= '\uFF5A')||LA32_0=='\uFF5C'||LA32_0=='\uFF5E'||LA32_0=='\uFF61'||(LA32_0 >= '\uFF64' && LA32_0 <= '\uFFBE')||(LA32_0 >= '\uFFC2' && LA32_0 <= '\uFFC7')||(LA32_0 >= '\uFFCA' && LA32_0 <= '\uFFCF')||(LA32_0 >= '\uFFD2' && LA32_0 <= '\uFFD7')||(LA32_0 >= '\uFFDA' && LA32_0 <= '\uFFDC')||(LA32_0 >= '\uFFE0' && LA32_0 <= '\uFFE2')||(LA32_0 >= '\uFFE4' && LA32_0 <= '\uFFE6')||(LA32_0 >= '\uFFE8' && LA32_0 <= '\uFFEE')) ) {
                    alt32=2;
                }


                switch (alt32) {
            	case 1 :
            	    // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1460:17: F_ESC
            	    {
            	    mF_ESC(); if (state.failed) return ;


            	    }
            	    break;
            	case 2 :
            	    // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1461:19: IN_WORD
            	    {
            	    mIN_WORD(); if (state.failed) return ;


            	    }
            	    break;

            	default :
            	    break loop32;
                }
            } while (true);


            mSTAR(); if (state.failed) return ;


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "FTSPRE"

    // $ANTLR start "FTSWILD"
    public final void mFTSWILD() throws RecognitionException {
        try {
            int _type = FTSWILD;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1467:9: ( ( F_ESC | START_WORD | STAR | QUESTION_MARK ) ( F_ESC | IN_WORD | STAR | QUESTION_MARK )* )
            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1468:9: ( F_ESC | START_WORD | STAR | QUESTION_MARK ) ( F_ESC | IN_WORD | STAR | QUESTION_MARK )*
            {
            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1468:9: ( F_ESC | START_WORD | STAR | QUESTION_MARK )
            int alt33=4;
            int LA33_0 = input.LA(1);

            if ( (LA33_0=='\\') ) {
                alt33=1;
            }
            else if ( (LA33_0=='$'||(LA33_0 >= '0' && LA33_0 <= '9')||(LA33_0 >= 'A' && LA33_0 <= 'Z')||(LA33_0 >= 'a' && LA33_0 <= 'z')||(LA33_0 >= '\u00A2' && LA33_0 <= '\u00A7')||(LA33_0 >= '\u00A9' && LA33_0 <= '\u00AA')||LA33_0=='\u00AE'||LA33_0=='\u00B0'||(LA33_0 >= '\u00B2' && LA33_0 <= '\u00B3')||(LA33_0 >= '\u00B5' && LA33_0 <= '\u00B6')||(LA33_0 >= '\u00B9' && LA33_0 <= '\u00BA')||(LA33_0 >= '\u00BC' && LA33_0 <= '\u00BE')||(LA33_0 >= '\u00C0' && LA33_0 <= '\u00D6')||(LA33_0 >= '\u00D8' && LA33_0 <= '\u00F6')||(LA33_0 >= '\u00F8' && LA33_0 <= '\u0236')||(LA33_0 >= '\u0250' && LA33_0 <= '\u02C1')||(LA33_0 >= '\u02C6' && LA33_0 <= '\u02D1')||(LA33_0 >= '\u02E0' && LA33_0 <= '\u02E4')||LA33_0=='\u02EE'||(LA33_0 >= '\u0300' && LA33_0 <= '\u0357')||(LA33_0 >= '\u035D' && LA33_0 <= '\u036F')||LA33_0=='\u037A'||LA33_0=='\u0386'||(LA33_0 >= '\u0388' && LA33_0 <= '\u038A')||LA33_0=='\u038C'||(LA33_0 >= '\u038E' && LA33_0 <= '\u03A1')||(LA33_0 >= '\u03A3' && LA33_0 <= '\u03CE')||(LA33_0 >= '\u03D0' && LA33_0 <= '\u03F5')||(LA33_0 >= '\u03F7' && LA33_0 <= '\u03FB')||(LA33_0 >= '\u0400' && LA33_0 <= '\u0486')||(LA33_0 >= '\u0488' && LA33_0 <= '\u04CE')||(LA33_0 >= '\u04D0' && LA33_0 <= '\u04F5')||(LA33_0 >= '\u04F8' && LA33_0 <= '\u04F9')||(LA33_0 >= '\u0500' && LA33_0 <= '\u050F')||(LA33_0 >= '\u0531' && LA33_0 <= '\u0556')||LA33_0=='\u0559'||(LA33_0 >= '\u0561' && LA33_0 <= '\u0587')||(LA33_0 >= '\u0591' && LA33_0 <= '\u05A1')||(LA33_0 >= '\u05A3' && LA33_0 <= '\u05B9')||(LA33_0 >= '\u05BB' && LA33_0 <= '\u05BD')||LA33_0=='\u05BF'||(LA33_0 >= '\u05C1' && LA33_0 <= '\u05C2')||LA33_0=='\u05C4'||(LA33_0 >= '\u05D0' && LA33_0 <= '\u05EA')||(LA33_0 >= '\u05F0' && LA33_0 <= '\u05F2')||(LA33_0 >= '\u060E' && LA33_0 <= '\u0615')||(LA33_0 >= '\u0621' && LA33_0 <= '\u063A')||(LA33_0 >= '\u0640' && LA33_0 <= '\u0658')||(LA33_0 >= '\u0660' && LA33_0 <= '\u0669')||(LA33_0 >= '\u066E' && LA33_0 <= '\u06D3')||(LA33_0 >= '\u06D5' && LA33_0 <= '\u06DC')||(LA33_0 >= '\u06DE' && LA33_0 <= '\u06FF')||(LA33_0 >= '\u0710' && LA33_0 <= '\u074A')||(LA33_0 >= '\u074D' && LA33_0 <= '\u074F')||(LA33_0 >= '\u0780' && LA33_0 <= '\u07B1')||(LA33_0 >= '\u0901' && LA33_0 <= '\u0939')||(LA33_0 >= '\u093C' && LA33_0 <= '\u094D')||(LA33_0 >= '\u0950' && LA33_0 <= '\u0954')||(LA33_0 >= '\u0958' && LA33_0 <= '\u0963')||(LA33_0 >= '\u0966' && LA33_0 <= '\u096F')||(LA33_0 >= '\u0981' && LA33_0 <= '\u0983')||(LA33_0 >= '\u0985' && LA33_0 <= '\u098C')||(LA33_0 >= '\u098F' && LA33_0 <= '\u0990')||(LA33_0 >= '\u0993' && LA33_0 <= '\u09A8')||(LA33_0 >= '\u09AA' && LA33_0 <= '\u09B0')||LA33_0=='\u09B2'||(LA33_0 >= '\u09B6' && LA33_0 <= '\u09B9')||(LA33_0 >= '\u09BC' && LA33_0 <= '\u09C4')||(LA33_0 >= '\u09C7' && LA33_0 <= '\u09C8')||(LA33_0 >= '\u09CB' && LA33_0 <= '\u09CD')||LA33_0=='\u09D7'||(LA33_0 >= '\u09DC' && LA33_0 <= '\u09DD')||(LA33_0 >= '\u09DF' && LA33_0 <= '\u09E3')||(LA33_0 >= '\u09E6' && LA33_0 <= '\u09FA')||(LA33_0 >= '\u0A01' && LA33_0 <= '\u0A03')||(LA33_0 >= '\u0A05' && LA33_0 <= '\u0A0A')||(LA33_0 >= '\u0A0F' && LA33_0 <= '\u0A10')||(LA33_0 >= '\u0A13' && LA33_0 <= '\u0A28')||(LA33_0 >= '\u0A2A' && LA33_0 <= '\u0A30')||(LA33_0 >= '\u0A32' && LA33_0 <= '\u0A33')||(LA33_0 >= '\u0A35' && LA33_0 <= '\u0A36')||(LA33_0 >= '\u0A38' && LA33_0 <= '\u0A39')||LA33_0=='\u0A3C'||(LA33_0 >= '\u0A3E' && LA33_0 <= '\u0A42')||(LA33_0 >= '\u0A47' && LA33_0 <= '\u0A48')||(LA33_0 >= '\u0A4B' && LA33_0 <= '\u0A4D')||(LA33_0 >= '\u0A59' && LA33_0 <= '\u0A5C')||LA33_0=='\u0A5E'||(LA33_0 >= '\u0A66' && LA33_0 <= '\u0A74')||(LA33_0 >= '\u0A81' && LA33_0 <= '\u0A83')||(LA33_0 >= '\u0A85' && LA33_0 <= '\u0A8D')||(LA33_0 >= '\u0A8F' && LA33_0 <= '\u0A91')||(LA33_0 >= '\u0A93' && LA33_0 <= '\u0AA8')||(LA33_0 >= '\u0AAA' && LA33_0 <= '\u0AB0')||(LA33_0 >= '\u0AB2' && LA33_0 <= '\u0AB3')||(LA33_0 >= '\u0AB5' && LA33_0 <= '\u0AB9')||(LA33_0 >= '\u0ABC' && LA33_0 <= '\u0AC5')||(LA33_0 >= '\u0AC7' && LA33_0 <= '\u0AC9')||(LA33_0 >= '\u0ACB' && LA33_0 <= '\u0ACD')||LA33_0=='\u0AD0'||(LA33_0 >= '\u0AE0' && LA33_0 <= '\u0AE3')||(LA33_0 >= '\u0AE6' && LA33_0 <= '\u0AEF')||LA33_0=='\u0AF1'||(LA33_0 >= '\u0B01' && LA33_0 <= '\u0B03')||(LA33_0 >= '\u0B05' && LA33_0 <= '\u0B0C')||(LA33_0 >= '\u0B0F' && LA33_0 <= '\u0B10')||(LA33_0 >= '\u0B13' && LA33_0 <= '\u0B28')||(LA33_0 >= '\u0B2A' && LA33_0 <= '\u0B30')||(LA33_0 >= '\u0B32' && LA33_0 <= '\u0B33')||(LA33_0 >= '\u0B35' && LA33_0 <= '\u0B39')||(LA33_0 >= '\u0B3C' && LA33_0 <= '\u0B43')||(LA33_0 >= '\u0B47' && LA33_0 <= '\u0B48')||(LA33_0 >= '\u0B4B' && LA33_0 <= '\u0B4D')||(LA33_0 >= '\u0B56' && LA33_0 <= '\u0B57')||(LA33_0 >= '\u0B5C' && LA33_0 <= '\u0B5D')||(LA33_0 >= '\u0B5F' && LA33_0 <= '\u0B61')||(LA33_0 >= '\u0B66' && LA33_0 <= '\u0B71')||(LA33_0 >= '\u0B82' && LA33_0 <= '\u0B83')||(LA33_0 >= '\u0B85' && LA33_0 <= '\u0B8A')||(LA33_0 >= '\u0B8E' && LA33_0 <= '\u0B90')||(LA33_0 >= '\u0B92' && LA33_0 <= '\u0B95')||(LA33_0 >= '\u0B99' && LA33_0 <= '\u0B9A')||LA33_0=='\u0B9C'||(LA33_0 >= '\u0B9E' && LA33_0 <= '\u0B9F')||(LA33_0 >= '\u0BA3' && LA33_0 <= '\u0BA4')||(LA33_0 >= '\u0BA8' && LA33_0 <= '\u0BAA')||(LA33_0 >= '\u0BAE' && LA33_0 <= '\u0BB5')||(LA33_0 >= '\u0BB7' && LA33_0 <= '\u0BB9')||(LA33_0 >= '\u0BBE' && LA33_0 <= '\u0BC2')||(LA33_0 >= '\u0BC6' && LA33_0 <= '\u0BC8')||(LA33_0 >= '\u0BCA' && LA33_0 <= '\u0BCD')||LA33_0=='\u0BD7'||(LA33_0 >= '\u0BE7' && LA33_0 <= '\u0BFA')||(LA33_0 >= '\u0C01' && LA33_0 <= '\u0C03')||(LA33_0 >= '\u0C05' && LA33_0 <= '\u0C0C')||(LA33_0 >= '\u0C0E' && LA33_0 <= '\u0C10')||(LA33_0 >= '\u0C12' && LA33_0 <= '\u0C28')||(LA33_0 >= '\u0C2A' && LA33_0 <= '\u0C33')||(LA33_0 >= '\u0C35' && LA33_0 <= '\u0C39')||(LA33_0 >= '\u0C3E' && LA33_0 <= '\u0C44')||(LA33_0 >= '\u0C46' && LA33_0 <= '\u0C48')||(LA33_0 >= '\u0C4A' && LA33_0 <= '\u0C4D')||(LA33_0 >= '\u0C55' && LA33_0 <= '\u0C56')||(LA33_0 >= '\u0C60' && LA33_0 <= '\u0C61')||(LA33_0 >= '\u0C66' && LA33_0 <= '\u0C6F')||(LA33_0 >= '\u0C82' && LA33_0 <= '\u0C83')||(LA33_0 >= '\u0C85' && LA33_0 <= '\u0C8C')||(LA33_0 >= '\u0C8E' && LA33_0 <= '\u0C90')||(LA33_0 >= '\u0C92' && LA33_0 <= '\u0CA8')||(LA33_0 >= '\u0CAA' && LA33_0 <= '\u0CB3')||(LA33_0 >= '\u0CB5' && LA33_0 <= '\u0CB9')||(LA33_0 >= '\u0CBC' && LA33_0 <= '\u0CC4')||(LA33_0 >= '\u0CC6' && LA33_0 <= '\u0CC8')||(LA33_0 >= '\u0CCA' && LA33_0 <= '\u0CCD')||(LA33_0 >= '\u0CD5' && LA33_0 <= '\u0CD6')||LA33_0=='\u0CDE'||(LA33_0 >= '\u0CE0' && LA33_0 <= '\u0CE1')||(LA33_0 >= '\u0CE6' && LA33_0 <= '\u0CEF')||(LA33_0 >= '\u0D02' && LA33_0 <= '\u0D03')||(LA33_0 >= '\u0D05' && LA33_0 <= '\u0D0C')||(LA33_0 >= '\u0D0E' && LA33_0 <= '\u0D10')||(LA33_0 >= '\u0D12' && LA33_0 <= '\u0D28')||(LA33_0 >= '\u0D2A' && LA33_0 <= '\u0D39')||(LA33_0 >= '\u0D3E' && LA33_0 <= '\u0D43')||(LA33_0 >= '\u0D46' && LA33_0 <= '\u0D48')||(LA33_0 >= '\u0D4A' && LA33_0 <= '\u0D4D')||LA33_0=='\u0D57'||(LA33_0 >= '\u0D60' && LA33_0 <= '\u0D61')||(LA33_0 >= '\u0D66' && LA33_0 <= '\u0D6F')||(LA33_0 >= '\u0D82' && LA33_0 <= '\u0D83')||(LA33_0 >= '\u0D85' && LA33_0 <= '\u0D96')||(LA33_0 >= '\u0D9A' && LA33_0 <= '\u0DB1')||(LA33_0 >= '\u0DB3' && LA33_0 <= '\u0DBB')||LA33_0=='\u0DBD'||(LA33_0 >= '\u0DC0' && LA33_0 <= '\u0DC6')||LA33_0=='\u0DCA'||(LA33_0 >= '\u0DCF' && LA33_0 <= '\u0DD4')||LA33_0=='\u0DD6'||(LA33_0 >= '\u0DD8' && LA33_0 <= '\u0DDF')||(LA33_0 >= '\u0DF2' && LA33_0 <= '\u0DF3')||(LA33_0 >= '\u0E01' && LA33_0 <= '\u0E3A')||(LA33_0 >= '\u0E3F' && LA33_0 <= '\u0E4E')||(LA33_0 >= '\u0E50' && LA33_0 <= '\u0E59')||(LA33_0 >= '\u0E81' && LA33_0 <= '\u0E82')||LA33_0=='\u0E84'||(LA33_0 >= '\u0E87' && LA33_0 <= '\u0E88')||LA33_0=='\u0E8A'||LA33_0=='\u0E8D'||(LA33_0 >= '\u0E94' && LA33_0 <= '\u0E97')||(LA33_0 >= '\u0E99' && LA33_0 <= '\u0E9F')||(LA33_0 >= '\u0EA1' && LA33_0 <= '\u0EA3')||LA33_0=='\u0EA5'||LA33_0=='\u0EA7'||(LA33_0 >= '\u0EAA' && LA33_0 <= '\u0EAB')||(LA33_0 >= '\u0EAD' && LA33_0 <= '\u0EB9')||(LA33_0 >= '\u0EBB' && LA33_0 <= '\u0EBD')||(LA33_0 >= '\u0EC0' && LA33_0 <= '\u0EC4')||LA33_0=='\u0EC6'||(LA33_0 >= '\u0EC8' && LA33_0 <= '\u0ECD')||(LA33_0 >= '\u0ED0' && LA33_0 <= '\u0ED9')||(LA33_0 >= '\u0EDC' && LA33_0 <= '\u0EDD')||(LA33_0 >= '\u0F00' && LA33_0 <= '\u0F03')||(LA33_0 >= '\u0F13' && LA33_0 <= '\u0F39')||(LA33_0 >= '\u0F3E' && LA33_0 <= '\u0F47')||(LA33_0 >= '\u0F49' && LA33_0 <= '\u0F6A')||(LA33_0 >= '\u0F71' && LA33_0 <= '\u0F84')||(LA33_0 >= '\u0F86' && LA33_0 <= '\u0F8B')||(LA33_0 >= '\u0F90' && LA33_0 <= '\u0F97')||(LA33_0 >= '\u0F99' && LA33_0 <= '\u0FBC')||(LA33_0 >= '\u0FBE' && LA33_0 <= '\u0FCC')||LA33_0=='\u0FCF'||(LA33_0 >= '\u1000' && LA33_0 <= '\u1021')||(LA33_0 >= '\u1023' && LA33_0 <= '\u1027')||(LA33_0 >= '\u1029' && LA33_0 <= '\u102A')||(LA33_0 >= '\u102C' && LA33_0 <= '\u1032')||(LA33_0 >= '\u1036' && LA33_0 <= '\u1039')||(LA33_0 >= '\u1040' && LA33_0 <= '\u1049')||(LA33_0 >= '\u1050' && LA33_0 <= '\u1059')||(LA33_0 >= '\u10A0' && LA33_0 <= '\u10C5')||(LA33_0 >= '\u10D0' && LA33_0 <= '\u10F8')||(LA33_0 >= '\u1100' && LA33_0 <= '\u1159')||(LA33_0 >= '\u115F' && LA33_0 <= '\u11A2')||(LA33_0 >= '\u11A8' && LA33_0 <= '\u11F9')||(LA33_0 >= '\u1200' && LA33_0 <= '\u1206')||(LA33_0 >= '\u1208' && LA33_0 <= '\u1246')||LA33_0=='\u1248'||(LA33_0 >= '\u124A' && LA33_0 <= '\u124D')||(LA33_0 >= '\u1250' && LA33_0 <= '\u1256')||LA33_0=='\u1258'||(LA33_0 >= '\u125A' && LA33_0 <= '\u125D')||(LA33_0 >= '\u1260' && LA33_0 <= '\u1286')||LA33_0=='\u1288'||(LA33_0 >= '\u128A' && LA33_0 <= '\u128D')||(LA33_0 >= '\u1290' && LA33_0 <= '\u12AE')||LA33_0=='\u12B0'||(LA33_0 >= '\u12B2' && LA33_0 <= '\u12B5')||(LA33_0 >= '\u12B8' && LA33_0 <= '\u12BE')||LA33_0=='\u12C0'||(LA33_0 >= '\u12C2' && LA33_0 <= '\u12C5')||(LA33_0 >= '\u12C8' && LA33_0 <= '\u12CE')||(LA33_0 >= '\u12D0' && LA33_0 <= '\u12D6')||(LA33_0 >= '\u12D8' && LA33_0 <= '\u12EE')||(LA33_0 >= '\u12F0' && LA33_0 <= '\u130E')||LA33_0=='\u1310'||(LA33_0 >= '\u1312' && LA33_0 <= '\u1315')||(LA33_0 >= '\u1318' && LA33_0 <= '\u131E')||(LA33_0 >= '\u1320' && LA33_0 <= '\u1346')||(LA33_0 >= '\u1348' && LA33_0 <= '\u135A')||(LA33_0 >= '\u1369' && LA33_0 <= '\u137C')||(LA33_0 >= '\u13A0' && LA33_0 <= '\u13F4')||(LA33_0 >= '\u1401' && LA33_0 <= '\u166C')||(LA33_0 >= '\u166F' && LA33_0 <= '\u1676')||(LA33_0 >= '\u1681' && LA33_0 <= '\u169A')||(LA33_0 >= '\u16A0' && LA33_0 <= '\u16EA')||(LA33_0 >= '\u16EE' && LA33_0 <= '\u16F0')||(LA33_0 >= '\u1700' && LA33_0 <= '\u170C')||(LA33_0 >= '\u170E' && LA33_0 <= '\u1714')||(LA33_0 >= '\u1720' && LA33_0 <= '\u1734')||(LA33_0 >= '\u1740' && LA33_0 <= '\u1753')||(LA33_0 >= '\u1760' && LA33_0 <= '\u176C')||(LA33_0 >= '\u176E' && LA33_0 <= '\u1770')||(LA33_0 >= '\u1772' && LA33_0 <= '\u1773')||(LA33_0 >= '\u1780' && LA33_0 <= '\u17B3')||(LA33_0 >= '\u17B6' && LA33_0 <= '\u17D3')||LA33_0=='\u17D7'||(LA33_0 >= '\u17DB' && LA33_0 <= '\u17DD')||(LA33_0 >= '\u17E0' && LA33_0 <= '\u17E9')||(LA33_0 >= '\u17F0' && LA33_0 <= '\u17F9')||(LA33_0 >= '\u180B' && LA33_0 <= '\u180D')||(LA33_0 >= '\u1810' && LA33_0 <= '\u1819')||(LA33_0 >= '\u1820' && LA33_0 <= '\u1877')||(LA33_0 >= '\u1880' && LA33_0 <= '\u18A9')||(LA33_0 >= '\u1900' && LA33_0 <= '\u191C')||(LA33_0 >= '\u1920' && LA33_0 <= '\u192B')||(LA33_0 >= '\u1930' && LA33_0 <= '\u193B')||LA33_0=='\u1940'||(LA33_0 >= '\u1946' && LA33_0 <= '\u196D')||(LA33_0 >= '\u1970' && LA33_0 <= '\u1974')||(LA33_0 >= '\u19E0' && LA33_0 <= '\u19FF')||(LA33_0 >= '\u1D00' && LA33_0 <= '\u1D6B')||(LA33_0 >= '\u1E00' && LA33_0 <= '\u1E9B')||(LA33_0 >= '\u1EA0' && LA33_0 <= '\u1EF9')||(LA33_0 >= '\u1F00' && LA33_0 <= '\u1F15')||(LA33_0 >= '\u1F18' && LA33_0 <= '\u1F1D')||(LA33_0 >= '\u1F20' && LA33_0 <= '\u1F45')||(LA33_0 >= '\u1F48' && LA33_0 <= '\u1F4D')||(LA33_0 >= '\u1F50' && LA33_0 <= '\u1F57')||LA33_0=='\u1F59'||LA33_0=='\u1F5B'||LA33_0=='\u1F5D'||(LA33_0 >= '\u1F5F' && LA33_0 <= '\u1F7D')||(LA33_0 >= '\u1F80' && LA33_0 <= '\u1FB4')||(LA33_0 >= '\u1FB6' && LA33_0 <= '\u1FBC')||LA33_0=='\u1FBE'||(LA33_0 >= '\u1FC2' && LA33_0 <= '\u1FC4')||(LA33_0 >= '\u1FC6' && LA33_0 <= '\u1FCC')||(LA33_0 >= '\u1FD0' && LA33_0 <= '\u1FD3')||(LA33_0 >= '\u1FD6' && LA33_0 <= '\u1FDB')||(LA33_0 >= '\u1FE0' && LA33_0 <= '\u1FEC')||(LA33_0 >= '\u1FF2' && LA33_0 <= '\u1FF4')||(LA33_0 >= '\u1FF6' && LA33_0 <= '\u1FFC')||(LA33_0 >= '\u2070' && LA33_0 <= '\u2071')||(LA33_0 >= '\u2074' && LA33_0 <= '\u2079')||(LA33_0 >= '\u207F' && LA33_0 <= '\u2089')||(LA33_0 >= '\u20A0' && LA33_0 <= '\u20B1')||(LA33_0 >= '\u20D0' && LA33_0 <= '\u20EA')||(LA33_0 >= '\u2100' && LA33_0 <= '\u213B')||(LA33_0 >= '\u213D' && LA33_0 <= '\u213F')||(LA33_0 >= '\u2145' && LA33_0 <= '\u214A')||(LA33_0 >= '\u2153' && LA33_0 <= '\u2183')||(LA33_0 >= '\u2195' && LA33_0 <= '\u2199')||(LA33_0 >= '\u219C' && LA33_0 <= '\u219F')||(LA33_0 >= '\u21A1' && LA33_0 <= '\u21A2')||(LA33_0 >= '\u21A4' && LA33_0 <= '\u21A5')||(LA33_0 >= '\u21A7' && LA33_0 <= '\u21AD')||(LA33_0 >= '\u21AF' && LA33_0 <= '\u21CD')||(LA33_0 >= '\u21D0' && LA33_0 <= '\u21D1')||LA33_0=='\u21D3'||(LA33_0 >= '\u21D5' && LA33_0 <= '\u21F3')||(LA33_0 >= '\u2300' && LA33_0 <= '\u2307')||(LA33_0 >= '\u230C' && LA33_0 <= '\u231F')||(LA33_0 >= '\u2322' && LA33_0 <= '\u2328')||(LA33_0 >= '\u232B' && LA33_0 <= '\u237B')||(LA33_0 >= '\u237D' && LA33_0 <= '\u239A')||(LA33_0 >= '\u23B7' && LA33_0 <= '\u23D0')||(LA33_0 >= '\u2400' && LA33_0 <= '\u2426')||(LA33_0 >= '\u2440' && LA33_0 <= '\u244A')||(LA33_0 >= '\u2460' && LA33_0 <= '\u25B6')||(LA33_0 >= '\u25B8' && LA33_0 <= '\u25C0')||(LA33_0 >= '\u25C2' && LA33_0 <= '\u25F7')||(LA33_0 >= '\u2600' && LA33_0 <= '\u2617')||(LA33_0 >= '\u2619' && LA33_0 <= '\u266E')||(LA33_0 >= '\u2670' && LA33_0 <= '\u267D')||(LA33_0 >= '\u2680' && LA33_0 <= '\u2691')||(LA33_0 >= '\u26A0' && LA33_0 <= '\u26A1')||(LA33_0 >= '\u2701' && LA33_0 <= '\u2704')||(LA33_0 >= '\u2706' && LA33_0 <= '\u2709')||(LA33_0 >= '\u270C' && LA33_0 <= '\u2727')||(LA33_0 >= '\u2729' && LA33_0 <= '\u274B')||LA33_0=='\u274D'||(LA33_0 >= '\u274F' && LA33_0 <= '\u2752')||LA33_0=='\u2756'||(LA33_0 >= '\u2758' && LA33_0 <= '\u275E')||(LA33_0 >= '\u2761' && LA33_0 <= '\u2767')||(LA33_0 >= '\u2776' && LA33_0 <= '\u2794')||(LA33_0 >= '\u2798' && LA33_0 <= '\u27AF')||(LA33_0 >= '\u27B1' && LA33_0 <= '\u27BE')||(LA33_0 >= '\u2800' && LA33_0 <= '\u28FF')||(LA33_0 >= '\u2B00' && LA33_0 <= '\u2B0D')||(LA33_0 >= '\u2E80' && LA33_0 <= '\u2E99')||(LA33_0 >= '\u2E9B' && LA33_0 <= '\u2EF3')||(LA33_0 >= '\u2F00' && LA33_0 <= '\u2FD5')||(LA33_0 >= '\u2FF0' && LA33_0 <= '\u2FFB')||(LA33_0 >= '\u3004' && LA33_0 <= '\u3007')||(LA33_0 >= '\u3012' && LA33_0 <= '\u3013')||(LA33_0 >= '\u3020' && LA33_0 <= '\u302F')||(LA33_0 >= '\u3031' && LA33_0 <= '\u303C')||(LA33_0 >= '\u303E' && LA33_0 <= '\u303F')||(LA33_0 >= '\u3041' && LA33_0 <= '\u3096')||(LA33_0 >= '\u3099' && LA33_0 <= '\u309A')||(LA33_0 >= '\u309D' && LA33_0 <= '\u309F')||(LA33_0 >= '\u30A1' && LA33_0 <= '\u30FA')||(LA33_0 >= '\u30FC' && LA33_0 <= '\u30FF')||(LA33_0 >= '\u3105' && LA33_0 <= '\u312C')||(LA33_0 >= '\u3131' && LA33_0 <= '\u318E')||(LA33_0 >= '\u3190' && LA33_0 <= '\u31B7')||(LA33_0 >= '\u31F0' && LA33_0 <= '\u321E')||(LA33_0 >= '\u3220' && LA33_0 <= '\u3243')||(LA33_0 >= '\u3250' && LA33_0 <= '\u327D')||(LA33_0 >= '\u327F' && LA33_0 <= '\u32FE')||(LA33_0 >= '\u3300' && LA33_0 <= '\u4DB5')||(LA33_0 >= '\u4DC0' && LA33_0 <= '\u9FA5')||(LA33_0 >= '\uA000' && LA33_0 <= '\uA48C')||(LA33_0 >= '\uA490' && LA33_0 <= '\uA4C6')||(LA33_0 >= '\uAC00' && LA33_0 <= '\uD7A3')||(LA33_0 >= '\uF900' && LA33_0 <= '\uFA2D')||(LA33_0 >= '\uFA30' && LA33_0 <= '\uFA6A')||(LA33_0 >= '\uFB00' && LA33_0 <= '\uFB06')||(LA33_0 >= '\uFB13' && LA33_0 <= '\uFB17')||(LA33_0 >= '\uFB1D' && LA33_0 <= '\uFB28')||(LA33_0 >= '\uFB2A' && LA33_0 <= '\uFB36')||(LA33_0 >= '\uFB38' && LA33_0 <= '\uFB3C')||LA33_0=='\uFB3E'||(LA33_0 >= '\uFB40' && LA33_0 <= '\uFB41')||(LA33_0 >= '\uFB43' && LA33_0 <= '\uFB44')||(LA33_0 >= '\uFB46' && LA33_0 <= '\uFBB1')||(LA33_0 >= '\uFBD3' && LA33_0 <= '\uFD3D')||(LA33_0 >= '\uFD50' && LA33_0 <= '\uFD8F')||(LA33_0 >= '\uFD92' && LA33_0 <= '\uFDC7')||(LA33_0 >= '\uFDF0' && LA33_0 <= '\uFDFD')||(LA33_0 >= '\uFE00' && LA33_0 <= '\uFE0F')||(LA33_0 >= '\uFE20' && LA33_0 <= '\uFE23')||LA33_0=='\uFE69'||(LA33_0 >= '\uFE70' && LA33_0 <= '\uFE74')||(LA33_0 >= '\uFE76' && LA33_0 <= '\uFEFC')||LA33_0=='\uFF04'||(LA33_0 >= '\uFF10' && LA33_0 <= '\uFF19')||(LA33_0 >= '\uFF21' && LA33_0 <= '\uFF3A')||(LA33_0 >= '\uFF41' && LA33_0 <= '\uFF5A')||(LA33_0 >= '\uFF66' && LA33_0 <= '\uFFBE')||(LA33_0 >= '\uFFC2' && LA33_0 <= '\uFFC7')||(LA33_0 >= '\uFFCA' && LA33_0 <= '\uFFCF')||(LA33_0 >= '\uFFD2' && LA33_0 <= '\uFFD7')||(LA33_0 >= '\uFFDA' && LA33_0 <= '\uFFDC')||(LA33_0 >= '\uFFE0' && LA33_0 <= '\uFFE1')||(LA33_0 >= '\uFFE4' && LA33_0 <= '\uFFE6')||LA33_0=='\uFFE8'||(LA33_0 >= '\uFFED' && LA33_0 <= '\uFFEE')) ) {
                alt33=2;
            }
            else if ( (LA33_0=='*') ) {
                alt33=3;
            }
            else if ( (LA33_0=='?') ) {
                alt33=4;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 33, 0, input);

                throw nvae;

            }
            switch (alt33) {
                case 1 :
                    // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1469:17: F_ESC
                    {
                    mF_ESC(); if (state.failed) return ;


                    }
                    break;
                case 2 :
                    // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1470:19: START_WORD
                    {
                    mSTART_WORD(); if (state.failed) return ;


                    }
                    break;
                case 3 :
                    // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1471:19: STAR
                    {
                    mSTAR(); if (state.failed) return ;


                    }
                    break;
                case 4 :
                    // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1472:19: QUESTION_MARK
                    {
                    mQUESTION_MARK(); if (state.failed) return ;


                    }
                    break;

            }


            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1474:9: ( F_ESC | IN_WORD | STAR | QUESTION_MARK )*
            loop34:
            do {
                int alt34=5;
                int LA34_0 = input.LA(1);

                if ( (LA34_0=='\\') ) {
                    alt34=1;
                }
                else if ( ((LA34_0 >= '!' && LA34_0 <= '\'')||LA34_0=='+'||LA34_0=='-'||(LA34_0 >= '/' && LA34_0 <= '9')||LA34_0==';'||LA34_0=='='||(LA34_0 >= '@' && LA34_0 <= 'Z')||LA34_0=='_'||(LA34_0 >= 'a' && LA34_0 <= 'z')||LA34_0=='|'||(LA34_0 >= '\u00A1' && LA34_0 <= '\u00A7')||(LA34_0 >= '\u00A9' && LA34_0 <= '\u00AA')||LA34_0=='\u00AC'||LA34_0=='\u00AE'||(LA34_0 >= '\u00B0' && LA34_0 <= '\u00B3')||(LA34_0 >= '\u00B5' && LA34_0 <= '\u00B7')||(LA34_0 >= '\u00B9' && LA34_0 <= '\u00BA')||(LA34_0 >= '\u00BC' && LA34_0 <= '\u0236')||(LA34_0 >= '\u0250' && LA34_0 <= '\u02C1')||(LA34_0 >= '\u02C6' && LA34_0 <= '\u02D1')||(LA34_0 >= '\u02E0' && LA34_0 <= '\u02E4')||LA34_0=='\u02EE'||(LA34_0 >= '\u0300' && LA34_0 <= '\u0357')||(LA34_0 >= '\u035D' && LA34_0 <= '\u036F')||LA34_0=='\u037A'||LA34_0=='\u037E'||(LA34_0 >= '\u0386' && LA34_0 <= '\u038A')||LA34_0=='\u038C'||(LA34_0 >= '\u038E' && LA34_0 <= '\u03A1')||(LA34_0 >= '\u03A3' && LA34_0 <= '\u03CE')||(LA34_0 >= '\u03D0' && LA34_0 <= '\u03FB')||(LA34_0 >= '\u0400' && LA34_0 <= '\u0486')||(LA34_0 >= '\u0488' && LA34_0 <= '\u04CE')||(LA34_0 >= '\u04D0' && LA34_0 <= '\u04F5')||(LA34_0 >= '\u04F8' && LA34_0 <= '\u04F9')||(LA34_0 >= '\u0500' && LA34_0 <= '\u050F')||(LA34_0 >= '\u0531' && LA34_0 <= '\u0556')||(LA34_0 >= '\u0559' && LA34_0 <= '\u055F')||(LA34_0 >= '\u0561' && LA34_0 <= '\u0587')||(LA34_0 >= '\u0589' && LA34_0 <= '\u058A')||(LA34_0 >= '\u0591' && LA34_0 <= '\u05A1')||(LA34_0 >= '\u05A3' && LA34_0 <= '\u05B9')||(LA34_0 >= '\u05BB' && LA34_0 <= '\u05C4')||(LA34_0 >= '\u05D0' && LA34_0 <= '\u05EA')||(LA34_0 >= '\u05F0' && LA34_0 <= '\u05F4')||(LA34_0 >= '\u060C' && LA34_0 <= '\u0615')||LA34_0=='\u061B'||LA34_0=='\u061F'||(LA34_0 >= '\u0621' && LA34_0 <= '\u063A')||(LA34_0 >= '\u0640' && LA34_0 <= '\u0658')||(LA34_0 >= '\u0660' && LA34_0 <= '\u06DC')||(LA34_0 >= '\u06DE' && LA34_0 <= '\u070D')||(LA34_0 >= '\u0710' && LA34_0 <= '\u074A')||(LA34_0 >= '\u074D' && LA34_0 <= '\u074F')||(LA34_0 >= '\u0780' && LA34_0 <= '\u07B1')||(LA34_0 >= '\u0901' && LA34_0 <= '\u0939')||(LA34_0 >= '\u093C' && LA34_0 <= '\u094D')||(LA34_0 >= '\u0950' && LA34_0 <= '\u0954')||(LA34_0 >= '\u0958' && LA34_0 <= '\u0970')||(LA34_0 >= '\u0981' && LA34_0 <= '\u0983')||(LA34_0 >= '\u0985' && LA34_0 <= '\u098C')||(LA34_0 >= '\u098F' && LA34_0 <= '\u0990')||(LA34_0 >= '\u0993' && LA34_0 <= '\u09A8')||(LA34_0 >= '\u09AA' && LA34_0 <= '\u09B0')||LA34_0=='\u09B2'||(LA34_0 >= '\u09B6' && LA34_0 <= '\u09B9')||(LA34_0 >= '\u09BC' && LA34_0 <= '\u09C4')||(LA34_0 >= '\u09C7' && LA34_0 <= '\u09C8')||(LA34_0 >= '\u09CB' && LA34_0 <= '\u09CD')||LA34_0=='\u09D7'||(LA34_0 >= '\u09DC' && LA34_0 <= '\u09DD')||(LA34_0 >= '\u09DF' && LA34_0 <= '\u09E3')||(LA34_0 >= '\u09E6' && LA34_0 <= '\u09FA')||(LA34_0 >= '\u0A01' && LA34_0 <= '\u0A03')||(LA34_0 >= '\u0A05' && LA34_0 <= '\u0A0A')||(LA34_0 >= '\u0A0F' && LA34_0 <= '\u0A10')||(LA34_0 >= '\u0A13' && LA34_0 <= '\u0A28')||(LA34_0 >= '\u0A2A' && LA34_0 <= '\u0A30')||(LA34_0 >= '\u0A32' && LA34_0 <= '\u0A33')||(LA34_0 >= '\u0A35' && LA34_0 <= '\u0A36')||(LA34_0 >= '\u0A38' && LA34_0 <= '\u0A39')||LA34_0=='\u0A3C'||(LA34_0 >= '\u0A3E' && LA34_0 <= '\u0A42')||(LA34_0 >= '\u0A47' && LA34_0 <= '\u0A48')||(LA34_0 >= '\u0A4B' && LA34_0 <= '\u0A4D')||(LA34_0 >= '\u0A59' && LA34_0 <= '\u0A5C')||LA34_0=='\u0A5E'||(LA34_0 >= '\u0A66' && LA34_0 <= '\u0A74')||(LA34_0 >= '\u0A81' && LA34_0 <= '\u0A83')||(LA34_0 >= '\u0A85' && LA34_0 <= '\u0A8D')||(LA34_0 >= '\u0A8F' && LA34_0 <= '\u0A91')||(LA34_0 >= '\u0A93' && LA34_0 <= '\u0AA8')||(LA34_0 >= '\u0AAA' && LA34_0 <= '\u0AB0')||(LA34_0 >= '\u0AB2' && LA34_0 <= '\u0AB3')||(LA34_0 >= '\u0AB5' && LA34_0 <= '\u0AB9')||(LA34_0 >= '\u0ABC' && LA34_0 <= '\u0AC5')||(LA34_0 >= '\u0AC7' && LA34_0 <= '\u0AC9')||(LA34_0 >= '\u0ACB' && LA34_0 <= '\u0ACD')||LA34_0=='\u0AD0'||(LA34_0 >= '\u0AE0' && LA34_0 <= '\u0AE3')||(LA34_0 >= '\u0AE6' && LA34_0 <= '\u0AEF')||LA34_0=='\u0AF1'||(LA34_0 >= '\u0B01' && LA34_0 <= '\u0B03')||(LA34_0 >= '\u0B05' && LA34_0 <= '\u0B0C')||(LA34_0 >= '\u0B0F' && LA34_0 <= '\u0B10')||(LA34_0 >= '\u0B13' && LA34_0 <= '\u0B28')||(LA34_0 >= '\u0B2A' && LA34_0 <= '\u0B30')||(LA34_0 >= '\u0B32' && LA34_0 <= '\u0B33')||(LA34_0 >= '\u0B35' && LA34_0 <= '\u0B39')||(LA34_0 >= '\u0B3C' && LA34_0 <= '\u0B43')||(LA34_0 >= '\u0B47' && LA34_0 <= '\u0B48')||(LA34_0 >= '\u0B4B' && LA34_0 <= '\u0B4D')||(LA34_0 >= '\u0B56' && LA34_0 <= '\u0B57')||(LA34_0 >= '\u0B5C' && LA34_0 <= '\u0B5D')||(LA34_0 >= '\u0B5F' && LA34_0 <= '\u0B61')||(LA34_0 >= '\u0B66' && LA34_0 <= '\u0B71')||(LA34_0 >= '\u0B82' && LA34_0 <= '\u0B83')||(LA34_0 >= '\u0B85' && LA34_0 <= '\u0B8A')||(LA34_0 >= '\u0B8E' && LA34_0 <= '\u0B90')||(LA34_0 >= '\u0B92' && LA34_0 <= '\u0B95')||(LA34_0 >= '\u0B99' && LA34_0 <= '\u0B9A')||LA34_0=='\u0B9C'||(LA34_0 >= '\u0B9E' && LA34_0 <= '\u0B9F')||(LA34_0 >= '\u0BA3' && LA34_0 <= '\u0BA4')||(LA34_0 >= '\u0BA8' && LA34_0 <= '\u0BAA')||(LA34_0 >= '\u0BAE' && LA34_0 <= '\u0BB5')||(LA34_0 >= '\u0BB7' && LA34_0 <= '\u0BB9')||(LA34_0 >= '\u0BBE' && LA34_0 <= '\u0BC2')||(LA34_0 >= '\u0BC6' && LA34_0 <= '\u0BC8')||(LA34_0 >= '\u0BCA' && LA34_0 <= '\u0BCD')||LA34_0=='\u0BD7'||(LA34_0 >= '\u0BE7' && LA34_0 <= '\u0BFA')||(LA34_0 >= '\u0C01' && LA34_0 <= '\u0C03')||(LA34_0 >= '\u0C05' && LA34_0 <= '\u0C0C')||(LA34_0 >= '\u0C0E' && LA34_0 <= '\u0C10')||(LA34_0 >= '\u0C12' && LA34_0 <= '\u0C28')||(LA34_0 >= '\u0C2A' && LA34_0 <= '\u0C33')||(LA34_0 >= '\u0C35' && LA34_0 <= '\u0C39')||(LA34_0 >= '\u0C3E' && LA34_0 <= '\u0C44')||(LA34_0 >= '\u0C46' && LA34_0 <= '\u0C48')||(LA34_0 >= '\u0C4A' && LA34_0 <= '\u0C4D')||(LA34_0 >= '\u0C55' && LA34_0 <= '\u0C56')||(LA34_0 >= '\u0C60' && LA34_0 <= '\u0C61')||(LA34_0 >= '\u0C66' && LA34_0 <= '\u0C6F')||(LA34_0 >= '\u0C82' && LA34_0 <= '\u0C83')||(LA34_0 >= '\u0C85' && LA34_0 <= '\u0C8C')||(LA34_0 >= '\u0C8E' && LA34_0 <= '\u0C90')||(LA34_0 >= '\u0C92' && LA34_0 <= '\u0CA8')||(LA34_0 >= '\u0CAA' && LA34_0 <= '\u0CB3')||(LA34_0 >= '\u0CB5' && LA34_0 <= '\u0CB9')||(LA34_0 >= '\u0CBC' && LA34_0 <= '\u0CC4')||(LA34_0 >= '\u0CC6' && LA34_0 <= '\u0CC8')||(LA34_0 >= '\u0CCA' && LA34_0 <= '\u0CCD')||(LA34_0 >= '\u0CD5' && LA34_0 <= '\u0CD6')||LA34_0=='\u0CDE'||(LA34_0 >= '\u0CE0' && LA34_0 <= '\u0CE1')||(LA34_0 >= '\u0CE6' && LA34_0 <= '\u0CEF')||(LA34_0 >= '\u0D02' && LA34_0 <= '\u0D03')||(LA34_0 >= '\u0D05' && LA34_0 <= '\u0D0C')||(LA34_0 >= '\u0D0E' && LA34_0 <= '\u0D10')||(LA34_0 >= '\u0D12' && LA34_0 <= '\u0D28')||(LA34_0 >= '\u0D2A' && LA34_0 <= '\u0D39')||(LA34_0 >= '\u0D3E' && LA34_0 <= '\u0D43')||(LA34_0 >= '\u0D46' && LA34_0 <= '\u0D48')||(LA34_0 >= '\u0D4A' && LA34_0 <= '\u0D4D')||LA34_0=='\u0D57'||(LA34_0 >= '\u0D60' && LA34_0 <= '\u0D61')||(LA34_0 >= '\u0D66' && LA34_0 <= '\u0D6F')||(LA34_0 >= '\u0D82' && LA34_0 <= '\u0D83')||(LA34_0 >= '\u0D85' && LA34_0 <= '\u0D96')||(LA34_0 >= '\u0D9A' && LA34_0 <= '\u0DB1')||(LA34_0 >= '\u0DB3' && LA34_0 <= '\u0DBB')||LA34_0=='\u0DBD'||(LA34_0 >= '\u0DC0' && LA34_0 <= '\u0DC6')||LA34_0=='\u0DCA'||(LA34_0 >= '\u0DCF' && LA34_0 <= '\u0DD4')||LA34_0=='\u0DD6'||(LA34_0 >= '\u0DD8' && LA34_0 <= '\u0DDF')||(LA34_0 >= '\u0DF2' && LA34_0 <= '\u0DF4')||(LA34_0 >= '\u0E01' && LA34_0 <= '\u0E3A')||(LA34_0 >= '\u0E3F' && LA34_0 <= '\u0E5B')||(LA34_0 >= '\u0E81' && LA34_0 <= '\u0E82')||LA34_0=='\u0E84'||(LA34_0 >= '\u0E87' && LA34_0 <= '\u0E88')||LA34_0=='\u0E8A'||LA34_0=='\u0E8D'||(LA34_0 >= '\u0E94' && LA34_0 <= '\u0E97')||(LA34_0 >= '\u0E99' && LA34_0 <= '\u0E9F')||(LA34_0 >= '\u0EA1' && LA34_0 <= '\u0EA3')||LA34_0=='\u0EA5'||LA34_0=='\u0EA7'||(LA34_0 >= '\u0EAA' && LA34_0 <= '\u0EAB')||(LA34_0 >= '\u0EAD' && LA34_0 <= '\u0EB9')||(LA34_0 >= '\u0EBB' && LA34_0 <= '\u0EBD')||(LA34_0 >= '\u0EC0' && LA34_0 <= '\u0EC4')||LA34_0=='\u0EC6'||(LA34_0 >= '\u0EC8' && LA34_0 <= '\u0ECD')||(LA34_0 >= '\u0ED0' && LA34_0 <= '\u0ED9')||(LA34_0 >= '\u0EDC' && LA34_0 <= '\u0EDD')||(LA34_0 >= '\u0F00' && LA34_0 <= '\u0F39')||(LA34_0 >= '\u0F3E' && LA34_0 <= '\u0F47')||(LA34_0 >= '\u0F49' && LA34_0 <= '\u0F6A')||(LA34_0 >= '\u0F71' && LA34_0 <= '\u0F8B')||(LA34_0 >= '\u0F90' && LA34_0 <= '\u0F97')||(LA34_0 >= '\u0F99' && LA34_0 <= '\u0FBC')||(LA34_0 >= '\u0FBE' && LA34_0 <= '\u0FCC')||LA34_0=='\u0FCF'||(LA34_0 >= '\u1000' && LA34_0 <= '\u1021')||(LA34_0 >= '\u1023' && LA34_0 <= '\u1027')||(LA34_0 >= '\u1029' && LA34_0 <= '\u102A')||(LA34_0 >= '\u102C' && LA34_0 <= '\u1032')||(LA34_0 >= '\u1036' && LA34_0 <= '\u1039')||(LA34_0 >= '\u1040' && LA34_0 <= '\u1059')||(LA34_0 >= '\u10A0' && LA34_0 <= '\u10C5')||(LA34_0 >= '\u10D0' && LA34_0 <= '\u10F8')||LA34_0=='\u10FB'||(LA34_0 >= '\u1100' && LA34_0 <= '\u1159')||(LA34_0 >= '\u115F' && LA34_0 <= '\u11A2')||(LA34_0 >= '\u11A8' && LA34_0 <= '\u11F9')||(LA34_0 >= '\u1200' && LA34_0 <= '\u1206')||(LA34_0 >= '\u1208' && LA34_0 <= '\u1246')||LA34_0=='\u1248'||(LA34_0 >= '\u124A' && LA34_0 <= '\u124D')||(LA34_0 >= '\u1250' && LA34_0 <= '\u1256')||LA34_0=='\u1258'||(LA34_0 >= '\u125A' && LA34_0 <= '\u125D')||(LA34_0 >= '\u1260' && LA34_0 <= '\u1286')||LA34_0=='\u1288'||(LA34_0 >= '\u128A' && LA34_0 <= '\u128D')||(LA34_0 >= '\u1290' && LA34_0 <= '\u12AE')||LA34_0=='\u12B0'||(LA34_0 >= '\u12B2' && LA34_0 <= '\u12B5')||(LA34_0 >= '\u12B8' && LA34_0 <= '\u12BE')||LA34_0=='\u12C0'||(LA34_0 >= '\u12C2' && LA34_0 <= '\u12C5')||(LA34_0 >= '\u12C8' && LA34_0 <= '\u12CE')||(LA34_0 >= '\u12D0' && LA34_0 <= '\u12D6')||(LA34_0 >= '\u12D8' && LA34_0 <= '\u12EE')||(LA34_0 >= '\u12F0' && LA34_0 <= '\u130E')||LA34_0=='\u1310'||(LA34_0 >= '\u1312' && LA34_0 <= '\u1315')||(LA34_0 >= '\u1318' && LA34_0 <= '\u131E')||(LA34_0 >= '\u1320' && LA34_0 <= '\u1346')||(LA34_0 >= '\u1348' && LA34_0 <= '\u135A')||(LA34_0 >= '\u1361' && LA34_0 <= '\u137C')||(LA34_0 >= '\u13A0' && LA34_0 <= '\u13F4')||(LA34_0 >= '\u1401' && LA34_0 <= '\u1676')||(LA34_0 >= '\u1681' && LA34_0 <= '\u169A')||(LA34_0 >= '\u16A0' && LA34_0 <= '\u16F0')||(LA34_0 >= '\u1700' && LA34_0 <= '\u170C')||(LA34_0 >= '\u170E' && LA34_0 <= '\u1714')||(LA34_0 >= '\u1720' && LA34_0 <= '\u1736')||(LA34_0 >= '\u1740' && LA34_0 <= '\u1753')||(LA34_0 >= '\u1760' && LA34_0 <= '\u176C')||(LA34_0 >= '\u176E' && LA34_0 <= '\u1770')||(LA34_0 >= '\u1772' && LA34_0 <= '\u1773')||(LA34_0 >= '\u1780' && LA34_0 <= '\u17B3')||(LA34_0 >= '\u17B6' && LA34_0 <= '\u17DD')||(LA34_0 >= '\u17E0' && LA34_0 <= '\u17E9')||(LA34_0 >= '\u17F0' && LA34_0 <= '\u17F9')||(LA34_0 >= '\u1800' && LA34_0 <= '\u180D')||(LA34_0 >= '\u1810' && LA34_0 <= '\u1819')||(LA34_0 >= '\u1820' && LA34_0 <= '\u1877')||(LA34_0 >= '\u1880' && LA34_0 <= '\u18A9')||(LA34_0 >= '\u1900' && LA34_0 <= '\u191C')||(LA34_0 >= '\u1920' && LA34_0 <= '\u192B')||(LA34_0 >= '\u1930' && LA34_0 <= '\u193B')||LA34_0=='\u1940'||(LA34_0 >= '\u1944' && LA34_0 <= '\u196D')||(LA34_0 >= '\u1970' && LA34_0 <= '\u1974')||(LA34_0 >= '\u19E0' && LA34_0 <= '\u19FF')||(LA34_0 >= '\u1D00' && LA34_0 <= '\u1D6B')||(LA34_0 >= '\u1E00' && LA34_0 <= '\u1E9B')||(LA34_0 >= '\u1EA0' && LA34_0 <= '\u1EF9')||(LA34_0 >= '\u1F00' && LA34_0 <= '\u1F15')||(LA34_0 >= '\u1F18' && LA34_0 <= '\u1F1D')||(LA34_0 >= '\u1F20' && LA34_0 <= '\u1F45')||(LA34_0 >= '\u1F48' && LA34_0 <= '\u1F4D')||(LA34_0 >= '\u1F50' && LA34_0 <= '\u1F57')||LA34_0=='\u1F59'||LA34_0=='\u1F5B'||LA34_0=='\u1F5D'||(LA34_0 >= '\u1F5F' && LA34_0 <= '\u1F7D')||(LA34_0 >= '\u1F80' && LA34_0 <= '\u1FB4')||(LA34_0 >= '\u1FB6' && LA34_0 <= '\u1FBC')||LA34_0=='\u1FBE'||(LA34_0 >= '\u1FC2' && LA34_0 <= '\u1FC4')||(LA34_0 >= '\u1FC6' && LA34_0 <= '\u1FCC')||(LA34_0 >= '\u1FD0' && LA34_0 <= '\u1FD3')||(LA34_0 >= '\u1FD6' && LA34_0 <= '\u1FDB')||(LA34_0 >= '\u1FE0' && LA34_0 <= '\u1FEC')||(LA34_0 >= '\u1FF2' && LA34_0 <= '\u1FF4')||(LA34_0 >= '\u1FF6' && LA34_0 <= '\u1FFC')||(LA34_0 >= '\u2010' && LA34_0 <= '\u2017')||(LA34_0 >= '\u2020' && LA34_0 <= '\u2027')||(LA34_0 >= '\u2030' && LA34_0 <= '\u2038')||(LA34_0 >= '\u203B' && LA34_0 <= '\u2044')||(LA34_0 >= '\u2047' && LA34_0 <= '\u2054')||LA34_0=='\u2057'||(LA34_0 >= '\u2070' && LA34_0 <= '\u2071')||(LA34_0 >= '\u2074' && LA34_0 <= '\u207C')||(LA34_0 >= '\u207F' && LA34_0 <= '\u208C')||(LA34_0 >= '\u20A0' && LA34_0 <= '\u20B1')||(LA34_0 >= '\u20D0' && LA34_0 <= '\u20EA')||(LA34_0 >= '\u2100' && LA34_0 <= '\u213B')||(LA34_0 >= '\u213D' && LA34_0 <= '\u214B')||(LA34_0 >= '\u2153' && LA34_0 <= '\u2183')||(LA34_0 >= '\u2190' && LA34_0 <= '\u2328')||(LA34_0 >= '\u232B' && LA34_0 <= '\u23B3')||(LA34_0 >= '\u23B6' && LA34_0 <= '\u23D0')||(LA34_0 >= '\u2400' && LA34_0 <= '\u2426')||(LA34_0 >= '\u2440' && LA34_0 <= '\u244A')||(LA34_0 >= '\u2460' && LA34_0 <= '\u2617')||(LA34_0 >= '\u2619' && LA34_0 <= '\u267D')||(LA34_0 >= '\u2680' && LA34_0 <= '\u2691')||(LA34_0 >= '\u26A0' && LA34_0 <= '\u26A1')||(LA34_0 >= '\u2701' && LA34_0 <= '\u2704')||(LA34_0 >= '\u2706' && LA34_0 <= '\u2709')||(LA34_0 >= '\u270C' && LA34_0 <= '\u2727')||(LA34_0 >= '\u2729' && LA34_0 <= '\u274B')||LA34_0=='\u274D'||(LA34_0 >= '\u274F' && LA34_0 <= '\u2752')||LA34_0=='\u2756'||(LA34_0 >= '\u2758' && LA34_0 <= '\u275E')||(LA34_0 >= '\u2761' && LA34_0 <= '\u2767')||(LA34_0 >= '\u2776' && LA34_0 <= '\u2794')||(LA34_0 >= '\u2798' && LA34_0 <= '\u27AF')||(LA34_0 >= '\u27B1' && LA34_0 <= '\u27BE')||(LA34_0 >= '\u27D0' && LA34_0 <= '\u27E5')||(LA34_0 >= '\u27F0' && LA34_0 <= '\u2982')||(LA34_0 >= '\u2999' && LA34_0 <= '\u29D7')||(LA34_0 >= '\u29DC' && LA34_0 <= '\u29FB')||(LA34_0 >= '\u29FE' && LA34_0 <= '\u2B0D')||(LA34_0 >= '\u2E80' && LA34_0 <= '\u2E99')||(LA34_0 >= '\u2E9B' && LA34_0 <= '\u2EF3')||(LA34_0 >= '\u2F00' && LA34_0 <= '\u2FD5')||(LA34_0 >= '\u2FF0' && LA34_0 <= '\u2FFB')||(LA34_0 >= '\u3001' && LA34_0 <= '\u3007')||(LA34_0 >= '\u3012' && LA34_0 <= '\u3013')||LA34_0=='\u301C'||(LA34_0 >= '\u3020' && LA34_0 <= '\u303F')||(LA34_0 >= '\u3041' && LA34_0 <= '\u3096')||(LA34_0 >= '\u3099' && LA34_0 <= '\u309A')||(LA34_0 >= '\u309D' && LA34_0 <= '\u30FF')||(LA34_0 >= '\u3105' && LA34_0 <= '\u312C')||(LA34_0 >= '\u3131' && LA34_0 <= '\u318E')||(LA34_0 >= '\u3190' && LA34_0 <= '\u31B7')||(LA34_0 >= '\u31F0' && LA34_0 <= '\u321E')||(LA34_0 >= '\u3220' && LA34_0 <= '\u3243')||(LA34_0 >= '\u3250' && LA34_0 <= '\u327D')||(LA34_0 >= '\u327F' && LA34_0 <= '\u32FE')||(LA34_0 >= '\u3300' && LA34_0 <= '\u4DB5')||(LA34_0 >= '\u4DC0' && LA34_0 <= '\u9FA5')||(LA34_0 >= '\uA000' && LA34_0 <= '\uA48C')||(LA34_0 >= '\uA490' && LA34_0 <= '\uA4C6')||(LA34_0 >= '\uAC00' && LA34_0 <= '\uD7A3')||(LA34_0 >= '\uF900' && LA34_0 <= '\uFA2D')||(LA34_0 >= '\uFA30' && LA34_0 <= '\uFA6A')||(LA34_0 >= '\uFB00' && LA34_0 <= '\uFB06')||(LA34_0 >= '\uFB13' && LA34_0 <= '\uFB17')||(LA34_0 >= '\uFB1D' && LA34_0 <= '\uFB36')||(LA34_0 >= '\uFB38' && LA34_0 <= '\uFB3C')||LA34_0=='\uFB3E'||(LA34_0 >= '\uFB40' && LA34_0 <= '\uFB41')||(LA34_0 >= '\uFB43' && LA34_0 <= '\uFB44')||(LA34_0 >= '\uFB46' && LA34_0 <= '\uFBB1')||(LA34_0 >= '\uFBD3' && LA34_0 <= '\uFD3D')||(LA34_0 >= '\uFD50' && LA34_0 <= '\uFD8F')||(LA34_0 >= '\uFD92' && LA34_0 <= '\uFDC7')||(LA34_0 >= '\uFDF0' && LA34_0 <= '\uFDFD')||(LA34_0 >= '\uFE00' && LA34_0 <= '\uFE0F')||(LA34_0 >= '\uFE20' && LA34_0 <= '\uFE23')||(LA34_0 >= '\uFE30' && LA34_0 <= '\uFE34')||(LA34_0 >= '\uFE45' && LA34_0 <= '\uFE46')||(LA34_0 >= '\uFE49' && LA34_0 <= '\uFE52')||(LA34_0 >= '\uFE54' && LA34_0 <= '\uFE58')||(LA34_0 >= '\uFE5F' && LA34_0 <= '\uFE66')||(LA34_0 >= '\uFE68' && LA34_0 <= '\uFE6B')||(LA34_0 >= '\uFE70' && LA34_0 <= '\uFE74')||(LA34_0 >= '\uFE76' && LA34_0 <= '\uFEFC')||(LA34_0 >= '\uFF01' && LA34_0 <= '\uFF07')||(LA34_0 >= '\uFF0A' && LA34_0 <= '\uFF3A')||LA34_0=='\uFF3C'||LA34_0=='\uFF3F'||(LA34_0 >= '\uFF41' && LA34_0 <= '\uFF5A')||LA34_0=='\uFF5C'||LA34_0=='\uFF5E'||LA34_0=='\uFF61'||(LA34_0 >= '\uFF64' && LA34_0 <= '\uFFBE')||(LA34_0 >= '\uFFC2' && LA34_0 <= '\uFFC7')||(LA34_0 >= '\uFFCA' && LA34_0 <= '\uFFCF')||(LA34_0 >= '\uFFD2' && LA34_0 <= '\uFFD7')||(LA34_0 >= '\uFFDA' && LA34_0 <= '\uFFDC')||(LA34_0 >= '\uFFE0' && LA34_0 <= '\uFFE2')||(LA34_0 >= '\uFFE4' && LA34_0 <= '\uFFE6')||(LA34_0 >= '\uFFE8' && LA34_0 <= '\uFFEE')) ) {
                    alt34=2;
                }
                else if ( (LA34_0=='*') ) {
                    alt34=3;
                }
                else if ( (LA34_0=='?') ) {
                    alt34=4;
                }


                switch (alt34) {
            	case 1 :
            	    // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1475:17: F_ESC
            	    {
            	    mF_ESC(); if (state.failed) return ;


            	    }
            	    break;
            	case 2 :
            	    // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1476:19: IN_WORD
            	    {
            	    mIN_WORD(); if (state.failed) return ;


            	    }
            	    break;
            	case 3 :
            	    // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1477:19: STAR
            	    {
            	    mSTAR(); if (state.failed) return ;


            	    }
            	    break;
            	case 4 :
            	    // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1478:19: QUESTION_MARK
            	    {
            	    mQUESTION_MARK(); if (state.failed) return ;


            	    }
            	    break;

            	default :
            	    break loop34;
                }
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "FTSWILD"

    // $ANTLR start "F_ESC"
    public final void mF_ESC() throws RecognitionException {
        try {
            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1485:9: ( '\\\\' ( 'u' F_HEX F_HEX F_HEX F_HEX | . ) )
            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1486:9: '\\\\' ( 'u' F_HEX F_HEX F_HEX F_HEX | . )
            {
            match('\\'); if (state.failed) return ;

            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1487:9: ( 'u' F_HEX F_HEX F_HEX F_HEX | . )
            int alt35=2;
            int LA35_0 = input.LA(1);

            if ( (LA35_0=='u') ) {
                int LA35_1 = input.LA(2);

                if ( ((LA35_1 >= '0' && LA35_1 <= '9')||(LA35_1 >= 'A' && LA35_1 <= 'F')||(LA35_1 >= 'a' && LA35_1 <= 'f')) ) {
                    alt35=1;
                }
                else {
                    alt35=2;
                }
            }
            else if ( ((LA35_0 >= '\u0000' && LA35_0 <= 't')||(LA35_0 >= 'v' && LA35_0 <= '\uFFFF')) ) {
                alt35=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 35, 0, input);

                throw nvae;

            }
            switch (alt35) {
                case 1 :
                    // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1489:17: 'u' F_HEX F_HEX F_HEX F_HEX
                    {
                    match('u'); if (state.failed) return ;

                    mF_HEX(); if (state.failed) return ;


                    mF_HEX(); if (state.failed) return ;


                    mF_HEX(); if (state.failed) return ;


                    mF_HEX(); if (state.failed) return ;


                    }
                    break;
                case 2 :
                    // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1491:19: .
                    {
                    matchAny(); if (state.failed) return ;

                    }
                    break;

            }


            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "F_ESC"

    // $ANTLR start "F_HEX"
    public final void mF_HEX() throws RecognitionException {
        try {
            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1497:9: ( '0' .. '9' | 'a' .. 'f' | 'A' .. 'F' )
            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:
            {
            if ( (input.LA(1) >= '0' && input.LA(1) <= '9')||(input.LA(1) >= 'A' && input.LA(1) <= 'F')||(input.LA(1) >= 'a' && input.LA(1) <= 'f') ) {
                input.consume();
                state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "F_HEX"

    // $ANTLR start "START_WORD"
    public final void mSTART_WORD() throws RecognitionException {
        try {
            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1505:9: ( '\\u0024' | '\\u0030' .. '\\u0039' | '\\u0041' .. '\\u005a' | '\\u0061' .. '\\u007a' | '\\u00a2' .. '\\u00a7' | '\\u00a9' .. '\\u00aa' | '\\u00ae' | '\\u00b0' | '\\u00b2' .. '\\u00b3' | '\\u00b5' .. '\\u00b6' | '\\u00b9' .. '\\u00ba' | '\\u00bc' .. '\\u00be' | '\\u00c0' .. '\\u00d6' | '\\u00d8' .. '\\u00f6' | '\\u00f8' .. '\\u0236' | '\\u0250' .. '\\u02c1' | '\\u02c6' .. '\\u02d1' | '\\u02e0' .. '\\u02e4' | '\\u02ee' | '\\u0300' .. '\\u0357' | '\\u035d' .. '\\u036f' | '\\u037a' .. '\\u037a' | '\\u0386' | '\\u0388' .. '\\u038a' | '\\u038c' .. '\\u038c' | '\\u038e' .. '\\u03a1' | '\\u03a3' .. '\\u03ce' | '\\u03d0' .. '\\u03f5' | '\\u03f7' .. '\\u03fb' | '\\u0400' .. '\\u0486' | '\\u0488' .. '\\u04ce' | '\\u04d0' .. '\\u04f5' | '\\u04f8' .. '\\u04f9' | '\\u0500' .. '\\u050f' | '\\u0531' .. '\\u0556' | '\\u0559' | '\\u0561' .. '\\u0587' | '\\u0591' .. '\\u05a1' | '\\u05a3' .. '\\u05b9' | '\\u05bb' .. '\\u05bd' | '\\u05bf' | '\\u05c1' .. '\\u05c2' | '\\u05c4' .. '\\u05c4' | '\\u05d0' .. '\\u05ea' | '\\u05f0' .. '\\u05f2' | '\\u060e' .. '\\u0615' | '\\u0621' .. '\\u063a' | '\\u0640' .. '\\u0658' | '\\u0660' .. '\\u0669' | '\\u066e' .. '\\u06d3' | '\\u06d5' .. '\\u06dc' | '\\u06de' .. '\\u06ff' | '\\u0710' .. '\\u074a' | '\\u074d' .. '\\u074f' | '\\u0780' .. '\\u07b1' | '\\u0901' .. '\\u0939' | '\\u093c' .. '\\u094d' | '\\u0950' .. '\\u0954' | '\\u0958' .. '\\u0963' | '\\u0966' .. '\\u096f' | '\\u0981' .. '\\u0983' | '\\u0985' .. '\\u098c' | '\\u098f' .. '\\u0990' | '\\u0993' .. '\\u09a8' | '\\u09aa' .. '\\u09b0' | '\\u09b2' .. '\\u09b2' | '\\u09b6' .. '\\u09b9' | '\\u09bc' .. '\\u09c4' | '\\u09c7' .. '\\u09c8' | '\\u09cb' .. '\\u09cd' | '\\u09d7' .. '\\u09d7' | '\\u09dc' .. '\\u09dd' | '\\u09df' .. '\\u09e3' | '\\u09e6' .. '\\u09fa' | '\\u0a01' .. '\\u0a03' | '\\u0a05' .. '\\u0a0a' | '\\u0a0f' .. '\\u0a10' | '\\u0a13' .. '\\u0a28' | '\\u0a2a' .. '\\u0a30' | '\\u0a32' .. '\\u0a33' | '\\u0a35' .. '\\u0a36' | '\\u0a38' .. '\\u0a39' | '\\u0a3c' .. '\\u0a3c' | '\\u0a3e' .. '\\u0a42' | '\\u0a47' .. '\\u0a48' | '\\u0a4b' .. '\\u0a4d' | '\\u0a59' .. '\\u0a5c' | '\\u0a5e' .. '\\u0a5e' | '\\u0a66' .. '\\u0a74' | '\\u0a81' .. '\\u0a83' | '\\u0a85' .. '\\u0a8d' | '\\u0a8f' .. '\\u0a91' | '\\u0a93' .. '\\u0aa8' | '\\u0aaa' .. '\\u0ab0' | '\\u0ab2' .. '\\u0ab3' | '\\u0ab5' .. '\\u0ab9' | '\\u0abc' .. '\\u0ac5' | '\\u0ac7' .. '\\u0ac9' | '\\u0acb' .. '\\u0acd' | '\\u0ad0' .. '\\u0ad0' | '\\u0ae0' .. '\\u0ae3' | '\\u0ae6' .. '\\u0aef' | '\\u0af1' .. '\\u0af1' | '\\u0b01' .. '\\u0b03' | '\\u0b05' .. '\\u0b0c' | '\\u0b0f' .. '\\u0b10' | '\\u0b13' .. '\\u0b28' | '\\u0b2a' .. '\\u0b30' | '\\u0b32' .. '\\u0b33' | '\\u0b35' .. '\\u0b39' | '\\u0b3c' .. '\\u0b43' | '\\u0b47' .. '\\u0b48' | '\\u0b4b' .. '\\u0b4d' | '\\u0b56' .. '\\u0b57' | '\\u0b5c' .. '\\u0b5d' | '\\u0b5f' .. '\\u0b61' | '\\u0b66' .. '\\u0b71' | '\\u0b82' .. '\\u0b83' | '\\u0b85' .. '\\u0b8a' | '\\u0b8e' .. '\\u0b90' | '\\u0b92' .. '\\u0b95' | '\\u0b99' .. '\\u0b9a' | '\\u0b9c' .. '\\u0b9c' | '\\u0b9e' .. '\\u0b9f' | '\\u0ba3' .. '\\u0ba4' | '\\u0ba8' .. '\\u0baa' | '\\u0bae' .. '\\u0bb5' | '\\u0bb7' .. '\\u0bb9' | '\\u0bbe' .. '\\u0bc2' | '\\u0bc6' .. '\\u0bc8' | '\\u0bca' .. '\\u0bcd' | '\\u0bd7' .. '\\u0bd7' | '\\u0be7' .. '\\u0bfa' | '\\u0c01' .. '\\u0c03' | '\\u0c05' .. '\\u0c0c' | '\\u0c0e' .. '\\u0c10' | '\\u0c12' .. '\\u0c28' | '\\u0c2a' .. '\\u0c33' | '\\u0c35' .. '\\u0c39' | '\\u0c3e' .. '\\u0c44' | '\\u0c46' .. '\\u0c48' | '\\u0c4a' .. '\\u0c4d' | '\\u0c55' .. '\\u0c56' | '\\u0c60' .. '\\u0c61' | '\\u0c66' .. '\\u0c6f' | '\\u0c82' .. '\\u0c83' | '\\u0c85' .. '\\u0c8c' | '\\u0c8e' .. '\\u0c90' | '\\u0c92' .. '\\u0ca8' | '\\u0caa' .. '\\u0cb3' | '\\u0cb5' .. '\\u0cb9' | '\\u0cbc' .. '\\u0cc4' | '\\u0cc6' .. '\\u0cc8' | '\\u0cca' .. '\\u0ccd' | '\\u0cd5' .. '\\u0cd6' | '\\u0cde' .. '\\u0cde' | '\\u0ce0' .. '\\u0ce1' | '\\u0ce6' .. '\\u0cef' | '\\u0d02' .. '\\u0d03' | '\\u0d05' .. '\\u0d0c' | '\\u0d0e' .. '\\u0d10' | '\\u0d12' .. '\\u0d28' | '\\u0d2a' .. '\\u0d39' | '\\u0d3e' .. '\\u0d43' | '\\u0d46' .. '\\u0d48' | '\\u0d4a' .. '\\u0d4d' | '\\u0d57' .. '\\u0d57' | '\\u0d60' .. '\\u0d61' | '\\u0d66' .. '\\u0d6f' | '\\u0d82' .. '\\u0d83' | '\\u0d85' .. '\\u0d96' | '\\u0d9a' .. '\\u0db1' | '\\u0db3' .. '\\u0dbb' | '\\u0dbd' .. '\\u0dbd' | '\\u0dc0' .. '\\u0dc6' | '\\u0dca' .. '\\u0dca' | '\\u0dcf' .. '\\u0dd4' | '\\u0dd6' .. '\\u0dd6' | '\\u0dd8' .. '\\u0ddf' | '\\u0df2' .. '\\u0df3' | '\\u0e01' .. '\\u0e3a' | '\\u0e3f' .. '\\u0e4e' | '\\u0e50' .. '\\u0e59' | '\\u0e81' .. '\\u0e82' | '\\u0e84' .. '\\u0e84' | '\\u0e87' .. '\\u0e88' | '\\u0e8a' .. '\\u0e8a' | '\\u0e8d' .. '\\u0e8d' | '\\u0e94' .. '\\u0e97' | '\\u0e99' .. '\\u0e9f' | '\\u0ea1' .. '\\u0ea3' | '\\u0ea5' .. '\\u0ea5' | '\\u0ea7' .. '\\u0ea7' | '\\u0eaa' .. '\\u0eab' | '\\u0ead' .. '\\u0eb9' | '\\u0ebb' .. '\\u0ebd' | '\\u0ec0' .. '\\u0ec4' | '\\u0ec6' .. '\\u0ec6' | '\\u0ec8' .. '\\u0ecd' | '\\u0ed0' .. '\\u0ed9' | '\\u0edc' .. '\\u0edd' | '\\u0f00' .. '\\u0f03' | '\\u0f13' .. '\\u0f39' | '\\u0f3e' .. '\\u0f47' | '\\u0f49' .. '\\u0f6a' | '\\u0f71' .. '\\u0f84' | '\\u0f86' .. '\\u0f8b' | '\\u0f90' .. '\\u0f97' | '\\u0f99' .. '\\u0fbc' | '\\u0fbe' .. '\\u0fcc' | '\\u0fcf' .. '\\u0fcf' | '\\u1000' .. '\\u1021' | '\\u1023' .. '\\u1027' | '\\u1029' .. '\\u102a' | '\\u102c' .. '\\u1032' | '\\u1036' .. '\\u1039' | '\\u1040' .. '\\u1049' | '\\u1050' .. '\\u1059' | '\\u10a0' .. '\\u10c5' | '\\u10d0' .. '\\u10f8' | '\\u1100' .. '\\u1159' | '\\u115f' .. '\\u11a2' | '\\u11a8' .. '\\u11f9' | '\\u1200' .. '\\u1206' | '\\u1208' .. '\\u1246' | '\\u1248' .. '\\u1248' | '\\u124a' .. '\\u124d' | '\\u1250' .. '\\u1256' | '\\u1258' .. '\\u1258' | '\\u125a' .. '\\u125d' | '\\u1260' .. '\\u1286' | '\\u1288' .. '\\u1288' | '\\u128a' .. '\\u128d' | '\\u1290' .. '\\u12ae' | '\\u12b0' .. '\\u12b0' | '\\u12b2' .. '\\u12b5' | '\\u12b8' .. '\\u12be' | '\\u12c0' .. '\\u12c0' | '\\u12c2' .. '\\u12c5' | '\\u12c8' .. '\\u12ce' | '\\u12d0' .. '\\u12d6' | '\\u12d8' .. '\\u12ee' | '\\u12f0' .. '\\u130e' | '\\u1310' .. '\\u1310' | '\\u1312' .. '\\u1315' | '\\u1318' .. '\\u131e' | '\\u1320' .. '\\u1346' | '\\u1348' .. '\\u135a' | '\\u1369' .. '\\u137c' | '\\u13a0' .. '\\u13f4' | '\\u1401' .. '\\u166c' | '\\u166f' .. '\\u1676' | '\\u1681' .. '\\u169a' | '\\u16a0' .. '\\u16ea' | '\\u16ee' .. '\\u16f0' | '\\u1700' .. '\\u170c' | '\\u170e' .. '\\u1714' | '\\u1720' .. '\\u1734' | '\\u1740' .. '\\u1753' | '\\u1760' .. '\\u176c' | '\\u176e' .. '\\u1770' | '\\u1772' .. '\\u1773' | '\\u1780' .. '\\u17b3' | '\\u17b6' .. '\\u17d3' | '\\u17d7' | '\\u17db' .. '\\u17dd' | '\\u17e0' .. '\\u17e9' | '\\u17f0' .. '\\u17f9' | '\\u180b' .. '\\u180d' | '\\u1810' .. '\\u1819' | '\\u1820' .. '\\u1877' | '\\u1880' .. '\\u18a9' | '\\u1900' .. '\\u191c' | '\\u1920' .. '\\u192b' | '\\u1930' .. '\\u193b' | '\\u1940' .. '\\u1940' | '\\u1946' .. '\\u196d' | '\\u1970' .. '\\u1974' | '\\u19e0' .. '\\u19ff' | '\\u1d00' .. '\\u1d6b' | '\\u1e00' .. '\\u1e9b' | '\\u1ea0' .. '\\u1ef9' | '\\u1f00' .. '\\u1f15' | '\\u1f18' .. '\\u1f1d' | '\\u1f20' .. '\\u1f45' | '\\u1f48' .. '\\u1f4d' | '\\u1f50' .. '\\u1f57' | '\\u1f59' .. '\\u1f59' | '\\u1f5b' .. '\\u1f5b' | '\\u1f5d' .. '\\u1f5d' | '\\u1f5f' .. '\\u1f7d' | '\\u1f80' .. '\\u1fb4' | '\\u1fb6' .. '\\u1fbc' | '\\u1fbe' | '\\u1fc2' .. '\\u1fc4' | '\\u1fc6' .. '\\u1fcc' | '\\u1fd0' .. '\\u1fd3' | '\\u1fd6' .. '\\u1fdb' | '\\u1fe0' .. '\\u1fec' | '\\u1ff2' .. '\\u1ff4' | '\\u1ff6' .. '\\u1ffc' | '\\u2070' .. '\\u2071' | '\\u2074' .. '\\u2079' | '\\u207f' .. '\\u2089' | '\\u20a0' .. '\\u20b1' | '\\u20d0' .. '\\u20ea' | '\\u2100' .. '\\u213b' | '\\u213d' .. '\\u213f' | '\\u2145' .. '\\u214a' | '\\u2153' .. '\\u2183' | '\\u2195' .. '\\u2199' | '\\u219c' .. '\\u219f' | '\\u21a1' .. '\\u21a2' | '\\u21a4' .. '\\u21a5' | '\\u21a7' .. '\\u21ad' | '\\u21af' .. '\\u21cd' | '\\u21d0' .. '\\u21d1' | '\\u21d3' | '\\u21d5' .. '\\u21f3' | '\\u2300' .. '\\u2307' | '\\u230c' .. '\\u231f' | '\\u2322' .. '\\u2328' | '\\u232b' .. '\\u237b' | '\\u237d' .. '\\u239a' | '\\u23b7' .. '\\u23d0' | '\\u2400' .. '\\u2426' | '\\u2440' .. '\\u244a' | '\\u2460' .. '\\u25b6' | '\\u25b8' .. '\\u25c0' | '\\u25c2' .. '\\u25f7' | '\\u2600' .. '\\u2617' | '\\u2619' .. '\\u266e' | '\\u2670' .. '\\u267d' | '\\u2680' .. '\\u2691' | '\\u26a0' .. '\\u26a1' | '\\u2701' .. '\\u2704' | '\\u2706' .. '\\u2709' | '\\u270c' .. '\\u2727' | '\\u2729' .. '\\u274b' | '\\u274d' .. '\\u274d' | '\\u274f' .. '\\u2752' | '\\u2756' .. '\\u2756' | '\\u2758' .. '\\u275e' | '\\u2761' .. '\\u2767' | '\\u2776' .. '\\u2794' | '\\u2798' .. '\\u27af' | '\\u27b1' .. '\\u27be' | '\\u2800' .. '\\u28ff' | '\\u2b00' .. '\\u2b0d' | '\\u2e80' .. '\\u2e99' | '\\u2e9b' .. '\\u2ef3' | '\\u2f00' .. '\\u2fd5' | '\\u2ff0' .. '\\u2ffb' | '\\u3004' .. '\\u3007' | '\\u3012' .. '\\u3013' | '\\u3020' .. '\\u302f' | '\\u3031' .. '\\u303c' | '\\u303e' .. '\\u303f' | '\\u3041' .. '\\u3096' | '\\u3099' .. '\\u309a' | '\\u309d' .. '\\u309f' | '\\u30a1' .. '\\u30fa' | '\\u30fc' .. '\\u30ff' | '\\u3105' .. '\\u312c' | '\\u3131' .. '\\u318e' | '\\u3190' .. '\\u31b7' | '\\u31f0' .. '\\u321e' | '\\u3220' .. '\\u3243' | '\\u3250' .. '\\u327d' | '\\u327f' .. '\\u32fe' | '\\u3300' .. '\\u4db5' | '\\u4dc0' .. '\\u9fa5' | '\\ua000' .. '\\ua48c' | '\\ua490' .. '\\ua4c6' | '\\uac00' .. '\\ud7a3' | '\\uf900' .. '\\ufa2d' | '\\ufa30' .. '\\ufa6a' | '\\ufb00' .. '\\ufb06' | '\\ufb13' .. '\\ufb17' | '\\ufb1d' .. '\\ufb28' | '\\ufb2a' .. '\\ufb36' | '\\ufb38' .. '\\ufb3c' | '\\ufb3e' .. '\\ufb3e' | '\\ufb40' .. '\\ufb41' | '\\ufb43' .. '\\ufb44' | '\\ufb46' .. '\\ufbb1' | '\\ufbd3' .. '\\ufd3d' | '\\ufd50' .. '\\ufd8f' | '\\ufd92' .. '\\ufdc7' | '\\ufdf0' .. '\\ufdfd' | '\\ufe00' .. '\\ufe0f' | '\\ufe20' .. '\\ufe23' | '\\ufe69' | '\\ufe70' .. '\\ufe74' | '\\ufe76' .. '\\ufefc' | '\\uff04' | '\\uff10' .. '\\uff19' | '\\uff21' .. '\\uff3a' | '\\uff41' .. '\\uff5a' | '\\uff66' .. '\\uffbe' | '\\uffc2' .. '\\uffc7' | '\\uffca' .. '\\uffcf' | '\\uffd2' .. '\\uffd7' | '\\uffda' .. '\\uffdc' | '\\uffe0' .. '\\uffe1' | '\\uffe4' .. '\\uffe6' | '\\uffe8' | '\\uffed' .. '\\uffee' )
            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:
            {
            if ( input.LA(1)=='$'||(input.LA(1) >= '0' && input.LA(1) <= '9')||(input.LA(1) >= 'A' && input.LA(1) <= 'Z')||(input.LA(1) >= 'a' && input.LA(1) <= 'z')||(input.LA(1) >= '\u00A2' && input.LA(1) <= '\u00A7')||(input.LA(1) >= '\u00A9' && input.LA(1) <= '\u00AA')||input.LA(1)=='\u00AE'||input.LA(1)=='\u00B0'||(input.LA(1) >= '\u00B2' && input.LA(1) <= '\u00B3')||(input.LA(1) >= '\u00B5' && input.LA(1) <= '\u00B6')||(input.LA(1) >= '\u00B9' && input.LA(1) <= '\u00BA')||(input.LA(1) >= '\u00BC' && input.LA(1) <= '\u00BE')||(input.LA(1) >= '\u00C0' && input.LA(1) <= '\u00D6')||(input.LA(1) >= '\u00D8' && input.LA(1) <= '\u00F6')||(input.LA(1) >= '\u00F8' && input.LA(1) <= '\u0236')||(input.LA(1) >= '\u0250' && input.LA(1) <= '\u02C1')||(input.LA(1) >= '\u02C6' && input.LA(1) <= '\u02D1')||(input.LA(1) >= '\u02E0' && input.LA(1) <= '\u02E4')||input.LA(1)=='\u02EE'||(input.LA(1) >= '\u0300' && input.LA(1) <= '\u0357')||(input.LA(1) >= '\u035D' && input.LA(1) <= '\u036F')||input.LA(1)=='\u037A'||input.LA(1)=='\u0386'||(input.LA(1) >= '\u0388' && input.LA(1) <= '\u038A')||input.LA(1)=='\u038C'||(input.LA(1) >= '\u038E' && input.LA(1) <= '\u03A1')||(input.LA(1) >= '\u03A3' && input.LA(1) <= '\u03CE')||(input.LA(1) >= '\u03D0' && input.LA(1) <= '\u03F5')||(input.LA(1) >= '\u03F7' && input.LA(1) <= '\u03FB')||(input.LA(1) >= '\u0400' && input.LA(1) <= '\u0486')||(input.LA(1) >= '\u0488' && input.LA(1) <= '\u04CE')||(input.LA(1) >= '\u04D0' && input.LA(1) <= '\u04F5')||(input.LA(1) >= '\u04F8' && input.LA(1) <= '\u04F9')||(input.LA(1) >= '\u0500' && input.LA(1) <= '\u050F')||(input.LA(1) >= '\u0531' && input.LA(1) <= '\u0556')||input.LA(1)=='\u0559'||(input.LA(1) >= '\u0561' && input.LA(1) <= '\u0587')||(input.LA(1) >= '\u0591' && input.LA(1) <= '\u05A1')||(input.LA(1) >= '\u05A3' && input.LA(1) <= '\u05B9')||(input.LA(1) >= '\u05BB' && input.LA(1) <= '\u05BD')||input.LA(1)=='\u05BF'||(input.LA(1) >= '\u05C1' && input.LA(1) <= '\u05C2')||input.LA(1)=='\u05C4'||(input.LA(1) >= '\u05D0' && input.LA(1) <= '\u05EA')||(input.LA(1) >= '\u05F0' && input.LA(1) <= '\u05F2')||(input.LA(1) >= '\u060E' && input.LA(1) <= '\u0615')||(input.LA(1) >= '\u0621' && input.LA(1) <= '\u063A')||(input.LA(1) >= '\u0640' && input.LA(1) <= '\u0658')||(input.LA(1) >= '\u0660' && input.LA(1) <= '\u0669')||(input.LA(1) >= '\u066E' && input.LA(1) <= '\u06D3')||(input.LA(1) >= '\u06D5' && input.LA(1) <= '\u06DC')||(input.LA(1) >= '\u06DE' && input.LA(1) <= '\u06FF')||(input.LA(1) >= '\u0710' && input.LA(1) <= '\u074A')||(input.LA(1) >= '\u074D' && input.LA(1) <= '\u074F')||(input.LA(1) >= '\u0780' && input.LA(1) <= '\u07B1')||(input.LA(1) >= '\u0901' && input.LA(1) <= '\u0939')||(input.LA(1) >= '\u093C' && input.LA(1) <= '\u094D')||(input.LA(1) >= '\u0950' && input.LA(1) <= '\u0954')||(input.LA(1) >= '\u0958' && input.LA(1) <= '\u0963')||(input.LA(1) >= '\u0966' && input.LA(1) <= '\u096F')||(input.LA(1) >= '\u0981' && input.LA(1) <= '\u0983')||(input.LA(1) >= '\u0985' && input.LA(1) <= '\u098C')||(input.LA(1) >= '\u098F' && input.LA(1) <= '\u0990')||(input.LA(1) >= '\u0993' && input.LA(1) <= '\u09A8')||(input.LA(1) >= '\u09AA' && input.LA(1) <= '\u09B0')||input.LA(1)=='\u09B2'||(input.LA(1) >= '\u09B6' && input.LA(1) <= '\u09B9')||(input.LA(1) >= '\u09BC' && input.LA(1) <= '\u09C4')||(input.LA(1) >= '\u09C7' && input.LA(1) <= '\u09C8')||(input.LA(1) >= '\u09CB' && input.LA(1) <= '\u09CD')||input.LA(1)=='\u09D7'||(input.LA(1) >= '\u09DC' && input.LA(1) <= '\u09DD')||(input.LA(1) >= '\u09DF' && input.LA(1) <= '\u09E3')||(input.LA(1) >= '\u09E6' && input.LA(1) <= '\u09FA')||(input.LA(1) >= '\u0A01' && input.LA(1) <= '\u0A03')||(input.LA(1) >= '\u0A05' && input.LA(1) <= '\u0A0A')||(input.LA(1) >= '\u0A0F' && input.LA(1) <= '\u0A10')||(input.LA(1) >= '\u0A13' && input.LA(1) <= '\u0A28')||(input.LA(1) >= '\u0A2A' && input.LA(1) <= '\u0A30')||(input.LA(1) >= '\u0A32' && input.LA(1) <= '\u0A33')||(input.LA(1) >= '\u0A35' && input.LA(1) <= '\u0A36')||(input.LA(1) >= '\u0A38' && input.LA(1) <= '\u0A39')||input.LA(1)=='\u0A3C'||(input.LA(1) >= '\u0A3E' && input.LA(1) <= '\u0A42')||(input.LA(1) >= '\u0A47' && input.LA(1) <= '\u0A48')||(input.LA(1) >= '\u0A4B' && input.LA(1) <= '\u0A4D')||(input.LA(1) >= '\u0A59' && input.LA(1) <= '\u0A5C')||input.LA(1)=='\u0A5E'||(input.LA(1) >= '\u0A66' && input.LA(1) <= '\u0A74')||(input.LA(1) >= '\u0A81' && input.LA(1) <= '\u0A83')||(input.LA(1) >= '\u0A85' && input.LA(1) <= '\u0A8D')||(input.LA(1) >= '\u0A8F' && input.LA(1) <= '\u0A91')||(input.LA(1) >= '\u0A93' && input.LA(1) <= '\u0AA8')||(input.LA(1) >= '\u0AAA' && input.LA(1) <= '\u0AB0')||(input.LA(1) >= '\u0AB2' && input.LA(1) <= '\u0AB3')||(input.LA(1) >= '\u0AB5' && input.LA(1) <= '\u0AB9')||(input.LA(1) >= '\u0ABC' && input.LA(1) <= '\u0AC5')||(input.LA(1) >= '\u0AC7' && input.LA(1) <= '\u0AC9')||(input.LA(1) >= '\u0ACB' && input.LA(1) <= '\u0ACD')||input.LA(1)=='\u0AD0'||(input.LA(1) >= '\u0AE0' && input.LA(1) <= '\u0AE3')||(input.LA(1) >= '\u0AE6' && input.LA(1) <= '\u0AEF')||input.LA(1)=='\u0AF1'||(input.LA(1) >= '\u0B01' && input.LA(1) <= '\u0B03')||(input.LA(1) >= '\u0B05' && input.LA(1) <= '\u0B0C')||(input.LA(1) >= '\u0B0F' && input.LA(1) <= '\u0B10')||(input.LA(1) >= '\u0B13' && input.LA(1) <= '\u0B28')||(input.LA(1) >= '\u0B2A' && input.LA(1) <= '\u0B30')||(input.LA(1) >= '\u0B32' && input.LA(1) <= '\u0B33')||(input.LA(1) >= '\u0B35' && input.LA(1) <= '\u0B39')||(input.LA(1) >= '\u0B3C' && input.LA(1) <= '\u0B43')||(input.LA(1) >= '\u0B47' && input.LA(1) <= '\u0B48')||(input.LA(1) >= '\u0B4B' && input.LA(1) <= '\u0B4D')||(input.LA(1) >= '\u0B56' && input.LA(1) <= '\u0B57')||(input.LA(1) >= '\u0B5C' && input.LA(1) <= '\u0B5D')||(input.LA(1) >= '\u0B5F' && input.LA(1) <= '\u0B61')||(input.LA(1) >= '\u0B66' && input.LA(1) <= '\u0B71')||(input.LA(1) >= '\u0B82' && input.LA(1) <= '\u0B83')||(input.LA(1) >= '\u0B85' && input.LA(1) <= '\u0B8A')||(input.LA(1) >= '\u0B8E' && input.LA(1) <= '\u0B90')||(input.LA(1) >= '\u0B92' && input.LA(1) <= '\u0B95')||(input.LA(1) >= '\u0B99' && input.LA(1) <= '\u0B9A')||input.LA(1)=='\u0B9C'||(input.LA(1) >= '\u0B9E' && input.LA(1) <= '\u0B9F')||(input.LA(1) >= '\u0BA3' && input.LA(1) <= '\u0BA4')||(input.LA(1) >= '\u0BA8' && input.LA(1) <= '\u0BAA')||(input.LA(1) >= '\u0BAE' && input.LA(1) <= '\u0BB5')||(input.LA(1) >= '\u0BB7' && input.LA(1) <= '\u0BB9')||(input.LA(1) >= '\u0BBE' && input.LA(1) <= '\u0BC2')||(input.LA(1) >= '\u0BC6' && input.LA(1) <= '\u0BC8')||(input.LA(1) >= '\u0BCA' && input.LA(1) <= '\u0BCD')||input.LA(1)=='\u0BD7'||(input.LA(1) >= '\u0BE7' && input.LA(1) <= '\u0BFA')||(input.LA(1) >= '\u0C01' && input.LA(1) <= '\u0C03')||(input.LA(1) >= '\u0C05' && input.LA(1) <= '\u0C0C')||(input.LA(1) >= '\u0C0E' && input.LA(1) <= '\u0C10')||(input.LA(1) >= '\u0C12' && input.LA(1) <= '\u0C28')||(input.LA(1) >= '\u0C2A' && input.LA(1) <= '\u0C33')||(input.LA(1) >= '\u0C35' && input.LA(1) <= '\u0C39')||(input.LA(1) >= '\u0C3E' && input.LA(1) <= '\u0C44')||(input.LA(1) >= '\u0C46' && input.LA(1) <= '\u0C48')||(input.LA(1) >= '\u0C4A' && input.LA(1) <= '\u0C4D')||(input.LA(1) >= '\u0C55' && input.LA(1) <= '\u0C56')||(input.LA(1) >= '\u0C60' && input.LA(1) <= '\u0C61')||(input.LA(1) >= '\u0C66' && input.LA(1) <= '\u0C6F')||(input.LA(1) >= '\u0C82' && input.LA(1) <= '\u0C83')||(input.LA(1) >= '\u0C85' && input.LA(1) <= '\u0C8C')||(input.LA(1) >= '\u0C8E' && input.LA(1) <= '\u0C90')||(input.LA(1) >= '\u0C92' && input.LA(1) <= '\u0CA8')||(input.LA(1) >= '\u0CAA' && input.LA(1) <= '\u0CB3')||(input.LA(1) >= '\u0CB5' && input.LA(1) <= '\u0CB9')||(input.LA(1) >= '\u0CBC' && input.LA(1) <= '\u0CC4')||(input.LA(1) >= '\u0CC6' && input.LA(1) <= '\u0CC8')||(input.LA(1) >= '\u0CCA' && input.LA(1) <= '\u0CCD')||(input.LA(1) >= '\u0CD5' && input.LA(1) <= '\u0CD6')||input.LA(1)=='\u0CDE'||(input.LA(1) >= '\u0CE0' && input.LA(1) <= '\u0CE1')||(input.LA(1) >= '\u0CE6' && input.LA(1) <= '\u0CEF')||(input.LA(1) >= '\u0D02' && input.LA(1) <= '\u0D03')||(input.LA(1) >= '\u0D05' && input.LA(1) <= '\u0D0C')||(input.LA(1) >= '\u0D0E' && input.LA(1) <= '\u0D10')||(input.LA(1) >= '\u0D12' && input.LA(1) <= '\u0D28')||(input.LA(1) >= '\u0D2A' && input.LA(1) <= '\u0D39')||(input.LA(1) >= '\u0D3E' && input.LA(1) <= '\u0D43')||(input.LA(1) >= '\u0D46' && input.LA(1) <= '\u0D48')||(input.LA(1) >= '\u0D4A' && input.LA(1) <= '\u0D4D')||input.LA(1)=='\u0D57'||(input.LA(1) >= '\u0D60' && input.LA(1) <= '\u0D61')||(input.LA(1) >= '\u0D66' && input.LA(1) <= '\u0D6F')||(input.LA(1) >= '\u0D82' && input.LA(1) <= '\u0D83')||(input.LA(1) >= '\u0D85' && input.LA(1) <= '\u0D96')||(input.LA(1) >= '\u0D9A' && input.LA(1) <= '\u0DB1')||(input.LA(1) >= '\u0DB3' && input.LA(1) <= '\u0DBB')||input.LA(1)=='\u0DBD'||(input.LA(1) >= '\u0DC0' && input.LA(1) <= '\u0DC6')||input.LA(1)=='\u0DCA'||(input.LA(1) >= '\u0DCF' && input.LA(1) <= '\u0DD4')||input.LA(1)=='\u0DD6'||(input.LA(1) >= '\u0DD8' && input.LA(1) <= '\u0DDF')||(input.LA(1) >= '\u0DF2' && input.LA(1) <= '\u0DF3')||(input.LA(1) >= '\u0E01' && input.LA(1) <= '\u0E3A')||(input.LA(1) >= '\u0E3F' && input.LA(1) <= '\u0E4E')||(input.LA(1) >= '\u0E50' && input.LA(1) <= '\u0E59')||(input.LA(1) >= '\u0E81' && input.LA(1) <= '\u0E82')||input.LA(1)=='\u0E84'||(input.LA(1) >= '\u0E87' && input.LA(1) <= '\u0E88')||input.LA(1)=='\u0E8A'||input.LA(1)=='\u0E8D'||(input.LA(1) >= '\u0E94' && input.LA(1) <= '\u0E97')||(input.LA(1) >= '\u0E99' && input.LA(1) <= '\u0E9F')||(input.LA(1) >= '\u0EA1' && input.LA(1) <= '\u0EA3')||input.LA(1)=='\u0EA5'||input.LA(1)=='\u0EA7'||(input.LA(1) >= '\u0EAA' && input.LA(1) <= '\u0EAB')||(input.LA(1) >= '\u0EAD' && input.LA(1) <= '\u0EB9')||(input.LA(1) >= '\u0EBB' && input.LA(1) <= '\u0EBD')||(input.LA(1) >= '\u0EC0' && input.LA(1) <= '\u0EC4')||input.LA(1)=='\u0EC6'||(input.LA(1) >= '\u0EC8' && input.LA(1) <= '\u0ECD')||(input.LA(1) >= '\u0ED0' && input.LA(1) <= '\u0ED9')||(input.LA(1) >= '\u0EDC' && input.LA(1) <= '\u0EDD')||(input.LA(1) >= '\u0F00' && input.LA(1) <= '\u0F03')||(input.LA(1) >= '\u0F13' && input.LA(1) <= '\u0F39')||(input.LA(1) >= '\u0F3E' && input.LA(1) <= '\u0F47')||(input.LA(1) >= '\u0F49' && input.LA(1) <= '\u0F6A')||(input.LA(1) >= '\u0F71' && input.LA(1) <= '\u0F84')||(input.LA(1) >= '\u0F86' && input.LA(1) <= '\u0F8B')||(input.LA(1) >= '\u0F90' && input.LA(1) <= '\u0F97')||(input.LA(1) >= '\u0F99' && input.LA(1) <= '\u0FBC')||(input.LA(1) >= '\u0FBE' && input.LA(1) <= '\u0FCC')||input.LA(1)=='\u0FCF'||(input.LA(1) >= '\u1000' && input.LA(1) <= '\u1021')||(input.LA(1) >= '\u1023' && input.LA(1) <= '\u1027')||(input.LA(1) >= '\u1029' && input.LA(1) <= '\u102A')||(input.LA(1) >= '\u102C' && input.LA(1) <= '\u1032')||(input.LA(1) >= '\u1036' && input.LA(1) <= '\u1039')||(input.LA(1) >= '\u1040' && input.LA(1) <= '\u1049')||(input.LA(1) >= '\u1050' && input.LA(1) <= '\u1059')||(input.LA(1) >= '\u10A0' && input.LA(1) <= '\u10C5')||(input.LA(1) >= '\u10D0' && input.LA(1) <= '\u10F8')||(input.LA(1) >= '\u1100' && input.LA(1) <= '\u1159')||(input.LA(1) >= '\u115F' && input.LA(1) <= '\u11A2')||(input.LA(1) >= '\u11A8' && input.LA(1) <= '\u11F9')||(input.LA(1) >= '\u1200' && input.LA(1) <= '\u1206')||(input.LA(1) >= '\u1208' && input.LA(1) <= '\u1246')||input.LA(1)=='\u1248'||(input.LA(1) >= '\u124A' && input.LA(1) <= '\u124D')||(input.LA(1) >= '\u1250' && input.LA(1) <= '\u1256')||input.LA(1)=='\u1258'||(input.LA(1) >= '\u125A' && input.LA(1) <= '\u125D')||(input.LA(1) >= '\u1260' && input.LA(1) <= '\u1286')||input.LA(1)=='\u1288'||(input.LA(1) >= '\u128A' && input.LA(1) <= '\u128D')||(input.LA(1) >= '\u1290' && input.LA(1) <= '\u12AE')||input.LA(1)=='\u12B0'||(input.LA(1) >= '\u12B2' && input.LA(1) <= '\u12B5')||(input.LA(1) >= '\u12B8' && input.LA(1) <= '\u12BE')||input.LA(1)=='\u12C0'||(input.LA(1) >= '\u12C2' && input.LA(1) <= '\u12C5')||(input.LA(1) >= '\u12C8' && input.LA(1) <= '\u12CE')||(input.LA(1) >= '\u12D0' && input.LA(1) <= '\u12D6')||(input.LA(1) >= '\u12D8' && input.LA(1) <= '\u12EE')||(input.LA(1) >= '\u12F0' && input.LA(1) <= '\u130E')||input.LA(1)=='\u1310'||(input.LA(1) >= '\u1312' && input.LA(1) <= '\u1315')||(input.LA(1) >= '\u1318' && input.LA(1) <= '\u131E')||(input.LA(1) >= '\u1320' && input.LA(1) <= '\u1346')||(input.LA(1) >= '\u1348' && input.LA(1) <= '\u135A')||(input.LA(1) >= '\u1369' && input.LA(1) <= '\u137C')||(input.LA(1) >= '\u13A0' && input.LA(1) <= '\u13F4')||(input.LA(1) >= '\u1401' && input.LA(1) <= '\u166C')||(input.LA(1) >= '\u166F' && input.LA(1) <= '\u1676')||(input.LA(1) >= '\u1681' && input.LA(1) <= '\u169A')||(input.LA(1) >= '\u16A0' && input.LA(1) <= '\u16EA')||(input.LA(1) >= '\u16EE' && input.LA(1) <= '\u16F0')||(input.LA(1) >= '\u1700' && input.LA(1) <= '\u170C')||(input.LA(1) >= '\u170E' && input.LA(1) <= '\u1714')||(input.LA(1) >= '\u1720' && input.LA(1) <= '\u1734')||(input.LA(1) >= '\u1740' && input.LA(1) <= '\u1753')||(input.LA(1) >= '\u1760' && input.LA(1) <= '\u176C')||(input.LA(1) >= '\u176E' && input.LA(1) <= '\u1770')||(input.LA(1) >= '\u1772' && input.LA(1) <= '\u1773')||(input.LA(1) >= '\u1780' && input.LA(1) <= '\u17B3')||(input.LA(1) >= '\u17B6' && input.LA(1) <= '\u17D3')||input.LA(1)=='\u17D7'||(input.LA(1) >= '\u17DB' && input.LA(1) <= '\u17DD')||(input.LA(1) >= '\u17E0' && input.LA(1) <= '\u17E9')||(input.LA(1) >= '\u17F0' && input.LA(1) <= '\u17F9')||(input.LA(1) >= '\u180B' && input.LA(1) <= '\u180D')||(input.LA(1) >= '\u1810' && input.LA(1) <= '\u1819')||(input.LA(1) >= '\u1820' && input.LA(1) <= '\u1877')||(input.LA(1) >= '\u1880' && input.LA(1) <= '\u18A9')||(input.LA(1) >= '\u1900' && input.LA(1) <= '\u191C')||(input.LA(1) >= '\u1920' && input.LA(1) <= '\u192B')||(input.LA(1) >= '\u1930' && input.LA(1) <= '\u193B')||input.LA(1)=='\u1940'||(input.LA(1) >= '\u1946' && input.LA(1) <= '\u196D')||(input.LA(1) >= '\u1970' && input.LA(1) <= '\u1974')||(input.LA(1) >= '\u19E0' && input.LA(1) <= '\u19FF')||(input.LA(1) >= '\u1D00' && input.LA(1) <= '\u1D6B')||(input.LA(1) >= '\u1E00' && input.LA(1) <= '\u1E9B')||(input.LA(1) >= '\u1EA0' && input.LA(1) <= '\u1EF9')||(input.LA(1) >= '\u1F00' && input.LA(1) <= '\u1F15')||(input.LA(1) >= '\u1F18' && input.LA(1) <= '\u1F1D')||(input.LA(1) >= '\u1F20' && input.LA(1) <= '\u1F45')||(input.LA(1) >= '\u1F48' && input.LA(1) <= '\u1F4D')||(input.LA(1) >= '\u1F50' && input.LA(1) <= '\u1F57')||input.LA(1)=='\u1F59'||input.LA(1)=='\u1F5B'||input.LA(1)=='\u1F5D'||(input.LA(1) >= '\u1F5F' && input.LA(1) <= '\u1F7D')||(input.LA(1) >= '\u1F80' && input.LA(1) <= '\u1FB4')||(input.LA(1) >= '\u1FB6' && input.LA(1) <= '\u1FBC')||input.LA(1)=='\u1FBE'||(input.LA(1) >= '\u1FC2' && input.LA(1) <= '\u1FC4')||(input.LA(1) >= '\u1FC6' && input.LA(1) <= '\u1FCC')||(input.LA(1) >= '\u1FD0' && input.LA(1) <= '\u1FD3')||(input.LA(1) >= '\u1FD6' && input.LA(1) <= '\u1FDB')||(input.LA(1) >= '\u1FE0' && input.LA(1) <= '\u1FEC')||(input.LA(1) >= '\u1FF2' && input.LA(1) <= '\u1FF4')||(input.LA(1) >= '\u1FF6' && input.LA(1) <= '\u1FFC')||(input.LA(1) >= '\u2070' && input.LA(1) <= '\u2071')||(input.LA(1) >= '\u2074' && input.LA(1) <= '\u2079')||(input.LA(1) >= '\u207F' && input.LA(1) <= '\u2089')||(input.LA(1) >= '\u20A0' && input.LA(1) <= '\u20B1')||(input.LA(1) >= '\u20D0' && input.LA(1) <= '\u20EA')||(input.LA(1) >= '\u2100' && input.LA(1) <= '\u213B')||(input.LA(1) >= '\u213D' && input.LA(1) <= '\u213F')||(input.LA(1) >= '\u2145' && input.LA(1) <= '\u214A')||(input.LA(1) >= '\u2153' && input.LA(1) <= '\u2183')||(input.LA(1) >= '\u2195' && input.LA(1) <= '\u2199')||(input.LA(1) >= '\u219C' && input.LA(1) <= '\u219F')||(input.LA(1) >= '\u21A1' && input.LA(1) <= '\u21A2')||(input.LA(1) >= '\u21A4' && input.LA(1) <= '\u21A5')||(input.LA(1) >= '\u21A7' && input.LA(1) <= '\u21AD')||(input.LA(1) >= '\u21AF' && input.LA(1) <= '\u21CD')||(input.LA(1) >= '\u21D0' && input.LA(1) <= '\u21D1')||input.LA(1)=='\u21D3'||(input.LA(1) >= '\u21D5' && input.LA(1) <= '\u21F3')||(input.LA(1) >= '\u2300' && input.LA(1) <= '\u2307')||(input.LA(1) >= '\u230C' && input.LA(1) <= '\u231F')||(input.LA(1) >= '\u2322' && input.LA(1) <= '\u2328')||(input.LA(1) >= '\u232B' && input.LA(1) <= '\u237B')||(input.LA(1) >= '\u237D' && input.LA(1) <= '\u239A')||(input.LA(1) >= '\u23B7' && input.LA(1) <= '\u23D0')||(input.LA(1) >= '\u2400' && input.LA(1) <= '\u2426')||(input.LA(1) >= '\u2440' && input.LA(1) <= '\u244A')||(input.LA(1) >= '\u2460' && input.LA(1) <= '\u25B6')||(input.LA(1) >= '\u25B8' && input.LA(1) <= '\u25C0')||(input.LA(1) >= '\u25C2' && input.LA(1) <= '\u25F7')||(input.LA(1) >= '\u2600' && input.LA(1) <= '\u2617')||(input.LA(1) >= '\u2619' && input.LA(1) <= '\u266E')||(input.LA(1) >= '\u2670' && input.LA(1) <= '\u267D')||(input.LA(1) >= '\u2680' && input.LA(1) <= '\u2691')||(input.LA(1) >= '\u26A0' && input.LA(1) <= '\u26A1')||(input.LA(1) >= '\u2701' && input.LA(1) <= '\u2704')||(input.LA(1) >= '\u2706' && input.LA(1) <= '\u2709')||(input.LA(1) >= '\u270C' && input.LA(1) <= '\u2727')||(input.LA(1) >= '\u2729' && input.LA(1) <= '\u274B')||input.LA(1)=='\u274D'||(input.LA(1) >= '\u274F' && input.LA(1) <= '\u2752')||input.LA(1)=='\u2756'||(input.LA(1) >= '\u2758' && input.LA(1) <= '\u275E')||(input.LA(1) >= '\u2761' && input.LA(1) <= '\u2767')||(input.LA(1) >= '\u2776' && input.LA(1) <= '\u2794')||(input.LA(1) >= '\u2798' && input.LA(1) <= '\u27AF')||(input.LA(1) >= '\u27B1' && input.LA(1) <= '\u27BE')||(input.LA(1) >= '\u2800' && input.LA(1) <= '\u28FF')||(input.LA(1) >= '\u2B00' && input.LA(1) <= '\u2B0D')||(input.LA(1) >= '\u2E80' && input.LA(1) <= '\u2E99')||(input.LA(1) >= '\u2E9B' && input.LA(1) <= '\u2EF3')||(input.LA(1) >= '\u2F00' && input.LA(1) <= '\u2FD5')||(input.LA(1) >= '\u2FF0' && input.LA(1) <= '\u2FFB')||(input.LA(1) >= '\u3004' && input.LA(1) <= '\u3007')||(input.LA(1) >= '\u3012' && input.LA(1) <= '\u3013')||(input.LA(1) >= '\u3020' && input.LA(1) <= '\u302F')||(input.LA(1) >= '\u3031' && input.LA(1) <= '\u303C')||(input.LA(1) >= '\u303E' && input.LA(1) <= '\u303F')||(input.LA(1) >= '\u3041' && input.LA(1) <= '\u3096')||(input.LA(1) >= '\u3099' && input.LA(1) <= '\u309A')||(input.LA(1) >= '\u309D' && input.LA(1) <= '\u309F')||(input.LA(1) >= '\u30A1' && input.LA(1) <= '\u30FA')||(input.LA(1) >= '\u30FC' && input.LA(1) <= '\u30FF')||(input.LA(1) >= '\u3105' && input.LA(1) <= '\u312C')||(input.LA(1) >= '\u3131' && input.LA(1) <= '\u318E')||(input.LA(1) >= '\u3190' && input.LA(1) <= '\u31B7')||(input.LA(1) >= '\u31F0' && input.LA(1) <= '\u321E')||(input.LA(1) >= '\u3220' && input.LA(1) <= '\u3243')||(input.LA(1) >= '\u3250' && input.LA(1) <= '\u327D')||(input.LA(1) >= '\u327F' && input.LA(1) <= '\u32FE')||(input.LA(1) >= '\u3300' && input.LA(1) <= '\u4DB5')||(input.LA(1) >= '\u4DC0' && input.LA(1) <= '\u9FA5')||(input.LA(1) >= '\uA000' && input.LA(1) <= '\uA48C')||(input.LA(1) >= '\uA490' && input.LA(1) <= '\uA4C6')||(input.LA(1) >= '\uAC00' && input.LA(1) <= '\uD7A3')||(input.LA(1) >= '\uF900' && input.LA(1) <= '\uFA2D')||(input.LA(1) >= '\uFA30' && input.LA(1) <= '\uFA6A')||(input.LA(1) >= '\uFB00' && input.LA(1) <= '\uFB06')||(input.LA(1) >= '\uFB13' && input.LA(1) <= '\uFB17')||(input.LA(1) >= '\uFB1D' && input.LA(1) <= '\uFB28')||(input.LA(1) >= '\uFB2A' && input.LA(1) <= '\uFB36')||(input.LA(1) >= '\uFB38' && input.LA(1) <= '\uFB3C')||input.LA(1)=='\uFB3E'||(input.LA(1) >= '\uFB40' && input.LA(1) <= '\uFB41')||(input.LA(1) >= '\uFB43' && input.LA(1) <= '\uFB44')||(input.LA(1) >= '\uFB46' && input.LA(1) <= '\uFBB1')||(input.LA(1) >= '\uFBD3' && input.LA(1) <= '\uFD3D')||(input.LA(1) >= '\uFD50' && input.LA(1) <= '\uFD8F')||(input.LA(1) >= '\uFD92' && input.LA(1) <= '\uFDC7')||(input.LA(1) >= '\uFDF0' && input.LA(1) <= '\uFDFD')||(input.LA(1) >= '\uFE00' && input.LA(1) <= '\uFE0F')||(input.LA(1) >= '\uFE20' && input.LA(1) <= '\uFE23')||input.LA(1)=='\uFE69'||(input.LA(1) >= '\uFE70' && input.LA(1) <= '\uFE74')||(input.LA(1) >= '\uFE76' && input.LA(1) <= '\uFEFC')||input.LA(1)=='\uFF04'||(input.LA(1) >= '\uFF10' && input.LA(1) <= '\uFF19')||(input.LA(1) >= '\uFF21' && input.LA(1) <= '\uFF3A')||(input.LA(1) >= '\uFF41' && input.LA(1) <= '\uFF5A')||(input.LA(1) >= '\uFF66' && input.LA(1) <= '\uFFBE')||(input.LA(1) >= '\uFFC2' && input.LA(1) <= '\uFFC7')||(input.LA(1) >= '\uFFCA' && input.LA(1) <= '\uFFCF')||(input.LA(1) >= '\uFFD2' && input.LA(1) <= '\uFFD7')||(input.LA(1) >= '\uFFDA' && input.LA(1) <= '\uFFDC')||(input.LA(1) >= '\uFFE0' && input.LA(1) <= '\uFFE1')||(input.LA(1) >= '\uFFE4' && input.LA(1) <= '\uFFE6')||input.LA(1)=='\uFFE8'||(input.LA(1) >= '\uFFED' && input.LA(1) <= '\uFFEE') ) {
                input.consume();
                state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "START_WORD"

    // $ANTLR start "IN_WORD"
    public final void mIN_WORD() throws RecognitionException {
        try {
            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1928:9: ( '\\u0021' .. '\\u0027' | '\\u002b' | '\\u002d' | '\\u002f' .. '\\u0039' | '\\u003b' | '\\u003d' | '\\u0040' .. '\\u005a' | '\\u005f' | '\\u0061' .. '\\u007a' | '\\u007c' | '\\u00a1' .. '\\u00a7' | '\\u00a9' .. '\\u00aa' | '\\u00ac' | '\\u00ae' | '\\u00b0' .. '\\u00b3' | '\\u00b5' .. '\\u00b7' | '\\u00b9' .. '\\u00ba' | '\\u00bc' .. '\\u0236' | '\\u0250' .. '\\u02c1' | '\\u02c6' .. '\\u02d1' | '\\u02e0' .. '\\u02e4' | '\\u02ee' | '\\u0300' .. '\\u0357' | '\\u035d' .. '\\u036f' | '\\u037a' .. '\\u037a' | '\\u037e' .. '\\u037e' | '\\u0386' .. '\\u038a' | '\\u038c' .. '\\u038c' | '\\u038e' .. '\\u03a1' | '\\u03a3' .. '\\u03ce' | '\\u03d0' .. '\\u03fb' | '\\u0400' .. '\\u0486' | '\\u0488' .. '\\u04ce' | '\\u04d0' .. '\\u04f5' | '\\u04f8' .. '\\u04f9' | '\\u0500' .. '\\u050f' | '\\u0531' .. '\\u0556' | '\\u0559' .. '\\u055f' | '\\u0561' .. '\\u0587' | '\\u0589' .. '\\u058a' | '\\u0591' .. '\\u05a1' | '\\u05a3' .. '\\u05b9' | '\\u05bb' .. '\\u05c4' | '\\u05d0' .. '\\u05ea' | '\\u05f0' .. '\\u05f4' | '\\u060c' .. '\\u0615' | '\\u061b' .. '\\u061b' | '\\u061f' .. '\\u061f' | '\\u0621' .. '\\u063a' | '\\u0640' .. '\\u0658' | '\\u0660' .. '\\u06dc' | '\\u06de' .. '\\u070d' | '\\u0710' .. '\\u074a' | '\\u074d' .. '\\u074f' | '\\u0780' .. '\\u07b1' | '\\u0901' .. '\\u0939' | '\\u093c' .. '\\u094d' | '\\u0950' .. '\\u0954' | '\\u0958' .. '\\u0970' | '\\u0981' .. '\\u0983' | '\\u0985' .. '\\u098c' | '\\u098f' .. '\\u0990' | '\\u0993' .. '\\u09a8' | '\\u09aa' .. '\\u09b0' | '\\u09b2' .. '\\u09b2' | '\\u09b6' .. '\\u09b9' | '\\u09bc' .. '\\u09c4' | '\\u09c7' .. '\\u09c8' | '\\u09cb' .. '\\u09cd' | '\\u09d7' .. '\\u09d7' | '\\u09dc' .. '\\u09dd' | '\\u09df' .. '\\u09e3' | '\\u09e6' .. '\\u09fa' | '\\u0a01' .. '\\u0a03' | '\\u0a05' .. '\\u0a0a' | '\\u0a0f' .. '\\u0a10' | '\\u0a13' .. '\\u0a28' | '\\u0a2a' .. '\\u0a30' | '\\u0a32' .. '\\u0a33' | '\\u0a35' .. '\\u0a36' | '\\u0a38' .. '\\u0a39' | '\\u0a3c' .. '\\u0a3c' | '\\u0a3e' .. '\\u0a42' | '\\u0a47' .. '\\u0a48' | '\\u0a4b' .. '\\u0a4d' | '\\u0a59' .. '\\u0a5c' | '\\u0a5e' .. '\\u0a5e' | '\\u0a66' .. '\\u0a74' | '\\u0a81' .. '\\u0a83' | '\\u0a85' .. '\\u0a8d' | '\\u0a8f' .. '\\u0a91' | '\\u0a93' .. '\\u0aa8' | '\\u0aaa' .. '\\u0ab0' | '\\u0ab2' .. '\\u0ab3' | '\\u0ab5' .. '\\u0ab9' | '\\u0abc' .. '\\u0ac5' | '\\u0ac7' .. '\\u0ac9' | '\\u0acb' .. '\\u0acd' | '\\u0ad0' .. '\\u0ad0' | '\\u0ae0' .. '\\u0ae3' | '\\u0ae6' .. '\\u0aef' | '\\u0af1' .. '\\u0af1' | '\\u0b01' .. '\\u0b03' | '\\u0b05' .. '\\u0b0c' | '\\u0b0f' .. '\\u0b10' | '\\u0b13' .. '\\u0b28' | '\\u0b2a' .. '\\u0b30' | '\\u0b32' .. '\\u0b33' | '\\u0b35' .. '\\u0b39' | '\\u0b3c' .. '\\u0b43' | '\\u0b47' .. '\\u0b48' | '\\u0b4b' .. '\\u0b4d' | '\\u0b56' .. '\\u0b57' | '\\u0b5c' .. '\\u0b5d' | '\\u0b5f' .. '\\u0b61' | '\\u0b66' .. '\\u0b71' | '\\u0b82' .. '\\u0b83' | '\\u0b85' .. '\\u0b8a' | '\\u0b8e' .. '\\u0b90' | '\\u0b92' .. '\\u0b95' | '\\u0b99' .. '\\u0b9a' | '\\u0b9c' .. '\\u0b9c' | '\\u0b9e' .. '\\u0b9f' | '\\u0ba3' .. '\\u0ba4' | '\\u0ba8' .. '\\u0baa' | '\\u0bae' .. '\\u0bb5' | '\\u0bb7' .. '\\u0bb9' | '\\u0bbe' .. '\\u0bc2' | '\\u0bc6' .. '\\u0bc8' | '\\u0bca' .. '\\u0bcd' | '\\u0bd7' .. '\\u0bd7' | '\\u0be7' .. '\\u0bfa' | '\\u0c01' .. '\\u0c03' | '\\u0c05' .. '\\u0c0c' | '\\u0c0e' .. '\\u0c10' | '\\u0c12' .. '\\u0c28' | '\\u0c2a' .. '\\u0c33' | '\\u0c35' .. '\\u0c39' | '\\u0c3e' .. '\\u0c44' | '\\u0c46' .. '\\u0c48' | '\\u0c4a' .. '\\u0c4d' | '\\u0c55' .. '\\u0c56' | '\\u0c60' .. '\\u0c61' | '\\u0c66' .. '\\u0c6f' | '\\u0c82' .. '\\u0c83' | '\\u0c85' .. '\\u0c8c' | '\\u0c8e' .. '\\u0c90' | '\\u0c92' .. '\\u0ca8' | '\\u0caa' .. '\\u0cb3' | '\\u0cb5' .. '\\u0cb9' | '\\u0cbc' .. '\\u0cc4' | '\\u0cc6' .. '\\u0cc8' | '\\u0cca' .. '\\u0ccd' | '\\u0cd5' .. '\\u0cd6' | '\\u0cde' .. '\\u0cde' | '\\u0ce0' .. '\\u0ce1' | '\\u0ce6' .. '\\u0cef' | '\\u0d02' .. '\\u0d03' | '\\u0d05' .. '\\u0d0c' | '\\u0d0e' .. '\\u0d10' | '\\u0d12' .. '\\u0d28' | '\\u0d2a' .. '\\u0d39' | '\\u0d3e' .. '\\u0d43' | '\\u0d46' .. '\\u0d48' | '\\u0d4a' .. '\\u0d4d' | '\\u0d57' .. '\\u0d57' | '\\u0d60' .. '\\u0d61' | '\\u0d66' .. '\\u0d6f' | '\\u0d82' .. '\\u0d83' | '\\u0d85' .. '\\u0d96' | '\\u0d9a' .. '\\u0db1' | '\\u0db3' .. '\\u0dbb' | '\\u0dbd' .. '\\u0dbd' | '\\u0dc0' .. '\\u0dc6' | '\\u0dca' .. '\\u0dca' | '\\u0dcf' .. '\\u0dd4' | '\\u0dd6' .. '\\u0dd6' | '\\u0dd8' .. '\\u0ddf' | '\\u0df2' .. '\\u0df4' | '\\u0e01' .. '\\u0e3a' | '\\u0e3f' .. '\\u0e5b' | '\\u0e81' .. '\\u0e82' | '\\u0e84' .. '\\u0e84' | '\\u0e87' .. '\\u0e88' | '\\u0e8a' .. '\\u0e8a' | '\\u0e8d' .. '\\u0e8d' | '\\u0e94' .. '\\u0e97' | '\\u0e99' .. '\\u0e9f' | '\\u0ea1' .. '\\u0ea3' | '\\u0ea5' .. '\\u0ea5' | '\\u0ea7' .. '\\u0ea7' | '\\u0eaa' .. '\\u0eab' | '\\u0ead' .. '\\u0eb9' | '\\u0ebb' .. '\\u0ebd' | '\\u0ec0' .. '\\u0ec4' | '\\u0ec6' .. '\\u0ec6' | '\\u0ec8' .. '\\u0ecd' | '\\u0ed0' .. '\\u0ed9' | '\\u0edc' .. '\\u0edd' | '\\u0f00' .. '\\u0f39' | '\\u0f3e' .. '\\u0f47' | '\\u0f49' .. '\\u0f6a' | '\\u0f71' .. '\\u0f8b' | '\\u0f90' .. '\\u0f97' | '\\u0f99' .. '\\u0fbc' | '\\u0fbe' .. '\\u0fcc' | '\\u0fcf' .. '\\u0fcf' | '\\u1000' .. '\\u1021' | '\\u1023' .. '\\u1027' | '\\u1029' .. '\\u102a' | '\\u102c' .. '\\u1032' | '\\u1036' .. '\\u1039' | '\\u1040' .. '\\u1059' | '\\u10a0' .. '\\u10c5' | '\\u10d0' .. '\\u10f8' | '\\u10fb' .. '\\u10fb' | '\\u1100' .. '\\u1159' | '\\u115f' .. '\\u11a2' | '\\u11a8' .. '\\u11f9' | '\\u1200' .. '\\u1206' | '\\u1208' .. '\\u1246' | '\\u1248' .. '\\u1248' | '\\u124a' .. '\\u124d' | '\\u1250' .. '\\u1256' | '\\u1258' .. '\\u1258' | '\\u125a' .. '\\u125d' | '\\u1260' .. '\\u1286' | '\\u1288' .. '\\u1288' | '\\u128a' .. '\\u128d' | '\\u1290' .. '\\u12ae' | '\\u12b0' .. '\\u12b0' | '\\u12b2' .. '\\u12b5' | '\\u12b8' .. '\\u12be' | '\\u12c0' .. '\\u12c0' | '\\u12c2' .. '\\u12c5' | '\\u12c8' .. '\\u12ce' | '\\u12d0' .. '\\u12d6' | '\\u12d8' .. '\\u12ee' | '\\u12f0' .. '\\u130e' | '\\u1310' .. '\\u1310' | '\\u1312' .. '\\u1315' | '\\u1318' .. '\\u131e' | '\\u1320' .. '\\u1346' | '\\u1348' .. '\\u135a' | '\\u1361' .. '\\u137c' | '\\u13a0' .. '\\u13f4' | '\\u1401' .. '\\u1676' | '\\u1681' .. '\\u169a' | '\\u16a0' .. '\\u16f0' | '\\u1700' .. '\\u170c' | '\\u170e' .. '\\u1714' | '\\u1720' .. '\\u1736' | '\\u1740' .. '\\u1753' | '\\u1760' .. '\\u176c' | '\\u176e' .. '\\u1770' | '\\u1772' .. '\\u1773' | '\\u1780' .. '\\u17b3' | '\\u17b6' .. '\\u17dd' | '\\u17e0' .. '\\u17e9' | '\\u17f0' .. '\\u17f9' | '\\u1800' .. '\\u180d' | '\\u1810' .. '\\u1819' | '\\u1820' .. '\\u1877' | '\\u1880' .. '\\u18a9' | '\\u1900' .. '\\u191c' | '\\u1920' .. '\\u192b' | '\\u1930' .. '\\u193b' | '\\u1940' .. '\\u1940' | '\\u1944' .. '\\u196d' | '\\u1970' .. '\\u1974' | '\\u19e0' .. '\\u19ff' | '\\u1d00' .. '\\u1d6b' | '\\u1e00' .. '\\u1e9b' | '\\u1ea0' .. '\\u1ef9' | '\\u1f00' .. '\\u1f15' | '\\u1f18' .. '\\u1f1d' | '\\u1f20' .. '\\u1f45' | '\\u1f48' .. '\\u1f4d' | '\\u1f50' .. '\\u1f57' | '\\u1f59' .. '\\u1f59' | '\\u1f5b' .. '\\u1f5b' | '\\u1f5d' .. '\\u1f5d' | '\\u1f5f' .. '\\u1f7d' | '\\u1f80' .. '\\u1fb4' | '\\u1fb6' .. '\\u1fbc' | '\\u1fbe' | '\\u1fc2' .. '\\u1fc4' | '\\u1fc6' .. '\\u1fcc' | '\\u1fd0' .. '\\u1fd3' | '\\u1fd6' .. '\\u1fdb' | '\\u1fe0' .. '\\u1fec' | '\\u1ff2' .. '\\u1ff4' | '\\u1ff6' .. '\\u1ffc' | '\\u2010' .. '\\u2017' | '\\u2020' .. '\\u2027' | '\\u2030' .. '\\u2038' | '\\u203b' .. '\\u2044' | '\\u2047' .. '\\u2054' | '\\u2057' .. '\\u2057' | '\\u2070' .. '\\u2071' | '\\u2074' .. '\\u207c' | '\\u207f' .. '\\u208c' | '\\u20a0' .. '\\u20b1' | '\\u20d0' .. '\\u20ea' | '\\u2100' .. '\\u213b' | '\\u213d' .. '\\u214b' | '\\u2153' .. '\\u2183' | '\\u2190' .. '\\u2328' | '\\u232b' .. '\\u23b3' | '\\u23b6' .. '\\u23d0' | '\\u2400' .. '\\u2426' | '\\u2440' .. '\\u244a' | '\\u2460' .. '\\u2617' | '\\u2619' .. '\\u267d' | '\\u2680' .. '\\u2691' | '\\u26a0' .. '\\u26a1' | '\\u2701' .. '\\u2704' | '\\u2706' .. '\\u2709' | '\\u270c' .. '\\u2727' | '\\u2729' .. '\\u274b' | '\\u274d' .. '\\u274d' | '\\u274f' .. '\\u2752' | '\\u2756' .. '\\u2756' | '\\u2758' .. '\\u275e' | '\\u2761' .. '\\u2767' | '\\u2776' .. '\\u2794' | '\\u2798' .. '\\u27af' | '\\u27b1' .. '\\u27be' | '\\u27d0' .. '\\u27e5' | '\\u27f0' .. '\\u2982' | '\\u2999' .. '\\u29d7' | '\\u29dc' .. '\\u29fb' | '\\u29fe' .. '\\u2b0d' | '\\u2e80' .. '\\u2e99' | '\\u2e9b' .. '\\u2ef3' | '\\u2f00' .. '\\u2fd5' | '\\u2ff0' .. '\\u2ffb' | '\\u3001' .. '\\u3007' | '\\u3012' .. '\\u3013' | '\\u301c' | '\\u3020' .. '\\u303f' | '\\u3041' .. '\\u3096' | '\\u3099' .. '\\u309a' | '\\u309d' .. '\\u30ff' | '\\u3105' .. '\\u312c' | '\\u3131' .. '\\u318e' | '\\u3190' .. '\\u31b7' | '\\u31f0' .. '\\u321e' | '\\u3220' .. '\\u3243' | '\\u3250' .. '\\u327d' | '\\u327f' .. '\\u32fe' | '\\u3300' .. '\\u4db5' | '\\u4dc0' .. '\\u9fa5' | '\\ua000' .. '\\ua48c' | '\\ua490' .. '\\ua4c6' | '\\uac00' .. '\\ud7a3' | '\\uf900' .. '\\ufa2d' | '\\ufa30' .. '\\ufa6a' | '\\ufb00' .. '\\ufb06' | '\\ufb13' .. '\\ufb17' | '\\ufb1d' .. '\\ufb36' | '\\ufb38' .. '\\ufb3c' | '\\ufb3e' .. '\\ufb3e' | '\\ufb40' .. '\\ufb41' | '\\ufb43' .. '\\ufb44' | '\\ufb46' .. '\\ufbb1' | '\\ufbd3' .. '\\ufd3d' | '\\ufd50' .. '\\ufd8f' | '\\ufd92' .. '\\ufdc7' | '\\ufdf0' .. '\\ufdfd' | '\\ufe00' .. '\\ufe0f' | '\\ufe20' .. '\\ufe23' | '\\ufe30' .. '\\ufe34' | '\\ufe45' .. '\\ufe46' | '\\ufe49' .. '\\ufe52' | '\\ufe54' .. '\\ufe58' | '\\ufe5f' .. '\\ufe66' | '\\ufe68' .. '\\ufe6b' | '\\ufe70' .. '\\ufe74' | '\\ufe76' .. '\\ufefc' | '\\uff01' .. '\\uff07' | '\\uff0a' .. '\\uff3a' | '\\uff3c' | '\\uff3f' | '\\uff41' .. '\\uff5a' | '\\uff5c' | '\\uff5e' | '\\uff61' | '\\uff64' .. '\\uffbe' | '\\uffc2' .. '\\uffc7' | '\\uffca' .. '\\uffcf' | '\\uffd2' .. '\\uffd7' | '\\uffda' .. '\\uffdc' | '\\uffe0' .. '\\uffe2' | '\\uffe4' .. '\\uffe6' | '\\uffe8' .. '\\uffee' )
            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:
            {
            if ( (input.LA(1) >= '!' && input.LA(1) <= '\'')||input.LA(1)=='+'||input.LA(1)=='-'||(input.LA(1) >= '/' && input.LA(1) <= '9')||input.LA(1)==';'||input.LA(1)=='='||(input.LA(1) >= '@' && input.LA(1) <= 'Z')||input.LA(1)=='_'||(input.LA(1) >= 'a' && input.LA(1) <= 'z')||input.LA(1)=='|'||(input.LA(1) >= '\u00A1' && input.LA(1) <= '\u00A7')||(input.LA(1) >= '\u00A9' && input.LA(1) <= '\u00AA')||input.LA(1)=='\u00AC'||input.LA(1)=='\u00AE'||(input.LA(1) >= '\u00B0' && input.LA(1) <= '\u00B3')||(input.LA(1) >= '\u00B5' && input.LA(1) <= '\u00B7')||(input.LA(1) >= '\u00B9' && input.LA(1) <= '\u00BA')||(input.LA(1) >= '\u00BC' && input.LA(1) <= '\u0236')||(input.LA(1) >= '\u0250' && input.LA(1) <= '\u02C1')||(input.LA(1) >= '\u02C6' && input.LA(1) <= '\u02D1')||(input.LA(1) >= '\u02E0' && input.LA(1) <= '\u02E4')||input.LA(1)=='\u02EE'||(input.LA(1) >= '\u0300' && input.LA(1) <= '\u0357')||(input.LA(1) >= '\u035D' && input.LA(1) <= '\u036F')||input.LA(1)=='\u037A'||input.LA(1)=='\u037E'||(input.LA(1) >= '\u0386' && input.LA(1) <= '\u038A')||input.LA(1)=='\u038C'||(input.LA(1) >= '\u038E' && input.LA(1) <= '\u03A1')||(input.LA(1) >= '\u03A3' && input.LA(1) <= '\u03CE')||(input.LA(1) >= '\u03D0' && input.LA(1) <= '\u03FB')||(input.LA(1) >= '\u0400' && input.LA(1) <= '\u0486')||(input.LA(1) >= '\u0488' && input.LA(1) <= '\u04CE')||(input.LA(1) >= '\u04D0' && input.LA(1) <= '\u04F5')||(input.LA(1) >= '\u04F8' && input.LA(1) <= '\u04F9')||(input.LA(1) >= '\u0500' && input.LA(1) <= '\u050F')||(input.LA(1) >= '\u0531' && input.LA(1) <= '\u0556')||(input.LA(1) >= '\u0559' && input.LA(1) <= '\u055F')||(input.LA(1) >= '\u0561' && input.LA(1) <= '\u0587')||(input.LA(1) >= '\u0589' && input.LA(1) <= '\u058A')||(input.LA(1) >= '\u0591' && input.LA(1) <= '\u05A1')||(input.LA(1) >= '\u05A3' && input.LA(1) <= '\u05B9')||(input.LA(1) >= '\u05BB' && input.LA(1) <= '\u05C4')||(input.LA(1) >= '\u05D0' && input.LA(1) <= '\u05EA')||(input.LA(1) >= '\u05F0' && input.LA(1) <= '\u05F4')||(input.LA(1) >= '\u060C' && input.LA(1) <= '\u0615')||input.LA(1)=='\u061B'||input.LA(1)=='\u061F'||(input.LA(1) >= '\u0621' && input.LA(1) <= '\u063A')||(input.LA(1) >= '\u0640' && input.LA(1) <= '\u0658')||(input.LA(1) >= '\u0660' && input.LA(1) <= '\u06DC')||(input.LA(1) >= '\u06DE' && input.LA(1) <= '\u070D')||(input.LA(1) >= '\u0710' && input.LA(1) <= '\u074A')||(input.LA(1) >= '\u074D' && input.LA(1) <= '\u074F')||(input.LA(1) >= '\u0780' && input.LA(1) <= '\u07B1')||(input.LA(1) >= '\u0901' && input.LA(1) <= '\u0939')||(input.LA(1) >= '\u093C' && input.LA(1) <= '\u094D')||(input.LA(1) >= '\u0950' && input.LA(1) <= '\u0954')||(input.LA(1) >= '\u0958' && input.LA(1) <= '\u0970')||(input.LA(1) >= '\u0981' && input.LA(1) <= '\u0983')||(input.LA(1) >= '\u0985' && input.LA(1) <= '\u098C')||(input.LA(1) >= '\u098F' && input.LA(1) <= '\u0990')||(input.LA(1) >= '\u0993' && input.LA(1) <= '\u09A8')||(input.LA(1) >= '\u09AA' && input.LA(1) <= '\u09B0')||input.LA(1)=='\u09B2'||(input.LA(1) >= '\u09B6' && input.LA(1) <= '\u09B9')||(input.LA(1) >= '\u09BC' && input.LA(1) <= '\u09C4')||(input.LA(1) >= '\u09C7' && input.LA(1) <= '\u09C8')||(input.LA(1) >= '\u09CB' && input.LA(1) <= '\u09CD')||input.LA(1)=='\u09D7'||(input.LA(1) >= '\u09DC' && input.LA(1) <= '\u09DD')||(input.LA(1) >= '\u09DF' && input.LA(1) <= '\u09E3')||(input.LA(1) >= '\u09E6' && input.LA(1) <= '\u09FA')||(input.LA(1) >= '\u0A01' && input.LA(1) <= '\u0A03')||(input.LA(1) >= '\u0A05' && input.LA(1) <= '\u0A0A')||(input.LA(1) >= '\u0A0F' && input.LA(1) <= '\u0A10')||(input.LA(1) >= '\u0A13' && input.LA(1) <= '\u0A28')||(input.LA(1) >= '\u0A2A' && input.LA(1) <= '\u0A30')||(input.LA(1) >= '\u0A32' && input.LA(1) <= '\u0A33')||(input.LA(1) >= '\u0A35' && input.LA(1) <= '\u0A36')||(input.LA(1) >= '\u0A38' && input.LA(1) <= '\u0A39')||input.LA(1)=='\u0A3C'||(input.LA(1) >= '\u0A3E' && input.LA(1) <= '\u0A42')||(input.LA(1) >= '\u0A47' && input.LA(1) <= '\u0A48')||(input.LA(1) >= '\u0A4B' && input.LA(1) <= '\u0A4D')||(input.LA(1) >= '\u0A59' && input.LA(1) <= '\u0A5C')||input.LA(1)=='\u0A5E'||(input.LA(1) >= '\u0A66' && input.LA(1) <= '\u0A74')||(input.LA(1) >= '\u0A81' && input.LA(1) <= '\u0A83')||(input.LA(1) >= '\u0A85' && input.LA(1) <= '\u0A8D')||(input.LA(1) >= '\u0A8F' && input.LA(1) <= '\u0A91')||(input.LA(1) >= '\u0A93' && input.LA(1) <= '\u0AA8')||(input.LA(1) >= '\u0AAA' && input.LA(1) <= '\u0AB0')||(input.LA(1) >= '\u0AB2' && input.LA(1) <= '\u0AB3')||(input.LA(1) >= '\u0AB5' && input.LA(1) <= '\u0AB9')||(input.LA(1) >= '\u0ABC' && input.LA(1) <= '\u0AC5')||(input.LA(1) >= '\u0AC7' && input.LA(1) <= '\u0AC9')||(input.LA(1) >= '\u0ACB' && input.LA(1) <= '\u0ACD')||input.LA(1)=='\u0AD0'||(input.LA(1) >= '\u0AE0' && input.LA(1) <= '\u0AE3')||(input.LA(1) >= '\u0AE6' && input.LA(1) <= '\u0AEF')||input.LA(1)=='\u0AF1'||(input.LA(1) >= '\u0B01' && input.LA(1) <= '\u0B03')||(input.LA(1) >= '\u0B05' && input.LA(1) <= '\u0B0C')||(input.LA(1) >= '\u0B0F' && input.LA(1) <= '\u0B10')||(input.LA(1) >= '\u0B13' && input.LA(1) <= '\u0B28')||(input.LA(1) >= '\u0B2A' && input.LA(1) <= '\u0B30')||(input.LA(1) >= '\u0B32' && input.LA(1) <= '\u0B33')||(input.LA(1) >= '\u0B35' && input.LA(1) <= '\u0B39')||(input.LA(1) >= '\u0B3C' && input.LA(1) <= '\u0B43')||(input.LA(1) >= '\u0B47' && input.LA(1) <= '\u0B48')||(input.LA(1) >= '\u0B4B' && input.LA(1) <= '\u0B4D')||(input.LA(1) >= '\u0B56' && input.LA(1) <= '\u0B57')||(input.LA(1) >= '\u0B5C' && input.LA(1) <= '\u0B5D')||(input.LA(1) >= '\u0B5F' && input.LA(1) <= '\u0B61')||(input.LA(1) >= '\u0B66' && input.LA(1) <= '\u0B71')||(input.LA(1) >= '\u0B82' && input.LA(1) <= '\u0B83')||(input.LA(1) >= '\u0B85' && input.LA(1) <= '\u0B8A')||(input.LA(1) >= '\u0B8E' && input.LA(1) <= '\u0B90')||(input.LA(1) >= '\u0B92' && input.LA(1) <= '\u0B95')||(input.LA(1) >= '\u0B99' && input.LA(1) <= '\u0B9A')||input.LA(1)=='\u0B9C'||(input.LA(1) >= '\u0B9E' && input.LA(1) <= '\u0B9F')||(input.LA(1) >= '\u0BA3' && input.LA(1) <= '\u0BA4')||(input.LA(1) >= '\u0BA8' && input.LA(1) <= '\u0BAA')||(input.LA(1) >= '\u0BAE' && input.LA(1) <= '\u0BB5')||(input.LA(1) >= '\u0BB7' && input.LA(1) <= '\u0BB9')||(input.LA(1) >= '\u0BBE' && input.LA(1) <= '\u0BC2')||(input.LA(1) >= '\u0BC6' && input.LA(1) <= '\u0BC8')||(input.LA(1) >= '\u0BCA' && input.LA(1) <= '\u0BCD')||input.LA(1)=='\u0BD7'||(input.LA(1) >= '\u0BE7' && input.LA(1) <= '\u0BFA')||(input.LA(1) >= '\u0C01' && input.LA(1) <= '\u0C03')||(input.LA(1) >= '\u0C05' && input.LA(1) <= '\u0C0C')||(input.LA(1) >= '\u0C0E' && input.LA(1) <= '\u0C10')||(input.LA(1) >= '\u0C12' && input.LA(1) <= '\u0C28')||(input.LA(1) >= '\u0C2A' && input.LA(1) <= '\u0C33')||(input.LA(1) >= '\u0C35' && input.LA(1) <= '\u0C39')||(input.LA(1) >= '\u0C3E' && input.LA(1) <= '\u0C44')||(input.LA(1) >= '\u0C46' && input.LA(1) <= '\u0C48')||(input.LA(1) >= '\u0C4A' && input.LA(1) <= '\u0C4D')||(input.LA(1) >= '\u0C55' && input.LA(1) <= '\u0C56')||(input.LA(1) >= '\u0C60' && input.LA(1) <= '\u0C61')||(input.LA(1) >= '\u0C66' && input.LA(1) <= '\u0C6F')||(input.LA(1) >= '\u0C82' && input.LA(1) <= '\u0C83')||(input.LA(1) >= '\u0C85' && input.LA(1) <= '\u0C8C')||(input.LA(1) >= '\u0C8E' && input.LA(1) <= '\u0C90')||(input.LA(1) >= '\u0C92' && input.LA(1) <= '\u0CA8')||(input.LA(1) >= '\u0CAA' && input.LA(1) <= '\u0CB3')||(input.LA(1) >= '\u0CB5' && input.LA(1) <= '\u0CB9')||(input.LA(1) >= '\u0CBC' && input.LA(1) <= '\u0CC4')||(input.LA(1) >= '\u0CC6' && input.LA(1) <= '\u0CC8')||(input.LA(1) >= '\u0CCA' && input.LA(1) <= '\u0CCD')||(input.LA(1) >= '\u0CD5' && input.LA(1) <= '\u0CD6')||input.LA(1)=='\u0CDE'||(input.LA(1) >= '\u0CE0' && input.LA(1) <= '\u0CE1')||(input.LA(1) >= '\u0CE6' && input.LA(1) <= '\u0CEF')||(input.LA(1) >= '\u0D02' && input.LA(1) <= '\u0D03')||(input.LA(1) >= '\u0D05' && input.LA(1) <= '\u0D0C')||(input.LA(1) >= '\u0D0E' && input.LA(1) <= '\u0D10')||(input.LA(1) >= '\u0D12' && input.LA(1) <= '\u0D28')||(input.LA(1) >= '\u0D2A' && input.LA(1) <= '\u0D39')||(input.LA(1) >= '\u0D3E' && input.LA(1) <= '\u0D43')||(input.LA(1) >= '\u0D46' && input.LA(1) <= '\u0D48')||(input.LA(1) >= '\u0D4A' && input.LA(1) <= '\u0D4D')||input.LA(1)=='\u0D57'||(input.LA(1) >= '\u0D60' && input.LA(1) <= '\u0D61')||(input.LA(1) >= '\u0D66' && input.LA(1) <= '\u0D6F')||(input.LA(1) >= '\u0D82' && input.LA(1) <= '\u0D83')||(input.LA(1) >= '\u0D85' && input.LA(1) <= '\u0D96')||(input.LA(1) >= '\u0D9A' && input.LA(1) <= '\u0DB1')||(input.LA(1) >= '\u0DB3' && input.LA(1) <= '\u0DBB')||input.LA(1)=='\u0DBD'||(input.LA(1) >= '\u0DC0' && input.LA(1) <= '\u0DC6')||input.LA(1)=='\u0DCA'||(input.LA(1) >= '\u0DCF' && input.LA(1) <= '\u0DD4')||input.LA(1)=='\u0DD6'||(input.LA(1) >= '\u0DD8' && input.LA(1) <= '\u0DDF')||(input.LA(1) >= '\u0DF2' && input.LA(1) <= '\u0DF4')||(input.LA(1) >= '\u0E01' && input.LA(1) <= '\u0E3A')||(input.LA(1) >= '\u0E3F' && input.LA(1) <= '\u0E5B')||(input.LA(1) >= '\u0E81' && input.LA(1) <= '\u0E82')||input.LA(1)=='\u0E84'||(input.LA(1) >= '\u0E87' && input.LA(1) <= '\u0E88')||input.LA(1)=='\u0E8A'||input.LA(1)=='\u0E8D'||(input.LA(1) >= '\u0E94' && input.LA(1) <= '\u0E97')||(input.LA(1) >= '\u0E99' && input.LA(1) <= '\u0E9F')||(input.LA(1) >= '\u0EA1' && input.LA(1) <= '\u0EA3')||input.LA(1)=='\u0EA5'||input.LA(1)=='\u0EA7'||(input.LA(1) >= '\u0EAA' && input.LA(1) <= '\u0EAB')||(input.LA(1) >= '\u0EAD' && input.LA(1) <= '\u0EB9')||(input.LA(1) >= '\u0EBB' && input.LA(1) <= '\u0EBD')||(input.LA(1) >= '\u0EC0' && input.LA(1) <= '\u0EC4')||input.LA(1)=='\u0EC6'||(input.LA(1) >= '\u0EC8' && input.LA(1) <= '\u0ECD')||(input.LA(1) >= '\u0ED0' && input.LA(1) <= '\u0ED9')||(input.LA(1) >= '\u0EDC' && input.LA(1) <= '\u0EDD')||(input.LA(1) >= '\u0F00' && input.LA(1) <= '\u0F39')||(input.LA(1) >= '\u0F3E' && input.LA(1) <= '\u0F47')||(input.LA(1) >= '\u0F49' && input.LA(1) <= '\u0F6A')||(input.LA(1) >= '\u0F71' && input.LA(1) <= '\u0F8B')||(input.LA(1) >= '\u0F90' && input.LA(1) <= '\u0F97')||(input.LA(1) >= '\u0F99' && input.LA(1) <= '\u0FBC')||(input.LA(1) >= '\u0FBE' && input.LA(1) <= '\u0FCC')||input.LA(1)=='\u0FCF'||(input.LA(1) >= '\u1000' && input.LA(1) <= '\u1021')||(input.LA(1) >= '\u1023' && input.LA(1) <= '\u1027')||(input.LA(1) >= '\u1029' && input.LA(1) <= '\u102A')||(input.LA(1) >= '\u102C' && input.LA(1) <= '\u1032')||(input.LA(1) >= '\u1036' && input.LA(1) <= '\u1039')||(input.LA(1) >= '\u1040' && input.LA(1) <= '\u1059')||(input.LA(1) >= '\u10A0' && input.LA(1) <= '\u10C5')||(input.LA(1) >= '\u10D0' && input.LA(1) <= '\u10F8')||input.LA(1)=='\u10FB'||(input.LA(1) >= '\u1100' && input.LA(1) <= '\u1159')||(input.LA(1) >= '\u115F' && input.LA(1) <= '\u11A2')||(input.LA(1) >= '\u11A8' && input.LA(1) <= '\u11F9')||(input.LA(1) >= '\u1200' && input.LA(1) <= '\u1206')||(input.LA(1) >= '\u1208' && input.LA(1) <= '\u1246')||input.LA(1)=='\u1248'||(input.LA(1) >= '\u124A' && input.LA(1) <= '\u124D')||(input.LA(1) >= '\u1250' && input.LA(1) <= '\u1256')||input.LA(1)=='\u1258'||(input.LA(1) >= '\u125A' && input.LA(1) <= '\u125D')||(input.LA(1) >= '\u1260' && input.LA(1) <= '\u1286')||input.LA(1)=='\u1288'||(input.LA(1) >= '\u128A' && input.LA(1) <= '\u128D')||(input.LA(1) >= '\u1290' && input.LA(1) <= '\u12AE')||input.LA(1)=='\u12B0'||(input.LA(1) >= '\u12B2' && input.LA(1) <= '\u12B5')||(input.LA(1) >= '\u12B8' && input.LA(1) <= '\u12BE')||input.LA(1)=='\u12C0'||(input.LA(1) >= '\u12C2' && input.LA(1) <= '\u12C5')||(input.LA(1) >= '\u12C8' && input.LA(1) <= '\u12CE')||(input.LA(1) >= '\u12D0' && input.LA(1) <= '\u12D6')||(input.LA(1) >= '\u12D8' && input.LA(1) <= '\u12EE')||(input.LA(1) >= '\u12F0' && input.LA(1) <= '\u130E')||input.LA(1)=='\u1310'||(input.LA(1) >= '\u1312' && input.LA(1) <= '\u1315')||(input.LA(1) >= '\u1318' && input.LA(1) <= '\u131E')||(input.LA(1) >= '\u1320' && input.LA(1) <= '\u1346')||(input.LA(1) >= '\u1348' && input.LA(1) <= '\u135A')||(input.LA(1) >= '\u1361' && input.LA(1) <= '\u137C')||(input.LA(1) >= '\u13A0' && input.LA(1) <= '\u13F4')||(input.LA(1) >= '\u1401' && input.LA(1) <= '\u1676')||(input.LA(1) >= '\u1681' && input.LA(1) <= '\u169A')||(input.LA(1) >= '\u16A0' && input.LA(1) <= '\u16F0')||(input.LA(1) >= '\u1700' && input.LA(1) <= '\u170C')||(input.LA(1) >= '\u170E' && input.LA(1) <= '\u1714')||(input.LA(1) >= '\u1720' && input.LA(1) <= '\u1736')||(input.LA(1) >= '\u1740' && input.LA(1) <= '\u1753')||(input.LA(1) >= '\u1760' && input.LA(1) <= '\u176C')||(input.LA(1) >= '\u176E' && input.LA(1) <= '\u1770')||(input.LA(1) >= '\u1772' && input.LA(1) <= '\u1773')||(input.LA(1) >= '\u1780' && input.LA(1) <= '\u17B3')||(input.LA(1) >= '\u17B6' && input.LA(1) <= '\u17DD')||(input.LA(1) >= '\u17E0' && input.LA(1) <= '\u17E9')||(input.LA(1) >= '\u17F0' && input.LA(1) <= '\u17F9')||(input.LA(1) >= '\u1800' && input.LA(1) <= '\u180D')||(input.LA(1) >= '\u1810' && input.LA(1) <= '\u1819')||(input.LA(1) >= '\u1820' && input.LA(1) <= '\u1877')||(input.LA(1) >= '\u1880' && input.LA(1) <= '\u18A9')||(input.LA(1) >= '\u1900' && input.LA(1) <= '\u191C')||(input.LA(1) >= '\u1920' && input.LA(1) <= '\u192B')||(input.LA(1) >= '\u1930' && input.LA(1) <= '\u193B')||input.LA(1)=='\u1940'||(input.LA(1) >= '\u1944' && input.LA(1) <= '\u196D')||(input.LA(1) >= '\u1970' && input.LA(1) <= '\u1974')||(input.LA(1) >= '\u19E0' && input.LA(1) <= '\u19FF')||(input.LA(1) >= '\u1D00' && input.LA(1) <= '\u1D6B')||(input.LA(1) >= '\u1E00' && input.LA(1) <= '\u1E9B')||(input.LA(1) >= '\u1EA0' && input.LA(1) <= '\u1EF9')||(input.LA(1) >= '\u1F00' && input.LA(1) <= '\u1F15')||(input.LA(1) >= '\u1F18' && input.LA(1) <= '\u1F1D')||(input.LA(1) >= '\u1F20' && input.LA(1) <= '\u1F45')||(input.LA(1) >= '\u1F48' && input.LA(1) <= '\u1F4D')||(input.LA(1) >= '\u1F50' && input.LA(1) <= '\u1F57')||input.LA(1)=='\u1F59'||input.LA(1)=='\u1F5B'||input.LA(1)=='\u1F5D'||(input.LA(1) >= '\u1F5F' && input.LA(1) <= '\u1F7D')||(input.LA(1) >= '\u1F80' && input.LA(1) <= '\u1FB4')||(input.LA(1) >= '\u1FB6' && input.LA(1) <= '\u1FBC')||input.LA(1)=='\u1FBE'||(input.LA(1) >= '\u1FC2' && input.LA(1) <= '\u1FC4')||(input.LA(1) >= '\u1FC6' && input.LA(1) <= '\u1FCC')||(input.LA(1) >= '\u1FD0' && input.LA(1) <= '\u1FD3')||(input.LA(1) >= '\u1FD6' && input.LA(1) <= '\u1FDB')||(input.LA(1) >= '\u1FE0' && input.LA(1) <= '\u1FEC')||(input.LA(1) >= '\u1FF2' && input.LA(1) <= '\u1FF4')||(input.LA(1) >= '\u1FF6' && input.LA(1) <= '\u1FFC')||(input.LA(1) >= '\u2010' && input.LA(1) <= '\u2017')||(input.LA(1) >= '\u2020' && input.LA(1) <= '\u2027')||(input.LA(1) >= '\u2030' && input.LA(1) <= '\u2038')||(input.LA(1) >= '\u203B' && input.LA(1) <= '\u2044')||(input.LA(1) >= '\u2047' && input.LA(1) <= '\u2054')||input.LA(1)=='\u2057'||(input.LA(1) >= '\u2070' && input.LA(1) <= '\u2071')||(input.LA(1) >= '\u2074' && input.LA(1) <= '\u207C')||(input.LA(1) >= '\u207F' && input.LA(1) <= '\u208C')||(input.LA(1) >= '\u20A0' && input.LA(1) <= '\u20B1')||(input.LA(1) >= '\u20D0' && input.LA(1) <= '\u20EA')||(input.LA(1) >= '\u2100' && input.LA(1) <= '\u213B')||(input.LA(1) >= '\u213D' && input.LA(1) <= '\u214B')||(input.LA(1) >= '\u2153' && input.LA(1) <= '\u2183')||(input.LA(1) >= '\u2190' && input.LA(1) <= '\u2328')||(input.LA(1) >= '\u232B' && input.LA(1) <= '\u23B3')||(input.LA(1) >= '\u23B6' && input.LA(1) <= '\u23D0')||(input.LA(1) >= '\u2400' && input.LA(1) <= '\u2426')||(input.LA(1) >= '\u2440' && input.LA(1) <= '\u244A')||(input.LA(1) >= '\u2460' && input.LA(1) <= '\u2617')||(input.LA(1) >= '\u2619' && input.LA(1) <= '\u267D')||(input.LA(1) >= '\u2680' && input.LA(1) <= '\u2691')||(input.LA(1) >= '\u26A0' && input.LA(1) <= '\u26A1')||(input.LA(1) >= '\u2701' && input.LA(1) <= '\u2704')||(input.LA(1) >= '\u2706' && input.LA(1) <= '\u2709')||(input.LA(1) >= '\u270C' && input.LA(1) <= '\u2727')||(input.LA(1) >= '\u2729' && input.LA(1) <= '\u274B')||input.LA(1)=='\u274D'||(input.LA(1) >= '\u274F' && input.LA(1) <= '\u2752')||input.LA(1)=='\u2756'||(input.LA(1) >= '\u2758' && input.LA(1) <= '\u275E')||(input.LA(1) >= '\u2761' && input.LA(1) <= '\u2767')||(input.LA(1) >= '\u2776' && input.LA(1) <= '\u2794')||(input.LA(1) >= '\u2798' && input.LA(1) <= '\u27AF')||(input.LA(1) >= '\u27B1' && input.LA(1) <= '\u27BE')||(input.LA(1) >= '\u27D0' && input.LA(1) <= '\u27E5')||(input.LA(1) >= '\u27F0' && input.LA(1) <= '\u2982')||(input.LA(1) >= '\u2999' && input.LA(1) <= '\u29D7')||(input.LA(1) >= '\u29DC' && input.LA(1) <= '\u29FB')||(input.LA(1) >= '\u29FE' && input.LA(1) <= '\u2B0D')||(input.LA(1) >= '\u2E80' && input.LA(1) <= '\u2E99')||(input.LA(1) >= '\u2E9B' && input.LA(1) <= '\u2EF3')||(input.LA(1) >= '\u2F00' && input.LA(1) <= '\u2FD5')||(input.LA(1) >= '\u2FF0' && input.LA(1) <= '\u2FFB')||(input.LA(1) >= '\u3001' && input.LA(1) <= '\u3007')||(input.LA(1) >= '\u3012' && input.LA(1) <= '\u3013')||input.LA(1)=='\u301C'||(input.LA(1) >= '\u3020' && input.LA(1) <= '\u303F')||(input.LA(1) >= '\u3041' && input.LA(1) <= '\u3096')||(input.LA(1) >= '\u3099' && input.LA(1) <= '\u309A')||(input.LA(1) >= '\u309D' && input.LA(1) <= '\u30FF')||(input.LA(1) >= '\u3105' && input.LA(1) <= '\u312C')||(input.LA(1) >= '\u3131' && input.LA(1) <= '\u318E')||(input.LA(1) >= '\u3190' && input.LA(1) <= '\u31B7')||(input.LA(1) >= '\u31F0' && input.LA(1) <= '\u321E')||(input.LA(1) >= '\u3220' && input.LA(1) <= '\u3243')||(input.LA(1) >= '\u3250' && input.LA(1) <= '\u327D')||(input.LA(1) >= '\u327F' && input.LA(1) <= '\u32FE')||(input.LA(1) >= '\u3300' && input.LA(1) <= '\u4DB5')||(input.LA(1) >= '\u4DC0' && input.LA(1) <= '\u9FA5')||(input.LA(1) >= '\uA000' && input.LA(1) <= '\uA48C')||(input.LA(1) >= '\uA490' && input.LA(1) <= '\uA4C6')||(input.LA(1) >= '\uAC00' && input.LA(1) <= '\uD7A3')||(input.LA(1) >= '\uF900' && input.LA(1) <= '\uFA2D')||(input.LA(1) >= '\uFA30' && input.LA(1) <= '\uFA6A')||(input.LA(1) >= '\uFB00' && input.LA(1) <= '\uFB06')||(input.LA(1) >= '\uFB13' && input.LA(1) <= '\uFB17')||(input.LA(1) >= '\uFB1D' && input.LA(1) <= '\uFB36')||(input.LA(1) >= '\uFB38' && input.LA(1) <= '\uFB3C')||input.LA(1)=='\uFB3E'||(input.LA(1) >= '\uFB40' && input.LA(1) <= '\uFB41')||(input.LA(1) >= '\uFB43' && input.LA(1) <= '\uFB44')||(input.LA(1) >= '\uFB46' && input.LA(1) <= '\uFBB1')||(input.LA(1) >= '\uFBD3' && input.LA(1) <= '\uFD3D')||(input.LA(1) >= '\uFD50' && input.LA(1) <= '\uFD8F')||(input.LA(1) >= '\uFD92' && input.LA(1) <= '\uFDC7')||(input.LA(1) >= '\uFDF0' && input.LA(1) <= '\uFDFD')||(input.LA(1) >= '\uFE00' && input.LA(1) <= '\uFE0F')||(input.LA(1) >= '\uFE20' && input.LA(1) <= '\uFE23')||(input.LA(1) >= '\uFE30' && input.LA(1) <= '\uFE34')||(input.LA(1) >= '\uFE45' && input.LA(1) <= '\uFE46')||(input.LA(1) >= '\uFE49' && input.LA(1) <= '\uFE52')||(input.LA(1) >= '\uFE54' && input.LA(1) <= '\uFE58')||(input.LA(1) >= '\uFE5F' && input.LA(1) <= '\uFE66')||(input.LA(1) >= '\uFE68' && input.LA(1) <= '\uFE6B')||(input.LA(1) >= '\uFE70' && input.LA(1) <= '\uFE74')||(input.LA(1) >= '\uFE76' && input.LA(1) <= '\uFEFC')||(input.LA(1) >= '\uFF01' && input.LA(1) <= '\uFF07')||(input.LA(1) >= '\uFF0A' && input.LA(1) <= '\uFF3A')||input.LA(1)=='\uFF3C'||input.LA(1)=='\uFF3F'||(input.LA(1) >= '\uFF41' && input.LA(1) <= '\uFF5A')||input.LA(1)=='\uFF5C'||input.LA(1)=='\uFF5E'||input.LA(1)=='\uFF61'||(input.LA(1) >= '\uFF64' && input.LA(1) <= '\uFFBE')||(input.LA(1) >= '\uFFC2' && input.LA(1) <= '\uFFC7')||(input.LA(1) >= '\uFFCA' && input.LA(1) <= '\uFFCF')||(input.LA(1) >= '\uFFD2' && input.LA(1) <= '\uFFD7')||(input.LA(1) >= '\uFFDA' && input.LA(1) <= '\uFFDC')||(input.LA(1) >= '\uFFE0' && input.LA(1) <= '\uFFE2')||(input.LA(1) >= '\uFFE4' && input.LA(1) <= '\uFFE6')||(input.LA(1) >= '\uFFE8' && input.LA(1) <= '\uFFEE') ) {
                input.consume();
                state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "IN_WORD"

    // $ANTLR start "WS"
    public final void mWS() throws RecognitionException {
        try {
            int _type = WS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:2334:9: ( ( ' ' | '\\t' | '\\r' | '\\n' | '\\u000C' | '\\u00a0' | '\\u1680' | '\\u180e' | '\\u2000' .. '\\u200b' | '\\u2028' .. '\\u2029' | '\\u202f' | '\\u205f' | '\\u3000' )+ )
            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:2335:9: ( ' ' | '\\t' | '\\r' | '\\n' | '\\u000C' | '\\u00a0' | '\\u1680' | '\\u180e' | '\\u2000' .. '\\u200b' | '\\u2028' .. '\\u2029' | '\\u202f' | '\\u205f' | '\\u3000' )+
            {
            // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:2335:9: ( ' ' | '\\t' | '\\r' | '\\n' | '\\u000C' | '\\u00a0' | '\\u1680' | '\\u180e' | '\\u2000' .. '\\u200b' | '\\u2028' .. '\\u2029' | '\\u202f' | '\\u205f' | '\\u3000' )+
            int cnt36=0;
            loop36:
            do {
                int alt36=2;
                int LA36_0 = input.LA(1);

                if ( ((LA36_0 >= '\t' && LA36_0 <= '\n')||(LA36_0 >= '\f' && LA36_0 <= '\r')||LA36_0==' '||LA36_0=='\u00A0'||LA36_0=='\u1680'||LA36_0=='\u180E'||(LA36_0 >= '\u2000' && LA36_0 <= '\u200B')||(LA36_0 >= '\u2028' && LA36_0 <= '\u2029')||LA36_0=='\u202F'||LA36_0=='\u205F'||LA36_0=='\u3000') ) {
                    alt36=1;
                }


                switch (alt36) {
            	case 1 :
            	    // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:
            	    {
            	    if ( (input.LA(1) >= '\t' && input.LA(1) <= '\n')||(input.LA(1) >= '\f' && input.LA(1) <= '\r')||input.LA(1)==' '||input.LA(1)=='\u00A0'||input.LA(1)=='\u1680'||input.LA(1)=='\u180E'||(input.LA(1) >= '\u2000' && input.LA(1) <= '\u200B')||(input.LA(1) >= '\u2028' && input.LA(1) <= '\u2029')||input.LA(1)=='\u202F'||input.LA(1)=='\u205F'||input.LA(1)=='\u3000' ) {
            	        input.consume();
            	        state.failed=false;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return ;}
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;
            	    }


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


            if ( state.backtracking==0 ) { _channel = HIDDEN; }

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "WS"

    public void mTokens() throws RecognitionException {
        // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1:8: ( FTSPHRASE | URI | OR | AND | NOT | TILDA | LPAREN | RPAREN | PLUS | MINUS | COLON | STAR | AMP | EXCLAMATION | BAR | EQUALS | QUESTION_MARK | LCURL | RCURL | LSQUARE | RSQUARE | TO | COMMA | CARAT | DOLLAR | GT | LT | AT | PERCENT | ID | FLOATING_POINT_LITERAL | FTSWORD | FTSPRE | FTSWILD | WS )
        int alt37=35;
        alt37 = dfa37.predict(input);
        switch (alt37) {
            case 1 :
                // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1:10: FTSPHRASE
                {
                mFTSPHRASE(); if (state.failed) return ;


                }
                break;
            case 2 :
                // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1:20: URI
                {
                mURI(); if (state.failed) return ;


                }
                break;
            case 3 :
                // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1:24: OR
                {
                mOR(); if (state.failed) return ;


                }
                break;
            case 4 :
                // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1:27: AND
                {
                mAND(); if (state.failed) return ;


                }
                break;
            case 5 :
                // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1:31: NOT
                {
                mNOT(); if (state.failed) return ;


                }
                break;
            case 6 :
                // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1:35: TILDA
                {
                mTILDA(); if (state.failed) return ;


                }
                break;
            case 7 :
                // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1:41: LPAREN
                {
                mLPAREN(); if (state.failed) return ;


                }
                break;
            case 8 :
                // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1:48: RPAREN
                {
                mRPAREN(); if (state.failed) return ;


                }
                break;
            case 9 :
                // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1:55: PLUS
                {
                mPLUS(); if (state.failed) return ;


                }
                break;
            case 10 :
                // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1:60: MINUS
                {
                mMINUS(); if (state.failed) return ;


                }
                break;
            case 11 :
                // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1:66: COLON
                {
                mCOLON(); if (state.failed) return ;


                }
                break;
            case 12 :
                // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1:72: STAR
                {
                mSTAR(); if (state.failed) return ;


                }
                break;
            case 13 :
                // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1:77: AMP
                {
                mAMP(); if (state.failed) return ;


                }
                break;
            case 14 :
                // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1:81: EXCLAMATION
                {
                mEXCLAMATION(); if (state.failed) return ;


                }
                break;
            case 15 :
                // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1:93: BAR
                {
                mBAR(); if (state.failed) return ;


                }
                break;
            case 16 :
                // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1:97: EQUALS
                {
                mEQUALS(); if (state.failed) return ;


                }
                break;
            case 17 :
                // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1:104: QUESTION_MARK
                {
                mQUESTION_MARK(); if (state.failed) return ;


                }
                break;
            case 18 :
                // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1:118: LCURL
                {
                mLCURL(); if (state.failed) return ;


                }
                break;
            case 19 :
                // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1:124: RCURL
                {
                mRCURL(); if (state.failed) return ;


                }
                break;
            case 20 :
                // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1:130: LSQUARE
                {
                mLSQUARE(); if (state.failed) return ;


                }
                break;
            case 21 :
                // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1:138: RSQUARE
                {
                mRSQUARE(); if (state.failed) return ;


                }
                break;
            case 22 :
                // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1:146: TO
                {
                mTO(); if (state.failed) return ;


                }
                break;
            case 23 :
                // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1:149: COMMA
                {
                mCOMMA(); if (state.failed) return ;


                }
                break;
            case 24 :
                // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1:155: CARAT
                {
                mCARAT(); if (state.failed) return ;


                }
                break;
            case 25 :
                // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1:161: DOLLAR
                {
                mDOLLAR(); if (state.failed) return ;


                }
                break;
            case 26 :
                // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1:168: GT
                {
                mGT(); if (state.failed) return ;


                }
                break;
            case 27 :
                // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1:171: LT
                {
                mLT(); if (state.failed) return ;


                }
                break;
            case 28 :
                // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1:174: AT
                {
                mAT(); if (state.failed) return ;


                }
                break;
            case 29 :
                // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1:177: PERCENT
                {
                mPERCENT(); if (state.failed) return ;


                }
                break;
            case 30 :
                // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1:185: ID
                {
                mID(); if (state.failed) return ;


                }
                break;
            case 31 :
                // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1:188: FLOATING_POINT_LITERAL
                {
                mFLOATING_POINT_LITERAL(); if (state.failed) return ;


                }
                break;
            case 32 :
                // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1:211: FTSWORD
                {
                mFTSWORD(); if (state.failed) return ;


                }
                break;
            case 33 :
                // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1:219: FTSPRE
                {
                mFTSPRE(); if (state.failed) return ;


                }
                break;
            case 34 :
                // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1:226: FTSWILD
                {
                mFTSWILD(); if (state.failed) return ;


                }
                break;
            case 35 :
                // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:1:234: WS
                {
                mWS(); if (state.failed) return ;


                }
                break;

        }

    }

    // $ANTLR start synpred1_FTS
    public final void synpred1_FTS_fragment() throws RecognitionException {
        // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:966:17: ( F_URI_ALPHA | F_URI_DIGIT | F_URI_OTHER )
        // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:
        {
        if ( input.LA(1)=='!'||input.LA(1)=='$'||(input.LA(1) >= '&' && input.LA(1) <= '.')||(input.LA(1) >= '0' && input.LA(1) <= '9')||input.LA(1)==';'||input.LA(1)=='='||(input.LA(1) >= '@' && input.LA(1) <= '[')||input.LA(1)==']'||input.LA(1)=='_'||(input.LA(1) >= 'a' && input.LA(1) <= 'z')||input.LA(1)=='~' ) {
            input.consume();
            state.failed=false;
        }
        else {
            if (state.backtracking>0) {state.failed=true; return ;}
            MismatchedSetException mse = new MismatchedSetException(null,input);
            recover(mse);
            throw mse;
        }


        }

    }
    // $ANTLR end synpred1_FTS

    // $ANTLR start synpred2_FTS
    public final void synpred2_FTS_fragment() throws RecognitionException {
        // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:980:19: ( '//' )
        // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:980:20: '//'
        {
        match("//"); if (state.failed) return ;



        }

    }
    // $ANTLR end synpred2_FTS

    // $ANTLR start synpred3_FTS
    public final void synpred3_FTS_fragment() throws RecognitionException {
        // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:982:25: ( F_URI_ALPHA | F_URI_DIGIT | F_URI_OTHER | COLON )
        // W:\\alfresco\\HEAD-QA\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:
        {
        if ( input.LA(1)=='!'||input.LA(1)=='$'||(input.LA(1) >= '&' && input.LA(1) <= '.')||(input.LA(1) >= '0' && input.LA(1) <= ';')||input.LA(1)=='='||(input.LA(1) >= '@' && input.LA(1) <= '[')||input.LA(1)==']'||input.LA(1)=='_'||(input.LA(1) >= 'a' && input.LA(1) <= 'z')||input.LA(1)=='~' ) {
            input.consume();
            state.failed=false;
        }
        else {
            if (state.backtracking>0) {state.failed=true; return ;}
            MismatchedSetException mse = new MismatchedSetException(null,input);
            recover(mse);
            throw mse;
        }


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
    protected DFA37 dfa37 = new DFA37(this);
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
            "\1\1\1\uffff\1\5\1\1\1\uffff\11\1\1\2\12\1\1\3\1\1\1\uffff\1"+
            "\1\1\uffff\1\4\34\1\1\uffff\1\1\1\uffff\1\1\1\uffff\32\1\2\uffff"+
            "\1\6\1\1",
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
            return "965:9: ( ( F_URI_ALPHA | F_URI_DIGIT | F_URI_OTHER )=> ( F_URI_ALPHA | F_URI_DIGIT | F_URI_OTHER )+ COLON )?";
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
    static final String DFA37_eotS =
        "\3\uffff\1\53\3\65\3\uffff\1\75\1\77\1\uffff\1\100\4\uffff\1\104"+
        "\3\uffff\1\65\2\uffff\1\107\4\uffff\1\65\1\113\3\uffff\1\117\10"+
        "\uffff\2\120\6\65\2\uffff\1\117\1\123\1\uffff\4\65\10\uffff\2\130"+
        "\3\uffff\1\117\1\uffff\1\113\2\117\2\uffff\2\65\1\uffff\2\141\2"+
        "\142\1\uffff\3\117\1\113\1\117\3\65\2\uffff\2\117\3\65\2\117\3\65"+
        "\2\117\3\65\1\117";
    static final String DFA37_eofS =
        "\163\uffff";
    static final String DFA37_minS =
        "\1\11\2\uffff\4\41\3\uffff\2\56\1\uffff\1\41\4\uffff\1\41\3\uffff"+
        "\1\41\2\uffff\1\41\4\uffff\2\41\1\uffff\1\0\1\uffff\1\41\10\uffff"+
        "\10\41\1\0\1\uffff\2\41\1\uffff\4\41\10\uffff\2\41\1\uffff\1\0\1"+
        "\uffff\1\41\1\uffff\3\41\2\uffff\2\41\1\uffff\4\41\1\uffff\10\41"+
        "\2\uffff\20\41";
    static final String DFA37_maxS =
        "\1\uffee\2\uffff\1\176\3\uffee\3\uffff\2\71\1\uffff\1\uffee\4\uffff"+
        "\1\uffee\3\uffff\1\uffee\2\uffff\1\uffee\4\uffff\2\uffee\1\uffff"+
        "\1\uffff\1\uffff\1\uffee\10\uffff\10\uffee\1\uffff\1\uffff\2\uffee"+
        "\1\uffff\4\uffee\10\uffff\2\uffee\1\uffff\1\uffff\1\uffff\1\uffee"+
        "\1\uffff\3\uffee\2\uffff\2\uffee\1\uffff\4\uffee\1\uffff\10\uffee"+
        "\2\uffff\20\uffee";
    static final String DFA37_acceptS =
        "\1\uffff\2\1\4\uffff\1\6\1\7\1\10\2\uffff\1\13\1\uffff\1\15\1\16"+
        "\1\17\1\20\1\uffff\1\23\1\24\1\25\1\uffff\1\27\1\30\1\uffff\1\32"+
        "\1\33\1\34\1\35\2\uffff\1\37\1\uffff\1\36\1\uffff\1\43\6\2\1\22"+
        "\11\uffff\1\36\2\uffff\1\42\4\uffff\1\11\1\37\1\12\1\14\3\42\1\21"+
        "\2\uffff\1\31\1\uffff\1\37\1\uffff\1\37\3\uffff\1\40\1\3\2\uffff"+
        "\1\41\4\uffff\1\26\10\uffff\1\4\1\5\20\uffff";
    static final String DFA37_specialS =
        "\41\uffff\1\1\22\uffff\1\0\23\uffff\1\2\52\uffff}>";
    static final String[] DFA37_transitionS = {
            "\2\44\1\uffff\2\44\22\uffff\1\44\1\17\1\1\1\uffff\1\31\1\35"+
            "\1\16\1\2\1\10\1\11\1\15\1\12\1\27\1\13\1\40\1\uffff\12\37\1"+
            "\14\1\uffff\1\33\1\21\1\32\1\22\1\34\1\5\14\36\1\6\1\4\4\36"+
            "\1\26\6\36\1\24\1\41\1\25\1\30\1\42\1\uffff\1\5\14\36\1\6\1"+
            "\4\4\36\1\26\6\36\1\3\1\20\1\23\1\7\41\uffff\1\44\1\uffff\6"+
            "\43\1\uffff\2\43\3\uffff\1\43\1\uffff\1\43\1\uffff\2\43\1\uffff"+
            "\2\43\2\uffff\2\43\1\uffff\3\43\1\uffff\27\43\1\uffff\37\43"+
            "\1\uffff\u013f\43\31\uffff\162\43\4\uffff\14\43\16\uffff\5\43"+
            "\11\uffff\1\43\21\uffff\130\43\5\uffff\23\43\12\uffff\1\43\13"+
            "\uffff\1\43\1\uffff\3\43\1\uffff\1\43\1\uffff\24\43\1\uffff"+
            "\54\43\1\uffff\46\43\1\uffff\5\43\4\uffff\u0087\43\1\uffff\107"+
            "\43\1\uffff\46\43\2\uffff\2\43\6\uffff\20\43\41\uffff\46\43"+
            "\2\uffff\1\43\7\uffff\47\43\11\uffff\21\43\1\uffff\27\43\1\uffff"+
            "\3\43\1\uffff\1\43\1\uffff\2\43\1\uffff\1\43\13\uffff\33\43"+
            "\5\uffff\3\43\33\uffff\10\43\13\uffff\32\43\5\uffff\31\43\7"+
            "\uffff\12\43\4\uffff\146\43\1\uffff\10\43\1\uffff\42\43\20\uffff"+
            "\73\43\2\uffff\3\43\60\uffff\62\43\u014f\uffff\71\43\2\uffff"+
            "\22\43\2\uffff\5\43\3\uffff\14\43\2\uffff\12\43\21\uffff\3\43"+
            "\1\uffff\10\43\2\uffff\2\43\2\uffff\26\43\1\uffff\7\43\1\uffff"+
            "\1\43\3\uffff\4\43\2\uffff\11\43\2\uffff\2\43\2\uffff\3\43\11"+
            "\uffff\1\43\4\uffff\2\43\1\uffff\5\43\2\uffff\25\43\6\uffff"+
            "\3\43\1\uffff\6\43\4\uffff\2\43\2\uffff\26\43\1\uffff\7\43\1"+
            "\uffff\2\43\1\uffff\2\43\1\uffff\2\43\2\uffff\1\43\1\uffff\5"+
            "\43\4\uffff\2\43\2\uffff\3\43\13\uffff\4\43\1\uffff\1\43\7\uffff"+
            "\17\43\14\uffff\3\43\1\uffff\11\43\1\uffff\3\43\1\uffff\26\43"+
            "\1\uffff\7\43\1\uffff\2\43\1\uffff\5\43\2\uffff\12\43\1\uffff"+
            "\3\43\1\uffff\3\43\2\uffff\1\43\17\uffff\4\43\2\uffff\12\43"+
            "\1\uffff\1\43\17\uffff\3\43\1\uffff\10\43\2\uffff\2\43\2\uffff"+
            "\26\43\1\uffff\7\43\1\uffff\2\43\1\uffff\5\43\2\uffff\10\43"+
            "\3\uffff\2\43\2\uffff\3\43\10\uffff\2\43\4\uffff\2\43\1\uffff"+
            "\3\43\4\uffff\14\43\20\uffff\2\43\1\uffff\6\43\3\uffff\3\43"+
            "\1\uffff\4\43\3\uffff\2\43\1\uffff\1\43\1\uffff\2\43\3\uffff"+
            "\2\43\3\uffff\3\43\3\uffff\10\43\1\uffff\3\43\4\uffff\5\43\3"+
            "\uffff\3\43\1\uffff\4\43\11\uffff\1\43\17\uffff\24\43\6\uffff"+
            "\3\43\1\uffff\10\43\1\uffff\3\43\1\uffff\27\43\1\uffff\12\43"+
            "\1\uffff\5\43\4\uffff\7\43\1\uffff\3\43\1\uffff\4\43\7\uffff"+
            "\2\43\11\uffff\2\43\4\uffff\12\43\22\uffff\2\43\1\uffff\10\43"+
            "\1\uffff\3\43\1\uffff\27\43\1\uffff\12\43\1\uffff\5\43\2\uffff"+
            "\11\43\1\uffff\3\43\1\uffff\4\43\7\uffff\2\43\7\uffff\1\43\1"+
            "\uffff\2\43\4\uffff\12\43\22\uffff\2\43\1\uffff\10\43\1\uffff"+
            "\3\43\1\uffff\27\43\1\uffff\20\43\4\uffff\6\43\2\uffff\3\43"+
            "\1\uffff\4\43\11\uffff\1\43\10\uffff\2\43\4\uffff\12\43\22\uffff"+
            "\2\43\1\uffff\22\43\3\uffff\30\43\1\uffff\11\43\1\uffff\1\43"+
            "\2\uffff\7\43\3\uffff\1\43\4\uffff\6\43\1\uffff\1\43\1\uffff"+
            "\10\43\22\uffff\2\43\15\uffff\72\43\4\uffff\20\43\1\uffff\12"+
            "\43\47\uffff\2\43\1\uffff\1\43\2\uffff\2\43\1\uffff\1\43\2\uffff"+
            "\1\43\6\uffff\4\43\1\uffff\7\43\1\uffff\3\43\1\uffff\1\43\1"+
            "\uffff\1\43\2\uffff\2\43\1\uffff\15\43\1\uffff\3\43\2\uffff"+
            "\5\43\1\uffff\1\43\1\uffff\6\43\2\uffff\12\43\2\uffff\2\43\42"+
            "\uffff\4\43\17\uffff\47\43\4\uffff\12\43\1\uffff\42\43\6\uffff"+
            "\24\43\1\uffff\6\43\4\uffff\10\43\1\uffff\44\43\1\uffff\17\43"+
            "\2\uffff\1\43\60\uffff\42\43\1\uffff\5\43\1\uffff\2\43\1\uffff"+
            "\7\43\3\uffff\4\43\6\uffff\12\43\6\uffff\12\43\106\uffff\46"+
            "\43\12\uffff\51\43\7\uffff\132\43\5\uffff\104\43\5\uffff\122"+
            "\43\6\uffff\7\43\1\uffff\77\43\1\uffff\1\43\1\uffff\4\43\2\uffff"+
            "\7\43\1\uffff\1\43\1\uffff\4\43\2\uffff\47\43\1\uffff\1\43\1"+
            "\uffff\4\43\2\uffff\37\43\1\uffff\1\43\1\uffff\4\43\2\uffff"+
            "\7\43\1\uffff\1\43\1\uffff\4\43\2\uffff\7\43\1\uffff\7\43\1"+
            "\uffff\27\43\1\uffff\37\43\1\uffff\1\43\1\uffff\4\43\2\uffff"+
            "\7\43\1\uffff\47\43\1\uffff\23\43\16\uffff\24\43\43\uffff\125"+
            "\43\14\uffff\u026c\43\2\uffff\10\43\11\uffff\1\44\32\43\5\uffff"+
            "\113\43\3\uffff\3\43\17\uffff\15\43\1\uffff\7\43\13\uffff\25"+
            "\43\13\uffff\24\43\14\uffff\15\43\1\uffff\3\43\1\uffff\2\43"+
            "\14\uffff\64\43\2\uffff\36\43\3\uffff\1\43\3\uffff\3\43\2\uffff"+
            "\12\43\6\uffff\12\43\21\uffff\3\43\1\44\1\uffff\12\43\6\uffff"+
            "\130\43\10\uffff\52\43\126\uffff\35\43\3\uffff\14\43\4\uffff"+
            "\14\43\4\uffff\1\43\5\uffff\50\43\2\uffff\5\43\153\uffff\40"+
            "\43\u0300\uffff\154\43\u0094\uffff\u009c\43\4\uffff\132\43\6"+
            "\uffff\26\43\2\uffff\6\43\2\uffff\46\43\2\uffff\6\43\2\uffff"+
            "\10\43\1\uffff\1\43\1\uffff\1\43\1\uffff\1\43\1\uffff\37\43"+
            "\2\uffff\65\43\1\uffff\7\43\1\uffff\1\43\3\uffff\3\43\1\uffff"+
            "\7\43\3\uffff\4\43\2\uffff\6\43\4\uffff\15\43\5\uffff\3\43\1"+
            "\uffff\7\43\3\uffff\14\44\34\uffff\2\44\5\uffff\1\44\57\uffff"+
            "\1\44\20\uffff\2\43\2\uffff\6\43\5\uffff\13\43\26\uffff\22\43"+
            "\36\uffff\33\43\25\uffff\74\43\1\uffff\3\43\5\uffff\6\43\10"+
            "\uffff\61\43\21\uffff\5\43\2\uffff\4\43\1\uffff\2\43\1\uffff"+
            "\2\43\1\uffff\7\43\1\uffff\37\43\2\uffff\2\43\1\uffff\1\43\1"+
            "\uffff\37\43\u010c\uffff\10\43\4\uffff\24\43\2\uffff\7\43\2"+
            "\uffff\121\43\1\uffff\36\43\34\uffff\32\43\57\uffff\47\43\31"+
            "\uffff\13\43\25\uffff\u0157\43\1\uffff\11\43\1\uffff\66\43\10"+
            "\uffff\30\43\1\uffff\126\43\1\uffff\16\43\2\uffff\22\43\16\uffff"+
            "\2\43\137\uffff\4\43\1\uffff\4\43\2\uffff\34\43\1\uffff\43\43"+
            "\1\uffff\1\43\1\uffff\4\43\3\uffff\1\43\1\uffff\7\43\2\uffff"+
            "\7\43\16\uffff\37\43\3\uffff\30\43\1\uffff\16\43\101\uffff\u0100"+
            "\43\u0200\uffff\16\43\u0372\uffff\32\43\1\uffff\131\43\14\uffff"+
            "\u00d6\43\32\uffff\14\43\4\uffff\1\44\3\uffff\4\43\12\uffff"+
            "\2\43\14\uffff\20\43\1\uffff\14\43\1\uffff\2\43\1\uffff\126"+
            "\43\2\uffff\2\43\2\uffff\3\43\1\uffff\132\43\1\uffff\4\43\5"+
            "\uffff\50\43\4\uffff\136\43\1\uffff\50\43\70\uffff\57\43\1\uffff"+
            "\44\43\14\uffff\56\43\1\uffff\u0080\43\1\uffff\u1ab6\43\12\uffff"+
            "\u51e6\43\132\uffff\u048d\43\3\uffff\67\43\u0739\uffff\u2ba4"+
            "\43\u215c\uffff\u012e\43\2\uffff\73\43\u0095\uffff\7\43\14\uffff"+
            "\5\43\5\uffff\14\43\1\uffff\15\43\1\uffff\5\43\1\uffff\1\43"+
            "\1\uffff\2\43\1\uffff\2\43\1\uffff\154\43\41\uffff\u016b\43"+
            "\22\uffff\100\43\2\uffff\66\43\50\uffff\16\43\2\uffff\20\43"+
            "\20\uffff\4\43\105\uffff\1\43\6\uffff\5\43\1\uffff\u0087\43"+
            "\7\uffff\1\43\13\uffff\12\43\7\uffff\32\43\6\uffff\32\43\13"+
            "\uffff\131\43\3\uffff\6\43\2\uffff\6\43\2\uffff\6\43\2\uffff"+
            "\3\43\3\uffff\2\43\2\uffff\3\43\1\uffff\1\43\4\uffff\2\43",
            "",
            "",
            "\1\45\1\uffff\1\51\1\45\1\uffff\11\45\1\46\12\45\1\47\1\45"+
            "\1\uffff\1\45\1\uffff\1\50\34\45\1\uffff\1\45\1\uffff\1\45\1"+
            "\uffff\32\45\2\uffff\1\52\1\45",
            "\2\66\1\63\1\62\3\66\2\uffff\1\67\1\66\1\uffff\1\66\1\uffff"+
            "\1\66\12\60\1\uffff\1\66\1\uffff\1\66\1\uffff\1\70\1\66\21\57"+
            "\1\55\10\57\1\uffff\1\64\2\uffff\1\61\1\uffff\21\56\1\54\10"+
            "\56\1\uffff\1\66\44\uffff\7\66\1\uffff\2\66\1\uffff\1\66\1\uffff"+
            "\1\66\1\uffff\4\66\1\uffff\3\66\1\uffff\2\66\1\uffff\u017b\66"+
            "\31\uffff\162\66\4\uffff\14\66\16\uffff\5\66\11\uffff\1\66\21"+
            "\uffff\130\66\5\uffff\23\66\12\uffff\1\66\3\uffff\1\66\7\uffff"+
            "\5\66\1\uffff\1\66\1\uffff\24\66\1\uffff\54\66\1\uffff\54\66"+
            "\4\uffff\u0087\66\1\uffff\107\66\1\uffff\46\66\2\uffff\2\66"+
            "\6\uffff\20\66\41\uffff\46\66\2\uffff\7\66\1\uffff\47\66\1\uffff"+
            "\2\66\6\uffff\21\66\1\uffff\27\66\1\uffff\12\66\13\uffff\33"+
            "\66\5\uffff\5\66\27\uffff\12\66\5\uffff\1\66\3\uffff\1\66\1"+
            "\uffff\32\66\5\uffff\31\66\7\uffff\175\66\1\uffff\60\66\2\uffff"+
            "\73\66\2\uffff\3\66\60\uffff\62\66\u014f\uffff\71\66\2\uffff"+
            "\22\66\2\uffff\5\66\3\uffff\31\66\20\uffff\3\66\1\uffff\10\66"+
            "\2\uffff\2\66\2\uffff\26\66\1\uffff\7\66\1\uffff\1\66\3\uffff"+
            "\4\66\2\uffff\11\66\2\uffff\2\66\2\uffff\3\66\11\uffff\1\66"+
            "\4\uffff\2\66\1\uffff\5\66\2\uffff\25\66\6\uffff\3\66\1\uffff"+
            "\6\66\4\uffff\2\66\2\uffff\26\66\1\uffff\7\66\1\uffff\2\66\1"+
            "\uffff\2\66\1\uffff\2\66\2\uffff\1\66\1\uffff\5\66\4\uffff\2"+
            "\66\2\uffff\3\66\13\uffff\4\66\1\uffff\1\66\7\uffff\17\66\14"+
            "\uffff\3\66\1\uffff\11\66\1\uffff\3\66\1\uffff\26\66\1\uffff"+
            "\7\66\1\uffff\2\66\1\uffff\5\66\2\uffff\12\66\1\uffff\3\66\1"+
            "\uffff\3\66\2\uffff\1\66\17\uffff\4\66\2\uffff\12\66\1\uffff"+
            "\1\66\17\uffff\3\66\1\uffff\10\66\2\uffff\2\66\2\uffff\26\66"+
            "\1\uffff\7\66\1\uffff\2\66\1\uffff\5\66\2\uffff\10\66\3\uffff"+
            "\2\66\2\uffff\3\66\10\uffff\2\66\4\uffff\2\66\1\uffff\3\66\4"+
            "\uffff\14\66\20\uffff\2\66\1\uffff\6\66\3\uffff\3\66\1\uffff"+
            "\4\66\3\uffff\2\66\1\uffff\1\66\1\uffff\2\66\3\uffff\2\66\3"+
            "\uffff\3\66\3\uffff\10\66\1\uffff\3\66\4\uffff\5\66\3\uffff"+
            "\3\66\1\uffff\4\66\11\uffff\1\66\17\uffff\24\66\6\uffff\3\66"+
            "\1\uffff\10\66\1\uffff\3\66\1\uffff\27\66\1\uffff\12\66\1\uffff"+
            "\5\66\4\uffff\7\66\1\uffff\3\66\1\uffff\4\66\7\uffff\2\66\11"+
            "\uffff\2\66\4\uffff\12\66\22\uffff\2\66\1\uffff\10\66\1\uffff"+
            "\3\66\1\uffff\27\66\1\uffff\12\66\1\uffff\5\66\2\uffff\11\66"+
            "\1\uffff\3\66\1\uffff\4\66\7\uffff\2\66\7\uffff\1\66\1\uffff"+
            "\2\66\4\uffff\12\66\22\uffff\2\66\1\uffff\10\66\1\uffff\3\66"+
            "\1\uffff\27\66\1\uffff\20\66\4\uffff\6\66\2\uffff\3\66\1\uffff"+
            "\4\66\11\uffff\1\66\10\uffff\2\66\4\uffff\12\66\22\uffff\2\66"+
            "\1\uffff\22\66\3\uffff\30\66\1\uffff\11\66\1\uffff\1\66\2\uffff"+
            "\7\66\3\uffff\1\66\4\uffff\6\66\1\uffff\1\66\1\uffff\10\66\22"+
            "\uffff\3\66\14\uffff\72\66\4\uffff\35\66\45\uffff\2\66\1\uffff"+
            "\1\66\2\uffff\2\66\1\uffff\1\66\2\uffff\1\66\6\uffff\4\66\1"+
            "\uffff\7\66\1\uffff\3\66\1\uffff\1\66\1\uffff\1\66\2\uffff\2"+
            "\66\1\uffff\15\66\1\uffff\3\66\2\uffff\5\66\1\uffff\1\66\1\uffff"+
            "\6\66\2\uffff\12\66\2\uffff\2\66\42\uffff\72\66\4\uffff\12\66"+
            "\1\uffff\42\66\6\uffff\33\66\4\uffff\10\66\1\uffff\44\66\1\uffff"+
            "\17\66\2\uffff\1\66\60\uffff\42\66\1\uffff\5\66\1\uffff\2\66"+
            "\1\uffff\7\66\3\uffff\4\66\6\uffff\32\66\106\uffff\46\66\12"+
            "\uffff\51\66\2\uffff\1\66\4\uffff\132\66\5\uffff\104\66\5\uffff"+
            "\122\66\6\uffff\7\66\1\uffff\77\66\1\uffff\1\66\1\uffff\4\66"+
            "\2\uffff\7\66\1\uffff\1\66\1\uffff\4\66\2\uffff\47\66\1\uffff"+
            "\1\66\1\uffff\4\66\2\uffff\37\66\1\uffff\1\66\1\uffff\4\66\2"+
            "\uffff\7\66\1\uffff\1\66\1\uffff\4\66\2\uffff\7\66\1\uffff\7"+
            "\66\1\uffff\27\66\1\uffff\37\66\1\uffff\1\66\1\uffff\4\66\2"+
            "\uffff\7\66\1\uffff\47\66\1\uffff\23\66\6\uffff\34\66\43\uffff"+
            "\125\66\14\uffff\u0276\66\12\uffff\32\66\5\uffff\121\66\17\uffff"+
            "\15\66\1\uffff\7\66\13\uffff\27\66\11\uffff\24\66\14\uffff\15"+
            "\66\1\uffff\3\66\1\uffff\2\66\14\uffff\64\66\2\uffff\50\66\2"+
            "\uffff\12\66\6\uffff\12\66\6\uffff\16\66\2\uffff\12\66\6\uffff"+
            "\130\66\10\uffff\52\66\126\uffff\35\66\3\uffff\14\66\4\uffff"+
            "\14\66\4\uffff\1\66\3\uffff\52\66\2\uffff\5\66\153\uffff\40"+
            "\66\u0300\uffff\154\66\u0094\uffff\u009c\66\4\uffff\132\66\6"+
            "\uffff\26\66\2\uffff\6\66\2\uffff\46\66\2\uffff\6\66\2\uffff"+
            "\10\66\1\uffff\1\66\1\uffff\1\66\1\uffff\1\66\1\uffff\37\66"+
            "\2\uffff\65\66\1\uffff\7\66\1\uffff\1\66\3\uffff\3\66\1\uffff"+
            "\7\66\3\uffff\4\66\2\uffff\6\66\4\uffff\15\66\5\uffff\3\66\1"+
            "\uffff\7\66\23\uffff\10\66\10\uffff\10\66\10\uffff\11\66\2\uffff"+
            "\12\66\2\uffff\16\66\2\uffff\1\66\30\uffff\2\66\2\uffff\11\66"+
            "\2\uffff\16\66\23\uffff\22\66\36\uffff\33\66\25\uffff\74\66"+
            "\1\uffff\17\66\7\uffff\61\66\14\uffff\u0199\66\2\uffff\u0089"+
            "\66\2\uffff\33\66\57\uffff\47\66\31\uffff\13\66\25\uffff\u01b8"+
            "\66\1\uffff\145\66\2\uffff\22\66\16\uffff\2\66\137\uffff\4\66"+
            "\1\uffff\4\66\2\uffff\34\66\1\uffff\43\66\1\uffff\1\66\1\uffff"+
            "\4\66\3\uffff\1\66\1\uffff\7\66\2\uffff\7\66\16\uffff\37\66"+
            "\3\uffff\30\66\1\uffff\16\66\21\uffff\26\66\12\uffff\u0193\66"+
            "\26\uffff\77\66\4\uffff\40\66\2\uffff\u0110\66\u0372\uffff\32"+
            "\66\1\uffff\131\66\14\uffff\u00d6\66\32\uffff\14\66\5\uffff"+
            "\7\66\12\uffff\2\66\10\uffff\1\66\3\uffff\40\66\1\uffff\126"+
            "\66\2\uffff\2\66\2\uffff\143\66\5\uffff\50\66\4\uffff\136\66"+
            "\1\uffff\50\66\70\uffff\57\66\1\uffff\44\66\14\uffff\56\66\1"+
            "\uffff\u0080\66\1\uffff\u1ab6\66\12\uffff\u51e6\66\132\uffff"+
            "\u048d\66\3\uffff\67\66\u0739\uffff\u2ba4\66\u215c\uffff\u012e"+
            "\66\2\uffff\73\66\u0095\uffff\7\66\14\uffff\5\66\5\uffff\32"+
            "\66\1\uffff\5\66\1\uffff\1\66\1\uffff\2\66\1\uffff\2\66\1\uffff"+
            "\154\66\41\uffff\u016b\66\22\uffff\100\66\2\uffff\66\66\50\uffff"+
            "\16\66\2\uffff\20\66\20\uffff\4\66\14\uffff\5\66\20\uffff\2"+
            "\66\2\uffff\12\66\1\uffff\5\66\6\uffff\10\66\1\uffff\4\66\4"+
            "\uffff\5\66\1\uffff\u0087\66\4\uffff\7\66\2\uffff\61\66\1\uffff"+
            "\1\66\2\uffff\1\66\1\uffff\32\66\1\uffff\1\66\1\uffff\1\66\2"+
            "\uffff\1\66\2\uffff\133\66\3\uffff\6\66\2\uffff\6\66\2\uffff"+
            "\6\66\2\uffff\3\66\3\uffff\3\66\1\uffff\3\66\1\uffff\7\66",
            "\2\66\1\63\1\62\3\66\2\uffff\1\67\1\66\1\uffff\1\66\1\uffff"+
            "\1\66\12\60\1\uffff\1\66\1\uffff\1\66\1\uffff\1\70\1\66\15\57"+
            "\1\72\14\57\1\uffff\1\64\2\uffff\1\61\1\uffff\15\56\1\71\14"+
            "\56\1\uffff\1\66\44\uffff\7\66\1\uffff\2\66\1\uffff\1\66\1\uffff"+
            "\1\66\1\uffff\4\66\1\uffff\3\66\1\uffff\2\66\1\uffff\u017b\66"+
            "\31\uffff\162\66\4\uffff\14\66\16\uffff\5\66\11\uffff\1\66\21"+
            "\uffff\130\66\5\uffff\23\66\12\uffff\1\66\3\uffff\1\66\7\uffff"+
            "\5\66\1\uffff\1\66\1\uffff\24\66\1\uffff\54\66\1\uffff\54\66"+
            "\4\uffff\u0087\66\1\uffff\107\66\1\uffff\46\66\2\uffff\2\66"+
            "\6\uffff\20\66\41\uffff\46\66\2\uffff\7\66\1\uffff\47\66\1\uffff"+
            "\2\66\6\uffff\21\66\1\uffff\27\66\1\uffff\12\66\13\uffff\33"+
            "\66\5\uffff\5\66\27\uffff\12\66\5\uffff\1\66\3\uffff\1\66\1"+
            "\uffff\32\66\5\uffff\31\66\7\uffff\175\66\1\uffff\60\66\2\uffff"+
            "\73\66\2\uffff\3\66\60\uffff\62\66\u014f\uffff\71\66\2\uffff"+
            "\22\66\2\uffff\5\66\3\uffff\31\66\20\uffff\3\66\1\uffff\10\66"+
            "\2\uffff\2\66\2\uffff\26\66\1\uffff\7\66\1\uffff\1\66\3\uffff"+
            "\4\66\2\uffff\11\66\2\uffff\2\66\2\uffff\3\66\11\uffff\1\66"+
            "\4\uffff\2\66\1\uffff\5\66\2\uffff\25\66\6\uffff\3\66\1\uffff"+
            "\6\66\4\uffff\2\66\2\uffff\26\66\1\uffff\7\66\1\uffff\2\66\1"+
            "\uffff\2\66\1\uffff\2\66\2\uffff\1\66\1\uffff\5\66\4\uffff\2"+
            "\66\2\uffff\3\66\13\uffff\4\66\1\uffff\1\66\7\uffff\17\66\14"+
            "\uffff\3\66\1\uffff\11\66\1\uffff\3\66\1\uffff\26\66\1\uffff"+
            "\7\66\1\uffff\2\66\1\uffff\5\66\2\uffff\12\66\1\uffff\3\66\1"+
            "\uffff\3\66\2\uffff\1\66\17\uffff\4\66\2\uffff\12\66\1\uffff"+
            "\1\66\17\uffff\3\66\1\uffff\10\66\2\uffff\2\66\2\uffff\26\66"+
            "\1\uffff\7\66\1\uffff\2\66\1\uffff\5\66\2\uffff\10\66\3\uffff"+
            "\2\66\2\uffff\3\66\10\uffff\2\66\4\uffff\2\66\1\uffff\3\66\4"+
            "\uffff\14\66\20\uffff\2\66\1\uffff\6\66\3\uffff\3\66\1\uffff"+
            "\4\66\3\uffff\2\66\1\uffff\1\66\1\uffff\2\66\3\uffff\2\66\3"+
            "\uffff\3\66\3\uffff\10\66\1\uffff\3\66\4\uffff\5\66\3\uffff"+
            "\3\66\1\uffff\4\66\11\uffff\1\66\17\uffff\24\66\6\uffff\3\66"+
            "\1\uffff\10\66\1\uffff\3\66\1\uffff\27\66\1\uffff\12\66\1\uffff"+
            "\5\66\4\uffff\7\66\1\uffff\3\66\1\uffff\4\66\7\uffff\2\66\11"+
            "\uffff\2\66\4\uffff\12\66\22\uffff\2\66\1\uffff\10\66\1\uffff"+
            "\3\66\1\uffff\27\66\1\uffff\12\66\1\uffff\5\66\2\uffff\11\66"+
            "\1\uffff\3\66\1\uffff\4\66\7\uffff\2\66\7\uffff\1\66\1\uffff"+
            "\2\66\4\uffff\12\66\22\uffff\2\66\1\uffff\10\66\1\uffff\3\66"+
            "\1\uffff\27\66\1\uffff\20\66\4\uffff\6\66\2\uffff\3\66\1\uffff"+
            "\4\66\11\uffff\1\66\10\uffff\2\66\4\uffff\12\66\22\uffff\2\66"+
            "\1\uffff\22\66\3\uffff\30\66\1\uffff\11\66\1\uffff\1\66\2\uffff"+
            "\7\66\3\uffff\1\66\4\uffff\6\66\1\uffff\1\66\1\uffff\10\66\22"+
            "\uffff\3\66\14\uffff\72\66\4\uffff\35\66\45\uffff\2\66\1\uffff"+
            "\1\66\2\uffff\2\66\1\uffff\1\66\2\uffff\1\66\6\uffff\4\66\1"+
            "\uffff\7\66\1\uffff\3\66\1\uffff\1\66\1\uffff\1\66\2\uffff\2"+
            "\66\1\uffff\15\66\1\uffff\3\66\2\uffff\5\66\1\uffff\1\66\1\uffff"+
            "\6\66\2\uffff\12\66\2\uffff\2\66\42\uffff\72\66\4\uffff\12\66"+
            "\1\uffff\42\66\6\uffff\33\66\4\uffff\10\66\1\uffff\44\66\1\uffff"+
            "\17\66\2\uffff\1\66\60\uffff\42\66\1\uffff\5\66\1\uffff\2\66"+
            "\1\uffff\7\66\3\uffff\4\66\6\uffff\32\66\106\uffff\46\66\12"+
            "\uffff\51\66\2\uffff\1\66\4\uffff\132\66\5\uffff\104\66\5\uffff"+
            "\122\66\6\uffff\7\66\1\uffff\77\66\1\uffff\1\66\1\uffff\4\66"+
            "\2\uffff\7\66\1\uffff\1\66\1\uffff\4\66\2\uffff\47\66\1\uffff"+
            "\1\66\1\uffff\4\66\2\uffff\37\66\1\uffff\1\66\1\uffff\4\66\2"+
            "\uffff\7\66\1\uffff\1\66\1\uffff\4\66\2\uffff\7\66\1\uffff\7"+
            "\66\1\uffff\27\66\1\uffff\37\66\1\uffff\1\66\1\uffff\4\66\2"+
            "\uffff\7\66\1\uffff\47\66\1\uffff\23\66\6\uffff\34\66\43\uffff"+
            "\125\66\14\uffff\u0276\66\12\uffff\32\66\5\uffff\121\66\17\uffff"+
            "\15\66\1\uffff\7\66\13\uffff\27\66\11\uffff\24\66\14\uffff\15"+
            "\66\1\uffff\3\66\1\uffff\2\66\14\uffff\64\66\2\uffff\50\66\2"+
            "\uffff\12\66\6\uffff\12\66\6\uffff\16\66\2\uffff\12\66\6\uffff"+
            "\130\66\10\uffff\52\66\126\uffff\35\66\3\uffff\14\66\4\uffff"+
            "\14\66\4\uffff\1\66\3\uffff\52\66\2\uffff\5\66\153\uffff\40"+
            "\66\u0300\uffff\154\66\u0094\uffff\u009c\66\4\uffff\132\66\6"+
            "\uffff\26\66\2\uffff\6\66\2\uffff\46\66\2\uffff\6\66\2\uffff"+
            "\10\66\1\uffff\1\66\1\uffff\1\66\1\uffff\1\66\1\uffff\37\66"+
            "\2\uffff\65\66\1\uffff\7\66\1\uffff\1\66\3\uffff\3\66\1\uffff"+
            "\7\66\3\uffff\4\66\2\uffff\6\66\4\uffff\15\66\5\uffff\3\66\1"+
            "\uffff\7\66\23\uffff\10\66\10\uffff\10\66\10\uffff\11\66\2\uffff"+
            "\12\66\2\uffff\16\66\2\uffff\1\66\30\uffff\2\66\2\uffff\11\66"+
            "\2\uffff\16\66\23\uffff\22\66\36\uffff\33\66\25\uffff\74\66"+
            "\1\uffff\17\66\7\uffff\61\66\14\uffff\u0199\66\2\uffff\u0089"+
            "\66\2\uffff\33\66\57\uffff\47\66\31\uffff\13\66\25\uffff\u01b8"+
            "\66\1\uffff\145\66\2\uffff\22\66\16\uffff\2\66\137\uffff\4\66"+
            "\1\uffff\4\66\2\uffff\34\66\1\uffff\43\66\1\uffff\1\66\1\uffff"+
            "\4\66\3\uffff\1\66\1\uffff\7\66\2\uffff\7\66\16\uffff\37\66"+
            "\3\uffff\30\66\1\uffff\16\66\21\uffff\26\66\12\uffff\u0193\66"+
            "\26\uffff\77\66\4\uffff\40\66\2\uffff\u0110\66\u0372\uffff\32"+
            "\66\1\uffff\131\66\14\uffff\u00d6\66\32\uffff\14\66\5\uffff"+
            "\7\66\12\uffff\2\66\10\uffff\1\66\3\uffff\40\66\1\uffff\126"+
            "\66\2\uffff\2\66\2\uffff\143\66\5\uffff\50\66\4\uffff\136\66"+
            "\1\uffff\50\66\70\uffff\57\66\1\uffff\44\66\14\uffff\56\66\1"+
            "\uffff\u0080\66\1\uffff\u1ab6\66\12\uffff\u51e6\66\132\uffff"+
            "\u048d\66\3\uffff\67\66\u0739\uffff\u2ba4\66\u215c\uffff\u012e"+
            "\66\2\uffff\73\66\u0095\uffff\7\66\14\uffff\5\66\5\uffff\32"+
            "\66\1\uffff\5\66\1\uffff\1\66\1\uffff\2\66\1\uffff\2\66\1\uffff"+
            "\154\66\41\uffff\u016b\66\22\uffff\100\66\2\uffff\66\66\50\uffff"+
            "\16\66\2\uffff\20\66\20\uffff\4\66\14\uffff\5\66\20\uffff\2"+
            "\66\2\uffff\12\66\1\uffff\5\66\6\uffff\10\66\1\uffff\4\66\4"+
            "\uffff\5\66\1\uffff\u0087\66\4\uffff\7\66\2\uffff\61\66\1\uffff"+
            "\1\66\2\uffff\1\66\1\uffff\32\66\1\uffff\1\66\1\uffff\1\66\2"+
            "\uffff\1\66\2\uffff\133\66\3\uffff\6\66\2\uffff\6\66\2\uffff"+
            "\6\66\2\uffff\3\66\3\uffff\3\66\1\uffff\3\66\1\uffff\7\66",
            "\2\66\1\63\1\62\3\66\2\uffff\1\67\1\66\1\uffff\1\66\1\uffff"+
            "\1\66\12\60\1\uffff\1\66\1\uffff\1\66\1\uffff\1\70\1\66\16\57"+
            "\1\74\13\57\1\uffff\1\64\2\uffff\1\61\1\uffff\16\56\1\73\13"+
            "\56\1\uffff\1\66\44\uffff\7\66\1\uffff\2\66\1\uffff\1\66\1\uffff"+
            "\1\66\1\uffff\4\66\1\uffff\3\66\1\uffff\2\66\1\uffff\u017b\66"+
            "\31\uffff\162\66\4\uffff\14\66\16\uffff\5\66\11\uffff\1\66\21"+
            "\uffff\130\66\5\uffff\23\66\12\uffff\1\66\3\uffff\1\66\7\uffff"+
            "\5\66\1\uffff\1\66\1\uffff\24\66\1\uffff\54\66\1\uffff\54\66"+
            "\4\uffff\u0087\66\1\uffff\107\66\1\uffff\46\66\2\uffff\2\66"+
            "\6\uffff\20\66\41\uffff\46\66\2\uffff\7\66\1\uffff\47\66\1\uffff"+
            "\2\66\6\uffff\21\66\1\uffff\27\66\1\uffff\12\66\13\uffff\33"+
            "\66\5\uffff\5\66\27\uffff\12\66\5\uffff\1\66\3\uffff\1\66\1"+
            "\uffff\32\66\5\uffff\31\66\7\uffff\175\66\1\uffff\60\66\2\uffff"+
            "\73\66\2\uffff\3\66\60\uffff\62\66\u014f\uffff\71\66\2\uffff"+
            "\22\66\2\uffff\5\66\3\uffff\31\66\20\uffff\3\66\1\uffff\10\66"+
            "\2\uffff\2\66\2\uffff\26\66\1\uffff\7\66\1\uffff\1\66\3\uffff"+
            "\4\66\2\uffff\11\66\2\uffff\2\66\2\uffff\3\66\11\uffff\1\66"+
            "\4\uffff\2\66\1\uffff\5\66\2\uffff\25\66\6\uffff\3\66\1\uffff"+
            "\6\66\4\uffff\2\66\2\uffff\26\66\1\uffff\7\66\1\uffff\2\66\1"+
            "\uffff\2\66\1\uffff\2\66\2\uffff\1\66\1\uffff\5\66\4\uffff\2"+
            "\66\2\uffff\3\66\13\uffff\4\66\1\uffff\1\66\7\uffff\17\66\14"+
            "\uffff\3\66\1\uffff\11\66\1\uffff\3\66\1\uffff\26\66\1\uffff"+
            "\7\66\1\uffff\2\66\1\uffff\5\66\2\uffff\12\66\1\uffff\3\66\1"+
            "\uffff\3\66\2\uffff\1\66\17\uffff\4\66\2\uffff\12\66\1\uffff"+
            "\1\66\17\uffff\3\66\1\uffff\10\66\2\uffff\2\66\2\uffff\26\66"+
            "\1\uffff\7\66\1\uffff\2\66\1\uffff\5\66\2\uffff\10\66\3\uffff"+
            "\2\66\2\uffff\3\66\10\uffff\2\66\4\uffff\2\66\1\uffff\3\66\4"+
            "\uffff\14\66\20\uffff\2\66\1\uffff\6\66\3\uffff\3\66\1\uffff"+
            "\4\66\3\uffff\2\66\1\uffff\1\66\1\uffff\2\66\3\uffff\2\66\3"+
            "\uffff\3\66\3\uffff\10\66\1\uffff\3\66\4\uffff\5\66\3\uffff"+
            "\3\66\1\uffff\4\66\11\uffff\1\66\17\uffff\24\66\6\uffff\3\66"+
            "\1\uffff\10\66\1\uffff\3\66\1\uffff\27\66\1\uffff\12\66\1\uffff"+
            "\5\66\4\uffff\7\66\1\uffff\3\66\1\uffff\4\66\7\uffff\2\66\11"+
            "\uffff\2\66\4\uffff\12\66\22\uffff\2\66\1\uffff\10\66\1\uffff"+
            "\3\66\1\uffff\27\66\1\uffff\12\66\1\uffff\5\66\2\uffff\11\66"+
            "\1\uffff\3\66\1\uffff\4\66\7\uffff\2\66\7\uffff\1\66\1\uffff"+
            "\2\66\4\uffff\12\66\22\uffff\2\66\1\uffff\10\66\1\uffff\3\66"+
            "\1\uffff\27\66\1\uffff\20\66\4\uffff\6\66\2\uffff\3\66\1\uffff"+
            "\4\66\11\uffff\1\66\10\uffff\2\66\4\uffff\12\66\22\uffff\2\66"+
            "\1\uffff\22\66\3\uffff\30\66\1\uffff\11\66\1\uffff\1\66\2\uffff"+
            "\7\66\3\uffff\1\66\4\uffff\6\66\1\uffff\1\66\1\uffff\10\66\22"+
            "\uffff\3\66\14\uffff\72\66\4\uffff\35\66\45\uffff\2\66\1\uffff"+
            "\1\66\2\uffff\2\66\1\uffff\1\66\2\uffff\1\66\6\uffff\4\66\1"+
            "\uffff\7\66\1\uffff\3\66\1\uffff\1\66\1\uffff\1\66\2\uffff\2"+
            "\66\1\uffff\15\66\1\uffff\3\66\2\uffff\5\66\1\uffff\1\66\1\uffff"+
            "\6\66\2\uffff\12\66\2\uffff\2\66\42\uffff\72\66\4\uffff\12\66"+
            "\1\uffff\42\66\6\uffff\33\66\4\uffff\10\66\1\uffff\44\66\1\uffff"+
            "\17\66\2\uffff\1\66\60\uffff\42\66\1\uffff\5\66\1\uffff\2\66"+
            "\1\uffff\7\66\3\uffff\4\66\6\uffff\32\66\106\uffff\46\66\12"+
            "\uffff\51\66\2\uffff\1\66\4\uffff\132\66\5\uffff\104\66\5\uffff"+
            "\122\66\6\uffff\7\66\1\uffff\77\66\1\uffff\1\66\1\uffff\4\66"+
            "\2\uffff\7\66\1\uffff\1\66\1\uffff\4\66\2\uffff\47\66\1\uffff"+
            "\1\66\1\uffff\4\66\2\uffff\37\66\1\uffff\1\66\1\uffff\4\66\2"+
            "\uffff\7\66\1\uffff\1\66\1\uffff\4\66\2\uffff\7\66\1\uffff\7"+
            "\66\1\uffff\27\66\1\uffff\37\66\1\uffff\1\66\1\uffff\4\66\2"+
            "\uffff\7\66\1\uffff\47\66\1\uffff\23\66\6\uffff\34\66\43\uffff"+
            "\125\66\14\uffff\u0276\66\12\uffff\32\66\5\uffff\121\66\17\uffff"+
            "\15\66\1\uffff\7\66\13\uffff\27\66\11\uffff\24\66\14\uffff\15"+
            "\66\1\uffff\3\66\1\uffff\2\66\14\uffff\64\66\2\uffff\50\66\2"+
            "\uffff\12\66\6\uffff\12\66\6\uffff\16\66\2\uffff\12\66\6\uffff"+
            "\130\66\10\uffff\52\66\126\uffff\35\66\3\uffff\14\66\4\uffff"+
            "\14\66\4\uffff\1\66\3\uffff\52\66\2\uffff\5\66\153\uffff\40"+
            "\66\u0300\uffff\154\66\u0094\uffff\u009c\66\4\uffff\132\66\6"+
            "\uffff\26\66\2\uffff\6\66\2\uffff\46\66\2\uffff\6\66\2\uffff"+
            "\10\66\1\uffff\1\66\1\uffff\1\66\1\uffff\1\66\1\uffff\37\66"+
            "\2\uffff\65\66\1\uffff\7\66\1\uffff\1\66\3\uffff\3\66\1\uffff"+
            "\7\66\3\uffff\4\66\2\uffff\6\66\4\uffff\15\66\5\uffff\3\66\1"+
            "\uffff\7\66\23\uffff\10\66\10\uffff\10\66\10\uffff\11\66\2\uffff"+
            "\12\66\2\uffff\16\66\2\uffff\1\66\30\uffff\2\66\2\uffff\11\66"+
            "\2\uffff\16\66\23\uffff\22\66\36\uffff\33\66\25\uffff\74\66"+
            "\1\uffff\17\66\7\uffff\61\66\14\uffff\u0199\66\2\uffff\u0089"+
            "\66\2\uffff\33\66\57\uffff\47\66\31\uffff\13\66\25\uffff\u01b8"+
            "\66\1\uffff\145\66\2\uffff\22\66\16\uffff\2\66\137\uffff\4\66"+
            "\1\uffff\4\66\2\uffff\34\66\1\uffff\43\66\1\uffff\1\66\1\uffff"+
            "\4\66\3\uffff\1\66\1\uffff\7\66\2\uffff\7\66\16\uffff\37\66"+
            "\3\uffff\30\66\1\uffff\16\66\21\uffff\26\66\12\uffff\u0193\66"+
            "\26\uffff\77\66\4\uffff\40\66\2\uffff\u0110\66\u0372\uffff\32"+
            "\66\1\uffff\131\66\14\uffff\u00d6\66\32\uffff\14\66\5\uffff"+
            "\7\66\12\uffff\2\66\10\uffff\1\66\3\uffff\40\66\1\uffff\126"+
            "\66\2\uffff\2\66\2\uffff\143\66\5\uffff\50\66\4\uffff\136\66"+
            "\1\uffff\50\66\70\uffff\57\66\1\uffff\44\66\14\uffff\56\66\1"+
            "\uffff\u0080\66\1\uffff\u1ab6\66\12\uffff\u51e6\66\132\uffff"+
            "\u048d\66\3\uffff\67\66\u0739\uffff\u2ba4\66\u215c\uffff\u012e"+
            "\66\2\uffff\73\66\u0095\uffff\7\66\14\uffff\5\66\5\uffff\32"+
            "\66\1\uffff\5\66\1\uffff\1\66\1\uffff\2\66\1\uffff\2\66\1\uffff"+
            "\154\66\41\uffff\u016b\66\22\uffff\100\66\2\uffff\66\66\50\uffff"+
            "\16\66\2\uffff\20\66\20\uffff\4\66\14\uffff\5\66\20\uffff\2"+
            "\66\2\uffff\12\66\1\uffff\5\66\6\uffff\10\66\1\uffff\4\66\4"+
            "\uffff\5\66\1\uffff\u0087\66\4\uffff\7\66\2\uffff\61\66\1\uffff"+
            "\1\66\2\uffff\1\66\1\uffff\32\66\1\uffff\1\66\1\uffff\1\66\2"+
            "\uffff\1\66\2\uffff\133\66\3\uffff\6\66\2\uffff\6\66\2\uffff"+
            "\6\66\2\uffff\3\66\3\uffff\3\66\1\uffff\3\66\1\uffff\7\66",
            "",
            "",
            "",
            "\1\40\1\uffff\12\76",
            "\1\40\1\uffff\12\76",
            "",
            "\7\102\2\uffff\1\103\1\102\1\uffff\1\102\1\uffff\13\102\1\uffff"+
            "\1\102\1\uffff\1\102\1\uffff\1\70\33\102\1\uffff\1\101\2\uffff"+
            "\1\102\1\uffff\32\102\1\uffff\1\102\44\uffff\7\102\1\uffff\2"+
            "\102\1\uffff\1\102\1\uffff\1\102\1\uffff\4\102\1\uffff\3\102"+
            "\1\uffff\2\102\1\uffff\u017b\102\31\uffff\162\102\4\uffff\14"+
            "\102\16\uffff\5\102\11\uffff\1\102\21\uffff\130\102\5\uffff"+
            "\23\102\12\uffff\1\102\3\uffff\1\102\7\uffff\5\102\1\uffff\1"+
            "\102\1\uffff\24\102\1\uffff\54\102\1\uffff\54\102\4\uffff\u0087"+
            "\102\1\uffff\107\102\1\uffff\46\102\2\uffff\2\102\6\uffff\20"+
            "\102\41\uffff\46\102\2\uffff\7\102\1\uffff\47\102\1\uffff\2"+
            "\102\6\uffff\21\102\1\uffff\27\102\1\uffff\12\102\13\uffff\33"+
            "\102\5\uffff\5\102\27\uffff\12\102\5\uffff\1\102\3\uffff\1\102"+
            "\1\uffff\32\102\5\uffff\31\102\7\uffff\175\102\1\uffff\60\102"+
            "\2\uffff\73\102\2\uffff\3\102\60\uffff\62\102\u014f\uffff\71"+
            "\102\2\uffff\22\102\2\uffff\5\102\3\uffff\31\102\20\uffff\3"+
            "\102\1\uffff\10\102\2\uffff\2\102\2\uffff\26\102\1\uffff\7\102"+
            "\1\uffff\1\102\3\uffff\4\102\2\uffff\11\102\2\uffff\2\102\2"+
            "\uffff\3\102\11\uffff\1\102\4\uffff\2\102\1\uffff\5\102\2\uffff"+
            "\25\102\6\uffff\3\102\1\uffff\6\102\4\uffff\2\102\2\uffff\26"+
            "\102\1\uffff\7\102\1\uffff\2\102\1\uffff\2\102\1\uffff\2\102"+
            "\2\uffff\1\102\1\uffff\5\102\4\uffff\2\102\2\uffff\3\102\13"+
            "\uffff\4\102\1\uffff\1\102\7\uffff\17\102\14\uffff\3\102\1\uffff"+
            "\11\102\1\uffff\3\102\1\uffff\26\102\1\uffff\7\102\1\uffff\2"+
            "\102\1\uffff\5\102\2\uffff\12\102\1\uffff\3\102\1\uffff\3\102"+
            "\2\uffff\1\102\17\uffff\4\102\2\uffff\12\102\1\uffff\1\102\17"+
            "\uffff\3\102\1\uffff\10\102\2\uffff\2\102\2\uffff\26\102\1\uffff"+
            "\7\102\1\uffff\2\102\1\uffff\5\102\2\uffff\10\102\3\uffff\2"+
            "\102\2\uffff\3\102\10\uffff\2\102\4\uffff\2\102\1\uffff\3\102"+
            "\4\uffff\14\102\20\uffff\2\102\1\uffff\6\102\3\uffff\3\102\1"+
            "\uffff\4\102\3\uffff\2\102\1\uffff\1\102\1\uffff\2\102\3\uffff"+
            "\2\102\3\uffff\3\102\3\uffff\10\102\1\uffff\3\102\4\uffff\5"+
            "\102\3\uffff\3\102\1\uffff\4\102\11\uffff\1\102\17\uffff\24"+
            "\102\6\uffff\3\102\1\uffff\10\102\1\uffff\3\102\1\uffff\27\102"+
            "\1\uffff\12\102\1\uffff\5\102\4\uffff\7\102\1\uffff\3\102\1"+
            "\uffff\4\102\7\uffff\2\102\11\uffff\2\102\4\uffff\12\102\22"+
            "\uffff\2\102\1\uffff\10\102\1\uffff\3\102\1\uffff\27\102\1\uffff"+
            "\12\102\1\uffff\5\102\2\uffff\11\102\1\uffff\3\102\1\uffff\4"+
            "\102\7\uffff\2\102\7\uffff\1\102\1\uffff\2\102\4\uffff\12\102"+
            "\22\uffff\2\102\1\uffff\10\102\1\uffff\3\102\1\uffff\27\102"+
            "\1\uffff\20\102\4\uffff\6\102\2\uffff\3\102\1\uffff\4\102\11"+
            "\uffff\1\102\10\uffff\2\102\4\uffff\12\102\22\uffff\2\102\1"+
            "\uffff\22\102\3\uffff\30\102\1\uffff\11\102\1\uffff\1\102\2"+
            "\uffff\7\102\3\uffff\1\102\4\uffff\6\102\1\uffff\1\102\1\uffff"+
            "\10\102\22\uffff\3\102\14\uffff\72\102\4\uffff\35\102\45\uffff"+
            "\2\102\1\uffff\1\102\2\uffff\2\102\1\uffff\1\102\2\uffff\1\102"+
            "\6\uffff\4\102\1\uffff\7\102\1\uffff\3\102\1\uffff\1\102\1\uffff"+
            "\1\102\2\uffff\2\102\1\uffff\15\102\1\uffff\3\102\2\uffff\5"+
            "\102\1\uffff\1\102\1\uffff\6\102\2\uffff\12\102\2\uffff\2\102"+
            "\42\uffff\72\102\4\uffff\12\102\1\uffff\42\102\6\uffff\33\102"+
            "\4\uffff\10\102\1\uffff\44\102\1\uffff\17\102\2\uffff\1\102"+
            "\60\uffff\42\102\1\uffff\5\102\1\uffff\2\102\1\uffff\7\102\3"+
            "\uffff\4\102\6\uffff\32\102\106\uffff\46\102\12\uffff\51\102"+
            "\2\uffff\1\102\4\uffff\132\102\5\uffff\104\102\5\uffff\122\102"+
            "\6\uffff\7\102\1\uffff\77\102\1\uffff\1\102\1\uffff\4\102\2"+
            "\uffff\7\102\1\uffff\1\102\1\uffff\4\102\2\uffff\47\102\1\uffff"+
            "\1\102\1\uffff\4\102\2\uffff\37\102\1\uffff\1\102\1\uffff\4"+
            "\102\2\uffff\7\102\1\uffff\1\102\1\uffff\4\102\2\uffff\7\102"+
            "\1\uffff\7\102\1\uffff\27\102\1\uffff\37\102\1\uffff\1\102\1"+
            "\uffff\4\102\2\uffff\7\102\1\uffff\47\102\1\uffff\23\102\6\uffff"+
            "\34\102\43\uffff\125\102\14\uffff\u0276\102\12\uffff\32\102"+
            "\5\uffff\121\102\17\uffff\15\102\1\uffff\7\102\13\uffff\27\102"+
            "\11\uffff\24\102\14\uffff\15\102\1\uffff\3\102\1\uffff\2\102"+
            "\14\uffff\64\102\2\uffff\50\102\2\uffff\12\102\6\uffff\12\102"+
            "\6\uffff\16\102\2\uffff\12\102\6\uffff\130\102\10\uffff\52\102"+
            "\126\uffff\35\102\3\uffff\14\102\4\uffff\14\102\4\uffff\1\102"+
            "\3\uffff\52\102\2\uffff\5\102\153\uffff\40\102\u0300\uffff\154"+
            "\102\u0094\uffff\u009c\102\4\uffff\132\102\6\uffff\26\102\2"+
            "\uffff\6\102\2\uffff\46\102\2\uffff\6\102\2\uffff\10\102\1\uffff"+
            "\1\102\1\uffff\1\102\1\uffff\1\102\1\uffff\37\102\2\uffff\65"+
            "\102\1\uffff\7\102\1\uffff\1\102\3\uffff\3\102\1\uffff\7\102"+
            "\3\uffff\4\102\2\uffff\6\102\4\uffff\15\102\5\uffff\3\102\1"+
            "\uffff\7\102\23\uffff\10\102\10\uffff\10\102\10\uffff\11\102"+
            "\2\uffff\12\102\2\uffff\16\102\2\uffff\1\102\30\uffff\2\102"+
            "\2\uffff\11\102\2\uffff\16\102\23\uffff\22\102\36\uffff\33\102"+
            "\25\uffff\74\102\1\uffff\17\102\7\uffff\61\102\14\uffff\u0199"+
            "\102\2\uffff\u0089\102\2\uffff\33\102\57\uffff\47\102\31\uffff"+
            "\13\102\25\uffff\u01b8\102\1\uffff\145\102\2\uffff\22\102\16"+
            "\uffff\2\102\137\uffff\4\102\1\uffff\4\102\2\uffff\34\102\1"+
            "\uffff\43\102\1\uffff\1\102\1\uffff\4\102\3\uffff\1\102\1\uffff"+
            "\7\102\2\uffff\7\102\16\uffff\37\102\3\uffff\30\102\1\uffff"+
            "\16\102\21\uffff\26\102\12\uffff\u0193\102\26\uffff\77\102\4"+
            "\uffff\40\102\2\uffff\u0110\102\u0372\uffff\32\102\1\uffff\131"+
            "\102\14\uffff\u00d6\102\32\uffff\14\102\5\uffff\7\102\12\uffff"+
            "\2\102\10\uffff\1\102\3\uffff\40\102\1\uffff\126\102\2\uffff"+
            "\2\102\2\uffff\143\102\5\uffff\50\102\4\uffff\136\102\1\uffff"+
            "\50\102\70\uffff\57\102\1\uffff\44\102\14\uffff\56\102\1\uffff"+
            "\u0080\102\1\uffff\u1ab6\102\12\uffff\u51e6\102\132\uffff\u048d"+
            "\102\3\uffff\67\102\u0739\uffff\u2ba4\102\u215c\uffff\u012e"+
            "\102\2\uffff\73\102\u0095\uffff\7\102\14\uffff\5\102\5\uffff"+
            "\32\102\1\uffff\5\102\1\uffff\1\102\1\uffff\2\102\1\uffff\2"+
            "\102\1\uffff\154\102\41\uffff\u016b\102\22\uffff\100\102\2\uffff"+
            "\66\102\50\uffff\16\102\2\uffff\20\102\20\uffff\4\102\14\uffff"+
            "\5\102\20\uffff\2\102\2\uffff\12\102\1\uffff\5\102\6\uffff\10"+
            "\102\1\uffff\4\102\4\uffff\5\102\1\uffff\u0087\102\4\uffff\7"+
            "\102\2\uffff\61\102\1\uffff\1\102\2\uffff\1\102\1\uffff\32\102"+
            "\1\uffff\1\102\1\uffff\1\102\2\uffff\1\102\2\uffff\133\102\3"+
            "\uffff\6\102\2\uffff\6\102\2\uffff\6\102\2\uffff\3\102\3\uffff"+
            "\3\102\1\uffff\3\102\1\uffff\7\102",
            "",
            "",
            "",
            "",
            "\7\102\2\uffff\1\103\1\102\1\uffff\1\102\1\uffff\13\102\1\uffff"+
            "\1\102\1\uffff\1\102\1\uffff\1\70\33\102\1\uffff\1\101\2\uffff"+
            "\1\102\1\uffff\32\102\1\uffff\1\102\44\uffff\7\102\1\uffff\2"+
            "\102\1\uffff\1\102\1\uffff\1\102\1\uffff\4\102\1\uffff\3\102"+
            "\1\uffff\2\102\1\uffff\u017b\102\31\uffff\162\102\4\uffff\14"+
            "\102\16\uffff\5\102\11\uffff\1\102\21\uffff\130\102\5\uffff"+
            "\23\102\12\uffff\1\102\3\uffff\1\102\7\uffff\5\102\1\uffff\1"+
            "\102\1\uffff\24\102\1\uffff\54\102\1\uffff\54\102\4\uffff\u0087"+
            "\102\1\uffff\107\102\1\uffff\46\102\2\uffff\2\102\6\uffff\20"+
            "\102\41\uffff\46\102\2\uffff\7\102\1\uffff\47\102\1\uffff\2"+
            "\102\6\uffff\21\102\1\uffff\27\102\1\uffff\12\102\13\uffff\33"+
            "\102\5\uffff\5\102\27\uffff\12\102\5\uffff\1\102\3\uffff\1\102"+
            "\1\uffff\32\102\5\uffff\31\102\7\uffff\175\102\1\uffff\60\102"+
            "\2\uffff\73\102\2\uffff\3\102\60\uffff\62\102\u014f\uffff\71"+
            "\102\2\uffff\22\102\2\uffff\5\102\3\uffff\31\102\20\uffff\3"+
            "\102\1\uffff\10\102\2\uffff\2\102\2\uffff\26\102\1\uffff\7\102"+
            "\1\uffff\1\102\3\uffff\4\102\2\uffff\11\102\2\uffff\2\102\2"+
            "\uffff\3\102\11\uffff\1\102\4\uffff\2\102\1\uffff\5\102\2\uffff"+
            "\25\102\6\uffff\3\102\1\uffff\6\102\4\uffff\2\102\2\uffff\26"+
            "\102\1\uffff\7\102\1\uffff\2\102\1\uffff\2\102\1\uffff\2\102"+
            "\2\uffff\1\102\1\uffff\5\102\4\uffff\2\102\2\uffff\3\102\13"+
            "\uffff\4\102\1\uffff\1\102\7\uffff\17\102\14\uffff\3\102\1\uffff"+
            "\11\102\1\uffff\3\102\1\uffff\26\102\1\uffff\7\102\1\uffff\2"+
            "\102\1\uffff\5\102\2\uffff\12\102\1\uffff\3\102\1\uffff\3\102"+
            "\2\uffff\1\102\17\uffff\4\102\2\uffff\12\102\1\uffff\1\102\17"+
            "\uffff\3\102\1\uffff\10\102\2\uffff\2\102\2\uffff\26\102\1\uffff"+
            "\7\102\1\uffff\2\102\1\uffff\5\102\2\uffff\10\102\3\uffff\2"+
            "\102\2\uffff\3\102\10\uffff\2\102\4\uffff\2\102\1\uffff\3\102"+
            "\4\uffff\14\102\20\uffff\2\102\1\uffff\6\102\3\uffff\3\102\1"+
            "\uffff\4\102\3\uffff\2\102\1\uffff\1\102\1\uffff\2\102\3\uffff"+
            "\2\102\3\uffff\3\102\3\uffff\10\102\1\uffff\3\102\4\uffff\5"+
            "\102\3\uffff\3\102\1\uffff\4\102\11\uffff\1\102\17\uffff\24"+
            "\102\6\uffff\3\102\1\uffff\10\102\1\uffff\3\102\1\uffff\27\102"+
            "\1\uffff\12\102\1\uffff\5\102\4\uffff\7\102\1\uffff\3\102\1"+
            "\uffff\4\102\7\uffff\2\102\11\uffff\2\102\4\uffff\12\102\22"+
            "\uffff\2\102\1\uffff\10\102\1\uffff\3\102\1\uffff\27\102\1\uffff"+
            "\12\102\1\uffff\5\102\2\uffff\11\102\1\uffff\3\102\1\uffff\4"+
            "\102\7\uffff\2\102\7\uffff\1\102\1\uffff\2\102\4\uffff\12\102"+
            "\22\uffff\2\102\1\uffff\10\102\1\uffff\3\102\1\uffff\27\102"+
            "\1\uffff\20\102\4\uffff\6\102\2\uffff\3\102\1\uffff\4\102\11"+
            "\uffff\1\102\10\uffff\2\102\4\uffff\12\102\22\uffff\2\102\1"+
            "\uffff\22\102\3\uffff\30\102\1\uffff\11\102\1\uffff\1\102\2"+
            "\uffff\7\102\3\uffff\1\102\4\uffff\6\102\1\uffff\1\102\1\uffff"+
            "\10\102\22\uffff\3\102\14\uffff\72\102\4\uffff\35\102\45\uffff"+
            "\2\102\1\uffff\1\102\2\uffff\2\102\1\uffff\1\102\2\uffff\1\102"+
            "\6\uffff\4\102\1\uffff\7\102\1\uffff\3\102\1\uffff\1\102\1\uffff"+
            "\1\102\2\uffff\2\102\1\uffff\15\102\1\uffff\3\102\2\uffff\5"+
            "\102\1\uffff\1\102\1\uffff\6\102\2\uffff\12\102\2\uffff\2\102"+
            "\42\uffff\72\102\4\uffff\12\102\1\uffff\42\102\6\uffff\33\102"+
            "\4\uffff\10\102\1\uffff\44\102\1\uffff\17\102\2\uffff\1\102"+
            "\60\uffff\42\102\1\uffff\5\102\1\uffff\2\102\1\uffff\7\102\3"+
            "\uffff\4\102\6\uffff\32\102\106\uffff\46\102\12\uffff\51\102"+
            "\2\uffff\1\102\4\uffff\132\102\5\uffff\104\102\5\uffff\122\102"+
            "\6\uffff\7\102\1\uffff\77\102\1\uffff\1\102\1\uffff\4\102\2"+
            "\uffff\7\102\1\uffff\1\102\1\uffff\4\102\2\uffff\47\102\1\uffff"+
            "\1\102\1\uffff\4\102\2\uffff\37\102\1\uffff\1\102\1\uffff\4"+
            "\102\2\uffff\7\102\1\uffff\1\102\1\uffff\4\102\2\uffff\7\102"+
            "\1\uffff\7\102\1\uffff\27\102\1\uffff\37\102\1\uffff\1\102\1"+
            "\uffff\4\102\2\uffff\7\102\1\uffff\47\102\1\uffff\23\102\6\uffff"+
            "\34\102\43\uffff\125\102\14\uffff\u0276\102\12\uffff\32\102"+
            "\5\uffff\121\102\17\uffff\15\102\1\uffff\7\102\13\uffff\27\102"+
            "\11\uffff\24\102\14\uffff\15\102\1\uffff\3\102\1\uffff\2\102"+
            "\14\uffff\64\102\2\uffff\50\102\2\uffff\12\102\6\uffff\12\102"+
            "\6\uffff\16\102\2\uffff\12\102\6\uffff\130\102\10\uffff\52\102"+
            "\126\uffff\35\102\3\uffff\14\102\4\uffff\14\102\4\uffff\1\102"+
            "\3\uffff\52\102\2\uffff\5\102\153\uffff\40\102\u0300\uffff\154"+
            "\102\u0094\uffff\u009c\102\4\uffff\132\102\6\uffff\26\102\2"+
            "\uffff\6\102\2\uffff\46\102\2\uffff\6\102\2\uffff\10\102\1\uffff"+
            "\1\102\1\uffff\1\102\1\uffff\1\102\1\uffff\37\102\2\uffff\65"+
            "\102\1\uffff\7\102\1\uffff\1\102\3\uffff\3\102\1\uffff\7\102"+
            "\3\uffff\4\102\2\uffff\6\102\4\uffff\15\102\5\uffff\3\102\1"+
            "\uffff\7\102\23\uffff\10\102\10\uffff\10\102\10\uffff\11\102"+
            "\2\uffff\12\102\2\uffff\16\102\2\uffff\1\102\30\uffff\2\102"+
            "\2\uffff\11\102\2\uffff\16\102\23\uffff\22\102\36\uffff\33\102"+
            "\25\uffff\74\102\1\uffff\17\102\7\uffff\61\102\14\uffff\u0199"+
            "\102\2\uffff\u0089\102\2\uffff\33\102\57\uffff\47\102\31\uffff"+
            "\13\102\25\uffff\u01b8\102\1\uffff\145\102\2\uffff\22\102\16"+
            "\uffff\2\102\137\uffff\4\102\1\uffff\4\102\2\uffff\34\102\1"+
            "\uffff\43\102\1\uffff\1\102\1\uffff\4\102\3\uffff\1\102\1\uffff"+
            "\7\102\2\uffff\7\102\16\uffff\37\102\3\uffff\30\102\1\uffff"+
            "\16\102\21\uffff\26\102\12\uffff\u0193\102\26\uffff\77\102\4"+
            "\uffff\40\102\2\uffff\u0110\102\u0372\uffff\32\102\1\uffff\131"+
            "\102\14\uffff\u00d6\102\32\uffff\14\102\5\uffff\7\102\12\uffff"+
            "\2\102\10\uffff\1\102\3\uffff\40\102\1\uffff\126\102\2\uffff"+
            "\2\102\2\uffff\143\102\5\uffff\50\102\4\uffff\136\102\1\uffff"+
            "\50\102\70\uffff\57\102\1\uffff\44\102\14\uffff\56\102\1\uffff"+
            "\u0080\102\1\uffff\u1ab6\102\12\uffff\u51e6\102\132\uffff\u048d"+
            "\102\3\uffff\67\102\u0739\uffff\u2ba4\102\u215c\uffff\u012e"+
            "\102\2\uffff\73\102\u0095\uffff\7\102\14\uffff\5\102\5\uffff"+
            "\32\102\1\uffff\5\102\1\uffff\1\102\1\uffff\2\102\1\uffff\2"+
            "\102\1\uffff\154\102\41\uffff\u016b\102\22\uffff\100\102\2\uffff"+
            "\66\102\50\uffff\16\102\2\uffff\20\102\20\uffff\4\102\14\uffff"+
            "\5\102\20\uffff\2\102\2\uffff\12\102\1\uffff\5\102\6\uffff\10"+
            "\102\1\uffff\4\102\4\uffff\5\102\1\uffff\u0087\102\4\uffff\7"+
            "\102\2\uffff\61\102\1\uffff\1\102\2\uffff\1\102\1\uffff\32\102"+
            "\1\uffff\1\102\1\uffff\1\102\2\uffff\1\102\2\uffff\133\102\3"+
            "\uffff\6\102\2\uffff\6\102\2\uffff\6\102\2\uffff\3\102\3\uffff"+
            "\3\102\1\uffff\3\102\1\uffff\7\102",
            "",
            "",
            "",
            "\2\66\1\63\1\62\3\66\2\uffff\1\67\1\66\1\uffff\1\66\1\uffff"+
            "\1\66\12\60\1\uffff\1\66\1\uffff\1\66\1\uffff\1\70\1\66\16\57"+
            "\1\106\13\57\1\uffff\1\64\2\uffff\1\61\1\uffff\16\56\1\105\13"+
            "\56\1\uffff\1\66\44\uffff\7\66\1\uffff\2\66\1\uffff\1\66\1\uffff"+
            "\1\66\1\uffff\4\66\1\uffff\3\66\1\uffff\2\66\1\uffff\u017b\66"+
            "\31\uffff\162\66\4\uffff\14\66\16\uffff\5\66\11\uffff\1\66\21"+
            "\uffff\130\66\5\uffff\23\66\12\uffff\1\66\3\uffff\1\66\7\uffff"+
            "\5\66\1\uffff\1\66\1\uffff\24\66\1\uffff\54\66\1\uffff\54\66"+
            "\4\uffff\u0087\66\1\uffff\107\66\1\uffff\46\66\2\uffff\2\66"+
            "\6\uffff\20\66\41\uffff\46\66\2\uffff\7\66\1\uffff\47\66\1\uffff"+
            "\2\66\6\uffff\21\66\1\uffff\27\66\1\uffff\12\66\13\uffff\33"+
            "\66\5\uffff\5\66\27\uffff\12\66\5\uffff\1\66\3\uffff\1\66\1"+
            "\uffff\32\66\5\uffff\31\66\7\uffff\175\66\1\uffff\60\66\2\uffff"+
            "\73\66\2\uffff\3\66\60\uffff\62\66\u014f\uffff\71\66\2\uffff"+
            "\22\66\2\uffff\5\66\3\uffff\31\66\20\uffff\3\66\1\uffff\10\66"+
            "\2\uffff\2\66\2\uffff\26\66\1\uffff\7\66\1\uffff\1\66\3\uffff"+
            "\4\66\2\uffff\11\66\2\uffff\2\66\2\uffff\3\66\11\uffff\1\66"+
            "\4\uffff\2\66\1\uffff\5\66\2\uffff\25\66\6\uffff\3\66\1\uffff"+
            "\6\66\4\uffff\2\66\2\uffff\26\66\1\uffff\7\66\1\uffff\2\66\1"+
            "\uffff\2\66\1\uffff\2\66\2\uffff\1\66\1\uffff\5\66\4\uffff\2"+
            "\66\2\uffff\3\66\13\uffff\4\66\1\uffff\1\66\7\uffff\17\66\14"+
            "\uffff\3\66\1\uffff\11\66\1\uffff\3\66\1\uffff\26\66\1\uffff"+
            "\7\66\1\uffff\2\66\1\uffff\5\66\2\uffff\12\66\1\uffff\3\66\1"+
            "\uffff\3\66\2\uffff\1\66\17\uffff\4\66\2\uffff\12\66\1\uffff"+
            "\1\66\17\uffff\3\66\1\uffff\10\66\2\uffff\2\66\2\uffff\26\66"+
            "\1\uffff\7\66\1\uffff\2\66\1\uffff\5\66\2\uffff\10\66\3\uffff"+
            "\2\66\2\uffff\3\66\10\uffff\2\66\4\uffff\2\66\1\uffff\3\66\4"+
            "\uffff\14\66\20\uffff\2\66\1\uffff\6\66\3\uffff\3\66\1\uffff"+
            "\4\66\3\uffff\2\66\1\uffff\1\66\1\uffff\2\66\3\uffff\2\66\3"+
            "\uffff\3\66\3\uffff\10\66\1\uffff\3\66\4\uffff\5\66\3\uffff"+
            "\3\66\1\uffff\4\66\11\uffff\1\66\17\uffff\24\66\6\uffff\3\66"+
            "\1\uffff\10\66\1\uffff\3\66\1\uffff\27\66\1\uffff\12\66\1\uffff"+
            "\5\66\4\uffff\7\66\1\uffff\3\66\1\uffff\4\66\7\uffff\2\66\11"+
            "\uffff\2\66\4\uffff\12\66\22\uffff\2\66\1\uffff\10\66\1\uffff"+
            "\3\66\1\uffff\27\66\1\uffff\12\66\1\uffff\5\66\2\uffff\11\66"+
            "\1\uffff\3\66\1\uffff\4\66\7\uffff\2\66\7\uffff\1\66\1\uffff"+
            "\2\66\4\uffff\12\66\22\uffff\2\66\1\uffff\10\66\1\uffff\3\66"+
            "\1\uffff\27\66\1\uffff\20\66\4\uffff\6\66\2\uffff\3\66\1\uffff"+
            "\4\66\11\uffff\1\66\10\uffff\2\66\4\uffff\12\66\22\uffff\2\66"+
            "\1\uffff\22\66\3\uffff\30\66\1\uffff\11\66\1\uffff\1\66\2\uffff"+
            "\7\66\3\uffff\1\66\4\uffff\6\66\1\uffff\1\66\1\uffff\10\66\22"+
            "\uffff\3\66\14\uffff\72\66\4\uffff\35\66\45\uffff\2\66\1\uffff"+
            "\1\66\2\uffff\2\66\1\uffff\1\66\2\uffff\1\66\6\uffff\4\66\1"+
            "\uffff\7\66\1\uffff\3\66\1\uffff\1\66\1\uffff\1\66\2\uffff\2"+
            "\66\1\uffff\15\66\1\uffff\3\66\2\uffff\5\66\1\uffff\1\66\1\uffff"+
            "\6\66\2\uffff\12\66\2\uffff\2\66\42\uffff\72\66\4\uffff\12\66"+
            "\1\uffff\42\66\6\uffff\33\66\4\uffff\10\66\1\uffff\44\66\1\uffff"+
            "\17\66\2\uffff\1\66\60\uffff\42\66\1\uffff\5\66\1\uffff\2\66"+
            "\1\uffff\7\66\3\uffff\4\66\6\uffff\32\66\106\uffff\46\66\12"+
            "\uffff\51\66\2\uffff\1\66\4\uffff\132\66\5\uffff\104\66\5\uffff"+
            "\122\66\6\uffff\7\66\1\uffff\77\66\1\uffff\1\66\1\uffff\4\66"+
            "\2\uffff\7\66\1\uffff\1\66\1\uffff\4\66\2\uffff\47\66\1\uffff"+
            "\1\66\1\uffff\4\66\2\uffff\37\66\1\uffff\1\66\1\uffff\4\66\2"+
            "\uffff\7\66\1\uffff\1\66\1\uffff\4\66\2\uffff\7\66\1\uffff\7"+
            "\66\1\uffff\27\66\1\uffff\37\66\1\uffff\1\66\1\uffff\4\66\2"+
            "\uffff\7\66\1\uffff\47\66\1\uffff\23\66\6\uffff\34\66\43\uffff"+
            "\125\66\14\uffff\u0276\66\12\uffff\32\66\5\uffff\121\66\17\uffff"+
            "\15\66\1\uffff\7\66\13\uffff\27\66\11\uffff\24\66\14\uffff\15"+
            "\66\1\uffff\3\66\1\uffff\2\66\14\uffff\64\66\2\uffff\50\66\2"+
            "\uffff\12\66\6\uffff\12\66\6\uffff\16\66\2\uffff\12\66\6\uffff"+
            "\130\66\10\uffff\52\66\126\uffff\35\66\3\uffff\14\66\4\uffff"+
            "\14\66\4\uffff\1\66\3\uffff\52\66\2\uffff\5\66\153\uffff\40"+
            "\66\u0300\uffff\154\66\u0094\uffff\u009c\66\4\uffff\132\66\6"+
            "\uffff\26\66\2\uffff\6\66\2\uffff\46\66\2\uffff\6\66\2\uffff"+
            "\10\66\1\uffff\1\66\1\uffff\1\66\1\uffff\1\66\1\uffff\37\66"+
            "\2\uffff\65\66\1\uffff\7\66\1\uffff\1\66\3\uffff\3\66\1\uffff"+
            "\7\66\3\uffff\4\66\2\uffff\6\66\4\uffff\15\66\5\uffff\3\66\1"+
            "\uffff\7\66\23\uffff\10\66\10\uffff\10\66\10\uffff\11\66\2\uffff"+
            "\12\66\2\uffff\16\66\2\uffff\1\66\30\uffff\2\66\2\uffff\11\66"+
            "\2\uffff\16\66\23\uffff\22\66\36\uffff\33\66\25\uffff\74\66"+
            "\1\uffff\17\66\7\uffff\61\66\14\uffff\u0199\66\2\uffff\u0089"+
            "\66\2\uffff\33\66\57\uffff\47\66\31\uffff\13\66\25\uffff\u01b8"+
            "\66\1\uffff\145\66\2\uffff\22\66\16\uffff\2\66\137\uffff\4\66"+
            "\1\uffff\4\66\2\uffff\34\66\1\uffff\43\66\1\uffff\1\66\1\uffff"+
            "\4\66\3\uffff\1\66\1\uffff\7\66\2\uffff\7\66\16\uffff\37\66"+
            "\3\uffff\30\66\1\uffff\16\66\21\uffff\26\66\12\uffff\u0193\66"+
            "\26\uffff\77\66\4\uffff\40\66\2\uffff\u0110\66\u0372\uffff\32"+
            "\66\1\uffff\131\66\14\uffff\u00d6\66\32\uffff\14\66\5\uffff"+
            "\7\66\12\uffff\2\66\10\uffff\1\66\3\uffff\40\66\1\uffff\126"+
            "\66\2\uffff\2\66\2\uffff\143\66\5\uffff\50\66\4\uffff\136\66"+
            "\1\uffff\50\66\70\uffff\57\66\1\uffff\44\66\14\uffff\56\66\1"+
            "\uffff\u0080\66\1\uffff\u1ab6\66\12\uffff\u51e6\66\132\uffff"+
            "\u048d\66\3\uffff\67\66\u0739\uffff\u2ba4\66\u215c\uffff\u012e"+
            "\66\2\uffff\73\66\u0095\uffff\7\66\14\uffff\5\66\5\uffff\32"+
            "\66\1\uffff\5\66\1\uffff\1\66\1\uffff\2\66\1\uffff\2\66\1\uffff"+
            "\154\66\41\uffff\u016b\66\22\uffff\100\66\2\uffff\66\66\50\uffff"+
            "\16\66\2\uffff\20\66\20\uffff\4\66\14\uffff\5\66\20\uffff\2"+
            "\66\2\uffff\12\66\1\uffff\5\66\6\uffff\10\66\1\uffff\4\66\4"+
            "\uffff\5\66\1\uffff\u0087\66\4\uffff\7\66\2\uffff\61\66\1\uffff"+
            "\1\66\2\uffff\1\66\1\uffff\32\66\1\uffff\1\66\1\uffff\1\66\2"+
            "\uffff\1\66\2\uffff\133\66\3\uffff\6\66\2\uffff\6\66\2\uffff"+
            "\6\66\2\uffff\3\66\3\uffff\3\66\1\uffff\3\66\1\uffff\7\66",
            "",
            "",
            "\7\66\2\uffff\1\67\1\66\1\uffff\1\66\1\uffff\13\66\1\uffff"+
            "\1\66\1\uffff\1\66\1\uffff\1\70\33\66\1\uffff\1\110\2\uffff"+
            "\1\66\1\uffff\32\66\1\uffff\1\66\44\uffff\7\66\1\uffff\2\66"+
            "\1\uffff\1\66\1\uffff\1\66\1\uffff\4\66\1\uffff\3\66\1\uffff"+
            "\2\66\1\uffff\u017b\66\31\uffff\162\66\4\uffff\14\66\16\uffff"+
            "\5\66\11\uffff\1\66\21\uffff\130\66\5\uffff\23\66\12\uffff\1"+
            "\66\3\uffff\1\66\7\uffff\5\66\1\uffff\1\66\1\uffff\24\66\1\uffff"+
            "\54\66\1\uffff\54\66\4\uffff\u0087\66\1\uffff\107\66\1\uffff"+
            "\46\66\2\uffff\2\66\6\uffff\20\66\41\uffff\46\66\2\uffff\7\66"+
            "\1\uffff\47\66\1\uffff\2\66\6\uffff\21\66\1\uffff\27\66\1\uffff"+
            "\12\66\13\uffff\33\66\5\uffff\5\66\27\uffff\12\66\5\uffff\1"+
            "\66\3\uffff\1\66\1\uffff\32\66\5\uffff\31\66\7\uffff\175\66"+
            "\1\uffff\60\66\2\uffff\73\66\2\uffff\3\66\60\uffff\62\66\u014f"+
            "\uffff\71\66\2\uffff\22\66\2\uffff\5\66\3\uffff\31\66\20\uffff"+
            "\3\66\1\uffff\10\66\2\uffff\2\66\2\uffff\26\66\1\uffff\7\66"+
            "\1\uffff\1\66\3\uffff\4\66\2\uffff\11\66\2\uffff\2\66\2\uffff"+
            "\3\66\11\uffff\1\66\4\uffff\2\66\1\uffff\5\66\2\uffff\25\66"+
            "\6\uffff\3\66\1\uffff\6\66\4\uffff\2\66\2\uffff\26\66\1\uffff"+
            "\7\66\1\uffff\2\66\1\uffff\2\66\1\uffff\2\66\2\uffff\1\66\1"+
            "\uffff\5\66\4\uffff\2\66\2\uffff\3\66\13\uffff\4\66\1\uffff"+
            "\1\66\7\uffff\17\66\14\uffff\3\66\1\uffff\11\66\1\uffff\3\66"+
            "\1\uffff\26\66\1\uffff\7\66\1\uffff\2\66\1\uffff\5\66\2\uffff"+
            "\12\66\1\uffff\3\66\1\uffff\3\66\2\uffff\1\66\17\uffff\4\66"+
            "\2\uffff\12\66\1\uffff\1\66\17\uffff\3\66\1\uffff\10\66\2\uffff"+
            "\2\66\2\uffff\26\66\1\uffff\7\66\1\uffff\2\66\1\uffff\5\66\2"+
            "\uffff\10\66\3\uffff\2\66\2\uffff\3\66\10\uffff\2\66\4\uffff"+
            "\2\66\1\uffff\3\66\4\uffff\14\66\20\uffff\2\66\1\uffff\6\66"+
            "\3\uffff\3\66\1\uffff\4\66\3\uffff\2\66\1\uffff\1\66\1\uffff"+
            "\2\66\3\uffff\2\66\3\uffff\3\66\3\uffff\10\66\1\uffff\3\66\4"+
            "\uffff\5\66\3\uffff\3\66\1\uffff\4\66\11\uffff\1\66\17\uffff"+
            "\24\66\6\uffff\3\66\1\uffff\10\66\1\uffff\3\66\1\uffff\27\66"+
            "\1\uffff\12\66\1\uffff\5\66\4\uffff\7\66\1\uffff\3\66\1\uffff"+
            "\4\66\7\uffff\2\66\11\uffff\2\66\4\uffff\12\66\22\uffff\2\66"+
            "\1\uffff\10\66\1\uffff\3\66\1\uffff\27\66\1\uffff\12\66\1\uffff"+
            "\5\66\2\uffff\11\66\1\uffff\3\66\1\uffff\4\66\7\uffff\2\66\7"+
            "\uffff\1\66\1\uffff\2\66\4\uffff\12\66\22\uffff\2\66\1\uffff"+
            "\10\66\1\uffff\3\66\1\uffff\27\66\1\uffff\20\66\4\uffff\6\66"+
            "\2\uffff\3\66\1\uffff\4\66\11\uffff\1\66\10\uffff\2\66\4\uffff"+
            "\12\66\22\uffff\2\66\1\uffff\22\66\3\uffff\30\66\1\uffff\11"+
            "\66\1\uffff\1\66\2\uffff\7\66\3\uffff\1\66\4\uffff\6\66\1\uffff"+
            "\1\66\1\uffff\10\66\22\uffff\3\66\14\uffff\72\66\4\uffff\35"+
            "\66\45\uffff\2\66\1\uffff\1\66\2\uffff\2\66\1\uffff\1\66\2\uffff"+
            "\1\66\6\uffff\4\66\1\uffff\7\66\1\uffff\3\66\1\uffff\1\66\1"+
            "\uffff\1\66\2\uffff\2\66\1\uffff\15\66\1\uffff\3\66\2\uffff"+
            "\5\66\1\uffff\1\66\1\uffff\6\66\2\uffff\12\66\2\uffff\2\66\42"+
            "\uffff\72\66\4\uffff\12\66\1\uffff\42\66\6\uffff\33\66\4\uffff"+
            "\10\66\1\uffff\44\66\1\uffff\17\66\2\uffff\1\66\60\uffff\42"+
            "\66\1\uffff\5\66\1\uffff\2\66\1\uffff\7\66\3\uffff\4\66\6\uffff"+
            "\32\66\106\uffff\46\66\12\uffff\51\66\2\uffff\1\66\4\uffff\132"+
            "\66\5\uffff\104\66\5\uffff\122\66\6\uffff\7\66\1\uffff\77\66"+
            "\1\uffff\1\66\1\uffff\4\66\2\uffff\7\66\1\uffff\1\66\1\uffff"+
            "\4\66\2\uffff\47\66\1\uffff\1\66\1\uffff\4\66\2\uffff\37\66"+
            "\1\uffff\1\66\1\uffff\4\66\2\uffff\7\66\1\uffff\1\66\1\uffff"+
            "\4\66\2\uffff\7\66\1\uffff\7\66\1\uffff\27\66\1\uffff\37\66"+
            "\1\uffff\1\66\1\uffff\4\66\2\uffff\7\66\1\uffff\47\66\1\uffff"+
            "\23\66\6\uffff\34\66\43\uffff\125\66\14\uffff\u0276\66\12\uffff"+
            "\32\66\5\uffff\121\66\17\uffff\15\66\1\uffff\7\66\13\uffff\27"+
            "\66\11\uffff\24\66\14\uffff\15\66\1\uffff\3\66\1\uffff\2\66"+
            "\14\uffff\64\66\2\uffff\50\66\2\uffff\12\66\6\uffff\12\66\6"+
            "\uffff\16\66\2\uffff\12\66\6\uffff\130\66\10\uffff\52\66\126"+
            "\uffff\35\66\3\uffff\14\66\4\uffff\14\66\4\uffff\1\66\3\uffff"+
            "\52\66\2\uffff\5\66\153\uffff\40\66\u0300\uffff\154\66\u0094"+
            "\uffff\u009c\66\4\uffff\132\66\6\uffff\26\66\2\uffff\6\66\2"+
            "\uffff\46\66\2\uffff\6\66\2\uffff\10\66\1\uffff\1\66\1\uffff"+
            "\1\66\1\uffff\1\66\1\uffff\37\66\2\uffff\65\66\1\uffff\7\66"+
            "\1\uffff\1\66\3\uffff\3\66\1\uffff\7\66\3\uffff\4\66\2\uffff"+
            "\6\66\4\uffff\15\66\5\uffff\3\66\1\uffff\7\66\23\uffff\10\66"+
            "\10\uffff\10\66\10\uffff\11\66\2\uffff\12\66\2\uffff\16\66\2"+
            "\uffff\1\66\30\uffff\2\66\2\uffff\11\66\2\uffff\16\66\23\uffff"+
            "\22\66\36\uffff\33\66\25\uffff\74\66\1\uffff\17\66\7\uffff\61"+
            "\66\14\uffff\u0199\66\2\uffff\u0089\66\2\uffff\33\66\57\uffff"+
            "\47\66\31\uffff\13\66\25\uffff\u01b8\66\1\uffff\145\66\2\uffff"+
            "\22\66\16\uffff\2\66\137\uffff\4\66\1\uffff\4\66\2\uffff\34"+
            "\66\1\uffff\43\66\1\uffff\1\66\1\uffff\4\66\3\uffff\1\66\1\uffff"+
            "\7\66\2\uffff\7\66\16\uffff\37\66\3\uffff\30\66\1\uffff\16\66"+
            "\21\uffff\26\66\12\uffff\u0193\66\26\uffff\77\66\4\uffff\40"+
            "\66\2\uffff\u0110\66\u0372\uffff\32\66\1\uffff\131\66\14\uffff"+
            "\u00d6\66\32\uffff\14\66\5\uffff\7\66\12\uffff\2\66\10\uffff"+
            "\1\66\3\uffff\40\66\1\uffff\126\66\2\uffff\2\66\2\uffff\143"+
            "\66\5\uffff\50\66\4\uffff\136\66\1\uffff\50\66\70\uffff\57\66"+
            "\1\uffff\44\66\14\uffff\56\66\1\uffff\u0080\66\1\uffff\u1ab6"+
            "\66\12\uffff\u51e6\66\132\uffff\u048d\66\3\uffff\67\66\u0739"+
            "\uffff\u2ba4\66\u215c\uffff\u012e\66\2\uffff\73\66\u0095\uffff"+
            "\7\66\14\uffff\5\66\5\uffff\32\66\1\uffff\5\66\1\uffff\1\66"+
            "\1\uffff\2\66\1\uffff\2\66\1\uffff\154\66\41\uffff\u016b\66"+
            "\22\uffff\100\66\2\uffff\66\66\50\uffff\16\66\2\uffff\20\66"+
            "\20\uffff\4\66\14\uffff\5\66\20\uffff\2\66\2\uffff\12\66\1\uffff"+
            "\5\66\6\uffff\10\66\1\uffff\4\66\4\uffff\5\66\1\uffff\u0087"+
            "\66\4\uffff\7\66\2\uffff\61\66\1\uffff\1\66\2\uffff\1\66\1\uffff"+
            "\32\66\1\uffff\1\66\1\uffff\1\66\2\uffff\1\66\2\uffff\133\66"+
            "\3\uffff\6\66\2\uffff\6\66\2\uffff\6\66\2\uffff\3\66\3\uffff"+
            "\3\66\1\uffff\3\66\1\uffff\7\66",
            "",
            "",
            "",
            "",
            "\2\66\1\63\1\62\3\66\2\uffff\1\67\1\66\1\uffff\1\66\1\uffff"+
            "\1\66\12\60\1\uffff\1\66\1\uffff\1\66\1\uffff\1\70\1\66\32\57"+
            "\1\uffff\1\64\2\uffff\1\61\1\uffff\32\56\1\uffff\1\66\44\uffff"+
            "\7\66\1\uffff\2\66\1\uffff\1\66\1\uffff\1\66\1\uffff\4\66\1"+
            "\uffff\3\66\1\uffff\2\66\1\uffff\u017b\66\31\uffff\162\66\4"+
            "\uffff\14\66\16\uffff\5\66\11\uffff\1\66\21\uffff\130\66\5\uffff"+
            "\23\66\12\uffff\1\66\3\uffff\1\66\7\uffff\5\66\1\uffff\1\66"+
            "\1\uffff\24\66\1\uffff\54\66\1\uffff\54\66\4\uffff\u0087\66"+
            "\1\uffff\107\66\1\uffff\46\66\2\uffff\2\66\6\uffff\20\66\41"+
            "\uffff\46\66\2\uffff\7\66\1\uffff\47\66\1\uffff\2\66\6\uffff"+
            "\21\66\1\uffff\27\66\1\uffff\12\66\13\uffff\33\66\5\uffff\5"+
            "\66\27\uffff\12\66\5\uffff\1\66\3\uffff\1\66\1\uffff\32\66\5"+
            "\uffff\31\66\7\uffff\175\66\1\uffff\60\66\2\uffff\73\66\2\uffff"+
            "\3\66\60\uffff\62\66\u014f\uffff\71\66\2\uffff\22\66\2\uffff"+
            "\5\66\3\uffff\31\66\20\uffff\3\66\1\uffff\10\66\2\uffff\2\66"+
            "\2\uffff\26\66\1\uffff\7\66\1\uffff\1\66\3\uffff\4\66\2\uffff"+
            "\11\66\2\uffff\2\66\2\uffff\3\66\11\uffff\1\66\4\uffff\2\66"+
            "\1\uffff\5\66\2\uffff\25\66\6\uffff\3\66\1\uffff\6\66\4\uffff"+
            "\2\66\2\uffff\26\66\1\uffff\7\66\1\uffff\2\66\1\uffff\2\66\1"+
            "\uffff\2\66\2\uffff\1\66\1\uffff\5\66\4\uffff\2\66\2\uffff\3"+
            "\66\13\uffff\4\66\1\uffff\1\66\7\uffff\17\66\14\uffff\3\66\1"+
            "\uffff\11\66\1\uffff\3\66\1\uffff\26\66\1\uffff\7\66\1\uffff"+
            "\2\66\1\uffff\5\66\2\uffff\12\66\1\uffff\3\66\1\uffff\3\66\2"+
            "\uffff\1\66\17\uffff\4\66\2\uffff\12\66\1\uffff\1\66\17\uffff"+
            "\3\66\1\uffff\10\66\2\uffff\2\66\2\uffff\26\66\1\uffff\7\66"+
            "\1\uffff\2\66\1\uffff\5\66\2\uffff\10\66\3\uffff\2\66\2\uffff"+
            "\3\66\10\uffff\2\66\4\uffff\2\66\1\uffff\3\66\4\uffff\14\66"+
            "\20\uffff\2\66\1\uffff\6\66\3\uffff\3\66\1\uffff\4\66\3\uffff"+
            "\2\66\1\uffff\1\66\1\uffff\2\66\3\uffff\2\66\3\uffff\3\66\3"+
            "\uffff\10\66\1\uffff\3\66\4\uffff\5\66\3\uffff\3\66\1\uffff"+
            "\4\66\11\uffff\1\66\17\uffff\24\66\6\uffff\3\66\1\uffff\10\66"+
            "\1\uffff\3\66\1\uffff\27\66\1\uffff\12\66\1\uffff\5\66\4\uffff"+
            "\7\66\1\uffff\3\66\1\uffff\4\66\7\uffff\2\66\11\uffff\2\66\4"+
            "\uffff\12\66\22\uffff\2\66\1\uffff\10\66\1\uffff\3\66\1\uffff"+
            "\27\66\1\uffff\12\66\1\uffff\5\66\2\uffff\11\66\1\uffff\3\66"+
            "\1\uffff\4\66\7\uffff\2\66\7\uffff\1\66\1\uffff\2\66\4\uffff"+
            "\12\66\22\uffff\2\66\1\uffff\10\66\1\uffff\3\66\1\uffff\27\66"+
            "\1\uffff\20\66\4\uffff\6\66\2\uffff\3\66\1\uffff\4\66\11\uffff"+
            "\1\66\10\uffff\2\66\4\uffff\12\66\22\uffff\2\66\1\uffff\22\66"+
            "\3\uffff\30\66\1\uffff\11\66\1\uffff\1\66\2\uffff\7\66\3\uffff"+
            "\1\66\4\uffff\6\66\1\uffff\1\66\1\uffff\10\66\22\uffff\3\66"+
            "\14\uffff\72\66\4\uffff\35\66\45\uffff\2\66\1\uffff\1\66\2\uffff"+
            "\2\66\1\uffff\1\66\2\uffff\1\66\6\uffff\4\66\1\uffff\7\66\1"+
            "\uffff\3\66\1\uffff\1\66\1\uffff\1\66\2\uffff\2\66\1\uffff\15"+
            "\66\1\uffff\3\66\2\uffff\5\66\1\uffff\1\66\1\uffff\6\66\2\uffff"+
            "\12\66\2\uffff\2\66\42\uffff\72\66\4\uffff\12\66\1\uffff\42"+
            "\66\6\uffff\33\66\4\uffff\10\66\1\uffff\44\66\1\uffff\17\66"+
            "\2\uffff\1\66\60\uffff\42\66\1\uffff\5\66\1\uffff\2\66\1\uffff"+
            "\7\66\3\uffff\4\66\6\uffff\32\66\106\uffff\46\66\12\uffff\51"+
            "\66\2\uffff\1\66\4\uffff\132\66\5\uffff\104\66\5\uffff\122\66"+
            "\6\uffff\7\66\1\uffff\77\66\1\uffff\1\66\1\uffff\4\66\2\uffff"+
            "\7\66\1\uffff\1\66\1\uffff\4\66\2\uffff\47\66\1\uffff\1\66\1"+
            "\uffff\4\66\2\uffff\37\66\1\uffff\1\66\1\uffff\4\66\2\uffff"+
            "\7\66\1\uffff\1\66\1\uffff\4\66\2\uffff\7\66\1\uffff\7\66\1"+
            "\uffff\27\66\1\uffff\37\66\1\uffff\1\66\1\uffff\4\66\2\uffff"+
            "\7\66\1\uffff\47\66\1\uffff\23\66\6\uffff\34\66\43\uffff\125"+
            "\66\14\uffff\u0276\66\12\uffff\32\66\5\uffff\121\66\17\uffff"+
            "\15\66\1\uffff\7\66\13\uffff\27\66\11\uffff\24\66\14\uffff\15"+
            "\66\1\uffff\3\66\1\uffff\2\66\14\uffff\64\66\2\uffff\50\66\2"+
            "\uffff\12\66\6\uffff\12\66\6\uffff\16\66\2\uffff\12\66\6\uffff"+
            "\130\66\10\uffff\52\66\126\uffff\35\66\3\uffff\14\66\4\uffff"+
            "\14\66\4\uffff\1\66\3\uffff\52\66\2\uffff\5\66\153\uffff\40"+
            "\66\u0300\uffff\154\66\u0094\uffff\u009c\66\4\uffff\132\66\6"+
            "\uffff\26\66\2\uffff\6\66\2\uffff\46\66\2\uffff\6\66\2\uffff"+
            "\10\66\1\uffff\1\66\1\uffff\1\66\1\uffff\1\66\1\uffff\37\66"+
            "\2\uffff\65\66\1\uffff\7\66\1\uffff\1\66\3\uffff\3\66\1\uffff"+
            "\7\66\3\uffff\4\66\2\uffff\6\66\4\uffff\15\66\5\uffff\3\66\1"+
            "\uffff\7\66\23\uffff\10\66\10\uffff\10\66\10\uffff\11\66\2\uffff"+
            "\12\66\2\uffff\16\66\2\uffff\1\66\30\uffff\2\66\2\uffff\11\66"+
            "\2\uffff\16\66\23\uffff\22\66\36\uffff\33\66\25\uffff\74\66"+
            "\1\uffff\17\66\7\uffff\61\66\14\uffff\u0199\66\2\uffff\u0089"+
            "\66\2\uffff\33\66\57\uffff\47\66\31\uffff\13\66\25\uffff\u01b8"+
            "\66\1\uffff\145\66\2\uffff\22\66\16\uffff\2\66\137\uffff\4\66"+
            "\1\uffff\4\66\2\uffff\34\66\1\uffff\43\66\1\uffff\1\66\1\uffff"+
            "\4\66\3\uffff\1\66\1\uffff\7\66\2\uffff\7\66\16\uffff\37\66"+
            "\3\uffff\30\66\1\uffff\16\66\21\uffff\26\66\12\uffff\u0193\66"+
            "\26\uffff\77\66\4\uffff\40\66\2\uffff\u0110\66\u0372\uffff\32"+
            "\66\1\uffff\131\66\14\uffff\u00d6\66\32\uffff\14\66\5\uffff"+
            "\7\66\12\uffff\2\66\10\uffff\1\66\3\uffff\40\66\1\uffff\126"+
            "\66\2\uffff\2\66\2\uffff\143\66\5\uffff\50\66\4\uffff\136\66"+
            "\1\uffff\50\66\70\uffff\57\66\1\uffff\44\66\14\uffff\56\66\1"+
            "\uffff\u0080\66\1\uffff\u1ab6\66\12\uffff\u51e6\66\132\uffff"+
            "\u048d\66\3\uffff\67\66\u0739\uffff\u2ba4\66\u215c\uffff\u012e"+
            "\66\2\uffff\73\66\u0095\uffff\7\66\14\uffff\5\66\5\uffff\32"+
            "\66\1\uffff\5\66\1\uffff\1\66\1\uffff\2\66\1\uffff\2\66\1\uffff"+
            "\154\66\41\uffff\u016b\66\22\uffff\100\66\2\uffff\66\66\50\uffff"+
            "\16\66\2\uffff\20\66\20\uffff\4\66\14\uffff\5\66\20\uffff\2"+
            "\66\2\uffff\12\66\1\uffff\5\66\6\uffff\10\66\1\uffff\4\66\4"+
            "\uffff\5\66\1\uffff\u0087\66\4\uffff\7\66\2\uffff\61\66\1\uffff"+
            "\1\66\2\uffff\1\66\1\uffff\32\66\1\uffff\1\66\1\uffff\1\66\2"+
            "\uffff\1\66\2\uffff\133\66\3\uffff\6\66\2\uffff\6\66\2\uffff"+
            "\6\66\2\uffff\3\66\3\uffff\3\66\1\uffff\3\66\1\uffff\7\66",
            "\7\66\2\uffff\1\67\1\66\1\uffff\1\66\1\111\1\66\12\114\1\uffff"+
            "\1\66\1\uffff\1\66\1\uffff\1\70\5\66\1\112\25\66\1\uffff\1\110"+
            "\2\uffff\1\66\1\uffff\4\66\1\112\25\66\1\uffff\1\66\44\uffff"+
            "\7\66\1\uffff\2\66\1\uffff\1\66\1\uffff\1\66\1\uffff\4\66\1"+
            "\uffff\3\66\1\uffff\2\66\1\uffff\u017b\66\31\uffff\162\66\4"+
            "\uffff\14\66\16\uffff\5\66\11\uffff\1\66\21\uffff\130\66\5\uffff"+
            "\23\66\12\uffff\1\66\3\uffff\1\66\7\uffff\5\66\1\uffff\1\66"+
            "\1\uffff\24\66\1\uffff\54\66\1\uffff\54\66\4\uffff\u0087\66"+
            "\1\uffff\107\66\1\uffff\46\66\2\uffff\2\66\6\uffff\20\66\41"+
            "\uffff\46\66\2\uffff\7\66\1\uffff\47\66\1\uffff\2\66\6\uffff"+
            "\21\66\1\uffff\27\66\1\uffff\12\66\13\uffff\33\66\5\uffff\5"+
            "\66\27\uffff\12\66\5\uffff\1\66\3\uffff\1\66\1\uffff\32\66\5"+
            "\uffff\31\66\7\uffff\175\66\1\uffff\60\66\2\uffff\73\66\2\uffff"+
            "\3\66\60\uffff\62\66\u014f\uffff\71\66\2\uffff\22\66\2\uffff"+
            "\5\66\3\uffff\31\66\20\uffff\3\66\1\uffff\10\66\2\uffff\2\66"+
            "\2\uffff\26\66\1\uffff\7\66\1\uffff\1\66\3\uffff\4\66\2\uffff"+
            "\11\66\2\uffff\2\66\2\uffff\3\66\11\uffff\1\66\4\uffff\2\66"+
            "\1\uffff\5\66\2\uffff\25\66\6\uffff\3\66\1\uffff\6\66\4\uffff"+
            "\2\66\2\uffff\26\66\1\uffff\7\66\1\uffff\2\66\1\uffff\2\66\1"+
            "\uffff\2\66\2\uffff\1\66\1\uffff\5\66\4\uffff\2\66\2\uffff\3"+
            "\66\13\uffff\4\66\1\uffff\1\66\7\uffff\17\66\14\uffff\3\66\1"+
            "\uffff\11\66\1\uffff\3\66\1\uffff\26\66\1\uffff\7\66\1\uffff"+
            "\2\66\1\uffff\5\66\2\uffff\12\66\1\uffff\3\66\1\uffff\3\66\2"+
            "\uffff\1\66\17\uffff\4\66\2\uffff\12\66\1\uffff\1\66\17\uffff"+
            "\3\66\1\uffff\10\66\2\uffff\2\66\2\uffff\26\66\1\uffff\7\66"+
            "\1\uffff\2\66\1\uffff\5\66\2\uffff\10\66\3\uffff\2\66\2\uffff"+
            "\3\66\10\uffff\2\66\4\uffff\2\66\1\uffff\3\66\4\uffff\14\66"+
            "\20\uffff\2\66\1\uffff\6\66\3\uffff\3\66\1\uffff\4\66\3\uffff"+
            "\2\66\1\uffff\1\66\1\uffff\2\66\3\uffff\2\66\3\uffff\3\66\3"+
            "\uffff\10\66\1\uffff\3\66\4\uffff\5\66\3\uffff\3\66\1\uffff"+
            "\4\66\11\uffff\1\66\17\uffff\24\66\6\uffff\3\66\1\uffff\10\66"+
            "\1\uffff\3\66\1\uffff\27\66\1\uffff\12\66\1\uffff\5\66\4\uffff"+
            "\7\66\1\uffff\3\66\1\uffff\4\66\7\uffff\2\66\11\uffff\2\66\4"+
            "\uffff\12\66\22\uffff\2\66\1\uffff\10\66\1\uffff\3\66\1\uffff"+
            "\27\66\1\uffff\12\66\1\uffff\5\66\2\uffff\11\66\1\uffff\3\66"+
            "\1\uffff\4\66\7\uffff\2\66\7\uffff\1\66\1\uffff\2\66\4\uffff"+
            "\12\66\22\uffff\2\66\1\uffff\10\66\1\uffff\3\66\1\uffff\27\66"+
            "\1\uffff\20\66\4\uffff\6\66\2\uffff\3\66\1\uffff\4\66\11\uffff"+
            "\1\66\10\uffff\2\66\4\uffff\12\66\22\uffff\2\66\1\uffff\22\66"+
            "\3\uffff\30\66\1\uffff\11\66\1\uffff\1\66\2\uffff\7\66\3\uffff"+
            "\1\66\4\uffff\6\66\1\uffff\1\66\1\uffff\10\66\22\uffff\3\66"+
            "\14\uffff\72\66\4\uffff\35\66\45\uffff\2\66\1\uffff\1\66\2\uffff"+
            "\2\66\1\uffff\1\66\2\uffff\1\66\6\uffff\4\66\1\uffff\7\66\1"+
            "\uffff\3\66\1\uffff\1\66\1\uffff\1\66\2\uffff\2\66\1\uffff\15"+
            "\66\1\uffff\3\66\2\uffff\5\66\1\uffff\1\66\1\uffff\6\66\2\uffff"+
            "\12\66\2\uffff\2\66\42\uffff\72\66\4\uffff\12\66\1\uffff\42"+
            "\66\6\uffff\33\66\4\uffff\10\66\1\uffff\44\66\1\uffff\17\66"+
            "\2\uffff\1\66\60\uffff\42\66\1\uffff\5\66\1\uffff\2\66\1\uffff"+
            "\7\66\3\uffff\4\66\6\uffff\32\66\106\uffff\46\66\12\uffff\51"+
            "\66\2\uffff\1\66\4\uffff\132\66\5\uffff\104\66\5\uffff\122\66"+
            "\6\uffff\7\66\1\uffff\77\66\1\uffff\1\66\1\uffff\4\66\2\uffff"+
            "\7\66\1\uffff\1\66\1\uffff\4\66\2\uffff\47\66\1\uffff\1\66\1"+
            "\uffff\4\66\2\uffff\37\66\1\uffff\1\66\1\uffff\4\66\2\uffff"+
            "\7\66\1\uffff\1\66\1\uffff\4\66\2\uffff\7\66\1\uffff\7\66\1"+
            "\uffff\27\66\1\uffff\37\66\1\uffff\1\66\1\uffff\4\66\2\uffff"+
            "\7\66\1\uffff\47\66\1\uffff\23\66\6\uffff\34\66\43\uffff\125"+
            "\66\14\uffff\u0276\66\12\uffff\32\66\5\uffff\121\66\17\uffff"+
            "\15\66\1\uffff\7\66\13\uffff\27\66\11\uffff\24\66\14\uffff\15"+
            "\66\1\uffff\3\66\1\uffff\2\66\14\uffff\64\66\2\uffff\50\66\2"+
            "\uffff\12\66\6\uffff\12\66\6\uffff\16\66\2\uffff\12\66\6\uffff"+
            "\130\66\10\uffff\52\66\126\uffff\35\66\3\uffff\14\66\4\uffff"+
            "\14\66\4\uffff\1\66\3\uffff\52\66\2\uffff\5\66\153\uffff\40"+
            "\66\u0300\uffff\154\66\u0094\uffff\u009c\66\4\uffff\132\66\6"+
            "\uffff\26\66\2\uffff\6\66\2\uffff\46\66\2\uffff\6\66\2\uffff"+
            "\10\66\1\uffff\1\66\1\uffff\1\66\1\uffff\1\66\1\uffff\37\66"+
            "\2\uffff\65\66\1\uffff\7\66\1\uffff\1\66\3\uffff\3\66\1\uffff"+
            "\7\66\3\uffff\4\66\2\uffff\6\66\4\uffff\15\66\5\uffff\3\66\1"+
            "\uffff\7\66\23\uffff\10\66\10\uffff\10\66\10\uffff\11\66\2\uffff"+
            "\12\66\2\uffff\16\66\2\uffff\1\66\30\uffff\2\66\2\uffff\11\66"+
            "\2\uffff\16\66\23\uffff\22\66\36\uffff\33\66\25\uffff\74\66"+
            "\1\uffff\17\66\7\uffff\61\66\14\uffff\u0199\66\2\uffff\u0089"+
            "\66\2\uffff\33\66\57\uffff\47\66\31\uffff\13\66\25\uffff\u01b8"+
            "\66\1\uffff\145\66\2\uffff\22\66\16\uffff\2\66\137\uffff\4\66"+
            "\1\uffff\4\66\2\uffff\34\66\1\uffff\43\66\1\uffff\1\66\1\uffff"+
            "\4\66\3\uffff\1\66\1\uffff\7\66\2\uffff\7\66\16\uffff\37\66"+
            "\3\uffff\30\66\1\uffff\16\66\21\uffff\26\66\12\uffff\u0193\66"+
            "\26\uffff\77\66\4\uffff\40\66\2\uffff\u0110\66\u0372\uffff\32"+
            "\66\1\uffff\131\66\14\uffff\u00d6\66\32\uffff\14\66\5\uffff"+
            "\7\66\12\uffff\2\66\10\uffff\1\66\3\uffff\40\66\1\uffff\126"+
            "\66\2\uffff\2\66\2\uffff\143\66\5\uffff\50\66\4\uffff\136\66"+
            "\1\uffff\50\66\70\uffff\57\66\1\uffff\44\66\14\uffff\56\66\1"+
            "\uffff\u0080\66\1\uffff\u1ab6\66\12\uffff\u51e6\66\132\uffff"+
            "\u048d\66\3\uffff\67\66\u0739\uffff\u2ba4\66\u215c\uffff\u012e"+
            "\66\2\uffff\73\66\u0095\uffff\7\66\14\uffff\5\66\5\uffff\32"+
            "\66\1\uffff\5\66\1\uffff\1\66\1\uffff\2\66\1\uffff\2\66\1\uffff"+
            "\154\66\41\uffff\u016b\66\22\uffff\100\66\2\uffff\66\66\50\uffff"+
            "\16\66\2\uffff\20\66\20\uffff\4\66\14\uffff\5\66\20\uffff\2"+
            "\66\2\uffff\12\66\1\uffff\5\66\6\uffff\10\66\1\uffff\4\66\4"+
            "\uffff\5\66\1\uffff\u0087\66\4\uffff\7\66\2\uffff\61\66\1\uffff"+
            "\1\66\2\uffff\1\66\1\uffff\32\66\1\uffff\1\66\1\uffff\1\66\2"+
            "\uffff\1\66\2\uffff\133\66\3\uffff\6\66\2\uffff\6\66\2\uffff"+
            "\6\66\2\uffff\3\66\3\uffff\3\66\1\uffff\3\66\1\uffff\7\66",
            "",
            "\165\116\1\115\uff8a\116",
            "",
            "\7\66\2\uffff\1\67\1\66\1\uffff\1\66\1\uffff\13\66\1\uffff"+
            "\1\66\1\uffff\1\66\1\uffff\1\70\33\66\1\uffff\1\110\2\uffff"+
            "\1\66\1\uffff\32\66\1\uffff\1\66\44\uffff\7\66\1\uffff\2\66"+
            "\1\uffff\1\66\1\uffff\1\66\1\uffff\4\66\1\uffff\3\66\1\uffff"+
            "\2\66\1\uffff\u017b\66\31\uffff\162\66\4\uffff\14\66\16\uffff"+
            "\5\66\11\uffff\1\66\21\uffff\130\66\5\uffff\23\66\12\uffff\1"+
            "\66\3\uffff\1\66\7\uffff\5\66\1\uffff\1\66\1\uffff\24\66\1\uffff"+
            "\54\66\1\uffff\54\66\4\uffff\u0087\66\1\uffff\107\66\1\uffff"+
            "\46\66\2\uffff\2\66\6\uffff\20\66\41\uffff\46\66\2\uffff\7\66"+
            "\1\uffff\47\66\1\uffff\2\66\6\uffff\21\66\1\uffff\27\66\1\uffff"+
            "\12\66\13\uffff\33\66\5\uffff\5\66\27\uffff\12\66\5\uffff\1"+
            "\66\3\uffff\1\66\1\uffff\32\66\5\uffff\31\66\7\uffff\175\66"+
            "\1\uffff\60\66\2\uffff\73\66\2\uffff\3\66\60\uffff\62\66\u014f"+
            "\uffff\71\66\2\uffff\22\66\2\uffff\5\66\3\uffff\31\66\20\uffff"+
            "\3\66\1\uffff\10\66\2\uffff\2\66\2\uffff\26\66\1\uffff\7\66"+
            "\1\uffff\1\66\3\uffff\4\66\2\uffff\11\66\2\uffff\2\66\2\uffff"+
            "\3\66\11\uffff\1\66\4\uffff\2\66\1\uffff\5\66\2\uffff\25\66"+
            "\6\uffff\3\66\1\uffff\6\66\4\uffff\2\66\2\uffff\26\66\1\uffff"+
            "\7\66\1\uffff\2\66\1\uffff\2\66\1\uffff\2\66\2\uffff\1\66\1"+
            "\uffff\5\66\4\uffff\2\66\2\uffff\3\66\13\uffff\4\66\1\uffff"+
            "\1\66\7\uffff\17\66\14\uffff\3\66\1\uffff\11\66\1\uffff\3\66"+
            "\1\uffff\26\66\1\uffff\7\66\1\uffff\2\66\1\uffff\5\66\2\uffff"+
            "\12\66\1\uffff\3\66\1\uffff\3\66\2\uffff\1\66\17\uffff\4\66"+
            "\2\uffff\12\66\1\uffff\1\66\17\uffff\3\66\1\uffff\10\66\2\uffff"+
            "\2\66\2\uffff\26\66\1\uffff\7\66\1\uffff\2\66\1\uffff\5\66\2"+
            "\uffff\10\66\3\uffff\2\66\2\uffff\3\66\10\uffff\2\66\4\uffff"+
            "\2\66\1\uffff\3\66\4\uffff\14\66\20\uffff\2\66\1\uffff\6\66"+
            "\3\uffff\3\66\1\uffff\4\66\3\uffff\2\66\1\uffff\1\66\1\uffff"+
            "\2\66\3\uffff\2\66\3\uffff\3\66\3\uffff\10\66\1\uffff\3\66\4"+
            "\uffff\5\66\3\uffff\3\66\1\uffff\4\66\11\uffff\1\66\17\uffff"+
            "\24\66\6\uffff\3\66\1\uffff\10\66\1\uffff\3\66\1\uffff\27\66"+
            "\1\uffff\12\66\1\uffff\5\66\4\uffff\7\66\1\uffff\3\66\1\uffff"+
            "\4\66\7\uffff\2\66\11\uffff\2\66\4\uffff\12\66\22\uffff\2\66"+
            "\1\uffff\10\66\1\uffff\3\66\1\uffff\27\66\1\uffff\12\66\1\uffff"+
            "\5\66\2\uffff\11\66\1\uffff\3\66\1\uffff\4\66\7\uffff\2\66\7"+
            "\uffff\1\66\1\uffff\2\66\4\uffff\12\66\22\uffff\2\66\1\uffff"+
            "\10\66\1\uffff\3\66\1\uffff\27\66\1\uffff\20\66\4\uffff\6\66"+
            "\2\uffff\3\66\1\uffff\4\66\11\uffff\1\66\10\uffff\2\66\4\uffff"+
            "\12\66\22\uffff\2\66\1\uffff\22\66\3\uffff\30\66\1\uffff\11"+
            "\66\1\uffff\1\66\2\uffff\7\66\3\uffff\1\66\4\uffff\6\66\1\uffff"+
            "\1\66\1\uffff\10\66\22\uffff\3\66\14\uffff\72\66\4\uffff\35"+
            "\66\45\uffff\2\66\1\uffff\1\66\2\uffff\2\66\1\uffff\1\66\2\uffff"+
            "\1\66\6\uffff\4\66\1\uffff\7\66\1\uffff\3\66\1\uffff\1\66\1"+
            "\uffff\1\66\2\uffff\2\66\1\uffff\15\66\1\uffff\3\66\2\uffff"+
            "\5\66\1\uffff\1\66\1\uffff\6\66\2\uffff\12\66\2\uffff\2\66\42"+
            "\uffff\72\66\4\uffff\12\66\1\uffff\42\66\6\uffff\33\66\4\uffff"+
            "\10\66\1\uffff\44\66\1\uffff\17\66\2\uffff\1\66\60\uffff\42"+
            "\66\1\uffff\5\66\1\uffff\2\66\1\uffff\7\66\3\uffff\4\66\6\uffff"+
            "\32\66\106\uffff\46\66\12\uffff\51\66\2\uffff\1\66\4\uffff\132"+
            "\66\5\uffff\104\66\5\uffff\122\66\6\uffff\7\66\1\uffff\77\66"+
            "\1\uffff\1\66\1\uffff\4\66\2\uffff\7\66\1\uffff\1\66\1\uffff"+
            "\4\66\2\uffff\47\66\1\uffff\1\66\1\uffff\4\66\2\uffff\37\66"+
            "\1\uffff\1\66\1\uffff\4\66\2\uffff\7\66\1\uffff\1\66\1\uffff"+
            "\4\66\2\uffff\7\66\1\uffff\7\66\1\uffff\27\66\1\uffff\37\66"+
            "\1\uffff\1\66\1\uffff\4\66\2\uffff\7\66\1\uffff\47\66\1\uffff"+
            "\23\66\6\uffff\34\66\43\uffff\125\66\14\uffff\u0276\66\12\uffff"+
            "\32\66\5\uffff\121\66\17\uffff\15\66\1\uffff\7\66\13\uffff\27"+
            "\66\11\uffff\24\66\14\uffff\15\66\1\uffff\3\66\1\uffff\2\66"+
            "\14\uffff\64\66\2\uffff\50\66\2\uffff\12\66\6\uffff\12\66\6"+
            "\uffff\16\66\2\uffff\12\66\6\uffff\130\66\10\uffff\52\66\126"+
            "\uffff\35\66\3\uffff\14\66\4\uffff\14\66\4\uffff\1\66\3\uffff"+
            "\52\66\2\uffff\5\66\153\uffff\40\66\u0300\uffff\154\66\u0094"+
            "\uffff\u009c\66\4\uffff\132\66\6\uffff\26\66\2\uffff\6\66\2"+
            "\uffff\46\66\2\uffff\6\66\2\uffff\10\66\1\uffff\1\66\1\uffff"+
            "\1\66\1\uffff\1\66\1\uffff\37\66\2\uffff\65\66\1\uffff\7\66"+
            "\1\uffff\1\66\3\uffff\3\66\1\uffff\7\66\3\uffff\4\66\2\uffff"+
            "\6\66\4\uffff\15\66\5\uffff\3\66\1\uffff\7\66\23\uffff\10\66"+
            "\10\uffff\10\66\10\uffff\11\66\2\uffff\12\66\2\uffff\16\66\2"+
            "\uffff\1\66\30\uffff\2\66\2\uffff\11\66\2\uffff\16\66\23\uffff"+
            "\22\66\36\uffff\33\66\25\uffff\74\66\1\uffff\17\66\7\uffff\61"+
            "\66\14\uffff\u0199\66\2\uffff\u0089\66\2\uffff\33\66\57\uffff"+
            "\47\66\31\uffff\13\66\25\uffff\u01b8\66\1\uffff\145\66\2\uffff"+
            "\22\66\16\uffff\2\66\137\uffff\4\66\1\uffff\4\66\2\uffff\34"+
            "\66\1\uffff\43\66\1\uffff\1\66\1\uffff\4\66\3\uffff\1\66\1\uffff"+
            "\7\66\2\uffff\7\66\16\uffff\37\66\3\uffff\30\66\1\uffff\16\66"+
            "\21\uffff\26\66\12\uffff\u0193\66\26\uffff\77\66\4\uffff\40"+
            "\66\2\uffff\u0110\66\u0372\uffff\32\66\1\uffff\131\66\14\uffff"+
            "\u00d6\66\32\uffff\14\66\5\uffff\7\66\12\uffff\2\66\10\uffff"+
            "\1\66\3\uffff\40\66\1\uffff\126\66\2\uffff\2\66\2\uffff\143"+
            "\66\5\uffff\50\66\4\uffff\136\66\1\uffff\50\66\70\uffff\57\66"+
            "\1\uffff\44\66\14\uffff\56\66\1\uffff\u0080\66\1\uffff\u1ab6"+
            "\66\12\uffff\u51e6\66\132\uffff\u048d\66\3\uffff\67\66\u0739"+
            "\uffff\u2ba4\66\u215c\uffff\u012e\66\2\uffff\73\66\u0095\uffff"+
            "\7\66\14\uffff\5\66\5\uffff\32\66\1\uffff\5\66\1\uffff\1\66"+
            "\1\uffff\2\66\1\uffff\2\66\1\uffff\154\66\41\uffff\u016b\66"+
            "\22\uffff\100\66\2\uffff\66\66\50\uffff\16\66\2\uffff\20\66"+
            "\20\uffff\4\66\14\uffff\5\66\20\uffff\2\66\2\uffff\12\66\1\uffff"+
            "\5\66\6\uffff\10\66\1\uffff\4\66\4\uffff\5\66\1\uffff\u0087"+
            "\66\4\uffff\7\66\2\uffff\61\66\1\uffff\1\66\2\uffff\1\66\1\uffff"+
            "\32\66\1\uffff\1\66\1\uffff\1\66\2\uffff\1\66\2\uffff\133\66"+
            "\3\uffff\6\66\2\uffff\6\66\2\uffff\6\66\2\uffff\3\66\3\uffff"+
            "\3\66\1\uffff\3\66\1\uffff\7\66",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\2\66\1\63\1\62\3\66\2\uffff\1\67\1\66\1\uffff\1\66\1\uffff"+
            "\1\66\12\60\1\uffff\1\66\1\uffff\1\66\1\uffff\1\70\1\66\32\57"+
            "\1\uffff\1\64\2\uffff\1\61\1\uffff\32\56\1\uffff\1\66\44\uffff"+
            "\7\66\1\uffff\2\66\1\uffff\1\66\1\uffff\1\66\1\uffff\4\66\1"+
            "\uffff\3\66\1\uffff\2\66\1\uffff\u017b\66\31\uffff\162\66\4"+
            "\uffff\14\66\16\uffff\5\66\11\uffff\1\66\21\uffff\130\66\5\uffff"+
            "\23\66\12\uffff\1\66\3\uffff\1\66\7\uffff\5\66\1\uffff\1\66"+
            "\1\uffff\24\66\1\uffff\54\66\1\uffff\54\66\4\uffff\u0087\66"+
            "\1\uffff\107\66\1\uffff\46\66\2\uffff\2\66\6\uffff\20\66\41"+
            "\uffff\46\66\2\uffff\7\66\1\uffff\47\66\1\uffff\2\66\6\uffff"+
            "\21\66\1\uffff\27\66\1\uffff\12\66\13\uffff\33\66\5\uffff\5"+
            "\66\27\uffff\12\66\5\uffff\1\66\3\uffff\1\66\1\uffff\32\66\5"+
            "\uffff\31\66\7\uffff\175\66\1\uffff\60\66\2\uffff\73\66\2\uffff"+
            "\3\66\60\uffff\62\66\u014f\uffff\71\66\2\uffff\22\66\2\uffff"+
            "\5\66\3\uffff\31\66\20\uffff\3\66\1\uffff\10\66\2\uffff\2\66"+
            "\2\uffff\26\66\1\uffff\7\66\1\uffff\1\66\3\uffff\4\66\2\uffff"+
            "\11\66\2\uffff\2\66\2\uffff\3\66\11\uffff\1\66\4\uffff\2\66"+
            "\1\uffff\5\66\2\uffff\25\66\6\uffff\3\66\1\uffff\6\66\4\uffff"+
            "\2\66\2\uffff\26\66\1\uffff\7\66\1\uffff\2\66\1\uffff\2\66\1"+
            "\uffff\2\66\2\uffff\1\66\1\uffff\5\66\4\uffff\2\66\2\uffff\3"+
            "\66\13\uffff\4\66\1\uffff\1\66\7\uffff\17\66\14\uffff\3\66\1"+
            "\uffff\11\66\1\uffff\3\66\1\uffff\26\66\1\uffff\7\66\1\uffff"+
            "\2\66\1\uffff\5\66\2\uffff\12\66\1\uffff\3\66\1\uffff\3\66\2"+
            "\uffff\1\66\17\uffff\4\66\2\uffff\12\66\1\uffff\1\66\17\uffff"+
            "\3\66\1\uffff\10\66\2\uffff\2\66\2\uffff\26\66\1\uffff\7\66"+
            "\1\uffff\2\66\1\uffff\5\66\2\uffff\10\66\3\uffff\2\66\2\uffff"+
            "\3\66\10\uffff\2\66\4\uffff\2\66\1\uffff\3\66\4\uffff\14\66"+
            "\20\uffff\2\66\1\uffff\6\66\3\uffff\3\66\1\uffff\4\66\3\uffff"+
            "\2\66\1\uffff\1\66\1\uffff\2\66\3\uffff\2\66\3\uffff\3\66\3"+
            "\uffff\10\66\1\uffff\3\66\4\uffff\5\66\3\uffff\3\66\1\uffff"+
            "\4\66\11\uffff\1\66\17\uffff\24\66\6\uffff\3\66\1\uffff\10\66"+
            "\1\uffff\3\66\1\uffff\27\66\1\uffff\12\66\1\uffff\5\66\4\uffff"+
            "\7\66\1\uffff\3\66\1\uffff\4\66\7\uffff\2\66\11\uffff\2\66\4"+
            "\uffff\12\66\22\uffff\2\66\1\uffff\10\66\1\uffff\3\66\1\uffff"+
            "\27\66\1\uffff\12\66\1\uffff\5\66\2\uffff\11\66\1\uffff\3\66"+
            "\1\uffff\4\66\7\uffff\2\66\7\uffff\1\66\1\uffff\2\66\4\uffff"+
            "\12\66\22\uffff\2\66\1\uffff\10\66\1\uffff\3\66\1\uffff\27\66"+
            "\1\uffff\20\66\4\uffff\6\66\2\uffff\3\66\1\uffff\4\66\11\uffff"+
            "\1\66\10\uffff\2\66\4\uffff\12\66\22\uffff\2\66\1\uffff\22\66"+
            "\3\uffff\30\66\1\uffff\11\66\1\uffff\1\66\2\uffff\7\66\3\uffff"+
            "\1\66\4\uffff\6\66\1\uffff\1\66\1\uffff\10\66\22\uffff\3\66"+
            "\14\uffff\72\66\4\uffff\35\66\45\uffff\2\66\1\uffff\1\66\2\uffff"+
            "\2\66\1\uffff\1\66\2\uffff\1\66\6\uffff\4\66\1\uffff\7\66\1"+
            "\uffff\3\66\1\uffff\1\66\1\uffff\1\66\2\uffff\2\66\1\uffff\15"+
            "\66\1\uffff\3\66\2\uffff\5\66\1\uffff\1\66\1\uffff\6\66\2\uffff"+
            "\12\66\2\uffff\2\66\42\uffff\72\66\4\uffff\12\66\1\uffff\42"+
            "\66\6\uffff\33\66\4\uffff\10\66\1\uffff\44\66\1\uffff\17\66"+
            "\2\uffff\1\66\60\uffff\42\66\1\uffff\5\66\1\uffff\2\66\1\uffff"+
            "\7\66\3\uffff\4\66\6\uffff\32\66\106\uffff\46\66\12\uffff\51"+
            "\66\2\uffff\1\66\4\uffff\132\66\5\uffff\104\66\5\uffff\122\66"+
            "\6\uffff\7\66\1\uffff\77\66\1\uffff\1\66\1\uffff\4\66\2\uffff"+
            "\7\66\1\uffff\1\66\1\uffff\4\66\2\uffff\47\66\1\uffff\1\66\1"+
            "\uffff\4\66\2\uffff\37\66\1\uffff\1\66\1\uffff\4\66\2\uffff"+
            "\7\66\1\uffff\1\66\1\uffff\4\66\2\uffff\7\66\1\uffff\7\66\1"+
            "\uffff\27\66\1\uffff\37\66\1\uffff\1\66\1\uffff\4\66\2\uffff"+
            "\7\66\1\uffff\47\66\1\uffff\23\66\6\uffff\34\66\43\uffff\125"+
            "\66\14\uffff\u0276\66\12\uffff\32\66\5\uffff\121\66\17\uffff"+
            "\15\66\1\uffff\7\66\13\uffff\27\66\11\uffff\24\66\14\uffff\15"+
            "\66\1\uffff\3\66\1\uffff\2\66\14\uffff\64\66\2\uffff\50\66\2"+
            "\uffff\12\66\6\uffff\12\66\6\uffff\16\66\2\uffff\12\66\6\uffff"+
            "\130\66\10\uffff\52\66\126\uffff\35\66\3\uffff\14\66\4\uffff"+
            "\14\66\4\uffff\1\66\3\uffff\52\66\2\uffff\5\66\153\uffff\40"+
            "\66\u0300\uffff\154\66\u0094\uffff\u009c\66\4\uffff\132\66\6"+
            "\uffff\26\66\2\uffff\6\66\2\uffff\46\66\2\uffff\6\66\2\uffff"+
            "\10\66\1\uffff\1\66\1\uffff\1\66\1\uffff\1\66\1\uffff\37\66"+
            "\2\uffff\65\66\1\uffff\7\66\1\uffff\1\66\3\uffff\3\66\1\uffff"+
            "\7\66\3\uffff\4\66\2\uffff\6\66\4\uffff\15\66\5\uffff\3\66\1"+
            "\uffff\7\66\23\uffff\10\66\10\uffff\10\66\10\uffff\11\66\2\uffff"+
            "\12\66\2\uffff\16\66\2\uffff\1\66\30\uffff\2\66\2\uffff\11\66"+
            "\2\uffff\16\66\23\uffff\22\66\36\uffff\33\66\25\uffff\74\66"+
            "\1\uffff\17\66\7\uffff\61\66\14\uffff\u0199\66\2\uffff\u0089"+
            "\66\2\uffff\33\66\57\uffff\47\66\31\uffff\13\66\25\uffff\u01b8"+
            "\66\1\uffff\145\66\2\uffff\22\66\16\uffff\2\66\137\uffff\4\66"+
            "\1\uffff\4\66\2\uffff\34\66\1\uffff\43\66\1\uffff\1\66\1\uffff"+
            "\4\66\3\uffff\1\66\1\uffff\7\66\2\uffff\7\66\16\uffff\37\66"+
            "\3\uffff\30\66\1\uffff\16\66\21\uffff\26\66\12\uffff\u0193\66"+
            "\26\uffff\77\66\4\uffff\40\66\2\uffff\u0110\66\u0372\uffff\32"+
            "\66\1\uffff\131\66\14\uffff\u00d6\66\32\uffff\14\66\5\uffff"+
            "\7\66\12\uffff\2\66\10\uffff\1\66\3\uffff\40\66\1\uffff\126"+
            "\66\2\uffff\2\66\2\uffff\143\66\5\uffff\50\66\4\uffff\136\66"+
            "\1\uffff\50\66\70\uffff\57\66\1\uffff\44\66\14\uffff\56\66\1"+
            "\uffff\u0080\66\1\uffff\u1ab6\66\12\uffff\u51e6\66\132\uffff"+
            "\u048d\66\3\uffff\67\66\u0739\uffff\u2ba4\66\u215c\uffff\u012e"+
            "\66\2\uffff\73\66\u0095\uffff\7\66\14\uffff\5\66\5\uffff\32"+
            "\66\1\uffff\5\66\1\uffff\1\66\1\uffff\2\66\1\uffff\2\66\1\uffff"+
            "\154\66\41\uffff\u016b\66\22\uffff\100\66\2\uffff\66\66\50\uffff"+
            "\16\66\2\uffff\20\66\20\uffff\4\66\14\uffff\5\66\20\uffff\2"+
            "\66\2\uffff\12\66\1\uffff\5\66\6\uffff\10\66\1\uffff\4\66\4"+
            "\uffff\5\66\1\uffff\u0087\66\4\uffff\7\66\2\uffff\61\66\1\uffff"+
            "\1\66\2\uffff\1\66\1\uffff\32\66\1\uffff\1\66\1\uffff\1\66\2"+
            "\uffff\1\66\2\uffff\133\66\3\uffff\6\66\2\uffff\6\66\2\uffff"+
            "\6\66\2\uffff\3\66\3\uffff\3\66\1\uffff\3\66\1\uffff\7\66",
            "\2\66\1\63\1\62\3\66\2\uffff\1\67\1\66\1\uffff\1\66\1\uffff"+
            "\1\66\12\60\1\uffff\1\66\1\uffff\1\66\1\uffff\1\70\1\66\32\57"+
            "\1\uffff\1\64\2\uffff\1\61\1\uffff\32\56\1\uffff\1\66\44\uffff"+
            "\7\66\1\uffff\2\66\1\uffff\1\66\1\uffff\1\66\1\uffff\4\66\1"+
            "\uffff\3\66\1\uffff\2\66\1\uffff\u017b\66\31\uffff\162\66\4"+
            "\uffff\14\66\16\uffff\5\66\11\uffff\1\66\21\uffff\130\66\5\uffff"+
            "\23\66\12\uffff\1\66\3\uffff\1\66\7\uffff\5\66\1\uffff\1\66"+
            "\1\uffff\24\66\1\uffff\54\66\1\uffff\54\66\4\uffff\u0087\66"+
            "\1\uffff\107\66\1\uffff\46\66\2\uffff\2\66\6\uffff\20\66\41"+
            "\uffff\46\66\2\uffff\7\66\1\uffff\47\66\1\uffff\2\66\6\uffff"+
            "\21\66\1\uffff\27\66\1\uffff\12\66\13\uffff\33\66\5\uffff\5"+
            "\66\27\uffff\12\66\5\uffff\1\66\3\uffff\1\66\1\uffff\32\66\5"+
            "\uffff\31\66\7\uffff\175\66\1\uffff\60\66\2\uffff\73\66\2\uffff"+
            "\3\66\60\uffff\62\66\u014f\uffff\71\66\2\uffff\22\66\2\uffff"+
            "\5\66\3\uffff\31\66\20\uffff\3\66\1\uffff\10\66\2\uffff\2\66"+
            "\2\uffff\26\66\1\uffff\7\66\1\uffff\1\66\3\uffff\4\66\2\uffff"+
            "\11\66\2\uffff\2\66\2\uffff\3\66\11\uffff\1\66\4\uffff\2\66"+
            "\1\uffff\5\66\2\uffff\25\66\6\uffff\3\66\1\uffff\6\66\4\uffff"+
            "\2\66\2\uffff\26\66\1\uffff\7\66\1\uffff\2\66\1\uffff\2\66\1"+
            "\uffff\2\66\2\uffff\1\66\1\uffff\5\66\4\uffff\2\66\2\uffff\3"+
            "\66\13\uffff\4\66\1\uffff\1\66\7\uffff\17\66\14\uffff\3\66\1"+
            "\uffff\11\66\1\uffff\3\66\1\uffff\26\66\1\uffff\7\66\1\uffff"+
            "\2\66\1\uffff\5\66\2\uffff\12\66\1\uffff\3\66\1\uffff\3\66\2"+
            "\uffff\1\66\17\uffff\4\66\2\uffff\12\66\1\uffff\1\66\17\uffff"+
            "\3\66\1\uffff\10\66\2\uffff\2\66\2\uffff\26\66\1\uffff\7\66"+
            "\1\uffff\2\66\1\uffff\5\66\2\uffff\10\66\3\uffff\2\66\2\uffff"+
            "\3\66\10\uffff\2\66\4\uffff\2\66\1\uffff\3\66\4\uffff\14\66"+
            "\20\uffff\2\66\1\uffff\6\66\3\uffff\3\66\1\uffff\4\66\3\uffff"+
            "\2\66\1\uffff\1\66\1\uffff\2\66\3\uffff\2\66\3\uffff\3\66\3"+
            "\uffff\10\66\1\uffff\3\66\4\uffff\5\66\3\uffff\3\66\1\uffff"+
            "\4\66\11\uffff\1\66\17\uffff\24\66\6\uffff\3\66\1\uffff\10\66"+
            "\1\uffff\3\66\1\uffff\27\66\1\uffff\12\66\1\uffff\5\66\4\uffff"+
            "\7\66\1\uffff\3\66\1\uffff\4\66\7\uffff\2\66\11\uffff\2\66\4"+
            "\uffff\12\66\22\uffff\2\66\1\uffff\10\66\1\uffff\3\66\1\uffff"+
            "\27\66\1\uffff\12\66\1\uffff\5\66\2\uffff\11\66\1\uffff\3\66"+
            "\1\uffff\4\66\7\uffff\2\66\7\uffff\1\66\1\uffff\2\66\4\uffff"+
            "\12\66\22\uffff\2\66\1\uffff\10\66\1\uffff\3\66\1\uffff\27\66"+
            "\1\uffff\20\66\4\uffff\6\66\2\uffff\3\66\1\uffff\4\66\11\uffff"+
            "\1\66\10\uffff\2\66\4\uffff\12\66\22\uffff\2\66\1\uffff\22\66"+
            "\3\uffff\30\66\1\uffff\11\66\1\uffff\1\66\2\uffff\7\66\3\uffff"+
            "\1\66\4\uffff\6\66\1\uffff\1\66\1\uffff\10\66\22\uffff\3\66"+
            "\14\uffff\72\66\4\uffff\35\66\45\uffff\2\66\1\uffff\1\66\2\uffff"+
            "\2\66\1\uffff\1\66\2\uffff\1\66\6\uffff\4\66\1\uffff\7\66\1"+
            "\uffff\3\66\1\uffff\1\66\1\uffff\1\66\2\uffff\2\66\1\uffff\15"+
            "\66\1\uffff\3\66\2\uffff\5\66\1\uffff\1\66\1\uffff\6\66\2\uffff"+
            "\12\66\2\uffff\2\66\42\uffff\72\66\4\uffff\12\66\1\uffff\42"+
            "\66\6\uffff\33\66\4\uffff\10\66\1\uffff\44\66\1\uffff\17\66"+
            "\2\uffff\1\66\60\uffff\42\66\1\uffff\5\66\1\uffff\2\66\1\uffff"+
            "\7\66\3\uffff\4\66\6\uffff\32\66\106\uffff\46\66\12\uffff\51"+
            "\66\2\uffff\1\66\4\uffff\132\66\5\uffff\104\66\5\uffff\122\66"+
            "\6\uffff\7\66\1\uffff\77\66\1\uffff\1\66\1\uffff\4\66\2\uffff"+
            "\7\66\1\uffff\1\66\1\uffff\4\66\2\uffff\47\66\1\uffff\1\66\1"+
            "\uffff\4\66\2\uffff\37\66\1\uffff\1\66\1\uffff\4\66\2\uffff"+
            "\7\66\1\uffff\1\66\1\uffff\4\66\2\uffff\7\66\1\uffff\7\66\1"+
            "\uffff\27\66\1\uffff\37\66\1\uffff\1\66\1\uffff\4\66\2\uffff"+
            "\7\66\1\uffff\47\66\1\uffff\23\66\6\uffff\34\66\43\uffff\125"+
            "\66\14\uffff\u0276\66\12\uffff\32\66\5\uffff\121\66\17\uffff"+
            "\15\66\1\uffff\7\66\13\uffff\27\66\11\uffff\24\66\14\uffff\15"+
            "\66\1\uffff\3\66\1\uffff\2\66\14\uffff\64\66\2\uffff\50\66\2"+
            "\uffff\12\66\6\uffff\12\66\6\uffff\16\66\2\uffff\12\66\6\uffff"+
            "\130\66\10\uffff\52\66\126\uffff\35\66\3\uffff\14\66\4\uffff"+
            "\14\66\4\uffff\1\66\3\uffff\52\66\2\uffff\5\66\153\uffff\40"+
            "\66\u0300\uffff\154\66\u0094\uffff\u009c\66\4\uffff\132\66\6"+
            "\uffff\26\66\2\uffff\6\66\2\uffff\46\66\2\uffff\6\66\2\uffff"+
            "\10\66\1\uffff\1\66\1\uffff\1\66\1\uffff\1\66\1\uffff\37\66"+
            "\2\uffff\65\66\1\uffff\7\66\1\uffff\1\66\3\uffff\3\66\1\uffff"+
            "\7\66\3\uffff\4\66\2\uffff\6\66\4\uffff\15\66\5\uffff\3\66\1"+
            "\uffff\7\66\23\uffff\10\66\10\uffff\10\66\10\uffff\11\66\2\uffff"+
            "\12\66\2\uffff\16\66\2\uffff\1\66\30\uffff\2\66\2\uffff\11\66"+
            "\2\uffff\16\66\23\uffff\22\66\36\uffff\33\66\25\uffff\74\66"+
            "\1\uffff\17\66\7\uffff\61\66\14\uffff\u0199\66\2\uffff\u0089"+
            "\66\2\uffff\33\66\57\uffff\47\66\31\uffff\13\66\25\uffff\u01b8"+
            "\66\1\uffff\145\66\2\uffff\22\66\16\uffff\2\66\137\uffff\4\66"+
            "\1\uffff\4\66\2\uffff\34\66\1\uffff\43\66\1\uffff\1\66\1\uffff"+
            "\4\66\3\uffff\1\66\1\uffff\7\66\2\uffff\7\66\16\uffff\37\66"+
            "\3\uffff\30\66\1\uffff\16\66\21\uffff\26\66\12\uffff\u0193\66"+
            "\26\uffff\77\66\4\uffff\40\66\2\uffff\u0110\66\u0372\uffff\32"+
            "\66\1\uffff\131\66\14\uffff\u00d6\66\32\uffff\14\66\5\uffff"+
            "\7\66\12\uffff\2\66\10\uffff\1\66\3\uffff\40\66\1\uffff\126"+
            "\66\2\uffff\2\66\2\uffff\143\66\5\uffff\50\66\4\uffff\136\66"+
            "\1\uffff\50\66\70\uffff\57\66\1\uffff\44\66\14\uffff\56\66\1"+
            "\uffff\u0080\66\1\uffff\u1ab6\66\12\uffff\u51e6\66\132\uffff"+
            "\u048d\66\3\uffff\67\66\u0739\uffff\u2ba4\66\u215c\uffff\u012e"+
            "\66\2\uffff\73\66\u0095\uffff\7\66\14\uffff\5\66\5\uffff\32"+
            "\66\1\uffff\5\66\1\uffff\1\66\1\uffff\2\66\1\uffff\2\66\1\uffff"+
            "\154\66\41\uffff\u016b\66\22\uffff\100\66\2\uffff\66\66\50\uffff"+
            "\16\66\2\uffff\20\66\20\uffff\4\66\14\uffff\5\66\20\uffff\2"+
            "\66\2\uffff\12\66\1\uffff\5\66\6\uffff\10\66\1\uffff\4\66\4"+
            "\uffff\5\66\1\uffff\u0087\66\4\uffff\7\66\2\uffff\61\66\1\uffff"+
            "\1\66\2\uffff\1\66\1\uffff\32\66\1\uffff\1\66\1\uffff\1\66\2"+
            "\uffff\1\66\2\uffff\133\66\3\uffff\6\66\2\uffff\6\66\2\uffff"+
            "\6\66\2\uffff\3\66\3\uffff\3\66\1\uffff\3\66\1\uffff\7\66",
            "\2\66\1\63\1\62\3\66\2\uffff\1\67\1\66\1\uffff\1\66\1\uffff"+
            "\1\66\12\60\1\uffff\1\66\1\uffff\1\66\1\uffff\1\70\1\66\32\57"+
            "\1\uffff\1\64\2\uffff\1\61\1\uffff\32\56\1\uffff\1\66\44\uffff"+
            "\7\66\1\uffff\2\66\1\uffff\1\66\1\uffff\1\66\1\uffff\4\66\1"+
            "\uffff\3\66\1\uffff\2\66\1\uffff\u017b\66\31\uffff\162\66\4"+
            "\uffff\14\66\16\uffff\5\66\11\uffff\1\66\21\uffff\130\66\5\uffff"+
            "\23\66\12\uffff\1\66\3\uffff\1\66\7\uffff\5\66\1\uffff\1\66"+
            "\1\uffff\24\66\1\uffff\54\66\1\uffff\54\66\4\uffff\u0087\66"+
            "\1\uffff\107\66\1\uffff\46\66\2\uffff\2\66\6\uffff\20\66\41"+
            "\uffff\46\66\2\uffff\7\66\1\uffff\47\66\1\uffff\2\66\6\uffff"+
            "\21\66\1\uffff\27\66\1\uffff\12\66\13\uffff\33\66\5\uffff\5"+
            "\66\27\uffff\12\66\5\uffff\1\66\3\uffff\1\66\1\uffff\32\66\5"+
            "\uffff\31\66\7\uffff\175\66\1\uffff\60\66\2\uffff\73\66\2\uffff"+
            "\3\66\60\uffff\62\66\u014f\uffff\71\66\2\uffff\22\66\2\uffff"+
            "\5\66\3\uffff\31\66\20\uffff\3\66\1\uffff\10\66\2\uffff\2\66"+
            "\2\uffff\26\66\1\uffff\7\66\1\uffff\1\66\3\uffff\4\66\2\uffff"+
            "\11\66\2\uffff\2\66\2\uffff\3\66\11\uffff\1\66\4\uffff\2\66"+
            "\1\uffff\5\66\2\uffff\25\66\6\uffff\3\66\1\uffff\6\66\4\uffff"+
            "\2\66\2\uffff\26\66\1\uffff\7\66\1\uffff\2\66\1\uffff\2\66\1"+
            "\uffff\2\66\2\uffff\1\66\1\uffff\5\66\4\uffff\2\66\2\uffff\3"+
            "\66\13\uffff\4\66\1\uffff\1\66\7\uffff\17\66\14\uffff\3\66\1"+
            "\uffff\11\66\1\uffff\3\66\1\uffff\26\66\1\uffff\7\66\1\uffff"+
            "\2\66\1\uffff\5\66\2\uffff\12\66\1\uffff\3\66\1\uffff\3\66\2"+
            "\uffff\1\66\17\uffff\4\66\2\uffff\12\66\1\uffff\1\66\17\uffff"+
            "\3\66\1\uffff\10\66\2\uffff\2\66\2\uffff\26\66\1\uffff\7\66"+
            "\1\uffff\2\66\1\uffff\5\66\2\uffff\10\66\3\uffff\2\66\2\uffff"+
            "\3\66\10\uffff\2\66\4\uffff\2\66\1\uffff\3\66\4\uffff\14\66"+
            "\20\uffff\2\66\1\uffff\6\66\3\uffff\3\66\1\uffff\4\66\3\uffff"+
            "\2\66\1\uffff\1\66\1\uffff\2\66\3\uffff\2\66\3\uffff\3\66\3"+
            "\uffff\10\66\1\uffff\3\66\4\uffff\5\66\3\uffff\3\66\1\uffff"+
            "\4\66\11\uffff\1\66\17\uffff\24\66\6\uffff\3\66\1\uffff\10\66"+
            "\1\uffff\3\66\1\uffff\27\66\1\uffff\12\66\1\uffff\5\66\4\uffff"+
            "\7\66\1\uffff\3\66\1\uffff\4\66\7\uffff\2\66\11\uffff\2\66\4"+
            "\uffff\12\66\22\uffff\2\66\1\uffff\10\66\1\uffff\3\66\1\uffff"+
            "\27\66\1\uffff\12\66\1\uffff\5\66\2\uffff\11\66\1\uffff\3\66"+
            "\1\uffff\4\66\7\uffff\2\66\7\uffff\1\66\1\uffff\2\66\4\uffff"+
            "\12\66\22\uffff\2\66\1\uffff\10\66\1\uffff\3\66\1\uffff\27\66"+
            "\1\uffff\20\66\4\uffff\6\66\2\uffff\3\66\1\uffff\4\66\11\uffff"+
            "\1\66\10\uffff\2\66\4\uffff\12\66\22\uffff\2\66\1\uffff\22\66"+
            "\3\uffff\30\66\1\uffff\11\66\1\uffff\1\66\2\uffff\7\66\3\uffff"+
            "\1\66\4\uffff\6\66\1\uffff\1\66\1\uffff\10\66\22\uffff\3\66"+
            "\14\uffff\72\66\4\uffff\35\66\45\uffff\2\66\1\uffff\1\66\2\uffff"+
            "\2\66\1\uffff\1\66\2\uffff\1\66\6\uffff\4\66\1\uffff\7\66\1"+
            "\uffff\3\66\1\uffff\1\66\1\uffff\1\66\2\uffff\2\66\1\uffff\15"+
            "\66\1\uffff\3\66\2\uffff\5\66\1\uffff\1\66\1\uffff\6\66\2\uffff"+
            "\12\66\2\uffff\2\66\42\uffff\72\66\4\uffff\12\66\1\uffff\42"+
            "\66\6\uffff\33\66\4\uffff\10\66\1\uffff\44\66\1\uffff\17\66"+
            "\2\uffff\1\66\60\uffff\42\66\1\uffff\5\66\1\uffff\2\66\1\uffff"+
            "\7\66\3\uffff\4\66\6\uffff\32\66\106\uffff\46\66\12\uffff\51"+
            "\66\2\uffff\1\66\4\uffff\132\66\5\uffff\104\66\5\uffff\122\66"+
            "\6\uffff\7\66\1\uffff\77\66\1\uffff\1\66\1\uffff\4\66\2\uffff"+
            "\7\66\1\uffff\1\66\1\uffff\4\66\2\uffff\47\66\1\uffff\1\66\1"+
            "\uffff\4\66\2\uffff\37\66\1\uffff\1\66\1\uffff\4\66\2\uffff"+
            "\7\66\1\uffff\1\66\1\uffff\4\66\2\uffff\7\66\1\uffff\7\66\1"+
            "\uffff\27\66\1\uffff\37\66\1\uffff\1\66\1\uffff\4\66\2\uffff"+
            "\7\66\1\uffff\47\66\1\uffff\23\66\6\uffff\34\66\43\uffff\125"+
            "\66\14\uffff\u0276\66\12\uffff\32\66\5\uffff\121\66\17\uffff"+
            "\15\66\1\uffff\7\66\13\uffff\27\66\11\uffff\24\66\14\uffff\15"+
            "\66\1\uffff\3\66\1\uffff\2\66\14\uffff\64\66\2\uffff\50\66\2"+
            "\uffff\12\66\6\uffff\12\66\6\uffff\16\66\2\uffff\12\66\6\uffff"+
            "\130\66\10\uffff\52\66\126\uffff\35\66\3\uffff\14\66\4\uffff"+
            "\14\66\4\uffff\1\66\3\uffff\52\66\2\uffff\5\66\153\uffff\40"+
            "\66\u0300\uffff\154\66\u0094\uffff\u009c\66\4\uffff\132\66\6"+
            "\uffff\26\66\2\uffff\6\66\2\uffff\46\66\2\uffff\6\66\2\uffff"+
            "\10\66\1\uffff\1\66\1\uffff\1\66\1\uffff\1\66\1\uffff\37\66"+
            "\2\uffff\65\66\1\uffff\7\66\1\uffff\1\66\3\uffff\3\66\1\uffff"+
            "\7\66\3\uffff\4\66\2\uffff\6\66\4\uffff\15\66\5\uffff\3\66\1"+
            "\uffff\7\66\23\uffff\10\66\10\uffff\10\66\10\uffff\11\66\2\uffff"+
            "\12\66\2\uffff\16\66\2\uffff\1\66\30\uffff\2\66\2\uffff\11\66"+
            "\2\uffff\16\66\23\uffff\22\66\36\uffff\33\66\25\uffff\74\66"+
            "\1\uffff\17\66\7\uffff\61\66\14\uffff\u0199\66\2\uffff\u0089"+
            "\66\2\uffff\33\66\57\uffff\47\66\31\uffff\13\66\25\uffff\u01b8"+
            "\66\1\uffff\145\66\2\uffff\22\66\16\uffff\2\66\137\uffff\4\66"+
            "\1\uffff\4\66\2\uffff\34\66\1\uffff\43\66\1\uffff\1\66\1\uffff"+
            "\4\66\3\uffff\1\66\1\uffff\7\66\2\uffff\7\66\16\uffff\37\66"+
            "\3\uffff\30\66\1\uffff\16\66\21\uffff\26\66\12\uffff\u0193\66"+
            "\26\uffff\77\66\4\uffff\40\66\2\uffff\u0110\66\u0372\uffff\32"+
            "\66\1\uffff\131\66\14\uffff\u00d6\66\32\uffff\14\66\5\uffff"+
            "\7\66\12\uffff\2\66\10\uffff\1\66\3\uffff\40\66\1\uffff\126"+
            "\66\2\uffff\2\66\2\uffff\143\66\5\uffff\50\66\4\uffff\136\66"+
            "\1\uffff\50\66\70\uffff\57\66\1\uffff\44\66\14\uffff\56\66\1"+
            "\uffff\u0080\66\1\uffff\u1ab6\66\12\uffff\u51e6\66\132\uffff"+
            "\u048d\66\3\uffff\67\66\u0739\uffff\u2ba4\66\u215c\uffff\u012e"+
            "\66\2\uffff\73\66\u0095\uffff\7\66\14\uffff\5\66\5\uffff\32"+
            "\66\1\uffff\5\66\1\uffff\1\66\1\uffff\2\66\1\uffff\2\66\1\uffff"+
            "\154\66\41\uffff\u016b\66\22\uffff\100\66\2\uffff\66\66\50\uffff"+
            "\16\66\2\uffff\20\66\20\uffff\4\66\14\uffff\5\66\20\uffff\2"+
            "\66\2\uffff\12\66\1\uffff\5\66\6\uffff\10\66\1\uffff\4\66\4"+
            "\uffff\5\66\1\uffff\u0087\66\4\uffff\7\66\2\uffff\61\66\1\uffff"+
            "\1\66\2\uffff\1\66\1\uffff\32\66\1\uffff\1\66\1\uffff\1\66\2"+
            "\uffff\1\66\2\uffff\133\66\3\uffff\6\66\2\uffff\6\66\2\uffff"+
            "\6\66\2\uffff\3\66\3\uffff\3\66\1\uffff\3\66\1\uffff\7\66",
            "\2\66\1\63\1\62\3\66\2\uffff\1\67\1\66\1\uffff\1\66\1\uffff"+
            "\1\66\12\60\1\uffff\1\66\1\uffff\1\66\1\uffff\1\70\1\66\32\57"+
            "\1\uffff\1\64\2\uffff\1\61\1\uffff\32\56\1\uffff\1\66\44\uffff"+
            "\7\66\1\uffff\2\66\1\uffff\1\66\1\uffff\1\66\1\uffff\4\66\1"+
            "\uffff\3\66\1\uffff\2\66\1\uffff\u017b\66\31\uffff\162\66\4"+
            "\uffff\14\66\16\uffff\5\66\11\uffff\1\66\21\uffff\130\66\5\uffff"+
            "\23\66\12\uffff\1\66\3\uffff\1\66\7\uffff\5\66\1\uffff\1\66"+
            "\1\uffff\24\66\1\uffff\54\66\1\uffff\54\66\4\uffff\u0087\66"+
            "\1\uffff\107\66\1\uffff\46\66\2\uffff\2\66\6\uffff\20\66\41"+
            "\uffff\46\66\2\uffff\7\66\1\uffff\47\66\1\uffff\2\66\6\uffff"+
            "\21\66\1\uffff\27\66\1\uffff\12\66\13\uffff\33\66\5\uffff\5"+
            "\66\27\uffff\12\66\5\uffff\1\66\3\uffff\1\66\1\uffff\32\66\5"+
            "\uffff\31\66\7\uffff\175\66\1\uffff\60\66\2\uffff\73\66\2\uffff"+
            "\3\66\60\uffff\62\66\u014f\uffff\71\66\2\uffff\22\66\2\uffff"+
            "\5\66\3\uffff\31\66\20\uffff\3\66\1\uffff\10\66\2\uffff\2\66"+
            "\2\uffff\26\66\1\uffff\7\66\1\uffff\1\66\3\uffff\4\66\2\uffff"+
            "\11\66\2\uffff\2\66\2\uffff\3\66\11\uffff\1\66\4\uffff\2\66"+
            "\1\uffff\5\66\2\uffff\25\66\6\uffff\3\66\1\uffff\6\66\4\uffff"+
            "\2\66\2\uffff\26\66\1\uffff\7\66\1\uffff\2\66\1\uffff\2\66\1"+
            "\uffff\2\66\2\uffff\1\66\1\uffff\5\66\4\uffff\2\66\2\uffff\3"+
            "\66\13\uffff\4\66\1\uffff\1\66\7\uffff\17\66\14\uffff\3\66\1"+
            "\uffff\11\66\1\uffff\3\66\1\uffff\26\66\1\uffff\7\66\1\uffff"+
            "\2\66\1\uffff\5\66\2\uffff\12\66\1\uffff\3\66\1\uffff\3\66\2"+
            "\uffff\1\66\17\uffff\4\66\2\uffff\12\66\1\uffff\1\66\17\uffff"+
            "\3\66\1\uffff\10\66\2\uffff\2\66\2\uffff\26\66\1\uffff\7\66"+
            "\1\uffff\2\66\1\uffff\5\66\2\uffff\10\66\3\uffff\2\66\2\uffff"+
            "\3\66\10\uffff\2\66\4\uffff\2\66\1\uffff\3\66\4\uffff\14\66"+
            "\20\uffff\2\66\1\uffff\6\66\3\uffff\3\66\1\uffff\4\66\3\uffff"+
            "\2\66\1\uffff\1\66\1\uffff\2\66\3\uffff\2\66\3\uffff\3\66\3"+
            "\uffff\10\66\1\uffff\3\66\4\uffff\5\66\3\uffff\3\66\1\uffff"+
            "\4\66\11\uffff\1\66\17\uffff\24\66\6\uffff\3\66\1\uffff\10\66"+
            "\1\uffff\3\66\1\uffff\27\66\1\uffff\12\66\1\uffff\5\66\4\uffff"+
            "\7\66\1\uffff\3\66\1\uffff\4\66\7\uffff\2\66\11\uffff\2\66\4"+
            "\uffff\12\66\22\uffff\2\66\1\uffff\10\66\1\uffff\3\66\1\uffff"+
            "\27\66\1\uffff\12\66\1\uffff\5\66\2\uffff\11\66\1\uffff\3\66"+
            "\1\uffff\4\66\7\uffff\2\66\7\uffff\1\66\1\uffff\2\66\4\uffff"+
            "\12\66\22\uffff\2\66\1\uffff\10\66\1\uffff\3\66\1\uffff\27\66"+
            "\1\uffff\20\66\4\uffff\6\66\2\uffff\3\66\1\uffff\4\66\11\uffff"+
            "\1\66\10\uffff\2\66\4\uffff\12\66\22\uffff\2\66\1\uffff\22\66"+
            "\3\uffff\30\66\1\uffff\11\66\1\uffff\1\66\2\uffff\7\66\3\uffff"+
            "\1\66\4\uffff\6\66\1\uffff\1\66\1\uffff\10\66\22\uffff\3\66"+
            "\14\uffff\72\66\4\uffff\35\66\45\uffff\2\66\1\uffff\1\66\2\uffff"+
            "\2\66\1\uffff\1\66\2\uffff\1\66\6\uffff\4\66\1\uffff\7\66\1"+
            "\uffff\3\66\1\uffff\1\66\1\uffff\1\66\2\uffff\2\66\1\uffff\15"+
            "\66\1\uffff\3\66\2\uffff\5\66\1\uffff\1\66\1\uffff\6\66\2\uffff"+
            "\12\66\2\uffff\2\66\42\uffff\72\66\4\uffff\12\66\1\uffff\42"+
            "\66\6\uffff\33\66\4\uffff\10\66\1\uffff\44\66\1\uffff\17\66"+
            "\2\uffff\1\66\60\uffff\42\66\1\uffff\5\66\1\uffff\2\66\1\uffff"+
            "\7\66\3\uffff\4\66\6\uffff\32\66\106\uffff\46\66\12\uffff\51"+
            "\66\2\uffff\1\66\4\uffff\132\66\5\uffff\104\66\5\uffff\122\66"+
            "\6\uffff\7\66\1\uffff\77\66\1\uffff\1\66\1\uffff\4\66\2\uffff"+
            "\7\66\1\uffff\1\66\1\uffff\4\66\2\uffff\47\66\1\uffff\1\66\1"+
            "\uffff\4\66\2\uffff\37\66\1\uffff\1\66\1\uffff\4\66\2\uffff"+
            "\7\66\1\uffff\1\66\1\uffff\4\66\2\uffff\7\66\1\uffff\7\66\1"+
            "\uffff\27\66\1\uffff\37\66\1\uffff\1\66\1\uffff\4\66\2\uffff"+
            "\7\66\1\uffff\47\66\1\uffff\23\66\6\uffff\34\66\43\uffff\125"+
            "\66\14\uffff\u0276\66\12\uffff\32\66\5\uffff\121\66\17\uffff"+
            "\15\66\1\uffff\7\66\13\uffff\27\66\11\uffff\24\66\14\uffff\15"+
            "\66\1\uffff\3\66\1\uffff\2\66\14\uffff\64\66\2\uffff\50\66\2"+
            "\uffff\12\66\6\uffff\12\66\6\uffff\16\66\2\uffff\12\66\6\uffff"+
            "\130\66\10\uffff\52\66\126\uffff\35\66\3\uffff\14\66\4\uffff"+
            "\14\66\4\uffff\1\66\3\uffff\52\66\2\uffff\5\66\153\uffff\40"+
            "\66\u0300\uffff\154\66\u0094\uffff\u009c\66\4\uffff\132\66\6"+
            "\uffff\26\66\2\uffff\6\66\2\uffff\46\66\2\uffff\6\66\2\uffff"+
            "\10\66\1\uffff\1\66\1\uffff\1\66\1\uffff\1\66\1\uffff\37\66"+
            "\2\uffff\65\66\1\uffff\7\66\1\uffff\1\66\3\uffff\3\66\1\uffff"+
            "\7\66\3\uffff\4\66\2\uffff\6\66\4\uffff\15\66\5\uffff\3\66\1"+
            "\uffff\7\66\23\uffff\10\66\10\uffff\10\66\10\uffff\11\66\2\uffff"+
            "\12\66\2\uffff\16\66\2\uffff\1\66\30\uffff\2\66\2\uffff\11\66"+
            "\2\uffff\16\66\23\uffff\22\66\36\uffff\33\66\25\uffff\74\66"+
            "\1\uffff\17\66\7\uffff\61\66\14\uffff\u0199\66\2\uffff\u0089"+
            "\66\2\uffff\33\66\57\uffff\47\66\31\uffff\13\66\25\uffff\u01b8"+
            "\66\1\uffff\145\66\2\uffff\22\66\16\uffff\2\66\137\uffff\4\66"+
            "\1\uffff\4\66\2\uffff\34\66\1\uffff\43\66\1\uffff\1\66\1\uffff"+
            "\4\66\3\uffff\1\66\1\uffff\7\66\2\uffff\7\66\16\uffff\37\66"+
            "\3\uffff\30\66\1\uffff\16\66\21\uffff\26\66\12\uffff\u0193\66"+
            "\26\uffff\77\66\4\uffff\40\66\2\uffff\u0110\66\u0372\uffff\32"+
            "\66\1\uffff\131\66\14\uffff\u00d6\66\32\uffff\14\66\5\uffff"+
            "\7\66\12\uffff\2\66\10\uffff\1\66\3\uffff\40\66\1\uffff\126"+
            "\66\2\uffff\2\66\2\uffff\143\66\5\uffff\50\66\4\uffff\136\66"+
            "\1\uffff\50\66\70\uffff\57\66\1\uffff\44\66\14\uffff\56\66\1"+
            "\uffff\u0080\66\1\uffff\u1ab6\66\12\uffff\u51e6\66\132\uffff"+
            "\u048d\66\3\uffff\67\66\u0739\uffff\u2ba4\66\u215c\uffff\u012e"+
            "\66\2\uffff\73\66\u0095\uffff\7\66\14\uffff\5\66\5\uffff\32"+
            "\66\1\uffff\5\66\1\uffff\1\66\1\uffff\2\66\1\uffff\2\66\1\uffff"+
            "\154\66\41\uffff\u016b\66\22\uffff\100\66\2\uffff\66\66\50\uffff"+
            "\16\66\2\uffff\20\66\20\uffff\4\66\14\uffff\5\66\20\uffff\2"+
            "\66\2\uffff\12\66\1\uffff\5\66\6\uffff\10\66\1\uffff\4\66\4"+
            "\uffff\5\66\1\uffff\u0087\66\4\uffff\7\66\2\uffff\61\66\1\uffff"+
            "\1\66\2\uffff\1\66\1\uffff\32\66\1\uffff\1\66\1\uffff\1\66\2"+
            "\uffff\1\66\2\uffff\133\66\3\uffff\6\66\2\uffff\6\66\2\uffff"+
            "\6\66\2\uffff\3\66\3\uffff\3\66\1\uffff\3\66\1\uffff\7\66",
            "\2\66\1\63\1\62\3\66\2\uffff\1\67\1\66\1\uffff\1\66\1\uffff"+
            "\1\66\12\60\1\uffff\1\66\1\uffff\1\66\1\uffff\1\70\1\66\32\57"+
            "\1\uffff\1\64\2\uffff\1\61\1\uffff\32\56\1\uffff\1\66\44\uffff"+
            "\7\66\1\uffff\2\66\1\uffff\1\66\1\uffff\1\66\1\uffff\4\66\1"+
            "\uffff\3\66\1\uffff\2\66\1\uffff\u017b\66\31\uffff\162\66\4"+
            "\uffff\14\66\16\uffff\5\66\11\uffff\1\66\21\uffff\130\66\5\uffff"+
            "\23\66\12\uffff\1\66\3\uffff\1\66\7\uffff\5\66\1\uffff\1\66"+
            "\1\uffff\24\66\1\uffff\54\66\1\uffff\54\66\4\uffff\u0087\66"+
            "\1\uffff\107\66\1\uffff\46\66\2\uffff\2\66\6\uffff\20\66\41"+
            "\uffff\46\66\2\uffff\7\66\1\uffff\47\66\1\uffff\2\66\6\uffff"+
            "\21\66\1\uffff\27\66\1\uffff\12\66\13\uffff\33\66\5\uffff\5"+
            "\66\27\uffff\12\66\5\uffff\1\66\3\uffff\1\66\1\uffff\32\66\5"+
            "\uffff\31\66\7\uffff\175\66\1\uffff\60\66\2\uffff\73\66\2\uffff"+
            "\3\66\60\uffff\62\66\u014f\uffff\71\66\2\uffff\22\66\2\uffff"+
            "\5\66\3\uffff\31\66\20\uffff\3\66\1\uffff\10\66\2\uffff\2\66"+
            "\2\uffff\26\66\1\uffff\7\66\1\uffff\1\66\3\uffff\4\66\2\uffff"+
            "\11\66\2\uffff\2\66\2\uffff\3\66\11\uffff\1\66\4\uffff\2\66"+
            "\1\uffff\5\66\2\uffff\25\66\6\uffff\3\66\1\uffff\6\66\4\uffff"+
            "\2\66\2\uffff\26\66\1\uffff\7\66\1\uffff\2\66\1\uffff\2\66\1"+
            "\uffff\2\66\2\uffff\1\66\1\uffff\5\66\4\uffff\2\66\2\uffff\3"+
            "\66\13\uffff\4\66\1\uffff\1\66\7\uffff\17\66\14\uffff\3\66\1"+
            "\uffff\11\66\1\uffff\3\66\1\uffff\26\66\1\uffff\7\66\1\uffff"+
            "\2\66\1\uffff\5\66\2\uffff\12\66\1\uffff\3\66\1\uffff\3\66\2"+
            "\uffff\1\66\17\uffff\4\66\2\uffff\12\66\1\uffff\1\66\17\uffff"+
            "\3\66\1\uffff\10\66\2\uffff\2\66\2\uffff\26\66\1\uffff\7\66"+
            "\1\uffff\2\66\1\uffff\5\66\2\uffff\10\66\3\uffff\2\66\2\uffff"+
            "\3\66\10\uffff\2\66\4\uffff\2\66\1\uffff\3\66\4\uffff\14\66"+
            "\20\uffff\2\66\1\uffff\6\66\3\uffff\3\66\1\uffff\4\66\3\uffff"+
            "\2\66\1\uffff\1\66\1\uffff\2\66\3\uffff\2\66\3\uffff\3\66\3"+
            "\uffff\10\66\1\uffff\3\66\4\uffff\5\66\3\uffff\3\66\1\uffff"+
            "\4\66\11\uffff\1\66\17\uffff\24\66\6\uffff\3\66\1\uffff\10\66"+
            "\1\uffff\3\66\1\uffff\27\66\1\uffff\12\66\1\uffff\5\66\4\uffff"+
            "\7\66\1\uffff\3\66\1\uffff\4\66\7\uffff\2\66\11\uffff\2\66\4"+
            "\uffff\12\66\22\uffff\2\66\1\uffff\10\66\1\uffff\3\66\1\uffff"+
            "\27\66\1\uffff\12\66\1\uffff\5\66\2\uffff\11\66\1\uffff\3\66"+
            "\1\uffff\4\66\7\uffff\2\66\7\uffff\1\66\1\uffff\2\66\4\uffff"+
            "\12\66\22\uffff\2\66\1\uffff\10\66\1\uffff\3\66\1\uffff\27\66"+
            "\1\uffff\20\66\4\uffff\6\66\2\uffff\3\66\1\uffff\4\66\11\uffff"+
            "\1\66\10\uffff\2\66\4\uffff\12\66\22\uffff\2\66\1\uffff\22\66"+
            "\3\uffff\30\66\1\uffff\11\66\1\uffff\1\66\2\uffff\7\66\3\uffff"+
            "\1\66\4\uffff\6\66\1\uffff\1\66\1\uffff\10\66\22\uffff\3\66"+
            "\14\uffff\72\66\4\uffff\35\66\45\uffff\2\66\1\uffff\1\66\2\uffff"+
            "\2\66\1\uffff\1\66\2\uffff\1\66\6\uffff\4\66\1\uffff\7\66\1"+
            "\uffff\3\66\1\uffff\1\66\1\uffff\1\66\2\uffff\2\66\1\uffff\15"+
            "\66\1\uffff\3\66\2\uffff\5\66\1\uffff\1\66\1\uffff\6\66\2\uffff"+
            "\12\66\2\uffff\2\66\42\uffff\72\66\4\uffff\12\66\1\uffff\42"+
            "\66\6\uffff\33\66\4\uffff\10\66\1\uffff\44\66\1\uffff\17\66"+
            "\2\uffff\1\66\60\uffff\42\66\1\uffff\5\66\1\uffff\2\66\1\uffff"+
            "\7\66\3\uffff\4\66\6\uffff\32\66\106\uffff\46\66\12\uffff\51"+
            "\66\2\uffff\1\66\4\uffff\132\66\5\uffff\104\66\5\uffff\122\66"+
            "\6\uffff\7\66\1\uffff\77\66\1\uffff\1\66\1\uffff\4\66\2\uffff"+
            "\7\66\1\uffff\1\66\1\uffff\4\66\2\uffff\47\66\1\uffff\1\66\1"+
            "\uffff\4\66\2\uffff\37\66\1\uffff\1\66\1\uffff\4\66\2\uffff"+
            "\7\66\1\uffff\1\66\1\uffff\4\66\2\uffff\7\66\1\uffff\7\66\1"+
            "\uffff\27\66\1\uffff\37\66\1\uffff\1\66\1\uffff\4\66\2\uffff"+
            "\7\66\1\uffff\47\66\1\uffff\23\66\6\uffff\34\66\43\uffff\125"+
            "\66\14\uffff\u0276\66\12\uffff\32\66\5\uffff\121\66\17\uffff"+
            "\15\66\1\uffff\7\66\13\uffff\27\66\11\uffff\24\66\14\uffff\15"+
            "\66\1\uffff\3\66\1\uffff\2\66\14\uffff\64\66\2\uffff\50\66\2"+
            "\uffff\12\66\6\uffff\12\66\6\uffff\16\66\2\uffff\12\66\6\uffff"+
            "\130\66\10\uffff\52\66\126\uffff\35\66\3\uffff\14\66\4\uffff"+
            "\14\66\4\uffff\1\66\3\uffff\52\66\2\uffff\5\66\153\uffff\40"+
            "\66\u0300\uffff\154\66\u0094\uffff\u009c\66\4\uffff\132\66\6"+
            "\uffff\26\66\2\uffff\6\66\2\uffff\46\66\2\uffff\6\66\2\uffff"+
            "\10\66\1\uffff\1\66\1\uffff\1\66\1\uffff\1\66\1\uffff\37\66"+
            "\2\uffff\65\66\1\uffff\7\66\1\uffff\1\66\3\uffff\3\66\1\uffff"+
            "\7\66\3\uffff\4\66\2\uffff\6\66\4\uffff\15\66\5\uffff\3\66\1"+
            "\uffff\7\66\23\uffff\10\66\10\uffff\10\66\10\uffff\11\66\2\uffff"+
            "\12\66\2\uffff\16\66\2\uffff\1\66\30\uffff\2\66\2\uffff\11\66"+
            "\2\uffff\16\66\23\uffff\22\66\36\uffff\33\66\25\uffff\74\66"+
            "\1\uffff\17\66\7\uffff\61\66\14\uffff\u0199\66\2\uffff\u0089"+
            "\66\2\uffff\33\66\57\uffff\47\66\31\uffff\13\66\25\uffff\u01b8"+
            "\66\1\uffff\145\66\2\uffff\22\66\16\uffff\2\66\137\uffff\4\66"+
            "\1\uffff\4\66\2\uffff\34\66\1\uffff\43\66\1\uffff\1\66\1\uffff"+
            "\4\66\3\uffff\1\66\1\uffff\7\66\2\uffff\7\66\16\uffff\37\66"+
            "\3\uffff\30\66\1\uffff\16\66\21\uffff\26\66\12\uffff\u0193\66"+
            "\26\uffff\77\66\4\uffff\40\66\2\uffff\u0110\66\u0372\uffff\32"+
            "\66\1\uffff\131\66\14\uffff\u00d6\66\32\uffff\14\66\5\uffff"+
            "\7\66\12\uffff\2\66\10\uffff\1\66\3\uffff\40\66\1\uffff\126"+
            "\66\2\uffff\2\66\2\uffff\143\66\5\uffff\50\66\4\uffff\136\66"+
            "\1\uffff\50\66\70\uffff\57\66\1\uffff\44\66\14\uffff\56\66\1"+
            "\uffff\u0080\66\1\uffff\u1ab6\66\12\uffff\u51e6\66\132\uffff"+
            "\u048d\66\3\uffff\67\66\u0739\uffff\u2ba4\66\u215c\uffff\u012e"+
            "\66\2\uffff\73\66\u0095\uffff\7\66\14\uffff\5\66\5\uffff\32"+
            "\66\1\uffff\5\66\1\uffff\1\66\1\uffff\2\66\1\uffff\2\66\1\uffff"+
            "\154\66\41\uffff\u016b\66\22\uffff\100\66\2\uffff\66\66\50\uffff"+
            "\16\66\2\uffff\20\66\20\uffff\4\66\14\uffff\5\66\20\uffff\2"+
            "\66\2\uffff\12\66\1\uffff\5\66\6\uffff\10\66\1\uffff\4\66\4"+
            "\uffff\5\66\1\uffff\u0087\66\4\uffff\7\66\2\uffff\61\66\1\uffff"+
            "\1\66\2\uffff\1\66\1\uffff\32\66\1\uffff\1\66\1\uffff\1\66\2"+
            "\uffff\1\66\2\uffff\133\66\3\uffff\6\66\2\uffff\6\66\2\uffff"+
            "\6\66\2\uffff\3\66\3\uffff\3\66\1\uffff\3\66\1\uffff\7\66",
            "\2\66\1\63\1\62\3\66\2\uffff\1\67\1\66\1\uffff\1\66\1\uffff"+
            "\1\66\12\60\1\uffff\1\66\1\uffff\1\66\1\uffff\1\70\1\66\32\57"+
            "\1\uffff\1\64\2\uffff\1\61\1\uffff\32\56\1\uffff\1\66\44\uffff"+
            "\7\66\1\uffff\2\66\1\uffff\1\66\1\uffff\1\66\1\uffff\4\66\1"+
            "\uffff\3\66\1\uffff\2\66\1\uffff\u017b\66\31\uffff\162\66\4"+
            "\uffff\14\66\16\uffff\5\66\11\uffff\1\66\21\uffff\130\66\5\uffff"+
            "\23\66\12\uffff\1\66\3\uffff\1\66\7\uffff\5\66\1\uffff\1\66"+
            "\1\uffff\24\66\1\uffff\54\66\1\uffff\54\66\4\uffff\u0087\66"+
            "\1\uffff\107\66\1\uffff\46\66\2\uffff\2\66\6\uffff\20\66\41"+
            "\uffff\46\66\2\uffff\7\66\1\uffff\47\66\1\uffff\2\66\6\uffff"+
            "\21\66\1\uffff\27\66\1\uffff\12\66\13\uffff\33\66\5\uffff\5"+
            "\66\27\uffff\12\66\5\uffff\1\66\3\uffff\1\66\1\uffff\32\66\5"+
            "\uffff\31\66\7\uffff\175\66\1\uffff\60\66\2\uffff\73\66\2\uffff"+
            "\3\66\60\uffff\62\66\u014f\uffff\71\66\2\uffff\22\66\2\uffff"+
            "\5\66\3\uffff\31\66\20\uffff\3\66\1\uffff\10\66\2\uffff\2\66"+
            "\2\uffff\26\66\1\uffff\7\66\1\uffff\1\66\3\uffff\4\66\2\uffff"+
            "\11\66\2\uffff\2\66\2\uffff\3\66\11\uffff\1\66\4\uffff\2\66"+
            "\1\uffff\5\66\2\uffff\25\66\6\uffff\3\66\1\uffff\6\66\4\uffff"+
            "\2\66\2\uffff\26\66\1\uffff\7\66\1\uffff\2\66\1\uffff\2\66\1"+
            "\uffff\2\66\2\uffff\1\66\1\uffff\5\66\4\uffff\2\66\2\uffff\3"+
            "\66\13\uffff\4\66\1\uffff\1\66\7\uffff\17\66\14\uffff\3\66\1"+
            "\uffff\11\66\1\uffff\3\66\1\uffff\26\66\1\uffff\7\66\1\uffff"+
            "\2\66\1\uffff\5\66\2\uffff\12\66\1\uffff\3\66\1\uffff\3\66\2"+
            "\uffff\1\66\17\uffff\4\66\2\uffff\12\66\1\uffff\1\66\17\uffff"+
            "\3\66\1\uffff\10\66\2\uffff\2\66\2\uffff\26\66\1\uffff\7\66"+
            "\1\uffff\2\66\1\uffff\5\66\2\uffff\10\66\3\uffff\2\66\2\uffff"+
            "\3\66\10\uffff\2\66\4\uffff\2\66\1\uffff\3\66\4\uffff\14\66"+
            "\20\uffff\2\66\1\uffff\6\66\3\uffff\3\66\1\uffff\4\66\3\uffff"+
            "\2\66\1\uffff\1\66\1\uffff\2\66\3\uffff\2\66\3\uffff\3\66\3"+
            "\uffff\10\66\1\uffff\3\66\4\uffff\5\66\3\uffff\3\66\1\uffff"+
            "\4\66\11\uffff\1\66\17\uffff\24\66\6\uffff\3\66\1\uffff\10\66"+
            "\1\uffff\3\66\1\uffff\27\66\1\uffff\12\66\1\uffff\5\66\4\uffff"+
            "\7\66\1\uffff\3\66\1\uffff\4\66\7\uffff\2\66\11\uffff\2\66\4"+
            "\uffff\12\66\22\uffff\2\66\1\uffff\10\66\1\uffff\3\66\1\uffff"+
            "\27\66\1\uffff\12\66\1\uffff\5\66\2\uffff\11\66\1\uffff\3\66"+
            "\1\uffff\4\66\7\uffff\2\66\7\uffff\1\66\1\uffff\2\66\4\uffff"+
            "\12\66\22\uffff\2\66\1\uffff\10\66\1\uffff\3\66\1\uffff\27\66"+
            "\1\uffff\20\66\4\uffff\6\66\2\uffff\3\66\1\uffff\4\66\11\uffff"+
            "\1\66\10\uffff\2\66\4\uffff\12\66\22\uffff\2\66\1\uffff\22\66"+
            "\3\uffff\30\66\1\uffff\11\66\1\uffff\1\66\2\uffff\7\66\3\uffff"+
            "\1\66\4\uffff\6\66\1\uffff\1\66\1\uffff\10\66\22\uffff\3\66"+
            "\14\uffff\72\66\4\uffff\35\66\45\uffff\2\66\1\uffff\1\66\2\uffff"+
            "\2\66\1\uffff\1\66\2\uffff\1\66\6\uffff\4\66\1\uffff\7\66\1"+
            "\uffff\3\66\1\uffff\1\66\1\uffff\1\66\2\uffff\2\66\1\uffff\15"+
            "\66\1\uffff\3\66\2\uffff\5\66\1\uffff\1\66\1\uffff\6\66\2\uffff"+
            "\12\66\2\uffff\2\66\42\uffff\72\66\4\uffff\12\66\1\uffff\42"+
            "\66\6\uffff\33\66\4\uffff\10\66\1\uffff\44\66\1\uffff\17\66"+
            "\2\uffff\1\66\60\uffff\42\66\1\uffff\5\66\1\uffff\2\66\1\uffff"+
            "\7\66\3\uffff\4\66\6\uffff\32\66\106\uffff\46\66\12\uffff\51"+
            "\66\2\uffff\1\66\4\uffff\132\66\5\uffff\104\66\5\uffff\122\66"+
            "\6\uffff\7\66\1\uffff\77\66\1\uffff\1\66\1\uffff\4\66\2\uffff"+
            "\7\66\1\uffff\1\66\1\uffff\4\66\2\uffff\47\66\1\uffff\1\66\1"+
            "\uffff\4\66\2\uffff\37\66\1\uffff\1\66\1\uffff\4\66\2\uffff"+
            "\7\66\1\uffff\1\66\1\uffff\4\66\2\uffff\7\66\1\uffff\7\66\1"+
            "\uffff\27\66\1\uffff\37\66\1\uffff\1\66\1\uffff\4\66\2\uffff"+
            "\7\66\1\uffff\47\66\1\uffff\23\66\6\uffff\34\66\43\uffff\125"+
            "\66\14\uffff\u0276\66\12\uffff\32\66\5\uffff\121\66\17\uffff"+
            "\15\66\1\uffff\7\66\13\uffff\27\66\11\uffff\24\66\14\uffff\15"+
            "\66\1\uffff\3\66\1\uffff\2\66\14\uffff\64\66\2\uffff\50\66\2"+
            "\uffff\12\66\6\uffff\12\66\6\uffff\16\66\2\uffff\12\66\6\uffff"+
            "\130\66\10\uffff\52\66\126\uffff\35\66\3\uffff\14\66\4\uffff"+
            "\14\66\4\uffff\1\66\3\uffff\52\66\2\uffff\5\66\153\uffff\40"+
            "\66\u0300\uffff\154\66\u0094\uffff\u009c\66\4\uffff\132\66\6"+
            "\uffff\26\66\2\uffff\6\66\2\uffff\46\66\2\uffff\6\66\2\uffff"+
            "\10\66\1\uffff\1\66\1\uffff\1\66\1\uffff\1\66\1\uffff\37\66"+
            "\2\uffff\65\66\1\uffff\7\66\1\uffff\1\66\3\uffff\3\66\1\uffff"+
            "\7\66\3\uffff\4\66\2\uffff\6\66\4\uffff\15\66\5\uffff\3\66\1"+
            "\uffff\7\66\23\uffff\10\66\10\uffff\10\66\10\uffff\11\66\2\uffff"+
            "\12\66\2\uffff\16\66\2\uffff\1\66\30\uffff\2\66\2\uffff\11\66"+
            "\2\uffff\16\66\23\uffff\22\66\36\uffff\33\66\25\uffff\74\66"+
            "\1\uffff\17\66\7\uffff\61\66\14\uffff\u0199\66\2\uffff\u0089"+
            "\66\2\uffff\33\66\57\uffff\47\66\31\uffff\13\66\25\uffff\u01b8"+
            "\66\1\uffff\145\66\2\uffff\22\66\16\uffff\2\66\137\uffff\4\66"+
            "\1\uffff\4\66\2\uffff\34\66\1\uffff\43\66\1\uffff\1\66\1\uffff"+
            "\4\66\3\uffff\1\66\1\uffff\7\66\2\uffff\7\66\16\uffff\37\66"+
            "\3\uffff\30\66\1\uffff\16\66\21\uffff\26\66\12\uffff\u0193\66"+
            "\26\uffff\77\66\4\uffff\40\66\2\uffff\u0110\66\u0372\uffff\32"+
            "\66\1\uffff\131\66\14\uffff\u00d6\66\32\uffff\14\66\5\uffff"+
            "\7\66\12\uffff\2\66\10\uffff\1\66\3\uffff\40\66\1\uffff\126"+
            "\66\2\uffff\2\66\2\uffff\143\66\5\uffff\50\66\4\uffff\136\66"+
            "\1\uffff\50\66\70\uffff\57\66\1\uffff\44\66\14\uffff\56\66\1"+
            "\uffff\u0080\66\1\uffff\u1ab6\66\12\uffff\u51e6\66\132\uffff"+
            "\u048d\66\3\uffff\67\66\u0739\uffff\u2ba4\66\u215c\uffff\u012e"+
            "\66\2\uffff\73\66\u0095\uffff\7\66\14\uffff\5\66\5\uffff\32"+
            "\66\1\uffff\5\66\1\uffff\1\66\1\uffff\2\66\1\uffff\2\66\1\uffff"+
            "\154\66\41\uffff\u016b\66\22\uffff\100\66\2\uffff\66\66\50\uffff"+
            "\16\66\2\uffff\20\66\20\uffff\4\66\14\uffff\5\66\20\uffff\2"+
            "\66\2\uffff\12\66\1\uffff\5\66\6\uffff\10\66\1\uffff\4\66\4"+
            "\uffff\5\66\1\uffff\u0087\66\4\uffff\7\66\2\uffff\61\66\1\uffff"+
            "\1\66\2\uffff\1\66\1\uffff\32\66\1\uffff\1\66\1\uffff\1\66\2"+
            "\uffff\1\66\2\uffff\133\66\3\uffff\6\66\2\uffff\6\66\2\uffff"+
            "\6\66\2\uffff\3\66\3\uffff\3\66\1\uffff\3\66\1\uffff\7\66",
            "\2\66\1\63\1\62\3\66\2\uffff\1\67\1\66\1\uffff\1\66\1\uffff"+
            "\1\66\12\60\1\uffff\1\66\1\uffff\1\66\1\uffff\1\70\1\66\32\57"+
            "\1\uffff\1\64\2\uffff\1\61\1\uffff\32\56\1\uffff\1\66\44\uffff"+
            "\7\66\1\uffff\2\66\1\uffff\1\66\1\uffff\1\66\1\uffff\4\66\1"+
            "\uffff\3\66\1\uffff\2\66\1\uffff\u017b\66\31\uffff\162\66\4"+
            "\uffff\14\66\16\uffff\5\66\11\uffff\1\66\21\uffff\130\66\5\uffff"+
            "\23\66\12\uffff\1\66\3\uffff\1\66\7\uffff\5\66\1\uffff\1\66"+
            "\1\uffff\24\66\1\uffff\54\66\1\uffff\54\66\4\uffff\u0087\66"+
            "\1\uffff\107\66\1\uffff\46\66\2\uffff\2\66\6\uffff\20\66\41"+
            "\uffff\46\66\2\uffff\7\66\1\uffff\47\66\1\uffff\2\66\6\uffff"+
            "\21\66\1\uffff\27\66\1\uffff\12\66\13\uffff\33\66\5\uffff\5"+
            "\66\27\uffff\12\66\5\uffff\1\66\3\uffff\1\66\1\uffff\32\66\5"+
            "\uffff\31\66\7\uffff\175\66\1\uffff\60\66\2\uffff\73\66\2\uffff"+
            "\3\66\60\uffff\62\66\u014f\uffff\71\66\2\uffff\22\66\2\uffff"+
            "\5\66\3\uffff\31\66\20\uffff\3\66\1\uffff\10\66\2\uffff\2\66"+
            "\2\uffff\26\66\1\uffff\7\66\1\uffff\1\66\3\uffff\4\66\2\uffff"+
            "\11\66\2\uffff\2\66\2\uffff\3\66\11\uffff\1\66\4\uffff\2\66"+
            "\1\uffff\5\66\2\uffff\25\66\6\uffff\3\66\1\uffff\6\66\4\uffff"+
            "\2\66\2\uffff\26\66\1\uffff\7\66\1\uffff\2\66\1\uffff\2\66\1"+
            "\uffff\2\66\2\uffff\1\66\1\uffff\5\66\4\uffff\2\66\2\uffff\3"+
            "\66\13\uffff\4\66\1\uffff\1\66\7\uffff\17\66\14\uffff\3\66\1"+
            "\uffff\11\66\1\uffff\3\66\1\uffff\26\66\1\uffff\7\66\1\uffff"+
            "\2\66\1\uffff\5\66\2\uffff\12\66\1\uffff\3\66\1\uffff\3\66\2"+
            "\uffff\1\66\17\uffff\4\66\2\uffff\12\66\1\uffff\1\66\17\uffff"+
            "\3\66\1\uffff\10\66\2\uffff\2\66\2\uffff\26\66\1\uffff\7\66"+
            "\1\uffff\2\66\1\uffff\5\66\2\uffff\10\66\3\uffff\2\66\2\uffff"+
            "\3\66\10\uffff\2\66\4\uffff\2\66\1\uffff\3\66\4\uffff\14\66"+
            "\20\uffff\2\66\1\uffff\6\66\3\uffff\3\66\1\uffff\4\66\3\uffff"+
            "\2\66\1\uffff\1\66\1\uffff\2\66\3\uffff\2\66\3\uffff\3\66\3"+
            "\uffff\10\66\1\uffff\3\66\4\uffff\5\66\3\uffff\3\66\1\uffff"+
            "\4\66\11\uffff\1\66\17\uffff\24\66\6\uffff\3\66\1\uffff\10\66"+
            "\1\uffff\3\66\1\uffff\27\66\1\uffff\12\66\1\uffff\5\66\4\uffff"+
            "\7\66\1\uffff\3\66\1\uffff\4\66\7\uffff\2\66\11\uffff\2\66\4"+
            "\uffff\12\66\22\uffff\2\66\1\uffff\10\66\1\uffff\3\66\1\uffff"+
            "\27\66\1\uffff\12\66\1\uffff\5\66\2\uffff\11\66\1\uffff\3\66"+
            "\1\uffff\4\66\7\uffff\2\66\7\uffff\1\66\1\uffff\2\66\4\uffff"+
            "\12\66\22\uffff\2\66\1\uffff\10\66\1\uffff\3\66\1\uffff\27\66"+
            "\1\uffff\20\66\4\uffff\6\66\2\uffff\3\66\1\uffff\4\66\11\uffff"+
            "\1\66\10\uffff\2\66\4\uffff\12\66\22\uffff\2\66\1\uffff\22\66"+
            "\3\uffff\30\66\1\uffff\11\66\1\uffff\1\66\2\uffff\7\66\3\uffff"+
            "\1\66\4\uffff\6\66\1\uffff\1\66\1\uffff\10\66\22\uffff\3\66"+
            "\14\uffff\72\66\4\uffff\35\66\45\uffff\2\66\1\uffff\1\66\2\uffff"+
            "\2\66\1\uffff\1\66\2\uffff\1\66\6\uffff\4\66\1\uffff\7\66\1"+
            "\uffff\3\66\1\uffff\1\66\1\uffff\1\66\2\uffff\2\66\1\uffff\15"+
            "\66\1\uffff\3\66\2\uffff\5\66\1\uffff\1\66\1\uffff\6\66\2\uffff"+
            "\12\66\2\uffff\2\66\42\uffff\72\66\4\uffff\12\66\1\uffff\42"+
            "\66\6\uffff\33\66\4\uffff\10\66\1\uffff\44\66\1\uffff\17\66"+
            "\2\uffff\1\66\60\uffff\42\66\1\uffff\5\66\1\uffff\2\66\1\uffff"+
            "\7\66\3\uffff\4\66\6\uffff\32\66\106\uffff\46\66\12\uffff\51"+
            "\66\2\uffff\1\66\4\uffff\132\66\5\uffff\104\66\5\uffff\122\66"+
            "\6\uffff\7\66\1\uffff\77\66\1\uffff\1\66\1\uffff\4\66\2\uffff"+
            "\7\66\1\uffff\1\66\1\uffff\4\66\2\uffff\47\66\1\uffff\1\66\1"+
            "\uffff\4\66\2\uffff\37\66\1\uffff\1\66\1\uffff\4\66\2\uffff"+
            "\7\66\1\uffff\1\66\1\uffff\4\66\2\uffff\7\66\1\uffff\7\66\1"+
            "\uffff\27\66\1\uffff\37\66\1\uffff\1\66\1\uffff\4\66\2\uffff"+
            "\7\66\1\uffff\47\66\1\uffff\23\66\6\uffff\34\66\43\uffff\125"+
            "\66\14\uffff\u0276\66\12\uffff\32\66\5\uffff\121\66\17\uffff"+
            "\15\66\1\uffff\7\66\13\uffff\27\66\11\uffff\24\66\14\uffff\15"+
            "\66\1\uffff\3\66\1\uffff\2\66\14\uffff\64\66\2\uffff\50\66\2"+
            "\uffff\12\66\6\uffff\12\66\6\uffff\16\66\2\uffff\12\66\6\uffff"+
            "\130\66\10\uffff\52\66\126\uffff\35\66\3\uffff\14\66\4\uffff"+
            "\14\66\4\uffff\1\66\3\uffff\52\66\2\uffff\5\66\153\uffff\40"+
            "\66\u0300\uffff\154\66\u0094\uffff\u009c\66\4\uffff\132\66\6"+
            "\uffff\26\66\2\uffff\6\66\2\uffff\46\66\2\uffff\6\66\2\uffff"+
            "\10\66\1\uffff\1\66\1\uffff\1\66\1\uffff\1\66\1\uffff\37\66"+
            "\2\uffff\65\66\1\uffff\7\66\1\uffff\1\66\3\uffff\3\66\1\uffff"+
            "\7\66\3\uffff\4\66\2\uffff\6\66\4\uffff\15\66\5\uffff\3\66\1"+
            "\uffff\7\66\23\uffff\10\66\10\uffff\10\66\10\uffff\11\66\2\uffff"+
            "\12\66\2\uffff\16\66\2\uffff\1\66\30\uffff\2\66\2\uffff\11\66"+
            "\2\uffff\16\66\23\uffff\22\66\36\uffff\33\66\25\uffff\74\66"+
            "\1\uffff\17\66\7\uffff\61\66\14\uffff\u0199\66\2\uffff\u0089"+
            "\66\2\uffff\33\66\57\uffff\47\66\31\uffff\13\66\25\uffff\u01b8"+
            "\66\1\uffff\145\66\2\uffff\22\66\16\uffff\2\66\137\uffff\4\66"+
            "\1\uffff\4\66\2\uffff\34\66\1\uffff\43\66\1\uffff\1\66\1\uffff"+
            "\4\66\3\uffff\1\66\1\uffff\7\66\2\uffff\7\66\16\uffff\37\66"+
            "\3\uffff\30\66\1\uffff\16\66\21\uffff\26\66\12\uffff\u0193\66"+
            "\26\uffff\77\66\4\uffff\40\66\2\uffff\u0110\66\u0372\uffff\32"+
            "\66\1\uffff\131\66\14\uffff\u00d6\66\32\uffff\14\66\5\uffff"+
            "\7\66\12\uffff\2\66\10\uffff\1\66\3\uffff\40\66\1\uffff\126"+
            "\66\2\uffff\2\66\2\uffff\143\66\5\uffff\50\66\4\uffff\136\66"+
            "\1\uffff\50\66\70\uffff\57\66\1\uffff\44\66\14\uffff\56\66\1"+
            "\uffff\u0080\66\1\uffff\u1ab6\66\12\uffff\u51e6\66\132\uffff"+
            "\u048d\66\3\uffff\67\66\u0739\uffff\u2ba4\66\u215c\uffff\u012e"+
            "\66\2\uffff\73\66\u0095\uffff\7\66\14\uffff\5\66\5\uffff\32"+
            "\66\1\uffff\5\66\1\uffff\1\66\1\uffff\2\66\1\uffff\2\66\1\uffff"+
            "\154\66\41\uffff\u016b\66\22\uffff\100\66\2\uffff\66\66\50\uffff"+
            "\16\66\2\uffff\20\66\20\uffff\4\66\14\uffff\5\66\20\uffff\2"+
            "\66\2\uffff\12\66\1\uffff\5\66\6\uffff\10\66\1\uffff\4\66\4"+
            "\uffff\5\66\1\uffff\u0087\66\4\uffff\7\66\2\uffff\61\66\1\uffff"+
            "\1\66\2\uffff\1\66\1\uffff\32\66\1\uffff\1\66\1\uffff\1\66\2"+
            "\uffff\1\66\2\uffff\133\66\3\uffff\6\66\2\uffff\6\66\2\uffff"+
            "\6\66\2\uffff\3\66\3\uffff\3\66\1\uffff\3\66\1\uffff\7\66",
            "\2\66\1\63\1\62\3\66\2\uffff\1\67\1\66\1\uffff\1\66\1\uffff"+
            "\1\66\12\60\1\uffff\1\66\1\uffff\1\66\1\uffff\1\70\1\66\32\57"+
            "\1\uffff\1\64\2\uffff\1\61\1\uffff\32\56\1\uffff\1\66\44\uffff"+
            "\7\66\1\uffff\2\66\1\uffff\1\66\1\uffff\1\66\1\uffff\4\66\1"+
            "\uffff\3\66\1\uffff\2\66\1\uffff\u017b\66\31\uffff\162\66\4"+
            "\uffff\14\66\16\uffff\5\66\11\uffff\1\66\21\uffff\130\66\5\uffff"+
            "\23\66\12\uffff\1\66\3\uffff\1\66\7\uffff\5\66\1\uffff\1\66"+
            "\1\uffff\24\66\1\uffff\54\66\1\uffff\54\66\4\uffff\u0087\66"+
            "\1\uffff\107\66\1\uffff\46\66\2\uffff\2\66\6\uffff\20\66\41"+
            "\uffff\46\66\2\uffff\7\66\1\uffff\47\66\1\uffff\2\66\6\uffff"+
            "\21\66\1\uffff\27\66\1\uffff\12\66\13\uffff\33\66\5\uffff\5"+
            "\66\27\uffff\12\66\5\uffff\1\66\3\uffff\1\66\1\uffff\32\66\5"+
            "\uffff\31\66\7\uffff\175\66\1\uffff\60\66\2\uffff\73\66\2\uffff"+
            "\3\66\60\uffff\62\66\u014f\uffff\71\66\2\uffff\22\66\2\uffff"+
            "\5\66\3\uffff\31\66\20\uffff\3\66\1\uffff\10\66\2\uffff\2\66"+
            "\2\uffff\26\66\1\uffff\7\66\1\uffff\1\66\3\uffff\4\66\2\uffff"+
            "\11\66\2\uffff\2\66\2\uffff\3\66\11\uffff\1\66\4\uffff\2\66"+
            "\1\uffff\5\66\2\uffff\25\66\6\uffff\3\66\1\uffff\6\66\4\uffff"+
            "\2\66\2\uffff\26\66\1\uffff\7\66\1\uffff\2\66\1\uffff\2\66\1"+
            "\uffff\2\66\2\uffff\1\66\1\uffff\5\66\4\uffff\2\66\2\uffff\3"+
            "\66\13\uffff\4\66\1\uffff\1\66\7\uffff\17\66\14\uffff\3\66\1"+
            "\uffff\11\66\1\uffff\3\66\1\uffff\26\66\1\uffff\7\66\1\uffff"+
            "\2\66\1\uffff\5\66\2\uffff\12\66\1\uffff\3\66\1\uffff\3\66\2"+
            "\uffff\1\66\17\uffff\4\66\2\uffff\12\66\1\uffff\1\66\17\uffff"+
            "\3\66\1\uffff\10\66\2\uffff\2\66\2\uffff\26\66\1\uffff\7\66"+
            "\1\uffff\2\66\1\uffff\5\66\2\uffff\10\66\3\uffff\2\66\2\uffff"+
            "\3\66\10\uffff\2\66\4\uffff\2\66\1\uffff\3\66\4\uffff\14\66"+
            "\20\uffff\2\66\1\uffff\6\66\3\uffff\3\66\1\uffff\4\66\3\uffff"+
            "\2\66\1\uffff\1\66\1\uffff\2\66\3\uffff\2\66\3\uffff\3\66\3"+
            "\uffff\10\66\1\uffff\3\66\4\uffff\5\66\3\uffff\3\66\1\uffff"+
            "\4\66\11\uffff\1\66\17\uffff\24\66\6\uffff\3\66\1\uffff\10\66"+
            "\1\uffff\3\66\1\uffff\27\66\1\uffff\12\66\1\uffff\5\66\4\uffff"+
            "\7\66\1\uffff\3\66\1\uffff\4\66\7\uffff\2\66\11\uffff\2\66\4"+
            "\uffff\12\66\22\uffff\2\66\1\uffff\10\66\1\uffff\3\66\1\uffff"+
            "\27\66\1\uffff\12\66\1\uffff\5\66\2\uffff\11\66\1\uffff\3\66"+
            "\1\uffff\4\66\7\uffff\2\66\7\uffff\1\66\1\uffff\2\66\4\uffff"+
            "\12\66\22\uffff\2\66\1\uffff\10\66\1\uffff\3\66\1\uffff\27\66"+
            "\1\uffff\20\66\4\uffff\6\66\2\uffff\3\66\1\uffff\4\66\11\uffff"+
            "\1\66\10\uffff\2\66\4\uffff\12\66\22\uffff\2\66\1\uffff\22\66"+
            "\3\uffff\30\66\1\uffff\11\66\1\uffff\1\66\2\uffff\7\66\3\uffff"+
            "\1\66\4\uffff\6\66\1\uffff\1\66\1\uffff\10\66\22\uffff\3\66"+
            "\14\uffff\72\66\4\uffff\35\66\45\uffff\2\66\1\uffff\1\66\2\uffff"+
            "\2\66\1\uffff\1\66\2\uffff\1\66\6\uffff\4\66\1\uffff\7\66\1"+
            "\uffff\3\66\1\uffff\1\66\1\uffff\1\66\2\uffff\2\66\1\uffff\15"+
            "\66\1\uffff\3\66\2\uffff\5\66\1\uffff\1\66\1\uffff\6\66\2\uffff"+
            "\12\66\2\uffff\2\66\42\uffff\72\66\4\uffff\12\66\1\uffff\42"+
            "\66\6\uffff\33\66\4\uffff\10\66\1\uffff\44\66\1\uffff\17\66"+
            "\2\uffff\1\66\60\uffff\42\66\1\uffff\5\66\1\uffff\2\66\1\uffff"+
            "\7\66\3\uffff\4\66\6\uffff\32\66\106\uffff\46\66\12\uffff\51"+
            "\66\2\uffff\1\66\4\uffff\132\66\5\uffff\104\66\5\uffff\122\66"+
            "\6\uffff\7\66\1\uffff\77\66\1\uffff\1\66\1\uffff\4\66\2\uffff"+
            "\7\66\1\uffff\1\66\1\uffff\4\66\2\uffff\47\66\1\uffff\1\66\1"+
            "\uffff\4\66\2\uffff\37\66\1\uffff\1\66\1\uffff\4\66\2\uffff"+
            "\7\66\1\uffff\1\66\1\uffff\4\66\2\uffff\7\66\1\uffff\7\66\1"+
            "\uffff\27\66\1\uffff\37\66\1\uffff\1\66\1\uffff\4\66\2\uffff"+
            "\7\66\1\uffff\47\66\1\uffff\23\66\6\uffff\34\66\43\uffff\125"+
            "\66\14\uffff\u0276\66\12\uffff\32\66\5\uffff\121\66\17\uffff"+
            "\15\66\1\uffff\7\66\13\uffff\27\66\11\uffff\24\66\14\uffff\15"+
            "\66\1\uffff\3\66\1\uffff\2\66\14\uffff\64\66\2\uffff\50\66\2"+
            "\uffff\12\66\6\uffff\12\66\6\uffff\16\66\2\uffff\12\66\6\uffff"+
            "\130\66\10\uffff\52\66\126\uffff\35\66\3\uffff\14\66\4\uffff"+
            "\14\66\4\uffff\1\66\3\uffff\52\66\2\uffff\5\66\153\uffff\40"+
            "\66\u0300\uffff\154\66\u0094\uffff\u009c\66\4\uffff\132\66\6"+
            "\uffff\26\66\2\uffff\6\66\2\uffff\46\66\2\uffff\6\66\2\uffff"+
            "\10\66\1\uffff\1\66\1\uffff\1\66\1\uffff\1\66\1\uffff\37\66"+
            "\2\uffff\65\66\1\uffff\7\66\1\uffff\1\66\3\uffff\3\66\1\uffff"+
            "\7\66\3\uffff\4\66\2\uffff\6\66\4\uffff\15\66\5\uffff\3\66\1"+
            "\uffff\7\66\23\uffff\10\66\10\uffff\10\66\10\uffff\11\66\2\uffff"+
            "\12\66\2\uffff\16\66\2\uffff\1\66\30\uffff\2\66\2\uffff\11\66"+
            "\2\uffff\16\66\23\uffff\22\66\36\uffff\33\66\25\uffff\74\66"+
            "\1\uffff\17\66\7\uffff\61\66\14\uffff\u0199\66\2\uffff\u0089"+
            "\66\2\uffff\33\66\57\uffff\47\66\31\uffff\13\66\25\uffff\u01b8"+
            "\66\1\uffff\145\66\2\uffff\22\66\16\uffff\2\66\137\uffff\4\66"+
            "\1\uffff\4\66\2\uffff\34\66\1\uffff\43\66\1\uffff\1\66\1\uffff"+
            "\4\66\3\uffff\1\66\1\uffff\7\66\2\uffff\7\66\16\uffff\37\66"+
            "\3\uffff\30\66\1\uffff\16\66\21\uffff\26\66\12\uffff\u0193\66"+
            "\26\uffff\77\66\4\uffff\40\66\2\uffff\u0110\66\u0372\uffff\32"+
            "\66\1\uffff\131\66\14\uffff\u00d6\66\32\uffff\14\66\5\uffff"+
            "\7\66\12\uffff\2\66\10\uffff\1\66\3\uffff\40\66\1\uffff\126"+
            "\66\2\uffff\2\66\2\uffff\143\66\5\uffff\50\66\4\uffff\136\66"+
            "\1\uffff\50\66\70\uffff\57\66\1\uffff\44\66\14\uffff\56\66\1"+
            "\uffff\u0080\66\1\uffff\u1ab6\66\12\uffff\u51e6\66\132\uffff"+
            "\u048d\66\3\uffff\67\66\u0739\uffff\u2ba4\66\u215c\uffff\u012e"+
            "\66\2\uffff\73\66\u0095\uffff\7\66\14\uffff\5\66\5\uffff\32"+
            "\66\1\uffff\5\66\1\uffff\1\66\1\uffff\2\66\1\uffff\2\66\1\uffff"+
            "\154\66\41\uffff\u016b\66\22\uffff\100\66\2\uffff\66\66\50\uffff"+
            "\16\66\2\uffff\20\66\20\uffff\4\66\14\uffff\5\66\20\uffff\2"+
            "\66\2\uffff\12\66\1\uffff\5\66\6\uffff\10\66\1\uffff\4\66\4"+
            "\uffff\5\66\1\uffff\u0087\66\4\uffff\7\66\2\uffff\61\66\1\uffff"+
            "\1\66\2\uffff\1\66\1\uffff\32\66\1\uffff\1\66\1\uffff\1\66\2"+
            "\uffff\1\66\2\uffff\133\66\3\uffff\6\66\2\uffff\6\66\2\uffff"+
            "\6\66\2\uffff\3\66\3\uffff\3\66\1\uffff\3\66\1\uffff\7\66",
            "\165\122\1\121\uff8a\122",
            "",
            "\7\66\2\uffff\1\67\1\66\1\uffff\1\66\1\uffff\13\66\1\uffff"+
            "\1\66\1\uffff\1\66\1\uffff\1\70\33\66\1\uffff\1\110\2\uffff"+
            "\1\66\1\uffff\32\66\1\uffff\1\66\44\uffff\7\66\1\uffff\2\66"+
            "\1\uffff\1\66\1\uffff\1\66\1\uffff\4\66\1\uffff\3\66\1\uffff"+
            "\2\66\1\uffff\u017b\66\31\uffff\162\66\4\uffff\14\66\16\uffff"+
            "\5\66\11\uffff\1\66\21\uffff\130\66\5\uffff\23\66\12\uffff\1"+
            "\66\3\uffff\1\66\7\uffff\5\66\1\uffff\1\66\1\uffff\24\66\1\uffff"+
            "\54\66\1\uffff\54\66\4\uffff\u0087\66\1\uffff\107\66\1\uffff"+
            "\46\66\2\uffff\2\66\6\uffff\20\66\41\uffff\46\66\2\uffff\7\66"+
            "\1\uffff\47\66\1\uffff\2\66\6\uffff\21\66\1\uffff\27\66\1\uffff"+
            "\12\66\13\uffff\33\66\5\uffff\5\66\27\uffff\12\66\5\uffff\1"+
            "\66\3\uffff\1\66\1\uffff\32\66\5\uffff\31\66\7\uffff\175\66"+
            "\1\uffff\60\66\2\uffff\73\66\2\uffff\3\66\60\uffff\62\66\u014f"+
            "\uffff\71\66\2\uffff\22\66\2\uffff\5\66\3\uffff\31\66\20\uffff"+
            "\3\66\1\uffff\10\66\2\uffff\2\66\2\uffff\26\66\1\uffff\7\66"+
            "\1\uffff\1\66\3\uffff\4\66\2\uffff\11\66\2\uffff\2\66\2\uffff"+
            "\3\66\11\uffff\1\66\4\uffff\2\66\1\uffff\5\66\2\uffff\25\66"+
            "\6\uffff\3\66\1\uffff\6\66\4\uffff\2\66\2\uffff\26\66\1\uffff"+
            "\7\66\1\uffff\2\66\1\uffff\2\66\1\uffff\2\66\2\uffff\1\66\1"+
            "\uffff\5\66\4\uffff\2\66\2\uffff\3\66\13\uffff\4\66\1\uffff"+
            "\1\66\7\uffff\17\66\14\uffff\3\66\1\uffff\11\66\1\uffff\3\66"+
            "\1\uffff\26\66\1\uffff\7\66\1\uffff\2\66\1\uffff\5\66\2\uffff"+
            "\12\66\1\uffff\3\66\1\uffff\3\66\2\uffff\1\66\17\uffff\4\66"+
            "\2\uffff\12\66\1\uffff\1\66\17\uffff\3\66\1\uffff\10\66\2\uffff"+
            "\2\66\2\uffff\26\66\1\uffff\7\66\1\uffff\2\66\1\uffff\5\66\2"+
            "\uffff\10\66\3\uffff\2\66\2\uffff\3\66\10\uffff\2\66\4\uffff"+
            "\2\66\1\uffff\3\66\4\uffff\14\66\20\uffff\2\66\1\uffff\6\66"+
            "\3\uffff\3\66\1\uffff\4\66\3\uffff\2\66\1\uffff\1\66\1\uffff"+
            "\2\66\3\uffff\2\66\3\uffff\3\66\3\uffff\10\66\1\uffff\3\66\4"+
            "\uffff\5\66\3\uffff\3\66\1\uffff\4\66\11\uffff\1\66\17\uffff"+
            "\24\66\6\uffff\3\66\1\uffff\10\66\1\uffff\3\66\1\uffff\27\66"+
            "\1\uffff\12\66\1\uffff\5\66\4\uffff\7\66\1\uffff\3\66\1\uffff"+
            "\4\66\7\uffff\2\66\11\uffff\2\66\4\uffff\12\66\22\uffff\2\66"+
            "\1\uffff\10\66\1\uffff\3\66\1\uffff\27\66\1\uffff\12\66\1\uffff"+
            "\5\66\2\uffff\11\66\1\uffff\3\66\1\uffff\4\66\7\uffff\2\66\7"+
            "\uffff\1\66\1\uffff\2\66\4\uffff\12\66\22\uffff\2\66\1\uffff"+
            "\10\66\1\uffff\3\66\1\uffff\27\66\1\uffff\20\66\4\uffff\6\66"+
            "\2\uffff\3\66\1\uffff\4\66\11\uffff\1\66\10\uffff\2\66\4\uffff"+
            "\12\66\22\uffff\2\66\1\uffff\22\66\3\uffff\30\66\1\uffff\11"+
            "\66\1\uffff\1\66\2\uffff\7\66\3\uffff\1\66\4\uffff\6\66\1\uffff"+
            "\1\66\1\uffff\10\66\22\uffff\3\66\14\uffff\72\66\4\uffff\35"+
            "\66\45\uffff\2\66\1\uffff\1\66\2\uffff\2\66\1\uffff\1\66\2\uffff"+
            "\1\66\6\uffff\4\66\1\uffff\7\66\1\uffff\3\66\1\uffff\1\66\1"+
            "\uffff\1\66\2\uffff\2\66\1\uffff\15\66\1\uffff\3\66\2\uffff"+
            "\5\66\1\uffff\1\66\1\uffff\6\66\2\uffff\12\66\2\uffff\2\66\42"+
            "\uffff\72\66\4\uffff\12\66\1\uffff\42\66\6\uffff\33\66\4\uffff"+
            "\10\66\1\uffff\44\66\1\uffff\17\66\2\uffff\1\66\60\uffff\42"+
            "\66\1\uffff\5\66\1\uffff\2\66\1\uffff\7\66\3\uffff\4\66\6\uffff"+
            "\32\66\106\uffff\46\66\12\uffff\51\66\2\uffff\1\66\4\uffff\132"+
            "\66\5\uffff\104\66\5\uffff\122\66\6\uffff\7\66\1\uffff\77\66"+
            "\1\uffff\1\66\1\uffff\4\66\2\uffff\7\66\1\uffff\1\66\1\uffff"+
            "\4\66\2\uffff\47\66\1\uffff\1\66\1\uffff\4\66\2\uffff\37\66"+
            "\1\uffff\1\66\1\uffff\4\66\2\uffff\7\66\1\uffff\1\66\1\uffff"+
            "\4\66\2\uffff\7\66\1\uffff\7\66\1\uffff\27\66\1\uffff\37\66"+
            "\1\uffff\1\66\1\uffff\4\66\2\uffff\7\66\1\uffff\47\66\1\uffff"+
            "\23\66\6\uffff\34\66\43\uffff\125\66\14\uffff\u0276\66\12\uffff"+
            "\32\66\5\uffff\121\66\17\uffff\15\66\1\uffff\7\66\13\uffff\27"+
            "\66\11\uffff\24\66\14\uffff\15\66\1\uffff\3\66\1\uffff\2\66"+
            "\14\uffff\64\66\2\uffff\50\66\2\uffff\12\66\6\uffff\12\66\6"+
            "\uffff\16\66\2\uffff\12\66\6\uffff\130\66\10\uffff\52\66\126"+
            "\uffff\35\66\3\uffff\14\66\4\uffff\14\66\4\uffff\1\66\3\uffff"+
            "\52\66\2\uffff\5\66\153\uffff\40\66\u0300\uffff\154\66\u0094"+
            "\uffff\u009c\66\4\uffff\132\66\6\uffff\26\66\2\uffff\6\66\2"+
            "\uffff\46\66\2\uffff\6\66\2\uffff\10\66\1\uffff\1\66\1\uffff"+
            "\1\66\1\uffff\1\66\1\uffff\37\66\2\uffff\65\66\1\uffff\7\66"+
            "\1\uffff\1\66\3\uffff\3\66\1\uffff\7\66\3\uffff\4\66\2\uffff"+
            "\6\66\4\uffff\15\66\5\uffff\3\66\1\uffff\7\66\23\uffff\10\66"+
            "\10\uffff\10\66\10\uffff\11\66\2\uffff\12\66\2\uffff\16\66\2"+
            "\uffff\1\66\30\uffff\2\66\2\uffff\11\66\2\uffff\16\66\23\uffff"+
            "\22\66\36\uffff\33\66\25\uffff\74\66\1\uffff\17\66\7\uffff\61"+
            "\66\14\uffff\u0199\66\2\uffff\u0089\66\2\uffff\33\66\57\uffff"+
            "\47\66\31\uffff\13\66\25\uffff\u01b8\66\1\uffff\145\66\2\uffff"+
            "\22\66\16\uffff\2\66\137\uffff\4\66\1\uffff\4\66\2\uffff\34"+
            "\66\1\uffff\43\66\1\uffff\1\66\1\uffff\4\66\3\uffff\1\66\1\uffff"+
            "\7\66\2\uffff\7\66\16\uffff\37\66\3\uffff\30\66\1\uffff\16\66"+
            "\21\uffff\26\66\12\uffff\u0193\66\26\uffff\77\66\4\uffff\40"+
            "\66\2\uffff\u0110\66\u0372\uffff\32\66\1\uffff\131\66\14\uffff"+
            "\u00d6\66\32\uffff\14\66\5\uffff\7\66\12\uffff\2\66\10\uffff"+
            "\1\66\3\uffff\40\66\1\uffff\126\66\2\uffff\2\66\2\uffff\143"+
            "\66\5\uffff\50\66\4\uffff\136\66\1\uffff\50\66\70\uffff\57\66"+
            "\1\uffff\44\66\14\uffff\56\66\1\uffff\u0080\66\1\uffff\u1ab6"+
            "\66\12\uffff\u51e6\66\132\uffff\u048d\66\3\uffff\67\66\u0739"+
            "\uffff\u2ba4\66\u215c\uffff\u012e\66\2\uffff\73\66\u0095\uffff"+
            "\7\66\14\uffff\5\66\5\uffff\32\66\1\uffff\5\66\1\uffff\1\66"+
            "\1\uffff\2\66\1\uffff\2\66\1\uffff\154\66\41\uffff\u016b\66"+
            "\22\uffff\100\66\2\uffff\66\66\50\uffff\16\66\2\uffff\20\66"+
            "\20\uffff\4\66\14\uffff\5\66\20\uffff\2\66\2\uffff\12\66\1\uffff"+
            "\5\66\6\uffff\10\66\1\uffff\4\66\4\uffff\5\66\1\uffff\u0087"+
            "\66\4\uffff\7\66\2\uffff\61\66\1\uffff\1\66\2\uffff\1\66\1\uffff"+
            "\32\66\1\uffff\1\66\1\uffff\1\66\2\uffff\1\66\2\uffff\133\66"+
            "\3\uffff\6\66\2\uffff\6\66\2\uffff\6\66\2\uffff\3\66\3\uffff"+
            "\3\66\1\uffff\3\66\1\uffff\7\66",
            "\7\102\2\uffff\1\103\1\102\1\uffff\1\102\1\uffff\13\102\1\uffff"+
            "\1\102\1\uffff\1\102\1\uffff\1\70\33\102\1\uffff\1\101\2\uffff"+
            "\1\102\1\uffff\32\102\1\uffff\1\102\44\uffff\7\102\1\uffff\2"+
            "\102\1\uffff\1\102\1\uffff\1\102\1\uffff\4\102\1\uffff\3\102"+
            "\1\uffff\2\102\1\uffff\u017b\102\31\uffff\162\102\4\uffff\14"+
            "\102\16\uffff\5\102\11\uffff\1\102\21\uffff\130\102\5\uffff"+
            "\23\102\12\uffff\1\102\3\uffff\1\102\7\uffff\5\102\1\uffff\1"+
            "\102\1\uffff\24\102\1\uffff\54\102\1\uffff\54\102\4\uffff\u0087"+
            "\102\1\uffff\107\102\1\uffff\46\102\2\uffff\2\102\6\uffff\20"+
            "\102\41\uffff\46\102\2\uffff\7\102\1\uffff\47\102\1\uffff\2"+
            "\102\6\uffff\21\102\1\uffff\27\102\1\uffff\12\102\13\uffff\33"+
            "\102\5\uffff\5\102\27\uffff\12\102\5\uffff\1\102\3\uffff\1\102"+
            "\1\uffff\32\102\5\uffff\31\102\7\uffff\175\102\1\uffff\60\102"+
            "\2\uffff\73\102\2\uffff\3\102\60\uffff\62\102\u014f\uffff\71"+
            "\102\2\uffff\22\102\2\uffff\5\102\3\uffff\31\102\20\uffff\3"+
            "\102\1\uffff\10\102\2\uffff\2\102\2\uffff\26\102\1\uffff\7\102"+
            "\1\uffff\1\102\3\uffff\4\102\2\uffff\11\102\2\uffff\2\102\2"+
            "\uffff\3\102\11\uffff\1\102\4\uffff\2\102\1\uffff\5\102\2\uffff"+
            "\25\102\6\uffff\3\102\1\uffff\6\102\4\uffff\2\102\2\uffff\26"+
            "\102\1\uffff\7\102\1\uffff\2\102\1\uffff\2\102\1\uffff\2\102"+
            "\2\uffff\1\102\1\uffff\5\102\4\uffff\2\102\2\uffff\3\102\13"+
            "\uffff\4\102\1\uffff\1\102\7\uffff\17\102\14\uffff\3\102\1\uffff"+
            "\11\102\1\uffff\3\102\1\uffff\26\102\1\uffff\7\102\1\uffff\2"+
            "\102\1\uffff\5\102\2\uffff\12\102\1\uffff\3\102\1\uffff\3\102"+
            "\2\uffff\1\102\17\uffff\4\102\2\uffff\12\102\1\uffff\1\102\17"+
            "\uffff\3\102\1\uffff\10\102\2\uffff\2\102\2\uffff\26\102\1\uffff"+
            "\7\102\1\uffff\2\102\1\uffff\5\102\2\uffff\10\102\3\uffff\2"+
            "\102\2\uffff\3\102\10\uffff\2\102\4\uffff\2\102\1\uffff\3\102"+
            "\4\uffff\14\102\20\uffff\2\102\1\uffff\6\102\3\uffff\3\102\1"+
            "\uffff\4\102\3\uffff\2\102\1\uffff\1\102\1\uffff\2\102\3\uffff"+
            "\2\102\3\uffff\3\102\3\uffff\10\102\1\uffff\3\102\4\uffff\5"+
            "\102\3\uffff\3\102\1\uffff\4\102\11\uffff\1\102\17\uffff\24"+
            "\102\6\uffff\3\102\1\uffff\10\102\1\uffff\3\102\1\uffff\27\102"+
            "\1\uffff\12\102\1\uffff\5\102\4\uffff\7\102\1\uffff\3\102\1"+
            "\uffff\4\102\7\uffff\2\102\11\uffff\2\102\4\uffff\12\102\22"+
            "\uffff\2\102\1\uffff\10\102\1\uffff\3\102\1\uffff\27\102\1\uffff"+
            "\12\102\1\uffff\5\102\2\uffff\11\102\1\uffff\3\102\1\uffff\4"+
            "\102\7\uffff\2\102\7\uffff\1\102\1\uffff\2\102\4\uffff\12\102"+
            "\22\uffff\2\102\1\uffff\10\102\1\uffff\3\102\1\uffff\27\102"+
            "\1\uffff\20\102\4\uffff\6\102\2\uffff\3\102\1\uffff\4\102\11"+
            "\uffff\1\102\10\uffff\2\102\4\uffff\12\102\22\uffff\2\102\1"+
            "\uffff\22\102\3\uffff\30\102\1\uffff\11\102\1\uffff\1\102\2"+
            "\uffff\7\102\3\uffff\1\102\4\uffff\6\102\1\uffff\1\102\1\uffff"+
            "\10\102\22\uffff\3\102\14\uffff\72\102\4\uffff\35\102\45\uffff"+
            "\2\102\1\uffff\1\102\2\uffff\2\102\1\uffff\1\102\2\uffff\1\102"+
            "\6\uffff\4\102\1\uffff\7\102\1\uffff\3\102\1\uffff\1\102\1\uffff"+
            "\1\102\2\uffff\2\102\1\uffff\15\102\1\uffff\3\102\2\uffff\5"+
            "\102\1\uffff\1\102\1\uffff\6\102\2\uffff\12\102\2\uffff\2\102"+
            "\42\uffff\72\102\4\uffff\12\102\1\uffff\42\102\6\uffff\33\102"+
            "\4\uffff\10\102\1\uffff\44\102\1\uffff\17\102\2\uffff\1\102"+
            "\60\uffff\42\102\1\uffff\5\102\1\uffff\2\102\1\uffff\7\102\3"+
            "\uffff\4\102\6\uffff\32\102\106\uffff\46\102\12\uffff\51\102"+
            "\2\uffff\1\102\4\uffff\132\102\5\uffff\104\102\5\uffff\122\102"+
            "\6\uffff\7\102\1\uffff\77\102\1\uffff\1\102\1\uffff\4\102\2"+
            "\uffff\7\102\1\uffff\1\102\1\uffff\4\102\2\uffff\47\102\1\uffff"+
            "\1\102\1\uffff\4\102\2\uffff\37\102\1\uffff\1\102\1\uffff\4"+
            "\102\2\uffff\7\102\1\uffff\1\102\1\uffff\4\102\2\uffff\7\102"+
            "\1\uffff\7\102\1\uffff\27\102\1\uffff\37\102\1\uffff\1\102\1"+
            "\uffff\4\102\2\uffff\7\102\1\uffff\47\102\1\uffff\23\102\6\uffff"+
            "\34\102\43\uffff\125\102\14\uffff\u0276\102\12\uffff\32\102"+
            "\5\uffff\121\102\17\uffff\15\102\1\uffff\7\102\13\uffff\27\102"+
            "\11\uffff\24\102\14\uffff\15\102\1\uffff\3\102\1\uffff\2\102"+
            "\14\uffff\64\102\2\uffff\50\102\2\uffff\12\102\6\uffff\12\102"+
            "\6\uffff\16\102\2\uffff\12\102\6\uffff\130\102\10\uffff\52\102"+
            "\126\uffff\35\102\3\uffff\14\102\4\uffff\14\102\4\uffff\1\102"+
            "\3\uffff\52\102\2\uffff\5\102\153\uffff\40\102\u0300\uffff\154"+
            "\102\u0094\uffff\u009c\102\4\uffff\132\102\6\uffff\26\102\2"+
            "\uffff\6\102\2\uffff\46\102\2\uffff\6\102\2\uffff\10\102\1\uffff"+
            "\1\102\1\uffff\1\102\1\uffff\1\102\1\uffff\37\102\2\uffff\65"+
            "\102\1\uffff\7\102\1\uffff\1\102\3\uffff\3\102\1\uffff\7\102"+
            "\3\uffff\4\102\2\uffff\6\102\4\uffff\15\102\5\uffff\3\102\1"+
            "\uffff\7\102\23\uffff\10\102\10\uffff\10\102\10\uffff\11\102"+
            "\2\uffff\12\102\2\uffff\16\102\2\uffff\1\102\30\uffff\2\102"+
            "\2\uffff\11\102\2\uffff\16\102\23\uffff\22\102\36\uffff\33\102"+
            "\25\uffff\74\102\1\uffff\17\102\7\uffff\61\102\14\uffff\u0199"+
            "\102\2\uffff\u0089\102\2\uffff\33\102\57\uffff\47\102\31\uffff"+
            "\13\102\25\uffff\u01b8\102\1\uffff\145\102\2\uffff\22\102\16"+
            "\uffff\2\102\137\uffff\4\102\1\uffff\4\102\2\uffff\34\102\1"+
            "\uffff\43\102\1\uffff\1\102\1\uffff\4\102\3\uffff\1\102\1\uffff"+
            "\7\102\2\uffff\7\102\16\uffff\37\102\3\uffff\30\102\1\uffff"+
            "\16\102\21\uffff\26\102\12\uffff\u0193\102\26\uffff\77\102\4"+
            "\uffff\40\102\2\uffff\u0110\102\u0372\uffff\32\102\1\uffff\131"+
            "\102\14\uffff\u00d6\102\32\uffff\14\102\5\uffff\7\102\12\uffff"+
            "\2\102\10\uffff\1\102\3\uffff\40\102\1\uffff\126\102\2\uffff"+
            "\2\102\2\uffff\143\102\5\uffff\50\102\4\uffff\136\102\1\uffff"+
            "\50\102\70\uffff\57\102\1\uffff\44\102\14\uffff\56\102\1\uffff"+
            "\u0080\102\1\uffff\u1ab6\102\12\uffff\u51e6\102\132\uffff\u048d"+
            "\102\3\uffff\67\102\u0739\uffff\u2ba4\102\u215c\uffff\u012e"+
            "\102\2\uffff\73\102\u0095\uffff\7\102\14\uffff\5\102\5\uffff"+
            "\32\102\1\uffff\5\102\1\uffff\1\102\1\uffff\2\102\1\uffff\2"+
            "\102\1\uffff\154\102\41\uffff\u016b\102\22\uffff\100\102\2\uffff"+
            "\66\102\50\uffff\16\102\2\uffff\20\102\20\uffff\4\102\14\uffff"+
            "\5\102\20\uffff\2\102\2\uffff\12\102\1\uffff\5\102\6\uffff\10"+
            "\102\1\uffff\4\102\4\uffff\5\102\1\uffff\u0087\102\4\uffff\7"+
            "\102\2\uffff\61\102\1\uffff\1\102\2\uffff\1\102\1\uffff\32\102"+
            "\1\uffff\1\102\1\uffff\1\102\2\uffff\1\102\2\uffff\133\102\3"+
            "\uffff\6\102\2\uffff\6\102\2\uffff\6\102\2\uffff\3\102\3\uffff"+
            "\3\102\1\uffff\3\102\1\uffff\7\102",
            "",
            "\2\66\1\63\1\62\3\66\2\uffff\1\67\1\66\1\uffff\1\66\1\uffff"+
            "\1\66\12\60\1\uffff\1\66\1\uffff\1\66\1\uffff\1\70\1\66\3\57"+
            "\1\125\26\57\1\uffff\1\64\2\uffff\1\61\1\uffff\3\56\1\124\26"+
            "\56\1\uffff\1\66\44\uffff\7\66\1\uffff\2\66\1\uffff\1\66\1\uffff"+
            "\1\66\1\uffff\4\66\1\uffff\3\66\1\uffff\2\66\1\uffff\u017b\66"+
            "\31\uffff\162\66\4\uffff\14\66\16\uffff\5\66\11\uffff\1\66\21"+
            "\uffff\130\66\5\uffff\23\66\12\uffff\1\66\3\uffff\1\66\7\uffff"+
            "\5\66\1\uffff\1\66\1\uffff\24\66\1\uffff\54\66\1\uffff\54\66"+
            "\4\uffff\u0087\66\1\uffff\107\66\1\uffff\46\66\2\uffff\2\66"+
            "\6\uffff\20\66\41\uffff\46\66\2\uffff\7\66\1\uffff\47\66\1\uffff"+
            "\2\66\6\uffff\21\66\1\uffff\27\66\1\uffff\12\66\13\uffff\33"+
            "\66\5\uffff\5\66\27\uffff\12\66\5\uffff\1\66\3\uffff\1\66\1"+
            "\uffff\32\66\5\uffff\31\66\7\uffff\175\66\1\uffff\60\66\2\uffff"+
            "\73\66\2\uffff\3\66\60\uffff\62\66\u014f\uffff\71\66\2\uffff"+
            "\22\66\2\uffff\5\66\3\uffff\31\66\20\uffff\3\66\1\uffff\10\66"+
            "\2\uffff\2\66\2\uffff\26\66\1\uffff\7\66\1\uffff\1\66\3\uffff"+
            "\4\66\2\uffff\11\66\2\uffff\2\66\2\uffff\3\66\11\uffff\1\66"+
            "\4\uffff\2\66\1\uffff\5\66\2\uffff\25\66\6\uffff\3\66\1\uffff"+
            "\6\66\4\uffff\2\66\2\uffff\26\66\1\uffff\7\66\1\uffff\2\66\1"+
            "\uffff\2\66\1\uffff\2\66\2\uffff\1\66\1\uffff\5\66\4\uffff\2"+
            "\66\2\uffff\3\66\13\uffff\4\66\1\uffff\1\66\7\uffff\17\66\14"+
            "\uffff\3\66\1\uffff\11\66\1\uffff\3\66\1\uffff\26\66\1\uffff"+
            "\7\66\1\uffff\2\66\1\uffff\5\66\2\uffff\12\66\1\uffff\3\66\1"+
            "\uffff\3\66\2\uffff\1\66\17\uffff\4\66\2\uffff\12\66\1\uffff"+
            "\1\66\17\uffff\3\66\1\uffff\10\66\2\uffff\2\66\2\uffff\26\66"+
            "\1\uffff\7\66\1\uffff\2\66\1\uffff\5\66\2\uffff\10\66\3\uffff"+
            "\2\66\2\uffff\3\66\10\uffff\2\66\4\uffff\2\66\1\uffff\3\66\4"+
            "\uffff\14\66\20\uffff\2\66\1\uffff\6\66\3\uffff\3\66\1\uffff"+
            "\4\66\3\uffff\2\66\1\uffff\1\66\1\uffff\2\66\3\uffff\2\66\3"+
            "\uffff\3\66\3\uffff\10\66\1\uffff\3\66\4\uffff\5\66\3\uffff"+
            "\3\66\1\uffff\4\66\11\uffff\1\66\17\uffff\24\66\6\uffff\3\66"+
            "\1\uffff\10\66\1\uffff\3\66\1\uffff\27\66\1\uffff\12\66\1\uffff"+
            "\5\66\4\uffff\7\66\1\uffff\3\66\1\uffff\4\66\7\uffff\2\66\11"+
            "\uffff\2\66\4\uffff\12\66\22\uffff\2\66\1\uffff\10\66\1\uffff"+
            "\3\66\1\uffff\27\66\1\uffff\12\66\1\uffff\5\66\2\uffff\11\66"+
            "\1\uffff\3\66\1\uffff\4\66\7\uffff\2\66\7\uffff\1\66\1\uffff"+
            "\2\66\4\uffff\12\66\22\uffff\2\66\1\uffff\10\66\1\uffff\3\66"+
            "\1\uffff\27\66\1\uffff\20\66\4\uffff\6\66\2\uffff\3\66\1\uffff"+
            "\4\66\11\uffff\1\66\10\uffff\2\66\4\uffff\12\66\22\uffff\2\66"+
            "\1\uffff\22\66\3\uffff\30\66\1\uffff\11\66\1\uffff\1\66\2\uffff"+
            "\7\66\3\uffff\1\66\4\uffff\6\66\1\uffff\1\66\1\uffff\10\66\22"+
            "\uffff\3\66\14\uffff\72\66\4\uffff\35\66\45\uffff\2\66\1\uffff"+
            "\1\66\2\uffff\2\66\1\uffff\1\66\2\uffff\1\66\6\uffff\4\66\1"+
            "\uffff\7\66\1\uffff\3\66\1\uffff\1\66\1\uffff\1\66\2\uffff\2"+
            "\66\1\uffff\15\66\1\uffff\3\66\2\uffff\5\66\1\uffff\1\66\1\uffff"+
            "\6\66\2\uffff\12\66\2\uffff\2\66\42\uffff\72\66\4\uffff\12\66"+
            "\1\uffff\42\66\6\uffff\33\66\4\uffff\10\66\1\uffff\44\66\1\uffff"+
            "\17\66\2\uffff\1\66\60\uffff\42\66\1\uffff\5\66\1\uffff\2\66"+
            "\1\uffff\7\66\3\uffff\4\66\6\uffff\32\66\106\uffff\46\66\12"+
            "\uffff\51\66\2\uffff\1\66\4\uffff\132\66\5\uffff\104\66\5\uffff"+
            "\122\66\6\uffff\7\66\1\uffff\77\66\1\uffff\1\66\1\uffff\4\66"+
            "\2\uffff\7\66\1\uffff\1\66\1\uffff\4\66\2\uffff\47\66\1\uffff"+
            "\1\66\1\uffff\4\66\2\uffff\37\66\1\uffff\1\66\1\uffff\4\66\2"+
            "\uffff\7\66\1\uffff\1\66\1\uffff\4\66\2\uffff\7\66\1\uffff\7"+
            "\66\1\uffff\27\66\1\uffff\37\66\1\uffff\1\66\1\uffff\4\66\2"+
            "\uffff\7\66\1\uffff\47\66\1\uffff\23\66\6\uffff\34\66\43\uffff"+
            "\125\66\14\uffff\u0276\66\12\uffff\32\66\5\uffff\121\66\17\uffff"+
            "\15\66\1\uffff\7\66\13\uffff\27\66\11\uffff\24\66\14\uffff\15"+
            "\66\1\uffff\3\66\1\uffff\2\66\14\uffff\64\66\2\uffff\50\66\2"+
            "\uffff\12\66\6\uffff\12\66\6\uffff\16\66\2\uffff\12\66\6\uffff"+
            "\130\66\10\uffff\52\66\126\uffff\35\66\3\uffff\14\66\4\uffff"+
            "\14\66\4\uffff\1\66\3\uffff\52\66\2\uffff\5\66\153\uffff\40"+
            "\66\u0300\uffff\154\66\u0094\uffff\u009c\66\4\uffff\132\66\6"+
            "\uffff\26\66\2\uffff\6\66\2\uffff\46\66\2\uffff\6\66\2\uffff"+
            "\10\66\1\uffff\1\66\1\uffff\1\66\1\uffff\1\66\1\uffff\37\66"+
            "\2\uffff\65\66\1\uffff\7\66\1\uffff\1\66\3\uffff\3\66\1\uffff"+
            "\7\66\3\uffff\4\66\2\uffff\6\66\4\uffff\15\66\5\uffff\3\66\1"+
            "\uffff\7\66\23\uffff\10\66\10\uffff\10\66\10\uffff\11\66\2\uffff"+
            "\12\66\2\uffff\16\66\2\uffff\1\66\30\uffff\2\66\2\uffff\11\66"+
            "\2\uffff\16\66\23\uffff\22\66\36\uffff\33\66\25\uffff\74\66"+
            "\1\uffff\17\66\7\uffff\61\66\14\uffff\u0199\66\2\uffff\u0089"+
            "\66\2\uffff\33\66\57\uffff\47\66\31\uffff\13\66\25\uffff\u01b8"+
            "\66\1\uffff\145\66\2\uffff\22\66\16\uffff\2\66\137\uffff\4\66"+
            "\1\uffff\4\66\2\uffff\34\66\1\uffff\43\66\1\uffff\1\66\1\uffff"+
            "\4\66\3\uffff\1\66\1\uffff\7\66\2\uffff\7\66\16\uffff\37\66"+
            "\3\uffff\30\66\1\uffff\16\66\21\uffff\26\66\12\uffff\u0193\66"+
            "\26\uffff\77\66\4\uffff\40\66\2\uffff\u0110\66\u0372\uffff\32"+
            "\66\1\uffff\131\66\14\uffff\u00d6\66\32\uffff\14\66\5\uffff"+
            "\7\66\12\uffff\2\66\10\uffff\1\66\3\uffff\40\66\1\uffff\126"+
            "\66\2\uffff\2\66\2\uffff\143\66\5\uffff\50\66\4\uffff\136\66"+
            "\1\uffff\50\66\70\uffff\57\66\1\uffff\44\66\14\uffff\56\66\1"+
            "\uffff\u0080\66\1\uffff\u1ab6\66\12\uffff\u51e6\66\132\uffff"+
            "\u048d\66\3\uffff\67\66\u0739\uffff\u2ba4\66\u215c\uffff\u012e"+
            "\66\2\uffff\73\66\u0095\uffff\7\66\14\uffff\5\66\5\uffff\32"+
            "\66\1\uffff\5\66\1\uffff\1\66\1\uffff\2\66\1\uffff\2\66\1\uffff"+
            "\154\66\41\uffff\u016b\66\22\uffff\100\66\2\uffff\66\66\50\uffff"+
            "\16\66\2\uffff\20\66\20\uffff\4\66\14\uffff\5\66\20\uffff\2"+
            "\66\2\uffff\12\66\1\uffff\5\66\6\uffff\10\66\1\uffff\4\66\4"+
            "\uffff\5\66\1\uffff\u0087\66\4\uffff\7\66\2\uffff\61\66\1\uffff"+
            "\1\66\2\uffff\1\66\1\uffff\32\66\1\uffff\1\66\1\uffff\1\66\2"+
            "\uffff\1\66\2\uffff\133\66\3\uffff\6\66\2\uffff\6\66\2\uffff"+
            "\6\66\2\uffff\3\66\3\uffff\3\66\1\uffff\3\66\1\uffff\7\66",
            "\2\66\1\63\1\62\3\66\2\uffff\1\67\1\66\1\uffff\1\66\1\uffff"+
            "\1\66\12\60\1\uffff\1\66\1\uffff\1\66\1\uffff\1\70\1\66\3\57"+
            "\1\125\26\57\1\uffff\1\64\2\uffff\1\61\1\uffff\3\56\1\124\26"+
            "\56\1\uffff\1\66\44\uffff\7\66\1\uffff\2\66\1\uffff\1\66\1\uffff"+
            "\1\66\1\uffff\4\66\1\uffff\3\66\1\uffff\2\66\1\uffff\u017b\66"+
            "\31\uffff\162\66\4\uffff\14\66\16\uffff\5\66\11\uffff\1\66\21"+
            "\uffff\130\66\5\uffff\23\66\12\uffff\1\66\3\uffff\1\66\7\uffff"+
            "\5\66\1\uffff\1\66\1\uffff\24\66\1\uffff\54\66\1\uffff\54\66"+
            "\4\uffff\u0087\66\1\uffff\107\66\1\uffff\46\66\2\uffff\2\66"+
            "\6\uffff\20\66\41\uffff\46\66\2\uffff\7\66\1\uffff\47\66\1\uffff"+
            "\2\66\6\uffff\21\66\1\uffff\27\66\1\uffff\12\66\13\uffff\33"+
            "\66\5\uffff\5\66\27\uffff\12\66\5\uffff\1\66\3\uffff\1\66\1"+
            "\uffff\32\66\5\uffff\31\66\7\uffff\175\66\1\uffff\60\66\2\uffff"+
            "\73\66\2\uffff\3\66\60\uffff\62\66\u014f\uffff\71\66\2\uffff"+
            "\22\66\2\uffff\5\66\3\uffff\31\66\20\uffff\3\66\1\uffff\10\66"+
            "\2\uffff\2\66\2\uffff\26\66\1\uffff\7\66\1\uffff\1\66\3\uffff"+
            "\4\66\2\uffff\11\66\2\uffff\2\66\2\uffff\3\66\11\uffff\1\66"+
            "\4\uffff\2\66\1\uffff\5\66\2\uffff\25\66\6\uffff\3\66\1\uffff"+
            "\6\66\4\uffff\2\66\2\uffff\26\66\1\uffff\7\66\1\uffff\2\66\1"+
            "\uffff\2\66\1\uffff\2\66\2\uffff\1\66\1\uffff\5\66\4\uffff\2"+
            "\66\2\uffff\3\66\13\uffff\4\66\1\uffff\1\66\7\uffff\17\66\14"+
            "\uffff\3\66\1\uffff\11\66\1\uffff\3\66\1\uffff\26\66\1\uffff"+
            "\7\66\1\uffff\2\66\1\uffff\5\66\2\uffff\12\66\1\uffff\3\66\1"+
            "\uffff\3\66\2\uffff\1\66\17\uffff\4\66\2\uffff\12\66\1\uffff"+
            "\1\66\17\uffff\3\66\1\uffff\10\66\2\uffff\2\66\2\uffff\26\66"+
            "\1\uffff\7\66\1\uffff\2\66\1\uffff\5\66\2\uffff\10\66\3\uffff"+
            "\2\66\2\uffff\3\66\10\uffff\2\66\4\uffff\2\66\1\uffff\3\66\4"+
            "\uffff\14\66\20\uffff\2\66\1\uffff\6\66\3\uffff\3\66\1\uffff"+
            "\4\66\3\uffff\2\66\1\uffff\1\66\1\uffff\2\66\3\uffff\2\66\3"+
            "\uffff\3\66\3\uffff\10\66\1\uffff\3\66\4\uffff\5\66\3\uffff"+
            "\3\66\1\uffff\4\66\11\uffff\1\66\17\uffff\24\66\6\uffff\3\66"+
            "\1\uffff\10\66\1\uffff\3\66\1\uffff\27\66\1\uffff\12\66\1\uffff"+
            "\5\66\4\uffff\7\66\1\uffff\3\66\1\uffff\4\66\7\uffff\2\66\11"+
            "\uffff\2\66\4\uffff\12\66\22\uffff\2\66\1\uffff\10\66\1\uffff"+
            "\3\66\1\uffff\27\66\1\uffff\12\66\1\uffff\5\66\2\uffff\11\66"+
            "\1\uffff\3\66\1\uffff\4\66\7\uffff\2\66\7\uffff\1\66\1\uffff"+
            "\2\66\4\uffff\12\66\22\uffff\2\66\1\uffff\10\66\1\uffff\3\66"+
            "\1\uffff\27\66\1\uffff\20\66\4\uffff\6\66\2\uffff\3\66\1\uffff"+
            "\4\66\11\uffff\1\66\10\uffff\2\66\4\uffff\12\66\22\uffff\2\66"+
            "\1\uffff\22\66\3\uffff\30\66\1\uffff\11\66\1\uffff\1\66\2\uffff"+
            "\7\66\3\uffff\1\66\4\uffff\6\66\1\uffff\1\66\1\uffff\10\66\22"+
            "\uffff\3\66\14\uffff\72\66\4\uffff\35\66\45\uffff\2\66\1\uffff"+
            "\1\66\2\uffff\2\66\1\uffff\1\66\2\uffff\1\66\6\uffff\4\66\1"+
            "\uffff\7\66\1\uffff\3\66\1\uffff\1\66\1\uffff\1\66\2\uffff\2"+
            "\66\1\uffff\15\66\1\uffff\3\66\2\uffff\5\66\1\uffff\1\66\1\uffff"+
            "\6\66\2\uffff\12\66\2\uffff\2\66\42\uffff\72\66\4\uffff\12\66"+
            "\1\uffff\42\66\6\uffff\33\66\4\uffff\10\66\1\uffff\44\66\1\uffff"+
            "\17\66\2\uffff\1\66\60\uffff\42\66\1\uffff\5\66\1\uffff\2\66"+
            "\1\uffff\7\66\3\uffff\4\66\6\uffff\32\66\106\uffff\46\66\12"+
            "\uffff\51\66\2\uffff\1\66\4\uffff\132\66\5\uffff\104\66\5\uffff"+
            "\122\66\6\uffff\7\66\1\uffff\77\66\1\uffff\1\66\1\uffff\4\66"+
            "\2\uffff\7\66\1\uffff\1\66\1\uffff\4\66\2\uffff\47\66\1\uffff"+
            "\1\66\1\uffff\4\66\2\uffff\37\66\1\uffff\1\66\1\uffff\4\66\2"+
            "\uffff\7\66\1\uffff\1\66\1\uffff\4\66\2\uffff\7\66\1\uffff\7"+
            "\66\1\uffff\27\66\1\uffff\37\66\1\uffff\1\66\1\uffff\4\66\2"+
            "\uffff\7\66\1\uffff\47\66\1\uffff\23\66\6\uffff\34\66\43\uffff"+
            "\125\66\14\uffff\u0276\66\12\uffff\32\66\5\uffff\121\66\17\uffff"+
            "\15\66\1\uffff\7\66\13\uffff\27\66\11\uffff\24\66\14\uffff\15"+
            "\66\1\uffff\3\66\1\uffff\2\66\14\uffff\64\66\2\uffff\50\66\2"+
            "\uffff\12\66\6\uffff\12\66\6\uffff\16\66\2\uffff\12\66\6\uffff"+
            "\130\66\10\uffff\52\66\126\uffff\35\66\3\uffff\14\66\4\uffff"+
            "\14\66\4\uffff\1\66\3\uffff\52\66\2\uffff\5\66\153\uffff\40"+
            "\66\u0300\uffff\154\66\u0094\uffff\u009c\66\4\uffff\132\66\6"+
            "\uffff\26\66\2\uffff\6\66\2\uffff\46\66\2\uffff\6\66\2\uffff"+
            "\10\66\1\uffff\1\66\1\uffff\1\66\1\uffff\1\66\1\uffff\37\66"+
            "\2\uffff\65\66\1\uffff\7\66\1\uffff\1\66\3\uffff\3\66\1\uffff"+
            "\7\66\3\uffff\4\66\2\uffff\6\66\4\uffff\15\66\5\uffff\3\66\1"+
            "\uffff\7\66\23\uffff\10\66\10\uffff\10\66\10\uffff\11\66\2\uffff"+
            "\12\66\2\uffff\16\66\2\uffff\1\66\30\uffff\2\66\2\uffff\11\66"+
            "\2\uffff\16\66\23\uffff\22\66\36\uffff\33\66\25\uffff\74\66"+
            "\1\uffff\17\66\7\uffff\61\66\14\uffff\u0199\66\2\uffff\u0089"+
            "\66\2\uffff\33\66\57\uffff\47\66\31\uffff\13\66\25\uffff\u01b8"+
            "\66\1\uffff\145\66\2\uffff\22\66\16\uffff\2\66\137\uffff\4\66"+
            "\1\uffff\4\66\2\uffff\34\66\1\uffff\43\66\1\uffff\1\66\1\uffff"+
            "\4\66\3\uffff\1\66\1\uffff\7\66\2\uffff\7\66\16\uffff\37\66"+
            "\3\uffff\30\66\1\uffff\16\66\21\uffff\26\66\12\uffff\u0193\66"+
            "\26\uffff\77\66\4\uffff\40\66\2\uffff\u0110\66\u0372\uffff\32"+
            "\66\1\uffff\131\66\14\uffff\u00d6\66\32\uffff\14\66\5\uffff"+
            "\7\66\12\uffff\2\66\10\uffff\1\66\3\uffff\40\66\1\uffff\126"+
            "\66\2\uffff\2\66\2\uffff\143\66\5\uffff\50\66\4\uffff\136\66"+
            "\1\uffff\50\66\70\uffff\57\66\1\uffff\44\66\14\uffff\56\66\1"+
            "\uffff\u0080\66\1\uffff\u1ab6\66\12\uffff\u51e6\66\132\uffff"+
            "\u048d\66\3\uffff\67\66\u0739\uffff\u2ba4\66\u215c\uffff\u012e"+
            "\66\2\uffff\73\66\u0095\uffff\7\66\14\uffff\5\66\5\uffff\32"+
            "\66\1\uffff\5\66\1\uffff\1\66\1\uffff\2\66\1\uffff\2\66\1\uffff"+
            "\154\66\41\uffff\u016b\66\22\uffff\100\66\2\uffff\66\66\50\uffff"+
            "\16\66\2\uffff\20\66\20\uffff\4\66\14\uffff\5\66\20\uffff\2"+
            "\66\2\uffff\12\66\1\uffff\5\66\6\uffff\10\66\1\uffff\4\66\4"+
            "\uffff\5\66\1\uffff\u0087\66\4\uffff\7\66\2\uffff\61\66\1\uffff"+
            "\1\66\2\uffff\1\66\1\uffff\32\66\1\uffff\1\66\1\uffff\1\66\2"+
            "\uffff\1\66\2\uffff\133\66\3\uffff\6\66\2\uffff\6\66\2\uffff"+
            "\6\66\2\uffff\3\66\3\uffff\3\66\1\uffff\3\66\1\uffff\7\66",
            "\2\66\1\63\1\62\3\66\2\uffff\1\67\1\66\1\uffff\1\66\1\uffff"+
            "\1\66\12\60\1\uffff\1\66\1\uffff\1\66\1\uffff\1\70\1\66\23\57"+
            "\1\127\6\57\1\uffff\1\64\2\uffff\1\61\1\uffff\23\56\1\126\6"+
            "\56\1\uffff\1\66\44\uffff\7\66\1\uffff\2\66\1\uffff\1\66\1\uffff"+
            "\1\66\1\uffff\4\66\1\uffff\3\66\1\uffff\2\66\1\uffff\u017b\66"+
            "\31\uffff\162\66\4\uffff\14\66\16\uffff\5\66\11\uffff\1\66\21"+
            "\uffff\130\66\5\uffff\23\66\12\uffff\1\66\3\uffff\1\66\7\uffff"+
            "\5\66\1\uffff\1\66\1\uffff\24\66\1\uffff\54\66\1\uffff\54\66"+
            "\4\uffff\u0087\66\1\uffff\107\66\1\uffff\46\66\2\uffff\2\66"+
            "\6\uffff\20\66\41\uffff\46\66\2\uffff\7\66\1\uffff\47\66\1\uffff"+
            "\2\66\6\uffff\21\66\1\uffff\27\66\1\uffff\12\66\13\uffff\33"+
            "\66\5\uffff\5\66\27\uffff\12\66\5\uffff\1\66\3\uffff\1\66\1"+
            "\uffff\32\66\5\uffff\31\66\7\uffff\175\66\1\uffff\60\66\2\uffff"+
            "\73\66\2\uffff\3\66\60\uffff\62\66\u014f\uffff\71\66\2\uffff"+
            "\22\66\2\uffff\5\66\3\uffff\31\66\20\uffff\3\66\1\uffff\10\66"+
            "\2\uffff\2\66\2\uffff\26\66\1\uffff\7\66\1\uffff\1\66\3\uffff"+
            "\4\66\2\uffff\11\66\2\uffff\2\66\2\uffff\3\66\11\uffff\1\66"+
            "\4\uffff\2\66\1\uffff\5\66\2\uffff\25\66\6\uffff\3\66\1\uffff"+
            "\6\66\4\uffff\2\66\2\uffff\26\66\1\uffff\7\66\1\uffff\2\66\1"+
            "\uffff\2\66\1\uffff\2\66\2\uffff\1\66\1\uffff\5\66\4\uffff\2"+
            "\66\2\uffff\3\66\13\uffff\4\66\1\uffff\1\66\7\uffff\17\66\14"+
            "\uffff\3\66\1\uffff\11\66\1\uffff\3\66\1\uffff\26\66\1\uffff"+
            "\7\66\1\uffff\2\66\1\uffff\5\66\2\uffff\12\66\1\uffff\3\66\1"+
            "\uffff\3\66\2\uffff\1\66\17\uffff\4\66\2\uffff\12\66\1\uffff"+
            "\1\66\17\uffff\3\66\1\uffff\10\66\2\uffff\2\66\2\uffff\26\66"+
            "\1\uffff\7\66\1\uffff\2\66\1\uffff\5\66\2\uffff\10\66\3\uffff"+
            "\2\66\2\uffff\3\66\10\uffff\2\66\4\uffff\2\66\1\uffff\3\66\4"+
            "\uffff\14\66\20\uffff\2\66\1\uffff\6\66\3\uffff\3\66\1\uffff"+
            "\4\66\3\uffff\2\66\1\uffff\1\66\1\uffff\2\66\3\uffff\2\66\3"+
            "\uffff\3\66\3\uffff\10\66\1\uffff\3\66\4\uffff\5\66\3\uffff"+
            "\3\66\1\uffff\4\66\11\uffff\1\66\17\uffff\24\66\6\uffff\3\66"+
            "\1\uffff\10\66\1\uffff\3\66\1\uffff\27\66\1\uffff\12\66\1\uffff"+
            "\5\66\4\uffff\7\66\1\uffff\3\66\1\uffff\4\66\7\uffff\2\66\11"+
            "\uffff\2\66\4\uffff\12\66\22\uffff\2\66\1\uffff\10\66\1\uffff"+
            "\3\66\1\uffff\27\66\1\uffff\12\66\1\uffff\5\66\2\uffff\11\66"+
            "\1\uffff\3\66\1\uffff\4\66\7\uffff\2\66\7\uffff\1\66\1\uffff"+
            "\2\66\4\uffff\12\66\22\uffff\2\66\1\uffff\10\66\1\uffff\3\66"+
            "\1\uffff\27\66\1\uffff\20\66\4\uffff\6\66\2\uffff\3\66\1\uffff"+
            "\4\66\11\uffff\1\66\10\uffff\2\66\4\uffff\12\66\22\uffff\2\66"+
            "\1\uffff\22\66\3\uffff\30\66\1\uffff\11\66\1\uffff\1\66\2\uffff"+
            "\7\66\3\uffff\1\66\4\uffff\6\66\1\uffff\1\66\1\uffff\10\66\22"+
            "\uffff\3\66\14\uffff\72\66\4\uffff\35\66\45\uffff\2\66\1\uffff"+
            "\1\66\2\uffff\2\66\1\uffff\1\66\2\uffff\1\66\6\uffff\4\66\1"+
            "\uffff\7\66\1\uffff\3\66\1\uffff\1\66\1\uffff\1\66\2\uffff\2"+
            "\66\1\uffff\15\66\1\uffff\3\66\2\uffff\5\66\1\uffff\1\66\1\uffff"+
            "\6\66\2\uffff\12\66\2\uffff\2\66\42\uffff\72\66\4\uffff\12\66"+
            "\1\uffff\42\66\6\uffff\33\66\4\uffff\10\66\1\uffff\44\66\1\uffff"+
            "\17\66\2\uffff\1\66\60\uffff\42\66\1\uffff\5\66\1\uffff\2\66"+
            "\1\uffff\7\66\3\uffff\4\66\6\uffff\32\66\106\uffff\46\66\12"+
            "\uffff\51\66\2\uffff\1\66\4\uffff\132\66\5\uffff\104\66\5\uffff"+
            "\122\66\6\uffff\7\66\1\uffff\77\66\1\uffff\1\66\1\uffff\4\66"+
            "\2\uffff\7\66\1\uffff\1\66\1\uffff\4\66\2\uffff\47\66\1\uffff"+
            "\1\66\1\uffff\4\66\2\uffff\37\66\1\uffff\1\66\1\uffff\4\66\2"+
            "\uffff\7\66\1\uffff\1\66\1\uffff\4\66\2\uffff\7\66\1\uffff\7"+
            "\66\1\uffff\27\66\1\uffff\37\66\1\uffff\1\66\1\uffff\4\66\2"+
            "\uffff\7\66\1\uffff\47\66\1\uffff\23\66\6\uffff\34\66\43\uffff"+
            "\125\66\14\uffff\u0276\66\12\uffff\32\66\5\uffff\121\66\17\uffff"+
            "\15\66\1\uffff\7\66\13\uffff\27\66\11\uffff\24\66\14\uffff\15"+
            "\66\1\uffff\3\66\1\uffff\2\66\14\uffff\64\66\2\uffff\50\66\2"+
            "\uffff\12\66\6\uffff\12\66\6\uffff\16\66\2\uffff\12\66\6\uffff"+
            "\130\66\10\uffff\52\66\126\uffff\35\66\3\uffff\14\66\4\uffff"+
            "\14\66\4\uffff\1\66\3\uffff\52\66\2\uffff\5\66\153\uffff\40"+
            "\66\u0300\uffff\154\66\u0094\uffff\u009c\66\4\uffff\132\66\6"+
            "\uffff\26\66\2\uffff\6\66\2\uffff\46\66\2\uffff\6\66\2\uffff"+
            "\10\66\1\uffff\1\66\1\uffff\1\66\1\uffff\1\66\1\uffff\37\66"+
            "\2\uffff\65\66\1\uffff\7\66\1\uffff\1\66\3\uffff\3\66\1\uffff"+
            "\7\66\3\uffff\4\66\2\uffff\6\66\4\uffff\15\66\5\uffff\3\66\1"+
            "\uffff\7\66\23\uffff\10\66\10\uffff\10\66\10\uffff\11\66\2\uffff"+
            "\12\66\2\uffff\16\66\2\uffff\1\66\30\uffff\2\66\2\uffff\11\66"+
            "\2\uffff\16\66\23\uffff\22\66\36\uffff\33\66\25\uffff\74\66"+
            "\1\uffff\17\66\7\uffff\61\66\14\uffff\u0199\66\2\uffff\u0089"+
            "\66\2\uffff\33\66\57\uffff\47\66\31\uffff\13\66\25\uffff\u01b8"+
            "\66\1\uffff\145\66\2\uffff\22\66\16\uffff\2\66\137\uffff\4\66"+
            "\1\uffff\4\66\2\uffff\34\66\1\uffff\43\66\1\uffff\1\66\1\uffff"+
            "\4\66\3\uffff\1\66\1\uffff\7\66\2\uffff\7\66\16\uffff\37\66"+
            "\3\uffff\30\66\1\uffff\16\66\21\uffff\26\66\12\uffff\u0193\66"+
            "\26\uffff\77\66\4\uffff\40\66\2\uffff\u0110\66\u0372\uffff\32"+
            "\66\1\uffff\131\66\14\uffff\u00d6\66\32\uffff\14\66\5\uffff"+
            "\7\66\12\uffff\2\66\10\uffff\1\66\3\uffff\40\66\1\uffff\126"+
            "\66\2\uffff\2\66\2\uffff\143\66\5\uffff\50\66\4\uffff\136\66"+
            "\1\uffff\50\66\70\uffff\57\66\1\uffff\44\66\14\uffff\56\66\1"+
            "\uffff\u0080\66\1\uffff\u1ab6\66\12\uffff\u51e6\66\132\uffff"+
            "\u048d\66\3\uffff\67\66\u0739\uffff\u2ba4\66\u215c\uffff\u012e"+
            "\66\2\uffff\73\66\u0095\uffff\7\66\14\uffff\5\66\5\uffff\32"+
            "\66\1\uffff\5\66\1\uffff\1\66\1\uffff\2\66\1\uffff\2\66\1\uffff"+
            "\154\66\41\uffff\u016b\66\22\uffff\100\66\2\uffff\66\66\50\uffff"+
            "\16\66\2\uffff\20\66\20\uffff\4\66\14\uffff\5\66\20\uffff\2"+
            "\66\2\uffff\12\66\1\uffff\5\66\6\uffff\10\66\1\uffff\4\66\4"+
            "\uffff\5\66\1\uffff\u0087\66\4\uffff\7\66\2\uffff\61\66\1\uffff"+
            "\1\66\2\uffff\1\66\1\uffff\32\66\1\uffff\1\66\1\uffff\1\66\2"+
            "\uffff\1\66\2\uffff\133\66\3\uffff\6\66\2\uffff\6\66\2\uffff"+
            "\6\66\2\uffff\3\66\3\uffff\3\66\1\uffff\3\66\1\uffff\7\66",
            "\2\66\1\63\1\62\3\66\2\uffff\1\67\1\66\1\uffff\1\66\1\uffff"+
            "\1\66\12\60\1\uffff\1\66\1\uffff\1\66\1\uffff\1\70\1\66\23\57"+
            "\1\127\6\57\1\uffff\1\64\2\uffff\1\61\1\uffff\23\56\1\126\6"+
            "\56\1\uffff\1\66\44\uffff\7\66\1\uffff\2\66\1\uffff\1\66\1\uffff"+
            "\1\66\1\uffff\4\66\1\uffff\3\66\1\uffff\2\66\1\uffff\u017b\66"+
            "\31\uffff\162\66\4\uffff\14\66\16\uffff\5\66\11\uffff\1\66\21"+
            "\uffff\130\66\5\uffff\23\66\12\uffff\1\66\3\uffff\1\66\7\uffff"+
            "\5\66\1\uffff\1\66\1\uffff\24\66\1\uffff\54\66\1\uffff\54\66"+
            "\4\uffff\u0087\66\1\uffff\107\66\1\uffff\46\66\2\uffff\2\66"+
            "\6\uffff\20\66\41\uffff\46\66\2\uffff\7\66\1\uffff\47\66\1\uffff"+
            "\2\66\6\uffff\21\66\1\uffff\27\66\1\uffff\12\66\13\uffff\33"+
            "\66\5\uffff\5\66\27\uffff\12\66\5\uffff\1\66\3\uffff\1\66\1"+
            "\uffff\32\66\5\uffff\31\66\7\uffff\175\66\1\uffff\60\66\2\uffff"+
            "\73\66\2\uffff\3\66\60\uffff\62\66\u014f\uffff\71\66\2\uffff"+
            "\22\66\2\uffff\5\66\3\uffff\31\66\20\uffff\3\66\1\uffff\10\66"+
            "\2\uffff\2\66\2\uffff\26\66\1\uffff\7\66\1\uffff\1\66\3\uffff"+
            "\4\66\2\uffff\11\66\2\uffff\2\66\2\uffff\3\66\11\uffff\1\66"+
            "\4\uffff\2\66\1\uffff\5\66\2\uffff\25\66\6\uffff\3\66\1\uffff"+
            "\6\66\4\uffff\2\66\2\uffff\26\66\1\uffff\7\66\1\uffff\2\66\1"+
            "\uffff\2\66\1\uffff\2\66\2\uffff\1\66\1\uffff\5\66\4\uffff\2"+
            "\66\2\uffff\3\66\13\uffff\4\66\1\uffff\1\66\7\uffff\17\66\14"+
            "\uffff\3\66\1\uffff\11\66\1\uffff\3\66\1\uffff\26\66\1\uffff"+
            "\7\66\1\uffff\2\66\1\uffff\5\66\2\uffff\12\66\1\uffff\3\66\1"+
            "\uffff\3\66\2\uffff\1\66\17\uffff\4\66\2\uffff\12\66\1\uffff"+
            "\1\66\17\uffff\3\66\1\uffff\10\66\2\uffff\2\66\2\uffff\26\66"+
            "\1\uffff\7\66\1\uffff\2\66\1\uffff\5\66\2\uffff\10\66\3\uffff"+
            "\2\66\2\uffff\3\66\10\uffff\2\66\4\uffff\2\66\1\uffff\3\66\4"+
            "\uffff\14\66\20\uffff\2\66\1\uffff\6\66\3\uffff\3\66\1\uffff"+
            "\4\66\3\uffff\2\66\1\uffff\1\66\1\uffff\2\66\3\uffff\2\66\3"+
            "\uffff\3\66\3\uffff\10\66\1\uffff\3\66\4\uffff\5\66\3\uffff"+
            "\3\66\1\uffff\4\66\11\uffff\1\66\17\uffff\24\66\6\uffff\3\66"+
            "\1\uffff\10\66\1\uffff\3\66\1\uffff\27\66\1\uffff\12\66\1\uffff"+
            "\5\66\4\uffff\7\66\1\uffff\3\66\1\uffff\4\66\7\uffff\2\66\11"+
            "\uffff\2\66\4\uffff\12\66\22\uffff\2\66\1\uffff\10\66\1\uffff"+
            "\3\66\1\uffff\27\66\1\uffff\12\66\1\uffff\5\66\2\uffff\11\66"+
            "\1\uffff\3\66\1\uffff\4\66\7\uffff\2\66\7\uffff\1\66\1\uffff"+
            "\2\66\4\uffff\12\66\22\uffff\2\66\1\uffff\10\66\1\uffff\3\66"+
            "\1\uffff\27\66\1\uffff\20\66\4\uffff\6\66\2\uffff\3\66\1\uffff"+
            "\4\66\11\uffff\1\66\10\uffff\2\66\4\uffff\12\66\22\uffff\2\66"+
            "\1\uffff\22\66\3\uffff\30\66\1\uffff\11\66\1\uffff\1\66\2\uffff"+
            "\7\66\3\uffff\1\66\4\uffff\6\66\1\uffff\1\66\1\uffff\10\66\22"+
            "\uffff\3\66\14\uffff\72\66\4\uffff\35\66\45\uffff\2\66\1\uffff"+
            "\1\66\2\uffff\2\66\1\uffff\1\66\2\uffff\1\66\6\uffff\4\66\1"+
            "\uffff\7\66\1\uffff\3\66\1\uffff\1\66\1\uffff\1\66\2\uffff\2"+
            "\66\1\uffff\15\66\1\uffff\3\66\2\uffff\5\66\1\uffff\1\66\1\uffff"+
            "\6\66\2\uffff\12\66\2\uffff\2\66\42\uffff\72\66\4\uffff\12\66"+
            "\1\uffff\42\66\6\uffff\33\66\4\uffff\10\66\1\uffff\44\66\1\uffff"+
            "\17\66\2\uffff\1\66\60\uffff\42\66\1\uffff\5\66\1\uffff\2\66"+
            "\1\uffff\7\66\3\uffff\4\66\6\uffff\32\66\106\uffff\46\66\12"+
            "\uffff\51\66\2\uffff\1\66\4\uffff\132\66\5\uffff\104\66\5\uffff"+
            "\122\66\6\uffff\7\66\1\uffff\77\66\1\uffff\1\66\1\uffff\4\66"+
            "\2\uffff\7\66\1\uffff\1\66\1\uffff\4\66\2\uffff\47\66\1\uffff"+
            "\1\66\1\uffff\4\66\2\uffff\37\66\1\uffff\1\66\1\uffff\4\66\2"+
            "\uffff\7\66\1\uffff\1\66\1\uffff\4\66\2\uffff\7\66\1\uffff\7"+
            "\66\1\uffff\27\66\1\uffff\37\66\1\uffff\1\66\1\uffff\4\66\2"+
            "\uffff\7\66\1\uffff\47\66\1\uffff\23\66\6\uffff\34\66\43\uffff"+
            "\125\66\14\uffff\u0276\66\12\uffff\32\66\5\uffff\121\66\17\uffff"+
            "\15\66\1\uffff\7\66\13\uffff\27\66\11\uffff\24\66\14\uffff\15"+
            "\66\1\uffff\3\66\1\uffff\2\66\14\uffff\64\66\2\uffff\50\66\2"+
            "\uffff\12\66\6\uffff\12\66\6\uffff\16\66\2\uffff\12\66\6\uffff"+
            "\130\66\10\uffff\52\66\126\uffff\35\66\3\uffff\14\66\4\uffff"+
            "\14\66\4\uffff\1\66\3\uffff\52\66\2\uffff\5\66\153\uffff\40"+
            "\66\u0300\uffff\154\66\u0094\uffff\u009c\66\4\uffff\132\66\6"+
            "\uffff\26\66\2\uffff\6\66\2\uffff\46\66\2\uffff\6\66\2\uffff"+
            "\10\66\1\uffff\1\66\1\uffff\1\66\1\uffff\1\66\1\uffff\37\66"+
            "\2\uffff\65\66\1\uffff\7\66\1\uffff\1\66\3\uffff\3\66\1\uffff"+
            "\7\66\3\uffff\4\66\2\uffff\6\66\4\uffff\15\66\5\uffff\3\66\1"+
            "\uffff\7\66\23\uffff\10\66\10\uffff\10\66\10\uffff\11\66\2\uffff"+
            "\12\66\2\uffff\16\66\2\uffff\1\66\30\uffff\2\66\2\uffff\11\66"+
            "\2\uffff\16\66\23\uffff\22\66\36\uffff\33\66\25\uffff\74\66"+
            "\1\uffff\17\66\7\uffff\61\66\14\uffff\u0199\66\2\uffff\u0089"+
            "\66\2\uffff\33\66\57\uffff\47\66\31\uffff\13\66\25\uffff\u01b8"+
            "\66\1\uffff\145\66\2\uffff\22\66\16\uffff\2\66\137\uffff\4\66"+
            "\1\uffff\4\66\2\uffff\34\66\1\uffff\43\66\1\uffff\1\66\1\uffff"+
            "\4\66\3\uffff\1\66\1\uffff\7\66\2\uffff\7\66\16\uffff\37\66"+
            "\3\uffff\30\66\1\uffff\16\66\21\uffff\26\66\12\uffff\u0193\66"+
            "\26\uffff\77\66\4\uffff\40\66\2\uffff\u0110\66\u0372\uffff\32"+
            "\66\1\uffff\131\66\14\uffff\u00d6\66\32\uffff\14\66\5\uffff"+
            "\7\66\12\uffff\2\66\10\uffff\1\66\3\uffff\40\66\1\uffff\126"+
            "\66\2\uffff\2\66\2\uffff\143\66\5\uffff\50\66\4\uffff\136\66"+
            "\1\uffff\50\66\70\uffff\57\66\1\uffff\44\66\14\uffff\56\66\1"+
            "\uffff\u0080\66\1\uffff\u1ab6\66\12\uffff\u51e6\66\132\uffff"+
            "\u048d\66\3\uffff\67\66\u0739\uffff\u2ba4\66\u215c\uffff\u012e"+
            "\66\2\uffff\73\66\u0095\uffff\7\66\14\uffff\5\66\5\uffff\32"+
            "\66\1\uffff\5\66\1\uffff\1\66\1\uffff\2\66\1\uffff\2\66\1\uffff"+
            "\154\66\41\uffff\u016b\66\22\uffff\100\66\2\uffff\66\66\50\uffff"+
            "\16\66\2\uffff\20\66\20\uffff\4\66\14\uffff\5\66\20\uffff\2"+
            "\66\2\uffff\12\66\1\uffff\5\66\6\uffff\10\66\1\uffff\4\66\4"+
            "\uffff\5\66\1\uffff\u0087\66\4\uffff\7\66\2\uffff\61\66\1\uffff"+
            "\1\66\2\uffff\1\66\1\uffff\32\66\1\uffff\1\66\1\uffff\1\66\2"+
            "\uffff\1\66\2\uffff\133\66\3\uffff\6\66\2\uffff\6\66\2\uffff"+
            "\6\66\2\uffff\3\66\3\uffff\3\66\1\uffff\3\66\1\uffff\7\66",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\2\66\1\63\1\62\3\66\2\uffff\1\67\1\66\1\uffff\1\66\1\uffff"+
            "\1\66\12\60\1\uffff\1\66\1\uffff\1\66\1\uffff\1\70\1\66\32\57"+
            "\1\uffff\1\64\2\uffff\1\61\1\uffff\32\56\1\uffff\1\66\44\uffff"+
            "\7\66\1\uffff\2\66\1\uffff\1\66\1\uffff\1\66\1\uffff\4\66\1"+
            "\uffff\3\66\1\uffff\2\66\1\uffff\u017b\66\31\uffff\162\66\4"+
            "\uffff\14\66\16\uffff\5\66\11\uffff\1\66\21\uffff\130\66\5\uffff"+
            "\23\66\12\uffff\1\66\3\uffff\1\66\7\uffff\5\66\1\uffff\1\66"+
            "\1\uffff\24\66\1\uffff\54\66\1\uffff\54\66\4\uffff\u0087\66"+
            "\1\uffff\107\66\1\uffff\46\66\2\uffff\2\66\6\uffff\20\66\41"+
            "\uffff\46\66\2\uffff\7\66\1\uffff\47\66\1\uffff\2\66\6\uffff"+
            "\21\66\1\uffff\27\66\1\uffff\12\66\13\uffff\33\66\5\uffff\5"+
            "\66\27\uffff\12\66\5\uffff\1\66\3\uffff\1\66\1\uffff\32\66\5"+
            "\uffff\31\66\7\uffff\175\66\1\uffff\60\66\2\uffff\73\66\2\uffff"+
            "\3\66\60\uffff\62\66\u014f\uffff\71\66\2\uffff\22\66\2\uffff"+
            "\5\66\3\uffff\31\66\20\uffff\3\66\1\uffff\10\66\2\uffff\2\66"+
            "\2\uffff\26\66\1\uffff\7\66\1\uffff\1\66\3\uffff\4\66\2\uffff"+
            "\11\66\2\uffff\2\66\2\uffff\3\66\11\uffff\1\66\4\uffff\2\66"+
            "\1\uffff\5\66\2\uffff\25\66\6\uffff\3\66\1\uffff\6\66\4\uffff"+
            "\2\66\2\uffff\26\66\1\uffff\7\66\1\uffff\2\66\1\uffff\2\66\1"+
            "\uffff\2\66\2\uffff\1\66\1\uffff\5\66\4\uffff\2\66\2\uffff\3"+
            "\66\13\uffff\4\66\1\uffff\1\66\7\uffff\17\66\14\uffff\3\66\1"+
            "\uffff\11\66\1\uffff\3\66\1\uffff\26\66\1\uffff\7\66\1\uffff"+
            "\2\66\1\uffff\5\66\2\uffff\12\66\1\uffff\3\66\1\uffff\3\66\2"+
            "\uffff\1\66\17\uffff\4\66\2\uffff\12\66\1\uffff\1\66\17\uffff"+
            "\3\66\1\uffff\10\66\2\uffff\2\66\2\uffff\26\66\1\uffff\7\66"+
            "\1\uffff\2\66\1\uffff\5\66\2\uffff\10\66\3\uffff\2\66\2\uffff"+
            "\3\66\10\uffff\2\66\4\uffff\2\66\1\uffff\3\66\4\uffff\14\66"+
            "\20\uffff\2\66\1\uffff\6\66\3\uffff\3\66\1\uffff\4\66\3\uffff"+
            "\2\66\1\uffff\1\66\1\uffff\2\66\3\uffff\2\66\3\uffff\3\66\3"+
            "\uffff\10\66\1\uffff\3\66\4\uffff\5\66\3\uffff\3\66\1\uffff"+
            "\4\66\11\uffff\1\66\17\uffff\24\66\6\uffff\3\66\1\uffff\10\66"+
            "\1\uffff\3\66\1\uffff\27\66\1\uffff\12\66\1\uffff\5\66\4\uffff"+
            "\7\66\1\uffff\3\66\1\uffff\4\66\7\uffff\2\66\11\uffff\2\66\4"+
            "\uffff\12\66\22\uffff\2\66\1\uffff\10\66\1\uffff\3\66\1\uffff"+
            "\27\66\1\uffff\12\66\1\uffff\5\66\2\uffff\11\66\1\uffff\3\66"+
            "\1\uffff\4\66\7\uffff\2\66\7\uffff\1\66\1\uffff\2\66\4\uffff"+
            "\12\66\22\uffff\2\66\1\uffff\10\66\1\uffff\3\66\1\uffff\27\66"+
            "\1\uffff\20\66\4\uffff\6\66\2\uffff\3\66\1\uffff\4\66\11\uffff"+
            "\1\66\10\uffff\2\66\4\uffff\12\66\22\uffff\2\66\1\uffff\22\66"+
            "\3\uffff\30\66\1\uffff\11\66\1\uffff\1\66\2\uffff\7\66\3\uffff"+
            "\1\66\4\uffff\6\66\1\uffff\1\66\1\uffff\10\66\22\uffff\3\66"+
            "\14\uffff\72\66\4\uffff\35\66\45\uffff\2\66\1\uffff\1\66\2\uffff"+
            "\2\66\1\uffff\1\66\2\uffff\1\66\6\uffff\4\66\1\uffff\7\66\1"+
            "\uffff\3\66\1\uffff\1\66\1\uffff\1\66\2\uffff\2\66\1\uffff\15"+
            "\66\1\uffff\3\66\2\uffff\5\66\1\uffff\1\66\1\uffff\6\66\2\uffff"+
            "\12\66\2\uffff\2\66\42\uffff\72\66\4\uffff\12\66\1\uffff\42"+
            "\66\6\uffff\33\66\4\uffff\10\66\1\uffff\44\66\1\uffff\17\66"+
            "\2\uffff\1\66\60\uffff\42\66\1\uffff\5\66\1\uffff\2\66\1\uffff"+
            "\7\66\3\uffff\4\66\6\uffff\32\66\106\uffff\46\66\12\uffff\51"+
            "\66\2\uffff\1\66\4\uffff\132\66\5\uffff\104\66\5\uffff\122\66"+
            "\6\uffff\7\66\1\uffff\77\66\1\uffff\1\66\1\uffff\4\66\2\uffff"+
            "\7\66\1\uffff\1\66\1\uffff\4\66\2\uffff\47\66\1\uffff\1\66\1"+
            "\uffff\4\66\2\uffff\37\66\1\uffff\1\66\1\uffff\4\66\2\uffff"+
            "\7\66\1\uffff\1\66\1\uffff\4\66\2\uffff\7\66\1\uffff\7\66\1"+
            "\uffff\27\66\1\uffff\37\66\1\uffff\1\66\1\uffff\4\66\2\uffff"+
            "\7\66\1\uffff\47\66\1\uffff\23\66\6\uffff\34\66\43\uffff\125"+
            "\66\14\uffff\u0276\66\12\uffff\32\66\5\uffff\121\66\17\uffff"+
            "\15\66\1\uffff\7\66\13\uffff\27\66\11\uffff\24\66\14\uffff\15"+
            "\66\1\uffff\3\66\1\uffff\2\66\14\uffff\64\66\2\uffff\50\66\2"+
            "\uffff\12\66\6\uffff\12\66\6\uffff\16\66\2\uffff\12\66\6\uffff"+
            "\130\66\10\uffff\52\66\126\uffff\35\66\3\uffff\14\66\4\uffff"+
            "\14\66\4\uffff\1\66\3\uffff\52\66\2\uffff\5\66\153\uffff\40"+
            "\66\u0300\uffff\154\66\u0094\uffff\u009c\66\4\uffff\132\66\6"+
            "\uffff\26\66\2\uffff\6\66\2\uffff\46\66\2\uffff\6\66\2\uffff"+
            "\10\66\1\uffff\1\66\1\uffff\1\66\1\uffff\1\66\1\uffff\37\66"+
            "\2\uffff\65\66\1\uffff\7\66\1\uffff\1\66\3\uffff\3\66\1\uffff"+
            "\7\66\3\uffff\4\66\2\uffff\6\66\4\uffff\15\66\5\uffff\3\66\1"+
            "\uffff\7\66\23\uffff\10\66\10\uffff\10\66\10\uffff\11\66\2\uffff"+
            "\12\66\2\uffff\16\66\2\uffff\1\66\30\uffff\2\66\2\uffff\11\66"+
            "\2\uffff\16\66\23\uffff\22\66\36\uffff\33\66\25\uffff\74\66"+
            "\1\uffff\17\66\7\uffff\61\66\14\uffff\u0199\66\2\uffff\u0089"+
            "\66\2\uffff\33\66\57\uffff\47\66\31\uffff\13\66\25\uffff\u01b8"+
            "\66\1\uffff\145\66\2\uffff\22\66\16\uffff\2\66\137\uffff\4\66"+
            "\1\uffff\4\66\2\uffff\34\66\1\uffff\43\66\1\uffff\1\66\1\uffff"+
            "\4\66\3\uffff\1\66\1\uffff\7\66\2\uffff\7\66\16\uffff\37\66"+
            "\3\uffff\30\66\1\uffff\16\66\21\uffff\26\66\12\uffff\u0193\66"+
            "\26\uffff\77\66\4\uffff\40\66\2\uffff\u0110\66\u0372\uffff\32"+
            "\66\1\uffff\131\66\14\uffff\u00d6\66\32\uffff\14\66\5\uffff"+
            "\7\66\12\uffff\2\66\10\uffff\1\66\3\uffff\40\66\1\uffff\126"+
            "\66\2\uffff\2\66\2\uffff\143\66\5\uffff\50\66\4\uffff\136\66"+
            "\1\uffff\50\66\70\uffff\57\66\1\uffff\44\66\14\uffff\56\66\1"+
            "\uffff\u0080\66\1\uffff\u1ab6\66\12\uffff\u51e6\66\132\uffff"+
            "\u048d\66\3\uffff\67\66\u0739\uffff\u2ba4\66\u215c\uffff\u012e"+
            "\66\2\uffff\73\66\u0095\uffff\7\66\14\uffff\5\66\5\uffff\32"+
            "\66\1\uffff\5\66\1\uffff\1\66\1\uffff\2\66\1\uffff\2\66\1\uffff"+
            "\154\66\41\uffff\u016b\66\22\uffff\100\66\2\uffff\66\66\50\uffff"+
            "\16\66\2\uffff\20\66\20\uffff\4\66\14\uffff\5\66\20\uffff\2"+
            "\66\2\uffff\12\66\1\uffff\5\66\6\uffff\10\66\1\uffff\4\66\4"+
            "\uffff\5\66\1\uffff\u0087\66\4\uffff\7\66\2\uffff\61\66\1\uffff"+
            "\1\66\2\uffff\1\66\1\uffff\32\66\1\uffff\1\66\1\uffff\1\66\2"+
            "\uffff\1\66\2\uffff\133\66\3\uffff\6\66\2\uffff\6\66\2\uffff"+
            "\6\66\2\uffff\3\66\3\uffff\3\66\1\uffff\3\66\1\uffff\7\66",
            "\2\66\1\63\1\62\3\66\2\uffff\1\67\1\66\1\uffff\1\66\1\uffff"+
            "\1\66\12\60\1\uffff\1\66\1\uffff\1\66\1\uffff\1\70\1\66\32\57"+
            "\1\uffff\1\64\2\uffff\1\61\1\uffff\32\56\1\uffff\1\66\44\uffff"+
            "\7\66\1\uffff\2\66\1\uffff\1\66\1\uffff\1\66\1\uffff\4\66\1"+
            "\uffff\3\66\1\uffff\2\66\1\uffff\u017b\66\31\uffff\162\66\4"+
            "\uffff\14\66\16\uffff\5\66\11\uffff\1\66\21\uffff\130\66\5\uffff"+
            "\23\66\12\uffff\1\66\3\uffff\1\66\7\uffff\5\66\1\uffff\1\66"+
            "\1\uffff\24\66\1\uffff\54\66\1\uffff\54\66\4\uffff\u0087\66"+
            "\1\uffff\107\66\1\uffff\46\66\2\uffff\2\66\6\uffff\20\66\41"+
            "\uffff\46\66\2\uffff\7\66\1\uffff\47\66\1\uffff\2\66\6\uffff"+
            "\21\66\1\uffff\27\66\1\uffff\12\66\13\uffff\33\66\5\uffff\5"+
            "\66\27\uffff\12\66\5\uffff\1\66\3\uffff\1\66\1\uffff\32\66\5"+
            "\uffff\31\66\7\uffff\175\66\1\uffff\60\66\2\uffff\73\66\2\uffff"+
            "\3\66\60\uffff\62\66\u014f\uffff\71\66\2\uffff\22\66\2\uffff"+
            "\5\66\3\uffff\31\66\20\uffff\3\66\1\uffff\10\66\2\uffff\2\66"+
            "\2\uffff\26\66\1\uffff\7\66\1\uffff\1\66\3\uffff\4\66\2\uffff"+
            "\11\66\2\uffff\2\66\2\uffff\3\66\11\uffff\1\66\4\uffff\2\66"+
            "\1\uffff\5\66\2\uffff\25\66\6\uffff\3\66\1\uffff\6\66\4\uffff"+
            "\2\66\2\uffff\26\66\1\uffff\7\66\1\uffff\2\66\1\uffff\2\66\1"+
            "\uffff\2\66\2\uffff\1\66\1\uffff\5\66\4\uffff\2\66\2\uffff\3"+
            "\66\13\uffff\4\66\1\uffff\1\66\7\uffff\17\66\14\uffff\3\66\1"+
            "\uffff\11\66\1\uffff\3\66\1\uffff\26\66\1\uffff\7\66\1\uffff"+
            "\2\66\1\uffff\5\66\2\uffff\12\66\1\uffff\3\66\1\uffff\3\66\2"+
            "\uffff\1\66\17\uffff\4\66\2\uffff\12\66\1\uffff\1\66\17\uffff"+
            "\3\66\1\uffff\10\66\2\uffff\2\66\2\uffff\26\66\1\uffff\7\66"+
            "\1\uffff\2\66\1\uffff\5\66\2\uffff\10\66\3\uffff\2\66\2\uffff"+
            "\3\66\10\uffff\2\66\4\uffff\2\66\1\uffff\3\66\4\uffff\14\66"+
            "\20\uffff\2\66\1\uffff\6\66\3\uffff\3\66\1\uffff\4\66\3\uffff"+
            "\2\66\1\uffff\1\66\1\uffff\2\66\3\uffff\2\66\3\uffff\3\66\3"+
            "\uffff\10\66\1\uffff\3\66\4\uffff\5\66\3\uffff\3\66\1\uffff"+
            "\4\66\11\uffff\1\66\17\uffff\24\66\6\uffff\3\66\1\uffff\10\66"+
            "\1\uffff\3\66\1\uffff\27\66\1\uffff\12\66\1\uffff\5\66\4\uffff"+
            "\7\66\1\uffff\3\66\1\uffff\4\66\7\uffff\2\66\11\uffff\2\66\4"+
            "\uffff\12\66\22\uffff\2\66\1\uffff\10\66\1\uffff\3\66\1\uffff"+
            "\27\66\1\uffff\12\66\1\uffff\5\66\2\uffff\11\66\1\uffff\3\66"+
            "\1\uffff\4\66\7\uffff\2\66\7\uffff\1\66\1\uffff\2\66\4\uffff"+
            "\12\66\22\uffff\2\66\1\uffff\10\66\1\uffff\3\66\1\uffff\27\66"+
            "\1\uffff\20\66\4\uffff\6\66\2\uffff\3\66\1\uffff\4\66\11\uffff"+
            "\1\66\10\uffff\2\66\4\uffff\12\66\22\uffff\2\66\1\uffff\22\66"+
            "\3\uffff\30\66\1\uffff\11\66\1\uffff\1\66\2\uffff\7\66\3\uffff"+
            "\1\66\4\uffff\6\66\1\uffff\1\66\1\uffff\10\66\22\uffff\3\66"+
            "\14\uffff\72\66\4\uffff\35\66\45\uffff\2\66\1\uffff\1\66\2\uffff"+
            "\2\66\1\uffff\1\66\2\uffff\1\66\6\uffff\4\66\1\uffff\7\66\1"+
            "\uffff\3\66\1\uffff\1\66\1\uffff\1\66\2\uffff\2\66\1\uffff\15"+
            "\66\1\uffff\3\66\2\uffff\5\66\1\uffff\1\66\1\uffff\6\66\2\uffff"+
            "\12\66\2\uffff\2\66\42\uffff\72\66\4\uffff\12\66\1\uffff\42"+
            "\66\6\uffff\33\66\4\uffff\10\66\1\uffff\44\66\1\uffff\17\66"+
            "\2\uffff\1\66\60\uffff\42\66\1\uffff\5\66\1\uffff\2\66\1\uffff"+
            "\7\66\3\uffff\4\66\6\uffff\32\66\106\uffff\46\66\12\uffff\51"+
            "\66\2\uffff\1\66\4\uffff\132\66\5\uffff\104\66\5\uffff\122\66"+
            "\6\uffff\7\66\1\uffff\77\66\1\uffff\1\66\1\uffff\4\66\2\uffff"+
            "\7\66\1\uffff\1\66\1\uffff\4\66\2\uffff\47\66\1\uffff\1\66\1"+
            "\uffff\4\66\2\uffff\37\66\1\uffff\1\66\1\uffff\4\66\2\uffff"+
            "\7\66\1\uffff\1\66\1\uffff\4\66\2\uffff\7\66\1\uffff\7\66\1"+
            "\uffff\27\66\1\uffff\37\66\1\uffff\1\66\1\uffff\4\66\2\uffff"+
            "\7\66\1\uffff\47\66\1\uffff\23\66\6\uffff\34\66\43\uffff\125"+
            "\66\14\uffff\u0276\66\12\uffff\32\66\5\uffff\121\66\17\uffff"+
            "\15\66\1\uffff\7\66\13\uffff\27\66\11\uffff\24\66\14\uffff\15"+
            "\66\1\uffff\3\66\1\uffff\2\66\14\uffff\64\66\2\uffff\50\66\2"+
            "\uffff\12\66\6\uffff\12\66\6\uffff\16\66\2\uffff\12\66\6\uffff"+
            "\130\66\10\uffff\52\66\126\uffff\35\66\3\uffff\14\66\4\uffff"+
            "\14\66\4\uffff\1\66\3\uffff\52\66\2\uffff\5\66\153\uffff\40"+
            "\66\u0300\uffff\154\66\u0094\uffff\u009c\66\4\uffff\132\66\6"+
            "\uffff\26\66\2\uffff\6\66\2\uffff\46\66\2\uffff\6\66\2\uffff"+
            "\10\66\1\uffff\1\66\1\uffff\1\66\1\uffff\1\66\1\uffff\37\66"+
            "\2\uffff\65\66\1\uffff\7\66\1\uffff\1\66\3\uffff\3\66\1\uffff"+
            "\7\66\3\uffff\4\66\2\uffff\6\66\4\uffff\15\66\5\uffff\3\66\1"+
            "\uffff\7\66\23\uffff\10\66\10\uffff\10\66\10\uffff\11\66\2\uffff"+
            "\12\66\2\uffff\16\66\2\uffff\1\66\30\uffff\2\66\2\uffff\11\66"+
            "\2\uffff\16\66\23\uffff\22\66\36\uffff\33\66\25\uffff\74\66"+
            "\1\uffff\17\66\7\uffff\61\66\14\uffff\u0199\66\2\uffff\u0089"+
            "\66\2\uffff\33\66\57\uffff\47\66\31\uffff\13\66\25\uffff\u01b8"+
            "\66\1\uffff\145\66\2\uffff\22\66\16\uffff\2\66\137\uffff\4\66"+
            "\1\uffff\4\66\2\uffff\34\66\1\uffff\43\66\1\uffff\1\66\1\uffff"+
            "\4\66\3\uffff\1\66\1\uffff\7\66\2\uffff\7\66\16\uffff\37\66"+
            "\3\uffff\30\66\1\uffff\16\66\21\uffff\26\66\12\uffff\u0193\66"+
            "\26\uffff\77\66\4\uffff\40\66\2\uffff\u0110\66\u0372\uffff\32"+
            "\66\1\uffff\131\66\14\uffff\u00d6\66\32\uffff\14\66\5\uffff"+
            "\7\66\12\uffff\2\66\10\uffff\1\66\3\uffff\40\66\1\uffff\126"+
            "\66\2\uffff\2\66\2\uffff\143\66\5\uffff\50\66\4\uffff\136\66"+
            "\1\uffff\50\66\70\uffff\57\66\1\uffff\44\66\14\uffff\56\66\1"+
            "\uffff\u0080\66\1\uffff\u1ab6\66\12\uffff\u51e6\66\132\uffff"+
            "\u048d\66\3\uffff\67\66\u0739\uffff\u2ba4\66\u215c\uffff\u012e"+
            "\66\2\uffff\73\66\u0095\uffff\7\66\14\uffff\5\66\5\uffff\32"+
            "\66\1\uffff\5\66\1\uffff\1\66\1\uffff\2\66\1\uffff\2\66\1\uffff"+
            "\154\66\41\uffff\u016b\66\22\uffff\100\66\2\uffff\66\66\50\uffff"+
            "\16\66\2\uffff\20\66\20\uffff\4\66\14\uffff\5\66\20\uffff\2"+
            "\66\2\uffff\12\66\1\uffff\5\66\6\uffff\10\66\1\uffff\4\66\4"+
            "\uffff\5\66\1\uffff\u0087\66\4\uffff\7\66\2\uffff\61\66\1\uffff"+
            "\1\66\2\uffff\1\66\1\uffff\32\66\1\uffff\1\66\1\uffff\1\66\2"+
            "\uffff\1\66\2\uffff\133\66\3\uffff\6\66\2\uffff\6\66\2\uffff"+
            "\6\66\2\uffff\3\66\3\uffff\3\66\1\uffff\3\66\1\uffff\7\66",
            "",
            "\165\132\1\131\uff8a\132",
            "",
            "\7\66\2\uffff\1\67\1\133\1\uffff\1\133\1\uffff\1\66\12\134"+
            "\1\uffff\1\66\1\uffff\1\66\1\uffff\1\70\33\66\1\uffff\1\110"+
            "\2\uffff\1\66\1\uffff\32\66\1\uffff\1\66\44\uffff\7\66\1\uffff"+
            "\2\66\1\uffff\1\66\1\uffff\1\66\1\uffff\4\66\1\uffff\3\66\1"+
            "\uffff\2\66\1\uffff\u017b\66\31\uffff\162\66\4\uffff\14\66\16"+
            "\uffff\5\66\11\uffff\1\66\21\uffff\130\66\5\uffff\23\66\12\uffff"+
            "\1\66\3\uffff\1\66\7\uffff\5\66\1\uffff\1\66\1\uffff\24\66\1"+
            "\uffff\54\66\1\uffff\54\66\4\uffff\u0087\66\1\uffff\107\66\1"+
            "\uffff\46\66\2\uffff\2\66\6\uffff\20\66\41\uffff\46\66\2\uffff"+
            "\7\66\1\uffff\47\66\1\uffff\2\66\6\uffff\21\66\1\uffff\27\66"+
            "\1\uffff\12\66\13\uffff\33\66\5\uffff\5\66\27\uffff\12\66\5"+
            "\uffff\1\66\3\uffff\1\66\1\uffff\32\66\5\uffff\31\66\7\uffff"+
            "\175\66\1\uffff\60\66\2\uffff\73\66\2\uffff\3\66\60\uffff\62"+
            "\66\u014f\uffff\71\66\2\uffff\22\66\2\uffff\5\66\3\uffff\31"+
            "\66\20\uffff\3\66\1\uffff\10\66\2\uffff\2\66\2\uffff\26\66\1"+
            "\uffff\7\66\1\uffff\1\66\3\uffff\4\66\2\uffff\11\66\2\uffff"+
            "\2\66\2\uffff\3\66\11\uffff\1\66\4\uffff\2\66\1\uffff\5\66\2"+
            "\uffff\25\66\6\uffff\3\66\1\uffff\6\66\4\uffff\2\66\2\uffff"+
            "\26\66\1\uffff\7\66\1\uffff\2\66\1\uffff\2\66\1\uffff\2\66\2"+
            "\uffff\1\66\1\uffff\5\66\4\uffff\2\66\2\uffff\3\66\13\uffff"+
            "\4\66\1\uffff\1\66\7\uffff\17\66\14\uffff\3\66\1\uffff\11\66"+
            "\1\uffff\3\66\1\uffff\26\66\1\uffff\7\66\1\uffff\2\66\1\uffff"+
            "\5\66\2\uffff\12\66\1\uffff\3\66\1\uffff\3\66\2\uffff\1\66\17"+
            "\uffff\4\66\2\uffff\12\66\1\uffff\1\66\17\uffff\3\66\1\uffff"+
            "\10\66\2\uffff\2\66\2\uffff\26\66\1\uffff\7\66\1\uffff\2\66"+
            "\1\uffff\5\66\2\uffff\10\66\3\uffff\2\66\2\uffff\3\66\10\uffff"+
            "\2\66\4\uffff\2\66\1\uffff\3\66\4\uffff\14\66\20\uffff\2\66"+
            "\1\uffff\6\66\3\uffff\3\66\1\uffff\4\66\3\uffff\2\66\1\uffff"+
            "\1\66\1\uffff\2\66\3\uffff\2\66\3\uffff\3\66\3\uffff\10\66\1"+
            "\uffff\3\66\4\uffff\5\66\3\uffff\3\66\1\uffff\4\66\11\uffff"+
            "\1\66\17\uffff\24\66\6\uffff\3\66\1\uffff\10\66\1\uffff\3\66"+
            "\1\uffff\27\66\1\uffff\12\66\1\uffff\5\66\4\uffff\7\66\1\uffff"+
            "\3\66\1\uffff\4\66\7\uffff\2\66\11\uffff\2\66\4\uffff\12\66"+
            "\22\uffff\2\66\1\uffff\10\66\1\uffff\3\66\1\uffff\27\66\1\uffff"+
            "\12\66\1\uffff\5\66\2\uffff\11\66\1\uffff\3\66\1\uffff\4\66"+
            "\7\uffff\2\66\7\uffff\1\66\1\uffff\2\66\4\uffff\12\66\22\uffff"+
            "\2\66\1\uffff\10\66\1\uffff\3\66\1\uffff\27\66\1\uffff\20\66"+
            "\4\uffff\6\66\2\uffff\3\66\1\uffff\4\66\11\uffff\1\66\10\uffff"+
            "\2\66\4\uffff\12\66\22\uffff\2\66\1\uffff\22\66\3\uffff\30\66"+
            "\1\uffff\11\66\1\uffff\1\66\2\uffff\7\66\3\uffff\1\66\4\uffff"+
            "\6\66\1\uffff\1\66\1\uffff\10\66\22\uffff\3\66\14\uffff\72\66"+
            "\4\uffff\35\66\45\uffff\2\66\1\uffff\1\66\2\uffff\2\66\1\uffff"+
            "\1\66\2\uffff\1\66\6\uffff\4\66\1\uffff\7\66\1\uffff\3\66\1"+
            "\uffff\1\66\1\uffff\1\66\2\uffff\2\66\1\uffff\15\66\1\uffff"+
            "\3\66\2\uffff\5\66\1\uffff\1\66\1\uffff\6\66\2\uffff\12\66\2"+
            "\uffff\2\66\42\uffff\72\66\4\uffff\12\66\1\uffff\42\66\6\uffff"+
            "\33\66\4\uffff\10\66\1\uffff\44\66\1\uffff\17\66\2\uffff\1\66"+
            "\60\uffff\42\66\1\uffff\5\66\1\uffff\2\66\1\uffff\7\66\3\uffff"+
            "\4\66\6\uffff\32\66\106\uffff\46\66\12\uffff\51\66\2\uffff\1"+
            "\66\4\uffff\132\66\5\uffff\104\66\5\uffff\122\66\6\uffff\7\66"+
            "\1\uffff\77\66\1\uffff\1\66\1\uffff\4\66\2\uffff\7\66\1\uffff"+
            "\1\66\1\uffff\4\66\2\uffff\47\66\1\uffff\1\66\1\uffff\4\66\2"+
            "\uffff\37\66\1\uffff\1\66\1\uffff\4\66\2\uffff\7\66\1\uffff"+
            "\1\66\1\uffff\4\66\2\uffff\7\66\1\uffff\7\66\1\uffff\27\66\1"+
            "\uffff\37\66\1\uffff\1\66\1\uffff\4\66\2\uffff\7\66\1\uffff"+
            "\47\66\1\uffff\23\66\6\uffff\34\66\43\uffff\125\66\14\uffff"+
            "\u0276\66\12\uffff\32\66\5\uffff\121\66\17\uffff\15\66\1\uffff"+
            "\7\66\13\uffff\27\66\11\uffff\24\66\14\uffff\15\66\1\uffff\3"+
            "\66\1\uffff\2\66\14\uffff\64\66\2\uffff\50\66\2\uffff\12\66"+
            "\6\uffff\12\66\6\uffff\16\66\2\uffff\12\66\6\uffff\130\66\10"+
            "\uffff\52\66\126\uffff\35\66\3\uffff\14\66\4\uffff\14\66\4\uffff"+
            "\1\66\3\uffff\52\66\2\uffff\5\66\153\uffff\40\66\u0300\uffff"+
            "\154\66\u0094\uffff\u009c\66\4\uffff\132\66\6\uffff\26\66\2"+
            "\uffff\6\66\2\uffff\46\66\2\uffff\6\66\2\uffff\10\66\1\uffff"+
            "\1\66\1\uffff\1\66\1\uffff\1\66\1\uffff\37\66\2\uffff\65\66"+
            "\1\uffff\7\66\1\uffff\1\66\3\uffff\3\66\1\uffff\7\66\3\uffff"+
            "\4\66\2\uffff\6\66\4\uffff\15\66\5\uffff\3\66\1\uffff\7\66\23"+
            "\uffff\10\66\10\uffff\10\66\10\uffff\11\66\2\uffff\12\66\2\uffff"+
            "\16\66\2\uffff\1\66\30\uffff\2\66\2\uffff\11\66\2\uffff\16\66"+
            "\23\uffff\22\66\36\uffff\33\66\25\uffff\74\66\1\uffff\17\66"+
            "\7\uffff\61\66\14\uffff\u0199\66\2\uffff\u0089\66\2\uffff\33"+
            "\66\57\uffff\47\66\31\uffff\13\66\25\uffff\u01b8\66\1\uffff"+
            "\145\66\2\uffff\22\66\16\uffff\2\66\137\uffff\4\66\1\uffff\4"+
            "\66\2\uffff\34\66\1\uffff\43\66\1\uffff\1\66\1\uffff\4\66\3"+
            "\uffff\1\66\1\uffff\7\66\2\uffff\7\66\16\uffff\37\66\3\uffff"+
            "\30\66\1\uffff\16\66\21\uffff\26\66\12\uffff\u0193\66\26\uffff"+
            "\77\66\4\uffff\40\66\2\uffff\u0110\66\u0372\uffff\32\66\1\uffff"+
            "\131\66\14\uffff\u00d6\66\32\uffff\14\66\5\uffff\7\66\12\uffff"+
            "\2\66\10\uffff\1\66\3\uffff\40\66\1\uffff\126\66\2\uffff\2\66"+
            "\2\uffff\143\66\5\uffff\50\66\4\uffff\136\66\1\uffff\50\66\70"+
            "\uffff\57\66\1\uffff\44\66\14\uffff\56\66\1\uffff\u0080\66\1"+
            "\uffff\u1ab6\66\12\uffff\u51e6\66\132\uffff\u048d\66\3\uffff"+
            "\67\66\u0739\uffff\u2ba4\66\u215c\uffff\u012e\66\2\uffff\73"+
            "\66\u0095\uffff\7\66\14\uffff\5\66\5\uffff\32\66\1\uffff\5\66"+
            "\1\uffff\1\66\1\uffff\2\66\1\uffff\2\66\1\uffff\154\66\41\uffff"+
            "\u016b\66\22\uffff\100\66\2\uffff\66\66\50\uffff\16\66\2\uffff"+
            "\20\66\20\uffff\4\66\14\uffff\5\66\20\uffff\2\66\2\uffff\12"+
            "\66\1\uffff\5\66\6\uffff\10\66\1\uffff\4\66\4\uffff\5\66\1\uffff"+
            "\u0087\66\4\uffff\7\66\2\uffff\61\66\1\uffff\1\66\2\uffff\1"+
            "\66\1\uffff\32\66\1\uffff\1\66\1\uffff\1\66\2\uffff\1\66\2\uffff"+
            "\133\66\3\uffff\6\66\2\uffff\6\66\2\uffff\6\66\2\uffff\3\66"+
            "\3\uffff\3\66\1\uffff\3\66\1\uffff\7\66",
            "",
            "\7\66\2\uffff\1\67\1\66\1\uffff\1\66\1\111\1\66\12\114\1\uffff"+
            "\1\66\1\uffff\1\66\1\uffff\1\70\5\66\1\112\25\66\1\uffff\1\110"+
            "\2\uffff\1\66\1\uffff\4\66\1\112\25\66\1\uffff\1\66\44\uffff"+
            "\7\66\1\uffff\2\66\1\uffff\1\66\1\uffff\1\66\1\uffff\4\66\1"+
            "\uffff\3\66\1\uffff\2\66\1\uffff\u017b\66\31\uffff\162\66\4"+
            "\uffff\14\66\16\uffff\5\66\11\uffff\1\66\21\uffff\130\66\5\uffff"+
            "\23\66\12\uffff\1\66\3\uffff\1\66\7\uffff\5\66\1\uffff\1\66"+
            "\1\uffff\24\66\1\uffff\54\66\1\uffff\54\66\4\uffff\u0087\66"+
            "\1\uffff\107\66\1\uffff\46\66\2\uffff\2\66\6\uffff\20\66\41"+
            "\uffff\46\66\2\uffff\7\66\1\uffff\47\66\1\uffff\2\66\6\uffff"+
            "\21\66\1\uffff\27\66\1\uffff\12\66\13\uffff\33\66\5\uffff\5"+
            "\66\27\uffff\12\66\5\uffff\1\66\3\uffff\1\66\1\uffff\32\66\5"+
            "\uffff\31\66\7\uffff\175\66\1\uffff\60\66\2\uffff\73\66\2\uffff"+
            "\3\66\60\uffff\62\66\u014f\uffff\71\66\2\uffff\22\66\2\uffff"+
            "\5\66\3\uffff\31\66\20\uffff\3\66\1\uffff\10\66\2\uffff\2\66"+
            "\2\uffff\26\66\1\uffff\7\66\1\uffff\1\66\3\uffff\4\66\2\uffff"+
            "\11\66\2\uffff\2\66\2\uffff\3\66\11\uffff\1\66\4\uffff\2\66"+
            "\1\uffff\5\66\2\uffff\25\66\6\uffff\3\66\1\uffff\6\66\4\uffff"+
            "\2\66\2\uffff\26\66\1\uffff\7\66\1\uffff\2\66\1\uffff\2\66\1"+
            "\uffff\2\66\2\uffff\1\66\1\uffff\5\66\4\uffff\2\66\2\uffff\3"+
            "\66\13\uffff\4\66\1\uffff\1\66\7\uffff\17\66\14\uffff\3\66\1"+
            "\uffff\11\66\1\uffff\3\66\1\uffff\26\66\1\uffff\7\66\1\uffff"+
            "\2\66\1\uffff\5\66\2\uffff\12\66\1\uffff\3\66\1\uffff\3\66\2"+
            "\uffff\1\66\17\uffff\4\66\2\uffff\12\66\1\uffff\1\66\17\uffff"+
            "\3\66\1\uffff\10\66\2\uffff\2\66\2\uffff\26\66\1\uffff\7\66"+
            "\1\uffff\2\66\1\uffff\5\66\2\uffff\10\66\3\uffff\2\66\2\uffff"+
            "\3\66\10\uffff\2\66\4\uffff\2\66\1\uffff\3\66\4\uffff\14\66"+
            "\20\uffff\2\66\1\uffff\6\66\3\uffff\3\66\1\uffff\4\66\3\uffff"+
            "\2\66\1\uffff\1\66\1\uffff\2\66\3\uffff\2\66\3\uffff\3\66\3"+
            "\uffff\10\66\1\uffff\3\66\4\uffff\5\66\3\uffff\3\66\1\uffff"+
            "\4\66\11\uffff\1\66\17\uffff\24\66\6\uffff\3\66\1\uffff\10\66"+
            "\1\uffff\3\66\1\uffff\27\66\1\uffff\12\66\1\uffff\5\66\4\uffff"+
            "\7\66\1\uffff\3\66\1\uffff\4\66\7\uffff\2\66\11\uffff\2\66\4"+
            "\uffff\12\66\22\uffff\2\66\1\uffff\10\66\1\uffff\3\66\1\uffff"+
            "\27\66\1\uffff\12\66\1\uffff\5\66\2\uffff\11\66\1\uffff\3\66"+
            "\1\uffff\4\66\7\uffff\2\66\7\uffff\1\66\1\uffff\2\66\4\uffff"+
            "\12\66\22\uffff\2\66\1\uffff\10\66\1\uffff\3\66\1\uffff\27\66"+
            "\1\uffff\20\66\4\uffff\6\66\2\uffff\3\66\1\uffff\4\66\11\uffff"+
            "\1\66\10\uffff\2\66\4\uffff\12\66\22\uffff\2\66\1\uffff\22\66"+
            "\3\uffff\30\66\1\uffff\11\66\1\uffff\1\66\2\uffff\7\66\3\uffff"+
            "\1\66\4\uffff\6\66\1\uffff\1\66\1\uffff\10\66\22\uffff\3\66"+
            "\14\uffff\72\66\4\uffff\35\66\45\uffff\2\66\1\uffff\1\66\2\uffff"+
            "\2\66\1\uffff\1\66\2\uffff\1\66\6\uffff\4\66\1\uffff\7\66\1"+
            "\uffff\3\66\1\uffff\1\66\1\uffff\1\66\2\uffff\2\66\1\uffff\15"+
            "\66\1\uffff\3\66\2\uffff\5\66\1\uffff\1\66\1\uffff\6\66\2\uffff"+
            "\12\66\2\uffff\2\66\42\uffff\72\66\4\uffff\12\66\1\uffff\42"+
            "\66\6\uffff\33\66\4\uffff\10\66\1\uffff\44\66\1\uffff\17\66"+
            "\2\uffff\1\66\60\uffff\42\66\1\uffff\5\66\1\uffff\2\66\1\uffff"+
            "\7\66\3\uffff\4\66\6\uffff\32\66\106\uffff\46\66\12\uffff\51"+
            "\66\2\uffff\1\66\4\uffff\132\66\5\uffff\104\66\5\uffff\122\66"+
            "\6\uffff\7\66\1\uffff\77\66\1\uffff\1\66\1\uffff\4\66\2\uffff"+
            "\7\66\1\uffff\1\66\1\uffff\4\66\2\uffff\47\66\1\uffff\1\66\1"+
            "\uffff\4\66\2\uffff\37\66\1\uffff\1\66\1\uffff\4\66\2\uffff"+
            "\7\66\1\uffff\1\66\1\uffff\4\66\2\uffff\7\66\1\uffff\7\66\1"+
            "\uffff\27\66\1\uffff\37\66\1\uffff\1\66\1\uffff\4\66\2\uffff"+
            "\7\66\1\uffff\47\66\1\uffff\23\66\6\uffff\34\66\43\uffff\125"+
            "\66\14\uffff\u0276\66\12\uffff\32\66\5\uffff\121\66\17\uffff"+
            "\15\66\1\uffff\7\66\13\uffff\27\66\11\uffff\24\66\14\uffff\15"+
            "\66\1\uffff\3\66\1\uffff\2\66\14\uffff\64\66\2\uffff\50\66\2"+
            "\uffff\12\66\6\uffff\12\66\6\uffff\16\66\2\uffff\12\66\6\uffff"+
            "\130\66\10\uffff\52\66\126\uffff\35\66\3\uffff\14\66\4\uffff"+
            "\14\66\4\uffff\1\66\3\uffff\52\66\2\uffff\5\66\153\uffff\40"+
            "\66\u0300\uffff\154\66\u0094\uffff\u009c\66\4\uffff\132\66\6"+
            "\uffff\26\66\2\uffff\6\66\2\uffff\46\66\2\uffff\6\66\2\uffff"+
            "\10\66\1\uffff\1\66\1\uffff\1\66\1\uffff\1\66\1\uffff\37\66"+
            "\2\uffff\65\66\1\uffff\7\66\1\uffff\1\66\3\uffff\3\66\1\uffff"+
            "\7\66\3\uffff\4\66\2\uffff\6\66\4\uffff\15\66\5\uffff\3\66\1"+
            "\uffff\7\66\23\uffff\10\66\10\uffff\10\66\10\uffff\11\66\2\uffff"+
            "\12\66\2\uffff\16\66\2\uffff\1\66\30\uffff\2\66\2\uffff\11\66"+
            "\2\uffff\16\66\23\uffff\22\66\36\uffff\33\66\25\uffff\74\66"+
            "\1\uffff\17\66\7\uffff\61\66\14\uffff\u0199\66\2\uffff\u0089"+
            "\66\2\uffff\33\66\57\uffff\47\66\31\uffff\13\66\25\uffff\u01b8"+
            "\66\1\uffff\145\66\2\uffff\22\66\16\uffff\2\66\137\uffff\4\66"+
            "\1\uffff\4\66\2\uffff\34\66\1\uffff\43\66\1\uffff\1\66\1\uffff"+
            "\4\66\3\uffff\1\66\1\uffff\7\66\2\uffff\7\66\16\uffff\37\66"+
            "\3\uffff\30\66\1\uffff\16\66\21\uffff\26\66\12\uffff\u0193\66"+
            "\26\uffff\77\66\4\uffff\40\66\2\uffff\u0110\66\u0372\uffff\32"+
            "\66\1\uffff\131\66\14\uffff\u00d6\66\32\uffff\14\66\5\uffff"+
            "\7\66\12\uffff\2\66\10\uffff\1\66\3\uffff\40\66\1\uffff\126"+
            "\66\2\uffff\2\66\2\uffff\143\66\5\uffff\50\66\4\uffff\136\66"+
            "\1\uffff\50\66\70\uffff\57\66\1\uffff\44\66\14\uffff\56\66\1"+
            "\uffff\u0080\66\1\uffff\u1ab6\66\12\uffff\u51e6\66\132\uffff"+
            "\u048d\66\3\uffff\67\66\u0739\uffff\u2ba4\66\u215c\uffff\u012e"+
            "\66\2\uffff\73\66\u0095\uffff\7\66\14\uffff\5\66\5\uffff\32"+
            "\66\1\uffff\5\66\1\uffff\1\66\1\uffff\2\66\1\uffff\2\66\1\uffff"+
            "\154\66\41\uffff\u016b\66\22\uffff\100\66\2\uffff\66\66\50\uffff"+
            "\16\66\2\uffff\20\66\20\uffff\4\66\14\uffff\5\66\20\uffff\2"+
            "\66\2\uffff\12\66\1\uffff\5\66\6\uffff\10\66\1\uffff\4\66\4"+
            "\uffff\5\66\1\uffff\u0087\66\4\uffff\7\66\2\uffff\61\66\1\uffff"+
            "\1\66\2\uffff\1\66\1\uffff\32\66\1\uffff\1\66\1\uffff\1\66\2"+
            "\uffff\1\66\2\uffff\133\66\3\uffff\6\66\2\uffff\6\66\2\uffff"+
            "\6\66\2\uffff\3\66\3\uffff\3\66\1\uffff\3\66\1\uffff\7\66",
            "\7\66\2\uffff\1\67\1\66\1\uffff\1\66\1\uffff\1\66\12\135\1"+
            "\uffff\1\66\1\uffff\1\66\1\uffff\1\70\1\66\6\135\24\66\1\uffff"+
            "\1\110\2\uffff\1\66\1\uffff\6\135\24\66\1\uffff\1\66\44\uffff"+
            "\7\66\1\uffff\2\66\1\uffff\1\66\1\uffff\1\66\1\uffff\4\66\1"+
            "\uffff\3\66\1\uffff\2\66\1\uffff\u017b\66\31\uffff\162\66\4"+
            "\uffff\14\66\16\uffff\5\66\11\uffff\1\66\21\uffff\130\66\5\uffff"+
            "\23\66\12\uffff\1\66\3\uffff\1\66\7\uffff\5\66\1\uffff\1\66"+
            "\1\uffff\24\66\1\uffff\54\66\1\uffff\54\66\4\uffff\u0087\66"+
            "\1\uffff\107\66\1\uffff\46\66\2\uffff\2\66\6\uffff\20\66\41"+
            "\uffff\46\66\2\uffff\7\66\1\uffff\47\66\1\uffff\2\66\6\uffff"+
            "\21\66\1\uffff\27\66\1\uffff\12\66\13\uffff\33\66\5\uffff\5"+
            "\66\27\uffff\12\66\5\uffff\1\66\3\uffff\1\66\1\uffff\32\66\5"+
            "\uffff\31\66\7\uffff\175\66\1\uffff\60\66\2\uffff\73\66\2\uffff"+
            "\3\66\60\uffff\62\66\u014f\uffff\71\66\2\uffff\22\66\2\uffff"+
            "\5\66\3\uffff\31\66\20\uffff\3\66\1\uffff\10\66\2\uffff\2\66"+
            "\2\uffff\26\66\1\uffff\7\66\1\uffff\1\66\3\uffff\4\66\2\uffff"+
            "\11\66\2\uffff\2\66\2\uffff\3\66\11\uffff\1\66\4\uffff\2\66"+
            "\1\uffff\5\66\2\uffff\25\66\6\uffff\3\66\1\uffff\6\66\4\uffff"+
            "\2\66\2\uffff\26\66\1\uffff\7\66\1\uffff\2\66\1\uffff\2\66\1"+
            "\uffff\2\66\2\uffff\1\66\1\uffff\5\66\4\uffff\2\66\2\uffff\3"+
            "\66\13\uffff\4\66\1\uffff\1\66\7\uffff\17\66\14\uffff\3\66\1"+
            "\uffff\11\66\1\uffff\3\66\1\uffff\26\66\1\uffff\7\66\1\uffff"+
            "\2\66\1\uffff\5\66\2\uffff\12\66\1\uffff\3\66\1\uffff\3\66\2"+
            "\uffff\1\66\17\uffff\4\66\2\uffff\12\66\1\uffff\1\66\17\uffff"+
            "\3\66\1\uffff\10\66\2\uffff\2\66\2\uffff\26\66\1\uffff\7\66"+
            "\1\uffff\2\66\1\uffff\5\66\2\uffff\10\66\3\uffff\2\66\2\uffff"+
            "\3\66\10\uffff\2\66\4\uffff\2\66\1\uffff\3\66\4\uffff\14\66"+
            "\20\uffff\2\66\1\uffff\6\66\3\uffff\3\66\1\uffff\4\66\3\uffff"+
            "\2\66\1\uffff\1\66\1\uffff\2\66\3\uffff\2\66\3\uffff\3\66\3"+
            "\uffff\10\66\1\uffff\3\66\4\uffff\5\66\3\uffff\3\66\1\uffff"+
            "\4\66\11\uffff\1\66\17\uffff\24\66\6\uffff\3\66\1\uffff\10\66"+
            "\1\uffff\3\66\1\uffff\27\66\1\uffff\12\66\1\uffff\5\66\4\uffff"+
            "\7\66\1\uffff\3\66\1\uffff\4\66\7\uffff\2\66\11\uffff\2\66\4"+
            "\uffff\12\66\22\uffff\2\66\1\uffff\10\66\1\uffff\3\66\1\uffff"+
            "\27\66\1\uffff\12\66\1\uffff\5\66\2\uffff\11\66\1\uffff\3\66"+
            "\1\uffff\4\66\7\uffff\2\66\7\uffff\1\66\1\uffff\2\66\4\uffff"+
            "\12\66\22\uffff\2\66\1\uffff\10\66\1\uffff\3\66\1\uffff\27\66"+
            "\1\uffff\20\66\4\uffff\6\66\2\uffff\3\66\1\uffff\4\66\11\uffff"+
            "\1\66\10\uffff\2\66\4\uffff\12\66\22\uffff\2\66\1\uffff\22\66"+
            "\3\uffff\30\66\1\uffff\11\66\1\uffff\1\66\2\uffff\7\66\3\uffff"+
            "\1\66\4\uffff\6\66\1\uffff\1\66\1\uffff\10\66\22\uffff\3\66"+
            "\14\uffff\72\66\4\uffff\35\66\45\uffff\2\66\1\uffff\1\66\2\uffff"+
            "\2\66\1\uffff\1\66\2\uffff\1\66\6\uffff\4\66\1\uffff\7\66\1"+
            "\uffff\3\66\1\uffff\1\66\1\uffff\1\66\2\uffff\2\66\1\uffff\15"+
            "\66\1\uffff\3\66\2\uffff\5\66\1\uffff\1\66\1\uffff\6\66\2\uffff"+
            "\12\66\2\uffff\2\66\42\uffff\72\66\4\uffff\12\66\1\uffff\42"+
            "\66\6\uffff\33\66\4\uffff\10\66\1\uffff\44\66\1\uffff\17\66"+
            "\2\uffff\1\66\60\uffff\42\66\1\uffff\5\66\1\uffff\2\66\1\uffff"+
            "\7\66\3\uffff\4\66\6\uffff\32\66\106\uffff\46\66\12\uffff\51"+
            "\66\2\uffff\1\66\4\uffff\132\66\5\uffff\104\66\5\uffff\122\66"+
            "\6\uffff\7\66\1\uffff\77\66\1\uffff\1\66\1\uffff\4\66\2\uffff"+
            "\7\66\1\uffff\1\66\1\uffff\4\66\2\uffff\47\66\1\uffff\1\66\1"+
            "\uffff\4\66\2\uffff\37\66\1\uffff\1\66\1\uffff\4\66\2\uffff"+
            "\7\66\1\uffff\1\66\1\uffff\4\66\2\uffff\7\66\1\uffff\7\66\1"+
            "\uffff\27\66\1\uffff\37\66\1\uffff\1\66\1\uffff\4\66\2\uffff"+
            "\7\66\1\uffff\47\66\1\uffff\23\66\6\uffff\34\66\43\uffff\125"+
            "\66\14\uffff\u0276\66\12\uffff\32\66\5\uffff\121\66\17\uffff"+
            "\15\66\1\uffff\7\66\13\uffff\27\66\11\uffff\24\66\14\uffff\15"+
            "\66\1\uffff\3\66\1\uffff\2\66\14\uffff\64\66\2\uffff\50\66\2"+
            "\uffff\12\66\6\uffff\12\66\6\uffff\16\66\2\uffff\12\66\6\uffff"+
            "\130\66\10\uffff\52\66\126\uffff\35\66\3\uffff\14\66\4\uffff"+
            "\14\66\4\uffff\1\66\3\uffff\52\66\2\uffff\5\66\153\uffff\40"+
            "\66\u0300\uffff\154\66\u0094\uffff\u009c\66\4\uffff\132\66\6"+
            "\uffff\26\66\2\uffff\6\66\2\uffff\46\66\2\uffff\6\66\2\uffff"+
            "\10\66\1\uffff\1\66\1\uffff\1\66\1\uffff\1\66\1\uffff\37\66"+
            "\2\uffff\65\66\1\uffff\7\66\1\uffff\1\66\3\uffff\3\66\1\uffff"+
            "\7\66\3\uffff\4\66\2\uffff\6\66\4\uffff\15\66\5\uffff\3\66\1"+
            "\uffff\7\66\23\uffff\10\66\10\uffff\10\66\10\uffff\11\66\2\uffff"+
            "\12\66\2\uffff\16\66\2\uffff\1\66\30\uffff\2\66\2\uffff\11\66"+
            "\2\uffff\16\66\23\uffff\22\66\36\uffff\33\66\25\uffff\74\66"+
            "\1\uffff\17\66\7\uffff\61\66\14\uffff\u0199\66\2\uffff\u0089"+
            "\66\2\uffff\33\66\57\uffff\47\66\31\uffff\13\66\25\uffff\u01b8"+
            "\66\1\uffff\145\66\2\uffff\22\66\16\uffff\2\66\137\uffff\4\66"+
            "\1\uffff\4\66\2\uffff\34\66\1\uffff\43\66\1\uffff\1\66\1\uffff"+
            "\4\66\3\uffff\1\66\1\uffff\7\66\2\uffff\7\66\16\uffff\37\66"+
            "\3\uffff\30\66\1\uffff\16\66\21\uffff\26\66\12\uffff\u0193\66"+
            "\26\uffff\77\66\4\uffff\40\66\2\uffff\u0110\66\u0372\uffff\32"+
            "\66\1\uffff\131\66\14\uffff\u00d6\66\32\uffff\14\66\5\uffff"+
            "\7\66\12\uffff\2\66\10\uffff\1\66\3\uffff\40\66\1\uffff\126"+
            "\66\2\uffff\2\66\2\uffff\143\66\5\uffff\50\66\4\uffff\136\66"+
            "\1\uffff\50\66\70\uffff\57\66\1\uffff\44\66\14\uffff\56\66\1"+
            "\uffff\u0080\66\1\uffff\u1ab6\66\12\uffff\u51e6\66\132\uffff"+
            "\u048d\66\3\uffff\67\66\u0739\uffff\u2ba4\66\u215c\uffff\u012e"+
            "\66\2\uffff\73\66\u0095\uffff\7\66\14\uffff\5\66\5\uffff\32"+
            "\66\1\uffff\5\66\1\uffff\1\66\1\uffff\2\66\1\uffff\2\66\1\uffff"+
            "\154\66\41\uffff\u016b\66\22\uffff\100\66\2\uffff\66\66\50\uffff"+
            "\16\66\2\uffff\20\66\20\uffff\4\66\14\uffff\5\66\20\uffff\2"+
            "\66\2\uffff\12\66\1\uffff\5\66\6\uffff\10\66\1\uffff\4\66\4"+
            "\uffff\5\66\1\uffff\u0087\66\4\uffff\7\66\2\uffff\61\66\1\uffff"+
            "\1\66\2\uffff\1\66\1\uffff\32\66\1\uffff\1\66\1\uffff\1\66\2"+
            "\uffff\1\66\2\uffff\133\66\3\uffff\6\66\2\uffff\6\66\2\uffff"+
            "\6\66\2\uffff\3\66\3\uffff\3\66\1\uffff\3\66\1\uffff\7\66",
            "\7\66\2\uffff\1\67\1\66\1\uffff\1\66\1\uffff\13\66\1\uffff"+
            "\1\66\1\uffff\1\66\1\uffff\1\70\33\66\1\uffff\1\110\2\uffff"+
            "\1\66\1\uffff\32\66\1\uffff\1\66\44\uffff\7\66\1\uffff\2\66"+
            "\1\uffff\1\66\1\uffff\1\66\1\uffff\4\66\1\uffff\3\66\1\uffff"+
            "\2\66\1\uffff\u017b\66\31\uffff\162\66\4\uffff\14\66\16\uffff"+
            "\5\66\11\uffff\1\66\21\uffff\130\66\5\uffff\23\66\12\uffff\1"+
            "\66\3\uffff\1\66\7\uffff\5\66\1\uffff\1\66\1\uffff\24\66\1\uffff"+
            "\54\66\1\uffff\54\66\4\uffff\u0087\66\1\uffff\107\66\1\uffff"+
            "\46\66\2\uffff\2\66\6\uffff\20\66\41\uffff\46\66\2\uffff\7\66"+
            "\1\uffff\47\66\1\uffff\2\66\6\uffff\21\66\1\uffff\27\66\1\uffff"+
            "\12\66\13\uffff\33\66\5\uffff\5\66\27\uffff\12\66\5\uffff\1"+
            "\66\3\uffff\1\66\1\uffff\32\66\5\uffff\31\66\7\uffff\175\66"+
            "\1\uffff\60\66\2\uffff\73\66\2\uffff\3\66\60\uffff\62\66\u014f"+
            "\uffff\71\66\2\uffff\22\66\2\uffff\5\66\3\uffff\31\66\20\uffff"+
            "\3\66\1\uffff\10\66\2\uffff\2\66\2\uffff\26\66\1\uffff\7\66"+
            "\1\uffff\1\66\3\uffff\4\66\2\uffff\11\66\2\uffff\2\66\2\uffff"+
            "\3\66\11\uffff\1\66\4\uffff\2\66\1\uffff\5\66\2\uffff\25\66"+
            "\6\uffff\3\66\1\uffff\6\66\4\uffff\2\66\2\uffff\26\66\1\uffff"+
            "\7\66\1\uffff\2\66\1\uffff\2\66\1\uffff\2\66\2\uffff\1\66\1"+
            "\uffff\5\66\4\uffff\2\66\2\uffff\3\66\13\uffff\4\66\1\uffff"+
            "\1\66\7\uffff\17\66\14\uffff\3\66\1\uffff\11\66\1\uffff\3\66"+
            "\1\uffff\26\66\1\uffff\7\66\1\uffff\2\66\1\uffff\5\66\2\uffff"+
            "\12\66\1\uffff\3\66\1\uffff\3\66\2\uffff\1\66\17\uffff\4\66"+
            "\2\uffff\12\66\1\uffff\1\66\17\uffff\3\66\1\uffff\10\66\2\uffff"+
            "\2\66\2\uffff\26\66\1\uffff\7\66\1\uffff\2\66\1\uffff\5\66\2"+
            "\uffff\10\66\3\uffff\2\66\2\uffff\3\66\10\uffff\2\66\4\uffff"+
            "\2\66\1\uffff\3\66\4\uffff\14\66\20\uffff\2\66\1\uffff\6\66"+
            "\3\uffff\3\66\1\uffff\4\66\3\uffff\2\66\1\uffff\1\66\1\uffff"+
            "\2\66\3\uffff\2\66\3\uffff\3\66\3\uffff\10\66\1\uffff\3\66\4"+
            "\uffff\5\66\3\uffff\3\66\1\uffff\4\66\11\uffff\1\66\17\uffff"+
            "\24\66\6\uffff\3\66\1\uffff\10\66\1\uffff\3\66\1\uffff\27\66"+
            "\1\uffff\12\66\1\uffff\5\66\4\uffff\7\66\1\uffff\3\66\1\uffff"+
            "\4\66\7\uffff\2\66\11\uffff\2\66\4\uffff\12\66\22\uffff\2\66"+
            "\1\uffff\10\66\1\uffff\3\66\1\uffff\27\66\1\uffff\12\66\1\uffff"+
            "\5\66\2\uffff\11\66\1\uffff\3\66\1\uffff\4\66\7\uffff\2\66\7"+
            "\uffff\1\66\1\uffff\2\66\4\uffff\12\66\22\uffff\2\66\1\uffff"+
            "\10\66\1\uffff\3\66\1\uffff\27\66\1\uffff\20\66\4\uffff\6\66"+
            "\2\uffff\3\66\1\uffff\4\66\11\uffff\1\66\10\uffff\2\66\4\uffff"+
            "\12\66\22\uffff\2\66\1\uffff\22\66\3\uffff\30\66\1\uffff\11"+
            "\66\1\uffff\1\66\2\uffff\7\66\3\uffff\1\66\4\uffff\6\66\1\uffff"+
            "\1\66\1\uffff\10\66\22\uffff\3\66\14\uffff\72\66\4\uffff\35"+
            "\66\45\uffff\2\66\1\uffff\1\66\2\uffff\2\66\1\uffff\1\66\2\uffff"+
            "\1\66\6\uffff\4\66\1\uffff\7\66\1\uffff\3\66\1\uffff\1\66\1"+
            "\uffff\1\66\2\uffff\2\66\1\uffff\15\66\1\uffff\3\66\2\uffff"+
            "\5\66\1\uffff\1\66\1\uffff\6\66\2\uffff\12\66\2\uffff\2\66\42"+
            "\uffff\72\66\4\uffff\12\66\1\uffff\42\66\6\uffff\33\66\4\uffff"+
            "\10\66\1\uffff\44\66\1\uffff\17\66\2\uffff\1\66\60\uffff\42"+
            "\66\1\uffff\5\66\1\uffff\2\66\1\uffff\7\66\3\uffff\4\66\6\uffff"+
            "\32\66\106\uffff\46\66\12\uffff\51\66\2\uffff\1\66\4\uffff\132"+
            "\66\5\uffff\104\66\5\uffff\122\66\6\uffff\7\66\1\uffff\77\66"+
            "\1\uffff\1\66\1\uffff\4\66\2\uffff\7\66\1\uffff\1\66\1\uffff"+
            "\4\66\2\uffff\47\66\1\uffff\1\66\1\uffff\4\66\2\uffff\37\66"+
            "\1\uffff\1\66\1\uffff\4\66\2\uffff\7\66\1\uffff\1\66\1\uffff"+
            "\4\66\2\uffff\7\66\1\uffff\7\66\1\uffff\27\66\1\uffff\37\66"+
            "\1\uffff\1\66\1\uffff\4\66\2\uffff\7\66\1\uffff\47\66\1\uffff"+
            "\23\66\6\uffff\34\66\43\uffff\125\66\14\uffff\u0276\66\12\uffff"+
            "\32\66\5\uffff\121\66\17\uffff\15\66\1\uffff\7\66\13\uffff\27"+
            "\66\11\uffff\24\66\14\uffff\15\66\1\uffff\3\66\1\uffff\2\66"+
            "\14\uffff\64\66\2\uffff\50\66\2\uffff\12\66\6\uffff\12\66\6"+
            "\uffff\16\66\2\uffff\12\66\6\uffff\130\66\10\uffff\52\66\126"+
            "\uffff\35\66\3\uffff\14\66\4\uffff\14\66\4\uffff\1\66\3\uffff"+
            "\52\66\2\uffff\5\66\153\uffff\40\66\u0300\uffff\154\66\u0094"+
            "\uffff\u009c\66\4\uffff\132\66\6\uffff\26\66\2\uffff\6\66\2"+
            "\uffff\46\66\2\uffff\6\66\2\uffff\10\66\1\uffff\1\66\1\uffff"+
            "\1\66\1\uffff\1\66\1\uffff\37\66\2\uffff\65\66\1\uffff\7\66"+
            "\1\uffff\1\66\3\uffff\3\66\1\uffff\7\66\3\uffff\4\66\2\uffff"+
            "\6\66\4\uffff\15\66\5\uffff\3\66\1\uffff\7\66\23\uffff\10\66"+
            "\10\uffff\10\66\10\uffff\11\66\2\uffff\12\66\2\uffff\16\66\2"+
            "\uffff\1\66\30\uffff\2\66\2\uffff\11\66\2\uffff\16\66\23\uffff"+
            "\22\66\36\uffff\33\66\25\uffff\74\66\1\uffff\17\66\7\uffff\61"+
            "\66\14\uffff\u0199\66\2\uffff\u0089\66\2\uffff\33\66\57\uffff"+
            "\47\66\31\uffff\13\66\25\uffff\u01b8\66\1\uffff\145\66\2\uffff"+
            "\22\66\16\uffff\2\66\137\uffff\4\66\1\uffff\4\66\2\uffff\34"+
            "\66\1\uffff\43\66\1\uffff\1\66\1\uffff\4\66\3\uffff\1\66\1\uffff"+
            "\7\66\2\uffff\7\66\16\uffff\37\66\3\uffff\30\66\1\uffff\16\66"+
            "\21\uffff\26\66\12\uffff\u0193\66\26\uffff\77\66\4\uffff\40"+
            "\66\2\uffff\u0110\66\u0372\uffff\32\66\1\uffff\131\66\14\uffff"+
            "\u00d6\66\32\uffff\14\66\5\uffff\7\66\12\uffff\2\66\10\uffff"+
            "\1\66\3\uffff\40\66\1\uffff\126\66\2\uffff\2\66\2\uffff\143"+
            "\66\5\uffff\50\66\4\uffff\136\66\1\uffff\50\66\70\uffff\57\66"+
            "\1\uffff\44\66\14\uffff\56\66\1\uffff\u0080\66\1\uffff\u1ab6"+
            "\66\12\uffff\u51e6\66\132\uffff\u048d\66\3\uffff\67\66\u0739"+
            "\uffff\u2ba4\66\u215c\uffff\u012e\66\2\uffff\73\66\u0095\uffff"+
            "\7\66\14\uffff\5\66\5\uffff\32\66\1\uffff\5\66\1\uffff\1\66"+
            "\1\uffff\2\66\1\uffff\2\66\1\uffff\154\66\41\uffff\u016b\66"+
            "\22\uffff\100\66\2\uffff\66\66\50\uffff\16\66\2\uffff\20\66"+
            "\20\uffff\4\66\14\uffff\5\66\20\uffff\2\66\2\uffff\12\66\1\uffff"+
            "\5\66\6\uffff\10\66\1\uffff\4\66\4\uffff\5\66\1\uffff\u0087"+
            "\66\4\uffff\7\66\2\uffff\61\66\1\uffff\1\66\2\uffff\1\66\1\uffff"+
            "\32\66\1\uffff\1\66\1\uffff\1\66\2\uffff\1\66\2\uffff\133\66"+
            "\3\uffff\6\66\2\uffff\6\66\2\uffff\6\66\2\uffff\3\66\3\uffff"+
            "\3\66\1\uffff\3\66\1\uffff\7\66",
            "",
            "",
            "\2\66\1\63\1\62\3\66\2\uffff\1\67\1\66\1\uffff\1\66\1\uffff"+
            "\1\66\12\140\1\uffff\1\66\1\uffff\1\66\1\uffff\1\70\1\66\6\137"+
            "\24\57\1\uffff\1\64\2\uffff\1\61\1\uffff\6\136\24\56\1\uffff"+
            "\1\66\44\uffff\7\66\1\uffff\2\66\1\uffff\1\66\1\uffff\1\66\1"+
            "\uffff\4\66\1\uffff\3\66\1\uffff\2\66\1\uffff\u017b\66\31\uffff"+
            "\162\66\4\uffff\14\66\16\uffff\5\66\11\uffff\1\66\21\uffff\130"+
            "\66\5\uffff\23\66\12\uffff\1\66\3\uffff\1\66\7\uffff\5\66\1"+
            "\uffff\1\66\1\uffff\24\66\1\uffff\54\66\1\uffff\54\66\4\uffff"+
            "\u0087\66\1\uffff\107\66\1\uffff\46\66\2\uffff\2\66\6\uffff"+
            "\20\66\41\uffff\46\66\2\uffff\7\66\1\uffff\47\66\1\uffff\2\66"+
            "\6\uffff\21\66\1\uffff\27\66\1\uffff\12\66\13\uffff\33\66\5"+
            "\uffff\5\66\27\uffff\12\66\5\uffff\1\66\3\uffff\1\66\1\uffff"+
            "\32\66\5\uffff\31\66\7\uffff\175\66\1\uffff\60\66\2\uffff\73"+
            "\66\2\uffff\3\66\60\uffff\62\66\u014f\uffff\71\66\2\uffff\22"+
            "\66\2\uffff\5\66\3\uffff\31\66\20\uffff\3\66\1\uffff\10\66\2"+
            "\uffff\2\66\2\uffff\26\66\1\uffff\7\66\1\uffff\1\66\3\uffff"+
            "\4\66\2\uffff\11\66\2\uffff\2\66\2\uffff\3\66\11\uffff\1\66"+
            "\4\uffff\2\66\1\uffff\5\66\2\uffff\25\66\6\uffff\3\66\1\uffff"+
            "\6\66\4\uffff\2\66\2\uffff\26\66\1\uffff\7\66\1\uffff\2\66\1"+
            "\uffff\2\66\1\uffff\2\66\2\uffff\1\66\1\uffff\5\66\4\uffff\2"+
            "\66\2\uffff\3\66\13\uffff\4\66\1\uffff\1\66\7\uffff\17\66\14"+
            "\uffff\3\66\1\uffff\11\66\1\uffff\3\66\1\uffff\26\66\1\uffff"+
            "\7\66\1\uffff\2\66\1\uffff\5\66\2\uffff\12\66\1\uffff\3\66\1"+
            "\uffff\3\66\2\uffff\1\66\17\uffff\4\66\2\uffff\12\66\1\uffff"+
            "\1\66\17\uffff\3\66\1\uffff\10\66\2\uffff\2\66\2\uffff\26\66"+
            "\1\uffff\7\66\1\uffff\2\66\1\uffff\5\66\2\uffff\10\66\3\uffff"+
            "\2\66\2\uffff\3\66\10\uffff\2\66\4\uffff\2\66\1\uffff\3\66\4"+
            "\uffff\14\66\20\uffff\2\66\1\uffff\6\66\3\uffff\3\66\1\uffff"+
            "\4\66\3\uffff\2\66\1\uffff\1\66\1\uffff\2\66\3\uffff\2\66\3"+
            "\uffff\3\66\3\uffff\10\66\1\uffff\3\66\4\uffff\5\66\3\uffff"+
            "\3\66\1\uffff\4\66\11\uffff\1\66\17\uffff\24\66\6\uffff\3\66"+
            "\1\uffff\10\66\1\uffff\3\66\1\uffff\27\66\1\uffff\12\66\1\uffff"+
            "\5\66\4\uffff\7\66\1\uffff\3\66\1\uffff\4\66\7\uffff\2\66\11"+
            "\uffff\2\66\4\uffff\12\66\22\uffff\2\66\1\uffff\10\66\1\uffff"+
            "\3\66\1\uffff\27\66\1\uffff\12\66\1\uffff\5\66\2\uffff\11\66"+
            "\1\uffff\3\66\1\uffff\4\66\7\uffff\2\66\7\uffff\1\66\1\uffff"+
            "\2\66\4\uffff\12\66\22\uffff\2\66\1\uffff\10\66\1\uffff\3\66"+
            "\1\uffff\27\66\1\uffff\20\66\4\uffff\6\66\2\uffff\3\66\1\uffff"+
            "\4\66\11\uffff\1\66\10\uffff\2\66\4\uffff\12\66\22\uffff\2\66"+
            "\1\uffff\22\66\3\uffff\30\66\1\uffff\11\66\1\uffff\1\66\2\uffff"+
            "\7\66\3\uffff\1\66\4\uffff\6\66\1\uffff\1\66\1\uffff\10\66\22"+
            "\uffff\3\66\14\uffff\72\66\4\uffff\35\66\45\uffff\2\66\1\uffff"+
            "\1\66\2\uffff\2\66\1\uffff\1\66\2\uffff\1\66\6\uffff\4\66\1"+
            "\uffff\7\66\1\uffff\3\66\1\uffff\1\66\1\uffff\1\66\2\uffff\2"+
            "\66\1\uffff\15\66\1\uffff\3\66\2\uffff\5\66\1\uffff\1\66\1\uffff"+
            "\6\66\2\uffff\12\66\2\uffff\2\66\42\uffff\72\66\4\uffff\12\66"+
            "\1\uffff\42\66\6\uffff\33\66\4\uffff\10\66\1\uffff\44\66\1\uffff"+
            "\17\66\2\uffff\1\66\60\uffff\42\66\1\uffff\5\66\1\uffff\2\66"+
            "\1\uffff\7\66\3\uffff\4\66\6\uffff\32\66\106\uffff\46\66\12"+
            "\uffff\51\66\2\uffff\1\66\4\uffff\132\66\5\uffff\104\66\5\uffff"+
            "\122\66\6\uffff\7\66\1\uffff\77\66\1\uffff\1\66\1\uffff\4\66"+
            "\2\uffff\7\66\1\uffff\1\66\1\uffff\4\66\2\uffff\47\66\1\uffff"+
            "\1\66\1\uffff\4\66\2\uffff\37\66\1\uffff\1\66\1\uffff\4\66\2"+
            "\uffff\7\66\1\uffff\1\66\1\uffff\4\66\2\uffff\7\66\1\uffff\7"+
            "\66\1\uffff\27\66\1\uffff\37\66\1\uffff\1\66\1\uffff\4\66\2"+
            "\uffff\7\66\1\uffff\47\66\1\uffff\23\66\6\uffff\34\66\43\uffff"+
            "\125\66\14\uffff\u0276\66\12\uffff\32\66\5\uffff\121\66\17\uffff"+
            "\15\66\1\uffff\7\66\13\uffff\27\66\11\uffff\24\66\14\uffff\15"+
            "\66\1\uffff\3\66\1\uffff\2\66\14\uffff\64\66\2\uffff\50\66\2"+
            "\uffff\12\66\6\uffff\12\66\6\uffff\16\66\2\uffff\12\66\6\uffff"+
            "\130\66\10\uffff\52\66\126\uffff\35\66\3\uffff\14\66\4\uffff"+
            "\14\66\4\uffff\1\66\3\uffff\52\66\2\uffff\5\66\153\uffff\40"+
            "\66\u0300\uffff\154\66\u0094\uffff\u009c\66\4\uffff\132\66\6"+
            "\uffff\26\66\2\uffff\6\66\2\uffff\46\66\2\uffff\6\66\2\uffff"+
            "\10\66\1\uffff\1\66\1\uffff\1\66\1\uffff\1\66\1\uffff\37\66"+
            "\2\uffff\65\66\1\uffff\7\66\1\uffff\1\66\3\uffff\3\66\1\uffff"+
            "\7\66\3\uffff\4\66\2\uffff\6\66\4\uffff\15\66\5\uffff\3\66\1"+
            "\uffff\7\66\23\uffff\10\66\10\uffff\10\66\10\uffff\11\66\2\uffff"+
            "\12\66\2\uffff\16\66\2\uffff\1\66\30\uffff\2\66\2\uffff\11\66"+
            "\2\uffff\16\66\23\uffff\22\66\36\uffff\33\66\25\uffff\74\66"+
            "\1\uffff\17\66\7\uffff\61\66\14\uffff\u0199\66\2\uffff\u0089"+
            "\66\2\uffff\33\66\57\uffff\47\66\31\uffff\13\66\25\uffff\u01b8"+
            "\66\1\uffff\145\66\2\uffff\22\66\16\uffff\2\66\137\uffff\4\66"+
            "\1\uffff\4\66\2\uffff\34\66\1\uffff\43\66\1\uffff\1\66\1\uffff"+
            "\4\66\3\uffff\1\66\1\uffff\7\66\2\uffff\7\66\16\uffff\37\66"+
            "\3\uffff\30\66\1\uffff\16\66\21\uffff\26\66\12\uffff\u0193\66"+
            "\26\uffff\77\66\4\uffff\40\66\2\uffff\u0110\66\u0372\uffff\32"+
            "\66\1\uffff\131\66\14\uffff\u00d6\66\32\uffff\14\66\5\uffff"+
            "\7\66\12\uffff\2\66\10\uffff\1\66\3\uffff\40\66\1\uffff\126"+
            "\66\2\uffff\2\66\2\uffff\143\66\5\uffff\50\66\4\uffff\136\66"+
            "\1\uffff\50\66\70\uffff\57\66\1\uffff\44\66\14\uffff\56\66\1"+
            "\uffff\u0080\66\1\uffff\u1ab6\66\12\uffff\u51e6\66\132\uffff"+
            "\u048d\66\3\uffff\67\66\u0739\uffff\u2ba4\66\u215c\uffff\u012e"+
            "\66\2\uffff\73\66\u0095\uffff\7\66\14\uffff\5\66\5\uffff\32"+
            "\66\1\uffff\5\66\1\uffff\1\66\1\uffff\2\66\1\uffff\2\66\1\uffff"+
            "\154\66\41\uffff\u016b\66\22\uffff\100\66\2\uffff\66\66\50\uffff"+
            "\16\66\2\uffff\20\66\20\uffff\4\66\14\uffff\5\66\20\uffff\2"+
            "\66\2\uffff\12\66\1\uffff\5\66\6\uffff\10\66\1\uffff\4\66\4"+
            "\uffff\5\66\1\uffff\u0087\66\4\uffff\7\66\2\uffff\61\66\1\uffff"+
            "\1\66\2\uffff\1\66\1\uffff\32\66\1\uffff\1\66\1\uffff\1\66\2"+
            "\uffff\1\66\2\uffff\133\66\3\uffff\6\66\2\uffff\6\66\2\uffff"+
            "\6\66\2\uffff\3\66\3\uffff\3\66\1\uffff\3\66\1\uffff\7\66",
            "\2\66\1\63\1\62\3\66\2\uffff\1\67\1\66\1\uffff\1\66\1\uffff"+
            "\1\66\12\60\1\uffff\1\66\1\uffff\1\66\1\uffff\1\70\1\66\32\57"+
            "\1\uffff\1\64\2\uffff\1\61\1\uffff\32\56\1\uffff\1\66\44\uffff"+
            "\7\66\1\uffff\2\66\1\uffff\1\66\1\uffff\1\66\1\uffff\4\66\1"+
            "\uffff\3\66\1\uffff\2\66\1\uffff\u017b\66\31\uffff\162\66\4"+
            "\uffff\14\66\16\uffff\5\66\11\uffff\1\66\21\uffff\130\66\5\uffff"+
            "\23\66\12\uffff\1\66\3\uffff\1\66\7\uffff\5\66\1\uffff\1\66"+
            "\1\uffff\24\66\1\uffff\54\66\1\uffff\54\66\4\uffff\u0087\66"+
            "\1\uffff\107\66\1\uffff\46\66\2\uffff\2\66\6\uffff\20\66\41"+
            "\uffff\46\66\2\uffff\7\66\1\uffff\47\66\1\uffff\2\66\6\uffff"+
            "\21\66\1\uffff\27\66\1\uffff\12\66\13\uffff\33\66\5\uffff\5"+
            "\66\27\uffff\12\66\5\uffff\1\66\3\uffff\1\66\1\uffff\32\66\5"+
            "\uffff\31\66\7\uffff\175\66\1\uffff\60\66\2\uffff\73\66\2\uffff"+
            "\3\66\60\uffff\62\66\u014f\uffff\71\66\2\uffff\22\66\2\uffff"+
            "\5\66\3\uffff\31\66\20\uffff\3\66\1\uffff\10\66\2\uffff\2\66"+
            "\2\uffff\26\66\1\uffff\7\66\1\uffff\1\66\3\uffff\4\66\2\uffff"+
            "\11\66\2\uffff\2\66\2\uffff\3\66\11\uffff\1\66\4\uffff\2\66"+
            "\1\uffff\5\66\2\uffff\25\66\6\uffff\3\66\1\uffff\6\66\4\uffff"+
            "\2\66\2\uffff\26\66\1\uffff\7\66\1\uffff\2\66\1\uffff\2\66\1"+
            "\uffff\2\66\2\uffff\1\66\1\uffff\5\66\4\uffff\2\66\2\uffff\3"+
            "\66\13\uffff\4\66\1\uffff\1\66\7\uffff\17\66\14\uffff\3\66\1"+
            "\uffff\11\66\1\uffff\3\66\1\uffff\26\66\1\uffff\7\66\1\uffff"+
            "\2\66\1\uffff\5\66\2\uffff\12\66\1\uffff\3\66\1\uffff\3\66\2"+
            "\uffff\1\66\17\uffff\4\66\2\uffff\12\66\1\uffff\1\66\17\uffff"+
            "\3\66\1\uffff\10\66\2\uffff\2\66\2\uffff\26\66\1\uffff\7\66"+
            "\1\uffff\2\66\1\uffff\5\66\2\uffff\10\66\3\uffff\2\66\2\uffff"+
            "\3\66\10\uffff\2\66\4\uffff\2\66\1\uffff\3\66\4\uffff\14\66"+
            "\20\uffff\2\66\1\uffff\6\66\3\uffff\3\66\1\uffff\4\66\3\uffff"+
            "\2\66\1\uffff\1\66\1\uffff\2\66\3\uffff\2\66\3\uffff\3\66\3"+
            "\uffff\10\66\1\uffff\3\66\4\uffff\5\66\3\uffff\3\66\1\uffff"+
            "\4\66\11\uffff\1\66\17\uffff\24\66\6\uffff\3\66\1\uffff\10\66"+
            "\1\uffff\3\66\1\uffff\27\66\1\uffff\12\66\1\uffff\5\66\4\uffff"+
            "\7\66\1\uffff\3\66\1\uffff\4\66\7\uffff\2\66\11\uffff\2\66\4"+
            "\uffff\12\66\22\uffff\2\66\1\uffff\10\66\1\uffff\3\66\1\uffff"+
            "\27\66\1\uffff\12\66\1\uffff\5\66\2\uffff\11\66\1\uffff\3\66"+
            "\1\uffff\4\66\7\uffff\2\66\7\uffff\1\66\1\uffff\2\66\4\uffff"+
            "\12\66\22\uffff\2\66\1\uffff\10\66\1\uffff\3\66\1\uffff\27\66"+
            "\1\uffff\20\66\4\uffff\6\66\2\uffff\3\66\1\uffff\4\66\11\uffff"+
            "\1\66\10\uffff\2\66\4\uffff\12\66\22\uffff\2\66\1\uffff\22\66"+
            "\3\uffff\30\66\1\uffff\11\66\1\uffff\1\66\2\uffff\7\66\3\uffff"+
            "\1\66\4\uffff\6\66\1\uffff\1\66\1\uffff\10\66\22\uffff\3\66"+
            "\14\uffff\72\66\4\uffff\35\66\45\uffff\2\66\1\uffff\1\66\2\uffff"+
            "\2\66\1\uffff\1\66\2\uffff\1\66\6\uffff\4\66\1\uffff\7\66\1"+
            "\uffff\3\66\1\uffff\1\66\1\uffff\1\66\2\uffff\2\66\1\uffff\15"+
            "\66\1\uffff\3\66\2\uffff\5\66\1\uffff\1\66\1\uffff\6\66\2\uffff"+
            "\12\66\2\uffff\2\66\42\uffff\72\66\4\uffff\12\66\1\uffff\42"+
            "\66\6\uffff\33\66\4\uffff\10\66\1\uffff\44\66\1\uffff\17\66"+
            "\2\uffff\1\66\60\uffff\42\66\1\uffff\5\66\1\uffff\2\66\1\uffff"+
            "\7\66\3\uffff\4\66\6\uffff\32\66\106\uffff\46\66\12\uffff\51"+
            "\66\2\uffff\1\66\4\uffff\132\66\5\uffff\104\66\5\uffff\122\66"+
            "\6\uffff\7\66\1\uffff\77\66\1\uffff\1\66\1\uffff\4\66\2\uffff"+
            "\7\66\1\uffff\1\66\1\uffff\4\66\2\uffff\47\66\1\uffff\1\66\1"+
            "\uffff\4\66\2\uffff\37\66\1\uffff\1\66\1\uffff\4\66\2\uffff"+
            "\7\66\1\uffff\1\66\1\uffff\4\66\2\uffff\7\66\1\uffff\7\66\1"+
            "\uffff\27\66\1\uffff\37\66\1\uffff\1\66\1\uffff\4\66\2\uffff"+
            "\7\66\1\uffff\47\66\1\uffff\23\66\6\uffff\34\66\43\uffff\125"+
            "\66\14\uffff\u0276\66\12\uffff\32\66\5\uffff\121\66\17\uffff"+
            "\15\66\1\uffff\7\66\13\uffff\27\66\11\uffff\24\66\14\uffff\15"+
            "\66\1\uffff\3\66\1\uffff\2\66\14\uffff\64\66\2\uffff\50\66\2"+
            "\uffff\12\66\6\uffff\12\66\6\uffff\16\66\2\uffff\12\66\6\uffff"+
            "\130\66\10\uffff\52\66\126\uffff\35\66\3\uffff\14\66\4\uffff"+
            "\14\66\4\uffff\1\66\3\uffff\52\66\2\uffff\5\66\153\uffff\40"+
            "\66\u0300\uffff\154\66\u0094\uffff\u009c\66\4\uffff\132\66\6"+
            "\uffff\26\66\2\uffff\6\66\2\uffff\46\66\2\uffff\6\66\2\uffff"+
            "\10\66\1\uffff\1\66\1\uffff\1\66\1\uffff\1\66\1\uffff\37\66"+
            "\2\uffff\65\66\1\uffff\7\66\1\uffff\1\66\3\uffff\3\66\1\uffff"+
            "\7\66\3\uffff\4\66\2\uffff\6\66\4\uffff\15\66\5\uffff\3\66\1"+
            "\uffff\7\66\23\uffff\10\66\10\uffff\10\66\10\uffff\11\66\2\uffff"+
            "\12\66\2\uffff\16\66\2\uffff\1\66\30\uffff\2\66\2\uffff\11\66"+
            "\2\uffff\16\66\23\uffff\22\66\36\uffff\33\66\25\uffff\74\66"+
            "\1\uffff\17\66\7\uffff\61\66\14\uffff\u0199\66\2\uffff\u0089"+
            "\66\2\uffff\33\66\57\uffff\47\66\31\uffff\13\66\25\uffff\u01b8"+
            "\66\1\uffff\145\66\2\uffff\22\66\16\uffff\2\66\137\uffff\4\66"+
            "\1\uffff\4\66\2\uffff\34\66\1\uffff\43\66\1\uffff\1\66\1\uffff"+
            "\4\66\3\uffff\1\66\1\uffff\7\66\2\uffff\7\66\16\uffff\37\66"+
            "\3\uffff\30\66\1\uffff\16\66\21\uffff\26\66\12\uffff\u0193\66"+
            "\26\uffff\77\66\4\uffff\40\66\2\uffff\u0110\66\u0372\uffff\32"+
            "\66\1\uffff\131\66\14\uffff\u00d6\66\32\uffff\14\66\5\uffff"+
            "\7\66\12\uffff\2\66\10\uffff\1\66\3\uffff\40\66\1\uffff\126"+
            "\66\2\uffff\2\66\2\uffff\143\66\5\uffff\50\66\4\uffff\136\66"+
            "\1\uffff\50\66\70\uffff\57\66\1\uffff\44\66\14\uffff\56\66\1"+
            "\uffff\u0080\66\1\uffff\u1ab6\66\12\uffff\u51e6\66\132\uffff"+
            "\u048d\66\3\uffff\67\66\u0739\uffff\u2ba4\66\u215c\uffff\u012e"+
            "\66\2\uffff\73\66\u0095\uffff\7\66\14\uffff\5\66\5\uffff\32"+
            "\66\1\uffff\5\66\1\uffff\1\66\1\uffff\2\66\1\uffff\2\66\1\uffff"+
            "\154\66\41\uffff\u016b\66\22\uffff\100\66\2\uffff\66\66\50\uffff"+
            "\16\66\2\uffff\20\66\20\uffff\4\66\14\uffff\5\66\20\uffff\2"+
            "\66\2\uffff\12\66\1\uffff\5\66\6\uffff\10\66\1\uffff\4\66\4"+
            "\uffff\5\66\1\uffff\u0087\66\4\uffff\7\66\2\uffff\61\66\1\uffff"+
            "\1\66\2\uffff\1\66\1\uffff\32\66\1\uffff\1\66\1\uffff\1\66\2"+
            "\uffff\1\66\2\uffff\133\66\3\uffff\6\66\2\uffff\6\66\2\uffff"+
            "\6\66\2\uffff\3\66\3\uffff\3\66\1\uffff\3\66\1\uffff\7\66",
            "",
            "\2\66\1\63\1\62\3\66\2\uffff\1\67\1\66\1\uffff\1\66\1\uffff"+
            "\1\66\12\60\1\uffff\1\66\1\uffff\1\66\1\uffff\1\70\1\66\32\57"+
            "\1\uffff\1\64\2\uffff\1\61\1\uffff\32\56\1\uffff\1\66\44\uffff"+
            "\7\66\1\uffff\2\66\1\uffff\1\66\1\uffff\1\66\1\uffff\4\66\1"+
            "\uffff\3\66\1\uffff\2\66\1\uffff\u017b\66\31\uffff\162\66\4"+
            "\uffff\14\66\16\uffff\5\66\11\uffff\1\66\21\uffff\130\66\5\uffff"+
            "\23\66\12\uffff\1\66\3\uffff\1\66\7\uffff\5\66\1\uffff\1\66"+
            "\1\uffff\24\66\1\uffff\54\66\1\uffff\54\66\4\uffff\u0087\66"+
            "\1\uffff\107\66\1\uffff\46\66\2\uffff\2\66\6\uffff\20\66\41"+
            "\uffff\46\66\2\uffff\7\66\1\uffff\47\66\1\uffff\2\66\6\uffff"+
            "\21\66\1\uffff\27\66\1\uffff\12\66\13\uffff\33\66\5\uffff\5"+
            "\66\27\uffff\12\66\5\uffff\1\66\3\uffff\1\66\1\uffff\32\66\5"+
            "\uffff\31\66\7\uffff\175\66\1\uffff\60\66\2\uffff\73\66\2\uffff"+
            "\3\66\60\uffff\62\66\u014f\uffff\71\66\2\uffff\22\66\2\uffff"+
            "\5\66\3\uffff\31\66\20\uffff\3\66\1\uffff\10\66\2\uffff\2\66"+
            "\2\uffff\26\66\1\uffff\7\66\1\uffff\1\66\3\uffff\4\66\2\uffff"+
            "\11\66\2\uffff\2\66\2\uffff\3\66\11\uffff\1\66\4\uffff\2\66"+
            "\1\uffff\5\66\2\uffff\25\66\6\uffff\3\66\1\uffff\6\66\4\uffff"+
            "\2\66\2\uffff\26\66\1\uffff\7\66\1\uffff\2\66\1\uffff\2\66\1"+
            "\uffff\2\66\2\uffff\1\66\1\uffff\5\66\4\uffff\2\66\2\uffff\3"+
            "\66\13\uffff\4\66\1\uffff\1\66\7\uffff\17\66\14\uffff\3\66\1"+
            "\uffff\11\66\1\uffff\3\66\1\uffff\26\66\1\uffff\7\66\1\uffff"+
            "\2\66\1\uffff\5\66\2\uffff\12\66\1\uffff\3\66\1\uffff\3\66\2"+
            "\uffff\1\66\17\uffff\4\66\2\uffff\12\66\1\uffff\1\66\17\uffff"+
            "\3\66\1\uffff\10\66\2\uffff\2\66\2\uffff\26\66\1\uffff\7\66"+
            "\1\uffff\2\66\1\uffff\5\66\2\uffff\10\66\3\uffff\2\66\2\uffff"+
            "\3\66\10\uffff\2\66\4\uffff\2\66\1\uffff\3\66\4\uffff\14\66"+
            "\20\uffff\2\66\1\uffff\6\66\3\uffff\3\66\1\uffff\4\66\3\uffff"+
            "\2\66\1\uffff\1\66\1\uffff\2\66\3\uffff\2\66\3\uffff\3\66\3"+
            "\uffff\10\66\1\uffff\3\66\4\uffff\5\66\3\uffff\3\66\1\uffff"+
            "\4\66\11\uffff\1\66\17\uffff\24\66\6\uffff\3\66\1\uffff\10\66"+
            "\1\uffff\3\66\1\uffff\27\66\1\uffff\12\66\1\uffff\5\66\4\uffff"+
            "\7\66\1\uffff\3\66\1\uffff\4\66\7\uffff\2\66\11\uffff\2\66\4"+
            "\uffff\12\66\22\uffff\2\66\1\uffff\10\66\1\uffff\3\66\1\uffff"+
            "\27\66\1\uffff\12\66\1\uffff\5\66\2\uffff\11\66\1\uffff\3\66"+
            "\1\uffff\4\66\7\uffff\2\66\7\uffff\1\66\1\uffff\2\66\4\uffff"+
            "\12\66\22\uffff\2\66\1\uffff\10\66\1\uffff\3\66\1\uffff\27\66"+
            "\1\uffff\20\66\4\uffff\6\66\2\uffff\3\66\1\uffff\4\66\11\uffff"+
            "\1\66\10\uffff\2\66\4\uffff\12\66\22\uffff\2\66\1\uffff\22\66"+
            "\3\uffff\30\66\1\uffff\11\66\1\uffff\1\66\2\uffff\7\66\3\uffff"+
            "\1\66\4\uffff\6\66\1\uffff\1\66\1\uffff\10\66\22\uffff\3\66"+
            "\14\uffff\72\66\4\uffff\35\66\45\uffff\2\66\1\uffff\1\66\2\uffff"+
            "\2\66\1\uffff\1\66\2\uffff\1\66\6\uffff\4\66\1\uffff\7\66\1"+
            "\uffff\3\66\1\uffff\1\66\1\uffff\1\66\2\uffff\2\66\1\uffff\15"+
            "\66\1\uffff\3\66\2\uffff\5\66\1\uffff\1\66\1\uffff\6\66\2\uffff"+
            "\12\66\2\uffff\2\66\42\uffff\72\66\4\uffff\12\66\1\uffff\42"+
            "\66\6\uffff\33\66\4\uffff\10\66\1\uffff\44\66\1\uffff\17\66"+
            "\2\uffff\1\66\60\uffff\42\66\1\uffff\5\66\1\uffff\2\66\1\uffff"+
            "\7\66\3\uffff\4\66\6\uffff\32\66\106\uffff\46\66\12\uffff\51"+
            "\66\2\uffff\1\66\4\uffff\132\66\5\uffff\104\66\5\uffff\122\66"+
            "\6\uffff\7\66\1\uffff\77\66\1\uffff\1\66\1\uffff\4\66\2\uffff"+
            "\7\66\1\uffff\1\66\1\uffff\4\66\2\uffff\47\66\1\uffff\1\66\1"+
            "\uffff\4\66\2\uffff\37\66\1\uffff\1\66\1\uffff\4\66\2\uffff"+
            "\7\66\1\uffff\1\66\1\uffff\4\66\2\uffff\7\66\1\uffff\7\66\1"+
            "\uffff\27\66\1\uffff\37\66\1\uffff\1\66\1\uffff\4\66\2\uffff"+
            "\7\66\1\uffff\47\66\1\uffff\23\66\6\uffff\34\66\43\uffff\125"+
            "\66\14\uffff\u0276\66\12\uffff\32\66\5\uffff\121\66\17\uffff"+
            "\15\66\1\uffff\7\66\13\uffff\27\66\11\uffff\24\66\14\uffff\15"+
            "\66\1\uffff\3\66\1\uffff\2\66\14\uffff\64\66\2\uffff\50\66\2"+
            "\uffff\12\66\6\uffff\12\66\6\uffff\16\66\2\uffff\12\66\6\uffff"+
            "\130\66\10\uffff\52\66\126\uffff\35\66\3\uffff\14\66\4\uffff"+
            "\14\66\4\uffff\1\66\3\uffff\52\66\2\uffff\5\66\153\uffff\40"+
            "\66\u0300\uffff\154\66\u0094\uffff\u009c\66\4\uffff\132\66\6"+
            "\uffff\26\66\2\uffff\6\66\2\uffff\46\66\2\uffff\6\66\2\uffff"+
            "\10\66\1\uffff\1\66\1\uffff\1\66\1\uffff\1\66\1\uffff\37\66"+
            "\2\uffff\65\66\1\uffff\7\66\1\uffff\1\66\3\uffff\3\66\1\uffff"+
            "\7\66\3\uffff\4\66\2\uffff\6\66\4\uffff\15\66\5\uffff\3\66\1"+
            "\uffff\7\66\23\uffff\10\66\10\uffff\10\66\10\uffff\11\66\2\uffff"+
            "\12\66\2\uffff\16\66\2\uffff\1\66\30\uffff\2\66\2\uffff\11\66"+
            "\2\uffff\16\66\23\uffff\22\66\36\uffff\33\66\25\uffff\74\66"+
            "\1\uffff\17\66\7\uffff\61\66\14\uffff\u0199\66\2\uffff\u0089"+
            "\66\2\uffff\33\66\57\uffff\47\66\31\uffff\13\66\25\uffff\u01b8"+
            "\66\1\uffff\145\66\2\uffff\22\66\16\uffff\2\66\137\uffff\4\66"+
            "\1\uffff\4\66\2\uffff\34\66\1\uffff\43\66\1\uffff\1\66\1\uffff"+
            "\4\66\3\uffff\1\66\1\uffff\7\66\2\uffff\7\66\16\uffff\37\66"+
            "\3\uffff\30\66\1\uffff\16\66\21\uffff\26\66\12\uffff\u0193\66"+
            "\26\uffff\77\66\4\uffff\40\66\2\uffff\u0110\66\u0372\uffff\32"+
            "\66\1\uffff\131\66\14\uffff\u00d6\66\32\uffff\14\66\5\uffff"+
            "\7\66\12\uffff\2\66\10\uffff\1\66\3\uffff\40\66\1\uffff\126"+
            "\66\2\uffff\2\66\2\uffff\143\66\5\uffff\50\66\4\uffff\136\66"+
            "\1\uffff\50\66\70\uffff\57\66\1\uffff\44\66\14\uffff\56\66\1"+
            "\uffff\u0080\66\1\uffff\u1ab6\66\12\uffff\u51e6\66\132\uffff"+
            "\u048d\66\3\uffff\67\66\u0739\uffff\u2ba4\66\u215c\uffff\u012e"+
            "\66\2\uffff\73\66\u0095\uffff\7\66\14\uffff\5\66\5\uffff\32"+
            "\66\1\uffff\5\66\1\uffff\1\66\1\uffff\2\66\1\uffff\2\66\1\uffff"+
            "\154\66\41\uffff\u016b\66\22\uffff\100\66\2\uffff\66\66\50\uffff"+
            "\16\66\2\uffff\20\66\20\uffff\4\66\14\uffff\5\66\20\uffff\2"+
            "\66\2\uffff\12\66\1\uffff\5\66\6\uffff\10\66\1\uffff\4\66\4"+
            "\uffff\5\66\1\uffff\u0087\66\4\uffff\7\66\2\uffff\61\66\1\uffff"+
            "\1\66\2\uffff\1\66\1\uffff\32\66\1\uffff\1\66\1\uffff\1\66\2"+
            "\uffff\1\66\2\uffff\133\66\3\uffff\6\66\2\uffff\6\66\2\uffff"+
            "\6\66\2\uffff\3\66\3\uffff\3\66\1\uffff\3\66\1\uffff\7\66",
            "\2\66\1\63\1\62\3\66\2\uffff\1\67\1\66\1\uffff\1\66\1\uffff"+
            "\1\66\12\60\1\uffff\1\66\1\uffff\1\66\1\uffff\1\70\1\66\32\57"+
            "\1\uffff\1\64\2\uffff\1\61\1\uffff\32\56\1\uffff\1\66\44\uffff"+
            "\7\66\1\uffff\2\66\1\uffff\1\66\1\uffff\1\66\1\uffff\4\66\1"+
            "\uffff\3\66\1\uffff\2\66\1\uffff\u017b\66\31\uffff\162\66\4"+
            "\uffff\14\66\16\uffff\5\66\11\uffff\1\66\21\uffff\130\66\5\uffff"+
            "\23\66\12\uffff\1\66\3\uffff\1\66\7\uffff\5\66\1\uffff\1\66"+
            "\1\uffff\24\66\1\uffff\54\66\1\uffff\54\66\4\uffff\u0087\66"+
            "\1\uffff\107\66\1\uffff\46\66\2\uffff\2\66\6\uffff\20\66\41"+
            "\uffff\46\66\2\uffff\7\66\1\uffff\47\66\1\uffff\2\66\6\uffff"+
            "\21\66\1\uffff\27\66\1\uffff\12\66\13\uffff\33\66\5\uffff\5"+
            "\66\27\uffff\12\66\5\uffff\1\66\3\uffff\1\66\1\uffff\32\66\5"+
            "\uffff\31\66\7\uffff\175\66\1\uffff\60\66\2\uffff\73\66\2\uffff"+
            "\3\66\60\uffff\62\66\u014f\uffff\71\66\2\uffff\22\66\2\uffff"+
            "\5\66\3\uffff\31\66\20\uffff\3\66\1\uffff\10\66\2\uffff\2\66"+
            "\2\uffff\26\66\1\uffff\7\66\1\uffff\1\66\3\uffff\4\66\2\uffff"+
            "\11\66\2\uffff\2\66\2\uffff\3\66\11\uffff\1\66\4\uffff\2\66"+
            "\1\uffff\5\66\2\uffff\25\66\6\uffff\3\66\1\uffff\6\66\4\uffff"+
            "\2\66\2\uffff\26\66\1\uffff\7\66\1\uffff\2\66\1\uffff\2\66\1"+
            "\uffff\2\66\2\uffff\1\66\1\uffff\5\66\4\uffff\2\66\2\uffff\3"+
            "\66\13\uffff\4\66\1\uffff\1\66\7\uffff\17\66\14\uffff\3\66\1"+
            "\uffff\11\66\1\uffff\3\66\1\uffff\26\66\1\uffff\7\66\1\uffff"+
            "\2\66\1\uffff\5\66\2\uffff\12\66\1\uffff\3\66\1\uffff\3\66\2"+
            "\uffff\1\66\17\uffff\4\66\2\uffff\12\66\1\uffff\1\66\17\uffff"+
            "\3\66\1\uffff\10\66\2\uffff\2\66\2\uffff\26\66\1\uffff\7\66"+
            "\1\uffff\2\66\1\uffff\5\66\2\uffff\10\66\3\uffff\2\66\2\uffff"+
            "\3\66\10\uffff\2\66\4\uffff\2\66\1\uffff\3\66\4\uffff\14\66"+
            "\20\uffff\2\66\1\uffff\6\66\3\uffff\3\66\1\uffff\4\66\3\uffff"+
            "\2\66\1\uffff\1\66\1\uffff\2\66\3\uffff\2\66\3\uffff\3\66\3"+
            "\uffff\10\66\1\uffff\3\66\4\uffff\5\66\3\uffff\3\66\1\uffff"+
            "\4\66\11\uffff\1\66\17\uffff\24\66\6\uffff\3\66\1\uffff\10\66"+
            "\1\uffff\3\66\1\uffff\27\66\1\uffff\12\66\1\uffff\5\66\4\uffff"+
            "\7\66\1\uffff\3\66\1\uffff\4\66\7\uffff\2\66\11\uffff\2\66\4"+
            "\uffff\12\66\22\uffff\2\66\1\uffff\10\66\1\uffff\3\66\1\uffff"+
            "\27\66\1\uffff\12\66\1\uffff\5\66\2\uffff\11\66\1\uffff\3\66"+
            "\1\uffff\4\66\7\uffff\2\66\7\uffff\1\66\1\uffff\2\66\4\uffff"+
            "\12\66\22\uffff\2\66\1\uffff\10\66\1\uffff\3\66\1\uffff\27\66"+
            "\1\uffff\20\66\4\uffff\6\66\2\uffff\3\66\1\uffff\4\66\11\uffff"+
            "\1\66\10\uffff\2\66\4\uffff\12\66\22\uffff\2\66\1\uffff\22\66"+
            "\3\uffff\30\66\1\uffff\11\66\1\uffff\1\66\2\uffff\7\66\3\uffff"+
            "\1\66\4\uffff\6\66\1\uffff\1\66\1\uffff\10\66\22\uffff\3\66"+
            "\14\uffff\72\66\4\uffff\35\66\45\uffff\2\66\1\uffff\1\66\2\uffff"+
            "\2\66\1\uffff\1\66\2\uffff\1\66\6\uffff\4\66\1\uffff\7\66\1"+
            "\uffff\3\66\1\uffff\1\66\1\uffff\1\66\2\uffff\2\66\1\uffff\15"+
            "\66\1\uffff\3\66\2\uffff\5\66\1\uffff\1\66\1\uffff\6\66\2\uffff"+
            "\12\66\2\uffff\2\66\42\uffff\72\66\4\uffff\12\66\1\uffff\42"+
            "\66\6\uffff\33\66\4\uffff\10\66\1\uffff\44\66\1\uffff\17\66"+
            "\2\uffff\1\66\60\uffff\42\66\1\uffff\5\66\1\uffff\2\66\1\uffff"+
            "\7\66\3\uffff\4\66\6\uffff\32\66\106\uffff\46\66\12\uffff\51"+
            "\66\2\uffff\1\66\4\uffff\132\66\5\uffff\104\66\5\uffff\122\66"+
            "\6\uffff\7\66\1\uffff\77\66\1\uffff\1\66\1\uffff\4\66\2\uffff"+
            "\7\66\1\uffff\1\66\1\uffff\4\66\2\uffff\47\66\1\uffff\1\66\1"+
            "\uffff\4\66\2\uffff\37\66\1\uffff\1\66\1\uffff\4\66\2\uffff"+
            "\7\66\1\uffff\1\66\1\uffff\4\66\2\uffff\7\66\1\uffff\7\66\1"+
            "\uffff\27\66\1\uffff\37\66\1\uffff\1\66\1\uffff\4\66\2\uffff"+
            "\7\66\1\uffff\47\66\1\uffff\23\66\6\uffff\34\66\43\uffff\125"+
            "\66\14\uffff\u0276\66\12\uffff\32\66\5\uffff\121\66\17\uffff"+
            "\15\66\1\uffff\7\66\13\uffff\27\66\11\uffff\24\66\14\uffff\15"+
            "\66\1\uffff\3\66\1\uffff\2\66\14\uffff\64\66\2\uffff\50\66\2"+
            "\uffff\12\66\6\uffff\12\66\6\uffff\16\66\2\uffff\12\66\6\uffff"+
            "\130\66\10\uffff\52\66\126\uffff\35\66\3\uffff\14\66\4\uffff"+
            "\14\66\4\uffff\1\66\3\uffff\52\66\2\uffff\5\66\153\uffff\40"+
            "\66\u0300\uffff\154\66\u0094\uffff\u009c\66\4\uffff\132\66\6"+
            "\uffff\26\66\2\uffff\6\66\2\uffff\46\66\2\uffff\6\66\2\uffff"+
            "\10\66\1\uffff\1\66\1\uffff\1\66\1\uffff\1\66\1\uffff\37\66"+
            "\2\uffff\65\66\1\uffff\7\66\1\uffff\1\66\3\uffff\3\66\1\uffff"+
            "\7\66\3\uffff\4\66\2\uffff\6\66\4\uffff\15\66\5\uffff\3\66\1"+
            "\uffff\7\66\23\uffff\10\66\10\uffff\10\66\10\uffff\11\66\2\uffff"+
            "\12\66\2\uffff\16\66\2\uffff\1\66\30\uffff\2\66\2\uffff\11\66"+
            "\2\uffff\16\66\23\uffff\22\66\36\uffff\33\66\25\uffff\74\66"+
            "\1\uffff\17\66\7\uffff\61\66\14\uffff\u0199\66\2\uffff\u0089"+
            "\66\2\uffff\33\66\57\uffff\47\66\31\uffff\13\66\25\uffff\u01b8"+
            "\66\1\uffff\145\66\2\uffff\22\66\16\uffff\2\66\137\uffff\4\66"+
            "\1\uffff\4\66\2\uffff\34\66\1\uffff\43\66\1\uffff\1\66\1\uffff"+
            "\4\66\3\uffff\1\66\1\uffff\7\66\2\uffff\7\66\16\uffff\37\66"+
            "\3\uffff\30\66\1\uffff\16\66\21\uffff\26\66\12\uffff\u0193\66"+
            "\26\uffff\77\66\4\uffff\40\66\2\uffff\u0110\66\u0372\uffff\32"+
            "\66\1\uffff\131\66\14\uffff\u00d6\66\32\uffff\14\66\5\uffff"+
            "\7\66\12\uffff\2\66\10\uffff\1\66\3\uffff\40\66\1\uffff\126"+
            "\66\2\uffff\2\66\2\uffff\143\66\5\uffff\50\66\4\uffff\136\66"+
            "\1\uffff\50\66\70\uffff\57\66\1\uffff\44\66\14\uffff\56\66\1"+
            "\uffff\u0080\66\1\uffff\u1ab6\66\12\uffff\u51e6\66\132\uffff"+
            "\u048d\66\3\uffff\67\66\u0739\uffff\u2ba4\66\u215c\uffff\u012e"+
            "\66\2\uffff\73\66\u0095\uffff\7\66\14\uffff\5\66\5\uffff\32"+
            "\66\1\uffff\5\66\1\uffff\1\66\1\uffff\2\66\1\uffff\2\66\1\uffff"+
            "\154\66\41\uffff\u016b\66\22\uffff\100\66\2\uffff\66\66\50\uffff"+
            "\16\66\2\uffff\20\66\20\uffff\4\66\14\uffff\5\66\20\uffff\2"+
            "\66\2\uffff\12\66\1\uffff\5\66\6\uffff\10\66\1\uffff\4\66\4"+
            "\uffff\5\66\1\uffff\u0087\66\4\uffff\7\66\2\uffff\61\66\1\uffff"+
            "\1\66\2\uffff\1\66\1\uffff\32\66\1\uffff\1\66\1\uffff\1\66\2"+
            "\uffff\1\66\2\uffff\133\66\3\uffff\6\66\2\uffff\6\66\2\uffff"+
            "\6\66\2\uffff\3\66\3\uffff\3\66\1\uffff\3\66\1\uffff\7\66",
            "\2\66\1\63\1\62\3\66\2\uffff\1\67\1\66\1\uffff\1\66\1\uffff"+
            "\1\66\12\60\1\uffff\1\66\1\uffff\1\66\1\uffff\1\70\1\66\32\57"+
            "\1\uffff\1\64\2\uffff\1\61\1\uffff\32\56\1\uffff\1\66\44\uffff"+
            "\7\66\1\uffff\2\66\1\uffff\1\66\1\uffff\1\66\1\uffff\4\66\1"+
            "\uffff\3\66\1\uffff\2\66\1\uffff\u017b\66\31\uffff\162\66\4"+
            "\uffff\14\66\16\uffff\5\66\11\uffff\1\66\21\uffff\130\66\5\uffff"+
            "\23\66\12\uffff\1\66\3\uffff\1\66\7\uffff\5\66\1\uffff\1\66"+
            "\1\uffff\24\66\1\uffff\54\66\1\uffff\54\66\4\uffff\u0087\66"+
            "\1\uffff\107\66\1\uffff\46\66\2\uffff\2\66\6\uffff\20\66\41"+
            "\uffff\46\66\2\uffff\7\66\1\uffff\47\66\1\uffff\2\66\6\uffff"+
            "\21\66\1\uffff\27\66\1\uffff\12\66\13\uffff\33\66\5\uffff\5"+
            "\66\27\uffff\12\66\5\uffff\1\66\3\uffff\1\66\1\uffff\32\66\5"+
            "\uffff\31\66\7\uffff\175\66\1\uffff\60\66\2\uffff\73\66\2\uffff"+
            "\3\66\60\uffff\62\66\u014f\uffff\71\66\2\uffff\22\66\2\uffff"+
            "\5\66\3\uffff\31\66\20\uffff\3\66\1\uffff\10\66\2\uffff\2\66"+
            "\2\uffff\26\66\1\uffff\7\66\1\uffff\1\66\3\uffff\4\66\2\uffff"+
            "\11\66\2\uffff\2\66\2\uffff\3\66\11\uffff\1\66\4\uffff\2\66"+
            "\1\uffff\5\66\2\uffff\25\66\6\uffff\3\66\1\uffff\6\66\4\uffff"+
            "\2\66\2\uffff\26\66\1\uffff\7\66\1\uffff\2\66\1\uffff\2\66\1"+
            "\uffff\2\66\2\uffff\1\66\1\uffff\5\66\4\uffff\2\66\2\uffff\3"+
            "\66\13\uffff\4\66\1\uffff\1\66\7\uffff\17\66\14\uffff\3\66\1"+
            "\uffff\11\66\1\uffff\3\66\1\uffff\26\66\1\uffff\7\66\1\uffff"+
            "\2\66\1\uffff\5\66\2\uffff\12\66\1\uffff\3\66\1\uffff\3\66\2"+
            "\uffff\1\66\17\uffff\4\66\2\uffff\12\66\1\uffff\1\66\17\uffff"+
            "\3\66\1\uffff\10\66\2\uffff\2\66\2\uffff\26\66\1\uffff\7\66"+
            "\1\uffff\2\66\1\uffff\5\66\2\uffff\10\66\3\uffff\2\66\2\uffff"+
            "\3\66\10\uffff\2\66\4\uffff\2\66\1\uffff\3\66\4\uffff\14\66"+
            "\20\uffff\2\66\1\uffff\6\66\3\uffff\3\66\1\uffff\4\66\3\uffff"+
            "\2\66\1\uffff\1\66\1\uffff\2\66\3\uffff\2\66\3\uffff\3\66\3"+
            "\uffff\10\66\1\uffff\3\66\4\uffff\5\66\3\uffff\3\66\1\uffff"+
            "\4\66\11\uffff\1\66\17\uffff\24\66\6\uffff\3\66\1\uffff\10\66"+
            "\1\uffff\3\66\1\uffff\27\66\1\uffff\12\66\1\uffff\5\66\4\uffff"+
            "\7\66\1\uffff\3\66\1\uffff\4\66\7\uffff\2\66\11\uffff\2\66\4"+
            "\uffff\12\66\22\uffff\2\66\1\uffff\10\66\1\uffff\3\66\1\uffff"+
            "\27\66\1\uffff\12\66\1\uffff\5\66\2\uffff\11\66\1\uffff\3\66"+
            "\1\uffff\4\66\7\uffff\2\66\7\uffff\1\66\1\uffff\2\66\4\uffff"+
            "\12\66\22\uffff\2\66\1\uffff\10\66\1\uffff\3\66\1\uffff\27\66"+
            "\1\uffff\20\66\4\uffff\6\66\2\uffff\3\66\1\uffff\4\66\11\uffff"+
            "\1\66\10\uffff\2\66\4\uffff\12\66\22\uffff\2\66\1\uffff\22\66"+
            "\3\uffff\30\66\1\uffff\11\66\1\uffff\1\66\2\uffff\7\66\3\uffff"+
            "\1\66\4\uffff\6\66\1\uffff\1\66\1\uffff\10\66\22\uffff\3\66"+
            "\14\uffff\72\66\4\uffff\35\66\45\uffff\2\66\1\uffff\1\66\2\uffff"+
            "\2\66\1\uffff\1\66\2\uffff\1\66\6\uffff\4\66\1\uffff\7\66\1"+
            "\uffff\3\66\1\uffff\1\66\1\uffff\1\66\2\uffff\2\66\1\uffff\15"+
            "\66\1\uffff\3\66\2\uffff\5\66\1\uffff\1\66\1\uffff\6\66\2\uffff"+
            "\12\66\2\uffff\2\66\42\uffff\72\66\4\uffff\12\66\1\uffff\42"+
            "\66\6\uffff\33\66\4\uffff\10\66\1\uffff\44\66\1\uffff\17\66"+
            "\2\uffff\1\66\60\uffff\42\66\1\uffff\5\66\1\uffff\2\66\1\uffff"+
            "\7\66\3\uffff\4\66\6\uffff\32\66\106\uffff\46\66\12\uffff\51"+
            "\66\2\uffff\1\66\4\uffff\132\66\5\uffff\104\66\5\uffff\122\66"+
            "\6\uffff\7\66\1\uffff\77\66\1\uffff\1\66\1\uffff\4\66\2\uffff"+
            "\7\66\1\uffff\1\66\1\uffff\4\66\2\uffff\47\66\1\uffff\1\66\1"+
            "\uffff\4\66\2\uffff\37\66\1\uffff\1\66\1\uffff\4\66\2\uffff"+
            "\7\66\1\uffff\1\66\1\uffff\4\66\2\uffff\7\66\1\uffff\7\66\1"+
            "\uffff\27\66\1\uffff\37\66\1\uffff\1\66\1\uffff\4\66\2\uffff"+
            "\7\66\1\uffff\47\66\1\uffff\23\66\6\uffff\34\66\43\uffff\125"+
            "\66\14\uffff\u0276\66\12\uffff\32\66\5\uffff\121\66\17\uffff"+
            "\15\66\1\uffff\7\66\13\uffff\27\66\11\uffff\24\66\14\uffff\15"+
            "\66\1\uffff\3\66\1\uffff\2\66\14\uffff\64\66\2\uffff\50\66\2"+
            "\uffff\12\66\6\uffff\12\66\6\uffff\16\66\2\uffff\12\66\6\uffff"+
            "\130\66\10\uffff\52\66\126\uffff\35\66\3\uffff\14\66\4\uffff"+
            "\14\66\4\uffff\1\66\3\uffff\52\66\2\uffff\5\66\153\uffff\40"+
            "\66\u0300\uffff\154\66\u0094\uffff\u009c\66\4\uffff\132\66\6"+
            "\uffff\26\66\2\uffff\6\66\2\uffff\46\66\2\uffff\6\66\2\uffff"+
            "\10\66\1\uffff\1\66\1\uffff\1\66\1\uffff\1\66\1\uffff\37\66"+
            "\2\uffff\65\66\1\uffff\7\66\1\uffff\1\66\3\uffff\3\66\1\uffff"+
            "\7\66\3\uffff\4\66\2\uffff\6\66\4\uffff\15\66\5\uffff\3\66\1"+
            "\uffff\7\66\23\uffff\10\66\10\uffff\10\66\10\uffff\11\66\2\uffff"+
            "\12\66\2\uffff\16\66\2\uffff\1\66\30\uffff\2\66\2\uffff\11\66"+
            "\2\uffff\16\66\23\uffff\22\66\36\uffff\33\66\25\uffff\74\66"+
            "\1\uffff\17\66\7\uffff\61\66\14\uffff\u0199\66\2\uffff\u0089"+
            "\66\2\uffff\33\66\57\uffff\47\66\31\uffff\13\66\25\uffff\u01b8"+
            "\66\1\uffff\145\66\2\uffff\22\66\16\uffff\2\66\137\uffff\4\66"+
            "\1\uffff\4\66\2\uffff\34\66\1\uffff\43\66\1\uffff\1\66\1\uffff"+
            "\4\66\3\uffff\1\66\1\uffff\7\66\2\uffff\7\66\16\uffff\37\66"+
            "\3\uffff\30\66\1\uffff\16\66\21\uffff\26\66\12\uffff\u0193\66"+
            "\26\uffff\77\66\4\uffff\40\66\2\uffff\u0110\66\u0372\uffff\32"+
            "\66\1\uffff\131\66\14\uffff\u00d6\66\32\uffff\14\66\5\uffff"+
            "\7\66\12\uffff\2\66\10\uffff\1\66\3\uffff\40\66\1\uffff\126"+
            "\66\2\uffff\2\66\2\uffff\143\66\5\uffff\50\66\4\uffff\136\66"+
            "\1\uffff\50\66\70\uffff\57\66\1\uffff\44\66\14\uffff\56\66\1"+
            "\uffff\u0080\66\1\uffff\u1ab6\66\12\uffff\u51e6\66\132\uffff"+
            "\u048d\66\3\uffff\67\66\u0739\uffff\u2ba4\66\u215c\uffff\u012e"+
            "\66\2\uffff\73\66\u0095\uffff\7\66\14\uffff\5\66\5\uffff\32"+
            "\66\1\uffff\5\66\1\uffff\1\66\1\uffff\2\66\1\uffff\2\66\1\uffff"+
            "\154\66\41\uffff\u016b\66\22\uffff\100\66\2\uffff\66\66\50\uffff"+
            "\16\66\2\uffff\20\66\20\uffff\4\66\14\uffff\5\66\20\uffff\2"+
            "\66\2\uffff\12\66\1\uffff\5\66\6\uffff\10\66\1\uffff\4\66\4"+
            "\uffff\5\66\1\uffff\u0087\66\4\uffff\7\66\2\uffff\61\66\1\uffff"+
            "\1\66\2\uffff\1\66\1\uffff\32\66\1\uffff\1\66\1\uffff\1\66\2"+
            "\uffff\1\66\2\uffff\133\66\3\uffff\6\66\2\uffff\6\66\2\uffff"+
            "\6\66\2\uffff\3\66\3\uffff\3\66\1\uffff\3\66\1\uffff\7\66",
            "\2\66\1\63\1\62\3\66\2\uffff\1\67\1\66\1\uffff\1\66\1\uffff"+
            "\1\66\12\60\1\uffff\1\66\1\uffff\1\66\1\uffff\1\70\1\66\32\57"+
            "\1\uffff\1\64\2\uffff\1\61\1\uffff\32\56\1\uffff\1\66\44\uffff"+
            "\7\66\1\uffff\2\66\1\uffff\1\66\1\uffff\1\66\1\uffff\4\66\1"+
            "\uffff\3\66\1\uffff\2\66\1\uffff\u017b\66\31\uffff\162\66\4"+
            "\uffff\14\66\16\uffff\5\66\11\uffff\1\66\21\uffff\130\66\5\uffff"+
            "\23\66\12\uffff\1\66\3\uffff\1\66\7\uffff\5\66\1\uffff\1\66"+
            "\1\uffff\24\66\1\uffff\54\66\1\uffff\54\66\4\uffff\u0087\66"+
            "\1\uffff\107\66\1\uffff\46\66\2\uffff\2\66\6\uffff\20\66\41"+
            "\uffff\46\66\2\uffff\7\66\1\uffff\47\66\1\uffff\2\66\6\uffff"+
            "\21\66\1\uffff\27\66\1\uffff\12\66\13\uffff\33\66\5\uffff\5"+
            "\66\27\uffff\12\66\5\uffff\1\66\3\uffff\1\66\1\uffff\32\66\5"+
            "\uffff\31\66\7\uffff\175\66\1\uffff\60\66\2\uffff\73\66\2\uffff"+
            "\3\66\60\uffff\62\66\u014f\uffff\71\66\2\uffff\22\66\2\uffff"+
            "\5\66\3\uffff\31\66\20\uffff\3\66\1\uffff\10\66\2\uffff\2\66"+
            "\2\uffff\26\66\1\uffff\7\66\1\uffff\1\66\3\uffff\4\66\2\uffff"+
            "\11\66\2\uffff\2\66\2\uffff\3\66\11\uffff\1\66\4\uffff\2\66"+
            "\1\uffff\5\66\2\uffff\25\66\6\uffff\3\66\1\uffff\6\66\4\uffff"+
            "\2\66\2\uffff\26\66\1\uffff\7\66\1\uffff\2\66\1\uffff\2\66\1"+
            "\uffff\2\66\2\uffff\1\66\1\uffff\5\66\4\uffff\2\66\2\uffff\3"+
            "\66\13\uffff\4\66\1\uffff\1\66\7\uffff\17\66\14\uffff\3\66\1"+
            "\uffff\11\66\1\uffff\3\66\1\uffff\26\66\1\uffff\7\66\1\uffff"+
            "\2\66\1\uffff\5\66\2\uffff\12\66\1\uffff\3\66\1\uffff\3\66\2"+
            "\uffff\1\66\17\uffff\4\66\2\uffff\12\66\1\uffff\1\66\17\uffff"+
            "\3\66\1\uffff\10\66\2\uffff\2\66\2\uffff\26\66\1\uffff\7\66"+
            "\1\uffff\2\66\1\uffff\5\66\2\uffff\10\66\3\uffff\2\66\2\uffff"+
            "\3\66\10\uffff\2\66\4\uffff\2\66\1\uffff\3\66\4\uffff\14\66"+
            "\20\uffff\2\66\1\uffff\6\66\3\uffff\3\66\1\uffff\4\66\3\uffff"+
            "\2\66\1\uffff\1\66\1\uffff\2\66\3\uffff\2\66\3\uffff\3\66\3"+
            "\uffff\10\66\1\uffff\3\66\4\uffff\5\66\3\uffff\3\66\1\uffff"+
            "\4\66\11\uffff\1\66\17\uffff\24\66\6\uffff\3\66\1\uffff\10\66"+
            "\1\uffff\3\66\1\uffff\27\66\1\uffff\12\66\1\uffff\5\66\4\uffff"+
            "\7\66\1\uffff\3\66\1\uffff\4\66\7\uffff\2\66\11\uffff\2\66\4"+
            "\uffff\12\66\22\uffff\2\66\1\uffff\10\66\1\uffff\3\66\1\uffff"+
            "\27\66\1\uffff\12\66\1\uffff\5\66\2\uffff\11\66\1\uffff\3\66"+
            "\1\uffff\4\66\7\uffff\2\66\7\uffff\1\66\1\uffff\2\66\4\uffff"+
            "\12\66\22\uffff\2\66\1\uffff\10\66\1\uffff\3\66\1\uffff\27\66"+
            "\1\uffff\20\66\4\uffff\6\66\2\uffff\3\66\1\uffff\4\66\11\uffff"+
            "\1\66\10\uffff\2\66\4\uffff\12\66\22\uffff\2\66\1\uffff\22\66"+
            "\3\uffff\30\66\1\uffff\11\66\1\uffff\1\66\2\uffff\7\66\3\uffff"+
            "\1\66\4\uffff\6\66\1\uffff\1\66\1\uffff\10\66\22\uffff\3\66"+
            "\14\uffff\72\66\4\uffff\35\66\45\uffff\2\66\1\uffff\1\66\2\uffff"+
            "\2\66\1\uffff\1\66\2\uffff\1\66\6\uffff\4\66\1\uffff\7\66\1"+
            "\uffff\3\66\1\uffff\1\66\1\uffff\1\66\2\uffff\2\66\1\uffff\15"+
            "\66\1\uffff\3\66\2\uffff\5\66\1\uffff\1\66\1\uffff\6\66\2\uffff"+
            "\12\66\2\uffff\2\66\42\uffff\72\66\4\uffff\12\66\1\uffff\42"+
            "\66\6\uffff\33\66\4\uffff\10\66\1\uffff\44\66\1\uffff\17\66"+
            "\2\uffff\1\66\60\uffff\42\66\1\uffff\5\66\1\uffff\2\66\1\uffff"+
            "\7\66\3\uffff\4\66\6\uffff\32\66\106\uffff\46\66\12\uffff\51"+
            "\66\2\uffff\1\66\4\uffff\132\66\5\uffff\104\66\5\uffff\122\66"+
            "\6\uffff\7\66\1\uffff\77\66\1\uffff\1\66\1\uffff\4\66\2\uffff"+
            "\7\66\1\uffff\1\66\1\uffff\4\66\2\uffff\47\66\1\uffff\1\66\1"+
            "\uffff\4\66\2\uffff\37\66\1\uffff\1\66\1\uffff\4\66\2\uffff"+
            "\7\66\1\uffff\1\66\1\uffff\4\66\2\uffff\7\66\1\uffff\7\66\1"+
            "\uffff\27\66\1\uffff\37\66\1\uffff\1\66\1\uffff\4\66\2\uffff"+
            "\7\66\1\uffff\47\66\1\uffff\23\66\6\uffff\34\66\43\uffff\125"+
            "\66\14\uffff\u0276\66\12\uffff\32\66\5\uffff\121\66\17\uffff"+
            "\15\66\1\uffff\7\66\13\uffff\27\66\11\uffff\24\66\14\uffff\15"+
            "\66\1\uffff\3\66\1\uffff\2\66\14\uffff\64\66\2\uffff\50\66\2"+
            "\uffff\12\66\6\uffff\12\66\6\uffff\16\66\2\uffff\12\66\6\uffff"+
            "\130\66\10\uffff\52\66\126\uffff\35\66\3\uffff\14\66\4\uffff"+
            "\14\66\4\uffff\1\66\3\uffff\52\66\2\uffff\5\66\153\uffff\40"+
            "\66\u0300\uffff\154\66\u0094\uffff\u009c\66\4\uffff\132\66\6"+
            "\uffff\26\66\2\uffff\6\66\2\uffff\46\66\2\uffff\6\66\2\uffff"+
            "\10\66\1\uffff\1\66\1\uffff\1\66\1\uffff\1\66\1\uffff\37\66"+
            "\2\uffff\65\66\1\uffff\7\66\1\uffff\1\66\3\uffff\3\66\1\uffff"+
            "\7\66\3\uffff\4\66\2\uffff\6\66\4\uffff\15\66\5\uffff\3\66\1"+
            "\uffff\7\66\23\uffff\10\66\10\uffff\10\66\10\uffff\11\66\2\uffff"+
            "\12\66\2\uffff\16\66\2\uffff\1\66\30\uffff\2\66\2\uffff\11\66"+
            "\2\uffff\16\66\23\uffff\22\66\36\uffff\33\66\25\uffff\74\66"+
            "\1\uffff\17\66\7\uffff\61\66\14\uffff\u0199\66\2\uffff\u0089"+
            "\66\2\uffff\33\66\57\uffff\47\66\31\uffff\13\66\25\uffff\u01b8"+
            "\66\1\uffff\145\66\2\uffff\22\66\16\uffff\2\66\137\uffff\4\66"+
            "\1\uffff\4\66\2\uffff\34\66\1\uffff\43\66\1\uffff\1\66\1\uffff"+
            "\4\66\3\uffff\1\66\1\uffff\7\66\2\uffff\7\66\16\uffff\37\66"+
            "\3\uffff\30\66\1\uffff\16\66\21\uffff\26\66\12\uffff\u0193\66"+
            "\26\uffff\77\66\4\uffff\40\66\2\uffff\u0110\66\u0372\uffff\32"+
            "\66\1\uffff\131\66\14\uffff\u00d6\66\32\uffff\14\66\5\uffff"+
            "\7\66\12\uffff\2\66\10\uffff\1\66\3\uffff\40\66\1\uffff\126"+
            "\66\2\uffff\2\66\2\uffff\143\66\5\uffff\50\66\4\uffff\136\66"+
            "\1\uffff\50\66\70\uffff\57\66\1\uffff\44\66\14\uffff\56\66\1"+
            "\uffff\u0080\66\1\uffff\u1ab6\66\12\uffff\u51e6\66\132\uffff"+
            "\u048d\66\3\uffff\67\66\u0739\uffff\u2ba4\66\u215c\uffff\u012e"+
            "\66\2\uffff\73\66\u0095\uffff\7\66\14\uffff\5\66\5\uffff\32"+
            "\66\1\uffff\5\66\1\uffff\1\66\1\uffff\2\66\1\uffff\2\66\1\uffff"+
            "\154\66\41\uffff\u016b\66\22\uffff\100\66\2\uffff\66\66\50\uffff"+
            "\16\66\2\uffff\20\66\20\uffff\4\66\14\uffff\5\66\20\uffff\2"+
            "\66\2\uffff\12\66\1\uffff\5\66\6\uffff\10\66\1\uffff\4\66\4"+
            "\uffff\5\66\1\uffff\u0087\66\4\uffff\7\66\2\uffff\61\66\1\uffff"+
            "\1\66\2\uffff\1\66\1\uffff\32\66\1\uffff\1\66\1\uffff\1\66\2"+
            "\uffff\1\66\2\uffff\133\66\3\uffff\6\66\2\uffff\6\66\2\uffff"+
            "\6\66\2\uffff\3\66\3\uffff\3\66\1\uffff\3\66\1\uffff\7\66",
            "",
            "\7\66\2\uffff\1\67\1\66\1\uffff\1\66\1\uffff\1\66\12\143\1"+
            "\uffff\1\66\1\uffff\1\66\1\uffff\1\70\1\66\6\143\24\66\1\uffff"+
            "\1\110\2\uffff\1\66\1\uffff\6\143\24\66\1\uffff\1\66\44\uffff"+
            "\7\66\1\uffff\2\66\1\uffff\1\66\1\uffff\1\66\1\uffff\4\66\1"+
            "\uffff\3\66\1\uffff\2\66\1\uffff\u017b\66\31\uffff\162\66\4"+
            "\uffff\14\66\16\uffff\5\66\11\uffff\1\66\21\uffff\130\66\5\uffff"+
            "\23\66\12\uffff\1\66\3\uffff\1\66\7\uffff\5\66\1\uffff\1\66"+
            "\1\uffff\24\66\1\uffff\54\66\1\uffff\54\66\4\uffff\u0087\66"+
            "\1\uffff\107\66\1\uffff\46\66\2\uffff\2\66\6\uffff\20\66\41"+
            "\uffff\46\66\2\uffff\7\66\1\uffff\47\66\1\uffff\2\66\6\uffff"+
            "\21\66\1\uffff\27\66\1\uffff\12\66\13\uffff\33\66\5\uffff\5"+
            "\66\27\uffff\12\66\5\uffff\1\66\3\uffff\1\66\1\uffff\32\66\5"+
            "\uffff\31\66\7\uffff\175\66\1\uffff\60\66\2\uffff\73\66\2\uffff"+
            "\3\66\60\uffff\62\66\u014f\uffff\71\66\2\uffff\22\66\2\uffff"+
            "\5\66\3\uffff\31\66\20\uffff\3\66\1\uffff\10\66\2\uffff\2\66"+
            "\2\uffff\26\66\1\uffff\7\66\1\uffff\1\66\3\uffff\4\66\2\uffff"+
            "\11\66\2\uffff\2\66\2\uffff\3\66\11\uffff\1\66\4\uffff\2\66"+
            "\1\uffff\5\66\2\uffff\25\66\6\uffff\3\66\1\uffff\6\66\4\uffff"+
            "\2\66\2\uffff\26\66\1\uffff\7\66\1\uffff\2\66\1\uffff\2\66\1"+
            "\uffff\2\66\2\uffff\1\66\1\uffff\5\66\4\uffff\2\66\2\uffff\3"+
            "\66\13\uffff\4\66\1\uffff\1\66\7\uffff\17\66\14\uffff\3\66\1"+
            "\uffff\11\66\1\uffff\3\66\1\uffff\26\66\1\uffff\7\66\1\uffff"+
            "\2\66\1\uffff\5\66\2\uffff\12\66\1\uffff\3\66\1\uffff\3\66\2"+
            "\uffff\1\66\17\uffff\4\66\2\uffff\12\66\1\uffff\1\66\17\uffff"+
            "\3\66\1\uffff\10\66\2\uffff\2\66\2\uffff\26\66\1\uffff\7\66"+
            "\1\uffff\2\66\1\uffff\5\66\2\uffff\10\66\3\uffff\2\66\2\uffff"+
            "\3\66\10\uffff\2\66\4\uffff\2\66\1\uffff\3\66\4\uffff\14\66"+
            "\20\uffff\2\66\1\uffff\6\66\3\uffff\3\66\1\uffff\4\66\3\uffff"+
            "\2\66\1\uffff\1\66\1\uffff\2\66\3\uffff\2\66\3\uffff\3\66\3"+
            "\uffff\10\66\1\uffff\3\66\4\uffff\5\66\3\uffff\3\66\1\uffff"+
            "\4\66\11\uffff\1\66\17\uffff\24\66\6\uffff\3\66\1\uffff\10\66"+
            "\1\uffff\3\66\1\uffff\27\66\1\uffff\12\66\1\uffff\5\66\4\uffff"+
            "\7\66\1\uffff\3\66\1\uffff\4\66\7\uffff\2\66\11\uffff\2\66\4"+
            "\uffff\12\66\22\uffff\2\66\1\uffff\10\66\1\uffff\3\66\1\uffff"+
            "\27\66\1\uffff\12\66\1\uffff\5\66\2\uffff\11\66\1\uffff\3\66"+
            "\1\uffff\4\66\7\uffff\2\66\7\uffff\1\66\1\uffff\2\66\4\uffff"+
            "\12\66\22\uffff\2\66\1\uffff\10\66\1\uffff\3\66\1\uffff\27\66"+
            "\1\uffff\20\66\4\uffff\6\66\2\uffff\3\66\1\uffff\4\66\11\uffff"+
            "\1\66\10\uffff\2\66\4\uffff\12\66\22\uffff\2\66\1\uffff\22\66"+
            "\3\uffff\30\66\1\uffff\11\66\1\uffff\1\66\2\uffff\7\66\3\uffff"+
            "\1\66\4\uffff\6\66\1\uffff\1\66\1\uffff\10\66\22\uffff\3\66"+
            "\14\uffff\72\66\4\uffff\35\66\45\uffff\2\66\1\uffff\1\66\2\uffff"+
            "\2\66\1\uffff\1\66\2\uffff\1\66\6\uffff\4\66\1\uffff\7\66\1"+
            "\uffff\3\66\1\uffff\1\66\1\uffff\1\66\2\uffff\2\66\1\uffff\15"+
            "\66\1\uffff\3\66\2\uffff\5\66\1\uffff\1\66\1\uffff\6\66\2\uffff"+
            "\12\66\2\uffff\2\66\42\uffff\72\66\4\uffff\12\66\1\uffff\42"+
            "\66\6\uffff\33\66\4\uffff\10\66\1\uffff\44\66\1\uffff\17\66"+
            "\2\uffff\1\66\60\uffff\42\66\1\uffff\5\66\1\uffff\2\66\1\uffff"+
            "\7\66\3\uffff\4\66\6\uffff\32\66\106\uffff\46\66\12\uffff\51"+
            "\66\2\uffff\1\66\4\uffff\132\66\5\uffff\104\66\5\uffff\122\66"+
            "\6\uffff\7\66\1\uffff\77\66\1\uffff\1\66\1\uffff\4\66\2\uffff"+
            "\7\66\1\uffff\1\66\1\uffff\4\66\2\uffff\47\66\1\uffff\1\66\1"+
            "\uffff\4\66\2\uffff\37\66\1\uffff\1\66\1\uffff\4\66\2\uffff"+
            "\7\66\1\uffff\1\66\1\uffff\4\66\2\uffff\7\66\1\uffff\7\66\1"+
            "\uffff\27\66\1\uffff\37\66\1\uffff\1\66\1\uffff\4\66\2\uffff"+
            "\7\66\1\uffff\47\66\1\uffff\23\66\6\uffff\34\66\43\uffff\125"+
            "\66\14\uffff\u0276\66\12\uffff\32\66\5\uffff\121\66\17\uffff"+
            "\15\66\1\uffff\7\66\13\uffff\27\66\11\uffff\24\66\14\uffff\15"+
            "\66\1\uffff\3\66\1\uffff\2\66\14\uffff\64\66\2\uffff\50\66\2"+
            "\uffff\12\66\6\uffff\12\66\6\uffff\16\66\2\uffff\12\66\6\uffff"+
            "\130\66\10\uffff\52\66\126\uffff\35\66\3\uffff\14\66\4\uffff"+
            "\14\66\4\uffff\1\66\3\uffff\52\66\2\uffff\5\66\153\uffff\40"+
            "\66\u0300\uffff\154\66\u0094\uffff\u009c\66\4\uffff\132\66\6"+
            "\uffff\26\66\2\uffff\6\66\2\uffff\46\66\2\uffff\6\66\2\uffff"+
            "\10\66\1\uffff\1\66\1\uffff\1\66\1\uffff\1\66\1\uffff\37\66"+
            "\2\uffff\65\66\1\uffff\7\66\1\uffff\1\66\3\uffff\3\66\1\uffff"+
            "\7\66\3\uffff\4\66\2\uffff\6\66\4\uffff\15\66\5\uffff\3\66\1"+
            "\uffff\7\66\23\uffff\10\66\10\uffff\10\66\10\uffff\11\66\2\uffff"+
            "\12\66\2\uffff\16\66\2\uffff\1\66\30\uffff\2\66\2\uffff\11\66"+
            "\2\uffff\16\66\23\uffff\22\66\36\uffff\33\66\25\uffff\74\66"+
            "\1\uffff\17\66\7\uffff\61\66\14\uffff\u0199\66\2\uffff\u0089"+
            "\66\2\uffff\33\66\57\uffff\47\66\31\uffff\13\66\25\uffff\u01b8"+
            "\66\1\uffff\145\66\2\uffff\22\66\16\uffff\2\66\137\uffff\4\66"+
            "\1\uffff\4\66\2\uffff\34\66\1\uffff\43\66\1\uffff\1\66\1\uffff"+
            "\4\66\3\uffff\1\66\1\uffff\7\66\2\uffff\7\66\16\uffff\37\66"+
            "\3\uffff\30\66\1\uffff\16\66\21\uffff\26\66\12\uffff\u0193\66"+
            "\26\uffff\77\66\4\uffff\40\66\2\uffff\u0110\66\u0372\uffff\32"+
            "\66\1\uffff\131\66\14\uffff\u00d6\66\32\uffff\14\66\5\uffff"+
            "\7\66\12\uffff\2\66\10\uffff\1\66\3\uffff\40\66\1\uffff\126"+
            "\66\2\uffff\2\66\2\uffff\143\66\5\uffff\50\66\4\uffff\136\66"+
            "\1\uffff\50\66\70\uffff\57\66\1\uffff\44\66\14\uffff\56\66\1"+
            "\uffff\u0080\66\1\uffff\u1ab6\66\12\uffff\u51e6\66\132\uffff"+
            "\u048d\66\3\uffff\67\66\u0739\uffff\u2ba4\66\u215c\uffff\u012e"+
            "\66\2\uffff\73\66\u0095\uffff\7\66\14\uffff\5\66\5\uffff\32"+
            "\66\1\uffff\5\66\1\uffff\1\66\1\uffff\2\66\1\uffff\2\66\1\uffff"+
            "\154\66\41\uffff\u016b\66\22\uffff\100\66\2\uffff\66\66\50\uffff"+
            "\16\66\2\uffff\20\66\20\uffff\4\66\14\uffff\5\66\20\uffff\2"+
            "\66\2\uffff\12\66\1\uffff\5\66\6\uffff\10\66\1\uffff\4\66\4"+
            "\uffff\5\66\1\uffff\u0087\66\4\uffff\7\66\2\uffff\61\66\1\uffff"+
            "\1\66\2\uffff\1\66\1\uffff\32\66\1\uffff\1\66\1\uffff\1\66\2"+
            "\uffff\1\66\2\uffff\133\66\3\uffff\6\66\2\uffff\6\66\2\uffff"+
            "\6\66\2\uffff\3\66\3\uffff\3\66\1\uffff\3\66\1\uffff\7\66",
            "\7\66\2\uffff\1\67\1\66\1\uffff\1\66\1\uffff\13\66\1\uffff"+
            "\1\66\1\uffff\1\66\1\uffff\1\70\33\66\1\uffff\1\110\2\uffff"+
            "\1\66\1\uffff\32\66\1\uffff\1\66\44\uffff\7\66\1\uffff\2\66"+
            "\1\uffff\1\66\1\uffff\1\66\1\uffff\4\66\1\uffff\3\66\1\uffff"+
            "\2\66\1\uffff\u017b\66\31\uffff\162\66\4\uffff\14\66\16\uffff"+
            "\5\66\11\uffff\1\66\21\uffff\130\66\5\uffff\23\66\12\uffff\1"+
            "\66\3\uffff\1\66\7\uffff\5\66\1\uffff\1\66\1\uffff\24\66\1\uffff"+
            "\54\66\1\uffff\54\66\4\uffff\u0087\66\1\uffff\107\66\1\uffff"+
            "\46\66\2\uffff\2\66\6\uffff\20\66\41\uffff\46\66\2\uffff\7\66"+
            "\1\uffff\47\66\1\uffff\2\66\6\uffff\21\66\1\uffff\27\66\1\uffff"+
            "\12\66\13\uffff\33\66\5\uffff\5\66\27\uffff\12\66\5\uffff\1"+
            "\66\3\uffff\1\66\1\uffff\32\66\5\uffff\31\66\7\uffff\175\66"+
            "\1\uffff\60\66\2\uffff\73\66\2\uffff\3\66\60\uffff\62\66\u014f"+
            "\uffff\71\66\2\uffff\22\66\2\uffff\5\66\3\uffff\31\66\20\uffff"+
            "\3\66\1\uffff\10\66\2\uffff\2\66\2\uffff\26\66\1\uffff\7\66"+
            "\1\uffff\1\66\3\uffff\4\66\2\uffff\11\66\2\uffff\2\66\2\uffff"+
            "\3\66\11\uffff\1\66\4\uffff\2\66\1\uffff\5\66\2\uffff\25\66"+
            "\6\uffff\3\66\1\uffff\6\66\4\uffff\2\66\2\uffff\26\66\1\uffff"+
            "\7\66\1\uffff\2\66\1\uffff\2\66\1\uffff\2\66\2\uffff\1\66\1"+
            "\uffff\5\66\4\uffff\2\66\2\uffff\3\66\13\uffff\4\66\1\uffff"+
            "\1\66\7\uffff\17\66\14\uffff\3\66\1\uffff\11\66\1\uffff\3\66"+
            "\1\uffff\26\66\1\uffff\7\66\1\uffff\2\66\1\uffff\5\66\2\uffff"+
            "\12\66\1\uffff\3\66\1\uffff\3\66\2\uffff\1\66\17\uffff\4\66"+
            "\2\uffff\12\66\1\uffff\1\66\17\uffff\3\66\1\uffff\10\66\2\uffff"+
            "\2\66\2\uffff\26\66\1\uffff\7\66\1\uffff\2\66\1\uffff\5\66\2"+
            "\uffff\10\66\3\uffff\2\66\2\uffff\3\66\10\uffff\2\66\4\uffff"+
            "\2\66\1\uffff\3\66\4\uffff\14\66\20\uffff\2\66\1\uffff\6\66"+
            "\3\uffff\3\66\1\uffff\4\66\3\uffff\2\66\1\uffff\1\66\1\uffff"+
            "\2\66\3\uffff\2\66\3\uffff\3\66\3\uffff\10\66\1\uffff\3\66\4"+
            "\uffff\5\66\3\uffff\3\66\1\uffff\4\66\11\uffff\1\66\17\uffff"+
            "\24\66\6\uffff\3\66\1\uffff\10\66\1\uffff\3\66\1\uffff\27\66"+
            "\1\uffff\12\66\1\uffff\5\66\4\uffff\7\66\1\uffff\3\66\1\uffff"+
            "\4\66\7\uffff\2\66\11\uffff\2\66\4\uffff\12\66\22\uffff\2\66"+
            "\1\uffff\10\66\1\uffff\3\66\1\uffff\27\66\1\uffff\12\66\1\uffff"+
            "\5\66\2\uffff\11\66\1\uffff\3\66\1\uffff\4\66\7\uffff\2\66\7"+
            "\uffff\1\66\1\uffff\2\66\4\uffff\12\66\22\uffff\2\66\1\uffff"+
            "\10\66\1\uffff\3\66\1\uffff\27\66\1\uffff\20\66\4\uffff\6\66"+
            "\2\uffff\3\66\1\uffff\4\66\11\uffff\1\66\10\uffff\2\66\4\uffff"+
            "\12\66\22\uffff\2\66\1\uffff\22\66\3\uffff\30\66\1\uffff\11"+
            "\66\1\uffff\1\66\2\uffff\7\66\3\uffff\1\66\4\uffff\6\66\1\uffff"+
            "\1\66\1\uffff\10\66\22\uffff\3\66\14\uffff\72\66\4\uffff\35"+
            "\66\45\uffff\2\66\1\uffff\1\66\2\uffff\2\66\1\uffff\1\66\2\uffff"+
            "\1\66\6\uffff\4\66\1\uffff\7\66\1\uffff\3\66\1\uffff\1\66\1"+
            "\uffff\1\66\2\uffff\2\66\1\uffff\15\66\1\uffff\3\66\2\uffff"+
            "\5\66\1\uffff\1\66\1\uffff\6\66\2\uffff\12\66\2\uffff\2\66\42"+
            "\uffff\72\66\4\uffff\12\66\1\uffff\42\66\6\uffff\33\66\4\uffff"+
            "\10\66\1\uffff\44\66\1\uffff\17\66\2\uffff\1\66\60\uffff\42"+
            "\66\1\uffff\5\66\1\uffff\2\66\1\uffff\7\66\3\uffff\4\66\6\uffff"+
            "\32\66\106\uffff\46\66\12\uffff\51\66\2\uffff\1\66\4\uffff\132"+
            "\66\5\uffff\104\66\5\uffff\122\66\6\uffff\7\66\1\uffff\77\66"+
            "\1\uffff\1\66\1\uffff\4\66\2\uffff\7\66\1\uffff\1\66\1\uffff"+
            "\4\66\2\uffff\47\66\1\uffff\1\66\1\uffff\4\66\2\uffff\37\66"+
            "\1\uffff\1\66\1\uffff\4\66\2\uffff\7\66\1\uffff\1\66\1\uffff"+
            "\4\66\2\uffff\7\66\1\uffff\7\66\1\uffff\27\66\1\uffff\37\66"+
            "\1\uffff\1\66\1\uffff\4\66\2\uffff\7\66\1\uffff\47\66\1\uffff"+
            "\23\66\6\uffff\34\66\43\uffff\125\66\14\uffff\u0276\66\12\uffff"+
            "\32\66\5\uffff\121\66\17\uffff\15\66\1\uffff\7\66\13\uffff\27"+
            "\66\11\uffff\24\66\14\uffff\15\66\1\uffff\3\66\1\uffff\2\66"+
            "\14\uffff\64\66\2\uffff\50\66\2\uffff\12\66\6\uffff\12\66\6"+
            "\uffff\16\66\2\uffff\12\66\6\uffff\130\66\10\uffff\52\66\126"+
            "\uffff\35\66\3\uffff\14\66\4\uffff\14\66\4\uffff\1\66\3\uffff"+
            "\52\66\2\uffff\5\66\153\uffff\40\66\u0300\uffff\154\66\u0094"+
            "\uffff\u009c\66\4\uffff\132\66\6\uffff\26\66\2\uffff\6\66\2"+
            "\uffff\46\66\2\uffff\6\66\2\uffff\10\66\1\uffff\1\66\1\uffff"+
            "\1\66\1\uffff\1\66\1\uffff\37\66\2\uffff\65\66\1\uffff\7\66"+
            "\1\uffff\1\66\3\uffff\3\66\1\uffff\7\66\3\uffff\4\66\2\uffff"+
            "\6\66\4\uffff\15\66\5\uffff\3\66\1\uffff\7\66\23\uffff\10\66"+
            "\10\uffff\10\66\10\uffff\11\66\2\uffff\12\66\2\uffff\16\66\2"+
            "\uffff\1\66\30\uffff\2\66\2\uffff\11\66\2\uffff\16\66\23\uffff"+
            "\22\66\36\uffff\33\66\25\uffff\74\66\1\uffff\17\66\7\uffff\61"+
            "\66\14\uffff\u0199\66\2\uffff\u0089\66\2\uffff\33\66\57\uffff"+
            "\47\66\31\uffff\13\66\25\uffff\u01b8\66\1\uffff\145\66\2\uffff"+
            "\22\66\16\uffff\2\66\137\uffff\4\66\1\uffff\4\66\2\uffff\34"+
            "\66\1\uffff\43\66\1\uffff\1\66\1\uffff\4\66\3\uffff\1\66\1\uffff"+
            "\7\66\2\uffff\7\66\16\uffff\37\66\3\uffff\30\66\1\uffff\16\66"+
            "\21\uffff\26\66\12\uffff\u0193\66\26\uffff\77\66\4\uffff\40"+
            "\66\2\uffff\u0110\66\u0372\uffff\32\66\1\uffff\131\66\14\uffff"+
            "\u00d6\66\32\uffff\14\66\5\uffff\7\66\12\uffff\2\66\10\uffff"+
            "\1\66\3\uffff\40\66\1\uffff\126\66\2\uffff\2\66\2\uffff\143"+
            "\66\5\uffff\50\66\4\uffff\136\66\1\uffff\50\66\70\uffff\57\66"+
            "\1\uffff\44\66\14\uffff\56\66\1\uffff\u0080\66\1\uffff\u1ab6"+
            "\66\12\uffff\u51e6\66\132\uffff\u048d\66\3\uffff\67\66\u0739"+
            "\uffff\u2ba4\66\u215c\uffff\u012e\66\2\uffff\73\66\u0095\uffff"+
            "\7\66\14\uffff\5\66\5\uffff\32\66\1\uffff\5\66\1\uffff\1\66"+
            "\1\uffff\2\66\1\uffff\2\66\1\uffff\154\66\41\uffff\u016b\66"+
            "\22\uffff\100\66\2\uffff\66\66\50\uffff\16\66\2\uffff\20\66"+
            "\20\uffff\4\66\14\uffff\5\66\20\uffff\2\66\2\uffff\12\66\1\uffff"+
            "\5\66\6\uffff\10\66\1\uffff\4\66\4\uffff\5\66\1\uffff\u0087"+
            "\66\4\uffff\7\66\2\uffff\61\66\1\uffff\1\66\2\uffff\1\66\1\uffff"+
            "\32\66\1\uffff\1\66\1\uffff\1\66\2\uffff\1\66\2\uffff\133\66"+
            "\3\uffff\6\66\2\uffff\6\66\2\uffff\6\66\2\uffff\3\66\3\uffff"+
            "\3\66\1\uffff\3\66\1\uffff\7\66",
            "\7\66\2\uffff\1\67\1\66\1\uffff\1\66\1\uffff\1\66\12\134\1"+
            "\uffff\1\66\1\uffff\1\66\1\uffff\1\70\33\66\1\uffff\1\110\2"+
            "\uffff\1\66\1\uffff\32\66\1\uffff\1\66\44\uffff\7\66\1\uffff"+
            "\2\66\1\uffff\1\66\1\uffff\1\66\1\uffff\4\66\1\uffff\3\66\1"+
            "\uffff\2\66\1\uffff\u017b\66\31\uffff\162\66\4\uffff\14\66\16"+
            "\uffff\5\66\11\uffff\1\66\21\uffff\130\66\5\uffff\23\66\12\uffff"+
            "\1\66\3\uffff\1\66\7\uffff\5\66\1\uffff\1\66\1\uffff\24\66\1"+
            "\uffff\54\66\1\uffff\54\66\4\uffff\u0087\66\1\uffff\107\66\1"+
            "\uffff\46\66\2\uffff\2\66\6\uffff\20\66\41\uffff\46\66\2\uffff"+
            "\7\66\1\uffff\47\66\1\uffff\2\66\6\uffff\21\66\1\uffff\27\66"+
            "\1\uffff\12\66\13\uffff\33\66\5\uffff\5\66\27\uffff\12\66\5"+
            "\uffff\1\66\3\uffff\1\66\1\uffff\32\66\5\uffff\31\66\7\uffff"+
            "\175\66\1\uffff\60\66\2\uffff\73\66\2\uffff\3\66\60\uffff\62"+
            "\66\u014f\uffff\71\66\2\uffff\22\66\2\uffff\5\66\3\uffff\31"+
            "\66\20\uffff\3\66\1\uffff\10\66\2\uffff\2\66\2\uffff\26\66\1"+
            "\uffff\7\66\1\uffff\1\66\3\uffff\4\66\2\uffff\11\66\2\uffff"+
            "\2\66\2\uffff\3\66\11\uffff\1\66\4\uffff\2\66\1\uffff\5\66\2"+
            "\uffff\25\66\6\uffff\3\66\1\uffff\6\66\4\uffff\2\66\2\uffff"+
            "\26\66\1\uffff\7\66\1\uffff\2\66\1\uffff\2\66\1\uffff\2\66\2"+
            "\uffff\1\66\1\uffff\5\66\4\uffff\2\66\2\uffff\3\66\13\uffff"+
            "\4\66\1\uffff\1\66\7\uffff\17\66\14\uffff\3\66\1\uffff\11\66"+
            "\1\uffff\3\66\1\uffff\26\66\1\uffff\7\66\1\uffff\2\66\1\uffff"+
            "\5\66\2\uffff\12\66\1\uffff\3\66\1\uffff\3\66\2\uffff\1\66\17"+
            "\uffff\4\66\2\uffff\12\66\1\uffff\1\66\17\uffff\3\66\1\uffff"+
            "\10\66\2\uffff\2\66\2\uffff\26\66\1\uffff\7\66\1\uffff\2\66"+
            "\1\uffff\5\66\2\uffff\10\66\3\uffff\2\66\2\uffff\3\66\10\uffff"+
            "\2\66\4\uffff\2\66\1\uffff\3\66\4\uffff\14\66\20\uffff\2\66"+
            "\1\uffff\6\66\3\uffff\3\66\1\uffff\4\66\3\uffff\2\66\1\uffff"+
            "\1\66\1\uffff\2\66\3\uffff\2\66\3\uffff\3\66\3\uffff\10\66\1"+
            "\uffff\3\66\4\uffff\5\66\3\uffff\3\66\1\uffff\4\66\11\uffff"+
            "\1\66\17\uffff\24\66\6\uffff\3\66\1\uffff\10\66\1\uffff\3\66"+
            "\1\uffff\27\66\1\uffff\12\66\1\uffff\5\66\4\uffff\7\66\1\uffff"+
            "\3\66\1\uffff\4\66\7\uffff\2\66\11\uffff\2\66\4\uffff\12\66"+
            "\22\uffff\2\66\1\uffff\10\66\1\uffff\3\66\1\uffff\27\66\1\uffff"+
            "\12\66\1\uffff\5\66\2\uffff\11\66\1\uffff\3\66\1\uffff\4\66"+
            "\7\uffff\2\66\7\uffff\1\66\1\uffff\2\66\4\uffff\12\66\22\uffff"+
            "\2\66\1\uffff\10\66\1\uffff\3\66\1\uffff\27\66\1\uffff\20\66"+
            "\4\uffff\6\66\2\uffff\3\66\1\uffff\4\66\11\uffff\1\66\10\uffff"+
            "\2\66\4\uffff\12\66\22\uffff\2\66\1\uffff\22\66\3\uffff\30\66"+
            "\1\uffff\11\66\1\uffff\1\66\2\uffff\7\66\3\uffff\1\66\4\uffff"+
            "\6\66\1\uffff\1\66\1\uffff\10\66\22\uffff\3\66\14\uffff\72\66"+
            "\4\uffff\35\66\45\uffff\2\66\1\uffff\1\66\2\uffff\2\66\1\uffff"+
            "\1\66\2\uffff\1\66\6\uffff\4\66\1\uffff\7\66\1\uffff\3\66\1"+
            "\uffff\1\66\1\uffff\1\66\2\uffff\2\66\1\uffff\15\66\1\uffff"+
            "\3\66\2\uffff\5\66\1\uffff\1\66\1\uffff\6\66\2\uffff\12\66\2"+
            "\uffff\2\66\42\uffff\72\66\4\uffff\12\66\1\uffff\42\66\6\uffff"+
            "\33\66\4\uffff\10\66\1\uffff\44\66\1\uffff\17\66\2\uffff\1\66"+
            "\60\uffff\42\66\1\uffff\5\66\1\uffff\2\66\1\uffff\7\66\3\uffff"+
            "\4\66\6\uffff\32\66\106\uffff\46\66\12\uffff\51\66\2\uffff\1"+
            "\66\4\uffff\132\66\5\uffff\104\66\5\uffff\122\66\6\uffff\7\66"+
            "\1\uffff\77\66\1\uffff\1\66\1\uffff\4\66\2\uffff\7\66\1\uffff"+
            "\1\66\1\uffff\4\66\2\uffff\47\66\1\uffff\1\66\1\uffff\4\66\2"+
            "\uffff\37\66\1\uffff\1\66\1\uffff\4\66\2\uffff\7\66\1\uffff"+
            "\1\66\1\uffff\4\66\2\uffff\7\66\1\uffff\7\66\1\uffff\27\66\1"+
            "\uffff\37\66\1\uffff\1\66\1\uffff\4\66\2\uffff\7\66\1\uffff"+
            "\47\66\1\uffff\23\66\6\uffff\34\66\43\uffff\125\66\14\uffff"+
            "\u0276\66\12\uffff\32\66\5\uffff\121\66\17\uffff\15\66\1\uffff"+
            "\7\66\13\uffff\27\66\11\uffff\24\66\14\uffff\15\66\1\uffff\3"+
            "\66\1\uffff\2\66\14\uffff\64\66\2\uffff\50\66\2\uffff\12\66"+
            "\6\uffff\12\66\6\uffff\16\66\2\uffff\12\66\6\uffff\130\66\10"+
            "\uffff\52\66\126\uffff\35\66\3\uffff\14\66\4\uffff\14\66\4\uffff"+
            "\1\66\3\uffff\52\66\2\uffff\5\66\153\uffff\40\66\u0300\uffff"+
            "\154\66\u0094\uffff\u009c\66\4\uffff\132\66\6\uffff\26\66\2"+
            "\uffff\6\66\2\uffff\46\66\2\uffff\6\66\2\uffff\10\66\1\uffff"+
            "\1\66\1\uffff\1\66\1\uffff\1\66\1\uffff\37\66\2\uffff\65\66"+
            "\1\uffff\7\66\1\uffff\1\66\3\uffff\3\66\1\uffff\7\66\3\uffff"+
            "\4\66\2\uffff\6\66\4\uffff\15\66\5\uffff\3\66\1\uffff\7\66\23"+
            "\uffff\10\66\10\uffff\10\66\10\uffff\11\66\2\uffff\12\66\2\uffff"+
            "\16\66\2\uffff\1\66\30\uffff\2\66\2\uffff\11\66\2\uffff\16\66"+
            "\23\uffff\22\66\36\uffff\33\66\25\uffff\74\66\1\uffff\17\66"+
            "\7\uffff\61\66\14\uffff\u0199\66\2\uffff\u0089\66\2\uffff\33"+
            "\66\57\uffff\47\66\31\uffff\13\66\25\uffff\u01b8\66\1\uffff"+
            "\145\66\2\uffff\22\66\16\uffff\2\66\137\uffff\4\66\1\uffff\4"+
            "\66\2\uffff\34\66\1\uffff\43\66\1\uffff\1\66\1\uffff\4\66\3"+
            "\uffff\1\66\1\uffff\7\66\2\uffff\7\66\16\uffff\37\66\3\uffff"+
            "\30\66\1\uffff\16\66\21\uffff\26\66\12\uffff\u0193\66\26\uffff"+
            "\77\66\4\uffff\40\66\2\uffff\u0110\66\u0372\uffff\32\66\1\uffff"+
            "\131\66\14\uffff\u00d6\66\32\uffff\14\66\5\uffff\7\66\12\uffff"+
            "\2\66\10\uffff\1\66\3\uffff\40\66\1\uffff\126\66\2\uffff\2\66"+
            "\2\uffff\143\66\5\uffff\50\66\4\uffff\136\66\1\uffff\50\66\70"+
            "\uffff\57\66\1\uffff\44\66\14\uffff\56\66\1\uffff\u0080\66\1"+
            "\uffff\u1ab6\66\12\uffff\u51e6\66\132\uffff\u048d\66\3\uffff"+
            "\67\66\u0739\uffff\u2ba4\66\u215c\uffff\u012e\66\2\uffff\73"+
            "\66\u0095\uffff\7\66\14\uffff\5\66\5\uffff\32\66\1\uffff\5\66"+
            "\1\uffff\1\66\1\uffff\2\66\1\uffff\2\66\1\uffff\154\66\41\uffff"+
            "\u016b\66\22\uffff\100\66\2\uffff\66\66\50\uffff\16\66\2\uffff"+
            "\20\66\20\uffff\4\66\14\uffff\5\66\20\uffff\2\66\2\uffff\12"+
            "\66\1\uffff\5\66\6\uffff\10\66\1\uffff\4\66\4\uffff\5\66\1\uffff"+
            "\u0087\66\4\uffff\7\66\2\uffff\61\66\1\uffff\1\66\2\uffff\1"+
            "\66\1\uffff\32\66\1\uffff\1\66\1\uffff\1\66\2\uffff\1\66\2\uffff"+
            "\133\66\3\uffff\6\66\2\uffff\6\66\2\uffff\6\66\2\uffff\3\66"+
            "\3\uffff\3\66\1\uffff\3\66\1\uffff\7\66",
            "\7\66\2\uffff\1\67\1\66\1\uffff\1\66\1\uffff\1\66\12\134\1"+
            "\uffff\1\66\1\uffff\1\66\1\uffff\1\70\33\66\1\uffff\1\110\2"+
            "\uffff\1\66\1\uffff\32\66\1\uffff\1\66\44\uffff\7\66\1\uffff"+
            "\2\66\1\uffff\1\66\1\uffff\1\66\1\uffff\4\66\1\uffff\3\66\1"+
            "\uffff\2\66\1\uffff\u017b\66\31\uffff\162\66\4\uffff\14\66\16"+
            "\uffff\5\66\11\uffff\1\66\21\uffff\130\66\5\uffff\23\66\12\uffff"+
            "\1\66\3\uffff\1\66\7\uffff\5\66\1\uffff\1\66\1\uffff\24\66\1"+
            "\uffff\54\66\1\uffff\54\66\4\uffff\u0087\66\1\uffff\107\66\1"+
            "\uffff\46\66\2\uffff\2\66\6\uffff\20\66\41\uffff\46\66\2\uffff"+
            "\7\66\1\uffff\47\66\1\uffff\2\66\6\uffff\21\66\1\uffff\27\66"+
            "\1\uffff\12\66\13\uffff\33\66\5\uffff\5\66\27\uffff\12\66\5"+
            "\uffff\1\66\3\uffff\1\66\1\uffff\32\66\5\uffff\31\66\7\uffff"+
            "\175\66\1\uffff\60\66\2\uffff\73\66\2\uffff\3\66\60\uffff\62"+
            "\66\u014f\uffff\71\66\2\uffff\22\66\2\uffff\5\66\3\uffff\31"+
            "\66\20\uffff\3\66\1\uffff\10\66\2\uffff\2\66\2\uffff\26\66\1"+
            "\uffff\7\66\1\uffff\1\66\3\uffff\4\66\2\uffff\11\66\2\uffff"+
            "\2\66\2\uffff\3\66\11\uffff\1\66\4\uffff\2\66\1\uffff\5\66\2"+
            "\uffff\25\66\6\uffff\3\66\1\uffff\6\66\4\uffff\2\66\2\uffff"+
            "\26\66\1\uffff\7\66\1\uffff\2\66\1\uffff\2\66\1\uffff\2\66\2"+
            "\uffff\1\66\1\uffff\5\66\4\uffff\2\66\2\uffff\3\66\13\uffff"+
            "\4\66\1\uffff\1\66\7\uffff\17\66\14\uffff\3\66\1\uffff\11\66"+
            "\1\uffff\3\66\1\uffff\26\66\1\uffff\7\66\1\uffff\2\66\1\uffff"+
            "\5\66\2\uffff\12\66\1\uffff\3\66\1\uffff\3\66\2\uffff\1\66\17"+
            "\uffff\4\66\2\uffff\12\66\1\uffff\1\66\17\uffff\3\66\1\uffff"+
            "\10\66\2\uffff\2\66\2\uffff\26\66\1\uffff\7\66\1\uffff\2\66"+
            "\1\uffff\5\66\2\uffff\10\66\3\uffff\2\66\2\uffff\3\66\10\uffff"+
            "\2\66\4\uffff\2\66\1\uffff\3\66\4\uffff\14\66\20\uffff\2\66"+
            "\1\uffff\6\66\3\uffff\3\66\1\uffff\4\66\3\uffff\2\66\1\uffff"+
            "\1\66\1\uffff\2\66\3\uffff\2\66\3\uffff\3\66\3\uffff\10\66\1"+
            "\uffff\3\66\4\uffff\5\66\3\uffff\3\66\1\uffff\4\66\11\uffff"+
            "\1\66\17\uffff\24\66\6\uffff\3\66\1\uffff\10\66\1\uffff\3\66"+
            "\1\uffff\27\66\1\uffff\12\66\1\uffff\5\66\4\uffff\7\66\1\uffff"+
            "\3\66\1\uffff\4\66\7\uffff\2\66\11\uffff\2\66\4\uffff\12\66"+
            "\22\uffff\2\66\1\uffff\10\66\1\uffff\3\66\1\uffff\27\66\1\uffff"+
            "\12\66\1\uffff\5\66\2\uffff\11\66\1\uffff\3\66\1\uffff\4\66"+
            "\7\uffff\2\66\7\uffff\1\66\1\uffff\2\66\4\uffff\12\66\22\uffff"+
            "\2\66\1\uffff\10\66\1\uffff\3\66\1\uffff\27\66\1\uffff\20\66"+
            "\4\uffff\6\66\2\uffff\3\66\1\uffff\4\66\11\uffff\1\66\10\uffff"+
            "\2\66\4\uffff\12\66\22\uffff\2\66\1\uffff\22\66\3\uffff\30\66"+
            "\1\uffff\11\66\1\uffff\1\66\2\uffff\7\66\3\uffff\1\66\4\uffff"+
            "\6\66\1\uffff\1\66\1\uffff\10\66\22\uffff\3\66\14\uffff\72\66"+
            "\4\uffff\35\66\45\uffff\2\66\1\uffff\1\66\2\uffff\2\66\1\uffff"+
            "\1\66\2\uffff\1\66\6\uffff\4\66\1\uffff\7\66\1\uffff\3\66\1"+
            "\uffff\1\66\1\uffff\1\66\2\uffff\2\66\1\uffff\15\66\1\uffff"+
            "\3\66\2\uffff\5\66\1\uffff\1\66\1\uffff\6\66\2\uffff\12\66\2"+
            "\uffff\2\66\42\uffff\72\66\4\uffff\12\66\1\uffff\42\66\6\uffff"+
            "\33\66\4\uffff\10\66\1\uffff\44\66\1\uffff\17\66\2\uffff\1\66"+
            "\60\uffff\42\66\1\uffff\5\66\1\uffff\2\66\1\uffff\7\66\3\uffff"+
            "\4\66\6\uffff\32\66\106\uffff\46\66\12\uffff\51\66\2\uffff\1"+
            "\66\4\uffff\132\66\5\uffff\104\66\5\uffff\122\66\6\uffff\7\66"+
            "\1\uffff\77\66\1\uffff\1\66\1\uffff\4\66\2\uffff\7\66\1\uffff"+
            "\1\66\1\uffff\4\66\2\uffff\47\66\1\uffff\1\66\1\uffff\4\66\2"+
            "\uffff\37\66\1\uffff\1\66\1\uffff\4\66\2\uffff\7\66\1\uffff"+
            "\1\66\1\uffff\4\66\2\uffff\7\66\1\uffff\7\66\1\uffff\27\66\1"+
            "\uffff\37\66\1\uffff\1\66\1\uffff\4\66\2\uffff\7\66\1\uffff"+
            "\47\66\1\uffff\23\66\6\uffff\34\66\43\uffff\125\66\14\uffff"+
            "\u0276\66\12\uffff\32\66\5\uffff\121\66\17\uffff\15\66\1\uffff"+
            "\7\66\13\uffff\27\66\11\uffff\24\66\14\uffff\15\66\1\uffff\3"+
            "\66\1\uffff\2\66\14\uffff\64\66\2\uffff\50\66\2\uffff\12\66"+
            "\6\uffff\12\66\6\uffff\16\66\2\uffff\12\66\6\uffff\130\66\10"+
            "\uffff\52\66\126\uffff\35\66\3\uffff\14\66\4\uffff\14\66\4\uffff"+
            "\1\66\3\uffff\52\66\2\uffff\5\66\153\uffff\40\66\u0300\uffff"+
            "\154\66\u0094\uffff\u009c\66\4\uffff\132\66\6\uffff\26\66\2"+
            "\uffff\6\66\2\uffff\46\66\2\uffff\6\66\2\uffff\10\66\1\uffff"+
            "\1\66\1\uffff\1\66\1\uffff\1\66\1\uffff\37\66\2\uffff\65\66"+
            "\1\uffff\7\66\1\uffff\1\66\3\uffff\3\66\1\uffff\7\66\3\uffff"+
            "\4\66\2\uffff\6\66\4\uffff\15\66\5\uffff\3\66\1\uffff\7\66\23"+
            "\uffff\10\66\10\uffff\10\66\10\uffff\11\66\2\uffff\12\66\2\uffff"+
            "\16\66\2\uffff\1\66\30\uffff\2\66\2\uffff\11\66\2\uffff\16\66"+
            "\23\uffff\22\66\36\uffff\33\66\25\uffff\74\66\1\uffff\17\66"+
            "\7\uffff\61\66\14\uffff\u0199\66\2\uffff\u0089\66\2\uffff\33"+
            "\66\57\uffff\47\66\31\uffff\13\66\25\uffff\u01b8\66\1\uffff"+
            "\145\66\2\uffff\22\66\16\uffff\2\66\137\uffff\4\66\1\uffff\4"+
            "\66\2\uffff\34\66\1\uffff\43\66\1\uffff\1\66\1\uffff\4\66\3"+
            "\uffff\1\66\1\uffff\7\66\2\uffff\7\66\16\uffff\37\66\3\uffff"+
            "\30\66\1\uffff\16\66\21\uffff\26\66\12\uffff\u0193\66\26\uffff"+
            "\77\66\4\uffff\40\66\2\uffff\u0110\66\u0372\uffff\32\66\1\uffff"+
            "\131\66\14\uffff\u00d6\66\32\uffff\14\66\5\uffff\7\66\12\uffff"+
            "\2\66\10\uffff\1\66\3\uffff\40\66\1\uffff\126\66\2\uffff\2\66"+
            "\2\uffff\143\66\5\uffff\50\66\4\uffff\136\66\1\uffff\50\66\70"+
            "\uffff\57\66\1\uffff\44\66\14\uffff\56\66\1\uffff\u0080\66\1"+
            "\uffff\u1ab6\66\12\uffff\u51e6\66\132\uffff\u048d\66\3\uffff"+
            "\67\66\u0739\uffff\u2ba4\66\u215c\uffff\u012e\66\2\uffff\73"+
            "\66\u0095\uffff\7\66\14\uffff\5\66\5\uffff\32\66\1\uffff\5\66"+
            "\1\uffff\1\66\1\uffff\2\66\1\uffff\2\66\1\uffff\154\66\41\uffff"+
            "\u016b\66\22\uffff\100\66\2\uffff\66\66\50\uffff\16\66\2\uffff"+
            "\20\66\20\uffff\4\66\14\uffff\5\66\20\uffff\2\66\2\uffff\12"+
            "\66\1\uffff\5\66\6\uffff\10\66\1\uffff\4\66\4\uffff\5\66\1\uffff"+
            "\u0087\66\4\uffff\7\66\2\uffff\61\66\1\uffff\1\66\2\uffff\1"+
            "\66\1\uffff\32\66\1\uffff\1\66\1\uffff\1\66\2\uffff\1\66\2\uffff"+
            "\133\66\3\uffff\6\66\2\uffff\6\66\2\uffff\6\66\2\uffff\3\66"+
            "\3\uffff\3\66\1\uffff\3\66\1\uffff\7\66",
            "\7\66\2\uffff\1\67\1\66\1\uffff\1\66\1\uffff\1\66\12\144\1"+
            "\uffff\1\66\1\uffff\1\66\1\uffff\1\70\1\66\6\144\24\66\1\uffff"+
            "\1\110\2\uffff\1\66\1\uffff\6\144\24\66\1\uffff\1\66\44\uffff"+
            "\7\66\1\uffff\2\66\1\uffff\1\66\1\uffff\1\66\1\uffff\4\66\1"+
            "\uffff\3\66\1\uffff\2\66\1\uffff\u017b\66\31\uffff\162\66\4"+
            "\uffff\14\66\16\uffff\5\66\11\uffff\1\66\21\uffff\130\66\5\uffff"+
            "\23\66\12\uffff\1\66\3\uffff\1\66\7\uffff\5\66\1\uffff\1\66"+
            "\1\uffff\24\66\1\uffff\54\66\1\uffff\54\66\4\uffff\u0087\66"+
            "\1\uffff\107\66\1\uffff\46\66\2\uffff\2\66\6\uffff\20\66\41"+
            "\uffff\46\66\2\uffff\7\66\1\uffff\47\66\1\uffff\2\66\6\uffff"+
            "\21\66\1\uffff\27\66\1\uffff\12\66\13\uffff\33\66\5\uffff\5"+
            "\66\27\uffff\12\66\5\uffff\1\66\3\uffff\1\66\1\uffff\32\66\5"+
            "\uffff\31\66\7\uffff\175\66\1\uffff\60\66\2\uffff\73\66\2\uffff"+
            "\3\66\60\uffff\62\66\u014f\uffff\71\66\2\uffff\22\66\2\uffff"+
            "\5\66\3\uffff\31\66\20\uffff\3\66\1\uffff\10\66\2\uffff\2\66"+
            "\2\uffff\26\66\1\uffff\7\66\1\uffff\1\66\3\uffff\4\66\2\uffff"+
            "\11\66\2\uffff\2\66\2\uffff\3\66\11\uffff\1\66\4\uffff\2\66"+
            "\1\uffff\5\66\2\uffff\25\66\6\uffff\3\66\1\uffff\6\66\4\uffff"+
            "\2\66\2\uffff\26\66\1\uffff\7\66\1\uffff\2\66\1\uffff\2\66\1"+
            "\uffff\2\66\2\uffff\1\66\1\uffff\5\66\4\uffff\2\66\2\uffff\3"+
            "\66\13\uffff\4\66\1\uffff\1\66\7\uffff\17\66\14\uffff\3\66\1"+
            "\uffff\11\66\1\uffff\3\66\1\uffff\26\66\1\uffff\7\66\1\uffff"+
            "\2\66\1\uffff\5\66\2\uffff\12\66\1\uffff\3\66\1\uffff\3\66\2"+
            "\uffff\1\66\17\uffff\4\66\2\uffff\12\66\1\uffff\1\66\17\uffff"+
            "\3\66\1\uffff\10\66\2\uffff\2\66\2\uffff\26\66\1\uffff\7\66"+
            "\1\uffff\2\66\1\uffff\5\66\2\uffff\10\66\3\uffff\2\66\2\uffff"+
            "\3\66\10\uffff\2\66\4\uffff\2\66\1\uffff\3\66\4\uffff\14\66"+
            "\20\uffff\2\66\1\uffff\6\66\3\uffff\3\66\1\uffff\4\66\3\uffff"+
            "\2\66\1\uffff\1\66\1\uffff\2\66\3\uffff\2\66\3\uffff\3\66\3"+
            "\uffff\10\66\1\uffff\3\66\4\uffff\5\66\3\uffff\3\66\1\uffff"+
            "\4\66\11\uffff\1\66\17\uffff\24\66\6\uffff\3\66\1\uffff\10\66"+
            "\1\uffff\3\66\1\uffff\27\66\1\uffff\12\66\1\uffff\5\66\4\uffff"+
            "\7\66\1\uffff\3\66\1\uffff\4\66\7\uffff\2\66\11\uffff\2\66\4"+
            "\uffff\12\66\22\uffff\2\66\1\uffff\10\66\1\uffff\3\66\1\uffff"+
            "\27\66\1\uffff\12\66\1\uffff\5\66\2\uffff\11\66\1\uffff\3\66"+
            "\1\uffff\4\66\7\uffff\2\66\7\uffff\1\66\1\uffff\2\66\4\uffff"+
            "\12\66\22\uffff\2\66\1\uffff\10\66\1\uffff\3\66\1\uffff\27\66"+
            "\1\uffff\20\66\4\uffff\6\66\2\uffff\3\66\1\uffff\4\66\11\uffff"+
            "\1\66\10\uffff\2\66\4\uffff\12\66\22\uffff\2\66\1\uffff\22\66"+
            "\3\uffff\30\66\1\uffff\11\66\1\uffff\1\66\2\uffff\7\66\3\uffff"+
            "\1\66\4\uffff\6\66\1\uffff\1\66\1\uffff\10\66\22\uffff\3\66"+
            "\14\uffff\72\66\4\uffff\35\66\45\uffff\2\66\1\uffff\1\66\2\uffff"+
            "\2\66\1\uffff\1\66\2\uffff\1\66\6\uffff\4\66\1\uffff\7\66\1"+
            "\uffff\3\66\1\uffff\1\66\1\uffff\1\66\2\uffff\2\66\1\uffff\15"+
            "\66\1\uffff\3\66\2\uffff\5\66\1\uffff\1\66\1\uffff\6\66\2\uffff"+
            "\12\66\2\uffff\2\66\42\uffff\72\66\4\uffff\12\66\1\uffff\42"+
            "\66\6\uffff\33\66\4\uffff\10\66\1\uffff\44\66\1\uffff\17\66"+
            "\2\uffff\1\66\60\uffff\42\66\1\uffff\5\66\1\uffff\2\66\1\uffff"+
            "\7\66\3\uffff\4\66\6\uffff\32\66\106\uffff\46\66\12\uffff\51"+
            "\66\2\uffff\1\66\4\uffff\132\66\5\uffff\104\66\5\uffff\122\66"+
            "\6\uffff\7\66\1\uffff\77\66\1\uffff\1\66\1\uffff\4\66\2\uffff"+
            "\7\66\1\uffff\1\66\1\uffff\4\66\2\uffff\47\66\1\uffff\1\66\1"+
            "\uffff\4\66\2\uffff\37\66\1\uffff\1\66\1\uffff\4\66\2\uffff"+
            "\7\66\1\uffff\1\66\1\uffff\4\66\2\uffff\7\66\1\uffff\7\66\1"+
            "\uffff\27\66\1\uffff\37\66\1\uffff\1\66\1\uffff\4\66\2\uffff"+
            "\7\66\1\uffff\47\66\1\uffff\23\66\6\uffff\34\66\43\uffff\125"+
            "\66\14\uffff\u0276\66\12\uffff\32\66\5\uffff\121\66\17\uffff"+
            "\15\66\1\uffff\7\66\13\uffff\27\66\11\uffff\24\66\14\uffff\15"+
            "\66\1\uffff\3\66\1\uffff\2\66\14\uffff\64\66\2\uffff\50\66\2"+
            "\uffff\12\66\6\uffff\12\66\6\uffff\16\66\2\uffff\12\66\6\uffff"+
            "\130\66\10\uffff\52\66\126\uffff\35\66\3\uffff\14\66\4\uffff"+
            "\14\66\4\uffff\1\66\3\uffff\52\66\2\uffff\5\66\153\uffff\40"+
            "\66\u0300\uffff\154\66\u0094\uffff\u009c\66\4\uffff\132\66\6"+
            "\uffff\26\66\2\uffff\6\66\2\uffff\46\66\2\uffff\6\66\2\uffff"+
            "\10\66\1\uffff\1\66\1\uffff\1\66\1\uffff\1\66\1\uffff\37\66"+
            "\2\uffff\65\66\1\uffff\7\66\1\uffff\1\66\3\uffff\3\66\1\uffff"+
            "\7\66\3\uffff\4\66\2\uffff\6\66\4\uffff\15\66\5\uffff\3\66\1"+
            "\uffff\7\66\23\uffff\10\66\10\uffff\10\66\10\uffff\11\66\2\uffff"+
            "\12\66\2\uffff\16\66\2\uffff\1\66\30\uffff\2\66\2\uffff\11\66"+
            "\2\uffff\16\66\23\uffff\22\66\36\uffff\33\66\25\uffff\74\66"+
            "\1\uffff\17\66\7\uffff\61\66\14\uffff\u0199\66\2\uffff\u0089"+
            "\66\2\uffff\33\66\57\uffff\47\66\31\uffff\13\66\25\uffff\u01b8"+
            "\66\1\uffff\145\66\2\uffff\22\66\16\uffff\2\66\137\uffff\4\66"+
            "\1\uffff\4\66\2\uffff\34\66\1\uffff\43\66\1\uffff\1\66\1\uffff"+
            "\4\66\3\uffff\1\66\1\uffff\7\66\2\uffff\7\66\16\uffff\37\66"+
            "\3\uffff\30\66\1\uffff\16\66\21\uffff\26\66\12\uffff\u0193\66"+
            "\26\uffff\77\66\4\uffff\40\66\2\uffff\u0110\66\u0372\uffff\32"+
            "\66\1\uffff\131\66\14\uffff\u00d6\66\32\uffff\14\66\5\uffff"+
            "\7\66\12\uffff\2\66\10\uffff\1\66\3\uffff\40\66\1\uffff\126"+
            "\66\2\uffff\2\66\2\uffff\143\66\5\uffff\50\66\4\uffff\136\66"+
            "\1\uffff\50\66\70\uffff\57\66\1\uffff\44\66\14\uffff\56\66\1"+
            "\uffff\u0080\66\1\uffff\u1ab6\66\12\uffff\u51e6\66\132\uffff"+
            "\u048d\66\3\uffff\67\66\u0739\uffff\u2ba4\66\u215c\uffff\u012e"+
            "\66\2\uffff\73\66\u0095\uffff\7\66\14\uffff\5\66\5\uffff\32"+
            "\66\1\uffff\5\66\1\uffff\1\66\1\uffff\2\66\1\uffff\2\66\1\uffff"+
            "\154\66\41\uffff\u016b\66\22\uffff\100\66\2\uffff\66\66\50\uffff"+
            "\16\66\2\uffff\20\66\20\uffff\4\66\14\uffff\5\66\20\uffff\2"+
            "\66\2\uffff\12\66\1\uffff\5\66\6\uffff\10\66\1\uffff\4\66\4"+
            "\uffff\5\66\1\uffff\u0087\66\4\uffff\7\66\2\uffff\61\66\1\uffff"+
            "\1\66\2\uffff\1\66\1\uffff\32\66\1\uffff\1\66\1\uffff\1\66\2"+
            "\uffff\1\66\2\uffff\133\66\3\uffff\6\66\2\uffff\6\66\2\uffff"+
            "\6\66\2\uffff\3\66\3\uffff\3\66\1\uffff\3\66\1\uffff\7\66",
            "\2\66\1\63\1\62\3\66\2\uffff\1\67\1\66\1\uffff\1\66\1\uffff"+
            "\1\66\12\147\1\uffff\1\66\1\uffff\1\66\1\uffff\1\70\1\66\6\146"+
            "\24\57\1\uffff\1\64\2\uffff\1\61\1\uffff\6\145\24\56\1\uffff"+
            "\1\66\44\uffff\7\66\1\uffff\2\66\1\uffff\1\66\1\uffff\1\66\1"+
            "\uffff\4\66\1\uffff\3\66\1\uffff\2\66\1\uffff\u017b\66\31\uffff"+
            "\162\66\4\uffff\14\66\16\uffff\5\66\11\uffff\1\66\21\uffff\130"+
            "\66\5\uffff\23\66\12\uffff\1\66\3\uffff\1\66\7\uffff\5\66\1"+
            "\uffff\1\66\1\uffff\24\66\1\uffff\54\66\1\uffff\54\66\4\uffff"+
            "\u0087\66\1\uffff\107\66\1\uffff\46\66\2\uffff\2\66\6\uffff"+
            "\20\66\41\uffff\46\66\2\uffff\7\66\1\uffff\47\66\1\uffff\2\66"+
            "\6\uffff\21\66\1\uffff\27\66\1\uffff\12\66\13\uffff\33\66\5"+
            "\uffff\5\66\27\uffff\12\66\5\uffff\1\66\3\uffff\1\66\1\uffff"+
            "\32\66\5\uffff\31\66\7\uffff\175\66\1\uffff\60\66\2\uffff\73"+
            "\66\2\uffff\3\66\60\uffff\62\66\u014f\uffff\71\66\2\uffff\22"+
            "\66\2\uffff\5\66\3\uffff\31\66\20\uffff\3\66\1\uffff\10\66\2"+
            "\uffff\2\66\2\uffff\26\66\1\uffff\7\66\1\uffff\1\66\3\uffff"+
            "\4\66\2\uffff\11\66\2\uffff\2\66\2\uffff\3\66\11\uffff\1\66"+
            "\4\uffff\2\66\1\uffff\5\66\2\uffff\25\66\6\uffff\3\66\1\uffff"+
            "\6\66\4\uffff\2\66\2\uffff\26\66\1\uffff\7\66\1\uffff\2\66\1"+
            "\uffff\2\66\1\uffff\2\66\2\uffff\1\66\1\uffff\5\66\4\uffff\2"+
            "\66\2\uffff\3\66\13\uffff\4\66\1\uffff\1\66\7\uffff\17\66\14"+
            "\uffff\3\66\1\uffff\11\66\1\uffff\3\66\1\uffff\26\66\1\uffff"+
            "\7\66\1\uffff\2\66\1\uffff\5\66\2\uffff\12\66\1\uffff\3\66\1"+
            "\uffff\3\66\2\uffff\1\66\17\uffff\4\66\2\uffff\12\66\1\uffff"+
            "\1\66\17\uffff\3\66\1\uffff\10\66\2\uffff\2\66\2\uffff\26\66"+
            "\1\uffff\7\66\1\uffff\2\66\1\uffff\5\66\2\uffff\10\66\3\uffff"+
            "\2\66\2\uffff\3\66\10\uffff\2\66\4\uffff\2\66\1\uffff\3\66\4"+
            "\uffff\14\66\20\uffff\2\66\1\uffff\6\66\3\uffff\3\66\1\uffff"+
            "\4\66\3\uffff\2\66\1\uffff\1\66\1\uffff\2\66\3\uffff\2\66\3"+
            "\uffff\3\66\3\uffff\10\66\1\uffff\3\66\4\uffff\5\66\3\uffff"+
            "\3\66\1\uffff\4\66\11\uffff\1\66\17\uffff\24\66\6\uffff\3\66"+
            "\1\uffff\10\66\1\uffff\3\66\1\uffff\27\66\1\uffff\12\66\1\uffff"+
            "\5\66\4\uffff\7\66\1\uffff\3\66\1\uffff\4\66\7\uffff\2\66\11"+
            "\uffff\2\66\4\uffff\12\66\22\uffff\2\66\1\uffff\10\66\1\uffff"+
            "\3\66\1\uffff\27\66\1\uffff\12\66\1\uffff\5\66\2\uffff\11\66"+
            "\1\uffff\3\66\1\uffff\4\66\7\uffff\2\66\7\uffff\1\66\1\uffff"+
            "\2\66\4\uffff\12\66\22\uffff\2\66\1\uffff\10\66\1\uffff\3\66"+
            "\1\uffff\27\66\1\uffff\20\66\4\uffff\6\66\2\uffff\3\66\1\uffff"+
            "\4\66\11\uffff\1\66\10\uffff\2\66\4\uffff\12\66\22\uffff\2\66"+
            "\1\uffff\22\66\3\uffff\30\66\1\uffff\11\66\1\uffff\1\66\2\uffff"+
            "\7\66\3\uffff\1\66\4\uffff\6\66\1\uffff\1\66\1\uffff\10\66\22"+
            "\uffff\3\66\14\uffff\72\66\4\uffff\35\66\45\uffff\2\66\1\uffff"+
            "\1\66\2\uffff\2\66\1\uffff\1\66\2\uffff\1\66\6\uffff\4\66\1"+
            "\uffff\7\66\1\uffff\3\66\1\uffff\1\66\1\uffff\1\66\2\uffff\2"+
            "\66\1\uffff\15\66\1\uffff\3\66\2\uffff\5\66\1\uffff\1\66\1\uffff"+
            "\6\66\2\uffff\12\66\2\uffff\2\66\42\uffff\72\66\4\uffff\12\66"+
            "\1\uffff\42\66\6\uffff\33\66\4\uffff\10\66\1\uffff\44\66\1\uffff"+
            "\17\66\2\uffff\1\66\60\uffff\42\66\1\uffff\5\66\1\uffff\2\66"+
            "\1\uffff\7\66\3\uffff\4\66\6\uffff\32\66\106\uffff\46\66\12"+
            "\uffff\51\66\2\uffff\1\66\4\uffff\132\66\5\uffff\104\66\5\uffff"+
            "\122\66\6\uffff\7\66\1\uffff\77\66\1\uffff\1\66\1\uffff\4\66"+
            "\2\uffff\7\66\1\uffff\1\66\1\uffff\4\66\2\uffff\47\66\1\uffff"+
            "\1\66\1\uffff\4\66\2\uffff\37\66\1\uffff\1\66\1\uffff\4\66\2"+
            "\uffff\7\66\1\uffff\1\66\1\uffff\4\66\2\uffff\7\66\1\uffff\7"+
            "\66\1\uffff\27\66\1\uffff\37\66\1\uffff\1\66\1\uffff\4\66\2"+
            "\uffff\7\66\1\uffff\47\66\1\uffff\23\66\6\uffff\34\66\43\uffff"+
            "\125\66\14\uffff\u0276\66\12\uffff\32\66\5\uffff\121\66\17\uffff"+
            "\15\66\1\uffff\7\66\13\uffff\27\66\11\uffff\24\66\14\uffff\15"+
            "\66\1\uffff\3\66\1\uffff\2\66\14\uffff\64\66\2\uffff\50\66\2"+
            "\uffff\12\66\6\uffff\12\66\6\uffff\16\66\2\uffff\12\66\6\uffff"+
            "\130\66\10\uffff\52\66\126\uffff\35\66\3\uffff\14\66\4\uffff"+
            "\14\66\4\uffff\1\66\3\uffff\52\66\2\uffff\5\66\153\uffff\40"+
            "\66\u0300\uffff\154\66\u0094\uffff\u009c\66\4\uffff\132\66\6"+
            "\uffff\26\66\2\uffff\6\66\2\uffff\46\66\2\uffff\6\66\2\uffff"+
            "\10\66\1\uffff\1\66\1\uffff\1\66\1\uffff\1\66\1\uffff\37\66"+
            "\2\uffff\65\66\1\uffff\7\66\1\uffff\1\66\3\uffff\3\66\1\uffff"+
            "\7\66\3\uffff\4\66\2\uffff\6\66\4\uffff\15\66\5\uffff\3\66\1"+
            "\uffff\7\66\23\uffff\10\66\10\uffff\10\66\10\uffff\11\66\2\uffff"+
            "\12\66\2\uffff\16\66\2\uffff\1\66\30\uffff\2\66\2\uffff\11\66"+
            "\2\uffff\16\66\23\uffff\22\66\36\uffff\33\66\25\uffff\74\66"+
            "\1\uffff\17\66\7\uffff\61\66\14\uffff\u0199\66\2\uffff\u0089"+
            "\66\2\uffff\33\66\57\uffff\47\66\31\uffff\13\66\25\uffff\u01b8"+
            "\66\1\uffff\145\66\2\uffff\22\66\16\uffff\2\66\137\uffff\4\66"+
            "\1\uffff\4\66\2\uffff\34\66\1\uffff\43\66\1\uffff\1\66\1\uffff"+
            "\4\66\3\uffff\1\66\1\uffff\7\66\2\uffff\7\66\16\uffff\37\66"+
            "\3\uffff\30\66\1\uffff\16\66\21\uffff\26\66\12\uffff\u0193\66"+
            "\26\uffff\77\66\4\uffff\40\66\2\uffff\u0110\66\u0372\uffff\32"+
            "\66\1\uffff\131\66\14\uffff\u00d6\66\32\uffff\14\66\5\uffff"+
            "\7\66\12\uffff\2\66\10\uffff\1\66\3\uffff\40\66\1\uffff\126"+
            "\66\2\uffff\2\66\2\uffff\143\66\5\uffff\50\66\4\uffff\136\66"+
            "\1\uffff\50\66\70\uffff\57\66\1\uffff\44\66\14\uffff\56\66\1"+
            "\uffff\u0080\66\1\uffff\u1ab6\66\12\uffff\u51e6\66\132\uffff"+
            "\u048d\66\3\uffff\67\66\u0739\uffff\u2ba4\66\u215c\uffff\u012e"+
            "\66\2\uffff\73\66\u0095\uffff\7\66\14\uffff\5\66\5\uffff\32"+
            "\66\1\uffff\5\66\1\uffff\1\66\1\uffff\2\66\1\uffff\2\66\1\uffff"+
            "\154\66\41\uffff\u016b\66\22\uffff\100\66\2\uffff\66\66\50\uffff"+
            "\16\66\2\uffff\20\66\20\uffff\4\66\14\uffff\5\66\20\uffff\2"+
            "\66\2\uffff\12\66\1\uffff\5\66\6\uffff\10\66\1\uffff\4\66\4"+
            "\uffff\5\66\1\uffff\u0087\66\4\uffff\7\66\2\uffff\61\66\1\uffff"+
            "\1\66\2\uffff\1\66\1\uffff\32\66\1\uffff\1\66\1\uffff\1\66\2"+
            "\uffff\1\66\2\uffff\133\66\3\uffff\6\66\2\uffff\6\66\2\uffff"+
            "\6\66\2\uffff\3\66\3\uffff\3\66\1\uffff\3\66\1\uffff\7\66",
            "\2\66\1\63\1\62\3\66\2\uffff\1\67\1\66\1\uffff\1\66\1\uffff"+
            "\1\66\12\147\1\uffff\1\66\1\uffff\1\66\1\uffff\1\70\1\66\6\146"+
            "\24\57\1\uffff\1\64\2\uffff\1\61\1\uffff\6\145\24\56\1\uffff"+
            "\1\66\44\uffff\7\66\1\uffff\2\66\1\uffff\1\66\1\uffff\1\66\1"+
            "\uffff\4\66\1\uffff\3\66\1\uffff\2\66\1\uffff\u017b\66\31\uffff"+
            "\162\66\4\uffff\14\66\16\uffff\5\66\11\uffff\1\66\21\uffff\130"+
            "\66\5\uffff\23\66\12\uffff\1\66\3\uffff\1\66\7\uffff\5\66\1"+
            "\uffff\1\66\1\uffff\24\66\1\uffff\54\66\1\uffff\54\66\4\uffff"+
            "\u0087\66\1\uffff\107\66\1\uffff\46\66\2\uffff\2\66\6\uffff"+
            "\20\66\41\uffff\46\66\2\uffff\7\66\1\uffff\47\66\1\uffff\2\66"+
            "\6\uffff\21\66\1\uffff\27\66\1\uffff\12\66\13\uffff\33\66\5"+
            "\uffff\5\66\27\uffff\12\66\5\uffff\1\66\3\uffff\1\66\1\uffff"+
            "\32\66\5\uffff\31\66\7\uffff\175\66\1\uffff\60\66\2\uffff\73"+
            "\66\2\uffff\3\66\60\uffff\62\66\u014f\uffff\71\66\2\uffff\22"+
            "\66\2\uffff\5\66\3\uffff\31\66\20\uffff\3\66\1\uffff\10\66\2"+
            "\uffff\2\66\2\uffff\26\66\1\uffff\7\66\1\uffff\1\66\3\uffff"+
            "\4\66\2\uffff\11\66\2\uffff\2\66\2\uffff\3\66\11\uffff\1\66"+
            "\4\uffff\2\66\1\uffff\5\66\2\uffff\25\66\6\uffff\3\66\1\uffff"+
            "\6\66\4\uffff\2\66\2\uffff\26\66\1\uffff\7\66\1\uffff\2\66\1"+
            "\uffff\2\66\1\uffff\2\66\2\uffff\1\66\1\uffff\5\66\4\uffff\2"+
            "\66\2\uffff\3\66\13\uffff\4\66\1\uffff\1\66\7\uffff\17\66\14"+
            "\uffff\3\66\1\uffff\11\66\1\uffff\3\66\1\uffff\26\66\1\uffff"+
            "\7\66\1\uffff\2\66\1\uffff\5\66\2\uffff\12\66\1\uffff\3\66\1"+
            "\uffff\3\66\2\uffff\1\66\17\uffff\4\66\2\uffff\12\66\1\uffff"+
            "\1\66\17\uffff\3\66\1\uffff\10\66\2\uffff\2\66\2\uffff\26\66"+
            "\1\uffff\7\66\1\uffff\2\66\1\uffff\5\66\2\uffff\10\66\3\uffff"+
            "\2\66\2\uffff\3\66\10\uffff\2\66\4\uffff\2\66\1\uffff\3\66\4"+
            "\uffff\14\66\20\uffff\2\66\1\uffff\6\66\3\uffff\3\66\1\uffff"+
            "\4\66\3\uffff\2\66\1\uffff\1\66\1\uffff\2\66\3\uffff\2\66\3"+
            "\uffff\3\66\3\uffff\10\66\1\uffff\3\66\4\uffff\5\66\3\uffff"+
            "\3\66\1\uffff\4\66\11\uffff\1\66\17\uffff\24\66\6\uffff\3\66"+
            "\1\uffff\10\66\1\uffff\3\66\1\uffff\27\66\1\uffff\12\66\1\uffff"+
            "\5\66\4\uffff\7\66\1\uffff\3\66\1\uffff\4\66\7\uffff\2\66\11"+
            "\uffff\2\66\4\uffff\12\66\22\uffff\2\66\1\uffff\10\66\1\uffff"+
            "\3\66\1\uffff\27\66\1\uffff\12\66\1\uffff\5\66\2\uffff\11\66"+
            "\1\uffff\3\66\1\uffff\4\66\7\uffff\2\66\7\uffff\1\66\1\uffff"+
            "\2\66\4\uffff\12\66\22\uffff\2\66\1\uffff\10\66\1\uffff\3\66"+
            "\1\uffff\27\66\1\uffff\20\66\4\uffff\6\66\2\uffff\3\66\1\uffff"+
            "\4\66\11\uffff\1\66\10\uffff\2\66\4\uffff\12\66\22\uffff\2\66"+
            "\1\uffff\22\66\3\uffff\30\66\1\uffff\11\66\1\uffff\1\66\2\uffff"+
            "\7\66\3\uffff\1\66\4\uffff\6\66\1\uffff\1\66\1\uffff\10\66\22"+
            "\uffff\3\66\14\uffff\72\66\4\uffff\35\66\45\uffff\2\66\1\uffff"+
            "\1\66\2\uffff\2\66\1\uffff\1\66\2\uffff\1\66\6\uffff\4\66\1"+
            "\uffff\7\66\1\uffff\3\66\1\uffff\1\66\1\uffff\1\66\2\uffff\2"+
            "\66\1\uffff\15\66\1\uffff\3\66\2\uffff\5\66\1\uffff\1\66\1\uffff"+
            "\6\66\2\uffff\12\66\2\uffff\2\66\42\uffff\72\66\4\uffff\12\66"+
            "\1\uffff\42\66\6\uffff\33\66\4\uffff\10\66\1\uffff\44\66\1\uffff"+
            "\17\66\2\uffff\1\66\60\uffff\42\66\1\uffff\5\66\1\uffff\2\66"+
            "\1\uffff\7\66\3\uffff\4\66\6\uffff\32\66\106\uffff\46\66\12"+
            "\uffff\51\66\2\uffff\1\66\4\uffff\132\66\5\uffff\104\66\5\uffff"+
            "\122\66\6\uffff\7\66\1\uffff\77\66\1\uffff\1\66\1\uffff\4\66"+
            "\2\uffff\7\66\1\uffff\1\66\1\uffff\4\66\2\uffff\47\66\1\uffff"+
            "\1\66\1\uffff\4\66\2\uffff\37\66\1\uffff\1\66\1\uffff\4\66\2"+
            "\uffff\7\66\1\uffff\1\66\1\uffff\4\66\2\uffff\7\66\1\uffff\7"+
            "\66\1\uffff\27\66\1\uffff\37\66\1\uffff\1\66\1\uffff\4\66\2"+
            "\uffff\7\66\1\uffff\47\66\1\uffff\23\66\6\uffff\34\66\43\uffff"+
            "\125\66\14\uffff\u0276\66\12\uffff\32\66\5\uffff\121\66\17\uffff"+
            "\15\66\1\uffff\7\66\13\uffff\27\66\11\uffff\24\66\14\uffff\15"+
            "\66\1\uffff\3\66\1\uffff\2\66\14\uffff\64\66\2\uffff\50\66\2"+
            "\uffff\12\66\6\uffff\12\66\6\uffff\16\66\2\uffff\12\66\6\uffff"+
            "\130\66\10\uffff\52\66\126\uffff\35\66\3\uffff\14\66\4\uffff"+
            "\14\66\4\uffff\1\66\3\uffff\52\66\2\uffff\5\66\153\uffff\40"+
            "\66\u0300\uffff\154\66\u0094\uffff\u009c\66\4\uffff\132\66\6"+
            "\uffff\26\66\2\uffff\6\66\2\uffff\46\66\2\uffff\6\66\2\uffff"+
            "\10\66\1\uffff\1\66\1\uffff\1\66\1\uffff\1\66\1\uffff\37\66"+
            "\2\uffff\65\66\1\uffff\7\66\1\uffff\1\66\3\uffff\3\66\1\uffff"+
            "\7\66\3\uffff\4\66\2\uffff\6\66\4\uffff\15\66\5\uffff\3\66\1"+
            "\uffff\7\66\23\uffff\10\66\10\uffff\10\66\10\uffff\11\66\2\uffff"+
            "\12\66\2\uffff\16\66\2\uffff\1\66\30\uffff\2\66\2\uffff\11\66"+
            "\2\uffff\16\66\23\uffff\22\66\36\uffff\33\66\25\uffff\74\66"+
            "\1\uffff\17\66\7\uffff\61\66\14\uffff\u0199\66\2\uffff\u0089"+
            "\66\2\uffff\33\66\57\uffff\47\66\31\uffff\13\66\25\uffff\u01b8"+
            "\66\1\uffff\145\66\2\uffff\22\66\16\uffff\2\66\137\uffff\4\66"+
            "\1\uffff\4\66\2\uffff\34\66\1\uffff\43\66\1\uffff\1\66\1\uffff"+
            "\4\66\3\uffff\1\66\1\uffff\7\66\2\uffff\7\66\16\uffff\37\66"+
            "\3\uffff\30\66\1\uffff\16\66\21\uffff\26\66\12\uffff\u0193\66"+
            "\26\uffff\77\66\4\uffff\40\66\2\uffff\u0110\66\u0372\uffff\32"+
            "\66\1\uffff\131\66\14\uffff\u00d6\66\32\uffff\14\66\5\uffff"+
            "\7\66\12\uffff\2\66\10\uffff\1\66\3\uffff\40\66\1\uffff\126"+
            "\66\2\uffff\2\66\2\uffff\143\66\5\uffff\50\66\4\uffff\136\66"+
            "\1\uffff\50\66\70\uffff\57\66\1\uffff\44\66\14\uffff\56\66\1"+
            "\uffff\u0080\66\1\uffff\u1ab6\66\12\uffff\u51e6\66\132\uffff"+
            "\u048d\66\3\uffff\67\66\u0739\uffff\u2ba4\66\u215c\uffff\u012e"+
            "\66\2\uffff\73\66\u0095\uffff\7\66\14\uffff\5\66\5\uffff\32"+
            "\66\1\uffff\5\66\1\uffff\1\66\1\uffff\2\66\1\uffff\2\66\1\uffff"+
            "\154\66\41\uffff\u016b\66\22\uffff\100\66\2\uffff\66\66\50\uffff"+
            "\16\66\2\uffff\20\66\20\uffff\4\66\14\uffff\5\66\20\uffff\2"+
            "\66\2\uffff\12\66\1\uffff\5\66\6\uffff\10\66\1\uffff\4\66\4"+
            "\uffff\5\66\1\uffff\u0087\66\4\uffff\7\66\2\uffff\61\66\1\uffff"+
            "\1\66\2\uffff\1\66\1\uffff\32\66\1\uffff\1\66\1\uffff\1\66\2"+
            "\uffff\1\66\2\uffff\133\66\3\uffff\6\66\2\uffff\6\66\2\uffff"+
            "\6\66\2\uffff\3\66\3\uffff\3\66\1\uffff\3\66\1\uffff\7\66",
            "\2\66\1\63\1\62\3\66\2\uffff\1\67\1\66\1\uffff\1\66\1\uffff"+
            "\1\66\12\147\1\uffff\1\66\1\uffff\1\66\1\uffff\1\70\1\66\6\146"+
            "\24\57\1\uffff\1\64\2\uffff\1\61\1\uffff\6\145\24\56\1\uffff"+
            "\1\66\44\uffff\7\66\1\uffff\2\66\1\uffff\1\66\1\uffff\1\66\1"+
            "\uffff\4\66\1\uffff\3\66\1\uffff\2\66\1\uffff\u017b\66\31\uffff"+
            "\162\66\4\uffff\14\66\16\uffff\5\66\11\uffff\1\66\21\uffff\130"+
            "\66\5\uffff\23\66\12\uffff\1\66\3\uffff\1\66\7\uffff\5\66\1"+
            "\uffff\1\66\1\uffff\24\66\1\uffff\54\66\1\uffff\54\66\4\uffff"+
            "\u0087\66\1\uffff\107\66\1\uffff\46\66\2\uffff\2\66\6\uffff"+
            "\20\66\41\uffff\46\66\2\uffff\7\66\1\uffff\47\66\1\uffff\2\66"+
            "\6\uffff\21\66\1\uffff\27\66\1\uffff\12\66\13\uffff\33\66\5"+
            "\uffff\5\66\27\uffff\12\66\5\uffff\1\66\3\uffff\1\66\1\uffff"+
            "\32\66\5\uffff\31\66\7\uffff\175\66\1\uffff\60\66\2\uffff\73"+
            "\66\2\uffff\3\66\60\uffff\62\66\u014f\uffff\71\66\2\uffff\22"+
            "\66\2\uffff\5\66\3\uffff\31\66\20\uffff\3\66\1\uffff\10\66\2"+
            "\uffff\2\66\2\uffff\26\66\1\uffff\7\66\1\uffff\1\66\3\uffff"+
            "\4\66\2\uffff\11\66\2\uffff\2\66\2\uffff\3\66\11\uffff\1\66"+
            "\4\uffff\2\66\1\uffff\5\66\2\uffff\25\66\6\uffff\3\66\1\uffff"+
            "\6\66\4\uffff\2\66\2\uffff\26\66\1\uffff\7\66\1\uffff\2\66\1"+
            "\uffff\2\66\1\uffff\2\66\2\uffff\1\66\1\uffff\5\66\4\uffff\2"+
            "\66\2\uffff\3\66\13\uffff\4\66\1\uffff\1\66\7\uffff\17\66\14"+
            "\uffff\3\66\1\uffff\11\66\1\uffff\3\66\1\uffff\26\66\1\uffff"+
            "\7\66\1\uffff\2\66\1\uffff\5\66\2\uffff\12\66\1\uffff\3\66\1"+
            "\uffff\3\66\2\uffff\1\66\17\uffff\4\66\2\uffff\12\66\1\uffff"+
            "\1\66\17\uffff\3\66\1\uffff\10\66\2\uffff\2\66\2\uffff\26\66"+
            "\1\uffff\7\66\1\uffff\2\66\1\uffff\5\66\2\uffff\10\66\3\uffff"+
            "\2\66\2\uffff\3\66\10\uffff\2\66\4\uffff\2\66\1\uffff\3\66\4"+
            "\uffff\14\66\20\uffff\2\66\1\uffff\6\66\3\uffff\3\66\1\uffff"+
            "\4\66\3\uffff\2\66\1\uffff\1\66\1\uffff\2\66\3\uffff\2\66\3"+
            "\uffff\3\66\3\uffff\10\66\1\uffff\3\66\4\uffff\5\66\3\uffff"+
            "\3\66\1\uffff\4\66\11\uffff\1\66\17\uffff\24\66\6\uffff\3\66"+
            "\1\uffff\10\66\1\uffff\3\66\1\uffff\27\66\1\uffff\12\66\1\uffff"+
            "\5\66\4\uffff\7\66\1\uffff\3\66\1\uffff\4\66\7\uffff\2\66\11"+
            "\uffff\2\66\4\uffff\12\66\22\uffff\2\66\1\uffff\10\66\1\uffff"+
            "\3\66\1\uffff\27\66\1\uffff\12\66\1\uffff\5\66\2\uffff\11\66"+
            "\1\uffff\3\66\1\uffff\4\66\7\uffff\2\66\7\uffff\1\66\1\uffff"+
            "\2\66\4\uffff\12\66\22\uffff\2\66\1\uffff\10\66\1\uffff\3\66"+
            "\1\uffff\27\66\1\uffff\20\66\4\uffff\6\66\2\uffff\3\66\1\uffff"+
            "\4\66\11\uffff\1\66\10\uffff\2\66\4\uffff\12\66\22\uffff\2\66"+
            "\1\uffff\22\66\3\uffff\30\66\1\uffff\11\66\1\uffff\1\66\2\uffff"+
            "\7\66\3\uffff\1\66\4\uffff\6\66\1\uffff\1\66\1\uffff\10\66\22"+
            "\uffff\3\66\14\uffff\72\66\4\uffff\35\66\45\uffff\2\66\1\uffff"+
            "\1\66\2\uffff\2\66\1\uffff\1\66\2\uffff\1\66\6\uffff\4\66\1"+
            "\uffff\7\66\1\uffff\3\66\1\uffff\1\66\1\uffff\1\66\2\uffff\2"+
            "\66\1\uffff\15\66\1\uffff\3\66\2\uffff\5\66\1\uffff\1\66\1\uffff"+
            "\6\66\2\uffff\12\66\2\uffff\2\66\42\uffff\72\66\4\uffff\12\66"+
            "\1\uffff\42\66\6\uffff\33\66\4\uffff\10\66\1\uffff\44\66\1\uffff"+
            "\17\66\2\uffff\1\66\60\uffff\42\66\1\uffff\5\66\1\uffff\2\66"+
            "\1\uffff\7\66\3\uffff\4\66\6\uffff\32\66\106\uffff\46\66\12"+
            "\uffff\51\66\2\uffff\1\66\4\uffff\132\66\5\uffff\104\66\5\uffff"+
            "\122\66\6\uffff\7\66\1\uffff\77\66\1\uffff\1\66\1\uffff\4\66"+
            "\2\uffff\7\66\1\uffff\1\66\1\uffff\4\66\2\uffff\47\66\1\uffff"+
            "\1\66\1\uffff\4\66\2\uffff\37\66\1\uffff\1\66\1\uffff\4\66\2"+
            "\uffff\7\66\1\uffff\1\66\1\uffff\4\66\2\uffff\7\66\1\uffff\7"+
            "\66\1\uffff\27\66\1\uffff\37\66\1\uffff\1\66\1\uffff\4\66\2"+
            "\uffff\7\66\1\uffff\47\66\1\uffff\23\66\6\uffff\34\66\43\uffff"+
            "\125\66\14\uffff\u0276\66\12\uffff\32\66\5\uffff\121\66\17\uffff"+
            "\15\66\1\uffff\7\66\13\uffff\27\66\11\uffff\24\66\14\uffff\15"+
            "\66\1\uffff\3\66\1\uffff\2\66\14\uffff\64\66\2\uffff\50\66\2"+
            "\uffff\12\66\6\uffff\12\66\6\uffff\16\66\2\uffff\12\66\6\uffff"+
            "\130\66\10\uffff\52\66\126\uffff\35\66\3\uffff\14\66\4\uffff"+
            "\14\66\4\uffff\1\66\3\uffff\52\66\2\uffff\5\66\153\uffff\40"+
            "\66\u0300\uffff\154\66\u0094\uffff\u009c\66\4\uffff\132\66\6"+
            "\uffff\26\66\2\uffff\6\66\2\uffff\46\66\2\uffff\6\66\2\uffff"+
            "\10\66\1\uffff\1\66\1\uffff\1\66\1\uffff\1\66\1\uffff\37\66"+
            "\2\uffff\65\66\1\uffff\7\66\1\uffff\1\66\3\uffff\3\66\1\uffff"+
            "\7\66\3\uffff\4\66\2\uffff\6\66\4\uffff\15\66\5\uffff\3\66\1"+
            "\uffff\7\66\23\uffff\10\66\10\uffff\10\66\10\uffff\11\66\2\uffff"+
            "\12\66\2\uffff\16\66\2\uffff\1\66\30\uffff\2\66\2\uffff\11\66"+
            "\2\uffff\16\66\23\uffff\22\66\36\uffff\33\66\25\uffff\74\66"+
            "\1\uffff\17\66\7\uffff\61\66\14\uffff\u0199\66\2\uffff\u0089"+
            "\66\2\uffff\33\66\57\uffff\47\66\31\uffff\13\66\25\uffff\u01b8"+
            "\66\1\uffff\145\66\2\uffff\22\66\16\uffff\2\66\137\uffff\4\66"+
            "\1\uffff\4\66\2\uffff\34\66\1\uffff\43\66\1\uffff\1\66\1\uffff"+
            "\4\66\3\uffff\1\66\1\uffff\7\66\2\uffff\7\66\16\uffff\37\66"+
            "\3\uffff\30\66\1\uffff\16\66\21\uffff\26\66\12\uffff\u0193\66"+
            "\26\uffff\77\66\4\uffff\40\66\2\uffff\u0110\66\u0372\uffff\32"+
            "\66\1\uffff\131\66\14\uffff\u00d6\66\32\uffff\14\66\5\uffff"+
            "\7\66\12\uffff\2\66\10\uffff\1\66\3\uffff\40\66\1\uffff\126"+
            "\66\2\uffff\2\66\2\uffff\143\66\5\uffff\50\66\4\uffff\136\66"+
            "\1\uffff\50\66\70\uffff\57\66\1\uffff\44\66\14\uffff\56\66\1"+
            "\uffff\u0080\66\1\uffff\u1ab6\66\12\uffff\u51e6\66\132\uffff"+
            "\u048d\66\3\uffff\67\66\u0739\uffff\u2ba4\66\u215c\uffff\u012e"+
            "\66\2\uffff\73\66\u0095\uffff\7\66\14\uffff\5\66\5\uffff\32"+
            "\66\1\uffff\5\66\1\uffff\1\66\1\uffff\2\66\1\uffff\2\66\1\uffff"+
            "\154\66\41\uffff\u016b\66\22\uffff\100\66\2\uffff\66\66\50\uffff"+
            "\16\66\2\uffff\20\66\20\uffff\4\66\14\uffff\5\66\20\uffff\2"+
            "\66\2\uffff\12\66\1\uffff\5\66\6\uffff\10\66\1\uffff\4\66\4"+
            "\uffff\5\66\1\uffff\u0087\66\4\uffff\7\66\2\uffff\61\66\1\uffff"+
            "\1\66\2\uffff\1\66\1\uffff\32\66\1\uffff\1\66\1\uffff\1\66\2"+
            "\uffff\1\66\2\uffff\133\66\3\uffff\6\66\2\uffff\6\66\2\uffff"+
            "\6\66\2\uffff\3\66\3\uffff\3\66\1\uffff\3\66\1\uffff\7\66",
            "",
            "",
            "\7\66\2\uffff\1\67\1\66\1\uffff\1\66\1\uffff\1\66\12\150\1"+
            "\uffff\1\66\1\uffff\1\66\1\uffff\1\70\1\66\6\150\24\66\1\uffff"+
            "\1\110\2\uffff\1\66\1\uffff\6\150\24\66\1\uffff\1\66\44\uffff"+
            "\7\66\1\uffff\2\66\1\uffff\1\66\1\uffff\1\66\1\uffff\4\66\1"+
            "\uffff\3\66\1\uffff\2\66\1\uffff\u017b\66\31\uffff\162\66\4"+
            "\uffff\14\66\16\uffff\5\66\11\uffff\1\66\21\uffff\130\66\5\uffff"+
            "\23\66\12\uffff\1\66\3\uffff\1\66\7\uffff\5\66\1\uffff\1\66"+
            "\1\uffff\24\66\1\uffff\54\66\1\uffff\54\66\4\uffff\u0087\66"+
            "\1\uffff\107\66\1\uffff\46\66\2\uffff\2\66\6\uffff\20\66\41"+
            "\uffff\46\66\2\uffff\7\66\1\uffff\47\66\1\uffff\2\66\6\uffff"+
            "\21\66\1\uffff\27\66\1\uffff\12\66\13\uffff\33\66\5\uffff\5"+
            "\66\27\uffff\12\66\5\uffff\1\66\3\uffff\1\66\1\uffff\32\66\5"+
            "\uffff\31\66\7\uffff\175\66\1\uffff\60\66\2\uffff\73\66\2\uffff"+
            "\3\66\60\uffff\62\66\u014f\uffff\71\66\2\uffff\22\66\2\uffff"+
            "\5\66\3\uffff\31\66\20\uffff\3\66\1\uffff\10\66\2\uffff\2\66"+
            "\2\uffff\26\66\1\uffff\7\66\1\uffff\1\66\3\uffff\4\66\2\uffff"+
            "\11\66\2\uffff\2\66\2\uffff\3\66\11\uffff\1\66\4\uffff\2\66"+
            "\1\uffff\5\66\2\uffff\25\66\6\uffff\3\66\1\uffff\6\66\4\uffff"+
            "\2\66\2\uffff\26\66\1\uffff\7\66\1\uffff\2\66\1\uffff\2\66\1"+
            "\uffff\2\66\2\uffff\1\66\1\uffff\5\66\4\uffff\2\66\2\uffff\3"+
            "\66\13\uffff\4\66\1\uffff\1\66\7\uffff\17\66\14\uffff\3\66\1"+
            "\uffff\11\66\1\uffff\3\66\1\uffff\26\66\1\uffff\7\66\1\uffff"+
            "\2\66\1\uffff\5\66\2\uffff\12\66\1\uffff\3\66\1\uffff\3\66\2"+
            "\uffff\1\66\17\uffff\4\66\2\uffff\12\66\1\uffff\1\66\17\uffff"+
            "\3\66\1\uffff\10\66\2\uffff\2\66\2\uffff\26\66\1\uffff\7\66"+
            "\1\uffff\2\66\1\uffff\5\66\2\uffff\10\66\3\uffff\2\66\2\uffff"+
            "\3\66\10\uffff\2\66\4\uffff\2\66\1\uffff\3\66\4\uffff\14\66"+
            "\20\uffff\2\66\1\uffff\6\66\3\uffff\3\66\1\uffff\4\66\3\uffff"+
            "\2\66\1\uffff\1\66\1\uffff\2\66\3\uffff\2\66\3\uffff\3\66\3"+
            "\uffff\10\66\1\uffff\3\66\4\uffff\5\66\3\uffff\3\66\1\uffff"+
            "\4\66\11\uffff\1\66\17\uffff\24\66\6\uffff\3\66\1\uffff\10\66"+
            "\1\uffff\3\66\1\uffff\27\66\1\uffff\12\66\1\uffff\5\66\4\uffff"+
            "\7\66\1\uffff\3\66\1\uffff\4\66\7\uffff\2\66\11\uffff\2\66\4"+
            "\uffff\12\66\22\uffff\2\66\1\uffff\10\66\1\uffff\3\66\1\uffff"+
            "\27\66\1\uffff\12\66\1\uffff\5\66\2\uffff\11\66\1\uffff\3\66"+
            "\1\uffff\4\66\7\uffff\2\66\7\uffff\1\66\1\uffff\2\66\4\uffff"+
            "\12\66\22\uffff\2\66\1\uffff\10\66\1\uffff\3\66\1\uffff\27\66"+
            "\1\uffff\20\66\4\uffff\6\66\2\uffff\3\66\1\uffff\4\66\11\uffff"+
            "\1\66\10\uffff\2\66\4\uffff\12\66\22\uffff\2\66\1\uffff\22\66"+
            "\3\uffff\30\66\1\uffff\11\66\1\uffff\1\66\2\uffff\7\66\3\uffff"+
            "\1\66\4\uffff\6\66\1\uffff\1\66\1\uffff\10\66\22\uffff\3\66"+
            "\14\uffff\72\66\4\uffff\35\66\45\uffff\2\66\1\uffff\1\66\2\uffff"+
            "\2\66\1\uffff\1\66\2\uffff\1\66\6\uffff\4\66\1\uffff\7\66\1"+
            "\uffff\3\66\1\uffff\1\66\1\uffff\1\66\2\uffff\2\66\1\uffff\15"+
            "\66\1\uffff\3\66\2\uffff\5\66\1\uffff\1\66\1\uffff\6\66\2\uffff"+
            "\12\66\2\uffff\2\66\42\uffff\72\66\4\uffff\12\66\1\uffff\42"+
            "\66\6\uffff\33\66\4\uffff\10\66\1\uffff\44\66\1\uffff\17\66"+
            "\2\uffff\1\66\60\uffff\42\66\1\uffff\5\66\1\uffff\2\66\1\uffff"+
            "\7\66\3\uffff\4\66\6\uffff\32\66\106\uffff\46\66\12\uffff\51"+
            "\66\2\uffff\1\66\4\uffff\132\66\5\uffff\104\66\5\uffff\122\66"+
            "\6\uffff\7\66\1\uffff\77\66\1\uffff\1\66\1\uffff\4\66\2\uffff"+
            "\7\66\1\uffff\1\66\1\uffff\4\66\2\uffff\47\66\1\uffff\1\66\1"+
            "\uffff\4\66\2\uffff\37\66\1\uffff\1\66\1\uffff\4\66\2\uffff"+
            "\7\66\1\uffff\1\66\1\uffff\4\66\2\uffff\7\66\1\uffff\7\66\1"+
            "\uffff\27\66\1\uffff\37\66\1\uffff\1\66\1\uffff\4\66\2\uffff"+
            "\7\66\1\uffff\47\66\1\uffff\23\66\6\uffff\34\66\43\uffff\125"+
            "\66\14\uffff\u0276\66\12\uffff\32\66\5\uffff\121\66\17\uffff"+
            "\15\66\1\uffff\7\66\13\uffff\27\66\11\uffff\24\66\14\uffff\15"+
            "\66\1\uffff\3\66\1\uffff\2\66\14\uffff\64\66\2\uffff\50\66\2"+
            "\uffff\12\66\6\uffff\12\66\6\uffff\16\66\2\uffff\12\66\6\uffff"+
            "\130\66\10\uffff\52\66\126\uffff\35\66\3\uffff\14\66\4\uffff"+
            "\14\66\4\uffff\1\66\3\uffff\52\66\2\uffff\5\66\153\uffff\40"+
            "\66\u0300\uffff\154\66\u0094\uffff\u009c\66\4\uffff\132\66\6"+
            "\uffff\26\66\2\uffff\6\66\2\uffff\46\66\2\uffff\6\66\2\uffff"+
            "\10\66\1\uffff\1\66\1\uffff\1\66\1\uffff\1\66\1\uffff\37\66"+
            "\2\uffff\65\66\1\uffff\7\66\1\uffff\1\66\3\uffff\3\66\1\uffff"+
            "\7\66\3\uffff\4\66\2\uffff\6\66\4\uffff\15\66\5\uffff\3\66\1"+
            "\uffff\7\66\23\uffff\10\66\10\uffff\10\66\10\uffff\11\66\2\uffff"+
            "\12\66\2\uffff\16\66\2\uffff\1\66\30\uffff\2\66\2\uffff\11\66"+
            "\2\uffff\16\66\23\uffff\22\66\36\uffff\33\66\25\uffff\74\66"+
            "\1\uffff\17\66\7\uffff\61\66\14\uffff\u0199\66\2\uffff\u0089"+
            "\66\2\uffff\33\66\57\uffff\47\66\31\uffff\13\66\25\uffff\u01b8"+
            "\66\1\uffff\145\66\2\uffff\22\66\16\uffff\2\66\137\uffff\4\66"+
            "\1\uffff\4\66\2\uffff\34\66\1\uffff\43\66\1\uffff\1\66\1\uffff"+
            "\4\66\3\uffff\1\66\1\uffff\7\66\2\uffff\7\66\16\uffff\37\66"+
            "\3\uffff\30\66\1\uffff\16\66\21\uffff\26\66\12\uffff\u0193\66"+
            "\26\uffff\77\66\4\uffff\40\66\2\uffff\u0110\66\u0372\uffff\32"+
            "\66\1\uffff\131\66\14\uffff\u00d6\66\32\uffff\14\66\5\uffff"+
            "\7\66\12\uffff\2\66\10\uffff\1\66\3\uffff\40\66\1\uffff\126"+
            "\66\2\uffff\2\66\2\uffff\143\66\5\uffff\50\66\4\uffff\136\66"+
            "\1\uffff\50\66\70\uffff\57\66\1\uffff\44\66\14\uffff\56\66\1"+
            "\uffff\u0080\66\1\uffff\u1ab6\66\12\uffff\u51e6\66\132\uffff"+
            "\u048d\66\3\uffff\67\66\u0739\uffff\u2ba4\66\u215c\uffff\u012e"+
            "\66\2\uffff\73\66\u0095\uffff\7\66\14\uffff\5\66\5\uffff\32"+
            "\66\1\uffff\5\66\1\uffff\1\66\1\uffff\2\66\1\uffff\2\66\1\uffff"+
            "\154\66\41\uffff\u016b\66\22\uffff\100\66\2\uffff\66\66\50\uffff"+
            "\16\66\2\uffff\20\66\20\uffff\4\66\14\uffff\5\66\20\uffff\2"+
            "\66\2\uffff\12\66\1\uffff\5\66\6\uffff\10\66\1\uffff\4\66\4"+
            "\uffff\5\66\1\uffff\u0087\66\4\uffff\7\66\2\uffff\61\66\1\uffff"+
            "\1\66\2\uffff\1\66\1\uffff\32\66\1\uffff\1\66\1\uffff\1\66\2"+
            "\uffff\1\66\2\uffff\133\66\3\uffff\6\66\2\uffff\6\66\2\uffff"+
            "\6\66\2\uffff\3\66\3\uffff\3\66\1\uffff\3\66\1\uffff\7\66",
            "\7\66\2\uffff\1\67\1\66\1\uffff\1\66\1\uffff\1\66\12\151\1"+
            "\uffff\1\66\1\uffff\1\66\1\uffff\1\70\1\66\6\151\24\66\1\uffff"+
            "\1\110\2\uffff\1\66\1\uffff\6\151\24\66\1\uffff\1\66\44\uffff"+
            "\7\66\1\uffff\2\66\1\uffff\1\66\1\uffff\1\66\1\uffff\4\66\1"+
            "\uffff\3\66\1\uffff\2\66\1\uffff\u017b\66\31\uffff\162\66\4"+
            "\uffff\14\66\16\uffff\5\66\11\uffff\1\66\21\uffff\130\66\5\uffff"+
            "\23\66\12\uffff\1\66\3\uffff\1\66\7\uffff\5\66\1\uffff\1\66"+
            "\1\uffff\24\66\1\uffff\54\66\1\uffff\54\66\4\uffff\u0087\66"+
            "\1\uffff\107\66\1\uffff\46\66\2\uffff\2\66\6\uffff\20\66\41"+
            "\uffff\46\66\2\uffff\7\66\1\uffff\47\66\1\uffff\2\66\6\uffff"+
            "\21\66\1\uffff\27\66\1\uffff\12\66\13\uffff\33\66\5\uffff\5"+
            "\66\27\uffff\12\66\5\uffff\1\66\3\uffff\1\66\1\uffff\32\66\5"+
            "\uffff\31\66\7\uffff\175\66\1\uffff\60\66\2\uffff\73\66\2\uffff"+
            "\3\66\60\uffff\62\66\u014f\uffff\71\66\2\uffff\22\66\2\uffff"+
            "\5\66\3\uffff\31\66\20\uffff\3\66\1\uffff\10\66\2\uffff\2\66"+
            "\2\uffff\26\66\1\uffff\7\66\1\uffff\1\66\3\uffff\4\66\2\uffff"+
            "\11\66\2\uffff\2\66\2\uffff\3\66\11\uffff\1\66\4\uffff\2\66"+
            "\1\uffff\5\66\2\uffff\25\66\6\uffff\3\66\1\uffff\6\66\4\uffff"+
            "\2\66\2\uffff\26\66\1\uffff\7\66\1\uffff\2\66\1\uffff\2\66\1"+
            "\uffff\2\66\2\uffff\1\66\1\uffff\5\66\4\uffff\2\66\2\uffff\3"+
            "\66\13\uffff\4\66\1\uffff\1\66\7\uffff\17\66\14\uffff\3\66\1"+
            "\uffff\11\66\1\uffff\3\66\1\uffff\26\66\1\uffff\7\66\1\uffff"+
            "\2\66\1\uffff\5\66\2\uffff\12\66\1\uffff\3\66\1\uffff\3\66\2"+
            "\uffff\1\66\17\uffff\4\66\2\uffff\12\66\1\uffff\1\66\17\uffff"+
            "\3\66\1\uffff\10\66\2\uffff\2\66\2\uffff\26\66\1\uffff\7\66"+
            "\1\uffff\2\66\1\uffff\5\66\2\uffff\10\66\3\uffff\2\66\2\uffff"+
            "\3\66\10\uffff\2\66\4\uffff\2\66\1\uffff\3\66\4\uffff\14\66"+
            "\20\uffff\2\66\1\uffff\6\66\3\uffff\3\66\1\uffff\4\66\3\uffff"+
            "\2\66\1\uffff\1\66\1\uffff\2\66\3\uffff\2\66\3\uffff\3\66\3"+
            "\uffff\10\66\1\uffff\3\66\4\uffff\5\66\3\uffff\3\66\1\uffff"+
            "\4\66\11\uffff\1\66\17\uffff\24\66\6\uffff\3\66\1\uffff\10\66"+
            "\1\uffff\3\66\1\uffff\27\66\1\uffff\12\66\1\uffff\5\66\4\uffff"+
            "\7\66\1\uffff\3\66\1\uffff\4\66\7\uffff\2\66\11\uffff\2\66\4"+
            "\uffff\12\66\22\uffff\2\66\1\uffff\10\66\1\uffff\3\66\1\uffff"+
            "\27\66\1\uffff\12\66\1\uffff\5\66\2\uffff\11\66\1\uffff\3\66"+
            "\1\uffff\4\66\7\uffff\2\66\7\uffff\1\66\1\uffff\2\66\4\uffff"+
            "\12\66\22\uffff\2\66\1\uffff\10\66\1\uffff\3\66\1\uffff\27\66"+
            "\1\uffff\20\66\4\uffff\6\66\2\uffff\3\66\1\uffff\4\66\11\uffff"+
            "\1\66\10\uffff\2\66\4\uffff\12\66\22\uffff\2\66\1\uffff\22\66"+
            "\3\uffff\30\66\1\uffff\11\66\1\uffff\1\66\2\uffff\7\66\3\uffff"+
            "\1\66\4\uffff\6\66\1\uffff\1\66\1\uffff\10\66\22\uffff\3\66"+
            "\14\uffff\72\66\4\uffff\35\66\45\uffff\2\66\1\uffff\1\66\2\uffff"+
            "\2\66\1\uffff\1\66\2\uffff\1\66\6\uffff\4\66\1\uffff\7\66\1"+
            "\uffff\3\66\1\uffff\1\66\1\uffff\1\66\2\uffff\2\66\1\uffff\15"+
            "\66\1\uffff\3\66\2\uffff\5\66\1\uffff\1\66\1\uffff\6\66\2\uffff"+
            "\12\66\2\uffff\2\66\42\uffff\72\66\4\uffff\12\66\1\uffff\42"+
            "\66\6\uffff\33\66\4\uffff\10\66\1\uffff\44\66\1\uffff\17\66"+
            "\2\uffff\1\66\60\uffff\42\66\1\uffff\5\66\1\uffff\2\66\1\uffff"+
            "\7\66\3\uffff\4\66\6\uffff\32\66\106\uffff\46\66\12\uffff\51"+
            "\66\2\uffff\1\66\4\uffff\132\66\5\uffff\104\66\5\uffff\122\66"+
            "\6\uffff\7\66\1\uffff\77\66\1\uffff\1\66\1\uffff\4\66\2\uffff"+
            "\7\66\1\uffff\1\66\1\uffff\4\66\2\uffff\47\66\1\uffff\1\66\1"+
            "\uffff\4\66\2\uffff\37\66\1\uffff\1\66\1\uffff\4\66\2\uffff"+
            "\7\66\1\uffff\1\66\1\uffff\4\66\2\uffff\7\66\1\uffff\7\66\1"+
            "\uffff\27\66\1\uffff\37\66\1\uffff\1\66\1\uffff\4\66\2\uffff"+
            "\7\66\1\uffff\47\66\1\uffff\23\66\6\uffff\34\66\43\uffff\125"+
            "\66\14\uffff\u0276\66\12\uffff\32\66\5\uffff\121\66\17\uffff"+
            "\15\66\1\uffff\7\66\13\uffff\27\66\11\uffff\24\66\14\uffff\15"+
            "\66\1\uffff\3\66\1\uffff\2\66\14\uffff\64\66\2\uffff\50\66\2"+
            "\uffff\12\66\6\uffff\12\66\6\uffff\16\66\2\uffff\12\66\6\uffff"+
            "\130\66\10\uffff\52\66\126\uffff\35\66\3\uffff\14\66\4\uffff"+
            "\14\66\4\uffff\1\66\3\uffff\52\66\2\uffff\5\66\153\uffff\40"+
            "\66\u0300\uffff\154\66\u0094\uffff\u009c\66\4\uffff\132\66\6"+
            "\uffff\26\66\2\uffff\6\66\2\uffff\46\66\2\uffff\6\66\2\uffff"+
            "\10\66\1\uffff\1\66\1\uffff\1\66\1\uffff\1\66\1\uffff\37\66"+
            "\2\uffff\65\66\1\uffff\7\66\1\uffff\1\66\3\uffff\3\66\1\uffff"+
            "\7\66\3\uffff\4\66\2\uffff\6\66\4\uffff\15\66\5\uffff\3\66\1"+
            "\uffff\7\66\23\uffff\10\66\10\uffff\10\66\10\uffff\11\66\2\uffff"+
            "\12\66\2\uffff\16\66\2\uffff\1\66\30\uffff\2\66\2\uffff\11\66"+
            "\2\uffff\16\66\23\uffff\22\66\36\uffff\33\66\25\uffff\74\66"+
            "\1\uffff\17\66\7\uffff\61\66\14\uffff\u0199\66\2\uffff\u0089"+
            "\66\2\uffff\33\66\57\uffff\47\66\31\uffff\13\66\25\uffff\u01b8"+
            "\66\1\uffff\145\66\2\uffff\22\66\16\uffff\2\66\137\uffff\4\66"+
            "\1\uffff\4\66\2\uffff\34\66\1\uffff\43\66\1\uffff\1\66\1\uffff"+
            "\4\66\3\uffff\1\66\1\uffff\7\66\2\uffff\7\66\16\uffff\37\66"+
            "\3\uffff\30\66\1\uffff\16\66\21\uffff\26\66\12\uffff\u0193\66"+
            "\26\uffff\77\66\4\uffff\40\66\2\uffff\u0110\66\u0372\uffff\32"+
            "\66\1\uffff\131\66\14\uffff\u00d6\66\32\uffff\14\66\5\uffff"+
            "\7\66\12\uffff\2\66\10\uffff\1\66\3\uffff\40\66\1\uffff\126"+
            "\66\2\uffff\2\66\2\uffff\143\66\5\uffff\50\66\4\uffff\136\66"+
            "\1\uffff\50\66\70\uffff\57\66\1\uffff\44\66\14\uffff\56\66\1"+
            "\uffff\u0080\66\1\uffff\u1ab6\66\12\uffff\u51e6\66\132\uffff"+
            "\u048d\66\3\uffff\67\66\u0739\uffff\u2ba4\66\u215c\uffff\u012e"+
            "\66\2\uffff\73\66\u0095\uffff\7\66\14\uffff\5\66\5\uffff\32"+
            "\66\1\uffff\5\66\1\uffff\1\66\1\uffff\2\66\1\uffff\2\66\1\uffff"+
            "\154\66\41\uffff\u016b\66\22\uffff\100\66\2\uffff\66\66\50\uffff"+
            "\16\66\2\uffff\20\66\20\uffff\4\66\14\uffff\5\66\20\uffff\2"+
            "\66\2\uffff\12\66\1\uffff\5\66\6\uffff\10\66\1\uffff\4\66\4"+
            "\uffff\5\66\1\uffff\u0087\66\4\uffff\7\66\2\uffff\61\66\1\uffff"+
            "\1\66\2\uffff\1\66\1\uffff\32\66\1\uffff\1\66\1\uffff\1\66\2"+
            "\uffff\1\66\2\uffff\133\66\3\uffff\6\66\2\uffff\6\66\2\uffff"+
            "\6\66\2\uffff\3\66\3\uffff\3\66\1\uffff\3\66\1\uffff\7\66",
            "\2\66\1\63\1\62\3\66\2\uffff\1\67\1\66\1\uffff\1\66\1\uffff"+
            "\1\66\12\154\1\uffff\1\66\1\uffff\1\66\1\uffff\1\70\1\66\6\153"+
            "\24\57\1\uffff\1\64\2\uffff\1\61\1\uffff\6\152\24\56\1\uffff"+
            "\1\66\44\uffff\7\66\1\uffff\2\66\1\uffff\1\66\1\uffff\1\66\1"+
            "\uffff\4\66\1\uffff\3\66\1\uffff\2\66\1\uffff\u017b\66\31\uffff"+
            "\162\66\4\uffff\14\66\16\uffff\5\66\11\uffff\1\66\21\uffff\130"+
            "\66\5\uffff\23\66\12\uffff\1\66\3\uffff\1\66\7\uffff\5\66\1"+
            "\uffff\1\66\1\uffff\24\66\1\uffff\54\66\1\uffff\54\66\4\uffff"+
            "\u0087\66\1\uffff\107\66\1\uffff\46\66\2\uffff\2\66\6\uffff"+
            "\20\66\41\uffff\46\66\2\uffff\7\66\1\uffff\47\66\1\uffff\2\66"+
            "\6\uffff\21\66\1\uffff\27\66\1\uffff\12\66\13\uffff\33\66\5"+
            "\uffff\5\66\27\uffff\12\66\5\uffff\1\66\3\uffff\1\66\1\uffff"+
            "\32\66\5\uffff\31\66\7\uffff\175\66\1\uffff\60\66\2\uffff\73"+
            "\66\2\uffff\3\66\60\uffff\62\66\u014f\uffff\71\66\2\uffff\22"+
            "\66\2\uffff\5\66\3\uffff\31\66\20\uffff\3\66\1\uffff\10\66\2"+
            "\uffff\2\66\2\uffff\26\66\1\uffff\7\66\1\uffff\1\66\3\uffff"+
            "\4\66\2\uffff\11\66\2\uffff\2\66\2\uffff\3\66\11\uffff\1\66"+
            "\4\uffff\2\66\1\uffff\5\66\2\uffff\25\66\6\uffff\3\66\1\uffff"+
            "\6\66\4\uffff\2\66\2\uffff\26\66\1\uffff\7\66\1\uffff\2\66\1"+
            "\uffff\2\66\1\uffff\2\66\2\uffff\1\66\1\uffff\5\66\4\uffff\2"+
            "\66\2\uffff\3\66\13\uffff\4\66\1\uffff\1\66\7\uffff\17\66\14"+
            "\uffff\3\66\1\uffff\11\66\1\uffff\3\66\1\uffff\26\66\1\uffff"+
            "\7\66\1\uffff\2\66\1\uffff\5\66\2\uffff\12\66\1\uffff\3\66\1"+
            "\uffff\3\66\2\uffff\1\66\17\uffff\4\66\2\uffff\12\66\1\uffff"+
            "\1\66\17\uffff\3\66\1\uffff\10\66\2\uffff\2\66\2\uffff\26\66"+
            "\1\uffff\7\66\1\uffff\2\66\1\uffff\5\66\2\uffff\10\66\3\uffff"+
            "\2\66\2\uffff\3\66\10\uffff\2\66\4\uffff\2\66\1\uffff\3\66\4"+
            "\uffff\14\66\20\uffff\2\66\1\uffff\6\66\3\uffff\3\66\1\uffff"+
            "\4\66\3\uffff\2\66\1\uffff\1\66\1\uffff\2\66\3\uffff\2\66\3"+
            "\uffff\3\66\3\uffff\10\66\1\uffff\3\66\4\uffff\5\66\3\uffff"+
            "\3\66\1\uffff\4\66\11\uffff\1\66\17\uffff\24\66\6\uffff\3\66"+
            "\1\uffff\10\66\1\uffff\3\66\1\uffff\27\66\1\uffff\12\66\1\uffff"+
            "\5\66\4\uffff\7\66\1\uffff\3\66\1\uffff\4\66\7\uffff\2\66\11"+
            "\uffff\2\66\4\uffff\12\66\22\uffff\2\66\1\uffff\10\66\1\uffff"+
            "\3\66\1\uffff\27\66\1\uffff\12\66\1\uffff\5\66\2\uffff\11\66"+
            "\1\uffff\3\66\1\uffff\4\66\7\uffff\2\66\7\uffff\1\66\1\uffff"+
            "\2\66\4\uffff\12\66\22\uffff\2\66\1\uffff\10\66\1\uffff\3\66"+
            "\1\uffff\27\66\1\uffff\20\66\4\uffff\6\66\2\uffff\3\66\1\uffff"+
            "\4\66\11\uffff\1\66\10\uffff\2\66\4\uffff\12\66\22\uffff\2\66"+
            "\1\uffff\22\66\3\uffff\30\66\1\uffff\11\66\1\uffff\1\66\2\uffff"+
            "\7\66\3\uffff\1\66\4\uffff\6\66\1\uffff\1\66\1\uffff\10\66\22"+
            "\uffff\3\66\14\uffff\72\66\4\uffff\35\66\45\uffff\2\66\1\uffff"+
            "\1\66\2\uffff\2\66\1\uffff\1\66\2\uffff\1\66\6\uffff\4\66\1"+
            "\uffff\7\66\1\uffff\3\66\1\uffff\1\66\1\uffff\1\66\2\uffff\2"+
            "\66\1\uffff\15\66\1\uffff\3\66\2\uffff\5\66\1\uffff\1\66\1\uffff"+
            "\6\66\2\uffff\12\66\2\uffff\2\66\42\uffff\72\66\4\uffff\12\66"+
            "\1\uffff\42\66\6\uffff\33\66\4\uffff\10\66\1\uffff\44\66\1\uffff"+
            "\17\66\2\uffff\1\66\60\uffff\42\66\1\uffff\5\66\1\uffff\2\66"+
            "\1\uffff\7\66\3\uffff\4\66\6\uffff\32\66\106\uffff\46\66\12"+
            "\uffff\51\66\2\uffff\1\66\4\uffff\132\66\5\uffff\104\66\5\uffff"+
            "\122\66\6\uffff\7\66\1\uffff\77\66\1\uffff\1\66\1\uffff\4\66"+
            "\2\uffff\7\66\1\uffff\1\66\1\uffff\4\66\2\uffff\47\66\1\uffff"+
            "\1\66\1\uffff\4\66\2\uffff\37\66\1\uffff\1\66\1\uffff\4\66\2"+
            "\uffff\7\66\1\uffff\1\66\1\uffff\4\66\2\uffff\7\66\1\uffff\7"+
            "\66\1\uffff\27\66\1\uffff\37\66\1\uffff\1\66\1\uffff\4\66\2"+
            "\uffff\7\66\1\uffff\47\66\1\uffff\23\66\6\uffff\34\66\43\uffff"+
            "\125\66\14\uffff\u0276\66\12\uffff\32\66\5\uffff\121\66\17\uffff"+
            "\15\66\1\uffff\7\66\13\uffff\27\66\11\uffff\24\66\14\uffff\15"+
            "\66\1\uffff\3\66\1\uffff\2\66\14\uffff\64\66\2\uffff\50\66\2"+
            "\uffff\12\66\6\uffff\12\66\6\uffff\16\66\2\uffff\12\66\6\uffff"+
            "\130\66\10\uffff\52\66\126\uffff\35\66\3\uffff\14\66\4\uffff"+
            "\14\66\4\uffff\1\66\3\uffff\52\66\2\uffff\5\66\153\uffff\40"+
            "\66\u0300\uffff\154\66\u0094\uffff\u009c\66\4\uffff\132\66\6"+
            "\uffff\26\66\2\uffff\6\66\2\uffff\46\66\2\uffff\6\66\2\uffff"+
            "\10\66\1\uffff\1\66\1\uffff\1\66\1\uffff\1\66\1\uffff\37\66"+
            "\2\uffff\65\66\1\uffff\7\66\1\uffff\1\66\3\uffff\3\66\1\uffff"+
            "\7\66\3\uffff\4\66\2\uffff\6\66\4\uffff\15\66\5\uffff\3\66\1"+
            "\uffff\7\66\23\uffff\10\66\10\uffff\10\66\10\uffff\11\66\2\uffff"+
            "\12\66\2\uffff\16\66\2\uffff\1\66\30\uffff\2\66\2\uffff\11\66"+
            "\2\uffff\16\66\23\uffff\22\66\36\uffff\33\66\25\uffff\74\66"+
            "\1\uffff\17\66\7\uffff\61\66\14\uffff\u0199\66\2\uffff\u0089"+
            "\66\2\uffff\33\66\57\uffff\47\66\31\uffff\13\66\25\uffff\u01b8"+
            "\66\1\uffff\145\66\2\uffff\22\66\16\uffff\2\66\137\uffff\4\66"+
            "\1\uffff\4\66\2\uffff\34\66\1\uffff\43\66\1\uffff\1\66\1\uffff"+
            "\4\66\3\uffff\1\66\1\uffff\7\66\2\uffff\7\66\16\uffff\37\66"+
            "\3\uffff\30\66\1\uffff\16\66\21\uffff\26\66\12\uffff\u0193\66"+
            "\26\uffff\77\66\4\uffff\40\66\2\uffff\u0110\66\u0372\uffff\32"+
            "\66\1\uffff\131\66\14\uffff\u00d6\66\32\uffff\14\66\5\uffff"+
            "\7\66\12\uffff\2\66\10\uffff\1\66\3\uffff\40\66\1\uffff\126"+
            "\66\2\uffff\2\66\2\uffff\143\66\5\uffff\50\66\4\uffff\136\66"+
            "\1\uffff\50\66\70\uffff\57\66\1\uffff\44\66\14\uffff\56\66\1"+
            "\uffff\u0080\66\1\uffff\u1ab6\66\12\uffff\u51e6\66\132\uffff"+
            "\u048d\66\3\uffff\67\66\u0739\uffff\u2ba4\66\u215c\uffff\u012e"+
            "\66\2\uffff\73\66\u0095\uffff\7\66\14\uffff\5\66\5\uffff\32"+
            "\66\1\uffff\5\66\1\uffff\1\66\1\uffff\2\66\1\uffff\2\66\1\uffff"+
            "\154\66\41\uffff\u016b\66\22\uffff\100\66\2\uffff\66\66\50\uffff"+
            "\16\66\2\uffff\20\66\20\uffff\4\66\14\uffff\5\66\20\uffff\2"+
            "\66\2\uffff\12\66\1\uffff\5\66\6\uffff\10\66\1\uffff\4\66\4"+
            "\uffff\5\66\1\uffff\u0087\66\4\uffff\7\66\2\uffff\61\66\1\uffff"+
            "\1\66\2\uffff\1\66\1\uffff\32\66\1\uffff\1\66\1\uffff\1\66\2"+
            "\uffff\1\66\2\uffff\133\66\3\uffff\6\66\2\uffff\6\66\2\uffff"+
            "\6\66\2\uffff\3\66\3\uffff\3\66\1\uffff\3\66\1\uffff\7\66",
            "\2\66\1\63\1\62\3\66\2\uffff\1\67\1\66\1\uffff\1\66\1\uffff"+
            "\1\66\12\154\1\uffff\1\66\1\uffff\1\66\1\uffff\1\70\1\66\6\153"+
            "\24\57\1\uffff\1\64\2\uffff\1\61\1\uffff\6\152\24\56\1\uffff"+
            "\1\66\44\uffff\7\66\1\uffff\2\66\1\uffff\1\66\1\uffff\1\66\1"+
            "\uffff\4\66\1\uffff\3\66\1\uffff\2\66\1\uffff\u017b\66\31\uffff"+
            "\162\66\4\uffff\14\66\16\uffff\5\66\11\uffff\1\66\21\uffff\130"+
            "\66\5\uffff\23\66\12\uffff\1\66\3\uffff\1\66\7\uffff\5\66\1"+
            "\uffff\1\66\1\uffff\24\66\1\uffff\54\66\1\uffff\54\66\4\uffff"+
            "\u0087\66\1\uffff\107\66\1\uffff\46\66\2\uffff\2\66\6\uffff"+
            "\20\66\41\uffff\46\66\2\uffff\7\66\1\uffff\47\66\1\uffff\2\66"+
            "\6\uffff\21\66\1\uffff\27\66\1\uffff\12\66\13\uffff\33\66\5"+
            "\uffff\5\66\27\uffff\12\66\5\uffff\1\66\3\uffff\1\66\1\uffff"+
            "\32\66\5\uffff\31\66\7\uffff\175\66\1\uffff\60\66\2\uffff\73"+
            "\66\2\uffff\3\66\60\uffff\62\66\u014f\uffff\71\66\2\uffff\22"+
            "\66\2\uffff\5\66\3\uffff\31\66\20\uffff\3\66\1\uffff\10\66\2"+
            "\uffff\2\66\2\uffff\26\66\1\uffff\7\66\1\uffff\1\66\3\uffff"+
            "\4\66\2\uffff\11\66\2\uffff\2\66\2\uffff\3\66\11\uffff\1\66"+
            "\4\uffff\2\66\1\uffff\5\66\2\uffff\25\66\6\uffff\3\66\1\uffff"+
            "\6\66\4\uffff\2\66\2\uffff\26\66\1\uffff\7\66\1\uffff\2\66\1"+
            "\uffff\2\66\1\uffff\2\66\2\uffff\1\66\1\uffff\5\66\4\uffff\2"+
            "\66\2\uffff\3\66\13\uffff\4\66\1\uffff\1\66\7\uffff\17\66\14"+
            "\uffff\3\66\1\uffff\11\66\1\uffff\3\66\1\uffff\26\66\1\uffff"+
            "\7\66\1\uffff\2\66\1\uffff\5\66\2\uffff\12\66\1\uffff\3\66\1"+
            "\uffff\3\66\2\uffff\1\66\17\uffff\4\66\2\uffff\12\66\1\uffff"+
            "\1\66\17\uffff\3\66\1\uffff\10\66\2\uffff\2\66\2\uffff\26\66"+
            "\1\uffff\7\66\1\uffff\2\66\1\uffff\5\66\2\uffff\10\66\3\uffff"+
            "\2\66\2\uffff\3\66\10\uffff\2\66\4\uffff\2\66\1\uffff\3\66\4"+
            "\uffff\14\66\20\uffff\2\66\1\uffff\6\66\3\uffff\3\66\1\uffff"+
            "\4\66\3\uffff\2\66\1\uffff\1\66\1\uffff\2\66\3\uffff\2\66\3"+
            "\uffff\3\66\3\uffff\10\66\1\uffff\3\66\4\uffff\5\66\3\uffff"+
            "\3\66\1\uffff\4\66\11\uffff\1\66\17\uffff\24\66\6\uffff\3\66"+
            "\1\uffff\10\66\1\uffff\3\66\1\uffff\27\66\1\uffff\12\66\1\uffff"+
            "\5\66\4\uffff\7\66\1\uffff\3\66\1\uffff\4\66\7\uffff\2\66\11"+
            "\uffff\2\66\4\uffff\12\66\22\uffff\2\66\1\uffff\10\66\1\uffff"+
            "\3\66\1\uffff\27\66\1\uffff\12\66\1\uffff\5\66\2\uffff\11\66"+
            "\1\uffff\3\66\1\uffff\4\66\7\uffff\2\66\7\uffff\1\66\1\uffff"+
            "\2\66\4\uffff\12\66\22\uffff\2\66\1\uffff\10\66\1\uffff\3\66"+
            "\1\uffff\27\66\1\uffff\20\66\4\uffff\6\66\2\uffff\3\66\1\uffff"+
            "\4\66\11\uffff\1\66\10\uffff\2\66\4\uffff\12\66\22\uffff\2\66"+
            "\1\uffff\22\66\3\uffff\30\66\1\uffff\11\66\1\uffff\1\66\2\uffff"+
            "\7\66\3\uffff\1\66\4\uffff\6\66\1\uffff\1\66\1\uffff\10\66\22"+
            "\uffff\3\66\14\uffff\72\66\4\uffff\35\66\45\uffff\2\66\1\uffff"+
            "\1\66\2\uffff\2\66\1\uffff\1\66\2\uffff\1\66\6\uffff\4\66\1"+
            "\uffff\7\66\1\uffff\3\66\1\uffff\1\66\1\uffff\1\66\2\uffff\2"+
            "\66\1\uffff\15\66\1\uffff\3\66\2\uffff\5\66\1\uffff\1\66\1\uffff"+
            "\6\66\2\uffff\12\66\2\uffff\2\66\42\uffff\72\66\4\uffff\12\66"+
            "\1\uffff\42\66\6\uffff\33\66\4\uffff\10\66\1\uffff\44\66\1\uffff"+
            "\17\66\2\uffff\1\66\60\uffff\42\66\1\uffff\5\66\1\uffff\2\66"+
            "\1\uffff\7\66\3\uffff\4\66\6\uffff\32\66\106\uffff\46\66\12"+
            "\uffff\51\66\2\uffff\1\66\4\uffff\132\66\5\uffff\104\66\5\uffff"+
            "\122\66\6\uffff\7\66\1\uffff\77\66\1\uffff\1\66\1\uffff\4\66"+
            "\2\uffff\7\66\1\uffff\1\66\1\uffff\4\66\2\uffff\47\66\1\uffff"+
            "\1\66\1\uffff\4\66\2\uffff\37\66\1\uffff\1\66\1\uffff\4\66\2"+
            "\uffff\7\66\1\uffff\1\66\1\uffff\4\66\2\uffff\7\66\1\uffff\7"+
            "\66\1\uffff\27\66\1\uffff\37\66\1\uffff\1\66\1\uffff\4\66\2"+
            "\uffff\7\66\1\uffff\47\66\1\uffff\23\66\6\uffff\34\66\43\uffff"+
            "\125\66\14\uffff\u0276\66\12\uffff\32\66\5\uffff\121\66\17\uffff"+
            "\15\66\1\uffff\7\66\13\uffff\27\66\11\uffff\24\66\14\uffff\15"+
            "\66\1\uffff\3\66\1\uffff\2\66\14\uffff\64\66\2\uffff\50\66\2"+
            "\uffff\12\66\6\uffff\12\66\6\uffff\16\66\2\uffff\12\66\6\uffff"+
            "\130\66\10\uffff\52\66\126\uffff\35\66\3\uffff\14\66\4\uffff"+
            "\14\66\4\uffff\1\66\3\uffff\52\66\2\uffff\5\66\153\uffff\40"+
            "\66\u0300\uffff\154\66\u0094\uffff\u009c\66\4\uffff\132\66\6"+
            "\uffff\26\66\2\uffff\6\66\2\uffff\46\66\2\uffff\6\66\2\uffff"+
            "\10\66\1\uffff\1\66\1\uffff\1\66\1\uffff\1\66\1\uffff\37\66"+
            "\2\uffff\65\66\1\uffff\7\66\1\uffff\1\66\3\uffff\3\66\1\uffff"+
            "\7\66\3\uffff\4\66\2\uffff\6\66\4\uffff\15\66\5\uffff\3\66\1"+
            "\uffff\7\66\23\uffff\10\66\10\uffff\10\66\10\uffff\11\66\2\uffff"+
            "\12\66\2\uffff\16\66\2\uffff\1\66\30\uffff\2\66\2\uffff\11\66"+
            "\2\uffff\16\66\23\uffff\22\66\36\uffff\33\66\25\uffff\74\66"+
            "\1\uffff\17\66\7\uffff\61\66\14\uffff\u0199\66\2\uffff\u0089"+
            "\66\2\uffff\33\66\57\uffff\47\66\31\uffff\13\66\25\uffff\u01b8"+
            "\66\1\uffff\145\66\2\uffff\22\66\16\uffff\2\66\137\uffff\4\66"+
            "\1\uffff\4\66\2\uffff\34\66\1\uffff\43\66\1\uffff\1\66\1\uffff"+
            "\4\66\3\uffff\1\66\1\uffff\7\66\2\uffff\7\66\16\uffff\37\66"+
            "\3\uffff\30\66\1\uffff\16\66\21\uffff\26\66\12\uffff\u0193\66"+
            "\26\uffff\77\66\4\uffff\40\66\2\uffff\u0110\66\u0372\uffff\32"+
            "\66\1\uffff\131\66\14\uffff\u00d6\66\32\uffff\14\66\5\uffff"+
            "\7\66\12\uffff\2\66\10\uffff\1\66\3\uffff\40\66\1\uffff\126"+
            "\66\2\uffff\2\66\2\uffff\143\66\5\uffff\50\66\4\uffff\136\66"+
            "\1\uffff\50\66\70\uffff\57\66\1\uffff\44\66\14\uffff\56\66\1"+
            "\uffff\u0080\66\1\uffff\u1ab6\66\12\uffff\u51e6\66\132\uffff"+
            "\u048d\66\3\uffff\67\66\u0739\uffff\u2ba4\66\u215c\uffff\u012e"+
            "\66\2\uffff\73\66\u0095\uffff\7\66\14\uffff\5\66\5\uffff\32"+
            "\66\1\uffff\5\66\1\uffff\1\66\1\uffff\2\66\1\uffff\2\66\1\uffff"+
            "\154\66\41\uffff\u016b\66\22\uffff\100\66\2\uffff\66\66\50\uffff"+
            "\16\66\2\uffff\20\66\20\uffff\4\66\14\uffff\5\66\20\uffff\2"+
            "\66\2\uffff\12\66\1\uffff\5\66\6\uffff\10\66\1\uffff\4\66\4"+
            "\uffff\5\66\1\uffff\u0087\66\4\uffff\7\66\2\uffff\61\66\1\uffff"+
            "\1\66\2\uffff\1\66\1\uffff\32\66\1\uffff\1\66\1\uffff\1\66\2"+
            "\uffff\1\66\2\uffff\133\66\3\uffff\6\66\2\uffff\6\66\2\uffff"+
            "\6\66\2\uffff\3\66\3\uffff\3\66\1\uffff\3\66\1\uffff\7\66",
            "\2\66\1\63\1\62\3\66\2\uffff\1\67\1\66\1\uffff\1\66\1\uffff"+
            "\1\66\12\154\1\uffff\1\66\1\uffff\1\66\1\uffff\1\70\1\66\6\153"+
            "\24\57\1\uffff\1\64\2\uffff\1\61\1\uffff\6\152\24\56\1\uffff"+
            "\1\66\44\uffff\7\66\1\uffff\2\66\1\uffff\1\66\1\uffff\1\66\1"+
            "\uffff\4\66\1\uffff\3\66\1\uffff\2\66\1\uffff\u017b\66\31\uffff"+
            "\162\66\4\uffff\14\66\16\uffff\5\66\11\uffff\1\66\21\uffff\130"+
            "\66\5\uffff\23\66\12\uffff\1\66\3\uffff\1\66\7\uffff\5\66\1"+
            "\uffff\1\66\1\uffff\24\66\1\uffff\54\66\1\uffff\54\66\4\uffff"+
            "\u0087\66\1\uffff\107\66\1\uffff\46\66\2\uffff\2\66\6\uffff"+
            "\20\66\41\uffff\46\66\2\uffff\7\66\1\uffff\47\66\1\uffff\2\66"+
            "\6\uffff\21\66\1\uffff\27\66\1\uffff\12\66\13\uffff\33\66\5"+
            "\uffff\5\66\27\uffff\12\66\5\uffff\1\66\3\uffff\1\66\1\uffff"+
            "\32\66\5\uffff\31\66\7\uffff\175\66\1\uffff\60\66\2\uffff\73"+
            "\66\2\uffff\3\66\60\uffff\62\66\u014f\uffff\71\66\2\uffff\22"+
            "\66\2\uffff\5\66\3\uffff\31\66\20\uffff\3\66\1\uffff\10\66\2"+
            "\uffff\2\66\2\uffff\26\66\1\uffff\7\66\1\uffff\1\66\3\uffff"+
            "\4\66\2\uffff\11\66\2\uffff\2\66\2\uffff\3\66\11\uffff\1\66"+
            "\4\uffff\2\66\1\uffff\5\66\2\uffff\25\66\6\uffff\3\66\1\uffff"+
            "\6\66\4\uffff\2\66\2\uffff\26\66\1\uffff\7\66\1\uffff\2\66\1"+
            "\uffff\2\66\1\uffff\2\66\2\uffff\1\66\1\uffff\5\66\4\uffff\2"+
            "\66\2\uffff\3\66\13\uffff\4\66\1\uffff\1\66\7\uffff\17\66\14"+
            "\uffff\3\66\1\uffff\11\66\1\uffff\3\66\1\uffff\26\66\1\uffff"+
            "\7\66\1\uffff\2\66\1\uffff\5\66\2\uffff\12\66\1\uffff\3\66\1"+
            "\uffff\3\66\2\uffff\1\66\17\uffff\4\66\2\uffff\12\66\1\uffff"+
            "\1\66\17\uffff\3\66\1\uffff\10\66\2\uffff\2\66\2\uffff\26\66"+
            "\1\uffff\7\66\1\uffff\2\66\1\uffff\5\66\2\uffff\10\66\3\uffff"+
            "\2\66\2\uffff\3\66\10\uffff\2\66\4\uffff\2\66\1\uffff\3\66\4"+
            "\uffff\14\66\20\uffff\2\66\1\uffff\6\66\3\uffff\3\66\1\uffff"+
            "\4\66\3\uffff\2\66\1\uffff\1\66\1\uffff\2\66\3\uffff\2\66\3"+
            "\uffff\3\66\3\uffff\10\66\1\uffff\3\66\4\uffff\5\66\3\uffff"+
            "\3\66\1\uffff\4\66\11\uffff\1\66\17\uffff\24\66\6\uffff\3\66"+
            "\1\uffff\10\66\1\uffff\3\66\1\uffff\27\66\1\uffff\12\66\1\uffff"+
            "\5\66\4\uffff\7\66\1\uffff\3\66\1\uffff\4\66\7\uffff\2\66\11"+
            "\uffff\2\66\4\uffff\12\66\22\uffff\2\66\1\uffff\10\66\1\uffff"+
            "\3\66\1\uffff\27\66\1\uffff\12\66\1\uffff\5\66\2\uffff\11\66"+
            "\1\uffff\3\66\1\uffff\4\66\7\uffff\2\66\7\uffff\1\66\1\uffff"+
            "\2\66\4\uffff\12\66\22\uffff\2\66\1\uffff\10\66\1\uffff\3\66"+
            "\1\uffff\27\66\1\uffff\20\66\4\uffff\6\66\2\uffff\3\66\1\uffff"+
            "\4\66\11\uffff\1\66\10\uffff\2\66\4\uffff\12\66\22\uffff\2\66"+
            "\1\uffff\22\66\3\uffff\30\66\1\uffff\11\66\1\uffff\1\66\2\uffff"+
            "\7\66\3\uffff\1\66\4\uffff\6\66\1\uffff\1\66\1\uffff\10\66\22"+
            "\uffff\3\66\14\uffff\72\66\4\uffff\35\66\45\uffff\2\66\1\uffff"+
            "\1\66\2\uffff\2\66\1\uffff\1\66\2\uffff\1\66\6\uffff\4\66\1"+
            "\uffff\7\66\1\uffff\3\66\1\uffff\1\66\1\uffff\1\66\2\uffff\2"+
            "\66\1\uffff\15\66\1\uffff\3\66\2\uffff\5\66\1\uffff\1\66\1\uffff"+
            "\6\66\2\uffff\12\66\2\uffff\2\66\42\uffff\72\66\4\uffff\12\66"+
            "\1\uffff\42\66\6\uffff\33\66\4\uffff\10\66\1\uffff\44\66\1\uffff"+
            "\17\66\2\uffff\1\66\60\uffff\42\66\1\uffff\5\66\1\uffff\2\66"+
            "\1\uffff\7\66\3\uffff\4\66\6\uffff\32\66\106\uffff\46\66\12"+
            "\uffff\51\66\2\uffff\1\66\4\uffff\132\66\5\uffff\104\66\5\uffff"+
            "\122\66\6\uffff\7\66\1\uffff\77\66\1\uffff\1\66\1\uffff\4\66"+
            "\2\uffff\7\66\1\uffff\1\66\1\uffff\4\66\2\uffff\47\66\1\uffff"+
            "\1\66\1\uffff\4\66\2\uffff\37\66\1\uffff\1\66\1\uffff\4\66\2"+
            "\uffff\7\66\1\uffff\1\66\1\uffff\4\66\2\uffff\7\66\1\uffff\7"+
            "\66\1\uffff\27\66\1\uffff\37\66\1\uffff\1\66\1\uffff\4\66\2"+
            "\uffff\7\66\1\uffff\47\66\1\uffff\23\66\6\uffff\34\66\43\uffff"+
            "\125\66\14\uffff\u0276\66\12\uffff\32\66\5\uffff\121\66\17\uffff"+
            "\15\66\1\uffff\7\66\13\uffff\27\66\11\uffff\24\66\14\uffff\15"+
            "\66\1\uffff\3\66\1\uffff\2\66\14\uffff\64\66\2\uffff\50\66\2"+
            "\uffff\12\66\6\uffff\12\66\6\uffff\16\66\2\uffff\12\66\6\uffff"+
            "\130\66\10\uffff\52\66\126\uffff\35\66\3\uffff\14\66\4\uffff"+
            "\14\66\4\uffff\1\66\3\uffff\52\66\2\uffff\5\66\153\uffff\40"+
            "\66\u0300\uffff\154\66\u0094\uffff\u009c\66\4\uffff\132\66\6"+
            "\uffff\26\66\2\uffff\6\66\2\uffff\46\66\2\uffff\6\66\2\uffff"+
            "\10\66\1\uffff\1\66\1\uffff\1\66\1\uffff\1\66\1\uffff\37\66"+
            "\2\uffff\65\66\1\uffff\7\66\1\uffff\1\66\3\uffff\3\66\1\uffff"+
            "\7\66\3\uffff\4\66\2\uffff\6\66\4\uffff\15\66\5\uffff\3\66\1"+
            "\uffff\7\66\23\uffff\10\66\10\uffff\10\66\10\uffff\11\66\2\uffff"+
            "\12\66\2\uffff\16\66\2\uffff\1\66\30\uffff\2\66\2\uffff\11\66"+
            "\2\uffff\16\66\23\uffff\22\66\36\uffff\33\66\25\uffff\74\66"+
            "\1\uffff\17\66\7\uffff\61\66\14\uffff\u0199\66\2\uffff\u0089"+
            "\66\2\uffff\33\66\57\uffff\47\66\31\uffff\13\66\25\uffff\u01b8"+
            "\66\1\uffff\145\66\2\uffff\22\66\16\uffff\2\66\137\uffff\4\66"+
            "\1\uffff\4\66\2\uffff\34\66\1\uffff\43\66\1\uffff\1\66\1\uffff"+
            "\4\66\3\uffff\1\66\1\uffff\7\66\2\uffff\7\66\16\uffff\37\66"+
            "\3\uffff\30\66\1\uffff\16\66\21\uffff\26\66\12\uffff\u0193\66"+
            "\26\uffff\77\66\4\uffff\40\66\2\uffff\u0110\66\u0372\uffff\32"+
            "\66\1\uffff\131\66\14\uffff\u00d6\66\32\uffff\14\66\5\uffff"+
            "\7\66\12\uffff\2\66\10\uffff\1\66\3\uffff\40\66\1\uffff\126"+
            "\66\2\uffff\2\66\2\uffff\143\66\5\uffff\50\66\4\uffff\136\66"+
            "\1\uffff\50\66\70\uffff\57\66\1\uffff\44\66\14\uffff\56\66\1"+
            "\uffff\u0080\66\1\uffff\u1ab6\66\12\uffff\u51e6\66\132\uffff"+
            "\u048d\66\3\uffff\67\66\u0739\uffff\u2ba4\66\u215c\uffff\u012e"+
            "\66\2\uffff\73\66\u0095\uffff\7\66\14\uffff\5\66\5\uffff\32"+
            "\66\1\uffff\5\66\1\uffff\1\66\1\uffff\2\66\1\uffff\2\66\1\uffff"+
            "\154\66\41\uffff\u016b\66\22\uffff\100\66\2\uffff\66\66\50\uffff"+
            "\16\66\2\uffff\20\66\20\uffff\4\66\14\uffff\5\66\20\uffff\2"+
            "\66\2\uffff\12\66\1\uffff\5\66\6\uffff\10\66\1\uffff\4\66\4"+
            "\uffff\5\66\1\uffff\u0087\66\4\uffff\7\66\2\uffff\61\66\1\uffff"+
            "\1\66\2\uffff\1\66\1\uffff\32\66\1\uffff\1\66\1\uffff\1\66\2"+
            "\uffff\1\66\2\uffff\133\66\3\uffff\6\66\2\uffff\6\66\2\uffff"+
            "\6\66\2\uffff\3\66\3\uffff\3\66\1\uffff\3\66\1\uffff\7\66",
            "\7\66\2\uffff\1\67\1\66\1\uffff\1\66\1\uffff\1\66\12\155\1"+
            "\uffff\1\66\1\uffff\1\66\1\uffff\1\70\1\66\6\155\24\66\1\uffff"+
            "\1\110\2\uffff\1\66\1\uffff\6\155\24\66\1\uffff\1\66\44\uffff"+
            "\7\66\1\uffff\2\66\1\uffff\1\66\1\uffff\1\66\1\uffff\4\66\1"+
            "\uffff\3\66\1\uffff\2\66\1\uffff\u017b\66\31\uffff\162\66\4"+
            "\uffff\14\66\16\uffff\5\66\11\uffff\1\66\21\uffff\130\66\5\uffff"+
            "\23\66\12\uffff\1\66\3\uffff\1\66\7\uffff\5\66\1\uffff\1\66"+
            "\1\uffff\24\66\1\uffff\54\66\1\uffff\54\66\4\uffff\u0087\66"+
            "\1\uffff\107\66\1\uffff\46\66\2\uffff\2\66\6\uffff\20\66\41"+
            "\uffff\46\66\2\uffff\7\66\1\uffff\47\66\1\uffff\2\66\6\uffff"+
            "\21\66\1\uffff\27\66\1\uffff\12\66\13\uffff\33\66\5\uffff\5"+
            "\66\27\uffff\12\66\5\uffff\1\66\3\uffff\1\66\1\uffff\32\66\5"+
            "\uffff\31\66\7\uffff\175\66\1\uffff\60\66\2\uffff\73\66\2\uffff"+
            "\3\66\60\uffff\62\66\u014f\uffff\71\66\2\uffff\22\66\2\uffff"+
            "\5\66\3\uffff\31\66\20\uffff\3\66\1\uffff\10\66\2\uffff\2\66"+
            "\2\uffff\26\66\1\uffff\7\66\1\uffff\1\66\3\uffff\4\66\2\uffff"+
            "\11\66\2\uffff\2\66\2\uffff\3\66\11\uffff\1\66\4\uffff\2\66"+
            "\1\uffff\5\66\2\uffff\25\66\6\uffff\3\66\1\uffff\6\66\4\uffff"+
            "\2\66\2\uffff\26\66\1\uffff\7\66\1\uffff\2\66\1\uffff\2\66\1"+
            "\uffff\2\66\2\uffff\1\66\1\uffff\5\66\4\uffff\2\66\2\uffff\3"+
            "\66\13\uffff\4\66\1\uffff\1\66\7\uffff\17\66\14\uffff\3\66\1"+
            "\uffff\11\66\1\uffff\3\66\1\uffff\26\66\1\uffff\7\66\1\uffff"+
            "\2\66\1\uffff\5\66\2\uffff\12\66\1\uffff\3\66\1\uffff\3\66\2"+
            "\uffff\1\66\17\uffff\4\66\2\uffff\12\66\1\uffff\1\66\17\uffff"+
            "\3\66\1\uffff\10\66\2\uffff\2\66\2\uffff\26\66\1\uffff\7\66"+
            "\1\uffff\2\66\1\uffff\5\66\2\uffff\10\66\3\uffff\2\66\2\uffff"+
            "\3\66\10\uffff\2\66\4\uffff\2\66\1\uffff\3\66\4\uffff\14\66"+
            "\20\uffff\2\66\1\uffff\6\66\3\uffff\3\66\1\uffff\4\66\3\uffff"+
            "\2\66\1\uffff\1\66\1\uffff\2\66\3\uffff\2\66\3\uffff\3\66\3"+
            "\uffff\10\66\1\uffff\3\66\4\uffff\5\66\3\uffff\3\66\1\uffff"+
            "\4\66\11\uffff\1\66\17\uffff\24\66\6\uffff\3\66\1\uffff\10\66"+
            "\1\uffff\3\66\1\uffff\27\66\1\uffff\12\66\1\uffff\5\66\4\uffff"+
            "\7\66\1\uffff\3\66\1\uffff\4\66\7\uffff\2\66\11\uffff\2\66\4"+
            "\uffff\12\66\22\uffff\2\66\1\uffff\10\66\1\uffff\3\66\1\uffff"+
            "\27\66\1\uffff\12\66\1\uffff\5\66\2\uffff\11\66\1\uffff\3\66"+
            "\1\uffff\4\66\7\uffff\2\66\7\uffff\1\66\1\uffff\2\66\4\uffff"+
            "\12\66\22\uffff\2\66\1\uffff\10\66\1\uffff\3\66\1\uffff\27\66"+
            "\1\uffff\20\66\4\uffff\6\66\2\uffff\3\66\1\uffff\4\66\11\uffff"+
            "\1\66\10\uffff\2\66\4\uffff\12\66\22\uffff\2\66\1\uffff\22\66"+
            "\3\uffff\30\66\1\uffff\11\66\1\uffff\1\66\2\uffff\7\66\3\uffff"+
            "\1\66\4\uffff\6\66\1\uffff\1\66\1\uffff\10\66\22\uffff\3\66"+
            "\14\uffff\72\66\4\uffff\35\66\45\uffff\2\66\1\uffff\1\66\2\uffff"+
            "\2\66\1\uffff\1\66\2\uffff\1\66\6\uffff\4\66\1\uffff\7\66\1"+
            "\uffff\3\66\1\uffff\1\66\1\uffff\1\66\2\uffff\2\66\1\uffff\15"+
            "\66\1\uffff\3\66\2\uffff\5\66\1\uffff\1\66\1\uffff\6\66\2\uffff"+
            "\12\66\2\uffff\2\66\42\uffff\72\66\4\uffff\12\66\1\uffff\42"+
            "\66\6\uffff\33\66\4\uffff\10\66\1\uffff\44\66\1\uffff\17\66"+
            "\2\uffff\1\66\60\uffff\42\66\1\uffff\5\66\1\uffff\2\66\1\uffff"+
            "\7\66\3\uffff\4\66\6\uffff\32\66\106\uffff\46\66\12\uffff\51"+
            "\66\2\uffff\1\66\4\uffff\132\66\5\uffff\104\66\5\uffff\122\66"+
            "\6\uffff\7\66\1\uffff\77\66\1\uffff\1\66\1\uffff\4\66\2\uffff"+
            "\7\66\1\uffff\1\66\1\uffff\4\66\2\uffff\47\66\1\uffff\1\66\1"+
            "\uffff\4\66\2\uffff\37\66\1\uffff\1\66\1\uffff\4\66\2\uffff"+
            "\7\66\1\uffff\1\66\1\uffff\4\66\2\uffff\7\66\1\uffff\7\66\1"+
            "\uffff\27\66\1\uffff\37\66\1\uffff\1\66\1\uffff\4\66\2\uffff"+
            "\7\66\1\uffff\47\66\1\uffff\23\66\6\uffff\34\66\43\uffff\125"+
            "\66\14\uffff\u0276\66\12\uffff\32\66\5\uffff\121\66\17\uffff"+
            "\15\66\1\uffff\7\66\13\uffff\27\66\11\uffff\24\66\14\uffff\15"+
            "\66\1\uffff\3\66\1\uffff\2\66\14\uffff\64\66\2\uffff\50\66\2"+
            "\uffff\12\66\6\uffff\12\66\6\uffff\16\66\2\uffff\12\66\6\uffff"+
            "\130\66\10\uffff\52\66\126\uffff\35\66\3\uffff\14\66\4\uffff"+
            "\14\66\4\uffff\1\66\3\uffff\52\66\2\uffff\5\66\153\uffff\40"+
            "\66\u0300\uffff\154\66\u0094\uffff\u009c\66\4\uffff\132\66\6"+
            "\uffff\26\66\2\uffff\6\66\2\uffff\46\66\2\uffff\6\66\2\uffff"+
            "\10\66\1\uffff\1\66\1\uffff\1\66\1\uffff\1\66\1\uffff\37\66"+
            "\2\uffff\65\66\1\uffff\7\66\1\uffff\1\66\3\uffff\3\66\1\uffff"+
            "\7\66\3\uffff\4\66\2\uffff\6\66\4\uffff\15\66\5\uffff\3\66\1"+
            "\uffff\7\66\23\uffff\10\66\10\uffff\10\66\10\uffff\11\66\2\uffff"+
            "\12\66\2\uffff\16\66\2\uffff\1\66\30\uffff\2\66\2\uffff\11\66"+
            "\2\uffff\16\66\23\uffff\22\66\36\uffff\33\66\25\uffff\74\66"+
            "\1\uffff\17\66\7\uffff\61\66\14\uffff\u0199\66\2\uffff\u0089"+
            "\66\2\uffff\33\66\57\uffff\47\66\31\uffff\13\66\25\uffff\u01b8"+
            "\66\1\uffff\145\66\2\uffff\22\66\16\uffff\2\66\137\uffff\4\66"+
            "\1\uffff\4\66\2\uffff\34\66\1\uffff\43\66\1\uffff\1\66\1\uffff"+
            "\4\66\3\uffff\1\66\1\uffff\7\66\2\uffff\7\66\16\uffff\37\66"+
            "\3\uffff\30\66\1\uffff\16\66\21\uffff\26\66\12\uffff\u0193\66"+
            "\26\uffff\77\66\4\uffff\40\66\2\uffff\u0110\66\u0372\uffff\32"+
            "\66\1\uffff\131\66\14\uffff\u00d6\66\32\uffff\14\66\5\uffff"+
            "\7\66\12\uffff\2\66\10\uffff\1\66\3\uffff\40\66\1\uffff\126"+
            "\66\2\uffff\2\66\2\uffff\143\66\5\uffff\50\66\4\uffff\136\66"+
            "\1\uffff\50\66\70\uffff\57\66\1\uffff\44\66\14\uffff\56\66\1"+
            "\uffff\u0080\66\1\uffff\u1ab6\66\12\uffff\u51e6\66\132\uffff"+
            "\u048d\66\3\uffff\67\66\u0739\uffff\u2ba4\66\u215c\uffff\u012e"+
            "\66\2\uffff\73\66\u0095\uffff\7\66\14\uffff\5\66\5\uffff\32"+
            "\66\1\uffff\5\66\1\uffff\1\66\1\uffff\2\66\1\uffff\2\66\1\uffff"+
            "\154\66\41\uffff\u016b\66\22\uffff\100\66\2\uffff\66\66\50\uffff"+
            "\16\66\2\uffff\20\66\20\uffff\4\66\14\uffff\5\66\20\uffff\2"+
            "\66\2\uffff\12\66\1\uffff\5\66\6\uffff\10\66\1\uffff\4\66\4"+
            "\uffff\5\66\1\uffff\u0087\66\4\uffff\7\66\2\uffff\61\66\1\uffff"+
            "\1\66\2\uffff\1\66\1\uffff\32\66\1\uffff\1\66\1\uffff\1\66\2"+
            "\uffff\1\66\2\uffff\133\66\3\uffff\6\66\2\uffff\6\66\2\uffff"+
            "\6\66\2\uffff\3\66\3\uffff\3\66\1\uffff\3\66\1\uffff\7\66",
            "\7\66\2\uffff\1\67\1\66\1\uffff\1\66\1\uffff\1\66\12\156\1"+
            "\uffff\1\66\1\uffff\1\66\1\uffff\1\70\1\66\6\156\24\66\1\uffff"+
            "\1\110\2\uffff\1\66\1\uffff\6\156\24\66\1\uffff\1\66\44\uffff"+
            "\7\66\1\uffff\2\66\1\uffff\1\66\1\uffff\1\66\1\uffff\4\66\1"+
            "\uffff\3\66\1\uffff\2\66\1\uffff\u017b\66\31\uffff\162\66\4"+
            "\uffff\14\66\16\uffff\5\66\11\uffff\1\66\21\uffff\130\66\5\uffff"+
            "\23\66\12\uffff\1\66\3\uffff\1\66\7\uffff\5\66\1\uffff\1\66"+
            "\1\uffff\24\66\1\uffff\54\66\1\uffff\54\66\4\uffff\u0087\66"+
            "\1\uffff\107\66\1\uffff\46\66\2\uffff\2\66\6\uffff\20\66\41"+
            "\uffff\46\66\2\uffff\7\66\1\uffff\47\66\1\uffff\2\66\6\uffff"+
            "\21\66\1\uffff\27\66\1\uffff\12\66\13\uffff\33\66\5\uffff\5"+
            "\66\27\uffff\12\66\5\uffff\1\66\3\uffff\1\66\1\uffff\32\66\5"+
            "\uffff\31\66\7\uffff\175\66\1\uffff\60\66\2\uffff\73\66\2\uffff"+
            "\3\66\60\uffff\62\66\u014f\uffff\71\66\2\uffff\22\66\2\uffff"+
            "\5\66\3\uffff\31\66\20\uffff\3\66\1\uffff\10\66\2\uffff\2\66"+
            "\2\uffff\26\66\1\uffff\7\66\1\uffff\1\66\3\uffff\4\66\2\uffff"+
            "\11\66\2\uffff\2\66\2\uffff\3\66\11\uffff\1\66\4\uffff\2\66"+
            "\1\uffff\5\66\2\uffff\25\66\6\uffff\3\66\1\uffff\6\66\4\uffff"+
            "\2\66\2\uffff\26\66\1\uffff\7\66\1\uffff\2\66\1\uffff\2\66\1"+
            "\uffff\2\66\2\uffff\1\66\1\uffff\5\66\4\uffff\2\66\2\uffff\3"+
            "\66\13\uffff\4\66\1\uffff\1\66\7\uffff\17\66\14\uffff\3\66\1"+
            "\uffff\11\66\1\uffff\3\66\1\uffff\26\66\1\uffff\7\66\1\uffff"+
            "\2\66\1\uffff\5\66\2\uffff\12\66\1\uffff\3\66\1\uffff\3\66\2"+
            "\uffff\1\66\17\uffff\4\66\2\uffff\12\66\1\uffff\1\66\17\uffff"+
            "\3\66\1\uffff\10\66\2\uffff\2\66\2\uffff\26\66\1\uffff\7\66"+
            "\1\uffff\2\66\1\uffff\5\66\2\uffff\10\66\3\uffff\2\66\2\uffff"+
            "\3\66\10\uffff\2\66\4\uffff\2\66\1\uffff\3\66\4\uffff\14\66"+
            "\20\uffff\2\66\1\uffff\6\66\3\uffff\3\66\1\uffff\4\66\3\uffff"+
            "\2\66\1\uffff\1\66\1\uffff\2\66\3\uffff\2\66\3\uffff\3\66\3"+
            "\uffff\10\66\1\uffff\3\66\4\uffff\5\66\3\uffff\3\66\1\uffff"+
            "\4\66\11\uffff\1\66\17\uffff\24\66\6\uffff\3\66\1\uffff\10\66"+
            "\1\uffff\3\66\1\uffff\27\66\1\uffff\12\66\1\uffff\5\66\4\uffff"+
            "\7\66\1\uffff\3\66\1\uffff\4\66\7\uffff\2\66\11\uffff\2\66\4"+
            "\uffff\12\66\22\uffff\2\66\1\uffff\10\66\1\uffff\3\66\1\uffff"+
            "\27\66\1\uffff\12\66\1\uffff\5\66\2\uffff\11\66\1\uffff\3\66"+
            "\1\uffff\4\66\7\uffff\2\66\7\uffff\1\66\1\uffff\2\66\4\uffff"+
            "\12\66\22\uffff\2\66\1\uffff\10\66\1\uffff\3\66\1\uffff\27\66"+
            "\1\uffff\20\66\4\uffff\6\66\2\uffff\3\66\1\uffff\4\66\11\uffff"+
            "\1\66\10\uffff\2\66\4\uffff\12\66\22\uffff\2\66\1\uffff\22\66"+
            "\3\uffff\30\66\1\uffff\11\66\1\uffff\1\66\2\uffff\7\66\3\uffff"+
            "\1\66\4\uffff\6\66\1\uffff\1\66\1\uffff\10\66\22\uffff\3\66"+
            "\14\uffff\72\66\4\uffff\35\66\45\uffff\2\66\1\uffff\1\66\2\uffff"+
            "\2\66\1\uffff\1\66\2\uffff\1\66\6\uffff\4\66\1\uffff\7\66\1"+
            "\uffff\3\66\1\uffff\1\66\1\uffff\1\66\2\uffff\2\66\1\uffff\15"+
            "\66\1\uffff\3\66\2\uffff\5\66\1\uffff\1\66\1\uffff\6\66\2\uffff"+
            "\12\66\2\uffff\2\66\42\uffff\72\66\4\uffff\12\66\1\uffff\42"+
            "\66\6\uffff\33\66\4\uffff\10\66\1\uffff\44\66\1\uffff\17\66"+
            "\2\uffff\1\66\60\uffff\42\66\1\uffff\5\66\1\uffff\2\66\1\uffff"+
            "\7\66\3\uffff\4\66\6\uffff\32\66\106\uffff\46\66\12\uffff\51"+
            "\66\2\uffff\1\66\4\uffff\132\66\5\uffff\104\66\5\uffff\122\66"+
            "\6\uffff\7\66\1\uffff\77\66\1\uffff\1\66\1\uffff\4\66\2\uffff"+
            "\7\66\1\uffff\1\66\1\uffff\4\66\2\uffff\47\66\1\uffff\1\66\1"+
            "\uffff\4\66\2\uffff\37\66\1\uffff\1\66\1\uffff\4\66\2\uffff"+
            "\7\66\1\uffff\1\66\1\uffff\4\66\2\uffff\7\66\1\uffff\7\66\1"+
            "\uffff\27\66\1\uffff\37\66\1\uffff\1\66\1\uffff\4\66\2\uffff"+
            "\7\66\1\uffff\47\66\1\uffff\23\66\6\uffff\34\66\43\uffff\125"+
            "\66\14\uffff\u0276\66\12\uffff\32\66\5\uffff\121\66\17\uffff"+
            "\15\66\1\uffff\7\66\13\uffff\27\66\11\uffff\24\66\14\uffff\15"+
            "\66\1\uffff\3\66\1\uffff\2\66\14\uffff\64\66\2\uffff\50\66\2"+
            "\uffff\12\66\6\uffff\12\66\6\uffff\16\66\2\uffff\12\66\6\uffff"+
            "\130\66\10\uffff\52\66\126\uffff\35\66\3\uffff\14\66\4\uffff"+
            "\14\66\4\uffff\1\66\3\uffff\52\66\2\uffff\5\66\153\uffff\40"+
            "\66\u0300\uffff\154\66\u0094\uffff\u009c\66\4\uffff\132\66\6"+
            "\uffff\26\66\2\uffff\6\66\2\uffff\46\66\2\uffff\6\66\2\uffff"+
            "\10\66\1\uffff\1\66\1\uffff\1\66\1\uffff\1\66\1\uffff\37\66"+
            "\2\uffff\65\66\1\uffff\7\66\1\uffff\1\66\3\uffff\3\66\1\uffff"+
            "\7\66\3\uffff\4\66\2\uffff\6\66\4\uffff\15\66\5\uffff\3\66\1"+
            "\uffff\7\66\23\uffff\10\66\10\uffff\10\66\10\uffff\11\66\2\uffff"+
            "\12\66\2\uffff\16\66\2\uffff\1\66\30\uffff\2\66\2\uffff\11\66"+
            "\2\uffff\16\66\23\uffff\22\66\36\uffff\33\66\25\uffff\74\66"+
            "\1\uffff\17\66\7\uffff\61\66\14\uffff\u0199\66\2\uffff\u0089"+
            "\66\2\uffff\33\66\57\uffff\47\66\31\uffff\13\66\25\uffff\u01b8"+
            "\66\1\uffff\145\66\2\uffff\22\66\16\uffff\2\66\137\uffff\4\66"+
            "\1\uffff\4\66\2\uffff\34\66\1\uffff\43\66\1\uffff\1\66\1\uffff"+
            "\4\66\3\uffff\1\66\1\uffff\7\66\2\uffff\7\66\16\uffff\37\66"+
            "\3\uffff\30\66\1\uffff\16\66\21\uffff\26\66\12\uffff\u0193\66"+
            "\26\uffff\77\66\4\uffff\40\66\2\uffff\u0110\66\u0372\uffff\32"+
            "\66\1\uffff\131\66\14\uffff\u00d6\66\32\uffff\14\66\5\uffff"+
            "\7\66\12\uffff\2\66\10\uffff\1\66\3\uffff\40\66\1\uffff\126"+
            "\66\2\uffff\2\66\2\uffff\143\66\5\uffff\50\66\4\uffff\136\66"+
            "\1\uffff\50\66\70\uffff\57\66\1\uffff\44\66\14\uffff\56\66\1"+
            "\uffff\u0080\66\1\uffff\u1ab6\66\12\uffff\u51e6\66\132\uffff"+
            "\u048d\66\3\uffff\67\66\u0739\uffff\u2ba4\66\u215c\uffff\u012e"+
            "\66\2\uffff\73\66\u0095\uffff\7\66\14\uffff\5\66\5\uffff\32"+
            "\66\1\uffff\5\66\1\uffff\1\66\1\uffff\2\66\1\uffff\2\66\1\uffff"+
            "\154\66\41\uffff\u016b\66\22\uffff\100\66\2\uffff\66\66\50\uffff"+
            "\16\66\2\uffff\20\66\20\uffff\4\66\14\uffff\5\66\20\uffff\2"+
            "\66\2\uffff\12\66\1\uffff\5\66\6\uffff\10\66\1\uffff\4\66\4"+
            "\uffff\5\66\1\uffff\u0087\66\4\uffff\7\66\2\uffff\61\66\1\uffff"+
            "\1\66\2\uffff\1\66\1\uffff\32\66\1\uffff\1\66\1\uffff\1\66\2"+
            "\uffff\1\66\2\uffff\133\66\3\uffff\6\66\2\uffff\6\66\2\uffff"+
            "\6\66\2\uffff\3\66\3\uffff\3\66\1\uffff\3\66\1\uffff\7\66",
            "\2\66\1\63\1\62\3\66\2\uffff\1\67\1\66\1\uffff\1\66\1\uffff"+
            "\1\66\12\161\1\uffff\1\66\1\uffff\1\66\1\uffff\1\70\1\66\6\160"+
            "\24\57\1\uffff\1\64\2\uffff\1\61\1\uffff\6\157\24\56\1\uffff"+
            "\1\66\44\uffff\7\66\1\uffff\2\66\1\uffff\1\66\1\uffff\1\66\1"+
            "\uffff\4\66\1\uffff\3\66\1\uffff\2\66\1\uffff\u017b\66\31\uffff"+
            "\162\66\4\uffff\14\66\16\uffff\5\66\11\uffff\1\66\21\uffff\130"+
            "\66\5\uffff\23\66\12\uffff\1\66\3\uffff\1\66\7\uffff\5\66\1"+
            "\uffff\1\66\1\uffff\24\66\1\uffff\54\66\1\uffff\54\66\4\uffff"+
            "\u0087\66\1\uffff\107\66\1\uffff\46\66\2\uffff\2\66\6\uffff"+
            "\20\66\41\uffff\46\66\2\uffff\7\66\1\uffff\47\66\1\uffff\2\66"+
            "\6\uffff\21\66\1\uffff\27\66\1\uffff\12\66\13\uffff\33\66\5"+
            "\uffff\5\66\27\uffff\12\66\5\uffff\1\66\3\uffff\1\66\1\uffff"+
            "\32\66\5\uffff\31\66\7\uffff\175\66\1\uffff\60\66\2\uffff\73"+
            "\66\2\uffff\3\66\60\uffff\62\66\u014f\uffff\71\66\2\uffff\22"+
            "\66\2\uffff\5\66\3\uffff\31\66\20\uffff\3\66\1\uffff\10\66\2"+
            "\uffff\2\66\2\uffff\26\66\1\uffff\7\66\1\uffff\1\66\3\uffff"+
            "\4\66\2\uffff\11\66\2\uffff\2\66\2\uffff\3\66\11\uffff\1\66"+
            "\4\uffff\2\66\1\uffff\5\66\2\uffff\25\66\6\uffff\3\66\1\uffff"+
            "\6\66\4\uffff\2\66\2\uffff\26\66\1\uffff\7\66\1\uffff\2\66\1"+
            "\uffff\2\66\1\uffff\2\66\2\uffff\1\66\1\uffff\5\66\4\uffff\2"+
            "\66\2\uffff\3\66\13\uffff\4\66\1\uffff\1\66\7\uffff\17\66\14"+
            "\uffff\3\66\1\uffff\11\66\1\uffff\3\66\1\uffff\26\66\1\uffff"+
            "\7\66\1\uffff\2\66\1\uffff\5\66\2\uffff\12\66\1\uffff\3\66\1"+
            "\uffff\3\66\2\uffff\1\66\17\uffff\4\66\2\uffff\12\66\1\uffff"+
            "\1\66\17\uffff\3\66\1\uffff\10\66\2\uffff\2\66\2\uffff\26\66"+
            "\1\uffff\7\66\1\uffff\2\66\1\uffff\5\66\2\uffff\10\66\3\uffff"+
            "\2\66\2\uffff\3\66\10\uffff\2\66\4\uffff\2\66\1\uffff\3\66\4"+
            "\uffff\14\66\20\uffff\2\66\1\uffff\6\66\3\uffff\3\66\1\uffff"+
            "\4\66\3\uffff\2\66\1\uffff\1\66\1\uffff\2\66\3\uffff\2\66\3"+
            "\uffff\3\66\3\uffff\10\66\1\uffff\3\66\4\uffff\5\66\3\uffff"+
            "\3\66\1\uffff\4\66\11\uffff\1\66\17\uffff\24\66\6\uffff\3\66"+
            "\1\uffff\10\66\1\uffff\3\66\1\uffff\27\66\1\uffff\12\66\1\uffff"+
            "\5\66\4\uffff\7\66\1\uffff\3\66\1\uffff\4\66\7\uffff\2\66\11"+
            "\uffff\2\66\4\uffff\12\66\22\uffff\2\66\1\uffff\10\66\1\uffff"+
            "\3\66\1\uffff\27\66\1\uffff\12\66\1\uffff\5\66\2\uffff\11\66"+
            "\1\uffff\3\66\1\uffff\4\66\7\uffff\2\66\7\uffff\1\66\1\uffff"+
            "\2\66\4\uffff\12\66\22\uffff\2\66\1\uffff\10\66\1\uffff\3\66"+
            "\1\uffff\27\66\1\uffff\20\66\4\uffff\6\66\2\uffff\3\66\1\uffff"+
            "\4\66\11\uffff\1\66\10\uffff\2\66\4\uffff\12\66\22\uffff\2\66"+
            "\1\uffff\22\66\3\uffff\30\66\1\uffff\11\66\1\uffff\1\66\2\uffff"+
            "\7\66\3\uffff\1\66\4\uffff\6\66\1\uffff\1\66\1\uffff\10\66\22"+
            "\uffff\3\66\14\uffff\72\66\4\uffff\35\66\45\uffff\2\66\1\uffff"+
            "\1\66\2\uffff\2\66\1\uffff\1\66\2\uffff\1\66\6\uffff\4\66\1"+
            "\uffff\7\66\1\uffff\3\66\1\uffff\1\66\1\uffff\1\66\2\uffff\2"+
            "\66\1\uffff\15\66\1\uffff\3\66\2\uffff\5\66\1\uffff\1\66\1\uffff"+
            "\6\66\2\uffff\12\66\2\uffff\2\66\42\uffff\72\66\4\uffff\12\66"+
            "\1\uffff\42\66\6\uffff\33\66\4\uffff\10\66\1\uffff\44\66\1\uffff"+
            "\17\66\2\uffff\1\66\60\uffff\42\66\1\uffff\5\66\1\uffff\2\66"+
            "\1\uffff\7\66\3\uffff\4\66\6\uffff\32\66\106\uffff\46\66\12"+
            "\uffff\51\66\2\uffff\1\66\4\uffff\132\66\5\uffff\104\66\5\uffff"+
            "\122\66\6\uffff\7\66\1\uffff\77\66\1\uffff\1\66\1\uffff\4\66"+
            "\2\uffff\7\66\1\uffff\1\66\1\uffff\4\66\2\uffff\47\66\1\uffff"+
            "\1\66\1\uffff\4\66\2\uffff\37\66\1\uffff\1\66\1\uffff\4\66\2"+
            "\uffff\7\66\1\uffff\1\66\1\uffff\4\66\2\uffff\7\66\1\uffff\7"+
            "\66\1\uffff\27\66\1\uffff\37\66\1\uffff\1\66\1\uffff\4\66\2"+
            "\uffff\7\66\1\uffff\47\66\1\uffff\23\66\6\uffff\34\66\43\uffff"+
            "\125\66\14\uffff\u0276\66\12\uffff\32\66\5\uffff\121\66\17\uffff"+
            "\15\66\1\uffff\7\66\13\uffff\27\66\11\uffff\24\66\14\uffff\15"+
            "\66\1\uffff\3\66\1\uffff\2\66\14\uffff\64\66\2\uffff\50\66\2"+
            "\uffff\12\66\6\uffff\12\66\6\uffff\16\66\2\uffff\12\66\6\uffff"+
            "\130\66\10\uffff\52\66\126\uffff\35\66\3\uffff\14\66\4\uffff"+
            "\14\66\4\uffff\1\66\3\uffff\52\66\2\uffff\5\66\153\uffff\40"+
            "\66\u0300\uffff\154\66\u0094\uffff\u009c\66\4\uffff\132\66\6"+
            "\uffff\26\66\2\uffff\6\66\2\uffff\46\66\2\uffff\6\66\2\uffff"+
            "\10\66\1\uffff\1\66\1\uffff\1\66\1\uffff\1\66\1\uffff\37\66"+
            "\2\uffff\65\66\1\uffff\7\66\1\uffff\1\66\3\uffff\3\66\1\uffff"+
            "\7\66\3\uffff\4\66\2\uffff\6\66\4\uffff\15\66\5\uffff\3\66\1"+
            "\uffff\7\66\23\uffff\10\66\10\uffff\10\66\10\uffff\11\66\2\uffff"+
            "\12\66\2\uffff\16\66\2\uffff\1\66\30\uffff\2\66\2\uffff\11\66"+
            "\2\uffff\16\66\23\uffff\22\66\36\uffff\33\66\25\uffff\74\66"+
            "\1\uffff\17\66\7\uffff\61\66\14\uffff\u0199\66\2\uffff\u0089"+
            "\66\2\uffff\33\66\57\uffff\47\66\31\uffff\13\66\25\uffff\u01b8"+
            "\66\1\uffff\145\66\2\uffff\22\66\16\uffff\2\66\137\uffff\4\66"+
            "\1\uffff\4\66\2\uffff\34\66\1\uffff\43\66\1\uffff\1\66\1\uffff"+
            "\4\66\3\uffff\1\66\1\uffff\7\66\2\uffff\7\66\16\uffff\37\66"+
            "\3\uffff\30\66\1\uffff\16\66\21\uffff\26\66\12\uffff\u0193\66"+
            "\26\uffff\77\66\4\uffff\40\66\2\uffff\u0110\66\u0372\uffff\32"+
            "\66\1\uffff\131\66\14\uffff\u00d6\66\32\uffff\14\66\5\uffff"+
            "\7\66\12\uffff\2\66\10\uffff\1\66\3\uffff\40\66\1\uffff\126"+
            "\66\2\uffff\2\66\2\uffff\143\66\5\uffff\50\66\4\uffff\136\66"+
            "\1\uffff\50\66\70\uffff\57\66\1\uffff\44\66\14\uffff\56\66\1"+
            "\uffff\u0080\66\1\uffff\u1ab6\66\12\uffff\u51e6\66\132\uffff"+
            "\u048d\66\3\uffff\67\66\u0739\uffff\u2ba4\66\u215c\uffff\u012e"+
            "\66\2\uffff\73\66\u0095\uffff\7\66\14\uffff\5\66\5\uffff\32"+
            "\66\1\uffff\5\66\1\uffff\1\66\1\uffff\2\66\1\uffff\2\66\1\uffff"+
            "\154\66\41\uffff\u016b\66\22\uffff\100\66\2\uffff\66\66\50\uffff"+
            "\16\66\2\uffff\20\66\20\uffff\4\66\14\uffff\5\66\20\uffff\2"+
            "\66\2\uffff\12\66\1\uffff\5\66\6\uffff\10\66\1\uffff\4\66\4"+
            "\uffff\5\66\1\uffff\u0087\66\4\uffff\7\66\2\uffff\61\66\1\uffff"+
            "\1\66\2\uffff\1\66\1\uffff\32\66\1\uffff\1\66\1\uffff\1\66\2"+
            "\uffff\1\66\2\uffff\133\66\3\uffff\6\66\2\uffff\6\66\2\uffff"+
            "\6\66\2\uffff\3\66\3\uffff\3\66\1\uffff\3\66\1\uffff\7\66",
            "\2\66\1\63\1\62\3\66\2\uffff\1\67\1\66\1\uffff\1\66\1\uffff"+
            "\1\66\12\161\1\uffff\1\66\1\uffff\1\66\1\uffff\1\70\1\66\6\160"+
            "\24\57\1\uffff\1\64\2\uffff\1\61\1\uffff\6\157\24\56\1\uffff"+
            "\1\66\44\uffff\7\66\1\uffff\2\66\1\uffff\1\66\1\uffff\1\66\1"+
            "\uffff\4\66\1\uffff\3\66\1\uffff\2\66\1\uffff\u017b\66\31\uffff"+
            "\162\66\4\uffff\14\66\16\uffff\5\66\11\uffff\1\66\21\uffff\130"+
            "\66\5\uffff\23\66\12\uffff\1\66\3\uffff\1\66\7\uffff\5\66\1"+
            "\uffff\1\66\1\uffff\24\66\1\uffff\54\66\1\uffff\54\66\4\uffff"+
            "\u0087\66\1\uffff\107\66\1\uffff\46\66\2\uffff\2\66\6\uffff"+
            "\20\66\41\uffff\46\66\2\uffff\7\66\1\uffff\47\66\1\uffff\2\66"+
            "\6\uffff\21\66\1\uffff\27\66\1\uffff\12\66\13\uffff\33\66\5"+
            "\uffff\5\66\27\uffff\12\66\5\uffff\1\66\3\uffff\1\66\1\uffff"+
            "\32\66\5\uffff\31\66\7\uffff\175\66\1\uffff\60\66\2\uffff\73"+
            "\66\2\uffff\3\66\60\uffff\62\66\u014f\uffff\71\66\2\uffff\22"+
            "\66\2\uffff\5\66\3\uffff\31\66\20\uffff\3\66\1\uffff\10\66\2"+
            "\uffff\2\66\2\uffff\26\66\1\uffff\7\66\1\uffff\1\66\3\uffff"+
            "\4\66\2\uffff\11\66\2\uffff\2\66\2\uffff\3\66\11\uffff\1\66"+
            "\4\uffff\2\66\1\uffff\5\66\2\uffff\25\66\6\uffff\3\66\1\uffff"+
            "\6\66\4\uffff\2\66\2\uffff\26\66\1\uffff\7\66\1\uffff\2\66\1"+
            "\uffff\2\66\1\uffff\2\66\2\uffff\1\66\1\uffff\5\66\4\uffff\2"+
            "\66\2\uffff\3\66\13\uffff\4\66\1\uffff\1\66\7\uffff\17\66\14"+
            "\uffff\3\66\1\uffff\11\66\1\uffff\3\66\1\uffff\26\66\1\uffff"+
            "\7\66\1\uffff\2\66\1\uffff\5\66\2\uffff\12\66\1\uffff\3\66\1"+
            "\uffff\3\66\2\uffff\1\66\17\uffff\4\66\2\uffff\12\66\1\uffff"+
            "\1\66\17\uffff\3\66\1\uffff\10\66\2\uffff\2\66\2\uffff\26\66"+
            "\1\uffff\7\66\1\uffff\2\66\1\uffff\5\66\2\uffff\10\66\3\uffff"+
            "\2\66\2\uffff\3\66\10\uffff\2\66\4\uffff\2\66\1\uffff\3\66\4"+
            "\uffff\14\66\20\uffff\2\66\1\uffff\6\66\3\uffff\3\66\1\uffff"+
            "\4\66\3\uffff\2\66\1\uffff\1\66\1\uffff\2\66\3\uffff\2\66\3"+
            "\uffff\3\66\3\uffff\10\66\1\uffff\3\66\4\uffff\5\66\3\uffff"+
            "\3\66\1\uffff\4\66\11\uffff\1\66\17\uffff\24\66\6\uffff\3\66"+
            "\1\uffff\10\66\1\uffff\3\66\1\uffff\27\66\1\uffff\12\66\1\uffff"+
            "\5\66\4\uffff\7\66\1\uffff\3\66\1\uffff\4\66\7\uffff\2\66\11"+
            "\uffff\2\66\4\uffff\12\66\22\uffff\2\66\1\uffff\10\66\1\uffff"+
            "\3\66\1\uffff\27\66\1\uffff\12\66\1\uffff\5\66\2\uffff\11\66"+
            "\1\uffff\3\66\1\uffff\4\66\7\uffff\2\66\7\uffff\1\66\1\uffff"+
            "\2\66\4\uffff\12\66\22\uffff\2\66\1\uffff\10\66\1\uffff\3\66"+
            "\1\uffff\27\66\1\uffff\20\66\4\uffff\6\66\2\uffff\3\66\1\uffff"+
            "\4\66\11\uffff\1\66\10\uffff\2\66\4\uffff\12\66\22\uffff\2\66"+
            "\1\uffff\22\66\3\uffff\30\66\1\uffff\11\66\1\uffff\1\66\2\uffff"+
            "\7\66\3\uffff\1\66\4\uffff\6\66\1\uffff\1\66\1\uffff\10\66\22"+
            "\uffff\3\66\14\uffff\72\66\4\uffff\35\66\45\uffff\2\66\1\uffff"+
            "\1\66\2\uffff\2\66\1\uffff\1\66\2\uffff\1\66\6\uffff\4\66\1"+
            "\uffff\7\66\1\uffff\3\66\1\uffff\1\66\1\uffff\1\66\2\uffff\2"+
            "\66\1\uffff\15\66\1\uffff\3\66\2\uffff\5\66\1\uffff\1\66\1\uffff"+
            "\6\66\2\uffff\12\66\2\uffff\2\66\42\uffff\72\66\4\uffff\12\66"+
            "\1\uffff\42\66\6\uffff\33\66\4\uffff\10\66\1\uffff\44\66\1\uffff"+
            "\17\66\2\uffff\1\66\60\uffff\42\66\1\uffff\5\66\1\uffff\2\66"+
            "\1\uffff\7\66\3\uffff\4\66\6\uffff\32\66\106\uffff\46\66\12"+
            "\uffff\51\66\2\uffff\1\66\4\uffff\132\66\5\uffff\104\66\5\uffff"+
            "\122\66\6\uffff\7\66\1\uffff\77\66\1\uffff\1\66\1\uffff\4\66"+
            "\2\uffff\7\66\1\uffff\1\66\1\uffff\4\66\2\uffff\47\66\1\uffff"+
            "\1\66\1\uffff\4\66\2\uffff\37\66\1\uffff\1\66\1\uffff\4\66\2"+
            "\uffff\7\66\1\uffff\1\66\1\uffff\4\66\2\uffff\7\66\1\uffff\7"+
            "\66\1\uffff\27\66\1\uffff\37\66\1\uffff\1\66\1\uffff\4\66\2"+
            "\uffff\7\66\1\uffff\47\66\1\uffff\23\66\6\uffff\34\66\43\uffff"+
            "\125\66\14\uffff\u0276\66\12\uffff\32\66\5\uffff\121\66\17\uffff"+
            "\15\66\1\uffff\7\66\13\uffff\27\66\11\uffff\24\66\14\uffff\15"+
            "\66\1\uffff\3\66\1\uffff\2\66\14\uffff\64\66\2\uffff\50\66\2"+
            "\uffff\12\66\6\uffff\12\66\6\uffff\16\66\2\uffff\12\66\6\uffff"+
            "\130\66\10\uffff\52\66\126\uffff\35\66\3\uffff\14\66\4\uffff"+
            "\14\66\4\uffff\1\66\3\uffff\52\66\2\uffff\5\66\153\uffff\40"+
            "\66\u0300\uffff\154\66\u0094\uffff\u009c\66\4\uffff\132\66\6"+
            "\uffff\26\66\2\uffff\6\66\2\uffff\46\66\2\uffff\6\66\2\uffff"+
            "\10\66\1\uffff\1\66\1\uffff\1\66\1\uffff\1\66\1\uffff\37\66"+
            "\2\uffff\65\66\1\uffff\7\66\1\uffff\1\66\3\uffff\3\66\1\uffff"+
            "\7\66\3\uffff\4\66\2\uffff\6\66\4\uffff\15\66\5\uffff\3\66\1"+
            "\uffff\7\66\23\uffff\10\66\10\uffff\10\66\10\uffff\11\66\2\uffff"+
            "\12\66\2\uffff\16\66\2\uffff\1\66\30\uffff\2\66\2\uffff\11\66"+
            "\2\uffff\16\66\23\uffff\22\66\36\uffff\33\66\25\uffff\74\66"+
            "\1\uffff\17\66\7\uffff\61\66\14\uffff\u0199\66\2\uffff\u0089"+
            "\66\2\uffff\33\66\57\uffff\47\66\31\uffff\13\66\25\uffff\u01b8"+
            "\66\1\uffff\145\66\2\uffff\22\66\16\uffff\2\66\137\uffff\4\66"+
            "\1\uffff\4\66\2\uffff\34\66\1\uffff\43\66\1\uffff\1\66\1\uffff"+
            "\4\66\3\uffff\1\66\1\uffff\7\66\2\uffff\7\66\16\uffff\37\66"+
            "\3\uffff\30\66\1\uffff\16\66\21\uffff\26\66\12\uffff\u0193\66"+
            "\26\uffff\77\66\4\uffff\40\66\2\uffff\u0110\66\u0372\uffff\32"+
            "\66\1\uffff\131\66\14\uffff\u00d6\66\32\uffff\14\66\5\uffff"+
            "\7\66\12\uffff\2\66\10\uffff\1\66\3\uffff\40\66\1\uffff\126"+
            "\66\2\uffff\2\66\2\uffff\143\66\5\uffff\50\66\4\uffff\136\66"+
            "\1\uffff\50\66\70\uffff\57\66\1\uffff\44\66\14\uffff\56\66\1"+
            "\uffff\u0080\66\1\uffff\u1ab6\66\12\uffff\u51e6\66\132\uffff"+
            "\u048d\66\3\uffff\67\66\u0739\uffff\u2ba4\66\u215c\uffff\u012e"+
            "\66\2\uffff\73\66\u0095\uffff\7\66\14\uffff\5\66\5\uffff\32"+
            "\66\1\uffff\5\66\1\uffff\1\66\1\uffff\2\66\1\uffff\2\66\1\uffff"+
            "\154\66\41\uffff\u016b\66\22\uffff\100\66\2\uffff\66\66\50\uffff"+
            "\16\66\2\uffff\20\66\20\uffff\4\66\14\uffff\5\66\20\uffff\2"+
            "\66\2\uffff\12\66\1\uffff\5\66\6\uffff\10\66\1\uffff\4\66\4"+
            "\uffff\5\66\1\uffff\u0087\66\4\uffff\7\66\2\uffff\61\66\1\uffff"+
            "\1\66\2\uffff\1\66\1\uffff\32\66\1\uffff\1\66\1\uffff\1\66\2"+
            "\uffff\1\66\2\uffff\133\66\3\uffff\6\66\2\uffff\6\66\2\uffff"+
            "\6\66\2\uffff\3\66\3\uffff\3\66\1\uffff\3\66\1\uffff\7\66",
            "\2\66\1\63\1\62\3\66\2\uffff\1\67\1\66\1\uffff\1\66\1\uffff"+
            "\1\66\12\161\1\uffff\1\66\1\uffff\1\66\1\uffff\1\70\1\66\6\160"+
            "\24\57\1\uffff\1\64\2\uffff\1\61\1\uffff\6\157\24\56\1\uffff"+
            "\1\66\44\uffff\7\66\1\uffff\2\66\1\uffff\1\66\1\uffff\1\66\1"+
            "\uffff\4\66\1\uffff\3\66\1\uffff\2\66\1\uffff\u017b\66\31\uffff"+
            "\162\66\4\uffff\14\66\16\uffff\5\66\11\uffff\1\66\21\uffff\130"+
            "\66\5\uffff\23\66\12\uffff\1\66\3\uffff\1\66\7\uffff\5\66\1"+
            "\uffff\1\66\1\uffff\24\66\1\uffff\54\66\1\uffff\54\66\4\uffff"+
            "\u0087\66\1\uffff\107\66\1\uffff\46\66\2\uffff\2\66\6\uffff"+
            "\20\66\41\uffff\46\66\2\uffff\7\66\1\uffff\47\66\1\uffff\2\66"+
            "\6\uffff\21\66\1\uffff\27\66\1\uffff\12\66\13\uffff\33\66\5"+
            "\uffff\5\66\27\uffff\12\66\5\uffff\1\66\3\uffff\1\66\1\uffff"+
            "\32\66\5\uffff\31\66\7\uffff\175\66\1\uffff\60\66\2\uffff\73"+
            "\66\2\uffff\3\66\60\uffff\62\66\u014f\uffff\71\66\2\uffff\22"+
            "\66\2\uffff\5\66\3\uffff\31\66\20\uffff\3\66\1\uffff\10\66\2"+
            "\uffff\2\66\2\uffff\26\66\1\uffff\7\66\1\uffff\1\66\3\uffff"+
            "\4\66\2\uffff\11\66\2\uffff\2\66\2\uffff\3\66\11\uffff\1\66"+
            "\4\uffff\2\66\1\uffff\5\66\2\uffff\25\66\6\uffff\3\66\1\uffff"+
            "\6\66\4\uffff\2\66\2\uffff\26\66\1\uffff\7\66\1\uffff\2\66\1"+
            "\uffff\2\66\1\uffff\2\66\2\uffff\1\66\1\uffff\5\66\4\uffff\2"+
            "\66\2\uffff\3\66\13\uffff\4\66\1\uffff\1\66\7\uffff\17\66\14"+
            "\uffff\3\66\1\uffff\11\66\1\uffff\3\66\1\uffff\26\66\1\uffff"+
            "\7\66\1\uffff\2\66\1\uffff\5\66\2\uffff\12\66\1\uffff\3\66\1"+
            "\uffff\3\66\2\uffff\1\66\17\uffff\4\66\2\uffff\12\66\1\uffff"+
            "\1\66\17\uffff\3\66\1\uffff\10\66\2\uffff\2\66\2\uffff\26\66"+
            "\1\uffff\7\66\1\uffff\2\66\1\uffff\5\66\2\uffff\10\66\3\uffff"+
            "\2\66\2\uffff\3\66\10\uffff\2\66\4\uffff\2\66\1\uffff\3\66\4"+
            "\uffff\14\66\20\uffff\2\66\1\uffff\6\66\3\uffff\3\66\1\uffff"+
            "\4\66\3\uffff\2\66\1\uffff\1\66\1\uffff\2\66\3\uffff\2\66\3"+
            "\uffff\3\66\3\uffff\10\66\1\uffff\3\66\4\uffff\5\66\3\uffff"+
            "\3\66\1\uffff\4\66\11\uffff\1\66\17\uffff\24\66\6\uffff\3\66"+
            "\1\uffff\10\66\1\uffff\3\66\1\uffff\27\66\1\uffff\12\66\1\uffff"+
            "\5\66\4\uffff\7\66\1\uffff\3\66\1\uffff\4\66\7\uffff\2\66\11"+
            "\uffff\2\66\4\uffff\12\66\22\uffff\2\66\1\uffff\10\66\1\uffff"+
            "\3\66\1\uffff\27\66\1\uffff\12\66\1\uffff\5\66\2\uffff\11\66"+
            "\1\uffff\3\66\1\uffff\4\66\7\uffff\2\66\7\uffff\1\66\1\uffff"+
            "\2\66\4\uffff\12\66\22\uffff\2\66\1\uffff\10\66\1\uffff\3\66"+
            "\1\uffff\27\66\1\uffff\20\66\4\uffff\6\66\2\uffff\3\66\1\uffff"+
            "\4\66\11\uffff\1\66\10\uffff\2\66\4\uffff\12\66\22\uffff\2\66"+
            "\1\uffff\22\66\3\uffff\30\66\1\uffff\11\66\1\uffff\1\66\2\uffff"+
            "\7\66\3\uffff\1\66\4\uffff\6\66\1\uffff\1\66\1\uffff\10\66\22"+
            "\uffff\3\66\14\uffff\72\66\4\uffff\35\66\45\uffff\2\66\1\uffff"+
            "\1\66\2\uffff\2\66\1\uffff\1\66\2\uffff\1\66\6\uffff\4\66\1"+
            "\uffff\7\66\1\uffff\3\66\1\uffff\1\66\1\uffff\1\66\2\uffff\2"+
            "\66\1\uffff\15\66\1\uffff\3\66\2\uffff\5\66\1\uffff\1\66\1\uffff"+
            "\6\66\2\uffff\12\66\2\uffff\2\66\42\uffff\72\66\4\uffff\12\66"+
            "\1\uffff\42\66\6\uffff\33\66\4\uffff\10\66\1\uffff\44\66\1\uffff"+
            "\17\66\2\uffff\1\66\60\uffff\42\66\1\uffff\5\66\1\uffff\2\66"+
            "\1\uffff\7\66\3\uffff\4\66\6\uffff\32\66\106\uffff\46\66\12"+
            "\uffff\51\66\2\uffff\1\66\4\uffff\132\66\5\uffff\104\66\5\uffff"+
            "\122\66\6\uffff\7\66\1\uffff\77\66\1\uffff\1\66\1\uffff\4\66"+
            "\2\uffff\7\66\1\uffff\1\66\1\uffff\4\66\2\uffff\47\66\1\uffff"+
            "\1\66\1\uffff\4\66\2\uffff\37\66\1\uffff\1\66\1\uffff\4\66\2"+
            "\uffff\7\66\1\uffff\1\66\1\uffff\4\66\2\uffff\7\66\1\uffff\7"+
            "\66\1\uffff\27\66\1\uffff\37\66\1\uffff\1\66\1\uffff\4\66\2"+
            "\uffff\7\66\1\uffff\47\66\1\uffff\23\66\6\uffff\34\66\43\uffff"+
            "\125\66\14\uffff\u0276\66\12\uffff\32\66\5\uffff\121\66\17\uffff"+
            "\15\66\1\uffff\7\66\13\uffff\27\66\11\uffff\24\66\14\uffff\15"+
            "\66\1\uffff\3\66\1\uffff\2\66\14\uffff\64\66\2\uffff\50\66\2"+
            "\uffff\12\66\6\uffff\12\66\6\uffff\16\66\2\uffff\12\66\6\uffff"+
            "\130\66\10\uffff\52\66\126\uffff\35\66\3\uffff\14\66\4\uffff"+
            "\14\66\4\uffff\1\66\3\uffff\52\66\2\uffff\5\66\153\uffff\40"+
            "\66\u0300\uffff\154\66\u0094\uffff\u009c\66\4\uffff\132\66\6"+
            "\uffff\26\66\2\uffff\6\66\2\uffff\46\66\2\uffff\6\66\2\uffff"+
            "\10\66\1\uffff\1\66\1\uffff\1\66\1\uffff\1\66\1\uffff\37\66"+
            "\2\uffff\65\66\1\uffff\7\66\1\uffff\1\66\3\uffff\3\66\1\uffff"+
            "\7\66\3\uffff\4\66\2\uffff\6\66\4\uffff\15\66\5\uffff\3\66\1"+
            "\uffff\7\66\23\uffff\10\66\10\uffff\10\66\10\uffff\11\66\2\uffff"+
            "\12\66\2\uffff\16\66\2\uffff\1\66\30\uffff\2\66\2\uffff\11\66"+
            "\2\uffff\16\66\23\uffff\22\66\36\uffff\33\66\25\uffff\74\66"+
            "\1\uffff\17\66\7\uffff\61\66\14\uffff\u0199\66\2\uffff\u0089"+
            "\66\2\uffff\33\66\57\uffff\47\66\31\uffff\13\66\25\uffff\u01b8"+
            "\66\1\uffff\145\66\2\uffff\22\66\16\uffff\2\66\137\uffff\4\66"+
            "\1\uffff\4\66\2\uffff\34\66\1\uffff\43\66\1\uffff\1\66\1\uffff"+
            "\4\66\3\uffff\1\66\1\uffff\7\66\2\uffff\7\66\16\uffff\37\66"+
            "\3\uffff\30\66\1\uffff\16\66\21\uffff\26\66\12\uffff\u0193\66"+
            "\26\uffff\77\66\4\uffff\40\66\2\uffff\u0110\66\u0372\uffff\32"+
            "\66\1\uffff\131\66\14\uffff\u00d6\66\32\uffff\14\66\5\uffff"+
            "\7\66\12\uffff\2\66\10\uffff\1\66\3\uffff\40\66\1\uffff\126"+
            "\66\2\uffff\2\66\2\uffff\143\66\5\uffff\50\66\4\uffff\136\66"+
            "\1\uffff\50\66\70\uffff\57\66\1\uffff\44\66\14\uffff\56\66\1"+
            "\uffff\u0080\66\1\uffff\u1ab6\66\12\uffff\u51e6\66\132\uffff"+
            "\u048d\66\3\uffff\67\66\u0739\uffff\u2ba4\66\u215c\uffff\u012e"+
            "\66\2\uffff\73\66\u0095\uffff\7\66\14\uffff\5\66\5\uffff\32"+
            "\66\1\uffff\5\66\1\uffff\1\66\1\uffff\2\66\1\uffff\2\66\1\uffff"+
            "\154\66\41\uffff\u016b\66\22\uffff\100\66\2\uffff\66\66\50\uffff"+
            "\16\66\2\uffff\20\66\20\uffff\4\66\14\uffff\5\66\20\uffff\2"+
            "\66\2\uffff\12\66\1\uffff\5\66\6\uffff\10\66\1\uffff\4\66\4"+
            "\uffff\5\66\1\uffff\u0087\66\4\uffff\7\66\2\uffff\61\66\1\uffff"+
            "\1\66\2\uffff\1\66\1\uffff\32\66\1\uffff\1\66\1\uffff\1\66\2"+
            "\uffff\1\66\2\uffff\133\66\3\uffff\6\66\2\uffff\6\66\2\uffff"+
            "\6\66\2\uffff\3\66\3\uffff\3\66\1\uffff\3\66\1\uffff\7\66",
            "\7\66\2\uffff\1\67\1\66\1\uffff\1\66\1\uffff\1\66\12\162\1"+
            "\uffff\1\66\1\uffff\1\66\1\uffff\1\70\1\66\6\162\24\66\1\uffff"+
            "\1\110\2\uffff\1\66\1\uffff\6\162\24\66\1\uffff\1\66\44\uffff"+
            "\7\66\1\uffff\2\66\1\uffff\1\66\1\uffff\1\66\1\uffff\4\66\1"+
            "\uffff\3\66\1\uffff\2\66\1\uffff\u017b\66\31\uffff\162\66\4"+
            "\uffff\14\66\16\uffff\5\66\11\uffff\1\66\21\uffff\130\66\5\uffff"+
            "\23\66\12\uffff\1\66\3\uffff\1\66\7\uffff\5\66\1\uffff\1\66"+
            "\1\uffff\24\66\1\uffff\54\66\1\uffff\54\66\4\uffff\u0087\66"+
            "\1\uffff\107\66\1\uffff\46\66\2\uffff\2\66\6\uffff\20\66\41"+
            "\uffff\46\66\2\uffff\7\66\1\uffff\47\66\1\uffff\2\66\6\uffff"+
            "\21\66\1\uffff\27\66\1\uffff\12\66\13\uffff\33\66\5\uffff\5"+
            "\66\27\uffff\12\66\5\uffff\1\66\3\uffff\1\66\1\uffff\32\66\5"+
            "\uffff\31\66\7\uffff\175\66\1\uffff\60\66\2\uffff\73\66\2\uffff"+
            "\3\66\60\uffff\62\66\u014f\uffff\71\66\2\uffff\22\66\2\uffff"+
            "\5\66\3\uffff\31\66\20\uffff\3\66\1\uffff\10\66\2\uffff\2\66"+
            "\2\uffff\26\66\1\uffff\7\66\1\uffff\1\66\3\uffff\4\66\2\uffff"+
            "\11\66\2\uffff\2\66\2\uffff\3\66\11\uffff\1\66\4\uffff\2\66"+
            "\1\uffff\5\66\2\uffff\25\66\6\uffff\3\66\1\uffff\6\66\4\uffff"+
            "\2\66\2\uffff\26\66\1\uffff\7\66\1\uffff\2\66\1\uffff\2\66\1"+
            "\uffff\2\66\2\uffff\1\66\1\uffff\5\66\4\uffff\2\66\2\uffff\3"+
            "\66\13\uffff\4\66\1\uffff\1\66\7\uffff\17\66\14\uffff\3\66\1"+
            "\uffff\11\66\1\uffff\3\66\1\uffff\26\66\1\uffff\7\66\1\uffff"+
            "\2\66\1\uffff\5\66\2\uffff\12\66\1\uffff\3\66\1\uffff\3\66\2"+
            "\uffff\1\66\17\uffff\4\66\2\uffff\12\66\1\uffff\1\66\17\uffff"+
            "\3\66\1\uffff\10\66\2\uffff\2\66\2\uffff\26\66\1\uffff\7\66"+
            "\1\uffff\2\66\1\uffff\5\66\2\uffff\10\66\3\uffff\2\66\2\uffff"+
            "\3\66\10\uffff\2\66\4\uffff\2\66\1\uffff\3\66\4\uffff\14\66"+
            "\20\uffff\2\66\1\uffff\6\66\3\uffff\3\66\1\uffff\4\66\3\uffff"+
            "\2\66\1\uffff\1\66\1\uffff\2\66\3\uffff\2\66\3\uffff\3\66\3"+
            "\uffff\10\66\1\uffff\3\66\4\uffff\5\66\3\uffff\3\66\1\uffff"+
            "\4\66\11\uffff\1\66\17\uffff\24\66\6\uffff\3\66\1\uffff\10\66"+
            "\1\uffff\3\66\1\uffff\27\66\1\uffff\12\66\1\uffff\5\66\4\uffff"+
            "\7\66\1\uffff\3\66\1\uffff\4\66\7\uffff\2\66\11\uffff\2\66\4"+
            "\uffff\12\66\22\uffff\2\66\1\uffff\10\66\1\uffff\3\66\1\uffff"+
            "\27\66\1\uffff\12\66\1\uffff\5\66\2\uffff\11\66\1\uffff\3\66"+
            "\1\uffff\4\66\7\uffff\2\66\7\uffff\1\66\1\uffff\2\66\4\uffff"+
            "\12\66\22\uffff\2\66\1\uffff\10\66\1\uffff\3\66\1\uffff\27\66"+
            "\1\uffff\20\66\4\uffff\6\66\2\uffff\3\66\1\uffff\4\66\11\uffff"+
            "\1\66\10\uffff\2\66\4\uffff\12\66\22\uffff\2\66\1\uffff\22\66"+
            "\3\uffff\30\66\1\uffff\11\66\1\uffff\1\66\2\uffff\7\66\3\uffff"+
            "\1\66\4\uffff\6\66\1\uffff\1\66\1\uffff\10\66\22\uffff\3\66"+
            "\14\uffff\72\66\4\uffff\35\66\45\uffff\2\66\1\uffff\1\66\2\uffff"+
            "\2\66\1\uffff\1\66\2\uffff\1\66\6\uffff\4\66\1\uffff\7\66\1"+
            "\uffff\3\66\1\uffff\1\66\1\uffff\1\66\2\uffff\2\66\1\uffff\15"+
            "\66\1\uffff\3\66\2\uffff\5\66\1\uffff\1\66\1\uffff\6\66\2\uffff"+
            "\12\66\2\uffff\2\66\42\uffff\72\66\4\uffff\12\66\1\uffff\42"+
            "\66\6\uffff\33\66\4\uffff\10\66\1\uffff\44\66\1\uffff\17\66"+
            "\2\uffff\1\66\60\uffff\42\66\1\uffff\5\66\1\uffff\2\66\1\uffff"+
            "\7\66\3\uffff\4\66\6\uffff\32\66\106\uffff\46\66\12\uffff\51"+
            "\66\2\uffff\1\66\4\uffff\132\66\5\uffff\104\66\5\uffff\122\66"+
            "\6\uffff\7\66\1\uffff\77\66\1\uffff\1\66\1\uffff\4\66\2\uffff"+
            "\7\66\1\uffff\1\66\1\uffff\4\66\2\uffff\47\66\1\uffff\1\66\1"+
            "\uffff\4\66\2\uffff\37\66\1\uffff\1\66\1\uffff\4\66\2\uffff"+
            "\7\66\1\uffff\1\66\1\uffff\4\66\2\uffff\7\66\1\uffff\7\66\1"+
            "\uffff\27\66\1\uffff\37\66\1\uffff\1\66\1\uffff\4\66\2\uffff"+
            "\7\66\1\uffff\47\66\1\uffff\23\66\6\uffff\34\66\43\uffff\125"+
            "\66\14\uffff\u0276\66\12\uffff\32\66\5\uffff\121\66\17\uffff"+
            "\15\66\1\uffff\7\66\13\uffff\27\66\11\uffff\24\66\14\uffff\15"+
            "\66\1\uffff\3\66\1\uffff\2\66\14\uffff\64\66\2\uffff\50\66\2"+
            "\uffff\12\66\6\uffff\12\66\6\uffff\16\66\2\uffff\12\66\6\uffff"+
            "\130\66\10\uffff\52\66\126\uffff\35\66\3\uffff\14\66\4\uffff"+
            "\14\66\4\uffff\1\66\3\uffff\52\66\2\uffff\5\66\153\uffff\40"+
            "\66\u0300\uffff\154\66\u0094\uffff\u009c\66\4\uffff\132\66\6"+
            "\uffff\26\66\2\uffff\6\66\2\uffff\46\66\2\uffff\6\66\2\uffff"+
            "\10\66\1\uffff\1\66\1\uffff\1\66\1\uffff\1\66\1\uffff\37\66"+
            "\2\uffff\65\66\1\uffff\7\66\1\uffff\1\66\3\uffff\3\66\1\uffff"+
            "\7\66\3\uffff\4\66\2\uffff\6\66\4\uffff\15\66\5\uffff\3\66\1"+
            "\uffff\7\66\23\uffff\10\66\10\uffff\10\66\10\uffff\11\66\2\uffff"+
            "\12\66\2\uffff\16\66\2\uffff\1\66\30\uffff\2\66\2\uffff\11\66"+
            "\2\uffff\16\66\23\uffff\22\66\36\uffff\33\66\25\uffff\74\66"+
            "\1\uffff\17\66\7\uffff\61\66\14\uffff\u0199\66\2\uffff\u0089"+
            "\66\2\uffff\33\66\57\uffff\47\66\31\uffff\13\66\25\uffff\u01b8"+
            "\66\1\uffff\145\66\2\uffff\22\66\16\uffff\2\66\137\uffff\4\66"+
            "\1\uffff\4\66\2\uffff\34\66\1\uffff\43\66\1\uffff\1\66\1\uffff"+
            "\4\66\3\uffff\1\66\1\uffff\7\66\2\uffff\7\66\16\uffff\37\66"+
            "\3\uffff\30\66\1\uffff\16\66\21\uffff\26\66\12\uffff\u0193\66"+
            "\26\uffff\77\66\4\uffff\40\66\2\uffff\u0110\66\u0372\uffff\32"+
            "\66\1\uffff\131\66\14\uffff\u00d6\66\32\uffff\14\66\5\uffff"+
            "\7\66\12\uffff\2\66\10\uffff\1\66\3\uffff\40\66\1\uffff\126"+
            "\66\2\uffff\2\66\2\uffff\143\66\5\uffff\50\66\4\uffff\136\66"+
            "\1\uffff\50\66\70\uffff\57\66\1\uffff\44\66\14\uffff\56\66\1"+
            "\uffff\u0080\66\1\uffff\u1ab6\66\12\uffff\u51e6\66\132\uffff"+
            "\u048d\66\3\uffff\67\66\u0739\uffff\u2ba4\66\u215c\uffff\u012e"+
            "\66\2\uffff\73\66\u0095\uffff\7\66\14\uffff\5\66\5\uffff\32"+
            "\66\1\uffff\5\66\1\uffff\1\66\1\uffff\2\66\1\uffff\2\66\1\uffff"+
            "\154\66\41\uffff\u016b\66\22\uffff\100\66\2\uffff\66\66\50\uffff"+
            "\16\66\2\uffff\20\66\20\uffff\4\66\14\uffff\5\66\20\uffff\2"+
            "\66\2\uffff\12\66\1\uffff\5\66\6\uffff\10\66\1\uffff\4\66\4"+
            "\uffff\5\66\1\uffff\u0087\66\4\uffff\7\66\2\uffff\61\66\1\uffff"+
            "\1\66\2\uffff\1\66\1\uffff\32\66\1\uffff\1\66\1\uffff\1\66\2"+
            "\uffff\1\66\2\uffff\133\66\3\uffff\6\66\2\uffff\6\66\2\uffff"+
            "\6\66\2\uffff\3\66\3\uffff\3\66\1\uffff\3\66\1\uffff\7\66",
            "\7\66\2\uffff\1\67\1\66\1\uffff\1\66\1\uffff\13\66\1\uffff"+
            "\1\66\1\uffff\1\66\1\uffff\1\70\33\66\1\uffff\1\110\2\uffff"+
            "\1\66\1\uffff\32\66\1\uffff\1\66\44\uffff\7\66\1\uffff\2\66"+
            "\1\uffff\1\66\1\uffff\1\66\1\uffff\4\66\1\uffff\3\66\1\uffff"+
            "\2\66\1\uffff\u017b\66\31\uffff\162\66\4\uffff\14\66\16\uffff"+
            "\5\66\11\uffff\1\66\21\uffff\130\66\5\uffff\23\66\12\uffff\1"+
            "\66\3\uffff\1\66\7\uffff\5\66\1\uffff\1\66\1\uffff\24\66\1\uffff"+
            "\54\66\1\uffff\54\66\4\uffff\u0087\66\1\uffff\107\66\1\uffff"+
            "\46\66\2\uffff\2\66\6\uffff\20\66\41\uffff\46\66\2\uffff\7\66"+
            "\1\uffff\47\66\1\uffff\2\66\6\uffff\21\66\1\uffff\27\66\1\uffff"+
            "\12\66\13\uffff\33\66\5\uffff\5\66\27\uffff\12\66\5\uffff\1"+
            "\66\3\uffff\1\66\1\uffff\32\66\5\uffff\31\66\7\uffff\175\66"+
            "\1\uffff\60\66\2\uffff\73\66\2\uffff\3\66\60\uffff\62\66\u014f"+
            "\uffff\71\66\2\uffff\22\66\2\uffff\5\66\3\uffff\31\66\20\uffff"+
            "\3\66\1\uffff\10\66\2\uffff\2\66\2\uffff\26\66\1\uffff\7\66"+
            "\1\uffff\1\66\3\uffff\4\66\2\uffff\11\66\2\uffff\2\66\2\uffff"+
            "\3\66\11\uffff\1\66\4\uffff\2\66\1\uffff\5\66\2\uffff\25\66"+
            "\6\uffff\3\66\1\uffff\6\66\4\uffff\2\66\2\uffff\26\66\1\uffff"+
            "\7\66\1\uffff\2\66\1\uffff\2\66\1\uffff\2\66\2\uffff\1\66\1"+
            "\uffff\5\66\4\uffff\2\66\2\uffff\3\66\13\uffff\4\66\1\uffff"+
            "\1\66\7\uffff\17\66\14\uffff\3\66\1\uffff\11\66\1\uffff\3\66"+
            "\1\uffff\26\66\1\uffff\7\66\1\uffff\2\66\1\uffff\5\66\2\uffff"+
            "\12\66\1\uffff\3\66\1\uffff\3\66\2\uffff\1\66\17\uffff\4\66"+
            "\2\uffff\12\66\1\uffff\1\66\17\uffff\3\66\1\uffff\10\66\2\uffff"+
            "\2\66\2\uffff\26\66\1\uffff\7\66\1\uffff\2\66\1\uffff\5\66\2"+
            "\uffff\10\66\3\uffff\2\66\2\uffff\3\66\10\uffff\2\66\4\uffff"+
            "\2\66\1\uffff\3\66\4\uffff\14\66\20\uffff\2\66\1\uffff\6\66"+
            "\3\uffff\3\66\1\uffff\4\66\3\uffff\2\66\1\uffff\1\66\1\uffff"+
            "\2\66\3\uffff\2\66\3\uffff\3\66\3\uffff\10\66\1\uffff\3\66\4"+
            "\uffff\5\66\3\uffff\3\66\1\uffff\4\66\11\uffff\1\66\17\uffff"+
            "\24\66\6\uffff\3\66\1\uffff\10\66\1\uffff\3\66\1\uffff\27\66"+
            "\1\uffff\12\66\1\uffff\5\66\4\uffff\7\66\1\uffff\3\66\1\uffff"+
            "\4\66\7\uffff\2\66\11\uffff\2\66\4\uffff\12\66\22\uffff\2\66"+
            "\1\uffff\10\66\1\uffff\3\66\1\uffff\27\66\1\uffff\12\66\1\uffff"+
            "\5\66\2\uffff\11\66\1\uffff\3\66\1\uffff\4\66\7\uffff\2\66\7"+
            "\uffff\1\66\1\uffff\2\66\4\uffff\12\66\22\uffff\2\66\1\uffff"+
            "\10\66\1\uffff\3\66\1\uffff\27\66\1\uffff\20\66\4\uffff\6\66"+
            "\2\uffff\3\66\1\uffff\4\66\11\uffff\1\66\10\uffff\2\66\4\uffff"+
            "\12\66\22\uffff\2\66\1\uffff\22\66\3\uffff\30\66\1\uffff\11"+
            "\66\1\uffff\1\66\2\uffff\7\66\3\uffff\1\66\4\uffff\6\66\1\uffff"+
            "\1\66\1\uffff\10\66\22\uffff\3\66\14\uffff\72\66\4\uffff\35"+
            "\66\45\uffff\2\66\1\uffff\1\66\2\uffff\2\66\1\uffff\1\66\2\uffff"+
            "\1\66\6\uffff\4\66\1\uffff\7\66\1\uffff\3\66\1\uffff\1\66\1"+
            "\uffff\1\66\2\uffff\2\66\1\uffff\15\66\1\uffff\3\66\2\uffff"+
            "\5\66\1\uffff\1\66\1\uffff\6\66\2\uffff\12\66\2\uffff\2\66\42"+
            "\uffff\72\66\4\uffff\12\66\1\uffff\42\66\6\uffff\33\66\4\uffff"+
            "\10\66\1\uffff\44\66\1\uffff\17\66\2\uffff\1\66\60\uffff\42"+
            "\66\1\uffff\5\66\1\uffff\2\66\1\uffff\7\66\3\uffff\4\66\6\uffff"+
            "\32\66\106\uffff\46\66\12\uffff\51\66\2\uffff\1\66\4\uffff\132"+
            "\66\5\uffff\104\66\5\uffff\122\66\6\uffff\7\66\1\uffff\77\66"+
            "\1\uffff\1\66\1\uffff\4\66\2\uffff\7\66\1\uffff\1\66\1\uffff"+
            "\4\66\2\uffff\47\66\1\uffff\1\66\1\uffff\4\66\2\uffff\37\66"+
            "\1\uffff\1\66\1\uffff\4\66\2\uffff\7\66\1\uffff\1\66\1\uffff"+
            "\4\66\2\uffff\7\66\1\uffff\7\66\1\uffff\27\66\1\uffff\37\66"+
            "\1\uffff\1\66\1\uffff\4\66\2\uffff\7\66\1\uffff\47\66\1\uffff"+
            "\23\66\6\uffff\34\66\43\uffff\125\66\14\uffff\u0276\66\12\uffff"+
            "\32\66\5\uffff\121\66\17\uffff\15\66\1\uffff\7\66\13\uffff\27"+
            "\66\11\uffff\24\66\14\uffff\15\66\1\uffff\3\66\1\uffff\2\66"+
            "\14\uffff\64\66\2\uffff\50\66\2\uffff\12\66\6\uffff\12\66\6"+
            "\uffff\16\66\2\uffff\12\66\6\uffff\130\66\10\uffff\52\66\126"+
            "\uffff\35\66\3\uffff\14\66\4\uffff\14\66\4\uffff\1\66\3\uffff"+
            "\52\66\2\uffff\5\66\153\uffff\40\66\u0300\uffff\154\66\u0094"+
            "\uffff\u009c\66\4\uffff\132\66\6\uffff\26\66\2\uffff\6\66\2"+
            "\uffff\46\66\2\uffff\6\66\2\uffff\10\66\1\uffff\1\66\1\uffff"+
            "\1\66\1\uffff\1\66\1\uffff\37\66\2\uffff\65\66\1\uffff\7\66"+
            "\1\uffff\1\66\3\uffff\3\66\1\uffff\7\66\3\uffff\4\66\2\uffff"+
            "\6\66\4\uffff\15\66\5\uffff\3\66\1\uffff\7\66\23\uffff\10\66"+
            "\10\uffff\10\66\10\uffff\11\66\2\uffff\12\66\2\uffff\16\66\2"+
            "\uffff\1\66\30\uffff\2\66\2\uffff\11\66\2\uffff\16\66\23\uffff"+
            "\22\66\36\uffff\33\66\25\uffff\74\66\1\uffff\17\66\7\uffff\61"+
            "\66\14\uffff\u0199\66\2\uffff\u0089\66\2\uffff\33\66\57\uffff"+
            "\47\66\31\uffff\13\66\25\uffff\u01b8\66\1\uffff\145\66\2\uffff"+
            "\22\66\16\uffff\2\66\137\uffff\4\66\1\uffff\4\66\2\uffff\34"+
            "\66\1\uffff\43\66\1\uffff\1\66\1\uffff\4\66\3\uffff\1\66\1\uffff"+
            "\7\66\2\uffff\7\66\16\uffff\37\66\3\uffff\30\66\1\uffff\16\66"+
            "\21\uffff\26\66\12\uffff\u0193\66\26\uffff\77\66\4\uffff\40"+
            "\66\2\uffff\u0110\66\u0372\uffff\32\66\1\uffff\131\66\14\uffff"+
            "\u00d6\66\32\uffff\14\66\5\uffff\7\66\12\uffff\2\66\10\uffff"+
            "\1\66\3\uffff\40\66\1\uffff\126\66\2\uffff\2\66\2\uffff\143"+
            "\66\5\uffff\50\66\4\uffff\136\66\1\uffff\50\66\70\uffff\57\66"+
            "\1\uffff\44\66\14\uffff\56\66\1\uffff\u0080\66\1\uffff\u1ab6"+
            "\66\12\uffff\u51e6\66\132\uffff\u048d\66\3\uffff\67\66\u0739"+
            "\uffff\u2ba4\66\u215c\uffff\u012e\66\2\uffff\73\66\u0095\uffff"+
            "\7\66\14\uffff\5\66\5\uffff\32\66\1\uffff\5\66\1\uffff\1\66"+
            "\1\uffff\2\66\1\uffff\2\66\1\uffff\154\66\41\uffff\u016b\66"+
            "\22\uffff\100\66\2\uffff\66\66\50\uffff\16\66\2\uffff\20\66"+
            "\20\uffff\4\66\14\uffff\5\66\20\uffff\2\66\2\uffff\12\66\1\uffff"+
            "\5\66\6\uffff\10\66\1\uffff\4\66\4\uffff\5\66\1\uffff\u0087"+
            "\66\4\uffff\7\66\2\uffff\61\66\1\uffff\1\66\2\uffff\1\66\1\uffff"+
            "\32\66\1\uffff\1\66\1\uffff\1\66\2\uffff\1\66\2\uffff\133\66"+
            "\3\uffff\6\66\2\uffff\6\66\2\uffff\6\66\2\uffff\3\66\3\uffff"+
            "\3\66\1\uffff\3\66\1\uffff\7\66",
            "\2\66\1\63\1\62\3\66\2\uffff\1\67\1\66\1\uffff\1\66\1\uffff"+
            "\1\66\12\60\1\uffff\1\66\1\uffff\1\66\1\uffff\1\70\1\66\32\57"+
            "\1\uffff\1\64\2\uffff\1\61\1\uffff\32\56\1\uffff\1\66\44\uffff"+
            "\7\66\1\uffff\2\66\1\uffff\1\66\1\uffff\1\66\1\uffff\4\66\1"+
            "\uffff\3\66\1\uffff\2\66\1\uffff\u017b\66\31\uffff\162\66\4"+
            "\uffff\14\66\16\uffff\5\66\11\uffff\1\66\21\uffff\130\66\5\uffff"+
            "\23\66\12\uffff\1\66\3\uffff\1\66\7\uffff\5\66\1\uffff\1\66"+
            "\1\uffff\24\66\1\uffff\54\66\1\uffff\54\66\4\uffff\u0087\66"+
            "\1\uffff\107\66\1\uffff\46\66\2\uffff\2\66\6\uffff\20\66\41"+
            "\uffff\46\66\2\uffff\7\66\1\uffff\47\66\1\uffff\2\66\6\uffff"+
            "\21\66\1\uffff\27\66\1\uffff\12\66\13\uffff\33\66\5\uffff\5"+
            "\66\27\uffff\12\66\5\uffff\1\66\3\uffff\1\66\1\uffff\32\66\5"+
            "\uffff\31\66\7\uffff\175\66\1\uffff\60\66\2\uffff\73\66\2\uffff"+
            "\3\66\60\uffff\62\66\u014f\uffff\71\66\2\uffff\22\66\2\uffff"+
            "\5\66\3\uffff\31\66\20\uffff\3\66\1\uffff\10\66\2\uffff\2\66"+
            "\2\uffff\26\66\1\uffff\7\66\1\uffff\1\66\3\uffff\4\66\2\uffff"+
            "\11\66\2\uffff\2\66\2\uffff\3\66\11\uffff\1\66\4\uffff\2\66"+
            "\1\uffff\5\66\2\uffff\25\66\6\uffff\3\66\1\uffff\6\66\4\uffff"+
            "\2\66\2\uffff\26\66\1\uffff\7\66\1\uffff\2\66\1\uffff\2\66\1"+
            "\uffff\2\66\2\uffff\1\66\1\uffff\5\66\4\uffff\2\66\2\uffff\3"+
            "\66\13\uffff\4\66\1\uffff\1\66\7\uffff\17\66\14\uffff\3\66\1"+
            "\uffff\11\66\1\uffff\3\66\1\uffff\26\66\1\uffff\7\66\1\uffff"+
            "\2\66\1\uffff\5\66\2\uffff\12\66\1\uffff\3\66\1\uffff\3\66\2"+
            "\uffff\1\66\17\uffff\4\66\2\uffff\12\66\1\uffff\1\66\17\uffff"+
            "\3\66\1\uffff\10\66\2\uffff\2\66\2\uffff\26\66\1\uffff\7\66"+
            "\1\uffff\2\66\1\uffff\5\66\2\uffff\10\66\3\uffff\2\66\2\uffff"+
            "\3\66\10\uffff\2\66\4\uffff\2\66\1\uffff\3\66\4\uffff\14\66"+
            "\20\uffff\2\66\1\uffff\6\66\3\uffff\3\66\1\uffff\4\66\3\uffff"+
            "\2\66\1\uffff\1\66\1\uffff\2\66\3\uffff\2\66\3\uffff\3\66\3"+
            "\uffff\10\66\1\uffff\3\66\4\uffff\5\66\3\uffff\3\66\1\uffff"+
            "\4\66\11\uffff\1\66\17\uffff\24\66\6\uffff\3\66\1\uffff\10\66"+
            "\1\uffff\3\66\1\uffff\27\66\1\uffff\12\66\1\uffff\5\66\4\uffff"+
            "\7\66\1\uffff\3\66\1\uffff\4\66\7\uffff\2\66\11\uffff\2\66\4"+
            "\uffff\12\66\22\uffff\2\66\1\uffff\10\66\1\uffff\3\66\1\uffff"+
            "\27\66\1\uffff\12\66\1\uffff\5\66\2\uffff\11\66\1\uffff\3\66"+
            "\1\uffff\4\66\7\uffff\2\66\7\uffff\1\66\1\uffff\2\66\4\uffff"+
            "\12\66\22\uffff\2\66\1\uffff\10\66\1\uffff\3\66\1\uffff\27\66"+
            "\1\uffff\20\66\4\uffff\6\66\2\uffff\3\66\1\uffff\4\66\11\uffff"+
            "\1\66\10\uffff\2\66\4\uffff\12\66\22\uffff\2\66\1\uffff\22\66"+
            "\3\uffff\30\66\1\uffff\11\66\1\uffff\1\66\2\uffff\7\66\3\uffff"+
            "\1\66\4\uffff\6\66\1\uffff\1\66\1\uffff\10\66\22\uffff\3\66"+
            "\14\uffff\72\66\4\uffff\35\66\45\uffff\2\66\1\uffff\1\66\2\uffff"+
            "\2\66\1\uffff\1\66\2\uffff\1\66\6\uffff\4\66\1\uffff\7\66\1"+
            "\uffff\3\66\1\uffff\1\66\1\uffff\1\66\2\uffff\2\66\1\uffff\15"+
            "\66\1\uffff\3\66\2\uffff\5\66\1\uffff\1\66\1\uffff\6\66\2\uffff"+
            "\12\66\2\uffff\2\66\42\uffff\72\66\4\uffff\12\66\1\uffff\42"+
            "\66\6\uffff\33\66\4\uffff\10\66\1\uffff\44\66\1\uffff\17\66"+
            "\2\uffff\1\66\60\uffff\42\66\1\uffff\5\66\1\uffff\2\66\1\uffff"+
            "\7\66\3\uffff\4\66\6\uffff\32\66\106\uffff\46\66\12\uffff\51"+
            "\66\2\uffff\1\66\4\uffff\132\66\5\uffff\104\66\5\uffff\122\66"+
            "\6\uffff\7\66\1\uffff\77\66\1\uffff\1\66\1\uffff\4\66\2\uffff"+
            "\7\66\1\uffff\1\66\1\uffff\4\66\2\uffff\47\66\1\uffff\1\66\1"+
            "\uffff\4\66\2\uffff\37\66\1\uffff\1\66\1\uffff\4\66\2\uffff"+
            "\7\66\1\uffff\1\66\1\uffff\4\66\2\uffff\7\66\1\uffff\7\66\1"+
            "\uffff\27\66\1\uffff\37\66\1\uffff\1\66\1\uffff\4\66\2\uffff"+
            "\7\66\1\uffff\47\66\1\uffff\23\66\6\uffff\34\66\43\uffff\125"+
            "\66\14\uffff\u0276\66\12\uffff\32\66\5\uffff\121\66\17\uffff"+
            "\15\66\1\uffff\7\66\13\uffff\27\66\11\uffff\24\66\14\uffff\15"+
            "\66\1\uffff\3\66\1\uffff\2\66\14\uffff\64\66\2\uffff\50\66\2"+
            "\uffff\12\66\6\uffff\12\66\6\uffff\16\66\2\uffff\12\66\6\uffff"+
            "\130\66\10\uffff\52\66\126\uffff\35\66\3\uffff\14\66\4\uffff"+
            "\14\66\4\uffff\1\66\3\uffff\52\66\2\uffff\5\66\153\uffff\40"+
            "\66\u0300\uffff\154\66\u0094\uffff\u009c\66\4\uffff\132\66\6"+
            "\uffff\26\66\2\uffff\6\66\2\uffff\46\66\2\uffff\6\66\2\uffff"+
            "\10\66\1\uffff\1\66\1\uffff\1\66\1\uffff\1\66\1\uffff\37\66"+
            "\2\uffff\65\66\1\uffff\7\66\1\uffff\1\66\3\uffff\3\66\1\uffff"+
            "\7\66\3\uffff\4\66\2\uffff\6\66\4\uffff\15\66\5\uffff\3\66\1"+
            "\uffff\7\66\23\uffff\10\66\10\uffff\10\66\10\uffff\11\66\2\uffff"+
            "\12\66\2\uffff\16\66\2\uffff\1\66\30\uffff\2\66\2\uffff\11\66"+
            "\2\uffff\16\66\23\uffff\22\66\36\uffff\33\66\25\uffff\74\66"+
            "\1\uffff\17\66\7\uffff\61\66\14\uffff\u0199\66\2\uffff\u0089"+
            "\66\2\uffff\33\66\57\uffff\47\66\31\uffff\13\66\25\uffff\u01b8"+
            "\66\1\uffff\145\66\2\uffff\22\66\16\uffff\2\66\137\uffff\4\66"+
            "\1\uffff\4\66\2\uffff\34\66\1\uffff\43\66\1\uffff\1\66\1\uffff"+
            "\4\66\3\uffff\1\66\1\uffff\7\66\2\uffff\7\66\16\uffff\37\66"+
            "\3\uffff\30\66\1\uffff\16\66\21\uffff\26\66\12\uffff\u0193\66"+
            "\26\uffff\77\66\4\uffff\40\66\2\uffff\u0110\66\u0372\uffff\32"+
            "\66\1\uffff\131\66\14\uffff\u00d6\66\32\uffff\14\66\5\uffff"+
            "\7\66\12\uffff\2\66\10\uffff\1\66\3\uffff\40\66\1\uffff\126"+
            "\66\2\uffff\2\66\2\uffff\143\66\5\uffff\50\66\4\uffff\136\66"+
            "\1\uffff\50\66\70\uffff\57\66\1\uffff\44\66\14\uffff\56\66\1"+
            "\uffff\u0080\66\1\uffff\u1ab6\66\12\uffff\u51e6\66\132\uffff"+
            "\u048d\66\3\uffff\67\66\u0739\uffff\u2ba4\66\u215c\uffff\u012e"+
            "\66\2\uffff\73\66\u0095\uffff\7\66\14\uffff\5\66\5\uffff\32"+
            "\66\1\uffff\5\66\1\uffff\1\66\1\uffff\2\66\1\uffff\2\66\1\uffff"+
            "\154\66\41\uffff\u016b\66\22\uffff\100\66\2\uffff\66\66\50\uffff"+
            "\16\66\2\uffff\20\66\20\uffff\4\66\14\uffff\5\66\20\uffff\2"+
            "\66\2\uffff\12\66\1\uffff\5\66\6\uffff\10\66\1\uffff\4\66\4"+
            "\uffff\5\66\1\uffff\u0087\66\4\uffff\7\66\2\uffff\61\66\1\uffff"+
            "\1\66\2\uffff\1\66\1\uffff\32\66\1\uffff\1\66\1\uffff\1\66\2"+
            "\uffff\1\66\2\uffff\133\66\3\uffff\6\66\2\uffff\6\66\2\uffff"+
            "\6\66\2\uffff\3\66\3\uffff\3\66\1\uffff\3\66\1\uffff\7\66",
            "\2\66\1\63\1\62\3\66\2\uffff\1\67\1\66\1\uffff\1\66\1\uffff"+
            "\1\66\12\60\1\uffff\1\66\1\uffff\1\66\1\uffff\1\70\1\66\32\57"+
            "\1\uffff\1\64\2\uffff\1\61\1\uffff\32\56\1\uffff\1\66\44\uffff"+
            "\7\66\1\uffff\2\66\1\uffff\1\66\1\uffff\1\66\1\uffff\4\66\1"+
            "\uffff\3\66\1\uffff\2\66\1\uffff\u017b\66\31\uffff\162\66\4"+
            "\uffff\14\66\16\uffff\5\66\11\uffff\1\66\21\uffff\130\66\5\uffff"+
            "\23\66\12\uffff\1\66\3\uffff\1\66\7\uffff\5\66\1\uffff\1\66"+
            "\1\uffff\24\66\1\uffff\54\66\1\uffff\54\66\4\uffff\u0087\66"+
            "\1\uffff\107\66\1\uffff\46\66\2\uffff\2\66\6\uffff\20\66\41"+
            "\uffff\46\66\2\uffff\7\66\1\uffff\47\66\1\uffff\2\66\6\uffff"+
            "\21\66\1\uffff\27\66\1\uffff\12\66\13\uffff\33\66\5\uffff\5"+
            "\66\27\uffff\12\66\5\uffff\1\66\3\uffff\1\66\1\uffff\32\66\5"+
            "\uffff\31\66\7\uffff\175\66\1\uffff\60\66\2\uffff\73\66\2\uffff"+
            "\3\66\60\uffff\62\66\u014f\uffff\71\66\2\uffff\22\66\2\uffff"+
            "\5\66\3\uffff\31\66\20\uffff\3\66\1\uffff\10\66\2\uffff\2\66"+
            "\2\uffff\26\66\1\uffff\7\66\1\uffff\1\66\3\uffff\4\66\2\uffff"+
            "\11\66\2\uffff\2\66\2\uffff\3\66\11\uffff\1\66\4\uffff\2\66"+
            "\1\uffff\5\66\2\uffff\25\66\6\uffff\3\66\1\uffff\6\66\4\uffff"+
            "\2\66\2\uffff\26\66\1\uffff\7\66\1\uffff\2\66\1\uffff\2\66\1"+
            "\uffff\2\66\2\uffff\1\66\1\uffff\5\66\4\uffff\2\66\2\uffff\3"+
            "\66\13\uffff\4\66\1\uffff\1\66\7\uffff\17\66\14\uffff\3\66\1"+
            "\uffff\11\66\1\uffff\3\66\1\uffff\26\66\1\uffff\7\66\1\uffff"+
            "\2\66\1\uffff\5\66\2\uffff\12\66\1\uffff\3\66\1\uffff\3\66\2"+
            "\uffff\1\66\17\uffff\4\66\2\uffff\12\66\1\uffff\1\66\17\uffff"+
            "\3\66\1\uffff\10\66\2\uffff\2\66\2\uffff\26\66\1\uffff\7\66"+
            "\1\uffff\2\66\1\uffff\5\66\2\uffff\10\66\3\uffff\2\66\2\uffff"+
            "\3\66\10\uffff\2\66\4\uffff\2\66\1\uffff\3\66\4\uffff\14\66"+
            "\20\uffff\2\66\1\uffff\6\66\3\uffff\3\66\1\uffff\4\66\3\uffff"+
            "\2\66\1\uffff\1\66\1\uffff\2\66\3\uffff\2\66\3\uffff\3\66\3"+
            "\uffff\10\66\1\uffff\3\66\4\uffff\5\66\3\uffff\3\66\1\uffff"+
            "\4\66\11\uffff\1\66\17\uffff\24\66\6\uffff\3\66\1\uffff\10\66"+
            "\1\uffff\3\66\1\uffff\27\66\1\uffff\12\66\1\uffff\5\66\4\uffff"+
            "\7\66\1\uffff\3\66\1\uffff\4\66\7\uffff\2\66\11\uffff\2\66\4"+
            "\uffff\12\66\22\uffff\2\66\1\uffff\10\66\1\uffff\3\66\1\uffff"+
            "\27\66\1\uffff\12\66\1\uffff\5\66\2\uffff\11\66\1\uffff\3\66"+
            "\1\uffff\4\66\7\uffff\2\66\7\uffff\1\66\1\uffff\2\66\4\uffff"+
            "\12\66\22\uffff\2\66\1\uffff\10\66\1\uffff\3\66\1\uffff\27\66"+
            "\1\uffff\20\66\4\uffff\6\66\2\uffff\3\66\1\uffff\4\66\11\uffff"+
            "\1\66\10\uffff\2\66\4\uffff\12\66\22\uffff\2\66\1\uffff\22\66"+
            "\3\uffff\30\66\1\uffff\11\66\1\uffff\1\66\2\uffff\7\66\3\uffff"+
            "\1\66\4\uffff\6\66\1\uffff\1\66\1\uffff\10\66\22\uffff\3\66"+
            "\14\uffff\72\66\4\uffff\35\66\45\uffff\2\66\1\uffff\1\66\2\uffff"+
            "\2\66\1\uffff\1\66\2\uffff\1\66\6\uffff\4\66\1\uffff\7\66\1"+
            "\uffff\3\66\1\uffff\1\66\1\uffff\1\66\2\uffff\2\66\1\uffff\15"+
            "\66\1\uffff\3\66\2\uffff\5\66\1\uffff\1\66\1\uffff\6\66\2\uffff"+
            "\12\66\2\uffff\2\66\42\uffff\72\66\4\uffff\12\66\1\uffff\42"+
            "\66\6\uffff\33\66\4\uffff\10\66\1\uffff\44\66\1\uffff\17\66"+
            "\2\uffff\1\66\60\uffff\42\66\1\uffff\5\66\1\uffff\2\66\1\uffff"+
            "\7\66\3\uffff\4\66\6\uffff\32\66\106\uffff\46\66\12\uffff\51"+
            "\66\2\uffff\1\66\4\uffff\132\66\5\uffff\104\66\5\uffff\122\66"+
            "\6\uffff\7\66\1\uffff\77\66\1\uffff\1\66\1\uffff\4\66\2\uffff"+
            "\7\66\1\uffff\1\66\1\uffff\4\66\2\uffff\47\66\1\uffff\1\66\1"+
            "\uffff\4\66\2\uffff\37\66\1\uffff\1\66\1\uffff\4\66\2\uffff"+
            "\7\66\1\uffff\1\66\1\uffff\4\66\2\uffff\7\66\1\uffff\7\66\1"+
            "\uffff\27\66\1\uffff\37\66\1\uffff\1\66\1\uffff\4\66\2\uffff"+
            "\7\66\1\uffff\47\66\1\uffff\23\66\6\uffff\34\66\43\uffff\125"+
            "\66\14\uffff\u0276\66\12\uffff\32\66\5\uffff\121\66\17\uffff"+
            "\15\66\1\uffff\7\66\13\uffff\27\66\11\uffff\24\66\14\uffff\15"+
            "\66\1\uffff\3\66\1\uffff\2\66\14\uffff\64\66\2\uffff\50\66\2"+
            "\uffff\12\66\6\uffff\12\66\6\uffff\16\66\2\uffff\12\66\6\uffff"+
            "\130\66\10\uffff\52\66\126\uffff\35\66\3\uffff\14\66\4\uffff"+
            "\14\66\4\uffff\1\66\3\uffff\52\66\2\uffff\5\66\153\uffff\40"+
            "\66\u0300\uffff\154\66\u0094\uffff\u009c\66\4\uffff\132\66\6"+
            "\uffff\26\66\2\uffff\6\66\2\uffff\46\66\2\uffff\6\66\2\uffff"+
            "\10\66\1\uffff\1\66\1\uffff\1\66\1\uffff\1\66\1\uffff\37\66"+
            "\2\uffff\65\66\1\uffff\7\66\1\uffff\1\66\3\uffff\3\66\1\uffff"+
            "\7\66\3\uffff\4\66\2\uffff\6\66\4\uffff\15\66\5\uffff\3\66\1"+
            "\uffff\7\66\23\uffff\10\66\10\uffff\10\66\10\uffff\11\66\2\uffff"+
            "\12\66\2\uffff\16\66\2\uffff\1\66\30\uffff\2\66\2\uffff\11\66"+
            "\2\uffff\16\66\23\uffff\22\66\36\uffff\33\66\25\uffff\74\66"+
            "\1\uffff\17\66\7\uffff\61\66\14\uffff\u0199\66\2\uffff\u0089"+
            "\66\2\uffff\33\66\57\uffff\47\66\31\uffff\13\66\25\uffff\u01b8"+
            "\66\1\uffff\145\66\2\uffff\22\66\16\uffff\2\66\137\uffff\4\66"+
            "\1\uffff\4\66\2\uffff\34\66\1\uffff\43\66\1\uffff\1\66\1\uffff"+
            "\4\66\3\uffff\1\66\1\uffff\7\66\2\uffff\7\66\16\uffff\37\66"+
            "\3\uffff\30\66\1\uffff\16\66\21\uffff\26\66\12\uffff\u0193\66"+
            "\26\uffff\77\66\4\uffff\40\66\2\uffff\u0110\66\u0372\uffff\32"+
            "\66\1\uffff\131\66\14\uffff\u00d6\66\32\uffff\14\66\5\uffff"+
            "\7\66\12\uffff\2\66\10\uffff\1\66\3\uffff\40\66\1\uffff\126"+
            "\66\2\uffff\2\66\2\uffff\143\66\5\uffff\50\66\4\uffff\136\66"+
            "\1\uffff\50\66\70\uffff\57\66\1\uffff\44\66\14\uffff\56\66\1"+
            "\uffff\u0080\66\1\uffff\u1ab6\66\12\uffff\u51e6\66\132\uffff"+
            "\u048d\66\3\uffff\67\66\u0739\uffff\u2ba4\66\u215c\uffff\u012e"+
            "\66\2\uffff\73\66\u0095\uffff\7\66\14\uffff\5\66\5\uffff\32"+
            "\66\1\uffff\5\66\1\uffff\1\66\1\uffff\2\66\1\uffff\2\66\1\uffff"+
            "\154\66\41\uffff\u016b\66\22\uffff\100\66\2\uffff\66\66\50\uffff"+
            "\16\66\2\uffff\20\66\20\uffff\4\66\14\uffff\5\66\20\uffff\2"+
            "\66\2\uffff\12\66\1\uffff\5\66\6\uffff\10\66\1\uffff\4\66\4"+
            "\uffff\5\66\1\uffff\u0087\66\4\uffff\7\66\2\uffff\61\66\1\uffff"+
            "\1\66\2\uffff\1\66\1\uffff\32\66\1\uffff\1\66\1\uffff\1\66\2"+
            "\uffff\1\66\2\uffff\133\66\3\uffff\6\66\2\uffff\6\66\2\uffff"+
            "\6\66\2\uffff\3\66\3\uffff\3\66\1\uffff\3\66\1\uffff\7\66",
            "\2\66\1\63\1\62\3\66\2\uffff\1\67\1\66\1\uffff\1\66\1\uffff"+
            "\1\66\12\60\1\uffff\1\66\1\uffff\1\66\1\uffff\1\70\1\66\32\57"+
            "\1\uffff\1\64\2\uffff\1\61\1\uffff\32\56\1\uffff\1\66\44\uffff"+
            "\7\66\1\uffff\2\66\1\uffff\1\66\1\uffff\1\66\1\uffff\4\66\1"+
            "\uffff\3\66\1\uffff\2\66\1\uffff\u017b\66\31\uffff\162\66\4"+
            "\uffff\14\66\16\uffff\5\66\11\uffff\1\66\21\uffff\130\66\5\uffff"+
            "\23\66\12\uffff\1\66\3\uffff\1\66\7\uffff\5\66\1\uffff\1\66"+
            "\1\uffff\24\66\1\uffff\54\66\1\uffff\54\66\4\uffff\u0087\66"+
            "\1\uffff\107\66\1\uffff\46\66\2\uffff\2\66\6\uffff\20\66\41"+
            "\uffff\46\66\2\uffff\7\66\1\uffff\47\66\1\uffff\2\66\6\uffff"+
            "\21\66\1\uffff\27\66\1\uffff\12\66\13\uffff\33\66\5\uffff\5"+
            "\66\27\uffff\12\66\5\uffff\1\66\3\uffff\1\66\1\uffff\32\66\5"+
            "\uffff\31\66\7\uffff\175\66\1\uffff\60\66\2\uffff\73\66\2\uffff"+
            "\3\66\60\uffff\62\66\u014f\uffff\71\66\2\uffff\22\66\2\uffff"+
            "\5\66\3\uffff\31\66\20\uffff\3\66\1\uffff\10\66\2\uffff\2\66"+
            "\2\uffff\26\66\1\uffff\7\66\1\uffff\1\66\3\uffff\4\66\2\uffff"+
            "\11\66\2\uffff\2\66\2\uffff\3\66\11\uffff\1\66\4\uffff\2\66"+
            "\1\uffff\5\66\2\uffff\25\66\6\uffff\3\66\1\uffff\6\66\4\uffff"+
            "\2\66\2\uffff\26\66\1\uffff\7\66\1\uffff\2\66\1\uffff\2\66\1"+
            "\uffff\2\66\2\uffff\1\66\1\uffff\5\66\4\uffff\2\66\2\uffff\3"+
            "\66\13\uffff\4\66\1\uffff\1\66\7\uffff\17\66\14\uffff\3\66\1"+
            "\uffff\11\66\1\uffff\3\66\1\uffff\26\66\1\uffff\7\66\1\uffff"+
            "\2\66\1\uffff\5\66\2\uffff\12\66\1\uffff\3\66\1\uffff\3\66\2"+
            "\uffff\1\66\17\uffff\4\66\2\uffff\12\66\1\uffff\1\66\17\uffff"+
            "\3\66\1\uffff\10\66\2\uffff\2\66\2\uffff\26\66\1\uffff\7\66"+
            "\1\uffff\2\66\1\uffff\5\66\2\uffff\10\66\3\uffff\2\66\2\uffff"+
            "\3\66\10\uffff\2\66\4\uffff\2\66\1\uffff\3\66\4\uffff\14\66"+
            "\20\uffff\2\66\1\uffff\6\66\3\uffff\3\66\1\uffff\4\66\3\uffff"+
            "\2\66\1\uffff\1\66\1\uffff\2\66\3\uffff\2\66\3\uffff\3\66\3"+
            "\uffff\10\66\1\uffff\3\66\4\uffff\5\66\3\uffff\3\66\1\uffff"+
            "\4\66\11\uffff\1\66\17\uffff\24\66\6\uffff\3\66\1\uffff\10\66"+
            "\1\uffff\3\66\1\uffff\27\66\1\uffff\12\66\1\uffff\5\66\4\uffff"+
            "\7\66\1\uffff\3\66\1\uffff\4\66\7\uffff\2\66\11\uffff\2\66\4"+
            "\uffff\12\66\22\uffff\2\66\1\uffff\10\66\1\uffff\3\66\1\uffff"+
            "\27\66\1\uffff\12\66\1\uffff\5\66\2\uffff\11\66\1\uffff\3\66"+
            "\1\uffff\4\66\7\uffff\2\66\7\uffff\1\66\1\uffff\2\66\4\uffff"+
            "\12\66\22\uffff\2\66\1\uffff\10\66\1\uffff\3\66\1\uffff\27\66"+
            "\1\uffff\20\66\4\uffff\6\66\2\uffff\3\66\1\uffff\4\66\11\uffff"+
            "\1\66\10\uffff\2\66\4\uffff\12\66\22\uffff\2\66\1\uffff\22\66"+
            "\3\uffff\30\66\1\uffff\11\66\1\uffff\1\66\2\uffff\7\66\3\uffff"+
            "\1\66\4\uffff\6\66\1\uffff\1\66\1\uffff\10\66\22\uffff\3\66"+
            "\14\uffff\72\66\4\uffff\35\66\45\uffff\2\66\1\uffff\1\66\2\uffff"+
            "\2\66\1\uffff\1\66\2\uffff\1\66\6\uffff\4\66\1\uffff\7\66\1"+
            "\uffff\3\66\1\uffff\1\66\1\uffff\1\66\2\uffff\2\66\1\uffff\15"+
            "\66\1\uffff\3\66\2\uffff\5\66\1\uffff\1\66\1\uffff\6\66\2\uffff"+
            "\12\66\2\uffff\2\66\42\uffff\72\66\4\uffff\12\66\1\uffff\42"+
            "\66\6\uffff\33\66\4\uffff\10\66\1\uffff\44\66\1\uffff\17\66"+
            "\2\uffff\1\66\60\uffff\42\66\1\uffff\5\66\1\uffff\2\66\1\uffff"+
            "\7\66\3\uffff\4\66\6\uffff\32\66\106\uffff\46\66\12\uffff\51"+
            "\66\2\uffff\1\66\4\uffff\132\66\5\uffff\104\66\5\uffff\122\66"+
            "\6\uffff\7\66\1\uffff\77\66\1\uffff\1\66\1\uffff\4\66\2\uffff"+
            "\7\66\1\uffff\1\66\1\uffff\4\66\2\uffff\47\66\1\uffff\1\66\1"+
            "\uffff\4\66\2\uffff\37\66\1\uffff\1\66\1\uffff\4\66\2\uffff"+
            "\7\66\1\uffff\1\66\1\uffff\4\66\2\uffff\7\66\1\uffff\7\66\1"+
            "\uffff\27\66\1\uffff\37\66\1\uffff\1\66\1\uffff\4\66\2\uffff"+
            "\7\66\1\uffff\47\66\1\uffff\23\66\6\uffff\34\66\43\uffff\125"+
            "\66\14\uffff\u0276\66\12\uffff\32\66\5\uffff\121\66\17\uffff"+
            "\15\66\1\uffff\7\66\13\uffff\27\66\11\uffff\24\66\14\uffff\15"+
            "\66\1\uffff\3\66\1\uffff\2\66\14\uffff\64\66\2\uffff\50\66\2"+
            "\uffff\12\66\6\uffff\12\66\6\uffff\16\66\2\uffff\12\66\6\uffff"+
            "\130\66\10\uffff\52\66\126\uffff\35\66\3\uffff\14\66\4\uffff"+
            "\14\66\4\uffff\1\66\3\uffff\52\66\2\uffff\5\66\153\uffff\40"+
            "\66\u0300\uffff\154\66\u0094\uffff\u009c\66\4\uffff\132\66\6"+
            "\uffff\26\66\2\uffff\6\66\2\uffff\46\66\2\uffff\6\66\2\uffff"+
            "\10\66\1\uffff\1\66\1\uffff\1\66\1\uffff\1\66\1\uffff\37\66"+
            "\2\uffff\65\66\1\uffff\7\66\1\uffff\1\66\3\uffff\3\66\1\uffff"+
            "\7\66\3\uffff\4\66\2\uffff\6\66\4\uffff\15\66\5\uffff\3\66\1"+
            "\uffff\7\66\23\uffff\10\66\10\uffff\10\66\10\uffff\11\66\2\uffff"+
            "\12\66\2\uffff\16\66\2\uffff\1\66\30\uffff\2\66\2\uffff\11\66"+
            "\2\uffff\16\66\23\uffff\22\66\36\uffff\33\66\25\uffff\74\66"+
            "\1\uffff\17\66\7\uffff\61\66\14\uffff\u0199\66\2\uffff\u0089"+
            "\66\2\uffff\33\66\57\uffff\47\66\31\uffff\13\66\25\uffff\u01b8"+
            "\66\1\uffff\145\66\2\uffff\22\66\16\uffff\2\66\137\uffff\4\66"+
            "\1\uffff\4\66\2\uffff\34\66\1\uffff\43\66\1\uffff\1\66\1\uffff"+
            "\4\66\3\uffff\1\66\1\uffff\7\66\2\uffff\7\66\16\uffff\37\66"+
            "\3\uffff\30\66\1\uffff\16\66\21\uffff\26\66\12\uffff\u0193\66"+
            "\26\uffff\77\66\4\uffff\40\66\2\uffff\u0110\66\u0372\uffff\32"+
            "\66\1\uffff\131\66\14\uffff\u00d6\66\32\uffff\14\66\5\uffff"+
            "\7\66\12\uffff\2\66\10\uffff\1\66\3\uffff\40\66\1\uffff\126"+
            "\66\2\uffff\2\66\2\uffff\143\66\5\uffff\50\66\4\uffff\136\66"+
            "\1\uffff\50\66\70\uffff\57\66\1\uffff\44\66\14\uffff\56\66\1"+
            "\uffff\u0080\66\1\uffff\u1ab6\66\12\uffff\u51e6\66\132\uffff"+
            "\u048d\66\3\uffff\67\66\u0739\uffff\u2ba4\66\u215c\uffff\u012e"+
            "\66\2\uffff\73\66\u0095\uffff\7\66\14\uffff\5\66\5\uffff\32"+
            "\66\1\uffff\5\66\1\uffff\1\66\1\uffff\2\66\1\uffff\2\66\1\uffff"+
            "\154\66\41\uffff\u016b\66\22\uffff\100\66\2\uffff\66\66\50\uffff"+
            "\16\66\2\uffff\20\66\20\uffff\4\66\14\uffff\5\66\20\uffff\2"+
            "\66\2\uffff\12\66\1\uffff\5\66\6\uffff\10\66\1\uffff\4\66\4"+
            "\uffff\5\66\1\uffff\u0087\66\4\uffff\7\66\2\uffff\61\66\1\uffff"+
            "\1\66\2\uffff\1\66\1\uffff\32\66\1\uffff\1\66\1\uffff\1\66\2"+
            "\uffff\1\66\2\uffff\133\66\3\uffff\6\66\2\uffff\6\66\2\uffff"+
            "\6\66\2\uffff\3\66\3\uffff\3\66\1\uffff\3\66\1\uffff\7\66",
            "\7\66\2\uffff\1\67\1\66\1\uffff\1\66\1\uffff\13\66\1\uffff"+
            "\1\66\1\uffff\1\66\1\uffff\1\70\33\66\1\uffff\1\110\2\uffff"+
            "\1\66\1\uffff\32\66\1\uffff\1\66\44\uffff\7\66\1\uffff\2\66"+
            "\1\uffff\1\66\1\uffff\1\66\1\uffff\4\66\1\uffff\3\66\1\uffff"+
            "\2\66\1\uffff\u017b\66\31\uffff\162\66\4\uffff\14\66\16\uffff"+
            "\5\66\11\uffff\1\66\21\uffff\130\66\5\uffff\23\66\12\uffff\1"+
            "\66\3\uffff\1\66\7\uffff\5\66\1\uffff\1\66\1\uffff\24\66\1\uffff"+
            "\54\66\1\uffff\54\66\4\uffff\u0087\66\1\uffff\107\66\1\uffff"+
            "\46\66\2\uffff\2\66\6\uffff\20\66\41\uffff\46\66\2\uffff\7\66"+
            "\1\uffff\47\66\1\uffff\2\66\6\uffff\21\66\1\uffff\27\66\1\uffff"+
            "\12\66\13\uffff\33\66\5\uffff\5\66\27\uffff\12\66\5\uffff\1"+
            "\66\3\uffff\1\66\1\uffff\32\66\5\uffff\31\66\7\uffff\175\66"+
            "\1\uffff\60\66\2\uffff\73\66\2\uffff\3\66\60\uffff\62\66\u014f"+
            "\uffff\71\66\2\uffff\22\66\2\uffff\5\66\3\uffff\31\66\20\uffff"+
            "\3\66\1\uffff\10\66\2\uffff\2\66\2\uffff\26\66\1\uffff\7\66"+
            "\1\uffff\1\66\3\uffff\4\66\2\uffff\11\66\2\uffff\2\66\2\uffff"+
            "\3\66\11\uffff\1\66\4\uffff\2\66\1\uffff\5\66\2\uffff\25\66"+
            "\6\uffff\3\66\1\uffff\6\66\4\uffff\2\66\2\uffff\26\66\1\uffff"+
            "\7\66\1\uffff\2\66\1\uffff\2\66\1\uffff\2\66\2\uffff\1\66\1"+
            "\uffff\5\66\4\uffff\2\66\2\uffff\3\66\13\uffff\4\66\1\uffff"+
            "\1\66\7\uffff\17\66\14\uffff\3\66\1\uffff\11\66\1\uffff\3\66"+
            "\1\uffff\26\66\1\uffff\7\66\1\uffff\2\66\1\uffff\5\66\2\uffff"+
            "\12\66\1\uffff\3\66\1\uffff\3\66\2\uffff\1\66\17\uffff\4\66"+
            "\2\uffff\12\66\1\uffff\1\66\17\uffff\3\66\1\uffff\10\66\2\uffff"+
            "\2\66\2\uffff\26\66\1\uffff\7\66\1\uffff\2\66\1\uffff\5\66\2"+
            "\uffff\10\66\3\uffff\2\66\2\uffff\3\66\10\uffff\2\66\4\uffff"+
            "\2\66\1\uffff\3\66\4\uffff\14\66\20\uffff\2\66\1\uffff\6\66"+
            "\3\uffff\3\66\1\uffff\4\66\3\uffff\2\66\1\uffff\1\66\1\uffff"+
            "\2\66\3\uffff\2\66\3\uffff\3\66\3\uffff\10\66\1\uffff\3\66\4"+
            "\uffff\5\66\3\uffff\3\66\1\uffff\4\66\11\uffff\1\66\17\uffff"+
            "\24\66\6\uffff\3\66\1\uffff\10\66\1\uffff\3\66\1\uffff\27\66"+
            "\1\uffff\12\66\1\uffff\5\66\4\uffff\7\66\1\uffff\3\66\1\uffff"+
            "\4\66\7\uffff\2\66\11\uffff\2\66\4\uffff\12\66\22\uffff\2\66"+
            "\1\uffff\10\66\1\uffff\3\66\1\uffff\27\66\1\uffff\12\66\1\uffff"+
            "\5\66\2\uffff\11\66\1\uffff\3\66\1\uffff\4\66\7\uffff\2\66\7"+
            "\uffff\1\66\1\uffff\2\66\4\uffff\12\66\22\uffff\2\66\1\uffff"+
            "\10\66\1\uffff\3\66\1\uffff\27\66\1\uffff\20\66\4\uffff\6\66"+
            "\2\uffff\3\66\1\uffff\4\66\11\uffff\1\66\10\uffff\2\66\4\uffff"+
            "\12\66\22\uffff\2\66\1\uffff\22\66\3\uffff\30\66\1\uffff\11"+
            "\66\1\uffff\1\66\2\uffff\7\66\3\uffff\1\66\4\uffff\6\66\1\uffff"+
            "\1\66\1\uffff\10\66\22\uffff\3\66\14\uffff\72\66\4\uffff\35"+
            "\66\45\uffff\2\66\1\uffff\1\66\2\uffff\2\66\1\uffff\1\66\2\uffff"+
            "\1\66\6\uffff\4\66\1\uffff\7\66\1\uffff\3\66\1\uffff\1\66\1"+
            "\uffff\1\66\2\uffff\2\66\1\uffff\15\66\1\uffff\3\66\2\uffff"+
            "\5\66\1\uffff\1\66\1\uffff\6\66\2\uffff\12\66\2\uffff\2\66\42"+
            "\uffff\72\66\4\uffff\12\66\1\uffff\42\66\6\uffff\33\66\4\uffff"+
            "\10\66\1\uffff\44\66\1\uffff\17\66\2\uffff\1\66\60\uffff\42"+
            "\66\1\uffff\5\66\1\uffff\2\66\1\uffff\7\66\3\uffff\4\66\6\uffff"+
            "\32\66\106\uffff\46\66\12\uffff\51\66\2\uffff\1\66\4\uffff\132"+
            "\66\5\uffff\104\66\5\uffff\122\66\6\uffff\7\66\1\uffff\77\66"+
            "\1\uffff\1\66\1\uffff\4\66\2\uffff\7\66\1\uffff\1\66\1\uffff"+
            "\4\66\2\uffff\47\66\1\uffff\1\66\1\uffff\4\66\2\uffff\37\66"+
            "\1\uffff\1\66\1\uffff\4\66\2\uffff\7\66\1\uffff\1\66\1\uffff"+
            "\4\66\2\uffff\7\66\1\uffff\7\66\1\uffff\27\66\1\uffff\37\66"+
            "\1\uffff\1\66\1\uffff\4\66\2\uffff\7\66\1\uffff\47\66\1\uffff"+
            "\23\66\6\uffff\34\66\43\uffff\125\66\14\uffff\u0276\66\12\uffff"+
            "\32\66\5\uffff\121\66\17\uffff\15\66\1\uffff\7\66\13\uffff\27"+
            "\66\11\uffff\24\66\14\uffff\15\66\1\uffff\3\66\1\uffff\2\66"+
            "\14\uffff\64\66\2\uffff\50\66\2\uffff\12\66\6\uffff\12\66\6"+
            "\uffff\16\66\2\uffff\12\66\6\uffff\130\66\10\uffff\52\66\126"+
            "\uffff\35\66\3\uffff\14\66\4\uffff\14\66\4\uffff\1\66\3\uffff"+
            "\52\66\2\uffff\5\66\153\uffff\40\66\u0300\uffff\154\66\u0094"+
            "\uffff\u009c\66\4\uffff\132\66\6\uffff\26\66\2\uffff\6\66\2"+
            "\uffff\46\66\2\uffff\6\66\2\uffff\10\66\1\uffff\1\66\1\uffff"+
            "\1\66\1\uffff\1\66\1\uffff\37\66\2\uffff\65\66\1\uffff\7\66"+
            "\1\uffff\1\66\3\uffff\3\66\1\uffff\7\66\3\uffff\4\66\2\uffff"+
            "\6\66\4\uffff\15\66\5\uffff\3\66\1\uffff\7\66\23\uffff\10\66"+
            "\10\uffff\10\66\10\uffff\11\66\2\uffff\12\66\2\uffff\16\66\2"+
            "\uffff\1\66\30\uffff\2\66\2\uffff\11\66\2\uffff\16\66\23\uffff"+
            "\22\66\36\uffff\33\66\25\uffff\74\66\1\uffff\17\66\7\uffff\61"+
            "\66\14\uffff\u0199\66\2\uffff\u0089\66\2\uffff\33\66\57\uffff"+
            "\47\66\31\uffff\13\66\25\uffff\u01b8\66\1\uffff\145\66\2\uffff"+
            "\22\66\16\uffff\2\66\137\uffff\4\66\1\uffff\4\66\2\uffff\34"+
            "\66\1\uffff\43\66\1\uffff\1\66\1\uffff\4\66\3\uffff\1\66\1\uffff"+
            "\7\66\2\uffff\7\66\16\uffff\37\66\3\uffff\30\66\1\uffff\16\66"+
            "\21\uffff\26\66\12\uffff\u0193\66\26\uffff\77\66\4\uffff\40"+
            "\66\2\uffff\u0110\66\u0372\uffff\32\66\1\uffff\131\66\14\uffff"+
            "\u00d6\66\32\uffff\14\66\5\uffff\7\66\12\uffff\2\66\10\uffff"+
            "\1\66\3\uffff\40\66\1\uffff\126\66\2\uffff\2\66\2\uffff\143"+
            "\66\5\uffff\50\66\4\uffff\136\66\1\uffff\50\66\70\uffff\57\66"+
            "\1\uffff\44\66\14\uffff\56\66\1\uffff\u0080\66\1\uffff\u1ab6"+
            "\66\12\uffff\u51e6\66\132\uffff\u048d\66\3\uffff\67\66\u0739"+
            "\uffff\u2ba4\66\u215c\uffff\u012e\66\2\uffff\73\66\u0095\uffff"+
            "\7\66\14\uffff\5\66\5\uffff\32\66\1\uffff\5\66\1\uffff\1\66"+
            "\1\uffff\2\66\1\uffff\2\66\1\uffff\154\66\41\uffff\u016b\66"+
            "\22\uffff\100\66\2\uffff\66\66\50\uffff\16\66\2\uffff\20\66"+
            "\20\uffff\4\66\14\uffff\5\66\20\uffff\2\66\2\uffff\12\66\1\uffff"+
            "\5\66\6\uffff\10\66\1\uffff\4\66\4\uffff\5\66\1\uffff\u0087"+
            "\66\4\uffff\7\66\2\uffff\61\66\1\uffff\1\66\2\uffff\1\66\1\uffff"+
            "\32\66\1\uffff\1\66\1\uffff\1\66\2\uffff\1\66\2\uffff\133\66"+
            "\3\uffff\6\66\2\uffff\6\66\2\uffff\6\66\2\uffff\3\66\3\uffff"+
            "\3\66\1\uffff\3\66\1\uffff\7\66"
    };

    static final short[] DFA37_eot = DFA.unpackEncodedString(DFA37_eotS);
    static final short[] DFA37_eof = DFA.unpackEncodedString(DFA37_eofS);
    static final char[] DFA37_min = DFA.unpackEncodedStringToUnsignedChars(DFA37_minS);
    static final char[] DFA37_max = DFA.unpackEncodedStringToUnsignedChars(DFA37_maxS);
    static final short[] DFA37_accept = DFA.unpackEncodedString(DFA37_acceptS);
    static final short[] DFA37_special = DFA.unpackEncodedString(DFA37_specialS);
    static final short[][] DFA37_transition;

    static {
        int numStates = DFA37_transitionS.length;
        DFA37_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA37_transition[i] = DFA.unpackEncodedString(DFA37_transitionS[i]);
        }
    }

    class DFA37 extends DFA {

        public DFA37(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 37;
            this.eot = DFA37_eot;
            this.eof = DFA37_eof;
            this.min = DFA37_min;
            this.max = DFA37_max;
            this.accept = DFA37_accept;
            this.special = DFA37_special;
            this.transition = DFA37_transition;
        }
        public String getDescription() {
            return "1:1: Tokens : ( FTSPHRASE | URI | OR | AND | NOT | TILDA | LPAREN | RPAREN | PLUS | MINUS | COLON | STAR | AMP | EXCLAMATION | BAR | EQUALS | QUESTION_MARK | LCURL | RCURL | LSQUARE | RSQUARE | TO | COMMA | CARAT | DOLLAR | GT | LT | AT | PERCENT | ID | FLOATING_POINT_LITERAL | FTSWORD | FTSPRE | FTSWILD | WS );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            IntStream input = _input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA37_52 = input.LA(1);

                        s = -1;
                        if ( (LA37_52=='u') ) {s = 81;}

                        else if ( ((LA37_52 >= '\u0000' && LA37_52 <= 't')||(LA37_52 >= 'v' && LA37_52 <= '\uFFFF')) ) {s = 82;}

                        if ( s>=0 ) return s;
                        break;

                    case 1 : 
                        int LA37_33 = input.LA(1);

                        s = -1;
                        if ( (LA37_33=='u') ) {s = 77;}

                        else if ( ((LA37_33 >= '\u0000' && LA37_33 <= 't')||(LA37_33 >= 'v' && LA37_33 <= '\uFFFF')) ) {s = 78;}

                        if ( s>=0 ) return s;
                        break;

                    case 2 : 
                        int LA37_72 = input.LA(1);

                        s = -1;
                        if ( (LA37_72=='u') ) {s = 89;}

                        else if ( ((LA37_72 >= '\u0000' && LA37_72 <= 't')||(LA37_72 >= 'v' && LA37_72 <= '\uFFFF')) ) {s = 90;}

                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}

            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 37, _s, input);
            error(nvae);
            throw nvae;
        }

    }
 

}