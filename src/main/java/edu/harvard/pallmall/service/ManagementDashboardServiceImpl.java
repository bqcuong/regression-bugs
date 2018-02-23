package edu.harvard.pallmall.service;

import edu.harvard.pallmall.domain.Event;
import edu.harvard.pallmall.domain.Reader;
import edu.harvard.pallmall.domain.WristBand;
import edu.harvard.pallmall.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * The Management Dashboard Service Implementor...
 */
@Service("managementDashboardService")
@Repository
@Transactional
public class ManagementDashboardServiceImpl implements ManagementDashboardService {

    private DoctorRepository doctorRepository;
    private EventRepository eventRepository;
    private LocationRepository locationRepository;
    private ReaderRepository readerRepository;
    private WristBandRepository wristBandRepository;

    @Autowired
    public void setDoctorRepository(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    @Autowired
    public void setEventRepository(EventRepository EventRepository) {
        this.eventRepository = eventRepository;
    }

    @Autowired
    public void setLocationRepository(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    @Autowired
    public void setReaderRepository(ReaderRepository readerRepository) {
        this.readerRepository = readerRepository;
    }

    @Autowired
    public void setWristBandRepository(WristBandRepository wristBandRepository) {
        this.wristBandRepository = wristBandRepository;
    }

    //TODO add the ability to search for anything - this may be covered by tableau
    // Finds all Events
    @Transactional(readOnly=true)
    public Iterable<Event> findAllEvents() {
        return eventRepository.findAll();
    }

    // Finds an Event by its ID
    @Transactional(readOnly=true)
    public Event findEventById(Long id) {
        return eventRepository.findOne(id);
    }

    // Persists a new Event
    public Event saveEvent(Event event) {
        return eventRepository.save(event);
    }

}
