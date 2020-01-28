/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2010, The University of Melbourne, Australia
 */

package org.cloudbus.cloudsim;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.cloudbus.cloudsim.lists.PeList;
import org.cloudbus.cloudsim.provisioners.PeProvisioner;

/**
 * VmSchedulerTimeShared is a VMM allocation policy that
 * allocates one or more Pe to a VM, and allows sharing
 * of PEs by multiple VMs. This class also implements
 * 10% performance degration due to VM migration.
 *
 * @author		Rodrigo N. Calheiros
 * @author		Anton Beloglazov
 * @since		CloudSim Toolkit 1.0
 */
public class VmSchedulerTimeShared extends VmScheduler {

	/** The mips map requested. */
	private Map<String, List<Double>> mipsMapRequested;

	/** The pes in use. */
	private int pesInUse;

	/** The vms in migration. */
	private List<String> vmsMigratingOut;

	/**
	 * Instantiates a new vm scheduler time shared.
	 *
	 * @param pelist the pelist
	 */
	public VmSchedulerTimeShared(List<? extends Pe> pelist) {
		super(pelist);
		setMipsMapRequested(new HashMap<String, List<Double>>());
		setVmsInMigration(new ArrayList<String>());
	}

	/* (non-Javadoc)
	 * @see cloudsim.VmScheduler#allocatePesForVm(cloudsim.Vm, java.util.List)
	 */
	@Override
	public boolean allocatePesForVm(Vm vm, List<Double> mipsShareRequested) {
		/**
		 * TODO: add the same to RAM and BW provisioners
		 */
		if (vm.isInMigration()) {
			if (!getVmsInMigration().contains(vm.getUid())) {
				getVmsInMigration().add(vm.getUid());
			}
		} else {
			if (getVmsInMigration().contains(vm.getUid())) {
				getVmsInMigration().remove(vm.getUid());
			}
		}
		boolean result = allocatePesForVm(vm.getUid(), mipsShareRequested);
		updatePeProvisioning();
		return result;
	}

	/**
	 * Allocate pes for vm.
	 *
	 * @param vmUid the vm uid
	 * @param mipsShareRequested the mips share requested
	 *
	 * @return true, if successful
	 */
	protected boolean allocatePesForVm(String vmUid, List<Double> mipsShareRequested) {
		getMipsMapRequested().put(vmUid, mipsShareRequested);
		setPesInUse(getPesInUse() + mipsShareRequested.size());

		double totalRequestedMips = 0;
		double peMips = getPeCapacity();
		for (Double mips : mipsShareRequested) {
			if (mips > peMips) { // each virtual PE of a VM must require not more than the capacity of a physical PE
				return false;
			}
			totalRequestedMips += mips;
		}

		List<Double> mipsShareAllocated = new ArrayList<Double>();
		for (Double mipsRequested : mipsShareRequested) {
			if (getVmsInMigration().contains(vmUid)) {
				mipsRequested *= 0.9; // performance degradation due to migration = 10% MIPS
			}
			mipsShareAllocated.add(mipsRequested);
		}

		if (getAvailableMips() >= totalRequestedMips) {
			getMipsMap().put(vmUid, mipsShareAllocated);
			setAvailableMips(getAvailableMips() - totalRequestedMips);
		} else {
			int pesSkipped = 0;
			for (List<Double> mipsMap : getMipsMap().values()) {
				for (int i = 0; i < mipsMap.size(); i++) {
					if (mipsMap.get(i) == 0) {
						pesSkipped++;
						continue;
					}
				}
			}

			double shortage = (totalRequestedMips - getAvailableMips()) / (getPesInUse() - pesSkipped);

			getMipsMap().put(vmUid, mipsShareAllocated);
			setAvailableMips(0);

			double additionalShortage = 0;
			do {
				additionalShortage = 0;
				for (List<Double> mipsMap : getMipsMap().values()) {
					for (int i = 0; i < mipsMap.size(); i++) {
						if (mipsMap.get(i) == 0) {
							continue;
						}
						if (mipsMap.get(i) >= shortage) {
							mipsMap.set(i, mipsMap.get(i) - shortage);
						} else {
							additionalShortage += shortage - mipsMap.get(i);
							mipsMap.set(i, 0.0);
						}
						if (mipsMap.get(i) == 0) {
							pesSkipped++;
						}
					}
				}
				shortage = additionalShortage / (getPesInUse() - pesSkipped);
			} while (additionalShortage > 0);
		}

		return true;
	}

