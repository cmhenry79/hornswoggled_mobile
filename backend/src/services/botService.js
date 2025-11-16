import { collections } from '../config/firebase.js';
import { AppError } from '../middleware/errorHandler.js';
import { logger } from '../utils/logger.js';
import { GoogleGenerativeAI } from '@google/generative-ai';
import axios from 'axios';

export class BotService {
  constructor() {
    this.genAI = new GoogleGenerativeAI(process.env.GEMINI_API_KEY);
    this.model = this.genAI.getGenerativeModel({ model: process.env.GEMINI_MODEL || 'gemini-pro' });
  }

  async getAvailableBots() {
    try {
      const snapshot = await collections().bots().get();

      return snapshot.docs.map(doc => ({
        botId: doc.id,
        ...doc.data()
      }));
    } catch (error) {
      logger.error('Error in getAvailableBots:', error);
      throw new AppError('Failed to fetch bots', 500);
    }
  }

  async getBotById(botId) {
    try {
      const botDoc = await collections().bots().doc(botId).get();

      if (!botDoc.exists) {
        throw new AppError('Bot not found', 404);
      }

      return {
        botId: botDoc.id,
        ...botDoc.data()
      };
    } catch (error) {
      logger.error('Error in getBotById:', error);
      throw error;
    }
  }

  async generateSubmission(word, persona, difficulty, roundId) {
    try {
      const prompt = this.buildPrompt(word, persona, difficulty);

      const result = await this.model.generateContent(prompt);
      const response = await result.response;
      const text = response.text();

      // Decide submission type based on persona
      const submissionType = this.decideSubmissionType(persona);

      if (submissionType === 'gif') {
        // Get GIF URL from GIPHY
        const gifUrl = await this.getRandomGif(word);
        return {
          type: 'gif',
          content: text.trim().substring(0, 100),
          mediaUrl: gifUrl
        };
      }

      return {
        type: 'text',
        content: text.trim().substring(0, 200)
      };
    } catch (error) {
      logger.error('Error in generateSubmission:', error);

      // Fallback response
      return {
        type: 'text',
        content: this.getFallbackResponse(word, persona)
      };
    }
  }

  buildPrompt(word, persona, difficulty) {
    const basePrompt = `You are ${persona.name}, a bot player in Hornswoggled, a party game like Cards Against Humanity or Jackbox.

Your personality: ${persona.description}
Your humor style: ${persona.humorStyle}

The word to define is: "${word}"

Create a funny, ${persona.humorStyle} definition or response for this word. Keep it:
- Under 200 characters
- Hilarious and ${difficulty === 'hard' ? 'very clever' : difficulty === 'medium' ? 'witty' : 'simple and funny'}
- In the style of ${persona.name}
- Appropriate but edgy
${persona.traits ? `- Traits: ${persona.traits.join(', ')}` : ''}

Respond with ONLY the definition, no explanations or meta-commentary.`;

    return basePrompt;
  }

  decideSubmissionType(persona) {
    // 30% chance of GIF for certain personas
    if (persona.preferGifs && Math.random() < 0.3) {
      return 'gif';
    }

    // 10% chance of emoji mashup
    if (Math.random() < 0.1) {
      return 'emoji';
    }

    return 'text';
  }

  async getRandomGif(query) {
    try {
      if (!process.env.GIPHY_API_KEY) {
        return null;
      }

      const response = await axios.get('https://api.giphy.com/v1/gifs/search', {
        params: {
          api_key: process.env.GIPHY_API_KEY,
          q: query,
          limit: 20,
          rating: 'pg-13'
        }
      });

      if (response.data.data.length === 0) {
        return null;
      }

      const randomIndex = Math.floor(Math.random() * response.data.data.length);
      return response.data.data[randomIndex].images.original.url;
    } catch (error) {
      logger.error('Error fetching GIF:', error);
      return null;
    }
  }

  getFallbackResponse(word, persona) {
    const fallbacks = [
      `${word}? More like... not ${word}!`,
      `I'd tell you what ${word} means, but I'd have to charge you.`,
      `${word}: It's complicated.`,
      `Ask me about ${word} again and see what happens.`,
      `The official definition of ${word} is [REDACTED]`
    ];

    return fallbacks[Math.floor(Math.random() * fallbacks.length)];
  }

  async generateBotCommentary(gameContext, eventType) {
    try {
      const prompt = `Generate a short, funny bot commentary (max 50 characters) for this game event:
Event: ${eventType}
Context: ${JSON.stringify(gameContext)}

Respond with ONLY the commentary, no explanation.`;

      const result = await this.model.generateContent(prompt);
      const response = await result.response;

      return response.text().trim().substring(0, 50);
    } catch (error) {
      logger.error('Error generating bot commentary:', error);
      return null;
    }
  }
}
