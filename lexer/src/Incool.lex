/*
 *  The scanner definition for COOL.
 */

import java_cup.runtime.Symbol;

%%

%{

/*  Stuff enclosed in %{ %} is copied verbatim to the lexer class
 *  definition, all the extra variables/functions you want to use in the
 *  lexer actions should go here.  Don't remove or modify anything that
 *  was there initially.  */
	// Max size of string constants
    static int MAX_STR_CONST = 1025;
	int yy_lexical_state;
	int balanced_comment=0;
    // For assembling string constants
    StringBuffer string_buf = new StringBuffer();

    private int curr_lineno = 1;
    int get_curr_lineno() {
		return curr_lineno;
    }

    private AbstractSymbol filename;

    void set_filename(String fname) {
		filename = AbstractTable.stringtable.addString(fname);
    }

    AbstractSymbol curr_filename() {
		return filename;
    }
%}

%init{
/*  Stuff enclosed in %init{ %init} is copied verbatim to the lexer
 *  class constructor, all the extra initialization you want to do should
 *  go here.  Don't remove or modify anything that was there initially. */

    // empty for now
%init}

%eofval{

/*  Stuff enclosed in %eofval{ %eofval} specifies java code that is
 *  executed when end-of-file is reached.  If you use multiple lexical
 *  states and want to do something special if an EOF is encountered in
 *  one of those states, place your code in the switch statement.
 *  Ultimately, you should return the EOF symbol, or your lexer won't
 *  work.  */
	
	switch(yy_lexical_state) {
    case YYINITIAL:
    	break;
	case COMMENT:
		yybegin(YYINITIAL);
		yy_lexical_state=0;
		return new Symbol(TokenConstants.ERROR,AbstractTable.stringtable.addString("EOF in comment"));
	case STRING:
		yybegin(YYINITIAL);
		yy_lexical_state=0;
		return new Symbol(TokenConstants.ERROR,AbstractTable.stringtable.addString("EOF in String constant"));
	}
	 return new Symbol(TokenConstants.EOF);   
%eofval}
%unicode
%class CoolLexer
%cup
%line
%state COMMENT
%state STRING