	/**
	 * Update allocation of VMs on PEs.
	 */
	protected void updatePeProvisioning() {
		Iterator<Pe> peIterator  = getPeList().iterator();
		Pe pe = peIterator.next();
		PeProvisioner peProvisioner = pe.getPeProvisioner();
		peProvisioner.deallocateMipsForAllVms();
		double availableMips = peProvisioner.getAvailableMips();
		for (Map.Entry<String, List<Double>> entry : getMipsMap().entrySet()) {
			String vmUid = entry.getKey();
			for (double mips : entry.getValue()) {
				if (availableMips >= mips) {
					peProvisioner.allocateMipsForVm(vmUid, mips);
					availableMips -= mips;
				} else {
					while (mips >= 0) {
						peProvisioner.allocateMipsForVm(vmUid, availableMips);
						mips -= availableMips;
						if (mips <= 0.1) {
							mips = 0;
							break;
						}
						if (!peIterator.hasNext()) {
							Log.printLine("There is no enough MIPS (" + mips + ") to accommodate VM " + vmUid);
                                                        break;//CJMERA
                                                }
						pe = peIterator.next();
						peProvisioner = pe.getPeProvisioner();
						peProvisioner.deallocateMipsForAllVms();
						availableMips = peProvisioner.getAvailableMips();
					}
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see cloudsim.VmScheduler#deallocatePesForVm(cloudsim.Vm)
	 */
	@Override
	public void deallocatePesForVm(Vm vm){
		getMipsMapRequested().remove(vm.getUid());
		setPesInUse(0);
		getMipsMap().clear();
		setAvailableMips(PeList.getTotalMips(getPeList()));

		for (Pe pe : getPeList()) {
			pe.getPeProvisioner().deallocateMipsForVm(vm);
		}

		for (Map.Entry<String, List<Double>> entry : getMipsMapRequested().entrySet()) {
			allocatePesForVm(entry.getKey(), entry.getValue());
		}

		updatePeProvisioning();
	}

	/**
	 * Releases PEs allocated to all the VMs.
	 *
	 * @param vm the vm
	 *
	 * @pre $none
	 * @post $none
	 */
	@Override
	public void deallocatePesForAllVms() {
		super.deallocatePesForAllVms();
		getMipsMapRequested().clear();
		setPesInUse(0);
	}

	/**
	 * Returns maximum available MIPS among all the PEs.
	 * For the time shared policy it is just all the avaiable
	 * MIPS.
	 *
	 * @return max mips
	 */
	@Override
	public double getMaxAvailableMips() {
		return getAvailableMips();
	}

	/**
	 * Sets the pes in use.
	 *
	 * @param pesInUse the new pes in use
	 */
	protected void setPesInUse(int pesInUse) {
		this.pesInUse = pesInUse;
	}

	/**
	 * Gets the pes in use.
	 *
	 * @return the pes in use
	 */
	protected int getPesInUse() {
		return pesInUse;
	}

	/**
	 * Gets the mips map requested.
	 *
	 * @return the mips map requested
	 */
	protected Map<String, List<Double>> getMipsMapRequested() {
		return mipsMapRequested;
	}

	/**
	 * Sets the mips map requested.
	 *
	 * @param mipsMapRequested the mips map requested
	 */
	protected void setMipsMapRequested(Map<String, List<Double>> mipsMapRequested) {
		this.mipsMapRequested = mipsMapRequested;
	}

	/**
	 * Gets the vms in migration.
	 *
	 * @return the vms in migration
	 */
	protected List<String> getVmsInMigration() {
		return vmsMigratingOut;
	}

	/**
	 * Sets the vms in migration.
	 *
	 * @param vmsMigratingOut the new vms in migration
	 */
	protected void setVmsInMigration(List<String> vmsInMigration) {
		this.vmsMigratingOut = vmsInMigration;
	}

}
