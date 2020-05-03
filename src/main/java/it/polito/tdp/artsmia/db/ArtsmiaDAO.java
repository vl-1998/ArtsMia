package it.polito.tdp.artsmia.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.artsmia.model.Adiacenza;
import it.polito.tdp.artsmia.model.ArtObject;

public class ArtsmiaDAO {

	//Mi da il chiamante la struttura dati da riempire
	public void listObjects(Map <Integer, ArtObject> idMap) {
		
		String sql = "SELECT * from objects";
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {
				
				if (!idMap.containsKey(res.getInt("object_id"))) {
					ArtObject artObj = new ArtObject(res.getInt("object_id"), res.getString("classification"), res.getString("continent"), 
							res.getString("country"), res.getInt("curator_approved"), res.getString("dated"), res.getString("department"), 
							res.getString("medium"), res.getString("nationality"), res.getString("object_name"), res.getInt("restricted"), 
							res.getString("rights_type"), res.getString("role"), res.getString("room"), res.getString("style"), res.getString("title"));
					idMap.put(artObj.getId(), artObj);
					}
				
			}
			conn.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public int getPeso(ArtObject a1, ArtObject a2) {
		String sql = "select COUNT(*) as peso " + 
				"from exhibition_objects as eo1, exhibition_objects eo2 " + 
				"where eo1.exhibition_id = eo2.exhibition_id and " + 
				"eo1.object_id = ? and eo2.object_id = ?";
		
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, a1.getId());
			st.setInt(2, a2.getId());
			ResultSet res = st.executeQuery();
			
			//se c'è un risultato lo prendo 
			if(res.next()) {
				int peso = res.getInt("peso");
				conn.close();
				return peso;
			}
			conn.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;

	}
	
	public List <Adiacenza> getAdiacenze(){
		String sql= "select eo1.object_id as obj1, eo2.object_id obj2, COUNT(*) as peso " + 
				"from exhibition_objects as eo1, exhibition_objects eo2 " + 
				"where eo1.exhibition_id = eo2.exhibition_id " + 
				"and eo1.object_id > eo2.object_id " +  
				"group by eo1.object_id, eo2.object_id";
		
		Connection conn = DBConnect.getConnection();
		List <Adiacenza> adiacenze = new ArrayList <>();
		
		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();

			while (res.next()) {
				adiacenze.add(new Adiacenza (res.getInt("obj1"), res.getInt("obj2"), res.getInt("peso")));
			}

			conn.close();
			return adiacenze;

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}

	}
	
	
	
	
}
