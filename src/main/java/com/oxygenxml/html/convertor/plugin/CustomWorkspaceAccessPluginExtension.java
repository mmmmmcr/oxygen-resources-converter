package com.oxygenxml.html.convertor.plugin;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import com.oxygenxml.html.convertor.translator.OxygenTranslator;
import com.oxygenxml.html.convertor.translator.Tags;
import com.oxygenxml.html.convertor.translator.Translator;
import com.oxygenxml.html.convertor.view.ConvertorDialog;

import ro.sync.exml.plugin.workspace.WorkspaceAccessPluginExtension;
import ro.sync.exml.workspace.api.standalone.MenuBarCustomizer;
import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;

/**
 * Plugin extension - workspace access extension.
 */
public class CustomWorkspaceAccessPluginExtension implements WorkspaceAccessPluginExtension {

	private final static String MENU_NAME = "Tools";
	private final static String ANTERIOR_MENU_ITEM_ACTION_NAME = "XML_to_JSON";

	private JMenuItem convertorMenuItem;
	private Translator translator = new OxygenTranslator();

	/**
	 * @see ro.sync.exml.plugin.workspace.WorkspaceAccessPluginExtension#applicationStarted(ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace)
	 */
	@Override
	public void applicationStarted(final StandalonePluginWorkspace pluginWorkspaceAccess) {
		// You can set or read global options.
		// The "ro.sync.exml.options.APIAccessibleOptionTags" contains all
		// accessible keys.
		// pluginWorkspaceAccess.setGlobalObjectProperty("can.edit.read.only.files",
		// Boolean.FALSE);
		// Check In action

		// You can access the content inside each opened WSEditor depending on the
		// current editing page (Text/Grid or Author).
		// A sample action which will be mounted on the main menu, toolbar and
		// contextual menu.
		final Action convertAction = createConvertDialogAction(pluginWorkspaceAccess);

		// Create your own main menu and add it to Oxygen or remove one of Oxygen's
		// menus...
		pluginWorkspaceAccess.addMenuBarCustomizer(new MenuBarCustomizer() {
			/**
			 * @see ro.sync.exml.workspace.api.standalone.MenuBarCustomizer#customizeMainMenu(javax.swing.JMenuBar)
			 */
			@Override
			public void customizeMainMenu(JMenuBar mainMenuBar) {
				addActionInMenuBar(mainMenuBar, convertAction, pluginWorkspaceAccess);
			}
		});

		// add a item with convertAction in contextual menu of ProjectManager
		ProjectManagerEditor.addPopUpMenuCustomizer(pluginWorkspaceAccess, convertAction);

	}

	/**
	 * Create the Swing action which shows the current selection.
	 * 
	 * @param pluginWorkspaceAccess
	 *          The plugin workspace access.
	 * @return The "Show Selection" action
	 */
	@SuppressWarnings("serial")
	private AbstractAction createConvertDialogAction(final StandalonePluginWorkspace pluginWorkspaceAccess) {
		return new AbstractAction(translator.getTranslation(Tags.MENU_ITEM_TEXT)) {
			@Override
			public void actionPerformed(ActionEvent actionevent) {

				List<String> selectedFile = new ArrayList<String>();

				JMenuItem menuItemAction = (JMenuItem) (actionevent.getSource());
				
				
				System.out.println(convertorMenuItem);
				if (!menuItemAction.equals(convertorMenuItem)){
					System.out.println("nu e egal");
					selectedFile = ProjectManagerEditor.getSelectedHtmlAndMdFiles(pluginWorkspaceAccess);
				}
				
				ConvertorDialog convertorDialog = new ConvertorDialog(selectedFile,
						(JFrame) pluginWorkspaceAccess.getParentFrame(), translator);

			}
		};
	}

	/**
	 * Add the given Action in the given JMenuBar.
	 *
	 * @param mainMenuBar
	 *          The menuBar
	 * @param actionToAdd
	 *          The action.
	 * @param pluginWorkspaceAccess
	 *          The oxygen PluginWorkspaceAccess.
	 */
	private void addActionInMenuBar(JMenuBar mainMenuBar, Action actionToAdd,
			StandalonePluginWorkspace pluginWorkspaceAccess) {

		convertorMenuItem = new JMenuItem(actionToAdd);
		
		// get the number of items in MenuBar
		int menuBarSize = mainMenuBar.getMenuCount();

		// iterate over items in menuBar
		for (int j = 0; j < menuBarSize; j++) {

			// get the menu with index j
			JMenu menu = mainMenuBar.getMenu(j);

			if (menu != null) {

				int sizeMenu = menu.getItemCount();
				for (int i = 0; i < sizeMenu; i++) {

					JMenuItem menuItem = menu.getItem(i);

					if (menuItem != null) {

						Action action = menuItem.getAction();
						if (action != null) {
							// get the actionID
							String actionID = pluginWorkspaceAccess.getOxygenActionID(action);

							// The actionId is in format: menuNameId/menuItemActionID
							int indexOfSlash = actionID.indexOf("/");

							// check the menuNameID
							if (MENU_NAME.equals(actionID.substring(0, indexOfSlash))) {

								// the menuNameId is MENU_NAME
								// check the menuItemActionID
								if (ANTERIOR_MENU_ITEM_ACTION_NAME.equals(actionID.substring(indexOfSlash + 1))) {
									// the MenuIdemActionId is ANTERIOR_MENU_ITEM_ACTION_NAME.
									// add the action after this index.
									menu.add(convertorMenuItem, i + 1);

									// break the loops.
									j = menuBarSize;
									break;
								}

							} else {
								// the menuNameId is not MENU_NAME
								break;
							}
						}

					}
				}
			}

		}
	}

	/**
	 * @see ro.sync.exml.plugin.workspace.WorkspaceAccessPluginExtension#applicationClosing()
	 */
	@Override
	public boolean applicationClosing() {
		// You can reject the application closing here
		return true;
	}
}