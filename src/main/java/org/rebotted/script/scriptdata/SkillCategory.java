package org.rebotted.script.scriptdata;

public enum SkillCategory {

    AGILITY("Agility", new String[]{"Agility"}, new int[]{1}),
    COMBAT("Combat", new String[]{"Combat", "Fighter", "Killer", "Kill"}, new int[]{2}),
    CONSTRUCTION("Construction", new String[]{"Construction"}, new int[]{3}),
    COOKING("Cooking", new String[]{"Cooking"}, new int[]{4}),
    CRAFTING("Crafting", new String[]{"Crafting"}, new int[]{5}),
    FARMING("Farming", new String[]{"Farming"}, new int[]{6}),
    FIREMAKING("Firemaking", new String[]{"Firemaking", "FM"}, new int[]{7}),
    FISHING("Fishing", new String[]{"Fishing", "Fisher"}, new int[]{8}),
    FLETCHING("Fletching", new String[]{"Fletching"}, new int[]{9}),
    HERBLORE("Herblore", new String[]{"Herblore"}, new int[]{11}),
    HUNTER("Hunter", new String[]{"Hunter"}, new int[]{12}),
    MAGIC("Magic", new String[]{"Magic"}, new int[]{13}),
    MINING("Mining", new String[]{"Mining", "Ore"}, new int[]{14}),
    MINI_GAMES("Mini-games", new String[]{"Mini-games"}, new int[]{15}),
    PRAYER("Prayer", new String[]{"Prayer"}, new int[]{17}),
    RANGED("Ranged", new String[]{"Ranged", "Range"}, new int[]{18}),
    RUNECRAFTING("Runecrafting", new String[]{"Runecrafting", "Runes"}, new int[]{19}),
    SLAYER("Slayer", new String[]{"Slayer"}, new int[]{20}),
    SMITHING("Smithing", new String[]{"Smithing"}, new int[]{21}),
    THEIVING("Thieving", new String[]{"Thieving"}, new int[]{22}),
    WOODCUTTING("Woodcutting", new String[]{"Woodcutting,woodcut,chop,chopper"}, new int[]{23}),
    MONEY_MAKING("Money Making", new String[]{"Money Making", "Cash"}, new int[]{16}),
    MISC("Misc", new String[]{"Misc"}, new int[]{0, 24});

    private String skill;
    private String[] filters;
    private int[] ids;

    SkillCategory(String skill, String[] filters, int[] ids) {
        this.skill = skill;
        this.filters = filters;
        this.ids = ids;
    }

    public static SkillCategory getCategory(int id) {
        for (SkillCategory skillCategory : SkillCategory.values()) {
            for (int a : skillCategory.ids) {
                if (a == id) {
                    return skillCategory;
                }
            }
        }
        return SkillCategory.MISC;
    }

    public static SkillCategory detect(final String name, final String desc) {
        for (SkillCategory skillCategory : SkillCategory.values()) {
            for (String filter : skillCategory.filters) {
                if (name.toLowerCase().contains(filter.toLowerCase()) || desc.toLowerCase().contains(filter.toLowerCase())) {
                    return skillCategory;
                }
            }
        }
        return SkillCategory.MISC;
    }

    public String getName() {
        return this.skill;
    }

    public String[] getFiltersWord() {
        return this.filters;
    }

    public int[] getIds() {
        return ids;
    }

    @Override
    public String toString() {
        return name().charAt(0) + name().substring(1).replaceAll("_", " ").toLowerCase();
    }

}
