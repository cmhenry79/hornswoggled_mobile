import Filter from 'bad-words';

// Initialize profanity filter with custom configuration
const filter = new Filter();

// Add custom words if needed
// filter.addWords('custom1', 'custom2');

export function containsProfanity(text) {
  if (!text || typeof text !== 'string') return false;

  try {
    return filter.isProfane(text);
  } catch (error) {
    console.error('Error checking profanity:', error);
    return false;
  }
}

export function filterProfanity(text) {
  if (!text || typeof text !== 'string') return text;

  try {
    return filter.clean(text);
  } catch (error) {
    console.error('Error filtering profanity:', error);
    return text;
  }
}

export function validateSubmission(content) {
  if (!content) {
    return { valid: false, reason: 'Content is required' };
  }

  if (typeof content !== 'string') {
    return { valid: false, reason: 'Content must be a string' };
  }

  const trimmed = content.trim();

  if (trimmed.length === 0) {
    return { valid: false, reason: 'Content cannot be empty' };
  }

  if (trimmed.length > 500) {
    return { valid: false, reason: 'Content too long (max 500 characters)' };
  }

  // Check for profanity
  if (containsProfanity(trimmed)) {
    return {
      valid: false,
      reason: 'Content contains inappropriate language',
      filtered: filterProfanity(trimmed)
    };
  }

  return { valid: true, value: trimmed };
}
