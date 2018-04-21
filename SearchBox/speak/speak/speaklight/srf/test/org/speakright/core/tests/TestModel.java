/**
* Copyright (c) 2007 Ian Rae
* All Rights Reserved.
* Licensed under the Eclipse Public License - v 1.0
* For more information see http://www.eclipse.org/legal/epl-v10.html
*/
package org.speakright.core.tests;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.Test;
import org.speakright.core.*;
import org.speakright.core.flows.LoopFlow;
import org.speakright.core.flows.QuestionFlow;
import org.speakright.core.render.Prompt;

public class TestModel extends BaseTest {
	
	@Test public void mvn()
	{
		Model M = new Model();
		String s = M.ModelVarNames.CITY;
		assertEquals("mvn", "city", s);
	}

	@Test
	public void Run() {
		Model M = new Model();
		assertEquals("city", "", M.city().get());
		assertEquals("x", 0, M.x().get());

		M.city().set("toronto");
		M.x().set(33);
		assertEquals("city", "toronto", M.city().get());
		assertEquals("x", 33, M.x().get());
	}

	@Test
	public void Reflect() {
		log("refl");
		Model m = new Model();
		Class c = m.getClass();
		Method[] ar = c.getMethods();
		for (int i = 0; i < ar.length; i++) {
			String s = ar[i].getName();
			log(s);
			if (s == "city") {
				try {
					// ar[i].invoke(m, new Object[] { "ottawa" } );
					Object ret = ar[i].invoke(m, new Object[] {});
					assertEquals("class", "StringItem", ret.getClass()
							.getSimpleName());
					doItem((Model.StringItem) ret, "ottawa");
				} catch (InvocationTargetException e) {
				} catch (IllegalAccessException e) {
				} finally {

				}
			}
		}

		assertEquals("city", "ottawa", m.city().get());
	}

	void doItem(Model.StringItem item, String val) {
		Class c = item.getClass();
		Method[] ar = c.getMethods();
		for (int i = 0; i < ar.length; i++) {
			String s = ar[i].getName();
			log(s);
			if (s == "set") {
				try {
					ar[i].invoke(item, new Object[] { val });
				} catch (InvocationTargetException e) {
				} catch (IllegalAccessException e) {
				} finally {

				}
			}
		}
	}

//	@Test
//	public void Reflect2() {
//		log("refl");
//		Model m = new Model();
//		Class c = m.getClass();
//		Method[] ar = c.getMethods();
//		for (int i = 0; i < ar.length; i++) {
//			String s = ar[i].getName();
//			log(s);
//			if (s == "setX") {
//				try {
//					ar[i].invoke(m, new Object[] { 44 });
//				} catch (InvocationTargetException e) {
//				} catch (IllegalAccessException e) {
//				} finally {
//
//				}
//			}
//		}
//
//		assertEquals("city", "", m.city().get());
//		assertEquals("x", 44, m.x().get());
//	}

//	@Test
//	public void ReflectX() {
//		log("reflect x");
//		Model m = new Model();
//		Class c = m.getClass();
//		Method meth = FindMethod("setX", c);
//		Class[] params = meth.getParameterTypes();
//		log(params[0].getName());
//
//		try {
//			meth.invoke(m, new Object[] { 45 });
//		} catch (InvocationTargetException e) {
//		} catch (IllegalAccessException e) {
//		} finally {
//		}
//
//		assertEquals("X", 45, m.x().get());
//	}

	Method FindMethod(String methodName, Class c) {
		Method[] ar = c.getMethods();
		for (int i = 0; i < ar.length; i++) {
			String s = ar[i].getName();
			if (s == methodName) {
				return ar[i];
			}
		}
		return null;
	}

	@Test
	public void ModelBinding() {
		Model m = new Model();
		ModelBinder binder = new ModelBinder(m);
		MyFlow flow = new MyFlow("a");
		String slot = "destination";
		binder.addBinding(flow, slot, "City");

		String input = "halifax";
		SRResults results = new SRResults(input);
		results.addSlot(slot, input);
		binder.bind(flow, results);

		assertEquals("err", false, binder.failed(new SRError()));
		assertEquals("city", "halifax", m.city().get());
	}

