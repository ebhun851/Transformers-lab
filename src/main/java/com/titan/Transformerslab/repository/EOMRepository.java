package com.titan.Transformerslab.repository;

import java.sql.SQLException;
import java.util.Map;

import com.titan.Transformerslab.domain.ShiftOrderDetails;

public interface EOMRepository {

	public Map<String, ShiftOrderDetails> getShiftOrders(String storeNumber, String shiftNumber) throws SQLException;
}
