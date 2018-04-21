package org.speakright.core;


@SuppressWarnings("serial")
public class ModelBase implements IModel {

	transient IModelBinder m_binder;
	public IModelBinder ModelBinder() {
		return m_binder;
	}

	public void ModelBinderSet(IModelBinder binder) {
		m_binder = binder;
	}
}