	@Test
	public void ModelBindingBoolean() {
		Model m = new Model();
		ModelBinder binder = new ModelBinder(m);
		MyFlow flow = new MyFlow("a");
		String slot = "bb";
		binder.addBinding(flow, slot, "LoggedIn");

		String input = "1";
		SRResults results = new SRResults(input);
		results.addSlot(slot, input);
		binder.bind(flow, results);

		assertEquals("err", false, binder.failed(new SRError()));
		assertEquals("loggedIn", true, m.loggedIn().get());
	}

	@Test
	public void ModelInject() {
		Model m = new Model();
		ModelBinder binder = new ModelBinder(m);
		App1 flow = new App1();
		assertEquals("model", null, flow.M);

		binder.injectModel(flow);
		assertEquals("model", m, flow.M);

		// MyLoop doesn't have model M so binder does nothing
		LoopFlow loop = new LoopFlow(3);
		loop.add(new MyFlow("e"));
		loop.add(new MyFlow("f"));
		binder.injectModel(loop);
	}

	@Test
	public void ModelDataBind() {
		log("---------MODELDATABIND..");
		App1 flow = new App1();
		assertEquals("model", null, flow.M);

		TrailWrapper wrap1 = new TrailWrapper(flow);

		SRInstance run = StartIt(wrap1);
		flow.M.hack().set("1");
		Proceed(run, "id33");
		Proceed(run, "222");
		Proceed(run);
		Proceed(run, "choice1");
		Proceed(run, "halifax", "slot1");
		Proceed(run, "choice2");
		Proceed(run);

		Proceed(run, SRResults.ResultCode.DISCONNECT);
		assertEquals("fail", false, run.isFailed());
		assertEquals("fin", true, run.isFinished());
		assertEquals("start", true, run.isStarted());

		ChkTrail(run, "id;pwd;b;ask;choice1;ask;choice2;ask");
		assertEquals("city", "halifax", flow.M.city().get());
	}

	@Test
	public void ModelDataBindX() {
		log("---------MODELDATABINDX..");
		App1 flow = new App1();
		assertEquals("model", null, flow.M);

		TrailWrapper wrap1 = new TrailWrapper(flow);

		SRInstance run = StartIt(wrap1);
		flow.M.hack().set("2");
		Proceed(run, "id33");
		Proceed(run, "222");
		Proceed(run);
		Proceed(run, "choice1");
		Proceed(run, "46", "slot1");
		Proceed(run, "choice2");
		Proceed(run);

		Proceed(run, SRResults.ResultCode.DISCONNECT);
		chkFin(run);

		ChkTrail(run, "id;pwd;b;ask;choice1;ask;choice2;ask");
		assertEquals("x", 46, flow.M.x().get());
	}

	@SuppressWarnings("serial")
	public class EarlyBindingFlow extends QuestionFlow {

		public Model M; // can only access in Execute (or after)

		public EarlyBindingFlow(String text) {
			super("abc.grxml", text);
//			this.addBinding("slot2", "City");
			
			m_quest.grammar().addBinding("slot2", "City");
		}
	}

	@Test
	public void earlyBinding() {
		log("---------earlyBinding..");
		EarlyBindingFlow flow = new EarlyBindingFlow("early");
		assertEquals("model", null, flow.M);

		TrailWrapper wrap1 = new TrailWrapper(flow);

		SRInstance run = StartIt(wrap1);
		Proceed(run, "austin", "slot2");
		assertEquals("city", "austin", flow.M.city().get());

		chkFin(run);
		ChkTrail(run, "EarlyBindingFlow");
	}

	void chkFin(SRInstance run) {
		assertEquals("fail", false, run.isFailed());
		assertEquals("fin", true, run.isFinished());
		assertEquals("start", true, run.isStarted());
	}

	@Test
	public void modelItem() {
		Model.StringItem sit = new Model.StringItem();
		assertEquals("s", "", sit.get());

	}
}
