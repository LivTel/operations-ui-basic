package ngat.opsgui.perspectives.scheduling;

public interface SweepDisplay {

    /** Request a sweep to be displayed.
     * @param d The sweep we are interested in.
     * @param l The current latest sweep.
     * @param s True if we are synchronized.
     */
    public void displaySweep(int d, int l, boolean s);

}