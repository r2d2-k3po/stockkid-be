package net.stockkid.stockkidbe.entity;

public enum MemberRole {
    ADMIN, STAFF, USER;

    public static String getRoleHierarchy() {
        String result = "";
        MemberRole[] memberRoles = values();
        int size = memberRoles.length;
        String[] names = new String[size];
        for (int i = 0; i < size; i++) {
            names[i] = memberRoles[i].name();
        }
        for (int i = 0; i < size-1; i++) {
            result += "ROLE_" + names[i] + " > ROLE_" + names[i+1];
            if (i < size-2) result += "\n";
        }
        return result;
    }
}
