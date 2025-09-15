package djnd.ben1607.drink_shop.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class ChangeUpdate {
    public static <T> void handle(T request, T db) {
        Field[] fields = request.getClass().getDeclaredFields();
        for (Field x : fields) {
            // bỏ qua các trường final hoặc static (ex: OTP)
            int modifires = x.getModifiers();
            if (Modifier.isStatic(modifires) || Modifier.isFinal(modifires)) {
                continue;
            }
            x.setAccessible(true); // allow access private element
            try {
                Object value = x.get(request);
                if (value != null) {
                    x.set(db, value);
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
