package ngat.rcsgui.stable;

import javax.swing.plaf.*;
import javax.swing.plaf.metal.*;

public class MyLNF extends DefaultMetalTheme {

    @Override
	public String getName() { return "Phase2"; }

    private final ColorUIResource primary1 = new ColorUIResource(0,0,186); // labels ?
    private final ColorUIResource primary2 = new ColorUIResource(102,204,255); // menu hlgt,sliderbar,node hghlght 
    private final ColorUIResource primary3 = new ColorUIResource(153,204,204); // line, ttip bg, list sel bg, slider secy
    private final ColorUIResource secondary1 = new ColorUIResource(51,153,51); // borders 
    private final ColorUIResource secondary2 = new ColorUIResource(172,232,212); // greyed out, unsel tabs, btn press 
    // s3alt 153,204,204
    private final ColorUIResource secondary3 = new ColorUIResource(204,204,153); // bgs menu, dlg, popup, frame

    @Override
	public ColorUIResource getPrimary1() { return primary1;}
    @Override
	public ColorUIResource getPrimary2() { return primary2;}
    @Override
	public ColorUIResource getPrimary3() { return primary3;}
    @Override
	public ColorUIResource getSecondary1() { return secondary1;}
    @Override
	public ColorUIResource getSecondary2() { return secondary2;}
    @Override
	public ColorUIResource getSecondary3() { return secondary3;}

}
