package djnd.ben1607.drink_shop.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyLibrary {
    public String nameNotNull(String name) {
        if (name.trim().isEmpty() || name.trim().isBlank()) {
            return ">>> Name cannot be empty! <<<";
        } else {
            Pattern p = Pattern.compile("[a-zA-Z\\s]");
            Matcher m = p.matcher(name);
            if (!m.find()) {
                return ">>> Name cannot contain number and special character! <<<";
            } else {
                String[] ss = name.split("\\s+");
                if (ss.length < 2) {
                    return ">>> Name must be have 2 word! <<<";
                } else {
                    return "valid";
                }
            }
        }
    }
}
