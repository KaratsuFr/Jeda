package fr.ippon.tlse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import fr.ippon.tlse.ApplicationUtils;
import fr.ippon.tlse.business.IBusinessService;
import fr.ippon.tlse.domain.TuBasicDomain;
import fr.ippon.tlse.dto.ResourceDto;
import fr.ippon.tlse.dto.utils.DtoMapper;

public class MockTuBasicBuniess implements IBusinessService {

	private Map<String, TuBasicDomain>	mockPersistance	= new HashMap<>();

	public MockTuBasicBuniess() {
		for (int i = 0; i < 10; i++) {
			mockPersistance.put("" + i, TuBasicDomain.builder().num(i).text(UUID.randomUUID().toString()).build());
		}
	}

	@Override
	public ResourceDto readAll(Class<?> domainClass, Optional<String> parentId) {
		ApplicationUtils.SINGLETON.resetCacheClass();
		DtoMapper.SINGLETON.resetCache();
		return DtoMapper.SINGLETON.buildResourceFromDomain(new ArrayList<TuBasicDomain>(mockPersistance.values()));
	}

	@Override
	public ResourceDto searchByCriteria(ResourceDto resource, Class<?> domainClass) {
		return null;
	}

	@Override
	public ResourceDto readById(String id, Class<?> domainClass) {
		TuBasicDomain dom = mockPersistance.get(id);

		ArrayList<TuBasicDomain> lstResult = new ArrayList<>();
		lstResult.add(dom);
		return DtoMapper.SINGLETON.buildResourceFromDomain(lstResult);
	}

	@Override
	public ResourceDto createOrUpdate(ResourceDto resource, Class<?> domainClass) {
		List<TuBasicDomain> lstDomain = DtoMapper.SINGLETON.buildLstDomainFromResource(resource, TuBasicDomain.class);
		for (TuBasicDomain tuBasicDomain : lstDomain) {
			if (tuBasicDomain.getNum() == null) {
				tuBasicDomain.setNum(mockPersistance.size());
			}
			mockPersistance.put("" + tuBasicDomain.getNum(), tuBasicDomain);

		}
		return resource;
	}

	@Override
	public boolean deleteById(String id, Class<?> domainClass) {
		return mockPersistance.remove(id) != null;

	}

}
