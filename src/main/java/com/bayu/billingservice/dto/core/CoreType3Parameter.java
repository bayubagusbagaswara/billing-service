package com.bayu.billingservice.dto.core;

import com.bayu.billingservice.model.SfValRgDaily;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoreType3Parameter {

    private List<SfValRgDaily> sfValRgDailyList;

}
