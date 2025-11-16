import { collections } from '../config/firebase.js';
import { logger } from '../utils/logger.js';

export class AnalyticsService {
  async trackEvent(eventData) {
    try {
      if (process.env.ENABLE_ANALYTICS !== 'true') {
        return;
      }

      const event = {
        eventName: eventData.eventName,
        userId: eventData.userId || 'anonymous',
        properties: eventData.properties || {},
        timestamp: eventData.timestamp || new Date(),
        sessionId: eventData.sessionId || null,
        platform: eventData.platform || 'unknown'
      };

      await collections().analytics().add(event);

      logger.debug(`Event tracked: ${eventData.eventName}`);
    } catch (error) {
      // Don't throw errors for analytics failures
      logger.error('Error tracking event:', error);
    }
  }

  async trackEventsBatch(events, userId) {
    try {
      if (process.env.ENABLE_ANALYTICS !== 'true') {
        return;
      }

      const batch = collections().analytics().firestore.batch();
      const timestamp = new Date();

      for (const event of events) {
        const ref = collections().analytics().doc();
        batch.set(ref, {
          eventName: event.eventName,
          userId: userId || 'anonymous',
          properties: event.properties || {},
          timestamp: event.timestamp || timestamp,
          sessionId: event.sessionId || null,
          platform: event.platform || 'unknown'
        });
      }

      await batch.commit();

      logger.debug(`Batch tracked ${events.length} events`);
    } catch (error) {
      logger.error('Error tracking events batch:', error);
    }
  }

  async getEventStats(eventName, startDate, endDate) {
    try {
      let query = collections().analytics()
        .where('eventName', '==', eventName);

      if (startDate) {
        query = query.where('timestamp', '>=', startDate);
      }

      if (endDate) {
        query = query.where('timestamp', '<=', endDate);
      }

      const snapshot = await query.get();

      return {
        eventName,
        count: snapshot.size,
        events: snapshot.docs.map(doc => doc.data())
      };
    } catch (error) {
      logger.error('Error getting event stats:', error);
      throw error;
    }
  }
}
