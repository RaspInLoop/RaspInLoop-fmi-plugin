package org.raspinloop.pi4j.io.gpio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.internal.ui.SWTFactory;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.statushandlers.StatusManager;
import org.raspinloop.config.AlreadyUsedPin;
import org.raspinloop.config.BoardExtentionHardware;
import org.raspinloop.config.BoardHardware;
import org.raspinloop.config.HardwareConfig;
import org.raspinloop.config.Pin;
import org.raspinloop.fmi.plugin.Images;
import org.raspinloop.fmi.plugin.preferences.IHWListener;
import org.raspinloop.fmi.plugin.preferences.PinUsageBlock;
import org.raspinloop.fmi.plugin.preferences.PluggedHardwareEnumerator;
import org.raspinloop.fmi.plugin.preferences.RilHarwareListBlock;
import org.raspinloop.fmi.plugin.preferences.extension.AbstractHWConfigPage;

/**
 * Page used to edit a standard HW.
 * 
 * @since 3.3
 */
public class DefaultConfigPage extends AbstractHWConfigPage {

	// HW being edited or created
	private BoardHardware fHW;
	private Text fHWName;

	private IStatus[] fFieldStatus = new IStatus[1];
	private PinUsageBlock fPinUsageBlock;
	private RilHarwareListBlock fCompUsageBlock;
	private ArrayList<HardwareConfig> supportedHardwares;

	/**
	 * 
	 */
	public DefaultConfigPage() {
		super("Edit Hardware configuration");
		for (int i = 0; i < fFieldStatus.length; i++) {
			fFieldStatus[i] = Status.OK_STATUS;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.IDialogPage#getImage()
	 */
	@Override
	public Image getImage() {
		return Images.get(Images.IMG_HARDWARE);
	}

	private IHWListener compModifiedListener = new IHWListener() {
		@Override
		public void addOrRemoveHW(HardwareConfig hw) {
			fPinUsageBlock.setConfiguredComponentPins(fHW.getUsedByCompPins());
		}
	};

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets
	 * .Composite)
	 */
	public void createControl(Composite p) {
		// create a composite with standard margins and spacing
		Composite composite = new Composite(p, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));

		SWTFactory.createLabel(composite, "Hardware Name", 1);
		fHWName = SWTFactory.createSingleText(composite, 1);

		Label l = new Label(composite, SWT.NONE);
		l.setFont(composite.getFont());
		l.setText("Pin usage");
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.grabExcessHorizontalSpace = false;
		gd.verticalAlignment = SWT.BEGINNING;
		l.setLayoutData(gd);

		List<Pin> pinArrayList = new ArrayList<Pin>(fHW.getSupportedPin());
		final NaturalOrderComparator comparator = new NaturalOrderComparator();
		Collections.sort(pinArrayList, new Comparator<Pin>() {
			@Override
			public int compare(Pin pin1, Pin pin2) {
				return comparator.compare(pin1.getName(), pin2.getName());
			}
		});

		fPinUsageBlock = new PinUsageBlock(pinArrayList.toArray(new Pin[0]), fHW.getSimulatedProviderName());
		fPinUsageBlock.setWizard(getWizard());
		fPinUsageBlock.createControl(composite);
		Control pinUsageControl = fPinUsageBlock.getControl();
		pinUsageControl.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 1, 1));
		PluggedHardwareEnumerator enumerator = PluggedHardwareEnumerator.INSTANCE();
		supportedHardwares = enumerator.buildListImplementing(BoardExtentionHardware.class);

		fCompUsageBlock = new RilHarwareListBlock(fHW, supportedHardwares);
		fCompUsageBlock.setWizard(getWizard());
		fCompUsageBlock.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				updatePins(fHW);
			}
		});

		fCompUsageBlock.addEditHWListener(compModifiedListener);
		fCompUsageBlock.addAddHWListener(compModifiedListener);
		fCompUsageBlock.addDeleteHWListener(compModifiedListener);
		fCompUsageBlock.createControl(composite);
		Control compUsageControl = fCompUsageBlock.getControl();
		compUsageControl.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 2, 1));

		// add the listeners now to prevent them from monkeying with initialized
		// settings
		fHWName.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				validateHWName();
			}
		});

		Dialog.applyDialogFont(composite);
		setControl(composite);
		initializeFields();
	}

	/**
	 * Validates the entered name of the VM
	 * 
	 * @return the status of the name validation
	 */
	private void validateHWName() {
		nameChanged(fHWName.getText());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.debug.ui.launchConfigurations.AbstractVMInstallPage#finish
	 * ()
	 */
	@Override
	public boolean finish() {
		setFieldValuesToHW(fHW);
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jdt.debug.ui.launchConfigurations.AbstractVMInstallPage#
	 * getSelection()
	 */
	@Override
	public HardwareConfig getSelection() {
		return fHW;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jdt.debug.ui.launchConfigurations.AbstractVMInstallPage#
	 * setSelection(org.eclipse.jdt.launching.VMStandin)
	 */
	@Override
	public void setSelection(HardwareConfig hw) {
		super.setSelection(hw);
		if (hw instanceof BoardHardware)
			fHW = (BoardHardware) hw;
		setTitle("Configure Board");
		setDescription("Use this wizard to configure your simulated Raspberry Pi board  and its components.");
	}

	/**
	 * initialize fields to the specified HW
	 * 
	 * @param hw
	 *            the HW to initialize from
	 */
	protected void setFieldValuesToHW(BoardHardware hw) {

		hw.setName(fHWName.getText());
		updatePins(hw);
	}

	private void updatePins(BoardHardware hw) {
		try {
			ArrayList<Pin> pinToRemove = new ArrayList<Pin>();
			pinToRemove.addAll(hw.getInputPins());
			pinToRemove.addAll(hw.getOutputPins());
			
			for (Pin pin : pinToRemove) {
				hw.unUsePin(pin);
			}
			for (Pin inputPin : fPinUsageBlock.getConfiguredInputPins()) {
				hw.useInputPin(inputPin);
			}
			
			for (Pin outputPin : fPinUsageBlock.getConfiguredOutputPins()) {
				hw.useOutputPin(outputPin);
			}

		} catch (AlreadyUsedPin e) {
			IStatus status = new Status(1, "org.raspinloop.fmi.plugin.BaseHardwareConfigPage", e.getMessage());
			// Let the StatusManager handle the Status and provide a hint
			StatusManager.getManager().handle(status, StatusManager.LOG | StatusManager.SHOW);
		}
	}

	/**
	 * Initialize the dialogs fields
	 */
	private void initializeFields() {
		fHWName.setText(fHW.getName());
		fPinUsageBlock.setConfiguredInputPins(fHW.getInputPins());
		fPinUsageBlock.setConfiguredOutputPins(fHW.getOutputPins());
		fPinUsageBlock.setConfiguredComponentPins(fHW.getUsedByCompPins());
		fCompUsageBlock.setHwList(new ArrayList<HardwareConfig>(fHW.getComponents()));
		validateHWName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jdt.debug.ui.launchConfigurations.AbstractVMInstallPage#
	 * getVMStatus()
	 */
	@Override
	protected IStatus[] getHWStatus() {
		return fFieldStatus;
	}

}
