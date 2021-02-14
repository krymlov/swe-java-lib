/*
* Author    Yura
* Created   2018-02
*/

package swisseph;


import java.util.ArrayList;

/**
 * @author Yura
 * @version 1.0, 2018-02
 */
public class SwissOut {
    protected final ArrayList<String> out = new ArrayList<>();
    
    protected int skippedFirstPrintln = 0;
    protected int skipFirstPrintln = -1;
    protected boolean skipAllPrint;


    public void println(String string) {
        if ( skipFirstPrintln < 0 ) {
            out.add(string);
            return;
        }
        
        if ( skipFirstPrintln >= skippedFirstPrintln ) {
            ++skippedFirstPrintln;
            return;
        }
        
        out.add(string);
    }
    
    
    public void print(String string) {
        if ( skipAllPrint ) return;
        
        final int size = out.size();

        if ( size == 0 ) {
            out.add(string);
            return;
        }
        
        final int idx = size - 1;
        String value = out.remove(idx);
        out.add(idx, value += string);
    }
    
    public void println() {
        //out.add("");
    }


    public ArrayList<String> getOutput() {
        return out;
    }
    
    
    public void setSkipAllPrint(boolean skipAllPrint) {
        this.skipAllPrint = skipAllPrint;
    }


    public void setSkipFirstPrintln(int skipFirstPrintln) {
        this.skipFirstPrintln = skipFirstPrintln;
    }


    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        
        for (int i = 0, size = out.size(); i < size; i++) {
            builder.append(i).append('\t').append(out.get(i)).append('\n');
        }
        
        return builder.toString();
    }
}
