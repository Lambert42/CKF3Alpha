#include "eventscripts.h"

void main(event_args_t *args)
{
	int idx;
	vec3_t origin;
	vec3_t angles;
	vec3_t velocity;
	int silencer_on;
	vec3_t ShellVelocity;
	vec3_t ShellOrigin;
	vec3_t vecSrc, vecAiming;
	vec3_t up, right, forward;
	cl_entity_t *ent;
	vec3_t vSpread;

	ent = GetViewEntity();
	idx = args->entindex;
	silencer_on = args->bparam1;

	VectorCopy(args->origin, origin);
	VectorCopy(args->angles, angles);
	VectorCopy(args->velocity, velocity);

	angles[0] += args->iparam1 / 100.0;
	angles[1] += args->iparam2 / 100.0;

	AngleVectors(angles, forward, right, up);

	if (IsLocal(idx))
	{
		ShotsFired();
		MuzzleFlash();

		if (silencer_on)
			WeaponAnimation(1 + RandomLong(0, 2));
		else
			WeaponAnimation(8 + RandomLong(0, 2));

		if (silencer_on)
			CreateSmoke(ent->attachment[0], forward, 3, 0.3, 16, 16, 16, 3, velocity, false, 35);
		else
			CreateSmoke(ent->attachment[2], forward, 3, 0.2, 16, 16, 16, 4, velocity, false, 35);
	}

	if (IsLocal(idx))
	{
		if (!UseLeftHand())
			GetDefaultShellInfo(args, origin, velocity, ShellVelocity, ShellOrigin, forward, right, up, 20.0, -8.0, -10.0, false);
		else
			GetDefaultShellInfo(args, origin, velocity, ShellVelocity, ShellOrigin, forward, right, up, 20.0, -8.0, -10.0, false);

		VectorCopy(ent->attachment[1], ShellOrigin);
		ShellVelocity[2] -= 45;
	}
	else
		GetDefaultShellInfo(args, origin, velocity, ShellVelocity, ShellOrigin, forward, right, up, 20.0, -12.0, -4.0, false);

	EjectBrass(ShellOrigin, ShellVelocity, angles[1], FindModelIndex("models/rshell.mdl"), TE_BOUNCE_SHELL, idx, 10);

	if (silencer_on)
	{
		PlaySound(idx, origin, CHAN_WEAPON, "weapons/m4a1-1.wav", 1.0, 1.4, 0, 94 + RandomLong(0, 15));
	}
	else
	{
		if (RandomLong(0, 1))
			PlaySound(idx, origin, CHAN_WEAPON, "weapons/m4a1_unsil-1.wav", 1.0, 1.4, 0, 94 + RandomLong(0, 15));
		else
			PlaySound(idx, origin, CHAN_WEAPON, "weapons/m4a1_unsil-2.wav", 1.0, 1.4, 0, 94 + RandomLong(0, 15));
	}

	GetGunPosition(args, vecSrc, origin);

	VectorCopy(forward, vecAiming);

	vSpread[0] = args->fparam1;
	vSpread[1] = args->fparam2;
	vSpread[2] = 0;

	FireBullets(idx, forward, right, up, 1, vecSrc, vecAiming, vSpread, 8192, BULLET_PLAYER_556MM, 2);
}