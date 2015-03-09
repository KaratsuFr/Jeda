package fr.ippon.tlse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import fr.ippon.tlse.business.IBusinessService;
import fr.ippon.tlse.domain.TuBasicDomain;
import fr.ippon.tlse.dto.ResourceDto;
import fr.ippon.tlse.persistence.CursoWrapper;
import fr.ippon.tlse.persistence.CursoWrapperList;

public class MockTuBasicBuniess implements IBusinessService<TuBasicDomain> {

	private Map<String, TuBasicDomain>	mockPersistance	= new HashMap<>();

	public MockTuBasicBuniess() {
		for (int i = 0; i < 10; i++) {
			mockPersistance.put("" + i, TuBasicDomain.builder().num(i).text(UUID.randomUUID().toString()).build());
		}
	}

	@Override
	public CursoWrapper<TuBasicDomain> readAll(Class<TuBasicDomain> domainClass) {
		CursoWrapper<TuBasicDomain> cur = new CursoWrapperList<>(new ArrayList<TuBasicDomain>(mockPersistance.values()));
		return cur;
	}

	@Override
	public CursoWrapper<TuBasicDomain> searchByCriteria(ResourceDto resource, Class<TuBasicDomain> domainClass) {
		return null;
	}

	@Override
	public TuBasicDomain readById(String id, Class<TuBasicDomain> domainClass) {
		TuBasicDomain dom = mockPersistance.get(id);
		return dom;
	}

	@Override
	public List<TuBasicDomain> createOrUpdate(List<TuBasicDomain> lstDomain, Class<TuBasicDomain> domainClass) {

		for (TuBasicDomain tuBasicDomain : lstDomain) {
			if (tuBasicDomain.getNum() == null) {
				tuBasicDomain.setNum(mockPersistance.size());
			}
			mockPersistance.put("" + tuBasicDomain.getNum(), tuBasicDomain);
		}
		return lstDomain;
	}

	@Override
	public boolean deleteById(String id, Class<TuBasicDomain> domainClass) {
		return mockPersistance.remove(id) != null;

	}

}
