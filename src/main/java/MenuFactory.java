import data.AT;

import java.util.LinkedList;

public class MenuFactory {

    public LinkedList<String> menuBuilder(String menuName) {

        LinkedList<String> menu = new LinkedList<>();

        if(menuName.equalsIgnoreCase("cli")) {
            menu.add("1: Show COM Ports");
            menu.add("2: Connect to COM Port");
            menu.add("3: Disconnect from COM Port");
            menu.add("4: Check Connection Status");
            menu.add("5: Check Port Configuration");
            menu.add("6: Edit Port Configuration");
            menu.add("7: Revert Configuration");
            menu.add("8: Send Data");
            menu.add("0: Exit");
            return menu;
        }
        else if(menuName.equalsIgnoreCase("at")) {
            int pos = 0;
            for(AT command : AT.values()) {
                menu.add(pos + ": " + command);
                pos++;
            };
            return menu;
        }
        else {
            return null;
        }
    }
}
