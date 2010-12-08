/**
 * 
 */
package org.csstudio.opibuilder.adl2boy.translator;

import java.util.Map;

import junit.framework.TestCase;

import org.csstudio.opibuilder.util.MacrosInput;
import org.csstudio.opibuilder.widgetActions.OpenDisplayAction;
import org.csstudio.utility.adlparser.fileParser.ADLWidget;
import org.csstudio.utility.adlparser.fileParser.WrongADLFormatException;
import org.csstudio.utility.adlparser.fileParser.widgetParts.ADLTestObjects;
import org.csstudio.utility.adlparser.fileParser.widgetParts.RelatedDisplayItem;
import org.eclipse.core.runtime.Path;

/**
 * @author hammonds
 *
 */
public class RelatedDisplay2ModelUiPluginTest extends TestCase {
	RelatedDisplay2Model converter = null;
	
	/**
	 * @throws java.lang.Exception
	 */
	protected void setUp() throws Exception {
		converter = new RelatedDisplay2Model(ADLTestObjects.makeColorMap());
//		converter.setColorMap(makeColorMap());
	}

	/**
	 * @throws java.lang.Exception
	 */
	protected void tearDown() throws Exception {
		converter.cleanup();
		converter = null;
		
	}

	/**
	 * Test method for {@link org.csstudio.opibuilder.adl2boy.translator.RelatedDisplay2Model#RelatedDisplay2Model(org.csstudio.utility.adlparser.fileParser.ADLWidget, org.eclipse.swt.graphics.RGB[], org.csstudio.opibuilder.model.AbstractContainerModel)}.
	 */
	public void testRelatedDisplay2Model() {
		
	}
	
	public void testCreateOpenDisplayAction(){
		// Standard test
		{
			OpenDisplayAction od = converter.createOpenDisplayAction(ADLTestObjects.makeRelatedDisplay1());
			Path path = (Path)od.getPropertyValue(OpenDisplayAction.PROP_PATH);
			assertTrue("FilePath " + path, path.toString().equals("myfile.opi"));
			Boolean replace = (Boolean)od.getPropertyValue(OpenDisplayAction.PROP_REPLACE);
			assertTrue("Replace display " + replace, replace.equals(true));
			String label = od.getDescription();
			assertTrue(" TestLabel " + label, "myLabel".equals(label));
			
		}
		//test where there is no policy defined
		{
			OpenDisplayAction od = converter.createOpenDisplayAction(ADLTestObjects.makeRelatedDisplayNoPolicy());
			Path path = (Path)od.getPropertyValue(OpenDisplayAction.PROP_PATH);
			assertTrue("FilePath " + path, path.toString().equals("myfile.opi"));
			Boolean replace = (Boolean)od.getPropertyValue(OpenDisplayAction.PROP_REPLACE);
			assertTrue("Replace display " + replace, replace.equals(false));
			String label = (String)od.getPropertyValue(OpenDisplayAction.PROP_DESCRIPTION);
			assertTrue(" TestLabel " + label, "myLabel".equals(label));
			
		}
		//test where there are mixed set of arguments (from parent and not)
		{
			OpenDisplayAction od = converter.createOpenDisplayAction(ADLTestObjects.makeRelatedDisplayMixedArgs());
			Path path = (Path)od.getPropertyValue(OpenDisplayAction.PROP_PATH);
			assertTrue("FilePath " + path, path.toString().equals("path/myfile.opi"));
			Boolean replace = (Boolean)od.getPropertyValue(OpenDisplayAction.PROP_REPLACE);
			assertTrue("Replace display " + replace, replace.equals(false));
			String label = (String)od.getPropertyValue(OpenDisplayAction.PROP_DESCRIPTION);
			assertTrue(" TestLabel " + label, "my label".equals(label));
		}
	}

	public void testAddMacrosToOpenDisplayAction() {
		// Explicite definition
		RelatedDisplayItem[] rds = makeRelatedDisplays(ADLTestObjects.setupRelDispNoPolicy());
		OpenDisplayAction odAction = new OpenDisplayAction();
		converter.addMacrosToOpenDisplayAction(rds[0], odAction);
		Map<String,String> map = ((MacrosInput)(odAction.getPropertyValue(OpenDisplayAction.PROP_MACROS))).getMacrosMap();
		assertEquals("Map size for P=iocT1:,M=m1:, " + map, 2, map.size());
		// Definition using parent macros
		rds = makeRelatedDisplays(ADLTestObjects.setupRelDisp());
		odAction = new OpenDisplayAction();
		converter.addMacrosToOpenDisplayAction(rds[0], odAction);
		map = ((MacrosInput)(odAction.getPropertyValue(OpenDisplayAction.PROP_MACROS))).getMacrosMap();
		assertEquals("Map size for P=$(P),M=$(M):, " + map, 0 , map.size());
		// Definition using parent macros
		rds = makeRelatedDisplays(ADLTestObjects.setupRelDispMixedArgs());
		odAction = new OpenDisplayAction();
		converter.addMacrosToOpenDisplayAction(rds[0], odAction);
		map = ((MacrosInput)(odAction.getPropertyValue(OpenDisplayAction.PROP_MACROS))).getMacrosMap();
		assertEquals("Map size for P=$(P),M=$(M),T=temp:,PREC=3", 2, map.size());
	}

	private RelatedDisplayItem[] makeRelatedDisplays(ADLWidget adl) {
		RelatedDisplayItem[] rds = new RelatedDisplayItem[1];
		try {
			rds[0] = new RelatedDisplayItem(adl);
		} catch (WrongADLFormatException e) {
			fail("This should work since test items are known");
			e.printStackTrace();
		}
		return rds;
	}

	/**
	 * Test the makeMacros function
	 */
	public void testMakeMacros(){
		// Explicite definition
		MacrosInput mi = converter.makeMacros("P=iocT1:,M=m1:");
		Map<String,String> map = mi.getMacrosMap();
		assertEquals("Map size for P=iocT1:,M=m1:, " + map, 2, map.size());
		// Definition with straight macros
		mi = converter.makeMacros("P=$(P),M=$(M)");
		map = mi.getMacrosMap();

		assertEquals("Map size for P=$(P),M=$(M)", map.size(), 0);
		// Mixed macros with parent macros
		mi = converter.makeMacros("P=$(P),M=$(M),T=temp:,PREC=3");
		map = mi.getMacrosMap();
		assertEquals("Map size for P=$(P),M=$(M),T=temp:,PREC=3", map.size(), 2);
	}


}
