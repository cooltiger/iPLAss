/*
 * Copyright (C) 2014 INFORMATION SERVICES INTERNATIONAL - DENTSU, LTD. All Rights Reserved.
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

package org.iplass.mtp.view.top.parts;

/**
 * ツリービューパーツ
 * @author lis3wg
 */
public class TreeViewParts extends TemplateParts {

	/** SerialVersionUID */
	private static final long serialVersionUID = 2930181295501742814L;

	/** アイコンタグ */
	private String iconTag;

	/**
	 * ツリービュー名を設定します。
	 * @param treeViewName ツリービュー名
	 */
	public void setTreeViewName(String treeViewName) {
		setParam("treeViewName", treeViewName);
	}

	/**
	 * ツリービュー名を取得します。
	 * @return ツリービュー名
	 */
	public String getTreeViewName() {
		return getParam("treeViewName");
	}

	/**
	 * アイコンタグを取得します。
	 * @return アイコンタグ
	 */
	public String getIconTag() {
	    return iconTag;
	}

	/**
	 * アイコンタグを設定します。
	 * @param iconTag アイコンタグ
	 */
	public void setIconTag(String iconTag) {
	    this.iconTag = iconTag;
	}

}