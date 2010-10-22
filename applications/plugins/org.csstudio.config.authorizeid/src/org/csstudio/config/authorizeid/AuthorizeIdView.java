package org.csstudio.config.authorizeid;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Locale;

import javax.naming.InvalidNameException;
import javax.naming.ServiceUnavailableException;

import org.csstudio.config.authorizeid.ldap.AuthorizationIdGRManagement;
import org.csstudio.config.authorizeid.ldap.AuthorizationIdManagement;
import org.csstudio.config.authorizeid.ldap.LdapAccess;
import org.csstudio.config.authorizeid.ldap.ObjectClass1;
import org.csstudio.config.authorizeid.ldap.ObjectClass2;
import org.csstudio.platform.security.SecurityFacade;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.part.ViewPart;

/**
 * {@code AuthorizeIdView} is a class, which main task is to display GUI of
 * AuthorizeId plugin.
 * @author Rok Povsic
 */
public class AuthorizeIdView extends ViewPart {

	public static final String ID = "org.csstudio.config.authorizeid";//$NON-NLS-1$

	private Label label;
	private Combo combo;
	private TableViewer tableViewer1;
	private TableViewer tableViewer2;
	private Table table1;
	private Table table2;

	static final int COL_EAIG = 0;
	static final int COL_EAIR = 1;

    private static final String SECURITY_ID = "AuthorizeId";
	/**
	 * Creates a view for the plugin.
	 */
	@Override
	public void createPartControl(final Composite parent) {
		parent.setLayout(new GridLayout(1, false));

		final Group g = new Group(parent, SWT.NONE);
		g.setText(Messages.AuthorizeIdView_SELECT_GROUP);
		g.setLayout(new FillLayout(SWT.HORIZONTAL));

		label = new Label(g, SWT.CENTER);
		label.setText(Messages.AuthorizeIdView_GROUP);

		combo = new Combo(g, SWT.NONE | SWT.READ_ONLY);

		String[] groups = new String[] { Messages.AuthorizeIdView_MessageWrong1 };

			try {
				groups = LdapAccess.getGroups();
			} catch (final Exception e) {
				e.printStackTrace();
			}

			for (final String group : groups) {
				combo.add(group);
			}

		combo.addSelectionListener(new SelectionListener() {

			@Override
            public void widgetDefaultSelected(final SelectionEvent e) {
			}

			@Override
            public void widgetSelected(final SelectionEvent e) {
				refreshTable1();
				table2.removeAll();
				table2.clearAll();

			}

		});

		final Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new RowLayout());

		final Composite c1 = new Composite(composite, SWT.FILL);
		createTableViewer1(c1);
		tableViewer1.getTable().setLayoutData(
				new GridData(SWT.FILL, SWT.FILL, true, true));

		table1.addSelectionListener(new SelectionListener() {

			@Override
            public void widgetDefaultSelected(final SelectionEvent e) {
			}

			@Override
            public void widgetSelected(final SelectionEvent e) {
				refreshTable2();
			}

		});

		final Composite c2 = new Composite(composite, SWT.NONE);
		c2.setLayout(new FillLayout(SWT.VERTICAL));
		createButtons1(c2);


		// is used to create empty space between first set of buttons and
		// second table
		@SuppressWarnings("unused") //$NON-NLS-1$
        final
		Composite emptySpace = new Composite(composite, SWT.NONE);

		final Composite c3 = new Composite(composite, SWT.NONE);
		createTableViewer2(c3);
		tableViewer2.getTable().setLayoutData(
				new GridData(SWT.FILL, SWT.FILL, true, true));

		final Composite c4 = new Composite(composite, SWT.NONE);
		c4.setLayout(new FillLayout(SWT.VERTICAL));
		createButtons2(c4);

		tableViewer2.setContentProvider(new AuthorizeIdContentProvider());
		tableViewer2.setLabelProvider(new AuthorizeIdLabelProvider());

