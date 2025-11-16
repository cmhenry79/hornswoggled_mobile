// Simple profanity filter
// In production, use a proper library like 'bad-words' or external API

const badWords = [
  // Add words to filter based on your content policy
  // This is a minimal example
];

export function containsProfanity(text) {
  if (!text) return false;

  const lowerText = text.toLowerCase();

  return badWords.some(word => {
    const regex = new RegExp(`\\b${word}\\b`, 'i');
    return regex.test(lowerText);
  });
}

export function filterProfanity(text) {
  if (!text) return text;

  let filtered = text;

  badWords.forEach(word => {
    const regex = new RegExp(`\\b${word}\\b`, 'gi');
    filtered = filtered.replace(regex, '*'.repeat(word.length));
  });

  return filtered;
}

export function validateSubmission(content) {
  if (!content || typeof content !== 'string') {
    return { valid: false, reason: 'Invalid content' };
  }

  if (content.length > 500) {
    return { valid: false, reason: 'Content too long' };
  }

  if (process.env.ENABLE_PROFANITY_FILTER === 'true' && containsProfanity(content)) {
    return { valid: false, reason: 'Content contains inappropriate language' };
  }

  return { valid: true };
}
