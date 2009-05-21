/*
Copyright (c) 2000 The Regents of the University of California.
All rights reserved.

Permission to use, copy, modify, and distribute this software for any
purpose, without fee, and without written agreement is hereby granted,
provided that the above copyright notice and the following two
paragraphs appear in all copies of this software.

IN NO EVENT SHALL THE UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY FOR
DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES ARISING OUT
OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF THE UNIVERSITY OF
CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

THE UNIVERSITY OF CALIFORNIA SPECIFICALLY DISCLAIMS ANY WARRANTIES,
INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY
AND FITNESS FOR A PARTICULAR PURPOSE.  THE SOFTWARE PROVIDED HEREUNDER IS
ON AN "AS IS" BASIS, AND THE UNIVERSITY OF CALIFORNIA HAS NO OBLIGATION TO
PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS.
*/

import java.io.*;


import java_cup.runtime.Symbol;

/** The lexer driver class */
class Main {

    /** Loops over lexed tokens, printing them out to the console */
    public static void main(String[] args) {
    	args = Flags.handleFlags(args);
    	PrintStream outfile=System.out;
    	if(!Flags.verbose){
    		try{
    			File out = new File(args[0]+"_tokens");
    			if(out.exists()) out.delete();
    			outfile = new PrintStream((new FileOutputStream(args[0]+"_tokens",true)));
    		}
    		catch(Exception e){
    			System.err.println(e.getMessage());    		
    		}
    	}
    for (int i = 0; i < args.length; i++) {
	    FileReader file = null;       

	    try {
		file = new FileReader(args[i]);  
		outfile.println("#name \"" + args[i] + "\"");  
		CoolLexer lexer = new CoolLexer(file);   
		lexer.set_filename(args[i]);                  
		Symbol s;
		while ((s = lexer.next_token()).sym != TokenConstants.EOF) {
			   Utilities.dumpToken(outfile, lexer.get_curr_lineno(), s);
		    }
	    } catch (FileNotFoundException ex) {
		Utilities.fatalError("Could not open input file " + args[i]);
	    } catch (IOException ex) {
		Utilities.fatalError("Unexpected exception in lexer");
	    }
	}
    }
}
