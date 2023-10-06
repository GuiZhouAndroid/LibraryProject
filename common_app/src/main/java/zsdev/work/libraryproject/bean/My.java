package zsdev.work.libraryproject.bean;

import androidx.annotation.NonNull;

/**
 * Created: by 2023-09-26 18:28
 * Description:
 * Author: 张松
 */
public class My {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @NonNull
    @Override
    public String toString() {
        return "My{" +
                "name='" + name + '\'' +
                '}';
    }
}