lineterm = [\f\r\t\v\0]
digit = [0-9]
number = {digit}+
class = (c|C)(l|L)(a|A)(s|S)(s|S)
if = (i|I)(f|F)
else = (e|E)(l|L)(s|S)(e|E)
fi = (f|F)(i|I)
in = (i|I)(n|N)
inherits = (i|I)(n|N)(h|H)(e|E)(r|R)(i|I)(t|T)(s|S)
isvoid = (i|I)(s|S)(v|V)(o|O)(i|I)(d|D)
let = (l|L)(e|E)(t|T)
loop = (l|L)(o|O)(o|O)(p|P)
pool = (p|P)(o|O)(o|O)(l|L)
then = (t|T)(h|H)(e|E)(n|N)
while = (w|W)(h|H)(i|I)(l|L)(e|E)
case = (c|C)(a|A)(s|S)(e|E)
esac = (e|E)(s|S)(a|A)(c|C)
new = (n|N)(e|E)(w|W)
of = (o|O)(f|F)
not = (n|N)(o|O)(t|T)
true = "t"(r|R)(u|U)(e|E)
false = "f"(a|A)(l|L)(s|S)(e|E)
commentcontent = [^"*)""(*""\n"]|"("|")"|"*"
typeid = [A-Z]([a-zA-Z_0-9])*
objectid = [a-z]([a-zA-Z_0-9])*
inlinecomment = "--"~"\n"
error = ["[""]""'"">"]
blank = " "
%%
<YYINITIAL>{
"=>"			{ /* Sample lexical rule for "=>" arrow.
                                     Further lexical rules should be defined
                                     here, after the last %% separator */
                                  return new Symbol(TokenConstants.DARROW); }
{inlinecomment} {curr_lineno++;}
{class} {return new Symbol(TokenConstants.CLASS);}
{if}	{return new Symbol(TokenConstants.IF);}
{else} {return new Symbol(TokenConstants.ELSE);}
{fi} {return new Symbol(TokenConstants.FI);}
{in} {return new Symbol(TokenConstants.IN);}
{inherits} {return new Symbol(TokenConstants.INHERITS);}
{isvoid} {return new Symbol(TokenConstants.ISVOID);}
{let} {return new Symbol(TokenConstants.LET);}
{loop} {return new Symbol(TokenConstants.LOOP);}
{pool} {return new Symbol(TokenConstants.POOL);}
{then} {return new Symbol(TokenConstants.THEN);}
{while} {return new Symbol(TokenConstants.WHILE);}
{case} {return new Symbol(TokenConstants.CASE);}
{esac} {return new Symbol(TokenConstants.ESAC);}
{new} {return new Symbol(TokenConstants.NEW);}
{of} {return new Symbol(TokenConstants.OF);}
{not} {return new Symbol(TokenConstants.NOT);}
{true} {return new Symbol(TokenConstants.BOOL_CONST,AbstractTable.stringtable.addString("true"));}
{false} {return new Symbol(TokenConstants.BOOL_CONST,AbstractTable.stringtable.addString("false"));}
"-" {return new Symbol(TokenConstants.MINUS);}
"+" {return new Symbol(TokenConstants.PLUS);}
"*" {return new Symbol(TokenConstants.MULT);}
"/" {return new Symbol(TokenConstants.DIV);}
"(" {return new Symbol(TokenConstants.LPAREN);}
")" {return new Symbol(TokenConstants.RPAREN);}
":" {return new Symbol(TokenConstants.COLON);}
"." {return new Symbol(TokenConstants.DOT);}
";" {return new Symbol(TokenConstants.SEMI);}
"<" {return new Symbol(TokenConstants.LT);}
"<=" {return new Symbol(TokenConstants.LE);}
"<-" {return new Symbol(TokenConstants.ASSIGN);}
"{" {return new Symbol(TokenConstants.LBRACE);}
"}" {return new Symbol(TokenConstants.RBRACE);}
"=" {return new Symbol(TokenConstants.EQ);}
"," {return new Symbol(TokenConstants.COMMA);}
"~" {return new Symbol(TokenConstants.NEG);}
"@" {return new Symbol(TokenConstants.AT);}
"*)" {return new Symbol(TokenConstants.ERROR,AbstractTable.stringtable.addString("Unmatched *)"));}
{error} {return new Symbol(TokenConstants.ERROR,AbstractTable.stringtable.addString(yytext()));}
{typeid} {	return new Symbol(TokenConstants.TYPEID,AbstractTable.idtable.addString(yytext()));}
{objectid} {return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));}
{number} {return new Symbol(TokenConstants.INT_CONST,AbstractTable.inttable.addInt(Integer.parseInt(yytext())));}
{lineterm} {}
{blank} {}
"*)" {return new Symbol(TokenConstants.ERROR,AbstractTable.stringtable.addString("Unmatched *)"));}
"\n" {curr_lineno++;}
\"	{string_buf.setLength(0); yybegin(STRING);yy_lexical_state=2;}
<STRING>[^ \" "\b" "\t" "\f" "\n" "\0"]|{blank}* {	
													string_buf.append(yytext());
												 }
<STRING>"\t" {	
				string_buf.append("\t");
			 }
<STRING>"\f" {	
				string_buf.append("\f");
			 }
<STRING>"\b" {
				string_buf.append("\b");
			 }
<STRING>"\\\n" {
				 string_buf.append('\n');curr_lineno++;
				}
<STRING>"\\\\" {
				 string_buf.append('\\');
				}
<STRING>"\n" {curr_lineno++;yybegin(YYINITIAL);yy_lexical_state=0;return new Symbol(TokenConstants.ERROR,AbstractTable.stringtable.addString("Unterminated String constant"));}
<STRING>"\0" {return new Symbol(TokenConstants.ERROR,AbstractTable.stringtable.addString("String contain null characters"));}
<STRING>"\\0" {string_buf.append("0");}
<STRING>\" {
		 yybegin(YYINITIAL);yy_lexical_state=0;
			 if(string_buf.toString().length() > MAX_STR_CONST)
			 	return new Symbol(TokenConstants.ERROR,AbstractTable.stringtable.addString("String constant too long"));
			  else return new Symbol(TokenConstants.STR_CONST,AbstractTable.stringtable.addString(string_buf.toString()));	
			}
"(*"   {yybegin(COMMENT);balanced_comment++;yy_lexical_state=1;}
<COMMENT> {commentcontent} {/* ignore comment content */}
<COMMENT> "\n" {curr_lineno++;}
<COMMENT> "(*" {balanced_comment++;}
<COMMENT>"*)"  {balanced_comment--;if(balanced_comment==0){yybegin(YYINITIAL);yy_lexical_state=0;}}
. 		 {return new Symbol(TokenConstants.ERROR,AbstractTable.stringtable.addString(yytext()));}
}