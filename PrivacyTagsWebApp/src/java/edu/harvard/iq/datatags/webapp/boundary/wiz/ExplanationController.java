package edu.harvard.iq.datatags.webapp.boundary.wiz;

import edu.harvard.iq.datatags.webapp.model.Explanation;
import edu.harvard.iq.datatags.webapp.boundary.wiz.util.JsfUtil;
import edu.harvard.iq.datatags.webapp.boundary.wiz.util.PaginationHelper;

import java.io.Serializable;
import java.util.ResourceBundle;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

@ManagedBean(name = "explanationController")
@SessionScoped
public class ExplanationController implements Serializable {

	private Explanation current;
	private DataModel items = null;
	@EJB
	private edu.harvard.iq.datatags.webapp.boundary.wiz.ExplanationFacade ejbFacade;
	private PaginationHelper pagination;
	private int selectedItemIndex;

	public ExplanationController() {
	}

	public Explanation getSelected() {
		if (current == null) {
			current = new Explanation();
			selectedItemIndex = -1;
		}
		return current;
	}

	private ExplanationFacade getFacade() {
		return ejbFacade;
	}

	public PaginationHelper getPagination() {
		if (pagination == null) {
			pagination = new PaginationHelper(10) {

				@Override
				public int getItemsCount() {
					return getFacade().count();
				}

				@Override
				public DataModel createPageDataModel() {
					return new ListDataModel(getFacade().findRange(new int[]{getPageFirstItem(), getPageFirstItem() + getPageSize()}));
				}
			};
		}
		return pagination;
	}

	public String prepareList() {
		recreateModel();
		return "List";
	}

	public String prepareView() {
		current = (Explanation) getItems().getRowData();
		selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
		return "View";
	}

	public String prepareCreate() {
		current = new Explanation();
		selectedItemIndex = -1;
		return "Create";
	}

	public String create() {
		try {
			getFacade().create(current);
			JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/BundleWiz").getString("ExplanationCreated"));
			return prepareCreate();
		} catch (Exception e) {
			JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/BundleWiz").getString("PersistenceErrorOccured"));
			return null;
		}
	}

	public String prepareEdit() {
		current = (Explanation) getItems().getRowData();
		selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
		return "Edit";
	}

	public String update() {
		try {
			getFacade().edit(current);
			JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/BundleWiz").getString("ExplanationUpdated"));
			return "View";
		} catch (Exception e) {
			JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/BundleWiz").getString("PersistenceErrorOccured"));
			return null;
		}
	}

	public String destroy() {
		current = (Explanation) getItems().getRowData();
		selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
		performDestroy();
		recreatePagination();
		recreateModel();
		return "List";
	}

	public String destroyAndView() {
		performDestroy();
		recreateModel();
		updateCurrentItem();
		if (selectedItemIndex >= 0) {
			return "View";
		} else {
			// all items were removed - go back to list
			recreateModel();
			return "List";
		}
	}

	private void performDestroy() {
		try {
			getFacade().remove(current);
			JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/BundleWiz").getString("ExplanationDeleted"));
		} catch (Exception e) {
			JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/BundleWiz").getString("PersistenceErrorOccured"));
		}
	}

	private void updateCurrentItem() {
		int count = getFacade().count();
		if (selectedItemIndex >= count) {
			// selected index cannot be bigger than number of items:
			selectedItemIndex = count - 1;
			// go to previous page if last page disappeared:
			if (pagination.getPageFirstItem() >= count) {
				pagination.previousPage();
			}
		}
		if (selectedItemIndex >= 0) {
			current = getFacade().findRange(new int[]{selectedItemIndex, selectedItemIndex + 1}).get(0);
		}
	}

	public DataModel getItems() {
		if (items == null) {
			items = getPagination().createPageDataModel();
		}
		return items;
	}

	private void recreateModel() {
		items = null;
	}

	private void recreatePagination() {
		pagination = null;
	}

	public String next() {
		getPagination().nextPage();
		recreateModel();
		return "List";
	}

	public String previous() {
		getPagination().previousPage();
		recreateModel();
		return "List";
	}

	public SelectItem[] getItemsAvailableSelectMany() {
		return JsfUtil.getSelectItems(ejbFacade.findAll(), false);
	}

	public SelectItem[] getItemsAvailableSelectOne() {
		return JsfUtil.getSelectItems(ejbFacade.findAll(), true);
	}

	@FacesConverter(forClass = Explanation.class)
	public static class ExplanationControllerConverter implements Converter {

		@Override
		public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
			if (value == null || value.length() == 0) {
				return null;
			}
			ExplanationController controller = (ExplanationController) facesContext.getApplication().getELResolver().
					getValue(facesContext.getELContext(), null, "explanationController");
			return controller.ejbFacade.find(getKey(value));
		}

		java.lang.String getKey(String value) {
			java.lang.String key;
			key = value;
			return key;
		}

		String getStringKey(java.lang.String value) {
			StringBuilder sb = new StringBuilder();
			sb.append(value);
			return sb.toString();
		}

		@Override
		public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
			if (object == null) {
				return null;
			}
			if (object instanceof Explanation) {
				Explanation o = (Explanation) object;
				return getStringKey(o.getName());
			} else {
				throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Explanation.class.getName());
			}
		}

	}

}
