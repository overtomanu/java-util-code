package util;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JdbcUtils {
	public static Map<Integer, Object> executeStoredProcedureOrFunction(Connection connection, boolean isFunction,
			String procName, List<Object> params, Map<Integer, Integer> outparamTypeMap, boolean printLog)
			throws SQLException {
		if (printLog) {
			System.out.println("executeStoredProcedureOrFunction:" + procName + " entry");
		}
		String stmt;
		Map<Integer, Object> result = new HashMap<>();
		if (isFunction) {
			stmt = "{? = call " + procName + "(";
		} else {
			stmt = "{call " + procName + "(";
		}

		// skip placeholder for first param if this is function
		for (int i = isFunction ? 1 : 0; i < params.size(); i++) {
			// https://stackoverflow.com/questions/14554251/calling-an-oracle-pl-sql-procedure-in-java-using-a-callablestatement-with-a-bool/23088049#23088049
			if (params.get(i) instanceof Boolean) {
				stmt += "(CASE ? WHEN 0 THEN FALSE ELSE TRUE END),";
			} else {
				stmt += "?,";
			}
		}
		if (stmt.endsWith(",")) { stmt = stmt.substring(0, stmt.length() - 1); }
		stmt += ")}";

		if (printLog) {
			System.out.println("executeStoredProcedureOrFunction:callableStatement: " + stmt);
		}

		CallableStatement callSt = connection.prepareCall(stmt);

		// CallableStatement callSt = tx.createCallableStatement(stmt, 1);

		try {

			for (int i = 0; i < params.size(); i++) {
				int index = i + 1;
				Object value = params.get(i);
				if (value != null) {
					if (value instanceof Long) {
						callSt.setLong(index, (Long) value);
					} else if (value instanceof String) {
						callSt.setString(index, (String) value);
					} else if (value instanceof Date) {
						callSt.setDate(index, (Date) value);
					}
					// not supported in oracle jdbc
					else if (value instanceof Boolean) {
						if ((Boolean) value) {
							callSt.setInt(index, 1);
						} else {
							callSt.setInt(index, 0);
						}
						// callSt.setBoolean(index, (Boolean)value);
					} else {
						callSt.setObject(index, value);
					}
				} else if (value == null && !outparamTypeMap.containsKey(index)) {
					callSt.setNull(index, Types.NULL);
				} else if (outparamTypeMap.containsKey(index)) {
					// out param of type boolean not supported in oracle jdbc
					callSt.registerOutParameter(index, outparamTypeMap.get(index));
				}
			}

			callSt.execute();

			for (Map.Entry<Integer, Integer> me : outparamTypeMap.entrySet()) {
				if (me.getValue().equals(Types.INTEGER)) {
					result.put(me.getKey(), callSt.getLong(me.getKey()));
				} else if (me.getValue().equals(Types.VARCHAR)) {
					result.put(me.getKey(), callSt.getString(me.getKey()));
				} else if (me.getValue().equals(Types.BOOLEAN)) {
					result.put(me.getKey(), callSt.getBoolean(me.getKey()));
				} else {
					result.put(me.getKey(), callSt.getObject(me.getKey()));
				}
			}
			
			if (printLog) {
				System.out.println("executeStoredProcedureOrFunction:resultMap:" + result);
			}

		} finally {
			try {
				if (callSt != null)
					callSt.close();
			} catch (SQLException e) {
				if (printLog) {
					e.printStackTrace(System.out);
					e.printStackTrace(System.err);
				}
			}
			if (printLog) {
				System.out.println("executeStoredProcedureOrFunction:" + procName + " exit");
			}
		}

		return result;
	}

	public static Object executeSingleResultQuery(Connection connection, String query) throws SQLException {
		Statement opstmt = null;
		try {
			opstmt = connection.createStatement();
			opstmt.executeQuery(query);
			ResultSet rset = opstmt.getResultSet();
			if (rset.next())
				return rset.getObject(1);
		} finally {
			try {
				if (opstmt != null)
					opstmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static Long getNextValOfSequence(Connection connection, String dbSequenceName) throws SQLException {
		String stmt = "SELECT " + dbSequenceName + ".NEXTVAL as seq_nxt_val from dual";
		PreparedStatement pstmt = connection.prepareCall(stmt);
		ResultSet rs = null;
		try {
			rs = pstmt.executeQuery();
			if (rs.next()) {
				return rs.getLong("seq_nxt_val");
			} else {
				return 0L;
			}
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				if (pstmt != null)
					pstmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
