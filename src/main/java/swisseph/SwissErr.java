/*
* Author    Yura
* Created   2018-02
*/

package swisseph;

/**
 * @author  Yura
 * @version 1.0, 2018-02
 */
final class SwissErr {
    private static final char NCHAR = '\n';
    
    private final StringBuilder err = new StringBuilder(0);
    

    public void println(Throwable t) {
        err.append(t.getMessage()).append(NCHAR);
    }
    
    
    public void println(String string) {
        err.append(string).append(NCHAR);
    }
    
    
    public void println(int n) {
        err.append(n).append(NCHAR);
    }
    
    
    public void print(String string) {
        err.append(string);
    }
}
