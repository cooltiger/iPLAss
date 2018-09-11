/*
 * Copyright (C) 2011 INFORMATION SERVICES INTERNATIONAL - DENTSU, LTD. All Rights Reserved.
 *
 * Unless you have purchased a commercial license,
 * the following license terms apply:
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package org.iplass.mtp.view.generic.editor;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

import org.iplass.adminconsole.annotation.MultiLang;
import org.iplass.adminconsole.view.annotation.InputType;
import org.iplass.adminconsole.view.annotation.MetaFieldInfo;
import org.iplass.adminconsole.view.annotation.generic.EntityViewField;
import org.iplass.adminconsole.view.annotation.generic.FieldReferenceType;
import org.iplass.mtp.view.generic.Jsp;
import org.iplass.mtp.view.generic.Jsps;
import org.iplass.mtp.view.generic.ViewConst;

/**
 * 選択型プロパティエディタ
 * @author lis3wg
 */
@XmlAccessorType(XmlAccessType.FIELD)
@Jsps({
	@Jsp(path="/jsp/gem/generic/editor/SelectPropertyEditor.jsp", key=ViewConst.DESIGN_TYPE_GEM),
	@Jsp(path="/jsp/gem/aggregation/unit/editor/SelectPropertyEditor.jsp", key=ViewConst.DESIGN_TYPE_GEM_AGGREGATION)
})
public class SelectPropertyEditor extends PrimitivePropertyEditor {

	/** シリアルバージョンUID */
	private static final long serialVersionUID = -7350313249348123012L;

	/** 表示タイプ */
	@XmlType(namespace="http://mtp.iplass.org/xml/definition/view/generic")
	public enum SelectDisplayType {
		@XmlEnumValue("Radio")RADIO,
		@XmlEnumValue("Checkbox")CHECKBOX,
		@XmlEnumValue("Select")SELECT,
		@XmlEnumValue("Label")LABEL
	}

	/** 表示タイプ */
	@MetaFieldInfo(
			displayName="表示タイプ",
			displayNameKey="generic_editor_SelectPropertyEditor_displayTypeDisplaNameKey",
			inputType=InputType.ENUM,
			enumClass=SelectDisplayType.class,
			required=true,
			description="画面に表示する方法を選択します。",
			descriptionKey="generic_editor_SelectPropertyEditor_displayTypeDescriptionKey"
	)
	private SelectDisplayType displayType;

	/** セレクトボックスの値 */
	@MetaFieldInfo(
			displayName="選択値",
			displayNameKey="generic_editor_SelectPropertyEditor_valuesDisplaNameKey",
			inputType=InputType.REFERENCE,
			required=true,
			multiple=true,
			referenceClass=EditorValue.class
	)
	@MultiLang(isMultiLangValue = false)
	@EntityViewField(
			referenceTypes={FieldReferenceType.ALL}
	)
	private List<EditorValue> values;

	/** 初期値 */
	@MetaFieldInfo(
			displayName="初期値",
			displayNameKey="generic_editor_SelectPropertyEditor_defaultValueDisplaNameKey",
			description="新規作成時の初期値を設定します。",
			descriptionKey="generic_editor_SelectPropertyEditor_defaultValueDescriptionKey"
	)
	@EntityViewField(
			referenceTypes={FieldReferenceType.DETAIL}
	)
	private String defaultValue;

	/** CSV出力時に選択肢順でソート */
	@MetaFieldInfo(
			displayName="CSV出力時に選択肢順でソート",
			displayNameKey="generic_editor_SelectPropertyEditor_sortCsvOutputValueKey",
			inputType=InputType.CHECKBOX,
			description="CSV出力時に選択肢順でソートするかをしていします。",
			descriptionKey="generic_editor_SelectPropertyEditor_sortCsvOutputValueDescriptionKey"
	)
	@EntityViewField(
			referenceTypes={FieldReferenceType.SEARCHRESULT}
	)
	private boolean sortCsvOutputValue;

	/**
	 * デフォルトコンストラクタ
	 */
	public SelectPropertyEditor() {
	}

	@Override
	public SelectDisplayType getDisplayType() {
		return displayType;
	}

	/**
	 * 表示タイプを取得します。
	 * @param displayType 表示タイプ
	 */
	public void setDisplayType(SelectDisplayType displayType) {
		this.displayType = displayType;
	}

	/**
	 * セレクトボックスの値を取得します。
	 * @return セレクトボックスの値
	 */
	public List<EditorValue> getValues() {
		if (this.values == null) this.values = new ArrayList<EditorValue>();
		return values;
	}

	/**
	 * セレクトボックスの値を設定します。
	 * @param values セレクトボックスの値
	 */
	public void setValues(List<EditorValue> values) {
		this.values = values;
	}

	/**
	 * セレクトボックスの値を追加します。
	 * @param val セレクトボックスの値
	 */
	public void addValue(EditorValue val) {
		getValues().add(val);
	}

	public EditorValue getValue(String value) {
		if (value != null) {
			for (EditorValue eValue : getValues()) {
				if (eValue.getValue().equals(value)) {
					return eValue;
				}
			}
		}
		return null;
	}

	@Override
	public String getDefaultValue() {
		return defaultValue;
	}

	@Override
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	/**
	 * CSV出力時に選択肢順でソートするかを取得します。
	 *
	 * @return true:CSV出力時に選択肢順でソート
	 */
	public boolean isSortCsvOutputValue() {
		return sortCsvOutputValue;
	}

	/**
	 * CSV出力時に選択肢順でソートするかを設定します。
	 *
	 * @param sortCsvOutputValue true:CSV出力時に選択肢順でソート
	 */
	public void setSortCsvOutputValue(boolean sortCsvOutputValue) {
		this.sortCsvOutputValue = sortCsvOutputValue;
	}
}