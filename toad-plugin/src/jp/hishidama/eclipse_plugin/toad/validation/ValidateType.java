package jp.hishidama.eclipse_plugin.toad.validation;

public enum ValidateType {
	/** 設計用の精査 */
	DESIGN("Validate for design", "設計用の精査"),
	/** 実装用の精査 */
	IMPLEMENTS("Validate for implements", "実装用の精査"),
	/** DSLクラス生成用の精査 */
	GENERATE("Validate for generate", "実装用の精査"),
	/** 全精査 */
	ALL("Validate all", "全精査");

	private String menuName;
	private String description;

	private ValidateType(String menuName, String description) {
		this.menuName = menuName;
		this.description = description;
	}

	public String getMenuName() {
		return menuName;
	}

	public String getDescription() {
		return description;
	}
}
