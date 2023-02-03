package com.calmkin.dto;

import com.calmkin.pojo.Setmeal;
import com.calmkin.pojo.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
