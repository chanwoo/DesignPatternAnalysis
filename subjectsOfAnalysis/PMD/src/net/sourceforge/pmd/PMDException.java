/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package net.sourceforge.pmd;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * A convenience exception wrapper.  Contains the original exception, if any.  Also, contains
 * a severity number (int).  Zero implies no severity.  The higher the number the greater the
 * severity.
 *
 * @author Donald A. Leckie
 * @since August 30, 2002
 * @version $Revision: 1.4 $, $Date: 2003/11/19 21:27:11 $
 */
public class PMDException extends Exception {

    private Exception reason;
    private int severity;

    public PMDException(String message) {
        super(message);
    }

    public PMDException(String message, Exception reason) {
        super(message);
        this.reason = reason;
    }

    public void printStackTrace() {
        printStackTrace(System.err);
    }

    public void printStackTrace(PrintStream s) {
        super.printStackTrace(s);
        if (this.reason != null) {
            s.print("Caused by: ");
            this.reason.printStackTrace(s);
        }
    }

    public void printStackTrace(PrintWriter s) {
        super.printStackTrace(s);
        if (this.reason != null) {
            s.print("Caused by: ");
            this.reason.printStackTrace(s);
        }
    }

    public Exception getReason() {
        return reason;
    }

    public void setSeverity(int severity) {
        this.severity = severity;
    }

    public int getSeverity() {
        return severity;
    }
}