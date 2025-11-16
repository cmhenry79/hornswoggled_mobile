import Joi from 'joi';

export const schemas = {
  createRoom: Joi.object({
    visibility: Joi.string().valid('public', 'private').default('public'),
    maxPlayers: Joi.number().min(2).max(12).default(8),
    maxRounds: Joi.number().min(1).max(10).default(5),
    roundDuration: Joi.number().min(30).max(180).default(90),
    wordPackIds: Joi.array().items(Joi.string()).default(['default']),
    mutators: Joi.array().items(Joi.string()).default([]),
    enableBots: Joi.boolean().default(true)
  }),

  submitAnswer: Joi.object({
    roundId: Joi.string().required(),
    content: Joi.string().max(500).required(),
    type: Joi.string().valid('text', 'gif', 'image', 'doodle', 'emoji').default('text')
  }),

  updateProfile: Joi.object({
    displayName: Joi.string().min(3).max(30),
    avatarUrl: Joi.string().uri(),
    bio: Joi.string().max(200),
    preferences: Joi.object()
  }),

  trackEvent: Joi.object({
    eventName: Joi.string().required(),
    properties: Joi.object().default({})
  })
};

export function validate(data, schema) {
  const { error, value } = schema.validate(data, {
    abortEarly: false,
    stripUnknown: true
  });

  if (error) {
    const errors = error.details.map(detail => ({
      field: detail.path.join('.'),
      message: detail.message
    }));

    return { valid: false, errors };
  }

  return { valid: true, value };
}