		// TODO ask what this does
		getSite().setSelectionProvider(tableViewer1);
	}

	/**
	 * Creates a first table.
	 * @param parent a composite
	 */
	private void createTableViewer1(final Composite parent) {
		tableViewer1 = new TableViewer(parent, SWT.SINGLE | SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		tableViewer1.setColumnProperties(new String[] { Messages.AuthorizeIdView_EAIN});

		table1 = tableViewer1.getTable();

		table1.setSize(184, 500);

		table1.setHeaderVisible(true);
		table1.setLinesVisible(true);

		final TableColumn column = new TableColumn(table1, SWT.LEFT, 0);
		column.setText(Messages.AuthorizeIdView_EAIN);
		column.setWidth(180);

		column.addListener(SWT.Selection, new Listener() {
			@Override
            public void handleEvent(final Event e) {
				// sort column 1
				TableItem[] items = table1.getItems();
				final Collator collator = Collator.getInstance(Locale.getDefault());
				for (int i = 1; i < items.length; i++) {
					final String value1 = items[i].getText(0);
					for (int j = 0; j < i; j++) {
						final String value2 = items[j].getText(0);
						if (collator.compare(value1, value2) < 0) {
							final String[] values = { items[i].getText(0),
									items[i].getText(1) };
							items[i].dispose();
							final TableItem item = new TableItem(table1, SWT.NONE, j);
							item.setText(values);
							items = table1.getItems();
							break;
						}
					}
				}
			}
		});

	}

	/**
	 * Creates first set of buttons.
	 * @param parenta composite
	 */
	private void createButtons1(final Composite parent) {
		/**
		 * "New" button for the first table.
		 */
	    final boolean canExecute = SecurityFacade.getInstance().canExecute(SECURITY_ID, false);
		final Button _new = new Button(parent, SWT.PUSH);
		_new.setText(Messages.AuthorizeIdView_NEW);
		_new.setEnabled(canExecute);
		_new.addSelectionListener(new SelectionAdapter() {
			@Override
            public void widgetSelected(final SelectionEvent e) {
				if (combo.getText().equals("")) { //$NON-NLS-1$
					final Status status = new Status(IStatus.ERROR, Messages.AuthorizeIdView_Error,
							0, Messages.AuthorizeIdView_InvalidGroup, null);

					ErrorDialog.openError(
							Display.getCurrent().getActiveShell(),
							Messages.AuthorizeIdView_GroupError, Messages.AuthorizeIdView_GroupErrorDesc,
							status);
				} else {
					final InputDialog dialog = new InputDialog(Display.getCurrent()
							.getActiveShell(), Messages.AuthorizeIdView_NEW,
							Messages.AuthorizeIdView_Name, "", //$NON-NLS-1$
							new NewDataValidator());
					if (dialog.open() == Window.OK) {

						final String _name = dialog.getValue();
						final String _group = combo.getText();

						final ObjectClass1 oclass = ObjectClass1.AUTHORIZEID;

						final AuthorizationIdManagement nd = new AuthorizationIdManagement();
						try {
                            nd.insertNewData(_name, _group, oclass);
	                    } catch (final ServiceUnavailableException e1) {
	                        MessageDialog.openError(getSite().getShell(),
	                                                "LDAP error.",
	                                                "LDAP service unavailable, try again later or start the LDAP service manually.");
	                        return;
	                    } catch (final InvalidNameException e1) {
	                        MessageDialog.openError(getSite().getShell(),
	                                                "LDAP naming error.",
	                                                "LDAP action failed.\n" + e1.getMessage());
	                        return;
	                    }

						refreshTable1();
					}
				}
			}
		});

		/**
		 * "Edit" button for the first table.
		 */
		final Button _edit = new Button(parent, SWT.PUSH);
		_edit.setText(Messages.AuthorizeIdView_EDIT);
		_edit.setEnabled(canExecute);
		_edit.addSelectionListener(new SelectionAdapter() {
			@Override
            public void widgetSelected(final SelectionEvent e) {
				final String name = table1.getSelection()[0].getText();
				final String _group = combo.getText();

				final InputDialog dialog = new InputDialog(Display.getCurrent()
						.getActiveShell(), Messages.AuthorizeIdView_EDIT, Messages.AuthorizeIdView_NameEdit, name,
						new NewDataValidator());
				if (dialog.open() == Window.OK) {

					final String _name = dialog.getValue();

					final ObjectClass2 oclass2 = ObjectClass2.AUTHORIZEID;
					final AuthorizationIdGRManagement ndGr = new AuthorizationIdGRManagement();

					final ArrayList<String> eaig = new ArrayList<String>();
					final ArrayList<String> eair = new ArrayList<String>();

					try {
					    for (int i = 0; i < table2.getItemCount(); i++) {
					        eaig.add(table2.getItem(i).getText(0));
					        eair.add(table2.getItem(i).getText(1));

					        ndGr.deleteData(name, eair.get(i), eaig.get(i), _group);
					    }

					    final ObjectClass1 oclass = ObjectClass1.AUTHORIZEID;

					    final AuthorizationIdManagement nd = new AuthorizationIdManagement();
					    nd.deleteData(name, _group);
					    nd.insertNewData(_name, _group, oclass);

					    for (int i = 0; i < table2.getItemCount(); i++) {

					        ndGr.insertNewData(_name, _group, oclass2, eair.get(i),
					                           eaig.get(i));
					    }
					} catch (final ServiceUnavailableException e1) {
					    MessageDialog.openError(getSite().getShell(),
					                            "LDAP error.",
					                            "LDAP service unavailable, try again later or start the LDAP service manually.");
					    return;
					} catch (final InvalidNameException e1) {
					    MessageDialog.openError(getSite().getShell(),
					                            "LDAP naming error.",
					                            "LDAP action failed.\n" + e1.getMessage());
					    return;
					}

					refreshTable1();
				}

			}
		});

		/**
		 * "Delete" button for the first table.
		 */
		final Button _delete = new Button(parent, SWT.PUSH);
		_delete.setText(Messages.AuthorizeIdView_DELETE);
		_delete.setEnabled(canExecute);
		_delete.addSelectionListener(new SelectionAdapter() {
			@Override
            public void widgetSelected(final SelectionEvent e) {

				final String _name = table1.getSelection()[0].getText();
				final String _group = combo.getText();

				final MessageBox messageBox = new MessageBox(Display.getCurrent()
						.getActiveShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO);
				messageBox
						.setMessage(Messages.AuthorizeIdView_DelWarn
								+ Messages.AuthorizeIdView_DelWarn2);
				messageBox.setText(Messages.AuthorizeIdView_DelEntry);
				final int response = messageBox.open();
				if (response == SWT.YES) {

					deleteWholeTable2(_name, _group);

					final AuthorizationIdManagement aim = new AuthorizationIdManagement();
					try {
                        aim.deleteData(_name, _group);
                    } catch (final ServiceUnavailableException e1) {
                        MessageDialog.openError(getSite().getShell(),
                                                "LDAP error.",
                                                "LDAP service unavailable, try again later or start the LDAP service manually.");
                        return;
                    } catch (final InvalidNameException e1) {
                        MessageDialog.openError(getSite().getShell(),
                                                "LDAP naming error.",
                                                "LDAP action failed.\n" + e1.getMessage());
                        return;
                    }

					refreshTable1();
					refreshTable2();
				}
			}
		});
	}

	/**
	 * Creates a second table.
	 * @param parenta composite
	 */
	private void createTableViewer2(final Composite parent) {
		tableViewer2 = new TableViewer(parent, SWT.SINGLE | SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		tableViewer2.setColumnProperties(new String[] { "2", "3" }); //$NON-NLS-1$ //$NON-NLS-2$

		table2 = tableViewer2.getTable();
		table2.setSize(184 * 2, 500);
		table2.setHeaderVisible(true);
		table2.setLinesVisible(true);

		final TableColumn column1 = new TableColumn(table2, SWT.LEFT, COL_EAIG);
		column1.setText(Messages.AuthorizeIdView_EAIG);
		column1.setWidth(180);

		column1.addListener(SWT.Selection, new MyListener(0));

		final TableColumn column2 = new TableColumn(table2, SWT.LEFT, COL_EAIR);
		column2.setText(Messages.AuthorizeIdView_EAIR);
		column2.setWidth(180);

		column2.addListener(SWT.Selection, new MyListener(1));

	}

	/**
	 * Listener for sorting columns for second table.
	 */
	private class MyListener implements Listener {

		private final int i;

		public MyListener(final int i) {
			super();
			this.i = i;
		}

		@Override
        public void handleEvent(final Event event) {
			sortColumn(i);
		}
	}

	/**
	 * Sorts column alphabetically, when clicking on it's "header".
	 * @param colNum the number of column in table (starts with 0)
	 */
	private void sortColumn(final int colNum) {
		TableItem[] items = table2.getItems();
		final Collator collator = Collator.getInstance(Locale.getDefault());
		for (int i = 1; i < items.length; i++) {
			final String value1 = items[i].getText(colNum);
			for (int j = 0; j < i; j++) {
				final String value2 = items[j].getText(colNum);
				if (collator.compare(value1, value2) < 0) {
					final String[] values = { items[i].getText(0),
							items[i].getText(1) };
					items[i].dispose();
					final TableItem item = new TableItem(table2, SWT.NONE, j);
					item.setText(values);
					items = table2.getItems();
					break;
				}
			}
		}
	}

	/**
	 * Creates second set of buttons.
	 *
	 * @param parent
	 *            a composite
	 */
	private void createButtons2(final Composite parent) {
		/**
		 * "New" button for second table.
		 */
	    final boolean canExecute = SecurityFacade.getInstance().canExecute(SECURITY_ID, false);
		final Button _new = new Button(parent, SWT.PUSH);
		_new.setText(Messages.AuthorizeIdView_NEW);
		_new.setEnabled(canExecute);
		_new.addSelectionListener(new SelectionAdapter() {
			@Override
            public void widgetSelected(final SelectionEvent e) {
				final String _name = table1.getSelection()[0].getText();
				final CustomInputDialog dialog = new CustomInputDialog(Display
						.getCurrent().getActiveShell(), Messages.AuthorizeIdView_NEW,
						Messages.AuthorizeIdView_SelGroup,
						Messages.AuthorizeIdView_SelRole, null,null);

				if (dialog.open() == Window.OK) {
					final String _group = combo.getText();
					final String _eaig = dialog.getValue();
					final String _eair = dialog.getValue2();

					final ObjectClass2 oclass2 = ObjectClass2.AUTHORIZEID;

					final AuthorizationIdGRManagement nd = new AuthorizationIdGRManagement();
					try {
                        nd.insertNewData(_name, _group, oclass2, _eair, _eaig);
                    } catch (final ServiceUnavailableException e1) {
                        MessageDialog.openError(getSite().getShell(),
                                                "LDAP error.",
                                                "LDAP service unavailable, try again later or start the LDAP service manually.");
                        return;
                    } catch (final InvalidNameException e1) {
                        MessageDialog.openError(getSite().getShell(),
                                                "LDAP naming error.",
                                                "LDAP action failed.\n" + e1.getMessage());
                        return;
                    }

					refreshTable2();

				}
			}
		});

		/**
		 * "Edit" button for second table.
		 */
		final Button _edit = new Button(parent, SWT.PUSH);
		_edit.setText(Messages.AuthorizeIdView_EDIT);
		_edit.setEnabled(canExecute);
		_edit.addSelectionListener(new SelectionAdapter() {
			@Override
            public void widgetSelected(final SelectionEvent e) {
				final String _name = table1.getSelection()[0].getText();
				final String eaigSel = table2.getSelection()[0].getText(0);
				final String eairSel = table2.getSelection()[0].getText(1);
				final CustomInputDialog dialog = new CustomInputDialog(Display
						.getCurrent().getActiveShell(), Messages.AuthorizeIdView_EDIT,
						Messages.AuthorizeIdView_GroupEdit, Messages.AuthorizeIdView_RoleEdit, eaigSel,eairSel );
				if (dialog.open() == Window.OK) {
					final String _group = combo.getText();
					final String _eaig = dialog.getValue();
					final String _eair = dialog.getValue2();

					final ObjectClass2 oclass2 = ObjectClass2.AUTHORIZEID;

					final AuthorizationIdGRManagement nd = new AuthorizationIdGRManagement();
					try {
                        nd.deleteData(_name, eairSel, eaigSel, _group);

                        nd.insertNewData(_name, _group, oclass2, _eair, _eaig);
                    } catch (final ServiceUnavailableException e1) {
                        MessageDialog.openError(getSite().getShell(),
                                                "LDAP error.",
                                                "LDAP service unavailable, try again later or start the LDAP service manually.");
                        return;
                    } catch (final InvalidNameException e1) {
                        MessageDialog.openError(getSite().getShell(),
                                                "LDAP naming error.",
                                                "LDAP action failed.\n" + e1.getMessage());
                        return;
                    }
					refreshTable2();

				}
			}
		});

		/**
		 * "Delete" button for second table.
		 */
		final Button _delete = new Button(parent, SWT.PUSH);
		_delete.setText(Messages.AuthorizeIdView_DELETE);
		_delete.setEnabled(canExecute);
		_delete.addSelectionListener(new SelectionAdapter() {
			@Override
            public void widgetSelected(final SelectionEvent e) {
				final String _name = table1.getSelection()[0].getText();
				final String _eaig = table2.getSelection()[0].getText();
				final String _eair = table2.getSelection()[0].getText(1);
				final String _group = combo.getText();

				final MessageBox messageBox = new MessageBox(Display.getCurrent()
						.getActiveShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO);
				messageBox
						.setMessage(Messages.AuthorizeIdView_DelWarn);
				messageBox.setText(Messages.AuthorizeIdView_DelEntry);
				final int response = messageBox.open();
				if (response == SWT.YES) {
					final AuthorizationIdGRManagement aim = new AuthorizationIdGRManagement();
					try {
                        aim.deleteData(_name, _eair, _eaig, _group);
                    } catch (final ServiceUnavailableException e1) {
                        MessageDialog.openError(getSite().getShell(),
                                                "LDAP error.",
                                                "LDAP service unavailable, try again later or start the LDAP service manually.");
                        return;
                    } catch (final InvalidNameException e1) {
                        MessageDialog.openError(getSite().getShell(),
                                                "LDAP naming error.",
                                                "LDAP action failed.\n" + e1.getMessage());
                        return;
                    }
					refreshTable2();
				}
			}
		});
	}

	/**
	 * Deletes all data and fills the first table again.
	 */
	private void refreshTable1() {
		final String[] items = LdapAccess.getEain(combo.getText());

		table1.removeAll();
		table1.clearAll();

		for (final String item2 : items) {

			final TableItem item = new TableItem(table1, SWT.NONE);
			item.setText(item2);

		}
	}

	/**
	 * Deletes all data and fills the second table again.
	 */
	private void refreshTable2() {
		table2.removeAll();
		table2.clearAll();

		final AuthorizeIdEntry[] entries =
		    LdapAccess.getProp(table1.getSelection()[0].getText(), combo.getText());

		tableViewer2.setInput(entries);

	}

	/**
	 * Deletes whole second table.
	 * @param name
	 * @param group
	 */
	private void deleteWholeTable2(final String name, final String group) {
		final AuthorizationIdGRManagement aim2 = new AuthorizationIdGRManagement();

		for (int i = 0; i < table2.getItemCount(); i++) {
			if (table2.getItem(i).getText(0).equals("")) { //$NON-NLS-1$
				break;
			}

			final String _eaig = table2.getItem(i).getText(0);
			final String _eair = table2.getItem(i).getText(1);
			try {
                aim2.deleteData(name, _eair, _eaig, group);
            } catch (final ServiceUnavailableException e1) {
                MessageDialog.openError(getSite().getShell(),
                                        "LDAP error.",
                                        "LDAP service unavailable, try again later or start the LDAP service manually.");
                return;
            } catch (final InvalidNameException e1) {
                MessageDialog.openError(getSite().getShell(),
                                        "LDAP naming error.",
                                        "LDAP action failed.\n" + e1.getMessage());
                return;
            }

		}
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
	}

}
